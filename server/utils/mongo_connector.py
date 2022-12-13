import sys

sys.path.append("../")

from pymongo import MongoClient
import pickle
from models.recipes import rspace
import numpy as np
from datetime import date, datetime
from utils.constants import PREV_RECORDS_LIMIT, MONGO_URI


class MongoConnector:
    __instance = None

    @staticmethod
    def getInstance():
        if MongoConnector.__instance is None:
            MongoConnector()
        return MongoConnector.__instance

    def __init__(self):
        if MongoConnector.__instance is not None:
            raise Exception("This class is a singleton!")
        else:
            self.mongo_client = MongoClient(MONGO_URI)
            self.db = self.mongo_client.meal_check
            self.users = self.db.users
            MongoConnector.__instance = self

    def get_user_info(self, user_id) -> dict:
        return self.users.find_one(
            {"email": user_id},
            {
                "email": 1,
                "name": 1,
                "age": 1,
                "weight": 1,
                "height": 1,
                "goal": 1,
                "activity_level": 1,
                "prefs": 1,
                "recipes_tried": 1,
                "calorie_requirement": 1,
            },
        )

    def set_user_info(self, user_id, user_info, upsert=False):
        self.users.update_one({"email": user_id}, {"$set": user_info}, upsert=upsert)

    def get_user_vec_p(self, user_id) -> np.ndarray:
        vec_p = self.users.find_one({"email": user_id}, {"email": 1, "vec_p": 1})
        if vec_p is not None and "vec_p" in vec_p.keys():
            vec_p = pickle.loads(vec_p["vec_p"])
            return vec_p
        else:
            print("ERROR: user_vec_p is not set previously")
            return None

    def get_user_vec_c(self, user_id) -> np.ndarray:
        vec_c = self.users.find_one({"email": user_id}, {"email": 1, "vec_c": 1})
        if vec_c is not None and "vec_c" in vec_c.keys():
            vec_c = pickle.loads(vec_c["vec_c"])
            return vec_c
        else:
            print("ERROR: user_vec_c is not set previously")
            return None

    def set_user_vec_p(self, user_id, user_vec):
        vec_p = pickle.dumps(user_vec)
        self.users.update_one({"email": user_id}, {"$set": {"vec_p": vec_p}})

    def set_user_vec_c(self, user_id, user_vec):
        vec_c = pickle.dumps(user_vec)
        self.users.update_one({"email": user_id}, {"$set": {"vec_c": vec_c}})

    def set_user_prefs(self, user_id, prefs, upsert=True):
        self.users.update_one(
            {"email": user_id},
            {"$set": {"prefs": prefs["prefs"], "recipes_tried": 10}},
            upsert=upsert,
        )
        vec_p = rspace.build_user_vec_p(prefs["prefs"])
        # print(1, self.get_user_vec_p(user_id))
        self.set_user_vec_p(user_id, vec_p)
        # print(2, self.get_user_vec_p(user_id))

        vec_c = self.get_user_vec_c(user_id)
        if vec_c is None:
            vec_c = vec_p + np.random.randn(100) / 2
            self.set_user_vec_c(user_id, vec_c)

    def add_recipe_rating(self, user_id, recipe_id: int, rating):
        recipe_ratings = self.users.find_one(
            {"email": user_id}, {"_id": 0, "recipe_ratings": 1, "recipes_tried": 1}
        )

        new_vec_p = rspace.update_user_vec_p(
            self.get_user_vec_p(user_id),
            recipe_ratings["recipes_tried"],
            recipe_id,
            rating / 5,
        )
        # print(self.get_user_vec_p(user_id))
        # print(new_vec_p)
        self.set_user_vec_p(user_id, new_vec_p)

        new_vec_c = rspace.update_user_vec_c(self.get_user_vec_c(user_id), recipe_id)
        self.set_user_vec_c(user_id, new_vec_c)

        if (
            "recipe_ratings" in recipe_ratings.keys()
            and len(recipe_ratings["recipe_ratings"]) >= PREV_RECORDS_LIMIT
        ):
            self.users.update_one(
                {"email": user_id},
                {
                    "$push": {
                        "recipe_ratings": {"recipe_id": recipe_id, "rating": rating}
                    },
                    "$pop": {
                        "recipe_ratings": -1
                    },  # Only if number of larger than something
                },
            )
        else:
            self.users.update_one(
                {"email": user_id},
                {
                    "$push": {
                        "recipe_ratings": {"recipe_id": recipe_id, "rating": rating}
                    },
                },
            )

        self.add_todays_meal(user_id, recipe_id)

    def add_todays_meal(self, user_id, recipe_id):
        today_string = date.today().strftime("%d/%m/%Y")
        nested_key = "todays_meals.{}".format(today_string)

        self.users.update_one(
            {"email": user_id},
            {
                "$push": {nested_key: recipe_id},
            },
        )

    def get_todays_meals(self, user_id) -> list:
        today_string = date.today().strftime("%d/%m/%Y")
        nested_key = "todays_meals.{}".format(today_string)

        day_of_week = date.today().strftime("%A").lower()

        todays_planned_meals = self.get_meal_plan(user_id)[day_of_week]

        todays_meals = todays_planned_meals

        # prune
        # remove dinner
        todays_meals.pop()
        if datetime.now().hour < 17:
            # remove lunch
            todays_meals.pop()
        if datetime.now().hour < 12:
            # remove breakfast
            todays_meals.pop()

        updated_todays_meals = self.users.find_one({"email": user_id}, {nested_key: 1})
        # print(updated_todays_meals)
        if (
            updated_todays_meals is not None
            and "todays_meals" in updated_todays_meals.keys()
        ):
            updated_todays_meals = updated_todays_meals["todays_meals"]
            # print(updated_todays_meals)
            # print(today_string)
            if (
                updated_todays_meals is not None
                and today_string in updated_todays_meals.keys()
            ):
                updated_todays_meals = updated_todays_meals[today_string]
                # print(updated_todays_meals)
                for i, meal in enumerate(updated_todays_meals):
                    # print(i, meal)
                    if i < len(todays_meals):
                        todays_meals[i] = meal
                    else:
                        todays_meals.append(meal)

        if len(todays_meals) > 0:
            return todays_meals

        # if todays_meals is not None and "todays_meals" in todays_meals.keys():
        #     return todays_meals["todays_meals"]
        else:
            print("ERROR: todays_meals is not set.")
            return []

    def get_meal_plan(self, user_id) -> list:
        meal_plan = self.users.find_one({"email": user_id}, {"meal_plan": 1})
        if meal_plan is not None and "meal_plan" in meal_plan.keys():
            return meal_plan["meal_plan"]
        else:
            print("ERROR: meal_plan is not set.")
            return None

    def set_meal_plan(self, user_id, meal_plan):
        self.users.update_one({"email": user_id}, {"$set": {"meal_plan": meal_plan}})


connector = MongoConnector.getInstance()

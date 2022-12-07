import sys

sys.path.append("../")

from utils.constants import STOPWORDS, PREV_RECORDS_LIMIT
from pymongo import MongoClient
import pickle
from models.recipes import RecipeSpace


def remove_stop_words(s: str):
    s = s.split()
    words = []
    for w in s:
        if w not in STOPWORDS:
            words.append(w)

    return words


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
            self.mongo_client = MongoClient("localhost", 27017)
            self.db = self.mongo_client.meal_check
            self.users = self.db.users
            MongoConnector.__instance = self

    def get_user_info(self, user_id):
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
            },
        )

    def get_user_vec_p(self, user_id):
        vec_p = self.users.find_one({"email": user_id}, {"email": 1, "vec_p": 1})
        vec_p["vec_p"] = pickle.loads(vec_p["vec_p"])
        return vec_p

    def get_user_vec_c(self, user_id):
        vec_c = self.users.find_one({"email": user_id}, {"email": 1, "vec_c": 1})
        vec_c["vec_c"] = pickle.loads(vec_c["vec_c"])
        return vec_c

    def set_user_info(self, user_id, user_info, upsert=False):
        self.users.update_one({"email": user_id}, {"$set": user_info}, upsert=upsert)

    def set_user_prefs(self, user_id, prefs, upsert=True):
        self.users.update_one({"email": user_id}, {"$set": prefs}, upsert=upsert)
        vec_p = RecipeSpace.getInstance().build_user_vec_p(prefs["prefs"])
        self.set_user_vec_p(user_id, vec_p)

    def set_user_vec_p(self, user_id, user_vec):
        vec_p = pickle.dumps(user_vec)
        self.users.update_one({"email": user_id}, {"$set": {"vec_p": vec_p}})

    def set_user_vec_c(self, user_id, user_vec):
        vec_c = pickle.dumps(user_vec)
        self.users.update_one({"email": user_id}, {"$set": {"vec_c": vec_c}})

    def add_recipe_rating(self, user_id, recipe_id: int, rating):
        recipe_ratings = self.users.find_one(
            {"email": user_id}, {"_id": 0, "recipe_ratings": 1}
        )

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


connector = MongoConnector.getInstance()

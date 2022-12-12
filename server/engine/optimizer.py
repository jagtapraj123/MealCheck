import sys

sys.path.append("../")

import numpy as np
from utils.mongo_connector import connector
from models.user import User
from models.recipes import rspace
from models.clusters import RecipeClusters
from models.nv import NV
import random


def _get_suggestions(vec_p, vec_c, l, nutrition_req, count) -> list:
    clusters = RecipeClusters.getInstance()
    nv = NV.getInstance()

    user_vec_dist = vec_c - vec_p
    user_vec_dist = np.sqrt(np.dot(user_vec_dist, user_vec_dist))

    valid_set = clusters.get_nearby_clusters(vec_p, radius=l + user_vec_dist)
    print("Valid Set: {}".format(len(valid_set)))
    invalid_set = clusters.get_nearby_clusters(vec_c, radius=l)
    print("Invalid Set: {}".format(len(invalid_set)))
    valid_set -= invalid_set

    print("Valid Clusters: {}".format(len(valid_set)))

    valid_recipes = set()
    for c in valid_set:
        # print(8, clusters.get_recipes(c))
        valid_recipes = valid_recipes.union(clusters.get_recipes(c))

    print("Valid Recipes: {}".format(len(valid_recipes)))

    nutritional = nv.get_nearest_recipes(nutrition_req)
    valid_recipes = valid_recipes.intersection(nutritional)

    print("Valid Nutritional Recipes: {}".format(len(valid_recipes)))

    if len(valid_recipes) == 0:
        sample_recipes = set()
    elif count < len(valid_recipes):
        sample_recipes = set(random.sample(valid_recipes, count))
    else:
        sample_recipes = valid_recipes

    return list(sample_recipes)


def get_suggestions(user_id: str, l: float, count: int = 10) -> list:
    user = User(user_id)
    # clusters = RecipeClusters.getInstance()
    nv = NV.getInstance()

    todays_consumption = nv.get_consumption(user.todays_meals)

    # nutrition_req = (2400 - todays_consumption)/(3-len(user.todays_meals))
    if len(user.todays_meals) < 3:
        nutrition_req = (user.calorie_requirement - todays_consumption) / (
            3 - len(user.todays_meals)
        )
    elif (user.calorie_requirement - todays_consumption) > 50:
        nutrition_req = user.calorie_requirement - todays_consumption
    else:
        nutrition_req = 50

    sample_recipes = _get_suggestions(
        user.user_vec_p, user.user_vec_c, l, nutrition_req, 10
    )

    # recipe_space = RecipeSpace.getInstance()
    recipes = []
    for s in sample_recipes:
        r = rspace.get_recipe_from_id(s)

        recipes.append(
            {
                "recipe_id": int(s),
                "name": str(r["name"].title()),
                "minutes": str(r["minutes"]),
                "steps": str(r["steps"]),
                "ingredients": r["ingredients"],
                "description": r["description"],
                "nutrition": float(nv.get_nutrition(s)),
                "image": "https://img.delicious.com.au/XOkfl61w/del/2020/12/lemon-chicken-144144-1.jpg",
            }
        )
    return recipes


def make_meal_plan(user_id):
    user = User(user_id)
    clusters = RecipeClusters.getInstance()
    # recipes = RecipeSpace.getInstance()
    nv = NV.getInstance()
    vec_c = user.user_vec_c
    vec_p = user.user_vec_p
    recipes_tried = user.recipes_tried

    meal_plan = {
        "monday": [],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [],
        "saturday": [],
        "sunday": [],
    }

    # simulate week
    for d in meal_plan.keys():
        todays_meals = []
        todays_consumption = 0
        l = 2.5

        for i in range(3):
            l_ = l
            r = _get_suggestions(
                vec_p,
                vec_c,
                l_,
                (user.calorie_requirement - todays_consumption) / (3 - i),
                1,
            )
            while len(r) == 0:
                # reduce lambda and retry
                l_ -= 0.5
                r = _get_suggestions(
                    vec_p,
                    vec_c,
                    l_,
                    (user.calorie_requirement - todays_consumption) / (3 - i),
                    1,
                )

            todays_meals.append(int(r[0]))
            todays_consumption += nv.get_consumption(r)

            vec_c = rspace.update_user_vec_c(vec_c, r[0])
            vec_p = rspace.update_user_vec_p(vec_p, recipes_tried, r[0], 1)
            recipes_tried += 1

        meal_plan[d] = todays_meals

    return meal_plan


def make_and_set_meal_plan(user_id):
    meal_plan = make_meal_plan(user_id)

    print(meal_plan)

    connector.set_meal_plan(user_id, meal_plan)

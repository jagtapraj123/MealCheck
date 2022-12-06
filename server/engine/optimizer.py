import sys

sys.path.append("../")

import numpy as np
from models.user import User
from models.recipes import RecipeSpace
from models.clusters import RecipeClusters
import random


def get_suggestions(user_id: str, l: float, count: int = 10) -> list:
    user = User(user_id)
    clusters = RecipeClusters.getInstance()
    user_vec_dist = user.user_vec_c - user.user_vec_p
    user_vec_dist = np.sqrt(np.dot(user_vec_dist, user_vec_dist))
    valid_set = clusters.get_nearby_clusters(user.user_vec_p, radius=l + user_vec_dist)
    invalid_set = clusters.get_nearby_clusters(user.user_vec_c, radius=l)

    valid_set -= invalid_set

    print("Valid Clusters: {}".format(len(valid_set)))

    valid_recipes = set()
    for c in valid_set:
        valid_recipes += clusters.get_recipes(c)

    # Random sampling
    if len(valid_recipes) == 0:
        sample_recipes = set()
    elif count < len(valid_recipes):
        sample_recipes = set(random.sample(valid_recipes, count))
    else:
        sample_recipes = valid_recipes

    recipe_space = RecipeSpace.getInstance()
    recipes = []
    for s in sample_recipes:
        r = recipe_space.get_recipe_from_id(s)
        recipes.append(
            {
                "name": r["name"].title(),
                "minutes": r["minutes"],
                "steps": r["steps"],
                "ingredients": r["ingredients"],
                "description": r["description"],
            }
        )
    return recipes

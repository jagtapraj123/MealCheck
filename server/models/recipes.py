import sys

sys.path.append("../")

import pandas as pd
from gensim.models.doc2vec import Doc2Vec

from utils.constants import RECIPES_SPACE_MODEL_PATH, RAW_RECIPES_PATH, BETA
from tqdm import tqdm
import numpy as np


class RecipeSpace:
    __instance = None

    @staticmethod
    def getInstance():
        if RecipeSpace.__instance is None:
            RecipeSpace(RECIPES_SPACE_MODEL_PATH)
        return RecipeSpace.__instance

    def __init__(self, recipes_space_model_path):
        if RecipeSpace.__instance is not None:
            raise Exception(
                "RecipesSpace instance already exist at {}".format(
                    RecipeSpace.__instance
                )
            )

        self._model = Doc2Vec.load(recipes_space_model_path)

        self._recipes = pd.read_csv(RAW_RECIPES_PATH)

        self._recipes["steps"] = self._recipes["steps"].apply(
            lambda r: " ".join(
                list(map(lambda x: x.strip("'"), r.strip("[]").split(", ")))
            )
        )

        RecipeSpace.__instance = self

    def get_vec(self, recipe: str):
        return self._model.infer_vector(recipe.split())

    def get_nearest(self, recipe: str, count: int = 10):
        return self.dv.most_similar(self.get_vec(recipe), topn=count)

    def get_recipe_from_id(self, id: int) -> pd.Series:
        return self._recipes.iloc[id]

    def build_user_vec_p(self, recipe_ids):
        vec_p = self._model.dv.get_mean_vector(recipe_ids, pre_normalize=False)
        return vec_p

    def build_user_vec_c(self, recipe_ids):
        vec_c = np.zeros(100)
        for r_id in recipe_ids:
            vec_c = vec_c * (1 - BETA) + BETA * self._model.dv.get_vector(
                r_id, norm=False
            )
        return vec_c

    def update_user_vec_p(self, vec_p, count, recipe_id, rating):
        vec_p = vec_p * (count / (count + 1)) + rating * self._model.dv.get_vector(
            recipe_id
        ) / (count + 1)
        return vec_p

    def update_user_vec_c(self, vec_c, recipe_id):
        vec_c = vec_c * (1 - BETA) + BETA * self._model.dv.get_vector(
            recipe_id, norm=False
        )
        return vec_c

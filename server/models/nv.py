import sys

sys.path.append("../")

import numpy as np
import pandas as pd
from utils.constants import RAW_RECIPES_PATH


class NV:
    __instance = None

    @staticmethod
    def getInstance():
        if NV.__instance is None:
            NV()
        return NV.__instance

    def __init__(self):
        if NV.__instance is not None:
            raise Exception(
                "RecipesSpace instance already exist at {}".format(NV.__instance)
            )

        self._recipes = pd.read_csv(RAW_RECIPES_PATH)

        # self._recipes["steps"] = self._recipes["steps"].apply(
        #     lambda r: ", ".join(
        #         list(map(lambda x: x.strip("'"), r.strip("[]").split(", ")))
        #     )
        # )

        # self._recipes["nutrition"] = self._recipes["nutrition"].apply(
        #     lambda r:
        #         list(map(lambda x: float(x.strip("'")), r.strip("[]").split(", "))
        #     )
        # )
        # print(self._recipes["nutrition"][0])

        self._nv = np.array(
            self._recipes["nutrition"]
            .apply(
                lambda r: list(
                    map(lambda x: float(x.strip("'")), r.strip("[]").split(", "))
                )
            )
            .tolist()
        )
        # print(np.mean(self._nv))
        NV.__instance = self

    def get_nearest_recipes(self, req, threshold=50):
        return set(np.where((np.abs(self._nv[:, 0] - req)) < threshold)[0])

    def get_consumption(self, recipe_ids):
        return sum(self._nv[recipe_ids, 0])

    def get_nutrition(self, recipe_id):
        # print(recipe_id, self._nv.shape)
        return self._nv[recipe_id, 0]


nv = NV.getInstance()

from __future__ import annotations
import sys

sys.path.append("../")

import numpy as np
import pickle
from utils.constants import (
    RECIPES_SPACE_CLUSTERS_PATH,
    RECIPES_SPACE_CLUSTERS_ASSIGNS_PATH,
    RECIPES_SPACE_CLUSTERS_CENTERS_PATH,
)


class RecipeClusters:
    __instance = None

    @staticmethod
    def getInstance() -> RecipeClusters:
        if RecipeClusters.__instance is None:
            RecipeClusters(
                RECIPES_SPACE_CLUSTERS_PATH,
                RECIPES_SPACE_CLUSTERS_ASSIGNS_PATH,
                RECIPES_SPACE_CLUSTERS_CENTERS_PATH,
            )
        return RecipeClusters.__instance

    def __init__(
        self, clusters: str, cluster_assigns: str, cluster_centers: str
    ) -> None:
        if RecipeClusters.__instance is not None:
            raise Exception(
                "RecipesSpace instance already exist at {}".format(
                    RecipeClusters.__instance
                )
            )

        with open(clusters, "rb") as f:
            self._clusters = pickle.load(f)

        self._cluster_assigns = np.load(cluster_assigns)
        self._cluster_centers = np.load(cluster_centers)
        # temp = (self._cluster_centers - self._cluster_centers[3])
        # maxans = 0
        # for i in range(temp.shape[0]):
        #     maxans = max(maxans, np.sqrt(np.dot(temp[i], temp[i])))
        # print(maxans)
        RecipeClusters.__instance = self

    def get_nearby_clusters(self, vec: np.ndarray, radius: float) -> set:
        # print(self._clusters.transform([vec]))
        return set(np.where(self._clusters.transform([vec])[0] < radius * radius)[0])

    def get_recipes(self, cluster_id: int):
        return set(np.where(self._cluster_assigns == cluster_id)[0])


clusters = RecipeClusters.getInstance()

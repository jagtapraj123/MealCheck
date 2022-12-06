import sys
sys.path.append("../")

from gensim.models.doc2vec import Doc2Vec

from utils.constants import RECIPES_SPACE_MODEL_PATH

class RecipeSpace:
    __instance = None
    @staticmethod 
    def getInstance():
        if RecipeSpace.__instance is None:
            RecipeSpace(RECIPES_SPACE_MODEL_PATH)
        return RecipeSpace.__instance

    def __init__(self, recipes_space_model_path):
        if RecipeSpace.__instance is not None:
            raise Exception("RecipesSpace instance already exist at {}".format(RecipeSpace.__instance))

        self.model = Doc2Vec.load(recipes_space_model_path)
        RecipeSpace.__instance = self

    def get_vec(self, recipe: str):
        return self.model.infer_vector(recipe.split())

    def get_nearest(self, recipe: str, count: int = 10):
        return self.dv.most_similar(self.get_vec(recipe), topn=count)
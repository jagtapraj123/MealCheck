import sys

sys.path.append("../")

import numpy as np
import pandas as pd

from gensim.models.doc2vec import Doc2Vec, TaggedDocument

from utils.helpers import remove_stop_words

from tqdm import tqdm

from sklearn.cluster import KMeans
import pickle

if __name__ == "__main__":
    data_file_path = "../../dataset/RAW_recipes.csv"

    data = pd.read_csv(data_file_path)

    recipes = []

    for r in tqdm(data["steps"]):
        r = r.strip("[]").split(", ")
        steps = " ".join(list(map(lambda x: x.strip("'"), r)))
        recipes.append(remove_stop_words(steps))

    num_recipes = len(recipes)
    print("Recipes: {}".format(num_recipes))

    documents = [TaggedDocument(doc, [i]) for i, doc in enumerate(recipes)]

    doc2vec = Doc2Vec(documents, vector_size=100, window=2, workers=64, epochs=50)

    doc2vec.save("../saved_models/doc2vec_model")

    print("Vectors: {}".format(doc2vec.dv.vectors.shape))

    k_means = KMeans(10000, verbose=1).fit(doc2vec.dv.vectors)

    clusters = k_means.labels_
    cluster_centers = k_means.cluster_centers_

    with open("../saved_models/clusters.pkl", "wb") as f:
        pickle.dump(k_means, f)

    np.save("../saved_models/cluster_assignments.npy", clusters)
    np.save("../saved_models/cluster_centers.npy", cluster_centers)

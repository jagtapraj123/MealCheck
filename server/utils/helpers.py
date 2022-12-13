import sys

sys.path.append("../")

from utils.constants import STOPWORDS


def remove_stop_words(s: str) -> list:
    s = s.split()
    words = []
    for w in s:
        if w not in STOPWORDS:
            words.append(w)

    return words


def lambda_scaling(l: float):
    return l / 5 + 2.5

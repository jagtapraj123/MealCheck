import sys
sys.path.append("../")

from utils.constants import STOPWORDS
from pymongo import MongoClient

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
            self.mongo_client = MongoClient('localhost', 27017)
            MongoConnector.__instance = self

    def get_user_info(self, user_id):
        pass

    def get_user_vec_p(self, user_id):
        pass

    def get_user_vec_c(self, user_id):
        pass

    def set_user_info(self, user_id, user_info):
        pass

    def set_user_vec_p(self, user_id, user_vec):
        pass

    def set_user_vec_c(self, user_id, user_vec):
        pass

connector = MongoConnector.getInstance()
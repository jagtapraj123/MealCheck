import sys
sys.path.append("../")

from utils.helpers import connector

class User:
    def __init__(self, user_id) -> None:
        self._user_id = user_id
        self.__connector = connector
        self._user_info = self.__connector.get_user_info(user_id)
        self._user_vec_p = self.__connector.get_user_vec_p(user_id)
        self._user_vec_c = self.__connector.get_user_vec_c(user_id)
    
    @property
    def user_vec_p(self):
        return self._user_vec_p

    @user_vec_p.setter
    def user_vec(self, value):
        self._user_vec_p = value
        self.__connector.set_user_vec_p(self._user_id, self._user_vec_p)

    @property
    def user_vec_c(self):
        return self._user_vec_c

    @user_vec_c.setter
    def user_vec(self, value):
        self._user_vec_c = value
        self.__connector.set_user_vec_c(self._user_id, self._user_vec_c)

    @property
    def user_info(self):
        return self.user_info

    @user_info.setter
    def user_info(self, value):
        self._user_info = value
        self.__connector.set_user_info(self._user_id, self._user_info)

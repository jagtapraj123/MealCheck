import sys

sys.path.append("../")

from utils.mongo_connector import connector


class User:
    def __init__(self, user_id) -> None:
        self._user_id = user_id
        self.__connector = connector
        self._user_info = self.__connector.get_user_info(user_id)

        self._recipes_tried = self._user_info["recipes_tried"]
        self._calorie_requirement = self._user_info["calorie_requirement"]

        self._user_vec_p = None
        self._user_vec_c = None
        self._todays_meals = None
        # print(self._todays_meals)

    @property
    def user_vec_p(self):
        if self._user_vec_p is None:
            self._user_vec_p = self.__connector.get_user_vec_p(self._user_id)
        return self._user_vec_p.copy()

    @user_vec_p.setter
    def user_vec(self, value):
        self._user_vec_p = value
        self.__connector.set_user_vec_p(self._user_id, self._user_vec_p)

    @property
    def user_vec_c(self):
        if self._user_vec_c is None:
            self._user_vec_c = self.__connector.get_user_vec_c(self._user_id)
        return self._user_vec_c.copy()

    @user_vec_c.setter
    def user_vec(self, value):
        self._user_vec_c = value
        self.__connector.set_user_vec_c(self._user_id, self._user_vec_c)

    @property
    def user_info(self):
        return self._user_info.copy()

    @user_info.setter
    def user_info(self, value):
        self._user_info = value
        self.__connector.set_user_info(self._user_id, self._user_info)

    @property
    def todays_meals(self):
        if self._todays_meals is None:
            self._todays_meals = self.__connector.get_todays_meals(self._user_id)
        return self._todays_meals.copy()

    @property
    def recipes_tried(self):
        return self._recipes_tried

    @property
    def calorie_requirement(self):
        return self._calorie_requirement

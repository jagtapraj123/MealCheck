#!/bin/bash

kaggle datasets download shuyangli94/food-com-recipes-and-user-interactions

unzip food-com-recipes-and-user-interactions

rm interactions_test.csv interactions_train.csv interactions_validation.csv
import sys

sys.path.append("../")

from flask import Flask, request, json, jsonify, abort
from bson import json_util
import json
from utils.mongo_connector import connector
from utils.helpers import lambda_scaling
from engine.optimizer import get_suggestions, make_and_set_meal_plan
from models.nv import nv
from models.recipes import rspace
import pandas as pd
from multiprocessing import Process

from utils.constants import RAW_RECIPES_PATH


app = Flask(__name__)


@app.route("/", methods=("GET", "POST"))
def hello():
    return "<h1>Hello, World!</h1>"


# User apis
@app.route("/create_user", methods=("GET", "POST"))
def create_user():
    try:
        request_data = json.loads(request.data)
        user = connector.get_user_info(request_data["email"])
        if user is not None:
            return (
                jsonify(
                    {
                        "error": "User already exists!",
                    }
                ),
                403,
            )

        if request_data["gender"] == "Male":
            calorie_requirement = (
                13.397 * request_data["weight"]
                + 4.799 * request_data["height"]
                - 5.677 * request_data["age"]
                + 88.362
            )
        else:
            calorie_requirement = (
                9.247 * request_data["weight"]
                + 3.098 * request_data["height"]
                - 4.330 * request_data["age"]
                + 447.593
            )

        if request_data["activity_level"] == "Low":
            calorie_requirement -= 250
        elif request_data["activity_level"] == "high":
            calorie_requirement += 250

        connector.set_user_info(
            request_data["email"],
            {
                "email": request_data["email"],
                "name": request_data["name"],
                "age": request_data["age"],
                "weight": request_data["weight"],
                "height": request_data["height"],
                "goal": request_data["goal"],
                "activity_level": request_data["activity_level"],
                "gender": request_data["gender"],
                "calorie_requirement": calorie_requirement,
            },
            upsert=True,
        )
        # connector.set_user_info(
        #     request_data["email"],
        #     {
        #         "email": request_data["email"],
        #         "name": request_data["name"],
        #         "age": request_data["age"],
        #         "weight": request_data["weight"],
        #         "height": request_data["height"],
        #         "goal": request_data["goal"],
        #         "activity_level": request_data["activity_level"],
        #     },
        #     upsert=True,
        # )
        created_user = connector.get_user_info(request_data["email"])
        return json.loads(json_util.dumps(created_user))
    except:
        abort(404)


@app.route("/get_user", methods=("GET", "POST"))
def get_user():
    try:
        request_data = json.loads(request.data)
        user = connector.get_user_info(request_data["email"])
        if user is None:
            return (
                jsonify(
                    {
                        "error": "User not found!",
                    }
                ),
                403,
            )

        return json.loads(json_util.dumps(user))
    except:
        abort(404)


# get suggestions
@app.route("/get_suggestions", methods=("GET", "POST"))
def get_recipe_suggestions():
    try:
        request_data = json.loads(request.data)
        l = lambda_scaling(request_data["lambda"])
        suggestions = get_suggestions(request_data["email"], l, request_data["count"])

        return (
            jsonify(
                {
                    "status": "Sucess",
                    "suggestions": suggestions,
                }
            ),
            200,
        )
    except:
        abort(404)


# add ratings
@app.route("/add_rating", methods=("GET", "POST"))
def add_ratings():
    try:
        request_data = json.loads(request.data)
        connector.add_recipe_rating(
            request_data["email"], request_data["recipe_id"], request_data["rating"]
        )
        return (
            jsonify(
                {
                    "status": "Sucess",
                    "message": "Rating added!",
                }
            ),
            200,
        )
    except:
        abort(404)


# set prefs
@app.route("/set_prefs", methods=("GET", "POST"))
def set_prefs():
    try:
        request_data = json.loads(request.data)
        print(request_data)
        connector.set_user_prefs(
            request_data["email"], {"prefs": request_data["prefs"]}
        )
        if connector.get_meal_plan(request_data["email"]) is None:

            making_meal_plan = Process(
                target=make_and_set_meal_plan,
                daemon=True,
                kwargs={"user_id": request_data["email"]},
            )
            making_meal_plan.start()
        return (
            jsonify(
                {
                    "status": "Sucess",
                    "message": "Preferences Added!",
                }
            ),
            200,
        )
    except:
        abort(404)


# livesearch
@app.route("/get_recipes", methods=("GET", "POST"))
def get_recipes():
    request_data = json.loads(request.data)

    # recipes = RecipeSpace.getInstance()
    recipe_names = rspace.get_recipes_matching_str(request_data["string"].lower())

    return (
        jsonify(
            {
                "status": "Sucess",
                "recipes": recipe_names,
            }
        ),
        200,
    )


@app.route("/get_all_recipes", methods=("GET", "POST"))
def get_all_recipes():
    data = pd.read_csv(RAW_RECIPES_PATH)

    json_data = {"recipes": []}

    for r in data.index:
        # print(r, data.loc[r, "name"])
        json_data["recipes"].append(
            {
                "recipe_id": r,
                "name": (" ".join(str(data.loc[r, "name"]).split())).title(),
            }
        )

    return (
        jsonify(
            {
                "status": "Sucess",
                "recipes": json_data["recipes"],
            }
        ),
        200,
    )


@app.route("/get_meal_plan", methods=("GET", "POST"))
def get_meal_plan():
    request_data = json.loads(request.data)

    meal_plan = connector.get_meal_plan(request_data["email"])
    print(meal_plan)
    if meal_plan is not None:
        meals = []
        # recipe_space = RecipeSpace.getInstance()
        for r_id in meal_plan[request_data["day"]]:
            r = rspace.get_recipe_from_id(r_id)
            meals.append(
                {
                    "recipe_id": int(r_id),
                    "name": str(r["name"].title()),
                    "minutes": str(r["minutes"]),
                    "steps": str(r["steps"]),
                    "ingredients": r["ingredients"],
                    "description": r["description"],
                    "nutrition": float(nv.get_nutrition(r_id)),
                    "image": "https://img.delicious.com.au/XOkfl61w/del/2020/12/lemon-chicken-144144-1.jpg",
                }
            )
        return (
            jsonify(
                {
                    "status": "Sucess",
                    "meals": meals,
                }
            ),
            200,
        )
    else:
        abort(404)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=False, use_reloader=True)

import sys

sys.path.append("../")

from flask import Flask, request, json, jsonify, abort
from pymongo import MongoClient
from bson import json_util, ObjectId
import json
from utils.helpers import connector
from engine.optimizer import get_suggestions

app = Flask(__name__)

client = MongoClient("localhost", 27017)

db = client.meal_check
users = db.users


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
            },
            upsert=True,
        )
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
        print(user)
        return json.loads(json_util.dumps(user))
    except:
        abort(404)


@app.route("/get_all_users", methods=("GET", "POST"))
def get_all_users():
    all_users = users.find()
    return json.loads(json_util.dumps(all_users))


# get suggestions
@app.route("/get_suggestions", methods=("GET", "POST"))
def get_recipe_suggestions():
    try:
        request_data = json.loads(request.data)
        suggestions = get_suggestions(request_data["email"],  request_data["lambda"])
        return (
                jsonify(
                    {
                        "status": "Sucess",
                        "suggestions" : suggestions
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
        connector.set_user_prefs(request_data["email"], {
            "prefs" : request_data["prefs"]
        })
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


# reset prefs
def reset_prefs():
    pass

# livesearch
def livesearch():
    pass

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8000, debug=True)

from flask import Flask, request, json, jsonify
from pymongo import MongoClient
from bson import json_util, ObjectId
import json

app = Flask(__name__)

client = MongoClient("localhost", 27017)

db = client.flask_db
users = db.users


@app.route("/", methods=("GET", "POST"))
def hello():
    return "<h1>Hello, World!</h1>"


# User apis
@app.route("/create_user", methods=("GET", "POST"))
def create_user():
    request_data = json.loads(request.data)
    user = users.find_one({"email": request_data["email"]})
    if user is not None:
        return (
            jsonify(
                {
                    "error": "User already exists!",
                }
            ),
            403,
        )
    users.insert_one(
        {
            "email": request_data["email"],
            "name": request_data["name"],
            "age": request_data["age"],
            "weight": request_data["weight"],
            "height": request_data["height"],
            "goal": request_data["goal"],
            "activity_level": request_data["activity_level"],
        }
    )
    created_user = users.find_one({"email": request_data["email"]})
    return json.loads(json_util.dumps(created_user))


@app.route("/get_user", methods=("GET", "POST"))
def get_user():
    request_data = json.loads(request.data)
    user = users.find_one({"email": request_data["email"]})
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


@app.route("/get_all_users", methods=("GET", "POST"))
def get_all_users():
    print("Hi")
    # request_data = json.loads(request.data)
    all_users = users.find()
    print(type(all_users))
    return json.loads(json_util.dumps(all_users))


if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)

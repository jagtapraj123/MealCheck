from flask import Flask, request, json
from pymongo import MongoClient
from bson import json_util, ObjectId
import json

app = Flask(__name__)

client = MongoClient('localhost', 27017)

db = client.flask_db
users = db.users


@app.route('/', methods=('GET', 'POST'))
def hello():
    return '<h1>Hello, World!</h1>'


# User apis
@app.route('/create_user', methods=('GET', 'POST'))
def create_user():
    request_data = json.loads(request.data)
    users.insert_one({'email': request_data['email'], 'name': request_data['name'], 'age': request_data['age'], 'weight': request_data['weight'],
                      'height': request_data['height'], 'goal': request_data['goal'], 'activity_level': request_data['activity_level']})
    created_user = users.find_one({'email': request_data['email']})
    return json.loads(json_util.dumps(created_user))

@app.route('/get_user', methods=('GET', 'POST'))
def get_user():
    request_data = json.loads(request.data)
    user = users.find_one({'email': request_data['email']})
    return json.loads(json_util.dumps(user))



if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)
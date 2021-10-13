from flask import Flask, request, jsonify
import json

app = Flask(__name__)


@app.route('/')
def hello_world():
    """
    Demo function to check the api calls.

    Returns : string
    """
    return 'This is the first API call!'


@app.route("/get_parameters")
def get_parameters():
    """
    Will fetch the required parameters from the json database files.

    Returns : json output
    """
    category = request.args["category"]
    category_map = {"oral_presentations":"Review_Papers/iclr_2021_oral_presentations.json", 
    "poster_presentations":"Review_Papers/iclr_2021_poster_presentations.json",
    "spotlight_presentations":"Review_Papers/iclr_2021_spotlight_presentations.json",
    "withdrawn_rejected_submission":"Review_Papers/iclr_2021_withdrawn_rejected_submissions.json"}
    output_dict = {}
    f = open(category_map[category])
    data = json.load(f)
    for i in data:
        output_dict[i["data_id"]] = {"paper_title":i["paper_title"],"forum_link":i["forum_link"],"pdf_link":i["pdf_link"]}
        if "keywords" in i:
            output_dict[i["data_id"]]["keywords"] = i["keywords"]
    return jsonify(output_dict)

@app.route("/get_categories")
def get_categories():
    """
    Will get the categories from the json database files.

    Returns : json output
    """
    f = open("Review_Papers/iclr_2021_categories.json")
    data = json.load(f)
    return jsonify(data)
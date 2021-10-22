from flask import Flask, request, jsonify
from flask_ngrok import run_with_ngrok
import json

app = Flask(__name__)
run_with_ngrok(app)

database_folder = "/mnt/c/Users/tjsil/OneDrive/Desktop/Review_Papers/"


@app.route('/')
def hello_world():
    """
    Demo function to check the api calls.

    Returns : string
    """
    return 'Flask is running!'


@app.route("/get_parameters")
def get_parameters():
    """
    Will fetch the required parameters from the json database files.

    Returns : json output
    """
    year = request.args["year"]
    category = request.args["category"]
    output_dict = {}
    f = open(database_folder+"iclr_"+year+"/iclr_"+year+"_"+category+".json")
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
    year = request.args["year"]
    folder = "iclr_"+year
    f = open(database_folder+folder+"_categories.json")
    data = json.load(f)
    return jsonify(data)

@app.route("/get_years")
def get_years():
    """
    Will get the years from the json database files.

    Returns : json output
    """
    f = open(database_folder+"years.json")
    data = json.load(f)
    return jsonify(data)

if __name__ == "__main__":
  app.run()
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
    conference = request.args["conference"]
    output_dict = {}
    f = open(database_folder+conference+"_"+year+"/"+conference+"_"+year+"_"+category+".json")
    data = json.load(f)
    return jsonify(data)

@app.route("/get_info")
def get_info():
    """
    Will get the categories from the json database files.

    Returns : json output
    """
    years = ["2018","2019","2020","2021"]
    conferences = ["iclr"]
    global_conf_list=[]
    for conference in conferences:
        conference_dict = {}
        data = []
        for year in years:
            temp_dict={}
            categories={}
            f = open(database_folder+conference+"_"+year+"_categories.json")
            file_data = json.load(f)
            for i in list(file_data.keys()):
                categories[i.lower().replace(" ","_")]=i
            temp_dict["year"] = year
            temp_dict["categories"] = categories
            data.append(temp_dict)
        conference_dict["name"] = conference
        conference_dict["readable_name"] = conference.upper()
        conference_dict["data"]=data
        global_conf_list.append(conference_dict)
    return jsonify(global_conf_list)

@app.route("/get_comments")
def get_comments():
    """
    Will get the comments wrt the presentation.

    Returns : json output
    """
    year = request.args["year"]
    category = request.args["category"]
    conference = request.args["conference"]
    data_id = request.args["data_id"]

    f = open(database_folder+conference+"_"+year+"/"+conference+"_"+year+"_"+category+"/"+data_id+"_comments.json")
    data=json.load(f)

    if year=="2020":
        for d in data:
            key_list = list(d["content"].keys())
            for k in key_list:
                if "review_assessment" in k:
                    d["content"][k.split("review_assessment:_")[1].replace("_"," ")]=d["content"].pop(k)
                elif "experience_assessment" in k:
                    d["content"]["experience assessment"] = d["content"].pop(k)

    return jsonify(data)

if __name__ == "__main__":
  app.run()
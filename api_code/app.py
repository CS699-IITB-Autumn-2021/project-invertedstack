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
    # for i in data:
    #     output_dict[i["data_id"]] = {"paper_title":i["paper_title"],"forum_link":i["forum_link"],"pdf_link":i["pdf_link"],"authors":i["authors"],"abstract":i["abstract"]}
    #     if "keywords" in i:
    #         output_dict[i["data_id"]]["keywords"] = i["keywords"]
    return jsonify(output_dict)

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


    # year = request.args["year"]
    # conference = request.args["conference"]
    # f = open(database_folder+conference+"_"+year+"_categories.json")
    # data = json.load(f)
    # return jsonify(data)

# @app.route("/get_years")
# def get_years():
#     """
#     Will get the years from the json database files.

#     Returns : json output
#     """
#     f = open(database_folder+"years.json")
#     data = json.load(f)
#     return jsonify(data)

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
    return jsonify(data)

if __name__ == "__main__":
  app.run()
# Author - Tejpalsingh Siledar - 21Q050008


"""
This file contains code for all the api's to support the linking and fetching 
of the data from the json files.

API calls present in the file

** /
- This is a dummy api call to check if the server is working. This will return a simple
message saying that the "Flask is running!"

** /get_parameters
- This api call is to fetch the information from conference json files. 
- Further improvements
    - This call can be modified to serve only specific key value information from the 
    conference json files.

** /get_info
- This api call is to fetch all the category files present in the json database. 
- Further improvements 
    - The year and conference can be automated based on the json files 
    present in the database.
    - NULL values can be handled at the server side to improve this further.

** /get_comments
- This api call is to fetch all the comments specific to a particular paper. The paper id 
from conference file is used to map the comment file and fetch all the comments.
- Further improvements 
    - Comments are hierarchical and can be parsed at the server side to
    preprocess all the comments in proper format before sending it as the api response

"""


# Importing libraries.
from flask import Flask, request, jsonify
from flask_ngrok import run_with_ngrok
import json

# Initializing flask
app = Flask(__name__)

# Uncomment the line below to run with ngrok
# run_with_ngrok(app)

# Json files location.
database_folder = "/mnt/c/Users/tjsil/OneDrive/Desktop/Review_Papers/"

# Dummy API call.
@app.route('/')
def hello_world():
    """
    This is a demo function to check the api calls.

    Returns
    -------
    A sample string output.
    """
    return 'Flask is running!'


# API call to get parameters.
@app.route("/get_parameters")
def get_parameters():
    """
    Will fetch the all the data wrt a particular conference paper. 
    API arguments are year, category and conference.

    Returns
    -------
    json output containing conference specific data.
    """
    try:
        # Get arguments from api call.
        year = request.args["year"]
        category = request.args["category"]
        conference = request.args["conference"]

        # Read the json file corresponding to the api call arguments.
        f = open(database_folder + conference + "_" + year + "/" + conference + "_" + year + "_" + category + ".json")
        
        # Load the json file.
        data = json.load(f)

        # Code for preprocessing the data before returning. Handling of the empty strings.
        for d in data:
            for k in d.keys():
                if type(d[k]) == str:
                    if len(d[k]) == 0:
                        d[k] = "N.A."
    except:
        # Return if some error occurs.
        return jsonify("Invalid arguments please check.")

    # Returning the data.
    return jsonify(data)


# API call to get info.
@app.route("/get_info")
def get_info():
    """
    Will get the categories from the json database files.

    Returns
    -------
    json output containing all the info.
    """
    try:
        # Setting up years and conference. 
        years = ["2018", "2019", "2020", "2021"]
        conferences = ["iclr"]

        # global_conf_list collects data to be returned.
        global_conf_list = []

        # Looping over all conferences.
        for conference in conferences:
            conference_dict = {}
            data = []

            #Looping over all years.
            for year in years:
                temp_dict = {}
                categories = {}

                # Reading from the appropriate json file corresponding to the conference and year.
                f = open(database_folder + conference + "_" + year + "_categories.json")

                # Load json file.
                file_data = json.load(f)

                # Preprocessing the data before returning it. Replace empty space with _
                for i in list(file_data.keys()):
                    categories[i.lower().replace(" ", "_")] = i

                # Collect the data for each year in a list.
                temp_dict["year"] = year
                temp_dict["categories"] = categories
                data.append(temp_dict)

            # Collect data corresponding to each conference in a list.
            conference_dict["name"] = conference
            conference_dict["readable_name"] = conference.upper()
            conference_dict["data"] = data
            global_conf_list.append(conference_dict)
    except:
        # Return if some error occurs.
        return jsonify("Invalid arguments please check.")

    # Return the data.
    return jsonify(global_conf_list)


# API call to get comments.
@app.route("/get_comments")
def get_comments():
    """
    Will get the comments wrt the presentation.

    Returns
    -------
    json output containing comments.
    """
    try:
        # Get arguments from the api call.
        year = request.args["year"]
        category = request.args["category"]
        conference = request.args["conference"]
        data_id = request.args["data_id"]

        # Read the json file corresponding to the api arguments.
        f = open(
            database_folder + conference + "_" + year + "/" + conference + "_" + year + "_" + category + "/" + data_id
            + "_comments.json")

        # Load the json file.
        data = json.load(f)

        # Preprocessing of the json before returning. Handling of the improper keys.
        if year == "2020":
            for d in data:
                key_list = list(d["content"].keys())
                for k in key_list:
                    if "review_assessment" in k:
                        d["content"][k.split("review_assessment:_")[1].replace("_", " ")] = d["content"].pop(k)
                    elif "experience_assessment" in k:
                        d["content"]["experience assessment"] = d["content"].pop(k)
    except:
        # Return if some error occurs.
        return jsonify("Invalid arguments please check.")

    # Return the data.
    return jsonify(data)


# Main function declaration.
if __name__ == "__main__":
    # App run call.
    app.run()

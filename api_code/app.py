from flask import Flask, request, jsonify
from flask_ngrok import run_with_ngrok
import json

app = Flask(__name__)
# run_with_ngrok(app)

database_folder = "/mnt/c/Users/tjsil/OneDrive/Desktop/Review_Papers/"


@app.route('/')
def hello_world():
    """
    This is a demo function to check the api calls.

    Returns
    -------
    A sample string output.
    """
    return 'Flask is running!'


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


@app.route("/get_info")
def get_info():
    """
    Will get the categories from the json database files.

    Returns
    -------
    json output
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


@app.route("/get_comments")
def get_comments():
    """
    Will get the comments wrt the presentation.

    Returns
    -------
    json output
    """
    try:
        year = request.args["year"]
        category = request.args["category"]
        conference = request.args["conference"]
        data_id = request.args["data_id"]

        f = open(
            database_folder + conference + "_" + year + "/" + conference + "_" + year + "_" + category + "/" + data_id
            + "_comments.json")
        data = json.load(f)

        if year == "2020":
            for d in data:
                key_list = list(d["content"].keys())
                for k in key_list:
                    if "review_assessment" in k:
                        d["content"][k.split("review_assessment:_")[1].replace("_", " ")] = d["content"].pop(k)
                    elif "experience_assessment" in k:
                        d["content"]["experience assessment"] = d["content"].pop(k)
    except:
        return jsonify("Invalid arguments please check.")

    return jsonify(data)


if __name__ == "__main__":
    app.run()

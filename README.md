# ReviewsX

## Problem Statement/Motivation
- During RnD, you read a lot of papers and take notes of those for future reads
- You can probably use Google Keep or Microsoft OneNote for this
- But they aren't the greatest of the tools for the job -- they don't support many rich text facilities such as 
	- Maths
	- Code 
	- Markdown
- Often these apps will also need internet 
- There's also no in-built way in Google Keep / Microsoft OneNote to look at reviews of the paper from a site such as OpenReview

## Slogan
Minimalist OpenReview client with support for native markdown notes

## List of Features
- Build a note taking app that -
	- Runs offline
	- Supports math, code and markdown notes
	- Allows the user to favourite some of the notes 
	- Allows viewing and downloading papers and discussions from OpenReview 
	- Supports collections of papers 
	- Supports reading lists -- currently reading, already read and wishlist

## Technology Stack
- **Python**
- **Beautiful Soup**
- **Selenium**
- Flask
- **Shell Scripting**
- **Android**

## List of Deliverables
- [x] Server - capable of scraping the data from OpenReview for a given conference and serving the data to the android app 
- [x] Android app - with all the features stated above

## Hardware/Software Requirements.

### Hardware Requirements.
- Atleast 100MB of free storage space.

### Software Requirements.
- Android OS with minimum android version 6.0 (API level 23).
- (optional) PDF reader for viewing the PDF of the paper.

## How to operate?

### Scraping Papers
The following steps can be used to run the scraping code.

#### Dependencies

- Python version 3.8.0 (compatible with any Python 3 version)
- Chromebrowser
- Chromedriver (of a version compatible with your Chrome Browser)
- Libraries used:
    - selenium
    - requests
    - os
    - glob
    - tqdm
    - json
    - time

#### Chromedriver

To run the scraping code, please go to [this link](https://chromedriver.chromium.org/downloads) and download the chromedriver compatible with the version of your Chrome browser.

To find the version of your Chrome Browser, please follow [this link](https://help.zenplanner.com/hc/en-us/articles/204253654-How-to-Find-Your-Internet-Browser-Version-Number-Google-Chrome).

After downloading the zip file, extract the chromedriver file and save it on your computer. 

The path of this chromedriver should be provided in the chrome_driver_path variable in Config.py file.

#### How to run?

To run the code, go to the directory containing main.py file and run the following command - 

`python3 main.py`

In the following subsections, each file is described.

#### File Descriptions
##### Config.py

- To change configurations, the values in Config.py can be changed. The configurations include the following : 
    - data_dir : specify the path of directory where scraped json files will be stored
    - chrome_driver_path : specify the path where chromedriver is placed
    - home_url : specify the home-url which needs to be scraped. This version of ReviewsX supports scraping only 'https://openreview.net/'
    - venue_name : specify the name of venue which needs to be scraped. This version of ReviewsX scrapes all conferences of ICLR venue.
    - years : specify a comma separate list of years in quotation marks which needs to be scraped.
    - limit : specify the maximum number of papers that needs to be scraped for every category. If all papers need to be scraped then keep limit as None.

##### iclr_crawler.py

Contains class PageCrawler which contains methods to scrape data of all papers of all categories for all the years of conferences of a venue.

##### crawl_forum_comments.py

Contains code to scrape the comments from a forum of a particular paper. The code creates a json file per forum containing all forum comments and their replies in a proper nested structure.

##### main.py

The main code which imports Config.py, iclr_crawler.py and crawl_forum_comments.py and calls the appropriate functions.


#### Structure of json files and folders

The following sample tree structure is showing considering only 2021 year i.e. in the Config.py file the years variable is '2021'

```
data
└───iclr_2021
│   └───iclr_2021_oral_presentations
│       │   <paper_id1>.json
│       │   <paper_id1>.json
│       │   ...
│   └───iclr_2021_poster_presentations
│       │   <paper_id1>.json
│       │   <paper_id1>.json
│       │   ...
│   └───iclr_2021_spotlight_presentations
│       │   <paper_id1>.json
│       │   <paper_id1>.json
│       │   ... 
│   └───iclr_2021_withdrawn_rejected_submissions
│       │   <paper_id1>.json
│       │   <paper_id1>.json
│       │   ... 
|   │   iclr_2021_oral_presentation.json
|   │   iclr_2021_poster_presentations.json
|   |   iclr_2021_spotlight_presentations.json
|   │   iclr_2021_withdrawn_rejected_submissions.json
```

All the json files for different conferences in separate folders. For e.g., if the config file has 4 years - 2021,2020,2019 and 2018 - then 4 separate folders will get created - 
with the name iclr_year. Inside the folder, json files containing extracted information for all the papers for a particular category will be stored. 

The comments scraped from each paper discussions are stored in separate json files per paper. These files are stored in a separate folder for each category with the name paper-id.json

#### Year wise categories of ICLR conferences are scraped and saved in - iclr_year_categories.json
    
For instance the ICLR categories for the year 2021 in the file iclr_2021_categories.json - 

```
{
      "Oral Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#oral-presentations",
      "Spotlight Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#spotlight-presentations",
      "Poster Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#poster-presentations",
      "Withdrawn/Rejected Submissions": "https://openreview.net/group?id=ICLR.cc/2021/Conference#withdrawn-rejected-submissions"
}
```

#### Json files are created for every ICLR conference per category
The structure of the values in the json files is as follows - 

```
{
            "data_id": "",          
            "paper_title": "",
            "forum_link": "",             
            "pdf_link": "",           
            "authors": [            ],           
            "reviewed-version-(pdf)": "",           
            "keywords": "",           
            "abstract": "",           
            "code-of-ethics": "",
            "one-sentence-summary": "",
            "supplementary-material": ""          
 }
```

#### Comments information for each paper is saved in a separate json file
The structure of the json files for comments is as follows -
```
{
        "id": id of the comment,
        "content": {
            "title": title of the comment,
            "decision": Accept/Reject,
            "comment": comment content
        },
        "date": timestamp when the comment was posted,
        "signatures": author or group of authors who posted the comment,
        "reply": [the same nested comment structure, if any replies are there]
    },
```


### Flask API's
The following steps can be used to run the flask api's.

#### Dependencies

- Python version 3.8.0 (compatible with any version)
- Libraries used:
	- flask
	- json
	- flask_ngrok

#### How to run?

To run the code, go to the directory containing the app.py and run the following command - 

`flask run`
or 
`python app.py`

#### File Descriptions

##### app.py

Contains the flask api definitions which will act as the middleware to support the linking between the android and the scraped database.


### Android App
Users can do following tasks within the android app

#### Downloading data for a conference category 
- On the home screen users will see a list of cards with conference name, conference year and a particular category in that particular conference
- Simply tapping on that category will fetch all the papers in that specific category from the server

#### Searching for relevant papers within a category
- Users should simply tap the "search" button (magnifying glass icon) in the action bar and type the query 
- The app will filter the papers based on whether the query appears anywhere in the paper title, abstract or keywords

#### Viewing discussions for a particular paper
- From the category-wise or collection list of papers, simply tap on the paper to view discussions corresponding to the paper
- If the user is viewing discussions for a particular paper for the very first time, they will be fetched from the server and stored in the DB for future uses 

#### Viewing PDF for a particular paper
- From the discussion view of the paper, simply click on the PDF button in the action bar to view/download the PDF 

#### Taking notes for a particular paper
- From the discussion view of the paper, click on the "notes" button (2.5 lines icon next to PDF icon) in the action bar to open Notes related activity
- For the first time, a default note will be shown
- Click on the "edit" button (pencil icon in the action bar) to start editing the note. The editor supports live editing (you can immediately see the results real-time) along with full markdown as well as LaTeX
- Once the edit is done, click on the "save" button (floppy disk icon in the action bar) and save the note. This note is saved to the DB locally and will ALWAYS be available on the device
- Users can access this note by going into "All notes" section from the left hand drawer on the home screen 

#### Adding a particular paper to a collection 
- Open category wise paper list containing the paper 
- Scroll down to the intended paper and click on "Save to collection" button
- A list of collections with checkbox in front of each of the names of collections will be shown
- Select the collections as needed and click "Save"

#### Removing a particular paper from a collection
- Open the collection to which the paper belongs to 
- Scroll down to the intended paper and click on "Remove" button

Alternatively, 
- Open category wise paper list containing the paper 
- Scroll down to the intended paper and click on "Save to collection" button
- A list of collections with checkbox in front of each of the names of collections will be shown
- Deselect the collections as needed and click "Save"

#### Creating a new collection
- Open the left hand side drawer from the home screen
- Click on "New Collection" button to create the new collection
- The new collection must have a unique name and must be different from "Favourites", "Wishlist", "Already read", "Currently reading" and "All notes"

#### Deleting the entire collection
- Open the left hand side drawer from the home screen
- Go to the intended collection and click on the "delete" button (dustbin icon) on the right of it
- Note that only user created collections can be deleted

## Primary stakeholders of the product
- Research students.
- Professors.
- Anyone interested in reading research papers!

**Regarding concerns about NOT having the search functionality across conferences** - This was brought up in the discussion during the final demo and we (authors of this project) felt that we did not explain this properly. The main reason behind not having this functionality was primarily because of our stakeholders. Intended users of this app already know a thing-or-two about the topic they're interested in. Mainly the stakeholders are often going to think like "Hey what are some spotlight papers at ICLR 2022 that I might be interested in ?". These questions will always be specific to a particular category of the conference because often when attending these big conferences, you want to make sure that you don't miss a talk or a poster. If the conference happened in previous years, it's still valuable to search in a particular category so that you can catch up on that particular talk or poster using archives. For these types of users, a general purpose, all-conference search serves "less" functionality because then the users will have to parse through more info.

Therefore, there is technically no need to have this functionality on the home screen and hence we did not promise it in any of the presentations before. While we agree that this functionality could be useful for more general public, we decided to omit this from the demo since we did not promise it at all in any of our previous presentations. Moreover, implementing this functionality is a trivial task given the modularity of the app and middleware code. Even then, we wanted to focus more on delivering the promised requirements super well hence we focussed a LOT more on testing the promised requirements.

## Team details and contribution.

### Scraping the data off of OpenReview - Ashita Saxena - 21Q050009
- Scraping openreview.net for all ICLR  conferences.
- Extracting all papers for all categories and comments from discussion forums.
- Making the scraping configurable and extensible to other conferences as per requirements.

### Middleware and server management - Tejpalsingh Siledar - 21Q050008
- Creation and hosting of APIs to serve the requests to the Android App.
- Preprocessing and cleaning up of data catering to the needs of the frontend.
- Handling non-existent data that might be required by Android app cleanly.

### Android app - Ashutosh Sathe - 21Q050012
- Core markdown rendering functionality on Android.
- Conference agnostic interface with middleware.
- Efficient on-device storage using SQLite DB.

# Path to Code Documentation
* [Middleware and Server](docs/Python-SphinxDoc/html/index.html)
* [Android App](docs/ReviewsX-JavaDoc/index.html)

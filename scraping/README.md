# scraping code #

## Dependencies ##

- Python version 3.8.0 (compatible with any Python 3 version)
- Libraries used:
    - selenium
    - requests
    - os
    - glob
    - tqdm
    - json
    - time
   
## Chromedriver ##

To run the scraping code, please go to [this link](https://chromedriver.chromium.org/downloads) and download the chromedriver compatible with the version of your Chrome browser.

To find the version of your Chrome Browser, please follow [this link](https://help.zenplanner.com/hc/en-us/articles/204253654-How-to-Find-Your-Internet-Browser-Version-Number-Google-Chrome).

After downloading the zip file, extract the chromedriver file and save it on your computer. 

The path of this chromedriver should be provided in the chrome_driver_path variable in Config.py file.


## How to run? ##

To run the code, go to the directory containing main.py file and run the following command - 

`python3 main.py`

In the following subsections, each file is described.

## File Descriptions ##
### Config.py ###

- To change configurations, the values in Config.py can be changed. The configurations include the following : 
    - data_dir : specify the path of directory where scraped json files will be stored
    - chrome_driver_path : specify the path where chromedriver is placed
    - home_url : specify the home-url which needs to be scraped. This version of ReviewsX supports scraping only 'https://openreview.net/'
    - venue_name : specify the name of venue which needs to be scraped. This version of ReviewsX scrapes all conferences of ICLR venue.
    - years : specify a comma separate list of years in quotation marks which needs to be scraped.
    - limit : specify the maximum number of papers that needs to be scraped for every category. If all papers need to be scraped then keep limit as None.

### iclr_crawler.py ###

Contains class PageCrawler which contains methods to scrape data of all papers of all categories for all the years of conferences of a venue.

### crawl_forum_comments.py ###

Contains code to scrape the comments from a forum of a particular paper. The code creates a json file per forum containing all forum comments and their replies in a proper nested structure.

### main.py ###

The main code which imports Config.py, iclr_crawler.py and crawl_forum_comments.py and calls the appropriate functions.


## Structure of json files and folders ##

All the json files for different conferences in separate folders. For e.g., if the config file has 4 years - 2021,2020,2019 and 2018 - then 4 separate folders will get created - 
with the name iclr_year. Inside the folder, json files containing extracted information for all the papers for a particular category will be stored. 

The comments scraped from each paper discussions are stored in separate json files per paper. These files are stored in a separate folder for each category with the name paper-id.json

### Year wise categories of ICLR conferences are scraped and saved in - iclr_year_categories.json for the years 2021, 2020, 2019 and 2018 ###
    
For instance the ICLR categories for the year 2021 in the file iclr_2021_categories.json - 

{

      "Oral Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#oral-presentations",
      
      "Spotlight Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#spotlight-presentations",
      
      "Poster Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#poster-presentations",
      
      "Withdrawn/Rejected Submissions": "https://openreview.net/group?id=ICLR.cc/2021/Conference#withdrawn-rejected-submissions"

}

### Json files are created for every ICLR conference per category ###
The structure of the values in the json files is as follows - 

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

### Comments information for each paper is saved in a separate json file ###
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

# scraping code #

## Dependencies ##

- Python version 3.8.0 (compatible with any Python 3 version)
- Libraries used:
    - selenium
    - requests
    - os
    - glob
    - tqdm
    - pprint
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


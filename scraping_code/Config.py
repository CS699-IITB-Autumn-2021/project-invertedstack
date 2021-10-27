"""Documentation

**Task** - This file contains the configuration for the scraping code.
This file contains a Config class specifying various variables
related to scraping the papers and comments from openreviews.net

This file is imported in the main.py file 

**Author** - Ashita Saxena (21Q050009)

"""
class Config:
    '''
    This class contains variables that can be configured as per need. 
    '''
    #specify the path of directory where scraped json files will be stored
    data_dir = '../data/'
    #specify the path where chromedriver is placed
    chrome_driver_path = r'./chromedriver'
    #specify the home-url which needs to be scraped
    #this version supports only open-review
    home_url =  'https://openreview.net/'
    #specify the name of venue which needs to be scraped
    #this version scrapes all conferences of ICLR venue
    venue_name = 'ICLR'
    #specify the list of years which needs to be scraped
    years = ['2021']
    #specify the maximum number of papers that needs to be scraped for every category
    #if all papers need to be scraped then keep limit as None
    limit = 10
    
    
    
    

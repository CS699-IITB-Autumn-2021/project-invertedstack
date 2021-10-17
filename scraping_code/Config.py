class Config:
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
    years = ['2021','2020','2019','2018']
    #specify the maximum number of papers that needs to be scraped for every category
    #if all papers need to be scraped then keep limit as None
    limit = 50
    
    
    
    

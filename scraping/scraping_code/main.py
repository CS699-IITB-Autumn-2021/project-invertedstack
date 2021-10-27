"""Documentation

**Task** - This file is the entry point of the scraping code.
This file imports all the other modules containing functions
related to scraping the papers and comments from openreviews.net

We should be able to execute it as 

``$ python3 main.py``

Script should produce json data in the configured path.

**Author** - Ashita Saxena (21Q050009)

"""
import Config
import os,glob,json
import iclr_crawler
import crawl_forum_comments
import pprint 

if __name__ == "__main__":
    """Main method to call all the required functions.
    """
    config = Config.Config()
    home_url = config.home_url

    if not os.path.exists(config.data_dir):
        os.makedirs(config.data_dir)
    
    crawler = iclr_crawler.PageCrawler()
    #get all venues
    all_venues = crawler.get_all_venues(config.home_url)

    #get list of all iclr entries in open review
    iclr_conf = crawler.get_list_of_iclr_conf(all_venues[config.venue_name])

    #saving all the paper details for the years in json format
    for name in iclr_conf:
        if any(year in name for year in config.years):
            categories = {}
            conf_link=""
            with open(config.data_dir+name.lower().replace(" ", "_")+"_categories.json", "w") as f:
                conf_link = crawler.get_iclr_conf_link(iclr_conf[name])
                print("getting categories from - ", conf_link)
                categories = crawler.get_categories_from_iclr_conf(conf_link)
                json.dump(categories,f,indent=6)
            print("calling save_json_of_all_iclr ", name.strip()[-4:], " ", conf_link)
            pprint.pprint(categories)
            crawler.save_json_of_all_iclr(conf_link, name.strip()[-4:], categories, config.data_dir)

    #saving all the comments for all the papers
    for year in config.years:
        path = config.data_dir+"iclr_"+year+"/"
        files = [f for f in os.listdir(path) if os.path.isfile(os.path.join(path, f))]
        print(files)
        for file in files:
            cur_path = path+file[0:-5]+"/"
            if not os.path.exists(cur_path):
                print(cur_path)
                os.makedirs(cur_path)
            with open(path+file,'r') as f:
                papers = json.load(f)
                for paper in papers:
                    crawl_forum_comments.get_all_comments_for_forum_id(paper['data_id'], cur_path)

            


        

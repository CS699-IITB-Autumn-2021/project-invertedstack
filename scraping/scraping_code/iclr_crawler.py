"""Documentation

**Task** - This file contains the methods for scraping all the papers
of a conferences of ICLR from Openreview.

Contains class PageCrawler which contains methods to 
scrape data of all papers of all categories for 
all the years of conferences of a venue.

This file is imported in the main.py file 

**Author** - Ashita Saxena (21Q050009)

"""
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import json
import time
import os
import pprint
from tqdm import tqdm
import Config

config = Config.Config()


class PageCrawler:
    '''
    This class contains methods which
    can be used to scraped from openreview.net
    '''
    def __init__(self):
        '''This method initialzes the PageCrawler object with
        chrome options and chrome path to enable use of selenium
        Parameters
        ----------
        None

        Returns
        -------
        None
        '''
        self.chrome_options = Options()
        self.chrome_options.add_argument("--headless")
        # Reference the local Chromedriver instance
        self.chrome_path = config.chrome_driver_path

    def get_all_venues(self, url):
        '''This method scrapes all the venues
        which are available in the home_url
        Parameters
        ----------
        url : str
            url or the home page of open review

        Returns
        -------
        A dictionary containing all venues along with their url
        '''
        driver = webdriver.Chrome(executable_path=self.chrome_path,
                    options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)

        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#all-venues a")
            if(len(elements) > 0):
                ele_available = True
        all_venues = {}
        for element in elements:
            all_venue_link = element.get_attribute("href")
            all_venue_name = element.get_attribute("innerHTML")
            all_venues[all_venue_name] = all_venue_link

        driver.quit()

        return all_venues

    def get_list_of_iclr_conf(self, url):
        """This method scrapes and gets a list of
        all available iclr conferences.
        Parameters
        ----------
        url : str
            url of the venue home page
        
        Returns
        -------
        A dictionary containing all conferences of the
        venue along with the urls
        """
        driver = webdriver.Chrome(executable_path=self.chrome_path,
                                    options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)

        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#notes a")
            if(len(elements) > 0):
                ele_available = True
        iclr_confs = {}
        for element in elements:
            iclr_conf_link = element.get_attribute("href")
            iclr_conf_name = element.get_attribute("innerHTML")
            iclr_confs[iclr_conf_name] = iclr_conf_link

        driver.quit()

        return iclr_confs

    def get_iclr_conf_link(self, url):
        """This method finds the conference link
        out of other links from the venues. 
        Parameters
        ----------
        url : str
            url of the venure for a particular year
        
        Returns
        -------
        url string of the conference link for a particular year
        """
        driver = webdriver.Chrome(executable_path=self.chrome_path,
                                    options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)

        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#notes a")
            if(len(elements) > 0):
                ele_available = True
        iclr_conf_link = ""
        for element in elements:
            if("conference" in element.get_attribute("innerHTML").lower()):
                iclr_conf_link = element.get_attribute("href")

        driver.quit()

        return iclr_conf_link

    def get_categories_from_iclr_conf(self, url):
        """This method scrapes the categories 
        from a particular iclr conf. 
        Parameters
        ----------
        url : str
            url of the conference for a particular year
        
        Returns
        -------
        A dictionary containing the category name and the link
        """
        driver = webdriver.Chrome(executable_path=self.chrome_path,
                    options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)

        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("ul.nav-tabs a")
            if(len(elements) > 0):
                ele_available = True
        iclr_conf_cats = {}
        for element in elements:
            iclr_conf_link = element.get_attribute("href")
            iclr_conf_name = element.get_attribute("innerHTML")
            if "your" not in iclr_conf_name.lower():
                iclr_conf_cats[iclr_conf_name.strip()] = iclr_conf_link

        driver.quit()

        return iclr_conf_cats

    def get_conf_papers_from_category(self, category_url, year, category,
            output_path, limit=config.limit):
        """This method scrapes the papers 
        from a particular iclr conf category.
        Parameters
        ----------
        category_url : str
            url of the conference for a category for a particular year
        year : str
            year of the conference
        category : str
            category of the conference
        output_path : str
            path of the output for json files
        limit : int
            maximum number of files to be scraped
        
        Returns
        -------
        None
        """
        driver = webdriver.Chrome(executable_path=self.chrome_path,
                    options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(category_url)
        print(category_url)

        time.sleep(30)

        ele_available = False
        count = 0
        while not ele_available:
            li_of_all_papers = driver.find_elements_by_css_selector("""ul.submissions-list li.note""")
            count += 1
            print("count -", count, len(li_of_all_papers))
            if(len(li_of_all_papers) > 0):
                ele_available = True

        list_of_all_papers = []
        if not limit:
            limit = len(li_of_all_papers)
        for element in tqdm(li_of_all_papers[:limit], "creating data"):
            details_of_curr_paper = {}
            # get data-id
            details_of_curr_paper["data_id"] = element.get_attribute("data-id")
            # get forum and pdf links
            links = element.find_elements_by_css_selector("h4 a")
            details_of_curr_paper["paper_title"] = links[0].get_attribute("innerHTML").strip()
            details_of_curr_paper["forum_link"] = links[0].get_attribute("href")
            if len(links) > 1:
                details_of_curr_paper["pdf_link"] = links[1].get_attribute("href")
            else:
                details_of_curr_paper["pdf_link"] = ""
            # get all authors
            authors = []
            author_list = element.find_elements_by_css_selector("""div.note-authors
                                                        a.profile-link""")
            for author in author_list:
                authors.append(author.get_attribute("innerHTML"))
            details_of_curr_paper["authors"] = authors
            # get meta-info
            meta_info = element.find_elements_by_css_selector("ul.note-content li")
            for ele in meta_info:
                key = ele.find_element_by_css_selector("strong.note-content-field").get_attribute("innerHTML")
                key = key.strip()
                value = ele.find_element_by_css_selector("span.note-content-value").get_attribute("innerHTML")
                value = value.strip()
                details_of_curr_paper[key[:-1].lower().replace(' ', '-')] = value
            list_of_all_papers.append(details_of_curr_paper)

        outputfile = output_path + "/iclr_" + year + "_" + category.lower().replace("/", " ").replace(" ", "_")+".json"
        print("creating file : ", outputfile)
        with open(outputfile, "w") as f:
            json.dump(list_of_all_papers, f, indent=6)
        print("File created!!")
        driver.quit()

        return list_of_all_papers

    def save_json_of_all_iclr(self, url, year, categories, output_dir):
        """This method saves json files
        for all the categories of a particular conf
        Parameters
        ----------
        url : str
            url of the conference for a category for a particular year
        year : str
            year of the conference
        category : str
            category of the conference
        output_path : str
            path of the output for json files
        
        Returns
        -------
        None
        """
        output_path = output_dir + "iclr_" + year
        if not os.path.exists(output_path):
            os.makedirs(output_path)
        for cat in categories:
            self.get_conf_papers_from_category(categories[cat], year, cat, output_path)

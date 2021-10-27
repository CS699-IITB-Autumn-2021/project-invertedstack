from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import json

class openReview:
    
    def __init__(self):
        self.chrome_options = Options()
        self.chrome_options.add_argument("--headless") # Hides the browser window
        # Reference the local Chromedriver instance
        self.chrome_path = r'chromedriver'

    def get_active_venues(self,url):
        
        driver = webdriver.Chrome(executable_path=self.chrome_path, options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)


        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#active-venues a")
            if(len(elements)>0):
                ele_available = True
        active_venues = {}
        for element in elements:
            active_venue_link = element.get_attribute("href")
            active_venue_name = element.get_attribute("innerHTML")
            active_venues[active_venue_name] = active_venue_link

        driver.quit()

        return active_venues

    def get_open_venues(self,url):
        driver = webdriver.Chrome(executable_path=self.chrome_path, options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)


        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#open-venues a")
            if(len(elements)>0):
                ele_available = True
        open_venues = {}
        for element in elements:
            open_venue_link = element.get_attribute("href")
            open_venue_name = element.get_attribute("innerHTML")
            open_venues[open_venue_name] = open_venue_link

        driver.quit()

        return open_venues

    def get_all_venues(self,url):
        driver = webdriver.Chrome(executable_path=self.chrome_path, options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)


        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#all-venues a")
            if(len(elements)>0):
                ele_available = True
        all_venues = {}
        for element in elements:
            all_venue_link = element.get_attribute("href")
            all_venue_name = element.get_attribute("innerHTML")
            all_venues[all_venue_name] = all_venue_link

        driver.quit()

        return all_venues
    
    def get_list_of_iclr_conf(self,url):

        driver = webdriver.Chrome(executable_path=self.chrome_path, options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)


        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#notes a")
            if(len(elements)>0):
                ele_available = True
        iclr_confs = {}
        for element in elements:
            iclr_conf_link = element.get_attribute("href")
            iclr_conf_name = element.get_attribute("innerHTML")
            iclr_confs[iclr_conf_name] = iclr_conf_link

        driver.quit()

        return iclr_confs

    def get_iclr_conf_link(self,url):
        driver = webdriver.Chrome(executable_path=self.chrome_path, options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)

        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("div#notes a")
            if(len(elements)>0):
                ele_available = True
        iclr_conf_link=""
        for element in elements:
            if("conference" in element.get_attribute("innerHTML").lower()):
                iclr_conf_link =  element.get_attribute("href")
            

        driver.quit()

        return iclr_conf_link

    def get_categories_from_iclr_conf(self,url):
        driver = webdriver.Chrome(executable_path=self.chrome_path, options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(url)


        ele_available = False
        while not ele_available:
            elements = driver.find_elements_by_css_selector("ul.nav-tabs a")
            if(len(elements)>0):
                ele_available = True
        iclr_conf_cats = {}
        for element in elements:
            iclr_conf_link = element.get_attribute("href")
            iclr_conf_name = element.get_attribute("innerHTML")
            if "your" not in iclr_conf_name.lower():
                iclr_conf_cats[iclr_conf_name.strip()] = iclr_conf_link

        driver.quit()

        return iclr_conf_cats

if __name__ == "__main__":
    
    home_url = "https://openreview.net/"


    OR = openReview()
    print(OR.get_active_venues(home_url))
    print(OR.get_open_venues(home_url))
    all_venues = OR.get_all_venues(home_url)
    
    print(all_venues)
    
    all_iclr = OR.get_list_of_iclr_conf(all_venues['ICLR'])
    print(all_iclr)


    #get conf name 
    for name in all_iclr:
        if '2021' in name:
            iclr_2021_conf = all_iclr[name]
    

    #get iclr conference link        
    iclr_conf_link = OR.get_iclr_conf_link(iclr_2021_conf)
    print("ICLR CONF LINK",iclr_conf_link)

    #get all categories from a iclr conference
    categories = OR.get_categories_from_iclr_conf(iclr_conf_link)

    with open("iclr_2021_categories.json","w") as f:
        json.dump(categories,f,indent=6)

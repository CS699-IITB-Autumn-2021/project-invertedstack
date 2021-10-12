from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import json,time
import pprint
from tqdm import tqdm

class ICLR:
    def __init__(self):
        self.chrome_options = Options()
        self.chrome_options.add_argument("--headless") # Hides the browser window
        # Reference the local Chromedriver instance
        self.chrome_path = r'chromedriver'

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

    def get_conf_papers_from_category(self, category_url, year, category, limit=500):
        driver = webdriver.Chrome(executable_path=self.chrome_path, options=self.chrome_options)
        # Run the Webdriver, save page an quit browser
        driver.get(category_url)
        print(category_url)

        time.sleep(30)

        ele_available = False
        count=0
        while not ele_available:
            li_of_all_papers = driver.find_elements_by_css_selector("ul.submissions-list li.note")
            count+=1
            print("count -",count, len(li_of_all_papers))
            if(len(li_of_all_papers)>0):
                ele_available = True
                
        list_of_all_papers = []
        
        for element in tqdm(li_of_all_papers[:limit], "creating data"):
            details_of_curr_paper = {}
            #get data-id
            details_of_curr_paper["data_id"]= element.get_attribute("data-id")
            #get forum and pdf links
            links = element.find_elements_by_css_selector("h4 a")
            details_of_curr_paper["paper_title"] = links[0].get_attribute("innerHTML").strip()
            details_of_curr_paper["forum_link"] = links[0].get_attribute("href")
            if len(links) >1:
                details_of_curr_paper["pdf_link"] = links[1].get_attribute("href")
            else:
                details_of_curr_paper["pdf_link"] = ""
            #get all authors
            authors = []
            author_list = element.find_elements_by_css_selector("div.note-authors a.profile-link")
            for author in author_list:
                authors.append(author.get_attribute("innerHTML"))
            details_of_curr_paper["authors"] = authors
            #get meta-info
            meta_info = element.find_elements_by_css_selector("ul.note-content li")
            for ele in meta_info:
                key = ele.find_element_by_css_selector("strong.note-content-field").get_attribute("innerHTML").strip()
                value = ele.find_element_by_css_selector("span.note-content-value ").get_attribute("innerHTML").strip()
                details_of_curr_paper[key[:-1].lower().replace(' ','-')]=value
            list_of_all_papers.append(details_of_curr_paper)


        outputfile = "iclr_"+year+"_"+category.lower().replace("/"," ").replace(" ","_")+".json"
        print("creating file : ",outputfile)
        with open(outputfile,"w") as f:
            json.dump(list_of_all_papers,f,indent=6)
        print("File created!!")
        driver.quit()

        return list_of_all_papers
        

    def save_json_of_all_iclr(self,url,year,categories):
        for cat in categories:
            self.get_conf_papers_from_category(url,year,cat)
        


if __name__ == "__main__":
    home_url = "https://openreview.net/"


    OR = ICLR()
    #get all venues
    all_venues = OR.get_all_venues(home_url)

    #get list of all iclr entries in open review
    iclr_conf = OR.get_list_of_iclr_conf(all_venues['ICLR'])
    
    print(iclr_conf)

    #get conf name 
    for name in iclr_conf:
        if '2021' in name:
            iclr_2021_conf = iclr_conf[name]
    

    #get iclr conference link        
    iclr_conf_link = OR.get_iclr_conf_link(iclr_2021_conf)
    print("ICLR CONF LINK",iclr_conf_link)

    #get all categories from a iclr conference
    categories = OR.get_categories_from_iclr_conf(iclr_conf_link)

    #trying out retreiving iclr 2021 
    with open("iclr_2021_categories.json","w") as f:
        json.dump(categories,f,indent=6)

    print(categories)

    #saving all the paper details for 2020 2019 2018 in json format
    for name in iclr_conf:
        if any(year in name for year in ['2020','2019','2018']):
            categories = {}
            conf_link=""
            with open(name.lower().replace(" ","_")+"_cateogries.json","w") as f:
                conf_link = OR.get_iclr_conf_link(iclr_conf[name])
                print("getting categories from - ", conf_link)
                categories = OR.get_categories_from_iclr_conf(conf_link)
                json.dump(categories,f,indent=6)
            print("calling save_json_of_all_iclr ", name.strip()[-4:], " ", conf_link)
            pprint.pprint(categories)
            OR.save_json_of_all_iclr(conf_link, name.strip()[-4:], categories)
        

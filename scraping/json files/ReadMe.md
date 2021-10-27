## Structure of json files 

### Year wise categories of ICLR conferences are scraped and saved in - iclr_year_categories.json for the years 2021, 2020, 2019 and 2018
For instance the ICLR categories for the year 2021 in the file iclr_2021_categories.json - 

{

      "Oral Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#oral-presentations",
      
      "Spotlight Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#spotlight-presentations",
      
      "Poster Presentations": "https://openreview.net/group?id=ICLR.cc/2021/Conference#poster-presentations",
      
      "Withdrawn/Rejected Submissions": "https://openreview.net/group?id=ICLR.cc/2021/Conference#withdrawn-rejected-submissions"

}

### Json files are created for every ICLR conference per category 
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

## Hardware/Software Requirements.

### Hardware Requirements.
- Atleast 100MB of free storage space.

### Software Requirements.
- Android OS with minimum android version 6.0 (API level 23).
- (optional) PDF reader for viewing the PDF of the paper.

## How to operate?

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

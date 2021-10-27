## Hardware/Software Requirements.

### Hardware Requirements.
- Atleast 100MB of storage space.

### Software Requirements.
- Android OS.

## How to operate?

## Primary stakeholders of the product
- Research students.
- Professors.
- Anyone interested in reading Research Papers!

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

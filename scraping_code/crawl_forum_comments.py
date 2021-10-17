import requests,json

def get_all_comments_for_forum_id(forum_id,path):
    headers = {
        'authority': 'api.openreview.net',
        'access-control-allow-origin': '*',
        'accept': 'application/json, text/javascript, */*; q=0.01',
        'user-agent': 'Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1',
        'content-type': 'application/x-www-form-urlencoded; charset=UTF-8',
        'origin': 'https://openreview.net',
        'sec-fetch-site': 'same-site',
        'sec-fetch-mode': 'cors',
        'sec-fetch-dest': 'empty',
        'referer': 'https://openreview.net/',
        'accept-language': 'en-US,en;q=0.9,hi;q=0.8,ta;q=0.7',
        'cookie': '_ga=GA1.2.1468912180.1633190135; _gid=GA1.2.898326189.1634062697; _gat_gtag_UA_108703919_1=1',
    }

    params = (
        ('forum', forum_id),
        ('trash', 'true'),
        ('details', 'replyCount,writable,revisions,original,overwriting,invitation,tags'),
    )
    
    response = requests.get('https://api.openreview.net/notes', headers=headers, params=params)
    
    json_data = response.json()
    
    forum_id = params[0][1]

    children = {}
    def get_children(root,json_data):
        
        for comment in json_data:
            if 'replyto' in comment:
                if comment['replyto'] == root:
                    if root in children:
                        children[root].append(comment['id'])
                    else:
                        children[root] = [comment['id']]
    
        if root in children:
            for child in children[root]:
                get_children(child,json_data)
            
    def populate_json(root,json_data,comments):
        if root in children:
            for child in children[root]:
                details = {}
                details['id'] = child
                
                for comment in json_data:
                    if comment['id'] == child:
                        details['content'] = comment['content']
                        details['date'] = int(comment['cdate'])
                details['reply'] = []

                populate_json(child,json_data,details['reply'])
                comments.append(details)
        return comments
            
    get_children(forum_id,json_data['notes'])
    
    comments = populate_json(forum_id,json_data['notes'],[])
    dates = [ int(item['date']) for item in comments ]
    dates.sort(reverse = True)
    sorted_comments = []
    for date in dates:
        for item in comments:
            if item['date'] == date:
                sorted_comments.append(item)

    with open(path+forum_id+"_comments.json","w") as f:
            json.dump(sorted_comments, f, indent=4)


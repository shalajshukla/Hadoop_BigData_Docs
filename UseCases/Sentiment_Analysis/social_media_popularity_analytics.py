import json
import tweepy
from tweepy import OAuthHandler
from elasticsearch import Elasticsearch
import facebook
import json
import datetime
from config import *
from datetime import datetime

#facebook
access_token_fb = accesstokenfb
page_ids = pageids

#twitter
consumer_key = consumerkey
consumer_secret = consumerkeysecret
access_token_twitter = accesstokentwitter
access_token_secret = accesstokensecret

users=users
kibana_names = Kibananames

# create instance of elasticsearch
es = Elasticsearch(
    ['172.27.155.60', '172.27.155.97', '172.27.155.108'],
    # sniff before doing anything
    sniff_on_start=True,
    # refresh nodes after a node fails to respond
    sniff_on_connection_fail=True,
    # and also every 60 seconds
    sniffer_timeout=60
)

class FacebookDataScraper():

    def scrapeFacebookPageFeedStatus(self):
        graph = facebook.GraphAPI(access_token=access_token_fb)
        args = {'fields': 'id,name,fan_count,link,about,location'}
        print datetime.now()
        i=0
        for page_id in page_ids:
            page = graph.get_object(page_id,**args)
            print "\nThis is facebook data for page_id : " + page_id
            print page_id
            print page.get('name')
            print page.get('id')
            print page.get('fan_count')
            print page.get('link')
            print unicode(page.get('about'))
            print kibana_names[i]
		    
            if not page.get('location'):
                location=page.get('location')
            else:
                location = page.get('location')['city'] + ", " + page.get('location')['country']
            print location
	   
            if es.exists(index="social_media_logs",
                         doc_type="facebook",
                         id=page_id):
                fan_count_update={"doc": {"fan_count": page.get('fan_count'),
                                          "count": page.get('fan_count'),
										  "timestamp": datetime.now()}}
                es.update(index="social_media_logs",
                          doc_type="facebook",
				          id=page_id,
				          body=fan_count_update)
            else:					
                es.index(index="social_media_logs",
                         doc_type="facebook",
		      		     id=page_id,
                         body={"id": page.get('id'),
                               "name_on_facebook": page.get('name'),
                               "name": kibana_names[i],
                               "fan_count": page.get('fan_count'),
                               "link": page.get('link'),
                               "about": page.get('about'),
                               "location": location,
                               "timestamp": datetime.now(),
							   "social_site": 'facebook',
                               "count": page.get('fan_count')})
			
            i = i + 1				   
					  
class TwitterDataScrapper():
  
    def scrapeTwitterData(self): 
        auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
        auth.set_access_token(access_token_twitter, access_token_secret)
        api = tweepy.API(auth)
		
        i=0
        for user in users:
            user_data = api.get_user(user)
            print "\nThis is twitter data for user: " + user
            print user_data.screen_name
            print user_data.name
            print user_data.id
            print user_data.followers_count
            print user_data.url
            print user_data.description
            print user_data.location
            print kibana_names[i]
		
            if es.exists(index="social_media_logs",
                         doc_type="twitter",
                         id=user):
                followers_count_update={"doc": {"followers_count": user_data.followers_count,
                                                "count": user_data.followers_count,
												"timestamp": datetime.now()}}
                es.update(index="social_media_logs",
                          doc_type="twitter",
                          id=user,
                          body=followers_count_update)
            else:					
                es.index(index="social_media_logs",
                         doc_type="twitter",
                         id=user,
                         body={"id": user_data.id,
                               "name_on_twitter": user_data.name,
                               "name": kibana_names[i],
                               "followers_count": user_data.followers_count,
                               "link": user_data.url,
                               "description": user_data.description,
                               "location": user_data.location,
                               "timestamp": datetime.now(),
                               "social_site": 'twitter',
                               "count": user_data.followers_count})

            i = i + 1 
 
        #get trending topics and hashtags for the same   
        for woeid in woeids:
            data = api.trends_place(woeid)
            trends = data[0]['trends']
            #hashtags = []
            print "\nTop 25 trending hashtags " 
            for trend_data in trends:
                name = trend_data['name']
                if name.startswith('#'):
                    #hashtags.append(name)
                    print "\nHashtag = " + name
		 
 
if __name__ == '__main__':
    fb = FacebookDataScraper()
    fb.scrapeFacebookPageFeedStatus()
    twitter = TwitterDataScrapper()
    twitter.scrapeTwitterData()

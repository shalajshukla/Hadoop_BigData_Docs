import json
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
from textblob import TextBlob
from elasticsearch import Elasticsearch
import httplib
from config import *

consumer_key = consumerkey
consumer_secret = consumerkeysecret
access_token_twitter = accesstokentwitter
access_token_secret = accesstokensecret

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


class TweetStreamListener(StreamListener):
    # on success
    def on_data(self, data):
      # decode json
        dict_data = json.loads(data)

        # pass tweet into TextBlob
        if not dict_data["text"]:
            print "error in fetching twitter data!!"
        else:  
            tweet = TextBlob(dict_data["text"])

            # output sentiment polarity
            print tweet.sentiment.polarity

            # determine if sentiment is positive, negative, or neutral
            if tweet.sentiment.polarity < 0:
                sentiment = "negative"
            elif tweet.sentiment.polarity == 0:
                sentiment = "neutral"
            else:
                sentiment = "positive"
			
            for word in keywords:
                if word in dict_data['text'].lower():
                    keyword = word
                    break
                else:
                    keyword = ""
 
            # output sentiment
            print sentiment
            print keyword
		
            # add text and sentiment info to elasticsearch
            es.index(index="twitter_logs",
                     doc_type="tweets",
                     body={"author": dict_data["user"]["screen_name"],
					       "author_location": dict_data["user"]["location"],
                           "log_date": dict_data["created_at"],
					       "created_date": dict_data["user"]["created_at"],
                           "message": dict_data["text"],
                           "polarity": tweet.sentiment.polarity,
                           "subjectivity": tweet.sentiment.subjectivity,
                           "sentiment": sentiment,
					       "favorite_count": dict_data["favorite_count"],
					       "language": dict_data["lang"],
					       "coordinates": dict_data["coordinates"],
                           "keyword": keyword,
					       "entities": dict_data["entities"]})
        return True

    # on failure
    def on_error(self, status):
        print status

if __name__ == '__main__':

    # create instance of the tweepy tweet stream listener
    listener = TweetStreamListener()

    # set twitter keys/tokens
    auth = OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token_twitter, access_token_secret)

    # create instance of the tweepy stream
    stream = Stream(auth, listener)

    # search twitter for "Olympics, congress" keyword
    while True:
        try:
            stream.filter(track=keywords)
        except httplib.IncompleteRead as icread:
            continue
        except Exception as e:
            print e
            continue
        else:
            break	
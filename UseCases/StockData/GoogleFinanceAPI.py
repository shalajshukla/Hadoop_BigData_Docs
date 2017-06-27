import urllib.request as urlop
import json
import time

#http://digitalpbk.com/stock/google-finance-get-stock-quote-realtime
class GoogleFinanceAPI:
    def __init__(self):
        self.prefix = "http://finance.google.com/finance/info?client=ig&q="

    def get(self,symbol,exchange):
        url = self.prefix+"%s:%s"%(exchange,symbol)
        #print('url ',url)
        response = urlop.urlopen(url)
        content = response.read()
        body = content.decode('utf-8')
        #print("body " ,body)
        #print(" body 3 " , body[3:])
        #print(type(content))
        #print(type(body))
        #print("content :",content, " body :",body)
        #obj = json.loads(content[3:])
        obj = json.loads(body[3:])
        #obj = json.loads(content.decode('utf-8'))
        #obj = json.loads(u.readall().decode('utf-8'))
        #print(obj)
        return obj[0]

    def getQuote(self,quote):
        url = self.prefix+"%s"%(quote)
        print('url ',url)
        response = urlop.urlopen(url)
        content = response.read()
        body = content.decode('utf-8')
        #ignore first 3 charachter which is "// "
        obj = json.loads(body[3:])
        return obj

if __name__ == "__main__":
    print("run through script")
    c = GoogleFinanceAPI()

    while 1:
        #quote = c.get("MSFT","NASDAQ")
        #quote = c.get("RELIANCE","NSE")
        quote = c.getQuote("NSE:RELIANCE,NASDAQ:MSFT")
        #quote = map(lambda x: str.replace(x, "'", "\""), quote)
        #quote = quote.replace("'",'"')
        print(quote)
        time.sleep(30)
import http.client, urllib.parse

headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain", "X-Appengine-Cron":"true"}
conn = http.client.HTTPConnection("localhost", 8080)
conn.request("GET", "/deleteExpired", None, headers)
response = conn.getresponse()
print(response.status, response.reason)
data = response.read()
print(data.decode('UTF-8'))
conn.close()

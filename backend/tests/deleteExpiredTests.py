import http.client, urllib.parse

headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
conn = http.client.HTTPConnection("localhost", 8080)
conn.request("POST", "/deleteExpired", None, headers)
response = conn.getresponse()
print(response.status, response.reason)
data = response.read()
print(data.decode('UTF-8'))
conn.close()

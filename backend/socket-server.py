import subprocess
from bottle import run, post, request, response, get, route
import logging

logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)

@route('/<path>',method = 'POST')
def process(path):
    postdata = request.body.read()
    logging.debug("Server received request: "+path)
    try:
        return subprocess.check_output(['python3',path+'.py', postdata],shell=False)
    except subprocess.CalledProcessError as e:
        logging.debug("Server could not run function: "+path)

run(host='localhost', port=8080, debug=True, server='paste')

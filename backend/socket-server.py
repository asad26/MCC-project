import subprocess
from bottle import run, post, request, response, get, route
import logging
import glob, os

logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)

# Checking the available functions
functions = glob.glob("functions/*.py")

@route('/<path>',method = 'POST')
def process(path):
    function = 'functions/'+path+'.py'
    if function in functions:
        postdata = request.body.read()
        logging.debug("Server received request: "+path)
        try:
            logging.debug("Running "+function)
            return subprocess.check_output(['python3',function, postdata],shell=False)
        except subprocess.CalledProcessError as e:
            logging.debug("Server could not run function: "+path)
    else:
        return path+" not available"

run(host='localhost', port=8080, debug=True, server='paste')

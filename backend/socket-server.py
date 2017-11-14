import subprocess
from bottle import run, post, request, response, get, route

@route('/<path>',method = 'POST')
def process(path):
    for item in request.POST:
        argDict = item
    print("running "+str(path))
    try:
        return subprocess.check_output(['python3',path+'.py', argDict],shell=False)
    except subprocess.CalledProcessError as e:
        print("Could not run function: "+path)

run(host='localhost', port=8080, debug=True)

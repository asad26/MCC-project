import subprocess
from bottle import run, post, request, response, get, route
import logging
import glob, os
import sys
import importlib
import traceback

logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)

# Checking the available functions
function_folder = "functions"
function_suffix = ".py"
function_files = glob.glob(os.path.join(function_folder, "*" + function_suffix))

# a hack to make the absolute imports inside the functions work
# the imports could be made relative, but then
# calling the functions directly from command line wouldn't work
sys.path.append(os.path.abspath(function_folder))
#from deleteExpired import deleteGroup

functions = {}
for f in function_files:
    name = f[len(function_folder)+1:-len(function_suffix)]
    try:
        module = importlib.import_module(function_folder + "." + name)
        functions[name] = module.main
    except (KeyboardInterrupt, SystemExit):
        raise
    except:
        traceback.print_exc()
        print("Importing module", name, "failed")

print("Have functions:")
for name in functions:
    print(name)
print()

@route('/<path>',method = 'POST')
def process(path):
    function = path
    if function in functions:
        logging.debug("Server received request: "+path)
        try:
            ret = functions[function](request.forms)
        except (KeyboardInterrupt, SystemExit):
            raise
        except:
            traceback.print_exc()
            return "FAIL"
        if ret:
            return ret
        else:
            return "OK"
    else:
        return path+" not available"

run(host='0.0.0.0', port=8080, debug=True, server='paste')

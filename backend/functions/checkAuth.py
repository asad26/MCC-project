from config.config import config
import firebase_admin
from firebase_admin import credentials, auth
import logging



def user_is_authenticated(id_token):
    cred = credentials.Certificate(config["serviceAccount"])
    admin_app = firebase_admin.initialize_app(cred)

    logging.debug("Verifying authentication with id-token: "+id_token)
    decoded_token = auth.verify_id_token(id_token)
    uid = decoded_token['uid']
    logging.debug("User ID: "+str(uid))
    try:
        auth.get_user(uid)
        logging.debug("User authentication succeeded")
        return True, uid
    except (firebase_admin.auth.AuthError, ValueError) as e:
        logging.debug("User authentication failed")
        return False, None

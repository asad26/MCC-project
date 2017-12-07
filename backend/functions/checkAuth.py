from config.config import config
import firebase_admin
from firebase_admin import credentials, auth
import logging
import uuid


def user_is_authenticated(id_token):
    cred = credentials.Certificate(config["serviceAccount"])
    admin_app = firebase_admin.initialize_app(cred, name=str(uuid.uuid4()))

    logging.debug("Verifying authentication with id-token: "+id_token)
    decoded_token = auth.verify_id_token(id_token, app=admin_app)
    uid = decoded_token['uid']
    logging.debug("User ID: "+str(uid))
    try:
        auth.get_user(uid, app=admin_app)
        logging.debug("User authentication succeeded")
        return True, uid
    except (firebase_admin.auth.AuthError, ValueError) as e:
        logging.debug("User authentication failed")
        return False, None
    finally:
        firebase_admin.delete_app(admin_app)

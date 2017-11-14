from testconfig import config
import firebase_admin
from firebase_admin import credentials, auth



def user_is_authenticated(id_token):
    cred = credentials.Certificate(config["serviceAccount"])
    admin_app = firebase_admin.initialize_app(cred)
    
    print("Authenticating...")

    decoded_token = auth.verify_id_token(id_token)
    uid = decoded_token['uid']
    print(uid)
    try:
        auth.get_user(uid)
        print("User authentication succeeded")
        return True
    except (firebase_admin.auth.AuthError, ValueError) as e:
        return False

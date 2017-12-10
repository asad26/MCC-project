import pyrebase
from config.config import config
import logging
from checkAuth import user_is_authenticated

def main(kwargs_dict):
    logging.basicConfig(filename='logs/server.log',format='%(asctime)s %(message)s', level=logging.DEBUG)
    logging.debug("Running leave or delete group")
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    return joinGroup(db, **kwargs_dict)

def joinGroup(db, QRToken, userToken):
    authenticated, userID = user_is_authenticated(userToken)
    if not QRTokenIsLegitimate(db, QRToken):
        return "Incorrect QR token"
    QRList = QRToken.split('/')
    groupID = QRList[0]
    inviterID = QRList[1]
    if authenticated:
        if isInGroup(db, userID):
            return "Already in a group"
        else:
            return addToGroup(db, userID, groupID, inviterID)

def addToGroup(db, userID, groupID, inviterID):
    from createGroup import createQR, setUserTokenParams
    QR = createQR(groupID, userID)
    username = db.child("users").child(userID).child("userName").get().val()
    userData = {
        "name":username,
        "QR":QR
    }
    user = {
        userID:userData
    }
    logging.debug("Pushing user data to group members")
    db.child("groups").child(groupID).child("members").child(userID).set(userData)
    logging.debug("Pushing group id to user table")
    db.child("users").child(userID).update({"groupID":groupID})
    # set group id and expiry time in user token for Firebase Storage rules
    expiry = db.child("groups").child(groupID).child("expiry").get().val()
    setUserTokenParams(userID, groupID, expiry)
    createNewQRForInviter(db, groupID, inviterID)
    return QR

def createNewQRForInviter(db, groupID, inviterID):
    from createGroup import createQR
    qrtoken = createQR(groupID, inviterID)
    db.child("groups").child(groupID).child("members").child(inviterID).update({"QR":qrtoken})

def QRTokenIsLegitimate(db, QRToken):
    logging.debug("Checking provided QRToken is legit")
    QRList = QRToken.split('/')
    groupID = QRList[0]
    inviterID = QRList[1]
    QR = db.child("groups").child(groupID).child("members").child(inviterID).child("QR").get().val()
    return str(QR)==str(QRToken)

def isInGroup(db, userID):
    logging.debug("Checking if user is in some group")
    group = db.child("users").child(userID).child("groupID").get()
    logging.debug("usergroup: "+str(group.val()))
    return str(group.val())!='None'


if __name__ == "__main__":
    print(main(json.loads(sys.argv[1])))

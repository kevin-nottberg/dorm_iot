#Firebase Implementation

import pyrebase
import time

currentAlarm = ''
LED_STATUS = ''
currentLEDscript = ''

config = {
    "apiKey": "AIzaSyBagjPr_Gs6V_0C_PEC3AVdpwu28tZe2WM",
    "authDomain": "dorm-iot-project.firebaseapp.com",
    "databaseURL": "https://dorm-iot-project.firebaseio.com",
    "projectId": "dorm-iot-project",
    "storageBucket": "dorm-iot-project.appspot.com",
    "messagingSenderId": "645669478585"
}

firebase = pyrebase.initialize_app(config)

db = firebase.database()

def updateState():
    currentAlarm = db.child("dorm").child("alarm").get().val()
    LED_STATUS = db.child("dorm").child("status").get().val()

    currentLEDscript = db.child("dorm").child("status").get().val()

while True:
    updateState()

    currentAlarm = db.child("dorm").child("alarm").get().val()
    LED_STATUS = db.child("dorm").child("status").get().val()

    currentLEDscript = db.child("dorm").child("currentLEDScript").get().val()


    
    print("Alarm: " + currentAlarm)
    print("Status: " + LED_STATUS)
    print("Script Running: " + currentLEDscript)

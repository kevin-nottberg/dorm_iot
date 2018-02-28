#Firebase Implementation

import pyrebase
import time

currentAlarm = ''
LED_STATUS = ''
currentLEDscript = ''

config = {
    "apiKey": *Hidden*,
    "authDomain": *Hidden*,
    "databaseURL": *Hidden*,
    "projectId": *Hidden*,
    "storageBucket": *Hidden*,
    "messagingSenderId": *Hidden*
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

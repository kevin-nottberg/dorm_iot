import time
import datetime
import bibliopixel
import bibliopixel.drivers.SPI 
from bibliopixel import *

import pyrebase
import time

global currentAlarm
global LED_STATUS
global currentLEDscript

global currentColor 

hsvIncrement = ''

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

driver = bibliopixel.drivers.SPI.APA102.APA102(320)
led = bibliopixel.Strip(driver)
led.update()

inc = 0
#FlowingColor script
def flowingColorScript():
    global inc
    global currentColor
    
    newColor = colors.hsv2rgb_360((inc, 1.0, 1.0))
    currentColor = colors.hsv2rgb_360((inc, 1.0, 1.0))
    
    led.fill(newColor)
    led.update()

    inc = inc + 0.25
    if(inc > 359):
        inc = 0

    currentColor = newColor

def singleColorScript():
    global currentColor

    led.fill(currentColor)
    led.update()

def statusScript(status):
    if(LED_STATUS == 'on'):
        led.fillRGB(255, 0, 0)
        led.update()

    if(LED_STATUS == 'off'):
        led.fillRGB(0, 0, 0)
        led.update()

#Function is called every .75 of a second     
def updateStatus():
    global currentAlarm
    global LED_STATUS
    global currentLEDscript
    global currentColor

    #Getting Values
    currentAlarm = db.child("dorm").child("alarm").get().val()
    LED_STATUS = db.child("dorm").child("status").get().val()
    currentLEDscript = db.child("dorm").child("currentLEDScript").get().val()

    #Setting Values
    if(currentAlarm == time.strftime('%H:%M')):
        #Update firebase and let the program come and update LED_STATUS
        db.child("dorm").child("status").set('on')

    if(currentLEDscript == 'flowColor'):
        db.child("dorm").child("currentLEDColor").set(currentColor)

    if(currentLEDscript == 'singleColor'):
        currentColor = db.child("dorm").child("currentLEDColor").get().val()

#Initalizing
currentColor = [0, 0, 0]
updateStatus()
currentTime = int(round(time.time() * 1000))

while True:
    global currentAlarm
    global LED_STATUS
    global currentLEDscript

    global currentColor

    if(int(round(time.time() * 1000)) - 750 > currentTime):
        currentTime = int(round(time.time() * 1000))
        updateStatus()

    if(LED_STATUS == 'on'):
        if(currentLEDscript == 'flowColor'):
            flowingColorScript()
        if(currentLEDscript == 'singleColor'):
            singleColorScript()
        if(currentLEDscript == 'audioColor'):
            singleColorScript()

    if(LED_STATUS == 'off'):
        statusScript(LED_STATUS)        

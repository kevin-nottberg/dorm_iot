import time
import bibliopixel
import bibliopixel.drivers.SPI
import bibliopixel.colors as colors
from bibliopixel import *

driver = bibliopixel.drivers.SPI.LPD8806.LPD8806(320)
led = bibliopixel.Strip(driver)
led.update()

colorFlow = [colors.AliceBlue, colors.Aqua, colors.Blue, colors.BlueViolet, colors.CornflowerBlue, colors.Cyan]

i = 0;
while True:
    newColor = colors.hsv2rgb_360((i, 1.0, 1.0))

    led.fill(newColor)
    led.update()

    i = i + 1
    
    if(i > 359):
        i = 0

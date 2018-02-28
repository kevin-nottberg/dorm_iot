package com.dormiot.kevnot.dormiot;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KN125 on 10/22/2017.
 */

public class Dorm {

  public String alarm;
  public String status;
  public String currentLEDScript;
  public ArrayList<Integer> currentLEDColor;

  public Dorm() {}

  public Dorm(String alarm, Object currentLEDColor, String currentLEDScript, String status) {
    this.alarm = alarm;
    this.status = status;
    this.currentLEDColor = (ArrayList<Integer>) currentLEDColor;
    this.currentLEDScript = currentLEDScript;
  }
}

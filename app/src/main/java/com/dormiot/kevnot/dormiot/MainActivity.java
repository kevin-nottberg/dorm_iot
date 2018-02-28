package com.dormiot.kevnot.dormiot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dormiot.kevnot.dormiot.Dorm;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

  private FirebaseDatabase firebaseInstance;
  private DatabaseReference database;
  private DatabaseReference dormReference;

  private EditText alarm_hour;
  private EditText alarm_minute;

  private Button flowColor_scriptButton;
  private Button singleColor_scriptButton;
  private Button audioColor_scriptButton;
  private Button stripSlider_scriptButton;

  private RelativeLayout stripSliderView;

  private RelativeLayout colorPickerView;
  private RelativeLayout colorPickerPanel;

  private SeekBar redSeekBar, greenSeekBar, blueSeekBar;

  private TextView redValueText, greenValueText, blueValueText;

  //List of Alarms
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    firebaseInstance = FirebaseDatabase.getInstance();
    dormReference = firebaseInstance.getReference("dorm");

    //View decleration
    alarm_hour = (EditText) findViewById(R.id.hour_edit_text);
    alarm_minute = (EditText) findViewById(R.id.minute_edit_text);
    final ToggleButton LED_status_button = (ToggleButton) findViewById(R.id.led_status_button);

    flowColor_scriptButton = (Button) findViewById(R.id.flow_color_button);
    singleColor_scriptButton = (Button) findViewById(R.id.single_color_button);
    stripSlider_scriptButton = (Button) findViewById(R.id.stripSlider_color_button);

    audioColor_scriptButton = (Button) findViewById(R.id.audio_color_button);

    stripSliderView = (RelativeLayout) findViewById(R.id.strip_slider_view);

    colorPickerView = (RelativeLayout) findViewById(R.id.color_picker_view);
    colorPickerPanel = (RelativeLayout) findViewById(R.id.color_picker_panel);

    redSeekBar = (SeekBar) findViewById(R.id.red_seek_bar);
    greenSeekBar = (SeekBar) findViewById(R.id.green_seek_bar);
    blueSeekBar = (SeekBar) findViewById(R.id.blue_seek_bar);

    redValueText = (TextView) findViewById(R.id.red_value_text);
    greenValueText = (TextView) findViewById(R.id.green_value_text);
    blueValueText = (TextView) findViewById(R.id.blue_value_text);

    //redSeekBar.setMin(0);
    redSeekBar.setMax(255);

    //greenSeekBar.setMin(0);
    greenSeekBar.setMax(255);

    //blueSeekBar.setMin(0);
    blueSeekBar.setMax(255);

    setInitFields(dormReference, LED_status_button, alarm_hour, alarm_minute,
                        redSeekBar, greenSeekBar, blueSeekBar,
                        redValueText, greenValueText, blueValueText, colorPickerPanel);

    alarm_hour.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newAlarm = alarm_hour.getText().toString();
        newAlarm += ":" + alarm_minute.getText().toString();
        dormReference.child("alarm").setValue(newAlarm);
      }

      @Override
      public void afterTextChanged(Editable s) {}
    });

    alarm_minute.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newAlarm = alarm_hour.getText().toString();
        newAlarm += ":" + alarm_minute.getText().toString();
        dormReference.child("alarm").setValue(newAlarm);
      }

      @Override
      public void afterTextChanged(Editable s) {}
    });

    flowColor_scriptButton.setOnClickListener(new CompoundButton.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorPickerView.setVisibility(View.GONE);
        stripSliderView.setVisibility(View.GONE);
        dormReference.child("currentLEDScript").setValue("flowColor");
      }
    });

    singleColor_scriptButton.setOnClickListener(new CompoundButton.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorPickerView.setVisibility(View.VISIBLE);
        dormReference.child("currentLEDScript").setValue("singleColor");
      }
    });

    stripSlider_scriptButton.setOnClickListener(new CompoundButton.OnClickListener() {
      @Override
      public void onClick(View v) {
        stripSliderView.setVisibility(View.VISIBLE);
        colorPickerView.setVisibility(View.GONE);
        dormReference.child("currenLEDScript").setValue("stripSlider");
      }
    });

    audioColor_scriptButton.setOnClickListener(new CompoundButton.OnClickListener() {
      @Override
      public void onClick(View v) {
        colorPickerView.setVisibility(View.GONE);
        dormReference.child("currentLEDScript").setValue("audioColor");
      }
    });

    LED_status_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          //Change the value current LED_Status and push it
          //Change the text
          dormReference.child("status").setValue("on");
          Log.d("LED_STATUS", "Checked");
        } else {
          //Do the opposite
          Log.d("LED_STATUS", "Not Checked");
          dormReference.child("status").setValue("off");
        }
      }
    });

    stripSliderView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        int pixelWidth = (int) getViewWidth(stripSliderView) / 255;

        int currentPixel = (int) x / pixelWidth;

        dormReference.child("currentStripPosition").setValue(currentPixel);

        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return false;
      }
    });

    redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        redValueText.setText(Integer.toString(progress));
        //int redColor = Integer.parseInt((String) redValueText.getText());
        //int greenColor = Integer.parseInt((String) greenValueText.getText())
        colorPickerPanel.setBackgroundColor(Color.argb(255, progress,
                                              greenSeekBar.getProgress(), blueSeekBar.getProgress()));

        singleColor_scriptButton.setBackgroundColor(Color.argb(200, progress,
                                                      greenSeekBar.getProgress(), blueSeekBar.getProgress()));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        dormReference.child("currentLEDColor").child("1").setValue(seekBar.getProgress());
      }
    });

    greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        greenValueText.setText(Integer.toString(progress));

        colorPickerPanel.setBackgroundColor(Color.argb(255, redSeekBar.getProgress(),
                                                        progress, blueSeekBar.getProgress()));

        singleColor_scriptButton.setBackgroundColor(Color.argb(200, redSeekBar.getProgress(),
                                                                progress, blueSeekBar.getProgress()));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        dormReference.child("currentLEDColor").child("2").setValue(seekBar.getProgress());
      }
    });

    blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        blueValueText.setText(Integer.toString(progress));

        colorPickerPanel.setBackgroundColor(Color.argb(255, redSeekBar.getProgress(),
                                                        greenSeekBar.getProgress(), progress));

        singleColor_scriptButton.setBackgroundColor(Color.argb(200, redSeekBar.getProgress(),
                                                                greenSeekBar.getProgress(), progress));

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        dormReference.child("currentLEDColor").child("0").setValue(seekBar.getProgress());
      }
    });

    ValueEventListener dormListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Dorm dorm = dataSnapshot.getValue(Dorm.class);
        if(dorm.status.equals("on")) {
          LED_status_button.setChecked(true);
        } else if (dorm.status.equals("off")) {
          LED_status_button.setChecked(false);
        }
        int currRedValue = dorm.currentLEDColor.get(1);
        int currGreenValue = dorm.currentLEDColor.get(2);
        int currBlueValue = dorm.currentLEDColor.get(0);

        redSeekBar.setProgress(currRedValue);
        greenSeekBar.setProgress(currGreenValue);
        blueSeekBar.setProgress(currBlueValue);

        redValueText.setText(Integer.toString(dorm.currentLEDColor.get(1)));
        greenValueText.setText(Integer.toString(dorm.currentLEDColor.get(2)));
        blueValueText.setText(Integer.toString(dorm.currentLEDColor.get(0)));

        colorPickerPanel.setBackgroundColor(Color.argb(255, currRedValue, currGreenValue, currBlueValue));

        if(dorm.currentLEDScript == "flowColor") {
          flowColor_scriptButton.setBackgroundColor(Color.argb(200, dorm.currentLEDColor.get(1),
                                                                      dorm.currentLEDColor.get(2),
                                                                        dorm.currentLEDColor.get(0)));
        } else if(dorm.currentLEDScript == "singleColor") {
          singleColor_scriptButton.setBackgroundColor(Color.argb(200, dorm.currentLEDColor.get(1),
                                                                        dorm.currentLEDColor.get(2),
                                                                          dorm.currentLEDColor.get(0)));
        }

        if(dorm.currentLEDScript == "singleColor") {
          Log.d("LED", "visible");
          colorPickerView.setVisibility(View.VISIBLE);
        } else if(dorm.currentLEDScript == "flowColor" || dorm.currentLEDScript == "audioColor") {
          Log.d("LED", "notVisible");
          colorPickerView.setVisibility(View.GONE);
        }

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.w("DormIOT", "LoadDorm:onCancelled", databaseError.toException());
      }
    };
    dormReference.addValueEventListener(dormListener);
  }

  public void setInitFields(DatabaseReference reference, final ToggleButton button,
                            final EditText alarmEditHour, final EditText alarmEditMinute, final SeekBar redSeekBar,
                            final SeekBar greenSeekBar, final SeekBar blueSeekBar,
                            final TextView redValueText, final TextView greenValueText,
                            final TextView blueValueText, final RelativeLayout colorPickerPanel) {
    reference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Dorm singleDorm = dataSnapshot.getValue(Dorm.class);

        int hourStringIndex = singleDorm.alarm.indexOf(":");

        alarmEditHour.setText(singleDorm.alarm.substring(0, hourStringIndex), TextView.BufferType.EDITABLE);
        alarmEditMinute.setText(singleDorm.alarm.substring(hourStringIndex + 1, singleDorm.alarm.length()), TextView.BufferType.EDITABLE);

        if (singleDorm.status.equals("on")) {
          button.setChecked(true);
        } else if (singleDorm.status.equals("off")) {
          button.setChecked(false);
        }

        int currRedValue = singleDorm.currentLEDColor.get(1);
        int currGreenValue = singleDorm.currentLEDColor.get(2);
        int currBlueValue = singleDorm.currentLEDColor.get(0);

        redSeekBar.setProgress(currRedValue);
        greenSeekBar.setProgress(currGreenValue);
        blueSeekBar.setProgress(currBlueValue);

        redValueText.setText(Integer.toString(singleDorm.currentLEDColor.get(1)));
        greenValueText.setText(Integer.toString(singleDorm.currentLEDColor.get(2)));
        blueValueText.setText(Integer.toString(singleDorm.currentLEDColor.get(0)));

        colorPickerPanel.setBackgroundColor(Color.argb(255, currRedValue, currGreenValue, currBlueValue));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  public int getViewWidth(View v) {
    return v.getWidth();
  }

  public int getViewHeight(View v) {
    return v.getHeight();
  }
}



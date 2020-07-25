package com.example.telemetry_traces;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private TextView TV1;
    private TextView TV2;
    private TextView Timer;
    private TextView X,Y,Z;
    private long TimeStart = 0;
    private Handler TimeHandler = new Handler();
    private Runnable run;
    private SensorManager SM;
    private Date Time1, Time2;
    private String TimeNow;
    private float Difference;
    private int SecondsValue;

    static Map<Integer,String> TimeRepresentedBySeconds = new HashMap<>(); // This HashMap will contain data like {(0=01:10:10),(1=01:10:11),(2=01:10:12),(3=01:10:13),(4=01:10:14)}. This will remain same for all sensors.
    static Map<String, ArrayList<Float>> TimeData1 = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data for scalar sensors.
    static Map<String, ArrayList<Float>> TimeData2X = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data of X-Axis for vector sensors.
    static Map<String, ArrayList<Float>> TimeData2Y = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data of Y-Axis for vector sensors.
    static Map<String, ArrayList<Float>> TimeData2Z = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data of Z-Axis for vector sensors.

    static Spinner SensorType, ScalarSensor, VectorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button StartStopButton = findViewById(R.id.StartStopButton);
        StartStopButton.setText("START");
        Timer = findViewById(R.id.TimerTextView);
        SensorType = (Spinner) findViewById(R.id.SensorType);
        ScalarSensor = (Spinner) findViewById(R.id.ScalarSensor);
        VectorSensor = (Spinner) findViewById(R.id.VectorSensor);
        TV1 = (TextView) findViewById(R.id.SensorType_TV);
        TV2 = (TextView) findViewById(R.id.TextView2);
        final Sensor[] NowActive = {null};
        final SensorEventListener[] SEL1 = new SensorEventListener[1];
        final SensorEventListener[] SEL2 = new SensorEventListener[1];
        final SensorEventListener[] SEL3 = new SensorEventListener[1];
        final SensorEventListener[] SEL4 = new SensorEventListener[1];
        ScalarSensor.setVisibility(View.INVISIBLE); // Initially Scalar sensor spinner will be disabled. This will be displayed once sensor type will be selected.
        VectorSensor.setVisibility(View.INVISIBLE); // Initially Vector sensor spinner will be disabled. This will be displayed once sensor type will be selected.
        TV2.setVisibility(View.INVISIBLE); // Text view will be disabled. This will show text against appropriate spinner of selecting sensor.
        SensorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position != 0)
                {
                    if (position == 1)
                    {
                        TV2.setVisibility(View.VISIBLE);
                        ScalarSensor.setVisibility(View.VISIBLE);
                        VectorSensor.setVisibility(View.INVISIBLE);
                    }
                    else if (position == 2)
                    {
                        TV2.setVisibility(View.VISIBLE);
                        ScalarSensor.setVisibility(View.INVISIBLE);
                        VectorSensor.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    TV2.setVisibility(View.INVISIBLE);
                    ScalarSensor.setVisibility(View.INVISIBLE);
                    VectorSensor.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });

        StartStopButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                int SpinnerStatus = CheckSpinners(SensorType.getSelectedItemPosition(), ScalarSensor.getSelectedItemPosition(), VectorSensor.getSelectedItemPosition());
                if(SpinnerStatus != 0)
                {
                    Button button = (Button) v;
                    if (button.getText().equals("START"))
                    {
                        Toast.makeText(MainActivity.this, "Trace has been started.", Toast.LENGTH_LONG).show();
                        TimeStart = System.currentTimeMillis();
                        TimeHandler.postDelayed(run, 0);
                        Timer.setText("00:00:00");
                        button.setText("STOP");
                        SM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                        SensorType.setEnabled(false);

                        // Below code will run when user selects Light sensor from front end.
                        if(SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 1)
                        {
                            ScalarSensor.setEnabled(false);
                            TimeRepresentedBySeconds.clear();
                            TimeData1.clear();
                            try
                            {
                                SimpleDateFormat SDF = new SimpleDateFormat("hh:mm:ss"); // Making format I need for time.
                                TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Take current system time.
                                Time1=SDF.parse(TimeNow); // Converting time format as required.
                            }
                            catch(Exception e) // Catching exception if any occurs.
                            {
                                e.printStackTrace(); // Printing occurred exception or error.
                            }
                            SM.registerListener(SEL1[0] = new SensorEventListener()
                            {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onSensorChanged(SensorEvent event)
                                {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_LIGHT); // Checking active sensor status.
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("New value for light sensor : " + event.values[0]);
                                    if (Float.compare(event.values[0],600)<=0) // Limiting light sensor values to 600 LUX.
                                    {
                                        // I am keeping below line first to avoid as much delay as I can in getting time with sensor value.
                                        TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Getting system time again.
                                        // In below statement, I am passing 1 to indicate Scalar Sensor, passing light sensor value, 0.0f & 0.0f as there are no y-axis and z-axis values here.
                                        FillDataInDataStructures(1,event.values[0],0.0f,0.0f);
                                    }
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy){}
                            }, SM.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL); // Keeping Sensor delay to SENSOR_DELAY_NORMAL which delays values for 2 MilliSeconds.
                        }

                        // Below code will run when user selects Proximity sensor from front end.
                        else if(SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 2)
                        {
                            ScalarSensor.setEnabled(false);
                            TimeRepresentedBySeconds.clear();
                            TimeData1.clear();
                            try
                            {
                                SimpleDateFormat SDF = new SimpleDateFormat("hh:mm:ss"); // Making format I need for time.
                                TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Take current system time.
                                Time1=SDF.parse(TimeNow); // Converting time format as required.
                            }
                            catch(Exception e) // Catching exception if any occurs.
                            {
                                e.printStackTrace(); // Printing occurred exception or error.
                            }
                            SM.registerListener(SEL2[0] = new SensorEventListener() // Adding a sensor listener for the proximity sensor
                            {
                                @Override
                                public void onSensorChanged(SensorEvent event)
                                {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_PROXIMITY);
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("New value for proximity sensor : " + event.values[0]);
                                    if (Float.compare(event.values[0],20)<=0) // Limiting proximity sensor values to 20 Cms.
                                    {
                                        // I am keeping below line first to avoid as much delay as I can in getting time with sensor value.
                                        TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Getting system time again.
                                        // In below statement, I am passing 1 to indicate Scalar Sensor, passing proximity sensor value, 0.0f & 0.0f as there are no y-axis and z-axis values here.
                                        FillDataInDataStructures(1,event.values[0],0.0f,0.0f);
                                    }
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy){}
                            },  SM.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_UI);
                        }

                        // Below code will run when user selects Gyroscope sensor from front end.
                        if(SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 1)
                        {
                            VectorSensor.setEnabled(false);
                            TimeRepresentedBySeconds.clear();
                            TimeData2X.clear();
                            TimeData2Y.clear();
                            TimeData2Z.clear();
                            try
                            {
                                SimpleDateFormat SDF = new SimpleDateFormat("hh:mm:ss"); // Making format I need for time.
                                TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Take current system time.
                                Time1=SDF.parse(TimeNow); // Converting time format as required.
                            }
                            catch(Exception e) // Catching exception if any occurs.
                            {
                                e.printStackTrace(); // Printing occurred exception or error.
                            }
                            SM.registerListener(SEL3[0] = new SensorEventListener() // Adding a sensor listener for the gyroscope sensor.
                            {
                                @Override
                                public void onSensorChanged(SensorEvent event)
                                {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_GYROSCOPE);
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("New value for gyroscope sensor X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
                                    // In below if condition, I am limiting gyroscope sensor values to 1G positive or negative.
                                    if((Float.compare(event.values[0],-2)>0 && Float.compare(event.values[0],2)<0) && (Float.compare(event.values[1],-2)>0 && Float.compare(event.values[1],2)<0) && (Float.compare(event.values[2],-2)>0 && Float.compare(event.values[2],2)<0))
                                    {
                                        // I am keeping below line first to avoid as much delay as I can in getting time with sensor value.
                                        TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Getting system time again.
                                        // In below statement, I am passing 2 to indicate Vector Sensor, passing sensor's x-axis value, passing sensor's y-axis value &  finally passing sensor's z-axis value.
                                        FillDataInDataStructures(2,event.values[0],event.values[1],event.values[2]);
                                    }
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy){}
                            }, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
                        }

                        // Below code will run when user selects Linear Acceleration from front end.
                        else if(SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 2)
                        {
                            VectorSensor.setEnabled(false);
                            TimeRepresentedBySeconds.clear();
                            TimeData2X.clear();
                            TimeData2Y.clear();
                            TimeData2Z.clear();
                            try
                            {
                                SimpleDateFormat SDF = new SimpleDateFormat("hh:mm:ss"); // Making format I need for time.
                                TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Take current system time.
                                Time1=SDF.parse(TimeNow); // Converting time format as required.
                            }
                            catch(Exception e) // Catching exception if any occurs.
                            {
                                e.printStackTrace(); // Printing occurred exception or error.
                            }
                            SM.registerListener(SEL4[0] = new SensorEventListener() // Adding a sensor listener for the Linear Acceleration sensor.
                            {
                                @Override
                                public void onSensorChanged(SensorEvent event)
                                {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("New value for linear acceleration (MS-2) X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
                                    // In below if condition, I am limiting linear acceleration sensor values to 2 rads/s positive or negative.
                                    if((Float.compare(event.values[0],-1)>0 && Float.compare(event.values[0],1)<0) && (Float.compare(event.values[1],-1)>0 && Float.compare(event.values[1],1)<0) && (Float.compare(event.values[2],-1)>0 && Float.compare(event.values[2],1)<0))
                                    {
                                        // I am keeping below line first to avoid as much delay as I can in getting time with sensor value.
                                        TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Getting system time again.
                                        // In below statement, I am passing 2 to indicate Vector Sensor, passing sensor's x-axis value, passing sensor's y-axis value &  finally passing sensor's z-axis value.
                                        FillDataInDataStructures(2,event.values[0],event.values[1],event.values[2]);
                                    }
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy){}
                            },  SM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
                        }
                    }
                    else if(button.getText().equals("STOP"))
                    {
                        TimeHandler.removeCallbacks(run);
                        button.setText("START");
                        Timer.setText("Timer : 00:00:00");
                        X = findViewById(R.id.XValue);
                        Y = findViewById(R.id.YValue);
                        Z = findViewById(R.id.ZValue);
                        X.setText("X Value");
                        X.setBackgroundColor(Color.TRANSPARENT);
                        X.setTextColor(Color.BLACK);
                        Y.setText("Y Value");
                        Y.setBackgroundColor(Color.TRANSPARENT);
                        Y.setTextColor(Color.BLACK);
                        Z.setText("Z Value");
                        Z.setBackgroundColor(Color.TRANSPARENT);
                        Z.setTextColor(Color.BLACK);
                        SensorType.setEnabled(true);
                        if(SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 1)
                        {
                            SM.unregisterListener(SEL1[0], NowActive[0]);
                            ScalarSensor.setEnabled(true);
                            ScalarSensor.setSelection(0);
                        }
                        else if (SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 2)
                        {
                            SM.unregisterListener(SEL2[0], NowActive[0]);
                            ScalarSensor.setEnabled(true);
                            ScalarSensor.setSelection(0);
                        }
                        else if(SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 1)
                        {
                            SM.unregisterListener(SEL3[0], NowActive[0]);
                            VectorSensor.setEnabled(true);
                            VectorSensor.setSelection(0);
                        }
                        else if (SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 2)
                        {
                            SM.unregisterListener(SEL4[0], NowActive[0]);
                            VectorSensor.setEnabled(true);
                            VectorSensor.setSelection(0);
                        }
                        SensorType.setSelection(0);
                        TimeRepresentedBySeconds.clear();
                        TimeData1.clear();
                        TimeData2X.clear();
                        TimeData2Y.clear();
                        TimeData2Z.clear();
                        Toast.makeText(MainActivity.this, "Tracing has been stopped.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please select appropriate sensors to start the trace.", Toast.LENGTH_LONG).show();
                }
            }
        });
        run = new Runnable()
        {
            @Override
            public void run()
            {
                long MilliSeconds = System.currentTimeMillis() - TimeStart; // Taking time in milliseconds and subtracting it from time start.
                int Seconds = (int)(MilliSeconds/1000); // Defining milliseconds to seconds.
                int Minutes = Seconds/60; // Defining seconds to minutes.
                Seconds = Seconds % 60; // Converting to seconds.
                int Hours = Minutes/60; // Defining minutes to hours.
                Minutes = Minutes % 60; // Converting to hours.
                Timer.setText(String.format("Timer : "+"%02d:%02d:%02d",Hours,Minutes,Seconds)); // Updating text of timer.
                TimeHandler.postDelayed(this,500); // Delaying update in time by 0.5 seconds.
            }
        };
    }

    private int CheckSpinners(int SensorType, int ScalarSensor, int VectorSensor) // This function is used to check if all the spinners have appropriate values selected before starting the trace.
    {
        if(SensorType != 0) // If Sensor Type spinner is not set to "Select", do as below.
        {
            if(ScalarSensor != 0 || VectorSensor != 0) // If Scalar Sensor or Vector Sensor spinner is not set to "Select", do as below.
                return 1; // If everything is fine, return 1.
            else // If Scalar or Vector Sensor spinner is set to "Select", do as below.
                return 0; // If appropriate spinner is not selected, return 0.
        }
        else // If appropriate sensor type is not selected, do as below.
            return 0; // Return 0.
    }

    // Below function is responsible to fill in all required data in global variables of sensors.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void FillDataInDataStructures(int SensorType, Float SensorXValue, Float SensorYValue, Float SensorZValue) {
        try // Trying to catch exception.
        {
            SensorXValue = Float.parseFloat(String.format("%.3f",SensorXValue)); // Restricting values to 3 digits after decimal.
            SensorYValue = Float.parseFloat(String.format("%.3f",SensorYValue)); // Restricting values to 3 digits after decimal.
            SensorZValue = Float.parseFloat(String.format("%.3f",SensorZValue)); // Restricting values to 3 digits after decimal.
            X = findViewById(R.id.XValue); // Displaying values on text view for x-axis.
            Y = findViewById(R.id.YValue); // Displaying values on text view for x-axis.
            Z = findViewById(R.id.ZValue); // Displaying values on text view for x-axis.
            if(SensorType == 1) // If sensor type selected is scalar sensor, do as below.
            {
                Y.setText("Y : " + SensorXValue); // Display only y-axis values as there are no z-axis values and x-axis is time. There is timer to display time and hence, no need of displaying time on x-axis position.
            }
            else if(SensorType == 2) // If sensor type selected is vector sensor, do as below.
            {
                X.setText("X : " + SensorXValue); // Display x-axis values in text view.
                X.setBackgroundColor(Color.parseColor("#B90E0A")); // Defining background color.
                X.setTextColor(Color.parseColor("#FFFFFF")); // Defining font color.
                Y.setText("Y : " + SensorYValue); // Display y-axis values in text view.
                Y.setBackgroundColor(Color.parseColor("#000000")); // Defining background color.
                Y.setTextColor(Color.parseColor("#FFFFFF")); // Defining font color.
                Z.setText("Z : " + SensorZValue); // Display z-axis values in text view.
                Z.setBackgroundColor(Color.parseColor("#03C04A")); // Defining background color.
            }
            SimpleDateFormat SDF = new SimpleDateFormat("hh:mm:ss"); // Making format I need for time.
            Time2 = SDF.parse(TimeNow); // Converting time into required format.
            Difference = (((Time2.getTime() - Time1.getTime()) / 1000) % 24) % 5; // Calculating time difference.
            SecondsValue = Math.round(Difference); // Converting difference value in integer.
            if (!TimeRepresentedBySeconds.containsValue(TimeNow)) // If there are no value for given time, do as below.
            {
                String Temp = TimeRepresentedBySeconds.get(SecondsValue); // Fetching data of Key = SecondsValue from HashMap.
                if (SensorType == 1) // If sensor is a scalar sensor.
                {
                    if (TimeData1.containsKey(Temp)) // If HashMap (TimeData) contains any value of time stored in above Key = SecondsValue, do as below.
                    {
                        TimeData1.remove(Temp); // Remove all entries for above if condition.
                    }
                    TimeRepresentedBySeconds.put(SecondsValue, TimeNow); // Add new entry in HashMap for given time.
                    ArrayList<Float> SensorData = new ArrayList<Float>(); // This will contain sensor data.
                    SensorData.add(SensorXValue); // Add new entry in ArrayList for Sensor value.
                    TimeData1.put(TimeNow, SensorData); // Add new entry in HashMap for time and sensor combination.
                } else if (SensorType == 2) // If sensor is a vector sensor.
                {
                    if (TimeData2X.containsKey(Temp) && TimeData2Y.containsKey(Temp) && TimeData2Z.containsKey(Temp)) // If HashMap (TimeData) contains any value of time stored in above Key = SecondsValue, do as below.
                    {
                        TimeData2X.remove(Temp); // Remove all entries from sensor x-axis array for above if condition.
                        TimeData2Y.remove(Temp); // Remove all entries from sensor y-axis array for above if condition.
                        TimeData2Z.remove(Temp); // Remove all entries from sensor z-axis array for above if condition.
                    }
                    TimeRepresentedBySeconds.put(SecondsValue, TimeNow); // Add new entry in HashMap for given time.
                    ArrayList<Float> SensorData1 = new ArrayList<Float>(); // This will contain sensor data for x-axis.
                    ArrayList<Float> SensorData2 = new ArrayList<Float>(); // This will contain sensor data for y-axis.
                    ArrayList<Float> SensorData3 = new ArrayList<Float>(); // This will contain sensor data for z-axis.
                    SensorData1.add(SensorXValue); // Add new entry in ArrayList of x-axis for sensor value.
                    SensorData2.add(SensorYValue); // Add new entry in ArrayList of y-axis for sensor value.
                    SensorData3.add(SensorZValue); // Add new entry in ArrayList of z-axis for sensor value.
                    TimeData2X.put(TimeNow, SensorData1); // Add new entry in HashMap for time and sensor x-axis combination.
                    TimeData2Y.put(TimeNow, SensorData2); // Add new entry in HashMap for time and sensor y-axis combination.
                    TimeData2Z.put(TimeNow, SensorData3); // Add new entry in HashMap for time and sensor z-axis combination.
                }
            }
            else if (TimeRepresentedBySeconds.containsValue(TimeNow)) // If there are values for given time in HashMap, do as below.
            {
                if (SensorType == 1) // If sensor is a scalar sensor.
                {
                    ArrayList<Float> SensorData = TimeData1.get(TimeNow); // Fetching already created list from ArrayList.
                    SensorData.add(SensorXValue); // Add new entry in ArrayList for Sensor value.
                    TimeData1.put(TimeNow, SensorData); // Update new entry in HashMap for time and sensor combination.
                } else if (SensorType == 2) // If sensor is a vector sensor.
                {
                    ArrayList<Float> SensorData1 = TimeData2X.get(TimeNow); // Fetching already created list from ArrayList for x-axis.
                    ArrayList<Float> SensorData2 = TimeData2Y.get(TimeNow); // Fetching already created list from ArrayList for y-axis.
                    ArrayList<Float> SensorData3 = TimeData2Z.get(TimeNow); // Fetching already created list from ArrayList for z-axis.
                    SensorData1.add(SensorXValue); // Add new entry in ArrayList of x-axis for sensor value.
                    SensorData2.add(SensorYValue); // Add new entry in ArrayList of y-axis for sensor value.
                    SensorData3.add(SensorZValue); // Add new entry in ArrayList of z-axis for sensor value.
                    TimeData2X.put(TimeNow, SensorData1); // Add new entry in HashMap for time and sensor x-axis combination.
                    TimeData2Y.put(TimeNow, SensorData2); // Add new entry in HashMap for time and sensor y-axis combination.
                    TimeData2Z.put(TimeNow, SensorData3); // Add new entry in HashMap for time and sensor z-axis combination.
                }
            }
            System.out.println("TimeRepresentedBySeconds : " + TimeRepresentedBySeconds.entrySet());
            if (SensorType == 1) // If sensor is a scalar sensor.
            {
                System.out.println("TimeData1 (Light or Proximity Sensor Data) : " + TimeData1.entrySet());
            } else if (SensorType == 2) // If sensor is a vector sensor.
            {
                System.out.println("X-Axis (Gyroscope or Linear Acceleration Sensor Data) : " + TimeData2X.entrySet());
                System.out.println("Y-Axis (Gyroscope or Linear Acceleration Sensor Data) : " + TimeData2Y.entrySet());
                System.out.println("Z-Axis (Gyroscope or Linear Acceleration Sensor Data) : " + TimeData2Z.entrySet());
            }
        } catch (Exception e) // Catching exception if any occurs.
        {
            e.printStackTrace(); // Printing occurred exception or error.
        }
    }
}
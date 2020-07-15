package com.example.telemetry_traces;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    private TextView TV2;
    private TextView Timer;
    private long TimeStart = 0;
    private Handler TimeHandler = new Handler();
    private Runnable run;
    private SensorManager SM;
    private Date Time1, Time2;
    private String TimeNow;
    private float Difference;
    private int SecondsValue;

    static Map<Integer,String> TimeRepresentedBySeconds = new HashMap<>(); // This HashMap will contain data like {(0=01:10:10),(1=01:10:11),(2=01:10:12),(3=01:10:13),(4=01:10:14)}. This will remain same for all sensors.
    static Map<String, ArrayList<Float>> TimeData = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data like {(01:10:11=[124,200,430,590]),(01:10:12=[546,287,139,234]) & so on}

    static Spinner SensorType, ScalarSensor, VectorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timer = findViewById(R.id.TimerTextView);
        Button StartStopButton = findViewById(R.id.StartStopButton);

        SensorType = (Spinner) findViewById(R.id.SensorType);
        ScalarSensor = (Spinner) findViewById(R.id.ScalarSensor);
        VectorSensor = (Spinner) findViewById(R.id.VectorSensor);
        TV2 = (TextView) findViewById(R.id.TextView2);
        final Sensor[] NowActive = {null};
        final SensorEventListener[] SEL1 = new SensorEventListener[1];
        final SensorEventListener[] SEL2 = new SensorEventListener[1];
        final SensorEventListener[] SEL3 = new SensorEventListener[1];
        final SensorEventListener[] SEL4 = new SensorEventListener[1];

        StartStopButton.setText("START");
        ScalarSensor.setVisibility(View.INVISIBLE);
        VectorSensor.setVisibility(View.INVISIBLE);
        TV2.setVisibility(View.INVISIBLE);

        SensorType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if(position != 0)
                {
                    if(position == 1)
                    {
                        TV2.setVisibility(View.VISIBLE);
                        ScalarSensor.setVisibility(View.VISIBLE);
                        VectorSensor.setVisibility(View.INVISIBLE);
                    }
                    else if(position == 2)
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
            public void onNothingSelected(AdapterView<?> parent) {

            }
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

                        // Below code will run when user selects Light sensor from front end.
                        if(SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 1)
                        {
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
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_LIGHT); // Creating Sensor Active Status.
                                    NowActive[0] = ActiveSensor.get(0);
                                    if (event.values[0] <= 600) // Limiting light sensor values to 600 LUX.
                                    {
                                        // I am keeping below line first to avoid as much delay as I can in getting time with sensor value.
                                        TimeNow = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()); // Getting system time again.
                                        try // Trying to catch exception.
                                        {
                                            SimpleDateFormat SDF = new SimpleDateFormat("hh:mm:ss"); // Making format I need for time.
                                            Time2=SDF.parse(TimeNow); // Converting time into required format.
                                            Difference = (((Time2.getTime() - Time1.getTime())/1000)%24)%5; // Calculating time difference.
                                            // System.out.println("Difference : "+Difference);
                                            // System.out.println("Light Sensor: " + event.values[0] + " LUX");
                                            SecondsValue = Math.round(Difference); // Converting difference value in integer.
                                            if(!TimeRepresentedBySeconds.containsValue(TimeNow)) // If there are no value for given time, do as below.
                                            {
                                                String Mid = TimeRepresentedBySeconds.get(SecondsValue); // Fetching data of Key = SecondsValue from HashMap.
                                                if(TimeData.containsKey(Mid)) // If HashMap (TimeData) contains any value of time stored in above Key = SecondsValue, do as below.
                                                {
                                                    TimeData.remove(Mid); // Remove all entries for above if condition.
                                                }
                                                TimeRepresentedBySeconds.put(SecondsValue,TimeNow); // Add new entry in HashMap for given time.
                                                ArrayList<Float> LightSensorData = new ArrayList<Float>(); // This will contain light sensor data.
                                                LightSensorData.add(event.values[0]); // Add new entry in ArrayList for Light Sensor value.
                                                TimeData.put(TimeNow,LightSensorData); // Add new entry in HashMap for time and light sensor combination.
                                            }
                                            else // If there are values for given time in HashMap, do as below.
                                            {
                                                ArrayList<Float> LightSensorData = TimeData.get(TimeNow); // Fetching already created list from HashMap.
                                                LightSensorData.add(event.values[0]); // Add new entry in ArrayList for Light Sensor value.
                                                TimeData.put(TimeNow,LightSensorData); // Update new entry in HashMap for time and light sensor combination.
                                            }
                                            // System.out.println("TimeRepresentedBySeconds : "+TimeRepresentedBySeconds.entrySet());
                                            // System.out.println("TimeData : "+TimeData.entrySet());
                                        }
                                        catch(Exception e) // Catching exception if any occurs.
                                        {
                                            e.printStackTrace(); // Printing occurred exception or error.
                                        }
                                    }
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy){}
                            }, SM.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL); // Keeping Sensor delay to SENSOR_DELAY_NORMAL which delays values for 2 MilliSeconds.
                        }

                        // Below code will run when user selects Proximity sensor from front end.
                        else if(SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 2)
                        {
                            // add in a sensor listener for the proximity sensor
                            SM.registerListener(SEL2[0] = new SensorEventListener() {
                                @Override
                                public void onSensorChanged(SensorEvent event) {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_PROXIMITY);
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("Proximity Sensor: " + event.values[0] + "cm");
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy){}
                            },  SM.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_UI);
                        }

                        // Vector Sensor
                        if(SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 1)
                        {
                            // add in a sensor listener for the gyroscope
                            SM.registerListener(SEL3[0] = new SensorEventListener() {
                                @Override
                                public void onSensorChanged(SensorEvent event)
                                {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_GYROSCOPE);
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("gyroscope (rad/s) \n x: " + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2]);
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                                }
                            }, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
                        }
                        // Below code will run when user selects Linear Acceleration from front end.
                        else if(SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 2)
                        {
                            // add in a sensor listener for the Linear Acceleration
                            SM.registerListener(SEL4[0] = new SensorEventListener() {
                                @Override
                                public void onSensorChanged(SensorEvent event) {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("linear acceleration (ms-2) \n x: " + event.values[0] + " y: " + event.values[1] + " z: " + event.values[2]);

                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                                }
                            },  SM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
                        }
                    }
                    else if(button.getText().equals("STOP"))
                    {
                        TimeHandler.removeCallbacks(run);
                        button.setText("START");
                        Timer.setText("Timer : 00:00:00");
                        if(SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 1)
                        {
                            SM.unregisterListener(SEL1[0], NowActive[0]);
                            ScalarSensor.setSelection(0);
                        }
                        else if (SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 2)
                        {
                            SM.unregisterListener(SEL2[0], NowActive[0]);
                            ScalarSensor.setSelection(0);
                        }
                        else if(SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 1)
                        {
                            SM.unregisterListener(SEL3[0],NowActive[0]);
                            VectorSensor.setSelection(0);
                        }
                        else if (SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 2)
                        {
                            SM.unregisterListener(SEL4[0],NowActive[0]);
                            VectorSensor.setSelection(0);
                        }
                        SensorType.setSelection(0);
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
                long MilliSeconds = System.currentTimeMillis() - TimeStart;
                int Seconds = (int)(MilliSeconds/1000);
                int Minutes = Seconds/60;
                Seconds = Seconds % 60;
                int Hours = Minutes/60;
                Minutes = Minutes % 60;
                Timer.setText(String.format("Timer : "+"%02d:%02d:%02d",Hours,Minutes,Seconds));
                TimeHandler.postDelayed(this,500);
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
}
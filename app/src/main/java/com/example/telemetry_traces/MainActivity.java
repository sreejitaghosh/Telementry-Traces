package com.example.telemetry_traces;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
{
    private Spinner SensorType, ScalarSensor, VectorSensor;
    private TextView TV2;
    private TextView Timer;
    private long TimeStart = 0;
    Handler TimeHandler = new Handler();
    Runnable run;
    private TextView light_sm, proximity_sm;
    private SensorManager SM;


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
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
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
                            // add in a sensor listener for the light sensor
                            SM.registerListener(SEL1[0] = new SensorEventListener() {
                                @Override
                                public void onSensorChanged(SensorEvent event)
                                {
                                    List<Sensor> ActiveSensor = SM.getSensorList(Sensor.TYPE_LIGHT);
                                    NowActive[0] = ActiveSensor.get(0);
                                    System.out.println("Light Sensor: " + event.values[0] + " lux");
                                }
                                @Override
                                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                                }
                            }, SM.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_UI);
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
                                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                                }
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
                            }, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
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
                        }
                        else if (SensorType.getSelectedItemPosition() == 1 && ScalarSensor.getSelectedItemPosition() == 2)
                        {
                            SM.unregisterListener(SEL2[0], NowActive[0]);
                        }
                        if(SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 1)
                        {
                            SM.unregisterListener(SEL3[0],NowActive[0]);
                        }
                        else if (SensorType.getSelectedItemPosition() == 2 && VectorSensor.getSelectedItemPosition() == 2)
                        {
                            SM.unregisterListener(SEL4[0],NowActive[0]);
                        }
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
                long MSec = System.currentTimeMillis() - TimeStart;
                int Sec = (int)(MSec/1000);
                int Min = Sec/60;
                Sec = Sec % 60;
                int Hrs = Min/60;
                Min = Min % 60;

                Timer.setText(String.format("Timer : "+"%02d:%02d:%02d",Hrs,Min,Sec));
                TimeHandler.postDelayed(this,500);
            }
        };
    }

    private int CheckSpinners(int SensorType, int ScalarSensor, int VectorSensor)
    {
        if(SensorType != 0)
        {
            if(ScalarSensor != 0 || VectorSensor != 0)
                return 1;
            else
                return 0;
        }
        else
            return 0;
    }
}
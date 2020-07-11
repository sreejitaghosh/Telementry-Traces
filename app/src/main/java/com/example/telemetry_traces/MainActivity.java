package com.example.telemetry_traces;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView Timer;
    long TimeStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timer = findViewById(R.id.TimerTextView);
        Button StartStopButton = findViewById(R.id.StartStopButton);
        final Spinner SensorType = (Spinner) findViewById(R.id.SensorType);
        final Spinner ScalarSensor = (Spinner) findViewById(R.id.ScalarSensor);
        final Spinner VectorSensor = (Spinner) findViewById(R.id.VectorSensor);
        final TextView TV2 = (TextView) findViewById(R.id.TextView2);

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
                        VectorSensor.setVisibility(View.VISIBLE);
                        ScalarSensor.setVisibility(View.INVISIBLE);
                    }
                    else if(position == 2)
                    {
                        TV2.setVisibility(View.VISIBLE);
                        ScalarSensor.setVisibility(View.VISIBLE);
                        VectorSensor.setVisibility(View.INVISIBLE);
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
    }
}
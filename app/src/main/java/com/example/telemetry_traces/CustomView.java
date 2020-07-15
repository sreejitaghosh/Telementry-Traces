package com.example.telemetry_traces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;

public class CustomView extends View
{
    private ArrayList<String> TimeValues = new ArrayList<>(); // This array list will be used in all sensors to store timings of data recorded.
    private ArrayList<Double> LightSensorValues = new ArrayList<Double>(); // This array list will be used to store data of Light Sensor only.
    private ArrayList<Double> Centimeter = new ArrayList<Double>(); // This array list will be used to store data of Proximity Sensor only.
    private int CVLayoutSize; // This is global variable to store layout size of custom view.
    private float GraphOffset = 50;
    private float XAxisValue1,YAxisValue1,XAxisValue2,YAxisValue2;
    private int SensorId;
    private MainActivity M = new MainActivity();

    // Declaring required constructors of class CustomView.
    public CustomView(Context context) {
        super(context);
    }
    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    // End of declaration.

    public void onMeasure(int CustomViewWidth, int CustomViewHeight) // This method is used to force custom view to be square.
    {
        super.onMeasure(CustomViewWidth, CustomViewHeight);
        int ViewWidth, ViewHeight, CVSize;
        ViewWidth = MeasureSpec.getSize(CustomViewWidth); // Getting screen width.
        ViewHeight = MeasureSpec.getSize(CustomViewHeight); // Getting screen height.
        if (ViewHeight < ViewWidth) // In case Screen height is less than screen width, do as below.
        {
            CVSize = ViewHeight; // Size of custom view will be the one that is less than other to prevent view getting out of screen from any one dimension.
        }
        else // In case Screen width is less than screen height, do as below.
        {
            CVSize = ViewWidth; // Size of custom view will be the one that is less than other to prevent view getting out of screen from any one dimension.
        }
        this.CVLayoutSize = CVSize; // Assigning CVSize to the global variable so that it can be used in all the methods.
        setMeasuredDimension(CVSize,CVSize); // Setting dimensions of CV.
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // Defining all colors.
        Paint AxisColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        AxisColor.setColor(Color.BLACK); // This color will be given to X and Y axis.
        TextPaint TextColor = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextColor.setColor(Color.BLUE); // This color will be given to X and Y axis.
        TextColor.setTextSize(40);
        Paint Color1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Color1.setColor(Color.RED); // This color will be given to one of the graph lines.
        Paint Color2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Color2.setColor(Color.YELLOW); // This color will be given to one of the graph lines.
        Paint Color3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Color3.setColor(Color.GREEN); // This color will be given to one of the graph lines.
        // Colors defined.

        canvas.drawLine(GraphOffset,CVLayoutSize-GraphOffset*3,CVLayoutSize-GraphOffset,CVLayoutSize-GraphOffset*3,AxisColor); // This will draw X-axis on canvas.
        canvas.drawLine(GraphOffset*3,CVLayoutSize-GraphOffset,GraphOffset*3,GraphOffset,AxisColor); // This will draw Y-axis on canvas.
        canvas.drawText("Time Axis ->",getWidth()/3+GraphOffset,getHeight()-GraphOffset/2,TextColor); // Text below X-axis.
        canvas.rotate(-90,GraphOffset+10,getHeight()-getHeight()/4-GraphOffset*3/2); // Rotating canvas to rotate Y-axis text by 90 degrees.
        canvas.drawText("Value Axis (Values are in LUX) ->",0,getHeight()-getHeight()/3,TextColor); // Text below Y-axis.
        canvas.rotate(90,GraphOffset+10,getHeight()-getHeight()/4-GraphOffset*3/2); // Rotating canvas to bring back everything to it's original position.
        XAxisValue1 = GraphOffset*3; // Initializing x-axis' 0.
        YAxisValue1 = CVLayoutSize-GraphOffset*3; // Initializing y-axis' 0.
        SensorId = CheckSensor(); // Calling function to check which sensor is currently selected.
/*
        if(SensorId == 1)
        {
            this.TimeValues = M.TimeSpan;
            System.out.println("Time Values are : "+TimeValues.toString());
            this.LightSensorValues = M.Lux;
            System.out.println("Light Sensor Values are : "+LightSensorValues.toString());
            for(int i=0;i<LightSensorValues.size();i++)
            {
                XAxisValue2 = 0;
                YAxisValue2 = i;
                canvas.drawLine(XAxisValue1, YAxisValue2, XAxisValue2, YAxisValue2, Color1);
                XAxisValue1 = XAxisValue2;
                YAxisValue1 = YAxisValue2;
            }
            postInvalidateDelayed(5000);
        }
        else
        {
            System.out.println("No Sensors are selected.");
        }
*/
    }

    private int CheckSensor()
    {
        if(M.SensorType.getSelectedItemPosition() == 1)
        {
            if(M.ScalarSensor.getSelectedItemPosition() == 1)
            {
                return 1;
            }
            else if(M.ScalarSensor.getSelectedItemPosition() == 2)
            {
                return 2;
            }
        }
        else if(M.SensorType.getSelectedItemPosition() == 2)
        {
            if(M.VectorSensor.getSelectedItemPosition() == 1)
            {
                return 3;
            }
            else if(M.VectorSensor.getSelectedItemPosition() == 2)
            {
                return 4;
            }
        }
        return 0;
    }
}
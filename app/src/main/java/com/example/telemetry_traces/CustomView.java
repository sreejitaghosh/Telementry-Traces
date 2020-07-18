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
import java.util.HashMap;
import java.util.Map;

public class CustomView extends View
{
    private Map<String, ArrayList<Float>> ScalarSensorData = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data for scalar sensors.

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

        SensorId = CheckSensor(); // Calling function to check which sensor is currently selected.

        if(SensorId == 1 || SensorId == 2) // In case scalar sensors are selected, graph will be different.
        {
            canvas.drawLine(GraphOffset,CVLayoutSize-GraphOffset*3,CVLayoutSize-GraphOffset,CVLayoutSize-GraphOffset*3,AxisColor); // This will draw X-axis on canvas.
            canvas.drawLine(GraphOffset*3,CVLayoutSize-GraphOffset,GraphOffset*3,GraphOffset,AxisColor); // This will draw Y-axis on canvas.
            canvas.drawText("Time Axis (in Seconds) ->",getWidth()/3+GraphOffset,getHeight()-GraphOffset/2,TextColor); // Text below X-axis.
            XAxisValue1 = GraphOffset*3; // Initializing x-axis' 0.
            YAxisValue1 = CVLayoutSize-GraphOffset*3; // Initializing y-axis' 0.
            ScalarSensorData = M.TimeData1;
            if(SensorId == 1) // If Light Sensor is selected, run below code.
            {
                canvas.rotate(-90,GraphOffset+10,getHeight()-getHeight()/4-GraphOffset*3/2); // Rotating canvas to rotate Y-axis text by 90 degrees.
                canvas.drawText("Values in LUX ->",0,getHeight()-getHeight()/3,TextColor); // Text below Y-axis.
                canvas.rotate(90,GraphOffset+10,getHeight()-getHeight()/4-GraphOffset*3/2); // Rotating canvas to bring back everything to it's original position.
                System.out.println("Light Sensor Values are : "+ScalarSensorData.toString());
                // Max value in light sensor will be 600. Hence, all the values will be between 0 and 600. Y-axis will be divided into 12 parts of 50 each.
                float timePoints = (getWidth()-GraphOffset*4)/10; // This variable contains 1/10th of total width.
                for(int i=1;i<=11;i++) // This loop is to divide x-axis of time into 10 equal parts of total time 5 seconds.
                {
                    canvas.drawText((i-1)*0.5+"",i*timePoints-10,getHeight()-GraphOffset*2,TextColor); // Text below X-axis.
                }
                float light_LUX_Points = (getHeight() - GraphOffset*4)/12; // This variable contains 1/12th pixels of total height.
                int j=12;
                for(int i=1; i<=12; i++) // This loop is to divide y-axis of LUX into 12 equal parts of total 600 LUX.
                {
                    canvas.drawText(i * 50 + "", GraphOffset + 10, j*light_LUX_Points+20, TextColor); // Text for Y-axis. j is running in
                    j--;
                }
            }
            else if(SensorId == 2)
            {
                canvas.rotate(-90,GraphOffset+10,getHeight()-getHeight()/4-GraphOffset*3/2); // Rotating canvas to rotate Y-axis text by 90 degrees.
                canvas.drawText("Values in LUX ->",0,getHeight()-getHeight()/3,TextColor); // Text below Y-axis.
                canvas.rotate(90,GraphOffset+10,getHeight()-getHeight()/4-GraphOffset*3/2); // Rotating canvas to bring back everything to it's original position.
                System.out.println("Light Sensor Values are : "+ScalarSensorData.toString());
                // Max value in light sensor will be 600. Hence, all the values will be between 0 and 600. Y-axis will be divided into 12 parts of 50 each.
                float timePoints = (getWidth()-GraphOffset*4)/10; // This variable contains 1/10th of total width.
                for(int i=1;i<=11;i++) // This loop is to divide x-axis of time into 10 equal parts of total time 5 seconds.
                {
                    canvas.drawText((i-1)*0.5+"",i*timePoints-10,getHeight()-GraphOffset*2,TextColor); // Text below X-axis.
                }
                float light_LUX_Points = (getHeight() - GraphOffset*4)/12; // This variable contains 1/12th pixels of total height.
                int j=12;
                for(int i=1; i<=12; i++)
                {
                    canvas.drawText(i * 50 + "", GraphOffset + 10, (j)*light_LUX_Points+20, TextColor); // Text for Y-axis.
                    j--;
                }
            }
            postInvalidateDelayed(5000);
        }
        else if(SensorId == 3 || SensorId == 4) // In case vector sensors are selected, graph will be different.
        {
            postInvalidateDelayed(5000);
        }
        else // In case there are no sensors selected, graph will not be shown.
        {
            System.out.println("No Sensors are selected.");
        }
        postInvalidateDelayed(1000);
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
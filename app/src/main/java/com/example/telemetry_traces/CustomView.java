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
    private float GraphOffset;
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

        GraphOffset = getWidth()/12; // Setting Graph Offset. This will be used everywhere.
        SensorId = CheckSensor(); // Calling function to check which sensor is currently selected.

        float XAxisFontYPosition = getHeight()-GraphOffset-30; // This is Y position of X-Axis fonts. This is for numbers on X-Axis.
        float XAxisFontLabelXPosition = getWidth()/3; // This is X position of X-Axis Label fonts.
        float XAxisFontLabelYPosition = getHeight()-GraphOffset/2; // This is Y position of X-Axis Label fonts.

        if(SensorId == 1 || SensorId == 2) // In case scalar sensors are selected, graph will be different.
        {
            canvas.drawLine(GraphOffset,getHeight()-GraphOffset*2,getWidth()-GraphOffset,getHeight()-GraphOffset*2,AxisColor); // This will draw X-axis on canvas.
            canvas.drawText("0",GraphOffset+30,XAxisFontYPosition,TextColor); // Text "0" below X & Y axis.
            canvas.drawText("Time Axis (in Seconds)   ->",XAxisFontLabelXPosition,XAxisFontLabelYPosition,TextColor); // Text below X-axis.
            float timePoints = (getWidth()-GraphOffset*4)/5; // This variable contains 1/5th of total width after deduction of offset from both sides.
            canvas.translate(GraphOffset*2,0); // Translating canvas by offset size to get X-Axis points to proper positions.
            for(int i=1;i<=5;i++) // This loop is to divide x-axis of time into 5 equal parts of total time 5 seconds.
            {
                canvas.drawText(i+"",i*timePoints-10,XAxisFontYPosition,TextColor); // Text below X-axis.
            }
            canvas.translate(-GraphOffset*2,0); // Restoring back canvas to it's original position.

            XAxisValue1 = GraphOffset*2; // Initializing x-axis to 0 as per graph axis.
            YAxisValue1 = getHeight()-GraphOffset*2; // Initializing y-axis to 0 as per graph axis.
            ScalarSensorData = M.TimeData1; // Coping data from MainActivity's scalar sensor's HashMap to local HashMap for simplicity.

            if(SensorId == 1) // If light sensor is selected in spinner, run below code.
            {
                canvas.drawLine(GraphOffset*2,getHeight()-GraphOffset,GraphOffset*2,GraphOffset-20,AxisColor); // This will draw Y-axis on canvas.
                canvas.rotate(-90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to rotate Y-axis text by 90 degrees.
                canvas.drawText("Light sensor values in LUX   ->",0,getHeight()-getHeight()/3-(GraphOffset/3),TextColor); // Text below Y-axis.
                canvas.rotate(90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to bring back everything to it's original position.
                System.out.println("Light Sensor Values are : "+ScalarSensorData.toString());
                // Max value in light sensor will be 600. Hence, all the values will be between 0 and 600. Y-axis will be divided into 12 parts of 50 each.
                float lightPoints = (getHeight()-GraphOffset*3)/6; // This variable contains 1/10th pixels of total height.
                int j=6;
                for(int i=1; i<=6; i++) // This loop is to divide y-axis of LUX into 12 equal parts of total 600 LUX.
                {
                    canvas.drawText(i*100+"", GraphOffset, j*lightPoints-20, TextColor); // Text for Y-axis. j is running in decremental manner.
                    j--; // Decrementing j by 1.
                }


                XAxisValue2 = ((getWidth()-GraphOffset*4)/5)+GraphOffset*2; // Setting XAxisValue2 to 1 so that graph in 1st second will take changes of X-Axis automatically.
                int counter = 1; // Setting counter to 1.
                for(String Key:ScalarSensorData.keySet()) // This loop runs for all 5 seconds.
                {
                    for(int i=0;i<ScalarSensorData.get(Key).size();i++) // This loop runs till all values in 1st second is completely plotted.
                    {
                        YAxisValue2 = getHeight()-GraphOffset*2-(((getHeight()-GraphOffset*3)*ScalarSensorData.get(Key).get(i))/600); // This is total height of canvas - (height of point from sensor out of 600 LUX)
                        canvas.drawLine(XAxisValue1,YAxisValue1,XAxisValue2,YAxisValue2,Color1); // Connecting new points plotted on graph.
                        YAxisValue1 = YAxisValue2; // Assigning y-axis 2nd value to y-axis 1st value so that next line will be continued from where previous line is ended.
                        XAxisValue1 = XAxisValue2; // Assigning x-axis 2nd value to x-axis 1st value so that next line will be continued from where previous line is ended.
                    }
                    if(counter!=5) // If counter is not equal to 5, i.e. if this isn't the 5th second, do as below.
                    {
                        XAxisValue1 = XAxisValue2; // Assigning x-axis 2nd value to x-axis 1st value so that next line will be continued from where previous line is ended.
                        XAxisValue2 = XAxisValue2 + (getWidth()-GraphOffset*4)/5; // Incrementing position of x-axis.
                        counter++; // Incrementing counter by 1.
                    }
                    else // As the graph reaches 5th Second, reset counter and graph x-axis position.
                    {
                        XAxisValue1 = GraphOffset*2; // Initializing x-axis value back to 0.
                        counter = 1; // Incrementing counter by 1.
                    }
                }
            }
            else if(SensorId == 2) // If proximity sensor is selected by user from spinner.
            {
                // Code for Sensor 2.
            }
        }
        else if(SensorId == 3 || SensorId == 4) // In case vector sensors are selected, graph will be different.
        {

        }
        else // In case there are no sensors selected, graph will not be shown.
        {
            canvas.drawLine(GraphOffset,getHeight()-GraphOffset*2,getWidth()-GraphOffset,getHeight()-GraphOffset*2,AxisColor); // This will draw X-axis on canvas.
            canvas.drawLine(GraphOffset*2,getHeight()-GraphOffset,GraphOffset*2,GraphOffset,AxisColor); // This will draw Y-axis on canvas.
            canvas.drawText("0",GraphOffset+30,getHeight()-GraphOffset-30,TextColor); // Text below X-axis.
            canvas.drawText("Time Axis (in Seconds)   ->",getWidth()/3,getHeight()-GraphOffset/2,TextColor); // Text below X-axis.
            canvas.rotate(-90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to rotate Y-axis text by 90 degrees.
            canvas.drawText("Sensor values on Y-Axis   ->",0,getHeight()-getHeight()/3-(GraphOffset/3),TextColor); // Text below Y-axis.
            canvas.rotate(90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to bring back everything to it's original position.
            canvas.drawText("Graph will be displayed here",GraphOffset*3,getHeight()/2-GraphOffset*2,TextColor); // Message.
            canvas.drawText("once you start tracing or you",GraphOffset*3,getHeight()/2-GraphOffset*2+40,TextColor); // Message.
            canvas.drawText("can select sensor to view",GraphOffset*3,getHeight()/2-GraphOffset*2+80,TextColor); // Message.
            canvas.drawText("last run (if any) of your trace.",GraphOffset*3,getHeight()/2-GraphOffset*2+120,TextColor); // Message.
            System.out.println("No Sensors are selected.");
        }
        postInvalidateDelayed(5000);
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
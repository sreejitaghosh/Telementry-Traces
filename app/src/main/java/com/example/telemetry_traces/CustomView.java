package com.example.telemetry_traces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomView extends View
{
    private Map<String, ArrayList<Float>> ScalarSensorData = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data for scalar sensors.
    private Map<String, ArrayList<Float>> VectorSensorDataX = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data for vector sensors.
    private Map<String, ArrayList<Float>> VectorSensorDataY = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data for vector sensors.
    private Map<String, ArrayList<Float>> VectorSensorDataZ = new HashMap<String, ArrayList<Float>>(); // This HashMap will contain data for vector sensors.
    private float GraphOffset; // This will store offset of graph to leave space for custom view from all sides.
    private float XAxisValue1,YAxisValue1,XAxisValue2,YAxisValue2; // These will store values to show graph in scalar sensors.
    private float YAxisValue1X,YAxisValue2X; // These will store x-axis values in custom view graph y-axis values to show graph in vector sensors.
    private float YAxisValue1Y,YAxisValue2Y; // These will store y-axis values in custom view graph y-axis values to show graph in vector sensors.
    private float YAxisValue1Z,YAxisValue2Z; // These will store z-axis values in custom view graph y-axis values to show graph in vector sensors.
    private int SensorId; // This will store sensor id. Sensor id will be used to identify which sensor is now active.
    private MainActivity M = new MainActivity(); // Object of MainActivity class to fetch values of sensors.

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
        int ViewWidth, ViewHeight, LayoutSize; // Variables for screen width, screen height and custom view layout size.
        ViewWidth = MeasureSpec.getSize(CustomViewWidth); // Getting screen width.
        ViewHeight = MeasureSpec.getSize(CustomViewHeight); // Getting screen height.
        if (ViewHeight < ViewWidth) // If screen height is less than screen width, do as below.
        {
            LayoutSize = ViewHeight; // Custom view width and height will be same as screen width to prevent view getting out of screen from any one dimension.
        }
        else // If screen width is less than screen width, do as below.
        {
            LayoutSize = ViewWidth; // Custom view width and height will be same as screen width to prevent view getting out of screen from any one dimension.
        }
        setMeasuredDimension(LayoutSize,LayoutSize); // Assigning dimensions to Custom View.
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // Declaration of colors start.
        Paint AxisColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        AxisColor.setColor(Color.BLACK); // This color will be given to X and Y axis.
        AxisColor.setStrokeWidth(3);
        TextPaint TextColor = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        TextColor.setColor(Color.BLUE); // This color will be given to X and Y axis.
        TextColor.setTextSize(40);
        Paint Color1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Color1.setColor(Color.RED); // This color will be given to one of the graph lines.
        Color1.setStrokeWidth(5);
        Paint Color2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Color2.setColor(Color.BLACK); // This color will be given to one of the graph lines.
        Color2.setStrokeWidth(5);
        Paint Color3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        Color3.setColor(Color.GREEN); // This color will be given to one of the graph lines.
        Color3.setStrokeWidth(5);
        // Declaration of colors ends.
        GraphOffset = getWidth()/14; // Setting Graph Offset. This will be used everywhere.
        SensorId = CheckSensor(); // Calling function to check which sensor is currently selected.
        float XAxisFontYPosition = getHeight()-GraphOffset-20; // This is Y position of X-Axis fonts. This is for numbers on X-Axis.
        float XAxisFontLabelXPosition = getWidth()/3; // This is X position of X-Axis Label fonts.
        float XAxisFontLabelYPosition = getHeight()-GraphOffset/2; // This is Y position of X-Axis Label fonts.
        if(SensorId != 0) // If sensor id is 0, i.e. if any one of given sensors are selected by user, do as below.
            canvas.drawText("Graph will be updated in every 5 seconds.",GraphOffset*3,GraphOffset*2,TextColor); // Display this message at given x and y positions of screen.
        if(SensorId == 1 || SensorId == 2) // In case scalar sensors are selected, graph will be different.
        {
            float MaxYAxisValue = 0; // Initializing Max value of Y axis on graph to 0. This will be dynamically changed later on.
            int keycount = 0; // Initializing Key count to 0. We need Key count to define offset in each iteration. This will be used later.
            int j = 0; // Initializing j to 0. j will define Y-Axis values on graph.
            ScalarSensorData = M.TimeData1; // Coping data from MainActivity's scalar sensor's HashMap to local HashMap for simplicity.
            canvas.drawLine(GraphOffset,getHeight()-GraphOffset*2,getWidth()-GraphOffset,getHeight()-GraphOffset*2,AxisColor); // This will draw X-axis on canvas.
            canvas.drawLine(GraphOffset*2,getHeight()-GraphOffset,GraphOffset*2,GraphOffset*2,AxisColor); // This will draw Y-axis on canvas.
            canvas.drawText("0",GraphOffset+30,XAxisFontYPosition,TextColor); // Text "0" below X & Y axis.
            canvas.drawText("Time Axis (in Seconds)   ->",XAxisFontLabelXPosition,XAxisFontLabelYPosition,TextColor); // Text below X-axis.
            canvas.translate(GraphOffset*2,0); // Translating canvas by offset size to get X-Axis points to proper positions.
            for(int k=1; k<=5; k++) // This is for strokes at every axis points.
            {
                canvas.drawLine(k * GraphOffset*2, canvas.getHeight()-GraphOffset*2-10, k * GraphOffset*2, canvas.getHeight()-GraphOffset*2+10, AxisColor);
            }
            for(int i=1;i<=5;i++) // This loop is to divide x-axis of time into 5 equal parts of total time 5 seconds.
            {
                canvas.drawText(i+"",i*GraphOffset*2-10,XAxisFontYPosition,TextColor); // Text below X-axis.
            }
            canvas.translate(-GraphOffset*2,0); // Restoring back canvas to it's original position.
            XAxisValue1 = GraphOffset*2; // Initializing x-axis to 0 as per graph axis.
            YAxisValue1 = getHeight()-GraphOffset*2; // Initializing y-axis to 0 as per graph axis.
            if(SensorId == 1) // If sensor is Light Sensor, do as below.
            {
                canvas.rotate(-90, GraphOffset, getHeight() - getHeight() / 3); // Rotating canvas to rotate Y-axis text by 90 degrees.
                canvas.drawText("Light sensor values in LUX   ->", 0, getHeight() - getHeight() / 3 - (GraphOffset / 3), TextColor); // Text below Y-axis.
                canvas.rotate(90, GraphOffset, getHeight() - getHeight() / 3); // Rotating canvas to bring back everything to it's original position.
                System.out.println("Light Sensor Values are : " + ScalarSensorData.toString()); // Printing values for debug purpose.
                j=6; // In case Sensor is Light sensor, divide Y-axis in 6 parts.
            }
            else if(SensorId == 2) // If sensor is Proximity sensor, do as below.
            {
                canvas.drawText("Graph will not change unless sensor",GraphOffset*3,GraphOffset*2+GraphOffset/2,TextColor);
                canvas.drawText("sense value in every second.",GraphOffset*3,GraphOffset*3,TextColor);
                canvas.rotate(-90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to rotate Y-axis text by 90 degrees.
                canvas.drawText("Proximity sensor values in CM   ->",0,getHeight()-getHeight()/3-(GraphOffset/3),TextColor); // Text below Y-axis.
                canvas.rotate(90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to bring back everything to it's original position.
                System.out.println("Proximity Sensor Values are : "+ScalarSensorData.toString()); // Printing values for debug purpose.
                j=4; // In case Sensor is Proximity sensor, divide Y-axis in 4 parts.
            }
            int counter = 1; // Setting counter to 1.
            XAxisValue2 = GraphOffset*2; // Setting XAxisValue2 to 1 so that graph in 1st second will take changes of X-Axis automatically.
            if(ScalarSensorData.size()>0) // In case tracing is started yet, there will be values in data structure.
            {
                MaxYAxisValue = FindMaxValue(); // Y Axis values will change as per value here.
            }
            else if(ScalarSensorData.size() == 0 && SensorId == 1) // In case tracing is not started yet, there will be no value in data structure and if sensor id is 1, we want to set maximum Y-axis value to 600 for Light Sensor.
            {
                MaxYAxisValue = 600; // Assigning max value to Y-axis for light sensor.
            }
            else if(ScalarSensorData.size() == 0 && SensorId == 2) // In case tracing is not started yet, there will be no value in data structure and if sensor id is 1, we want to set maximum Y-axis value to 20 for Proximity Sensor.
            {
                MaxYAxisValue = 20; // Assigning max value to X-axis for proximity sensor.
            }
            if(SensorId == 1) // If light sensor is selected.
            {
                canvas.translate(0, GraphOffset * 3 / 2); // Translating canvas to Y-axis by 3/2. This is done to adjust values of Y-axis to proper positions.
                for (float i = MaxYAxisValue / 6; i <= MaxYAxisValue; i = i + MaxYAxisValue / 6) // This loop is to divide y-axis of LUX into 12 equal parts of total 600 LUX.
                {
                    canvas.drawText(String.format("%.0f", i), GraphOffset - 10, j * GraphOffset * 3 / 2 + 10, TextColor); // Text for Y-axis. j is running in decremental manner.
                    j--; // Decrementing j by 1.
                }
                for (int i = 1; i <= 6; i++) {
                    canvas.drawLine(GraphOffset * 2 - 10, i * GraphOffset * 3 / 2, GraphOffset * 2 + 10, i * GraphOffset * 3 / 2, AxisColor);
                }
                canvas.translate(0, -GraphOffset * 3 / 2); // Restoring back canvas to its original position.
            }
            else if(SensorId == 2) // If proximity sensor is selected.
            {
                canvas.translate(0,GraphOffset*2); // Translating canvas to Y-axis by 2*Graphoffset. This is done to adjust values of Y-axis to proper positions.
                for(float i=MaxYAxisValue/4; i<=MaxYAxisValue; i=i+MaxYAxisValue/4)
                {
                    canvas.drawText(i+"", GraphOffset-10, j*GraphOffset*2+10, TextColor); // Text for Y-axis. j is running in decremental manner.
                    j--; // Decrementing j by 1.
                }
                for (int i=0;i<5; i++)
                {
                    canvas.drawLine(GraphOffset*2-10, i * GraphOffset*2, GraphOffset*2+10, i * GraphOffset*2, AxisColor);
                }
                canvas.translate(0,-GraphOffset*2); // Restoring back canvas to its original position.
            }
            for(String Key:ScalarSensorData.keySet()) // This loop runs for all 5 seconds.
            {
                XAxisValue2 = XAxisValue2 + (GraphOffset*2)/ScalarSensorData.get(Key).size(); // Incrementing X axis value after dividing 1 second into eqaul parts of number of points recorded in each second by sensor.
                for(int i=0;i<ScalarSensorData.get(Key).size();i++) // This loop runs till all values in 1st second is completely plotted.
                {
                    // This is total height of canvas - (height of point from sensor out of max 600 LUX)
                    if(SensorId == 1)
                        YAxisValue2 = getHeight()-GraphOffset*2-(((getHeight()-GraphOffset*5)*ScalarSensorData.get(Key).get(i))/MaxYAxisValue);
                    else if(SensorId == 2)
                        YAxisValue2 = getHeight()-GraphOffset*2-(((getHeight()-GraphOffset*6)*ScalarSensorData.get(Key).get(i))/MaxYAxisValue);
                    canvas.drawLine(XAxisValue1,YAxisValue1,XAxisValue2,YAxisValue2,Color1); // Connecting new points plotted on graph.
                    YAxisValue1 = YAxisValue2; // Assigning y-axis 2nd value to y-axis 1st value so that next line will be continued from where previous line is ended.
                    XAxisValue1 = XAxisValue2; // Assigning x-axis 2nd value to x-axis 1st value so that next line will be continued from where previous line is ended.
                    XAxisValue2 = XAxisValue2 + (GraphOffset * 2) / ScalarSensorData.get(Key).size(); // Incrementing X axis value after dividing 1 second into eqaul parts of number of points recorded in each second by sensor.
                }
                if(counter!=5) // If counter is not equal to 5, i.e. if this isn't the 5th second, do as below.
                {
                    YAxisValue1 = YAxisValue2; // Assigning y-axis 2nd value to y-axis 1st value so that next line will be continued from where previous line is ended.
                    counter++; // Incrementing counter by 1.
                    keycount++; // Increamenting keycount by 1.
                    XAxisValue2 = GraphOffset*2 + GraphOffset*2*keycount; // Incrementing X axis by 1 second every time new list is occurring from HashMap.
                }
                else // As the graph reaches 5th Second, reset counter and graph x-axis position.
                {
                    XAxisValue1 = GraphOffset * 2; // Initializing X axis start position to 0.
                    XAxisValue2 = GraphOffset * 2; // Initializing X axis end position to 0. This is because I am adding some value to X axis end position at start of for loop above.
                    YAxisValue1 = YAxisValue2; // Assigning end position of last cycle to start position of new cycle.
                    counter = 1; // Resetting counter to 1.
                    keycount = 0; // Initializing keycount to 0. This will also initialize x end position to 0 + some seconds in 1st second.
                }
            }
            postInvalidateDelayed(5000); // Delaying refresh by 5 seconds. This will refresh graph every 5 seconds.
        }
        else if(SensorId == 3 || SensorId == 4) // In case vector sensors are selected, graph will be different.
        {
            float MaxYAxisValue = 0; // Initializing Max value of Y axis on graph to 0. This will be dynamically changed later on.
            int keycount = 0; // Initializing Key count to 0. We need Key count to define offset in each iteration. This will be used later.
            int j = 0; // Initializing j to 0. j will define Y-Axis values on graph.
            VectorSensorDataX = M.TimeData2X; // Coping data from MainActivity's vector sensor's HashMap to local HashMap for simplicity.
            VectorSensorDataY = M.TimeData2Y; // Coping data from MainActivity's vector sensor's HashMap to local HashMap for simplicity.
            VectorSensorDataZ = M.TimeData2Z; // Coping data from MainActivity's vector sensor's HashMap to local HashMap for simplicity.
            canvas.drawLine(GraphOffset, canvas.getHeight() - canvas.getHeight() / 2, canvas.getWidth() - GraphOffset, getHeight() - getHeight() / 2, AxisColor); // This will draw X-axis on canvas.
            canvas.drawLine(GraphOffset, GraphOffset * 2, GraphOffset, canvas.getHeight() - GraphOffset * 2, AxisColor); // This will draw Y-axis on canvas.
            canvas.drawText("0", GraphOffset / 2, getHeight() / 2 + 15, TextColor); // Text "0" below X & Y axis.
            canvas.drawText("Time Axis (in Seconds)   ->",canvas.getWidth()/3,canvas.getHeight()-GraphOffset*2, TextColor); // Text below X-axis.
            canvas.translate(GraphOffset, 0);
            for (int k = 1; k <= 5; k++) // This is for strokes at every axis points.
            {
                canvas.drawLine(k * GraphOffset * 2, canvas.getHeight() / 2 - 10, k * GraphOffset * 2, canvas.getHeight() / 2 + 10, AxisColor);
            }
            for (int i = 1; i <= 5; i++) // This loop is to divide x-axis of time into 5 equal parts of total time 5 seconds.
            {
                canvas.drawText(i + "", i * GraphOffset * 2 - 10, canvas.getHeight() / 2 + GraphOffset, TextColor); // Text below X-axis.
            }
            canvas.translate(-GraphOffset, 0);
            XAxisValue1 = GraphOffset; // Initializing X-Axis position.
            YAxisValue1X = getHeight() / 2; // Initializing Y-Axis position.
            YAxisValue1Y = getHeight() / 2; // Initializing Y-Axis position.
            YAxisValue1Z = getHeight() / 2; // Initializing Y-Axis position.
            int counter = 1; // Setting counter to 1.
            XAxisValue2 = GraphOffset; // Initializing X-Axis position.
            if (VectorSensorDataX.size() > 0)
                MaxYAxisValue = FindMaxValue();
            else if (SensorId == 3)
                MaxYAxisValue = 2.0f;
            else if (SensorId == 4)
                MaxYAxisValue = 1.0f;
            j = 1;
            canvas.translate(0, GraphOffset);
            for (int i = 1; i <= 5; i++) {
                canvas.drawLine(GraphOffset - 10, i * GraphOffset * 2, GraphOffset + 10, i * GraphOffset * 2, AxisColor);
            }
            for (float i = MaxYAxisValue; i >= -MaxYAxisValue; i = i - MaxYAxisValue / 2) {
                if (i != 0.0f && i > 0.0f)
                    canvas.drawText(i + "", GraphOffset + 15, j * GraphOffset * 2 + 15, TextColor);
                else if (i != 0.0f && i < 0.0f)
                    canvas.drawText("(" + i + ")", GraphOffset + 20, j * GraphOffset * 2 + 15, TextColor);
                j++;
            }
            canvas.translate(0, -GraphOffset);
            for (String Key : VectorSensorDataX.keySet())
            {
                XAxisValue2 = XAxisValue2 + ((GraphOffset * 2)/VectorSensorDataX.get(Key).size());
                for (int i = 0; i < VectorSensorDataX.get(Key).size(); i++)
                {
                    YAxisValue2X = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 3) * VectorSensorDataX.get(Key).get(i)) / MaxYAxisValue));
                    canvas.drawLine(XAxisValue1, YAxisValue1X, XAxisValue2, YAxisValue2X, Color1); // Connecting new points plotted on graph.
                    YAxisValue2Y = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 3) * VectorSensorDataY.get(Key).get(i)) / MaxYAxisValue));
                    canvas.drawLine(XAxisValue1, YAxisValue1Y, XAxisValue2, YAxisValue2Y, Color2); // Connecting new points plotted on graph.
                    YAxisValue2Z = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 3) * VectorSensorDataZ.get(Key).get(i)) / MaxYAxisValue));
                    canvas.drawLine(XAxisValue1, YAxisValue1Z, XAxisValue2, YAxisValue2Z, Color3); // Connecting new points plotted on graph.
                    YAxisValue1X = YAxisValue2X;
                    YAxisValue1Y = YAxisValue2Y;
                    YAxisValue1Z = YAxisValue2Z;
                    XAxisValue1 = XAxisValue2;
                    XAxisValue2 = XAxisValue2 + ((GraphOffset * 2) / VectorSensorDataX.get(Key).size());
                }
                if (counter < 5) {
                    YAxisValue1X = YAxisValue2X;
                    YAxisValue1Y = YAxisValue2Y;
                    YAxisValue1Z = YAxisValue2Z;
                    counter++;
                    keycount++;
                    XAxisValue2 = GraphOffset + (GraphOffset * 2 * keycount);
                } else {
                    XAxisValue1 = GraphOffset;
                    XAxisValue2 = GraphOffset;
                    YAxisValue1X = YAxisValue2X;
                    YAxisValue1Y = YAxisValue2Y;
                    YAxisValue1Z = YAxisValue2Z;
                    counter = 1;
                    keycount = 0;
                }
            }
            postInvalidateDelayed(5000);
        }
        else // In case there are no sensors selected, graph will not be shown.
        {
            canvas.drawLine(GraphOffset,getHeight()-GraphOffset*2,getWidth()-GraphOffset,getHeight()-GraphOffset*2,AxisColor); // This will draw X-axis on canvas.
            canvas.drawLine(GraphOffset*2,GraphOffset*2,GraphOffset*2,getHeight()-GraphOffset,AxisColor); // This will draw Y-axis on canvas.
            canvas.drawText("0",GraphOffset+30,getHeight()-GraphOffset-30,TextColor); // Text below X-axis.
            canvas.drawText("Time Axis (in Seconds)   ->",getWidth()/3,getHeight()-GraphOffset,TextColor); // Text below X-axis.
            canvas.rotate(-90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to rotate Y-axis text by 90 degrees.
            canvas.drawText("Sensor values on Y-Axis   ->",GraphOffset,getHeight()-getHeight()/3,TextColor); // Text below Y-axis.
            canvas.rotate(90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to bring back everything to it's original position.
            canvas.drawText("Graph will be displayed here",GraphOffset*4,getHeight()/2-GraphOffset,TextColor); // Message.
            canvas.drawText("once you start tracing.",GraphOffset*4,getHeight()/2-GraphOffset+40,TextColor); // Message.
            System.out.println("No Sensors are selected.");
            postInvalidateDelayed(1000);
        }
    }

    private int CheckSensor() // This function is responsible to check which sensor is exactly selected by user.
    {
        if(M.SensorType.getSelectedItemPosition() == 1) // If Sensor type is scalar sensor.
        {
            if(M.ScalarSensor.getSelectedItemPosition() == 1) // If scalar sensor is Light sensor.
            {
                return 1; // Return 1.
            }
            else if(M.ScalarSensor.getSelectedItemPosition() == 2) // If scalar sensor is Proximity sensor.
            {
                return 2; // Return 2.
            }
        }
        else if(M.SensorType.getSelectedItemPosition() == 2) // If Sensor type is vector sensor.
        {
            if(M.VectorSensor.getSelectedItemPosition() == 1) // If vector sensor is Gyroscope sensor.
            {
                return 3; // Return 3.
            }
            else if(M.VectorSensor.getSelectedItemPosition() == 2) // If vector sensor is Linear Accelerometer sensor.
            {
                return 4; // Return 4.
            }
        }
        return 0; // If no condition meets from above, return 0 to indicate no sensors are selected.
    }

    private float FindMaxValue() // This function is responsible to identify maximum value recorded in last 5 seconds by appropriate sensor.
    {
        List<Float> Max = new ArrayList<>(); // List to store Maximum value in scalar and vector sensors both.
        List<Float> Min = new ArrayList<>(); // List to store Minimum value in vector sensors.
        float min = 0; // Initializing variable to 0.
        float max = 0; // Initializing variable to 0.
        if(SensorId == 1 || SensorId == 2) // If sensor selected is scalar sensor, do as below.
        {
            for(String Key:ScalarSensorData.keySet()) // This loop runs for all keys in HashMap.
            {
                Max.add(Collections.max(ScalarSensorData.get(Key))); // This returns maximum value from list of current key in HashMap.
            }
            return Collections.max(Max); // Returning maximum value from list of all maximum values.
        }
        else if(SensorId == 3 || SensorId == 4) // If sensor selected is vector sensor, do as below.
        {
            for(String Key:VectorSensorDataX.keySet()) // This loop runs for all keys in HashMap.
            {
                Max.add(Collections.max(VectorSensorDataX.get(Key))); // Adding max value from positive x-axis values recorded for vector sensors.
                Min.add(Collections.min(VectorSensorDataX.get(Key))); // Adding max value from negative x-axis values recorded for vector sensors.
                Max.add(Collections.max(VectorSensorDataY.get(Key))); // Adding max value from positive y-axis values recorded for vector sensors.
                Min.add(Collections.min(VectorSensorDataY.get(Key))); // Adding max value from negative y-axis values recorded for vector sensors.
                Max.add(Collections.max(VectorSensorDataZ.get(Key))); // Adding max value from positive z-axis values recorded for vector sensors.
                Min.add(Collections.min(VectorSensorDataZ.get(Key))); // Adding max value from negative z-axis values recorded for vector sensors.
            }
            max = Collections.max(Max); // Calculating maximum from list of maximum values.
            min = Collections.min(Min); // Calculating minimum from list of minimum values.
            if(max < (min*(-1))) // Checking if maximum value is less than -ve of minimum value.
            {
                min = min*(-1); // If yes, multiply minimum value by -1.
                return min; // return minimum value.
            }
            return max; // Else return max value.
        }
        return 0.0f; // If no condition from above satisfy, return 0.
    }
}
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
    private float GraphOffset;
    private float XAxisValue1,YAxisValue1,XAxisValue2,YAxisValue2;
    private float YAxisValue1X,YAxisValue2X;
    private float YAxisValue1Y,YAxisValue2Y;
    private float YAxisValue1Z,YAxisValue2Z;
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
        int ViewWidth, ViewHeight, LayoutSize;
        ViewWidth = MeasureSpec.getSize(CustomViewWidth); // Getting screen width.
        ViewHeight = MeasureSpec.getSize(CustomViewHeight); // Getting screen height.
        if (ViewHeight < ViewWidth) // In case Screen height is less than screen width, do as below.
        {
            LayoutSize = ViewHeight; // Size of custom view will be the one that is less than other to prevent view getting out of screen from any one dimension.
        }
        else // In case Screen width is less than screen height, do as below.
        {
            LayoutSize = ViewWidth; // Size of custom view will be the one that is less than other to prevent view getting out of screen from any one dimension.
        }
        setMeasuredDimension(LayoutSize,LayoutSize); // Setting dimensions of CV.
    }

    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // Defining all colors.
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
        // Colors defined.
        GraphOffset = getWidth()/14; // Setting Graph Offset. This will be used everywhere.
        SensorId = CheckSensor(); // Calling function to check which sensor is currently selected.
        float XAxisFontYPosition = getHeight()-GraphOffset-20; // This is Y position of X-Axis fonts. This is for numbers on X-Axis.
        float XAxisFontLabelXPosition = getWidth()/3; // This is X position of X-Axis Label fonts.
        float XAxisFontLabelYPosition = getHeight()-GraphOffset/2; // This is Y position of X-Axis Label fonts.
        if(SensorId != 0)
            canvas.drawText("Graph will be updated in every 5 seconds.",GraphOffset*3,GraphOffset+(GraphOffset/2),TextColor);
        if(SensorId == 1 || SensorId == 2) // In case scalar sensors are selected, graph will be different.
        {
            float MaxYAxisValue = 0;
            int keycount = 0;
            int j = 0;
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
            if(SensorId == 1)
            {
                canvas.rotate(-90, GraphOffset, getHeight() - getHeight() / 3); // Rotating canvas to rotate Y-axis text by 90 degrees.
                canvas.drawText("Light sensor values in LUX   ->", 0, getHeight() - getHeight() / 3 - (GraphOffset / 3), TextColor); // Text below Y-axis.
                canvas.rotate(90, GraphOffset, getHeight() - getHeight() / 3); // Rotating canvas to bring back everything to it's original position.
                System.out.println("Light Sensor Values are : " + ScalarSensorData.toString());
                j=6;
            }
            else if(SensorId == 2)
            {
                canvas.drawText("Graph will not change unless sensor",GraphOffset*3,GraphOffset*2,TextColor);
                canvas.drawText("sense value in every second.",GraphOffset*3,GraphOffset*2+GraphOffset/2,TextColor);
                canvas.rotate(-90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to rotate Y-axis text by 90 degrees.
                canvas.drawText("Proximity sensor values in CM   ->",0,getHeight()-getHeight()/3-(GraphOffset/3),TextColor); // Text below Y-axis.
                canvas.rotate(90,GraphOffset,getHeight()-getHeight()/3); // Rotating canvas to bring back everything to it's original position.
                System.out.println("Proximity Sensor Values are : "+ScalarSensorData.toString());
                j=4;
            }
            int counter = 1; // Setting counter to 1.
            XAxisValue2 = GraphOffset*2; // Setting XAxisValue2 to 1 so that graph in 1st second will take changes of X-Axis automatically.
            if(ScalarSensorData.size()>0)
            {
                MaxYAxisValue = FindMaxValue(); // Y Axis values will change as per value here.
            }
            else if(ScalarSensorData.size() == 0 && SensorId == 1)
            {
                MaxYAxisValue = 600;
            }
            else if(ScalarSensorData.size() == 0 && SensorId == 2)
            {
                MaxYAxisValue = 20;
            }
            if(SensorId == 1)
            {
                canvas.translate(0, GraphOffset * 3 / 2);
                for (float i = MaxYAxisValue / 6; i <= MaxYAxisValue; i = i + MaxYAxisValue / 6) // This loop is to divide y-axis of LUX into 12 equal parts of total 600 LUX.
                {
                    canvas.drawText(String.format("%.0f", i), GraphOffset - 10, j * GraphOffset * 3 / 2 + 10, TextColor); // Text for Y-axis. j is running in decremental manner.
                    j--; // Decrementing j by 1.
                }
                for (int i = 1; i <= 6; i++) {
                    canvas.drawLine(GraphOffset * 2 - 10, i * GraphOffset * 3 / 2, GraphOffset * 2 + 10, i * GraphOffset * 3 / 2, AxisColor);
                }
                canvas.translate(0, -GraphOffset * 3 / 2);
            }
            else if(SensorId == 2)
            {
                canvas.translate(0,GraphOffset*2);
                for(float i=MaxYAxisValue/4; i<=MaxYAxisValue; i=i+MaxYAxisValue/4)
                {
                    canvas.drawText(i+"", GraphOffset-10, j*GraphOffset*2+10, TextColor); // Text for Y-axis. j is running in decremental manner.
                    j--; // Decrementing j by 1.
                }
                for (int i=0;i<5; i++)
                {
                    canvas.drawLine(GraphOffset*2-10, i * GraphOffset*2, GraphOffset*2+10, i * GraphOffset*2, AxisColor);
                }
                canvas.translate(0,-GraphOffset*2);
            }
            for(String Key:ScalarSensorData.keySet()) // This loop runs for all 5 seconds.
            {
                XAxisValue2 = XAxisValue2 + (GraphOffset*2)/ScalarSensorData.get(Key).size();
                for(int i=0;i<ScalarSensorData.get(Key).size();i++) // This loop runs till all values in 1st second is completely plotted.
                {
                    // This is total height of canvas - (height of point from sensor out of max 600 LUX)
                    if(SensorId == 1)
                        YAxisValue2 = getHeight()-GraphOffset*2-(((getHeight()-GraphOffset*5)*ScalarSensorData.get(Key).get(i))/MaxYAxisValue);
                    else
                        YAxisValue2 = getHeight()-GraphOffset*2-(((getHeight()-GraphOffset*6)*ScalarSensorData.get(Key).get(i))/MaxYAxisValue);
                    canvas.drawLine(XAxisValue1,YAxisValue1,XAxisValue2,YAxisValue2,Color1); // Connecting new points plotted on graph.
                    YAxisValue1 = YAxisValue2; // Assigning y-axis 2nd value to y-axis 1st value so that next line will be continued from where previous line is ended.
                    XAxisValue1 = XAxisValue2; // Assigning x-axis 2nd value to x-axis 1st value so that next line will be continued from where previous line is ended.
                    XAxisValue2 = XAxisValue2 + (GraphOffset * 2) / ScalarSensorData.get(Key).size();
                }
                if(counter!=5) // If counter is not equal to 5, i.e. if this isn't the 5th second, do as below.
                {
                    YAxisValue1 = YAxisValue2; // Assigning y-axis 2nd value to y-axis 1st value so that next line will be continued from where previous line is ended.
                    counter++; // Incrementing counter by 1.
                    keycount++;
                    XAxisValue2 = GraphOffset*2 + GraphOffset*2*keycount;
                }
                else // As the graph reaches 5th Second, reset counter and graph x-axis position.
                {
                    XAxisValue1 = GraphOffset * 2;
                    XAxisValue2 = GraphOffset * 2;
                    YAxisValue1 = YAxisValue2;
                    counter = 1; // Resetting counter to 1.
                    keycount = 0;
                }
            }
        }
        else if(SensorId == 3 || SensorId == 4) // In case vector sensors are selected, graph will be different.
        {
            VectorSensorDataX = M.TimeData2X; // Coping data from MainActivity's vector sensor's HashMap to local HashMap for simplicity.
            VectorSensorDataY = M.TimeData2Y; // Coping data from MainActivity's vector sensor's HashMap to local HashMap for simplicity.
            VectorSensorDataZ = M.TimeData2Z; // Coping data from MainActivity's vector sensor's HashMap to local HashMap for simplicity.
            canvas.drawLine(GraphOffset, getHeight() - getHeight() / 2, getWidth(), getHeight() - getHeight() / 2, AxisColor); // This will draw X-axis on canvas.
            canvas.drawLine(GraphOffset, GraphOffset, GraphOffset, getHeight() - GraphOffset, AxisColor); // This will draw Y-axis on canvas.
            canvas.drawText("0", GraphOffset / 2, getHeight() / 2 + 15, TextColor); // Text "0" below X & Y axis.
            canvas.drawText("Time Axis (in Seconds)   ->", XAxisFontLabelXPosition, XAxisFontLabelYPosition, TextColor); // Text below X-axis.
            float timePoints = getWidth() / 6; // This variable contains 1/5th of total width after deduction of offset from both sides.
            canvas.translate(GraphOffset, 0);
            for (int i = 1; i <= 5; i++) // This loop is to divide x-axis of time into 5 equal parts of total time 5 seconds.
            {
                canvas.drawText(i + "", i * timePoints, getHeight() / 2 + GraphOffset / 2, TextColor); // Text below X-axis.
            }
            for (int j = 0; j < getWidth() / 5; j++) {
                canvas.drawLine(j * GraphOffset, getHeight() / 2 + 10, j * GraphOffset, getHeight() / 2 - 10, AxisColor);
            }
            canvas.translate(-GraphOffset, 0);
            for (int i = 1; i < 12; i++) {
                canvas.drawLine(GraphOffset - 10, i * GraphOffset, GraphOffset + 10, i * GraphOffset, AxisColor);
            }
            XAxisValue1 = GraphOffset; // Initializing X-Axis position.
            YAxisValue1X = getHeight() / 2; // Initializing Y-Axis position.
            YAxisValue1Y = getHeight() / 2; // Initializing Y-Axis position.
            YAxisValue1Z = getHeight() / 2; // Initializing Y-Axis position.

            if (SensorId == 3) // For Gyroscope.
            {
                float YAxis = getHeight() / 6;
                XAxisValue2 = GraphOffset * 3; // Initializing X-Axis position.
                int j = 1;
                for (int i = 2; i >= -2; i--) {
                    if (i < 0)
                        canvas.drawText("(" + i + ")", GraphOffset + 15, j * YAxis + 10, TextColor);
                    else if (i > 0)
                        canvas.drawText(i + "", GraphOffset + 15, j * YAxis + 15, TextColor);
                    j++;
                }
                int counter = 1; // Setting counter to 1.
                for (String Key : VectorSensorDataX.keySet()) {
                    for (int i = 0; i < VectorSensorDataX.get(Key).size(); i++) {
                        if (VectorSensorDataX.get(Key).get(i) >= 0.0f) {
                            YAxisValue2X = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataX.get(Key).get(i)) / 2.0));
                            YAxisValue2Y = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataY.get(Key).get(i)) / 2.0));
                            YAxisValue2Z = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataZ.get(Key).get(i)) / 2.0));
                        } else {
                            YAxisValue2X = (float) (getHeight() / 2 + (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataX.get(Key).get(i)) / 2.0));
                            YAxisValue2Y = (float) (getHeight() / 2 + (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataY.get(Key).get(i)) / 2.0));
                            YAxisValue2Z = (float) (getHeight() / 2 + (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataZ.get(Key).get(i)) / 2.0));
                        }
                        canvas.drawLine(XAxisValue1, YAxisValue1X, XAxisValue2, YAxisValue2X, Color1); // Connecting new points plotted on graph.
                        canvas.drawLine(XAxisValue1, YAxisValue1Y, XAxisValue2, YAxisValue2Y, Color2); // Connecting new points plotted on graph.
                        canvas.drawLine(XAxisValue1, YAxisValue1Z, XAxisValue2, YAxisValue2Z, Color3); // Connecting new points plotted on graph.
                        XAxisValue1 = XAxisValue2;
//                        XAxisValue2 = XAxisValue2+(GraphOffset*2)/VectorSensorDataX.get(Key).size();
                        YAxisValue1X = YAxisValue2X;
                        YAxisValue1Y = YAxisValue2Y;
                        YAxisValue1Z = YAxisValue2Z;
                    }
                    if (counter != 5) {
                        XAxisValue1 = XAxisValue2;
                        XAxisValue2 = XAxisValue2 + GraphOffset * 2;
                        YAxisValue1X = YAxisValue2X;
                        YAxisValue1Y = YAxisValue2Y;
                        YAxisValue1Z = YAxisValue2Z;
                        counter++;
                    }
                    else {
                        XAxisValue1 = GraphOffset;
                        XAxisValue2 = GraphOffset * 3;
                        YAxisValue1X = YAxisValue2X;
                        YAxisValue1Y = YAxisValue2Y;
                        YAxisValue1Z = YAxisValue2Z;
                        counter = 1;
                    }
                }
            }
            else if (SensorId == 4) // For Linear Accelaration.
            {
                float YAxis = getHeight() / 6;
                XAxisValue2 = GraphOffset * 3; // Initializing X-Axis position.
                int j = 1;
                for (int i = 1; i >= -1; i = i-(i/2))
                {
                    if (i < 0)
                        canvas.drawText("(" + i + ")", GraphOffset + 15, j * YAxis + 10, TextColor);
                    else if (i > 0)
                        canvas.drawText(i + "", GraphOffset + 15, j * YAxis + 15, TextColor);
                    j++;
                }
                int counter = 1; // Setting counter to 1.
                for (String Key : VectorSensorDataX.keySet()) {
                    for (int i = 0; i < VectorSensorDataX.get(Key).size(); i++) {
                        if (VectorSensorDataX.get(Key).get(i) >= 0.0f) {
                            YAxisValue2X = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataX.get(Key).get(i)) / 1.0));
                            YAxisValue2Y = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataY.get(Key).get(i)) / 1.0));
                            YAxisValue2Z = (float) (getHeight() / 2 - (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataZ.get(Key).get(i)) / 1.0));
                        } else {
                            YAxisValue2X = (float) (getHeight() / 2 + (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataX.get(Key).get(i)) / 1.0));
                            YAxisValue2Y = (float) (getHeight() / 2 + (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataY.get(Key).get(i)) / 1.0));
                            YAxisValue2Z = (float) (getHeight() / 2 + (((getHeight() / 2 - GraphOffset * 2) * VectorSensorDataZ.get(Key).get(i)) / 1.0));
                        }
                        canvas.drawLine(XAxisValue1, YAxisValue1X, XAxisValue2, YAxisValue2X, Color1); // Connecting new points plotted on graph.
                        canvas.drawLine(XAxisValue1, YAxisValue1Y, XAxisValue2, YAxisValue2Y, Color2); // Connecting new points plotted on graph.
                        canvas.drawLine(XAxisValue1, YAxisValue1Z, XAxisValue2, YAxisValue2Z, Color3); // Connecting new points plotted on graph.
                        XAxisValue1 = XAxisValue2;
//                        XAxisValue2 = XAxisValue2+(GraphOffset*2)/VectorSensorDataX.get(Key).size();
                        YAxisValue1X = YAxisValue2X;
                        YAxisValue1Y = YAxisValue2Y;
                        YAxisValue1Z = YAxisValue2Z;
                    }
                    if (counter != 5) {
                        XAxisValue1 = XAxisValue2;
                        XAxisValue2 = XAxisValue2 + GraphOffset * 2;
                        YAxisValue1X = YAxisValue2X;
                        YAxisValue1Y = YAxisValue2Y;
                        YAxisValue1Z = YAxisValue2Z;
                        counter++;
                    } else {
                        XAxisValue1 = GraphOffset;
                        XAxisValue2 = GraphOffset * 3;
                        YAxisValue1X = YAxisValue2X;
                        YAxisValue1Y = YAxisValue2Y;
                        YAxisValue1Z = YAxisValue2Z;
                        counter = 1;
                    }
                }
            }
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

    private float FindMaxValue()
    {
        List<Float> Max = new ArrayList<>();
        if(SensorId == 1 || SensorId == 2)
        {
            for(String Key:ScalarSensorData.keySet())
            {
                Max.add(Collections.max(ScalarSensorData.get(Key)));
            }
            return Collections.max(Max);
        }
        else if(SensorId == 3 || SensorId == 4)
        {

        }
        return 0.0f;
    }
}
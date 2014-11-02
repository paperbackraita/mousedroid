package com.example.gyrotest;


import java.util.*;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener {
	private TextView tv;
	private SensorManager sManager;	
	private Sensor gyroscope;
	float[] rolls = new float[3];
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscope = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); 
        
    }
    
    @SuppressWarnings("deprecation")
	@Override  
    protected void onResume()  
    {  
        super.onResume();  
        sManager.registerListener(this, gyroscope ,SensorManager.SENSOR_DELAY_FASTEST);
        
    }  
  
    @Override  
    protected void onStop(){
        sManager.unregisterListener(this);  
        super.onStop();  
    }  
  
    @Override  
    public void onAccuracyChanged(Sensor arg0, int arg1)  
    {  
    } 
   
    @Override  
    public void onSensorChanged(SensorEvent event)  
    {  
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
    	{
        	if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)  
        	{  
        		return;  
        	}  
        	else{  
        		if(Math.abs(Math.abs(rolls[0])-Math.abs(event.values[2])) > 2.5)
        			rolls[0]=event.values[2];
        		if(Math.abs(Math.abs(rolls[1])-Math.abs(event.values[1])) > 2.5)
        			rolls[1]=event.values[1];
        		if(Math.abs(Math.abs(rolls[2])-Math.abs(event.values[0])) > 2)
        			rolls[2]=event.values[0];
        		tv.setText("Orientation X :"+ Float.toString(rolls[0]) +"\n"+  
        				"Orientation Y :"+ Float.toString(rolls[1]) +"\n"+  
        				"Orientation Z :"+ Float.toString(rolls[2]));
        		
        		/*tv.setText("Orientation X :"+ Float.toString(event.values[2]) +"\n"+  
        				"Orientation Y :"+ Float.toString(event.values[1]) +"\n"+  
        				"Orientation Z :"+ Float.toString(event.values[0]));*/
        	}
    	}
    }  


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.gyrotest;

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
    	{if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)  
        {  
            return;  
        }  
  
        tv.setText("Orientation X :"+ Float.toString(event.values[2]) +"\n"+  
                   "Orientation Y :"+ Float.toString(event.values[1]) +"\n"+  
                   "Orientation Z :"+ Float.toString(event.values[0]));
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

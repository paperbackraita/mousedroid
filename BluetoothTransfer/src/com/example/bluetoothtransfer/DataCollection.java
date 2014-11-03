package com.example.bluetoothtransfer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class DataCollection extends Service implements SensorEventListener {
	private SensorManager sManager;
	private Sensor gyroscope;
	private IBinder mBinder = new LocalBinder();
	private float[] values = new float[3];
	
	public void onCreate() {
		sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		gyroscope = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}
	
	/**
	 * Do nothing!
	 */
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		return;
	}
	
	/**
	 * Set the float values.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)
    	{
        	if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)  
        	{  
        		return;  
        	}  
        	else{  
        		values = event.values;
        	}
    	}
	}
	
	/**
	 * Called on Bind, registers the listener and returns the binder.
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		sManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
		return mBinder;
	}
	
	/**
	 * Get the data.
	 * @return Hardware Data.
	 */
	public float[] getData() {
		return this.values;
	}
	
	/**
	 * Local extension of the Binder Class.
	 * @author sukrit
	 */
	public class LocalBinder extends Binder {
		/**
		 * Return instance of DataCollection so clients can call public methods.
		 * @return DataCollection
		 */
		DataCollection getService() {
			return DataCollection.this;
		}
	}

}

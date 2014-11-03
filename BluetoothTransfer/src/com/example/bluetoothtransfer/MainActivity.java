package com.example.bluetoothtransfer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import com.example.bluetoothtransfer.DataCollection.LocalBinder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	DataCollection dataCollection;
	BluetoothAdapter adapter; // The default adapter.
	TextView view = null; // For errors.
	final int REQUEST_ENABLE_BT_SUCCESS = 1; // Successfull Bluetooth enable.
	private final UUID mouseDroidUUID = UUID.fromString("3DD7E793-C461-4FAE-B715-12E8940A0975");
		// UUID for Bluetooth connections.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dataCollection = new DataCollection();
		dataCollection.run();
		
		// Find the Bluetooth Adapter.
		adapter = BluetoothAdapter.getDefaultAdapter();
		view = (TextView) findViewById(R.id.textView1);
		// Does Bluetooth exist?
		if (adapter == null) {
			// Adapter was not found. No Bluetooth possibly.
			view.setText("No Bluetooth Device was found.");
		}
		else {
			// Is Bluetooth Enabled? If not, enable.
			if (!adapter.isEnabled()) {
				view.setText("Bluetooth is not enabled. Enabling ...");
				Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT_SUCCESS);
			}
			
			// Get the Paired Devices.
			Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
			while (pairedDevices.size() == 0) {
				pairedDevices = adapter.getBondedDevices();
			}
			
			BluetoothDevice pairedDevice = null;
			// Select the correct paired device. 
			// TODO :: Provide user with a list of devices and choose to connect to one.
			if (pairedDevices.size() > 0) {
				pairedDevice = (BluetoothDevice) pairedDevices.toArray()[0];
			}
			
			// Did such a BT device exist?
			if (pairedDevice == null) {
				view.setText("No paired device was found ...");
			} else {
				view.setText("Using " + pairedDevice.getName() + " as the device.");
			}
	
			// Connect to the device.
			ConnectToDevice connect = new ConnectToDevice(pairedDevice);
			connect.run();
			connect.disconnect();
			
		}
	}
	
	/**
	 * Inner class to connect to a device off of the main UI thread.
	 * @author sukrit
	 */
	private class ConnectToDevice extends Thread {
		
		private BluetoothSocket socket; // Socket for the communication.
		
		/**
		 * Constructor which requires an instance of the device to connect to.
		 * @param pairedDevice The device to connect to.
		 */
		public ConnectToDevice(BluetoothDevice pairedDevice) {
			try {
				socket = pairedDevice.createRfcommSocketToServiceRecord(mouseDroidUUID);
			} catch (IOException e) {
				view.setText("Error connecting to the paired device: " + e);
			}
		}
		
		/**
		 * Tries connecting to the device. If error, returns.
		 */
		public void run() {
			try {
				socket.connect();
			} catch (IOException err) {
				view.setText("Error making a connection: " + err);
			}
			AfterConnect connected = new AfterConnect(socket);
			connected.run();
		}
		
		/**
		 * Disconnects the socket.
		 */
		public void disconnect() {
			try {
				socket.close();
			} catch (IOException err) {
				view.setText("Error closing the socket: " + err);
			}
		}
	}
	
	private class AfterConnect extends Thread {
		private OutputStream toServer;
		
		public AfterConnect(BluetoothSocket socket) {
			toServer = null;
			try {
				toServer = socket.getOutputStream();
			} catch (IOException err) {
				view.setText("Error getting Output Stream: " + err);
			}
		}
		
		public void run() {
			try {
				while (true) {
					float[] values = dataCollection.getData();
					String toWrite = Arrays.toString(values);
					toServer.write(toWrite.getBytes());
				}
			} catch (IOException err) {
				view.setText("Error writing to the client: " + err);
			}
		}
	}
	
	private class DataCollection extends Thread implements SensorEventListener {
		private SensorManager sManager;
		private Sensor gyroscope;
		private float[] values = new float[3];
		
		public void run() {
			sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			gyroscope = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			sManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		public float[] getData() {
			return values;
		}
		
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			return;
		}

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
		
	}
	
	// Helper Functions.
	private void listPairedDevices(TextView view, Set<BluetoothDevice> pairedDevices) {
		StringBuilder devices = new StringBuilder();
		for (BluetoothDevice device : pairedDevices) {
			devices.append(device.getName());
			devices.append('\n');
		}
		view.setText(devices.toString());
	}
}

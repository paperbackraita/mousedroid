package com.example.bluetoothtransfer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	BluetoothAdapter adapter; // The default adapter.
	final int REQUEST_ENABLE_BT_SUCCESS = 1; // Successfull Bluetooth enable.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Find the Bluetooth Adapter.
		adapter = BluetoothAdapter.getDefaultAdapter();
		TextView view = (TextView) findViewById(R.id.textView1);
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
			
		}
	}
	
	/**
	 * Inner class to connect to a device off of the main UI thread.
	 * @author sukrit
	 */
	private class ConnectToDevice extends Thread {
		// Send data serially to the device. Standard String for serial communication.
		private final UUID serialUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
		private BluetoothSocket socket; // Socket for the communication.
		private final int PORT = 20;
		
		/**
		 * Constructor which requires an instance of the device to connect to.
		 * @param pairedDevice The device to connect to.
		 */
		public ConnectToDevice(BluetoothDevice pairedDevice) {
			socket = null;
			Method m = null;
			
			try {
				m = pairedDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
			} catch (NoSuchMethodException e) {
				return;
			}
			
			try {
				socket = (BluetoothSocket) m.invoke(pairedDevice, PORT);
			} catch (IllegalAccessException e) {} 
			catch (IllegalArgumentException e) {} 
			catch (InvocationTargetException e) {}
		}
		
		/**
		 * Tries connecting to the device. If error, returns.
		 */
		public void run() {
			try {
				socket.connect();
			} catch (IOException err) {
				return;
			}
			AfterConnect connected = new AfterConnect(socket);
			connected.run();
		}
	}
	
	private class AfterConnect extends Thread {
		private OutputStream toServer;
		
		public AfterConnect(BluetoothSocket socket) {
			toServer = null;
			try {
				toServer = socket.getOutputStream();
			} catch (IOException err) {
				// Do nothing.
			}
		}
		
		public void run() {
			try {
				// TODO :: Insert the Data Collection part and send the data in a loop, (with some fixed frequency?)
				toServer.write("Sukrit".getBytes());
			} catch (IOException err) {
				// Do nothing.
			}
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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

package com.example.mc_project;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.graphics.Color;
import android.app.Activity;
import android.app.ProgressDialog;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	 ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
	
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {	    	
	        String action = intent.getAction();
	        
	        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	        	final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
	        	 
	        	if (state == BluetoothAdapter.STATE_ON) {
	        		showToast("Enabled");
	        		 
	        		//showEnabled();
	        	 }
	        } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	mDeviceList = new ArrayList<BluetoothDevice>();
				
				//mProgressDlg.show();
	        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	//mProgressDlg.dismiss();
	        	
	        	//Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
	        	
	        	//newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
				
				//startActivity(newIntent);
	        	
	        	TextView devicelist_textview = (TextView) findViewById(R.id.discovered_Devices_Text_view);
	        	devicelist_textview.setText(mDeviceList.toString()); 
	        	
	        	
	        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	        	BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        
	        	mDeviceList.add(device);
	        	
	        	showToast("Found device " + device.getName());
	        }
	    }
	};
	
	
	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}	
	
	
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
              
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        //BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        
        if(bluetooth != null)
        {
            // Continue with bluetooth setup.
        	if (bluetooth.isEnabled()) {
        	    // Enabled. Work with Bluetooth.
        		TextView devicelist_textview = (TextView) findViewById(R.id.discovered_Devices_Text_view);
            	devicelist_textview.setText("Bluetooth is on and searching"); 
        		IntentFilter filter = new IntentFilter();
        		filter.addAction(BluetoothDevice.ACTION_FOUND);
        		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        		registerReceiver(mReceiver,filter);
        		filter.addAction(BluetoothDevice.ACTION_FOUND);
        		bluetooth.startDiscovery();
        	}
        	else
        	{
        	    // Disabled. Do something else.
        		TextView devicelist_textview = (TextView) findViewById(R.id.discovered_Devices_Text_view);
            	devicelist_textview.setText("Bluetooth is off"); 
        	}
        }
        else
        {
        	TextView devicelist_textview = (TextView) findViewById(R.id.discovered_Devices_Text_view);
        	devicelist_textview.setText("Bluetooth not found"); 
        }
        
        
    }
    
    
    
    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);         
        super.onDestroy();
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
}

package com.example.esc2013sensorapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.net.Uri;


public class SensorListActivity extends Activity implements SensorEventListener{

	final Context context = this;
	final SensorEventListener listener = this; 
	EditText box; 

	private static final String DEBUG = "SensorApp";
	private static final int NO_LISTENER = -1;
	private static final float RADS_TO_DEGREES = 57.2957795f;
	private static final String VIRTUAL_ORIENTATION = "Virtual Orientation Example";
	private SensorManager sensorManager;
	private ListView listView ;
	private ArrayAdapter<String> adapter ;
	private int CurrentDialogPosition = NO_LISTENER;
	
	private byte flag = 0;
	private float[] Orientation = new float[16], RotationMatrix = new float[16];
	private float[] accel = new float[3], geomagnetic = new float[3];


	List<Sensor> sensorlist;
	List<String> sensorView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_list);
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		listView = (ListView) findViewById(R.id.sensorlist);
		ArrayList<String> nameList = new ArrayList<String>();		
		

	    adapter = new ArrayAdapter<String>(this,
				  R.layout.simplerow, nameList);
	    
		sensorlist = sensorManager.getSensorList(Sensor.TYPE_ALL);		
	    Sensor sensor; 
	    
	    int i = 0;
	    while(i < sensorlist.size() )
	    {
	  	  String print = "";
	  	  
	  	  sensor = (Sensor) sensorlist.get(i);
	  	  print += sensor.getName();
	  	  Log.i(DEBUG, print);
	  	  
	  	  adapter.add(print);
	  	  i++;
	  	  
	    }

	    //Add in virtual orientation example
	    adapter.add(VIRTUAL_ORIENTATION);
	    
		// Assign adapter to ListView
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(
		        new android.widget.AdapterView.OnItemClickListener()
		        {

					@Override
					public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
					{
						SetupDialog(position);
					}
					
		         }
		     );
		        
	}

	//Method to setup dialog for each sensor.
	//Sets up 4 sample rate buttons and a cancel button
	private void SetupDialog(int position)
	{
		CurrentDialogPosition = position;
		
		// custom dialog
		final Dialog dialog = new Dialog(context);
		
		dialog.setContentView(R.layout.sensor_data_dialog);
		box  = (EditText) dialog.findViewById(R.id.editText1);
		dialog.setTitle("Activate "+ adapter.getItem(position));

		// set the custom dialog components - text, image and button
		ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);
		image.setImageResource(R.drawable.ic_launcher);
	
		// set the custom dialog components - text, image and button
			Button dialogButtonN = (Button) dialog.findViewById(R.id.buttonNormal);
			
		// if button is clicked, initiate normal sampling
		dialogButtonN.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CurrentDialogPosition > NO_LISTENER)
				{//select 
					
					sensorManager.unregisterListener(listener);
					while (! SetupSensorListener(CurrentDialogPosition, SensorManager.SENSOR_DELAY_NORMAL))
						Log.e(DEBUG, "Fail to register for Sensor");
				}
			}
		});
		
		Button dialogButtonU = (Button) dialog.findViewById(R.id.buttonUI);
		// if button is clicked, initiate UI sampling
		dialogButtonU.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CurrentDialogPosition > NO_LISTENER)
				{//select 
					sensorManager.unregisterListener(listener);
					while (! SetupSensorListener(CurrentDialogPosition, SensorManager.SENSOR_DELAY_UI))
						Log.e(DEBUG, "Fail to register for Sensor");
				}
			}
		});

		Button dialogButtonG = (Button) dialog.findViewById(R.id.buttonGaming);
		// if button is clicked, initiate game sampling
		dialogButtonG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CurrentDialogPosition > NO_LISTENER)
				{//select 
					sensorManager.unregisterListener(listener);
					while (! SetupSensorListener(CurrentDialogPosition, SensorManager.SENSOR_DELAY_GAME))
						Log.e(DEBUG, "Fail to register for Sensor");
				}
			}
		});
		
		Button dialogButtonF = (Button) dialog.findViewById(R.id.buttonFastest);
		
		// if button is clicked, initiate fast sampling
		dialogButtonF.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (CurrentDialogPosition > NO_LISTENER)
				{//select 
					sensorManager.unregisterListener(listener);
					while (! SetupSensorListener(CurrentDialogPosition,  SensorManager.SENSOR_DELAY_FASTEST))
						Log.e(DEBUG, "Fail to register for Sensor");
					
				}
			}
		});
		
		

		Button dialogButtonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
		
		// if button is clicked, close the custom dialog
		dialogButtonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//destroy all listeners 
				sensorManager.unregisterListener(listener);
				CurrentDialogPosition = NO_LISTENER;
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	//setups up sensor listener using pointer to SensorList and an inputed Sampling Rate
	//returns true if successful
	private boolean SetupSensorListener(int SensorListIndex, int SamplingRate)
	{
		if(adapter.getItem(SensorListIndex) == VIRTUAL_ORIENTATION)
		{
			Log.e(DEBUG, "Our orientation sensor");
			
			if (! sensorManager.registerListener(listener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SamplingRate))
			{
				Log.e(DEBUG, "Failed to register ACCEL");
				sensorManager.unregisterListener(listener);
				return false;
			}
			if (! sensorManager.registerListener(listener,
					sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SamplingRate))
			{
				Log.e(DEBUG, "Failed to register MAG");
				sensorManager.unregisterListener(listener);
				return false;
			}
			
		}
		
		else if (!sensorManager.registerListener(listener,
			sensorlist.get(SensorListIndex), SamplingRate))
		{
			Log.e(DEBUG, "Failed to register sensor");
			sensorManager.unregisterListener(listener);
			return false;
		}	
		
		return true;
		
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.activity_sensor_list);
        finish();
    }
    
    @Override
    protected void onPause() {
      // unregister listener
      super.onPause();
      sensorManager.unregisterListener(this);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sensor_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_email:
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto","spam@rebelbot.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Rebelbot Sensor App");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
				return true;
			case R.id.menu_website:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://rebelbot.com"));
				startActivity(browserIntent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	//update the text field
    private void UpdateBox(float[] values, int num) 
    {
    	if (box == null)
    	{
    		Log.e(DEBUG, "box is null!!!");
    		return;
    	}
        // Movement
        String str = ""; 
        int i = 0;
        
        while( i < num)
        {
        	str += String.format("%.4f", values[i++]);
        	if (i < num)
        		str += ", "; 
        }
        
        box.setText(str);
        

    }	
	
    @Override
    public void onSensorChanged(SensorEvent event) {
      
		float[] values = event.values;
		
		if((CurrentDialogPosition == NO_LISTENER) || (CurrentDialogPosition > adapter.getCount()))
			return; //no valid sensor dialog
    	//if desire orientation sensor data
    	if(adapter.getItem(CurrentDialogPosition) == VIRTUAL_ORIENTATION)
    	{
    		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
    		{
    			//save data
    			accel = Arrays.copyOf( values, values.length);
    			flag |= 0x1; //raise flag for data
    		}
    		else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
    		{
    			//save data
    			geomagnetic = Arrays.copyOf( values, values.length);
    			flag |= 0x2; //raise flag for data
    		}
    		
    		//if have both data process orientation
    		if(flag == 0x3)
    		{ //finish processing orientation sensor fusion per Android documentation.
    			SensorManager.getRotationMatrix(RotationMatrix, null, accel, geomagnetic);
    			SensorManager.getOrientation(RotationMatrix, Orientation);
    			ConvertToDegrees(Orientation);
    			UpdateBox(Orientation, 3);
    			flag = 0;
    		}
    	}
    	else 
    	{
    		
	    	switch(event.sensor.getType())
	    	{
	    	case Sensor.TYPE_AMBIENT_TEMPERATURE:
	    		UpdateBox(values, 1);
	    		break;
	    	case Sensor.TYPE_ACCELEROMETER:
	    		UpdateBox(values, 3);
	    		break;    		
	    	case Sensor.TYPE_GRAVITY:
	    		UpdateBox(values, 3);
	    		break; 
	    	case Sensor.TYPE_GYROSCOPE:
	    		UpdateBox(values, 3);
	    		break;
	    	case Sensor.TYPE_LIGHT:
	    		UpdateBox(values, 1);
	    		break;    		
	    	case Sensor.TYPE_ORIENTATION:
	    		UpdateBox(values, 3);
	    		break;  
	    	case Sensor.TYPE_LINEAR_ACCELERATION:
	    		UpdateBox(values, 3);
	    		break; 
	    	case Sensor.TYPE_MAGNETIC_FIELD:
	    		UpdateBox(values, 3);
	    		break;    		
	    	case Sensor.TYPE_PRESSURE:
	    		UpdateBox(values, 1);
	    		break; 
	    	case Sensor.TYPE_PROXIMITY:
	    		UpdateBox(values, 1);
	    		break;
	    	case Sensor.TYPE_RELATIVE_HUMIDITY:
	    		UpdateBox(values, 1);
	    		break;    		
	    	case Sensor.TYPE_ROTATION_VECTOR:
	    		UpdateBox(values, 3);
	    		break;  		
	    	}
    	}
    	


    }

	private void ConvertToDegrees(float[] arrayRads) {
		
		for(int i = 0; i < arrayRads.length; i++)
			arrayRads[i] *= RADS_TO_DEGREES;
	}


}

package com.darkever.airportweather;

/*
 * Here is my application. It fetches the weather info using JSON, updates and displays in the UI, 
 * and stores last entered airport code so it can be brought up in next launch. 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShowWeather extends Activity 
{
	private OnClickListener airportButtonListener = new OnClickListener() 
    {
        public void onClick(View view) 
        {		
        	EditText airportCode = (EditText)findViewById(R.id.AirportCode);
        	
        	fetchAirportWeather(airportCode.getText().toString());  
        	
        	SharedPreferences myPref = getPreferences(MODE_PRIVATE);
            String key = getString(R.string.key_airportcode);
            myPref.edit().putString(key, airportCode.getText().toString());
		}
    };
    	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        
        // Create button and set up listener
        Button button = (Button)findViewById(R.id.GetWeather);        
        button.setOnClickListener(airportButtonListener);        
        
        // Load previous preferences. If none exist use default (KEWR, Newark).
        SharedPreferences myPref = getPreferences(MODE_PRIVATE);
        String key = getString(R.string.key_airportcode);
        String defaultAirportCode = getString(R.string.default_airportcode);
        String airportCode = myPref.getString(key,defaultAirportCode);
        
        fetchAirportWeather(airportCode);
    }
    
    /*
     * At first I thought about doing a Service to do the fetchAirportWeather but given the size of the application
     * and the level of communication it was doing I felt it was overkill. I also wanted to return the application in a timely
     * fashiion. 
     */
    
    public void fetchAirportWeather(String airportCode)
    {
    	try {
			URL geonames = new URL("http://ws.geonames.org/weatherIcaoJSON?ICAO="+airportCode);
			URLConnection geonamesConnection = geonames.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(geonamesConnection.getInputStream()));
 
			String line;
			while ((line = in.readLine()) != null) 
			{
				JSONObject jo = (JSONObject)(new JSONObject(line)).get(this.getString(R.string.json_weather_observation));
				String s = jo.getString(getString(R.string.json_temperature));
				((TextView)findViewById(R.id.Temperature)).setText(s+" c");
				s = jo.getString(getString(R.string.json_clouds));
				((TextView)findViewById(R.id.Clouds)).setText(s);				
				s = jo.getString(getString(R.string.json_airport_name));
				((TextView)findViewById(R.id.AirportName)).setText(s);
				int i = jo.getInt(getString(R.string.json_humidity));				
				((TextView)findViewById(R.id.Humidity)).setText(i+"%");
			}
		/* 
		 * I put a simple Toast message to display any error just to recognize it's existence. Next step would be to
		 * to attempt to figure out what error and post a proper message.  
		*/	
		} catch (MalformedURLException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
			toast.show();
		} catch (IOException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
			toast.show();
		} catch (JSONException e) {
			Toast toast = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
			toast.show();
		}
    }
}
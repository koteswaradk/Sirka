package com.yoobikwiti.sirka;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RouteSearchActivity extends FragmentActivity implements OnClickListener,
		OnLocationChangedListener,
		OnMapReadyCallback {
	double latitude, longitude;
	private LocationManager locationManager;
	final int RQS_GooglePlayServices = 1;
	Location location;
	AutoCompleteTextView atvPlaces, endPlaces;
	double srcLat, srcLang, desLat, desLang;
	AtvPlacesTask atvplacesTask;
	AtvParserTask atvparserTask;
	EndPlacesTask endplacesTask;
	EndParserTask endparserTask;
	String result = null, starting_address, end_address, surce_marker, destination_marker;
	ImageButton atv_clear, end_clear, start_navigation, stop_avigation;

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.searchroute);
		atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
		endPlaces = (AutoCompleteTextView) findViewById(R.id.end_places);
		atv_clear = (ImageButton) findViewById(R.id.atv_clear);
		end_clear = (ImageButton) findViewById(R.id.end_clear);
		findViewById(R.id.start_navigation).setOnClickListener(this);
		atv_clear.setOnClickListener(this);
		end_clear.setOnClickListener(this);
		atvPlaces.setThreshold(3);
		endPlaces.setThreshold(3);
		LocationListener locListener = new RouteDisplayActivity();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location From GPS
		 location = locationManager.getLastKnownLocation(provider);
		
		atvPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				atvplacesTask = new AtvPlacesTask();
				atvplacesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		endPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				endplacesTask = new EndPlacesTask();
				endplacesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String current_address=null;
		switch (v.getId()) {
		case R.id.start_navigation:
			try{
				CheckInternetConnection connection_ok=new CheckInternetConnection(this);
				if(connection_ok.CheckInternet()){
				if (endPlaces.getText().length()<=0) {
					endPlaces.setError("Please Select Destination");
				}else
				if (atvPlaces.getText().length()<=0) {
					current_address=getLocationAddress(location.getLatitude(), location.getLongitude());
					//atvPlaces.setText(getLocationAddress(location.getLatitude(), location.getLongitude()));
					atvPlaces.setText("null");
					//Log.i("current address", address);
				}
				
				//Log.i("current address", address);
				
				/*LatLng srcLatLng = new LatLng(srcLat, srcLang);
				LatLng destLatLng = new LatLng(desLat, desLang);*/
				
				/*ConnectAsyncTask _connectAsyncTask = new ConnectAsyncTask();
				_connectAsyncTask.execute();*/
			/*	geoCodingSRC(atvPlaces.getText().toString());

				geoCodingDest(endPlaces.getText().toString());*/
				/*if (atvPlaces.getText().toString()!=null) {
					
				}*/
				if (!(endPlaces.getText().length()<=0&&atvPlaces.getText().length()<=0)) {
					Intent i=new Intent(RouteSearchActivity.this, RouteDisplayActivity.class);
					Bundle data=new Bundle();
					
					data.putString("source_address", atvPlaces.getText().toString());
					data.putString("destination_address", endPlaces.getText().toString());
					data.putBoolean("data_stored", true);
					i.putExtras(data);
					
					startActivity(i);
					atvPlaces.setText(atvPlaces.getText().toString());
					endPlaces.setText(endPlaces.getText().toString());
					endPlaces.setText("");
					atvPlaces.setText("");
				}else{
					//Toast.makeText(this, "",Toast.LENGTH_LONG ).show();
				}
				
				
				}else{
					Toast.makeText(this, "check your Internet",Toast.LENGTH_LONG ).show();
				}
				}catch (NullPointerException e) {
					// TODO: handle exception
					Toast.makeText(this, "Server Loading",Toast.LENGTH_LONG ).show();
				}catch (IndexOutOfBoundsException e) {
					// TODO: handle exception
					Toast.makeText(this, "please select from list",Toast.LENGTH_LONG ).show();
				}
			break;
		case R.id.atv_clear:
			atvPlaces.setText("");
			break;
			
		case R.id.end_clear:
			endPlaces.setText("");
			break;

		}
		}
		

	private String getLocationAddress(double latitude, double longitude) {

	
	try {
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> addressList = geocoder.getFromLocation(latitude,
				longitude, 1);
		if (addressList != null && addressList.size() > 0) {
			Address address = addressList.get(0);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				sb.append(address.getAddressLine(i)).append("\n");
			}
			/*
			 * sb.append(address.getLocality()).append("\n");
			 * sb.append(address.getPostalCode()).append("\n");
			 */
			sb.append(address.getCountryName());
			result = sb.toString();
			
			// s_address=result;
			Log.i("address", "" + result);
			Log.i("currently getting latitude ", "" + latitude + ""+ longitude);
		}
	} catch (IOException e) {
		// Log.e("GetLocation Address", "Unable connect to Geocoder", e);
	} catch (NullPointerException e) {
		// TODO: handle exception
		Toast.makeText(this, "some problem with google server",
				Toast.LENGTH_SHORT).show();
	}
	return result;
}
	private void geoCodingSRC(String srclocation) {
		Geocoder gc = new Geocoder(this);

		if (Geocoder.isPresent()) {
			List<Address> list = null;
			try {
				list = gc.getFromLocationName(srclocation, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Address address = list.get(0);

			srcLat = address.getLatitude();
			
			srcLang = address.getLongitude();
			

		}
	}
	private void geoCodingDest(String deslocation) {
		Geocoder gc = new Geocoder(this);

		if (Geocoder.isPresent()) {
			List<Address> list = null;
			try {
				list = gc.getFromLocationName(deslocation, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Address address = list.get(0);

			desLat = address.getLatitude();
			
			desLang = address.getLongitude();
			

		}
	}
	/** A method to download json data from url for source */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}
	// Fetches all places from GooglePlaces AutoComplete Web Service for source
		private class AtvPlacesTask extends AsyncTask<String, Void, String> {

			
			@Override
			protected String doInBackground(String... place) {
				// For storing data from web service
				String data = "";

				// Obtain browser key from https://code.google.com/apis/console
				//String key = "key=AIzaSyBJKgvXv2Hg76Vhf2Nw5vMW-bsaXFdTrjA";
				String key ="key=AIzaSyDZ1wcg5oEaJibRxZIpjLu5KvwxASIaNUk";
				String input = "";

				try {
					input = "input=" + URLEncoder.encode(place[0], "utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				// place type to be searched
				String types = "types=geocode";

				// Sensor enabled
				String sensor = "sensor=false";

				// Building the parameters to the web service
				String parameters = input + "&" + types + "&" + sensor + "&" + key;

				// Output format
				String output = "json";

				// Building the url to the web service
				String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
						+ output + "?" + parameters;

				try {
					// Fetching the data from web service in background for source
					data = downloadUrl(url);
				} catch (Exception e) {
					Log.d("Background Task", e.toString());
				}
				return data;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				// Creating ParserTask
				atvparserTask = new AtvParserTask();

				// Starting Parsing the JSON string returned by Web Service for
				// source
				atvparserTask.execute(result);
			}
		}

		/** A class to parse the Google Places in JSON format */
		private class AtvParserTask extends
				AsyncTask<String, Integer, List<HashMap<String, String>>> {

			JSONObject jObject;

			@Override
			protected List<HashMap<String, String>> doInBackground(
					String... jsonData) {

				List<HashMap<String, String>> places = null;

				PlaceJSONParser placeJsonParser = new PlaceJSONParser();

				try {
					jObject = new JSONObject(jsonData[0]);

					// Getting the parsed data as a List construct
					places = placeJsonParser.parse(jObject);

				} catch (Exception e) {
					Log.d("Exception", e.toString());
				}
				return places;
			}

			@Override
			protected void onPostExecute(List<HashMap<String, String>> result) {

				String[] from = new String[] { "description" };
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
						android.R.layout.simple_list_item_1, from, to);

				// Setting the adapter
				atvPlaces.setAdapter(adapter);
			}
		}

		// Fetches all places from GooglePlaces AutoComplete Web Service for source
		private class EndPlacesTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... place) {
				// For storing data from web service
				String data = "";

				// Obtain browser key from https://code.google.com/apis/console
				//String key = "key=AIzaSyBJKgvXv2Hg76Vhf2Nw5vMW-bsaXFdTrjA";
				String key ="key=AIzaSyDyzIN2IfNTR48qCKDNB3r33H3dBqKvIpU";
				String input = "";

				try {
					input = "input=" + URLEncoder.encode(place[0], "utf-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				// place type to be searched
				String types = "types=geocode";

				// Sensor enabled
				String sensor = "sensor=false";

				// Building the parameters to the web service
				String parameters = input + "&" + types + "&" + sensor + "&" + key;

				// Output format
				String output = "json";

				// Building the url to the web service
				String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
						+ output + "?" + parameters;

				try {
					// Fetching the data from web service in background for source
					data = downloadUrl(url);
				} catch (Exception e) {
					Log.d("Background Task", e.toString());
				}
				return data;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				// Creating ParserTask
				endparserTask = new EndParserTask();

				// Starting Parsing the JSON string returned by Web Service for
				// source
				endparserTask.execute(result);
			}
		}

		/** A class to parse the Google Places in JSON format */
		private class EndParserTask extends
				AsyncTask<String, Integer, List<HashMap<String, String>>> {

			JSONObject jObject;

			@Override
			protected List<HashMap<String, String>> doInBackground(
					String... jsonData) {

				List<HashMap<String, String>> places = null;

				PlaceJSONParser placeJsonParser = new PlaceJSONParser();

				try {
					jObject = new JSONObject(jsonData[0]);

					// Getting the parsed data as a List construct
					places = placeJsonParser.parse(jObject);

				} catch (Exception e) {
					Log.d("Exception", e.toString());
				}
				return places;
			}

			@Override
			protected void onPostExecute(List<HashMap<String, String>> result) {

				String[] from = new String[] { "description" };
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
						android.R.layout.simple_list_item_1, from, to);

				// Setting the adapter
				endPlaces.setAdapter(adapter);
			}
		}
/*	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode == ConnectionResult.SUCCESS) {
			
			 * Toast.makeText(getApplicationContext(),
			 * "isGooglePlayServicesAvailable SUCCESS",
			 * Toast.LENGTH_LONG).show();
			 
		} else {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					RQS_GooglePlayServices);
		}
		

	}*/
	@Override
	public void onMapReady(GoogleMap arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
	private class ConnectAsyncTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(RouteSearchActivity.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			geoCodingSRC(atvPlaces.getText().toString());

			geoCodingDest(endPlaces.getText().toString());
			/*if (atvPlaces.getText().toString()!=null) {
				
			}*/
			
			
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			Intent i=new Intent(RouteSearchActivity.this, RouteDisplayActivity.class);
			Bundle data=new Bundle();
			
			data.putString("source_address", atvPlaces.getText().toString());
			data.putString("destination_address", endPlaces.getText().toString());
			data.putBoolean("data_stored", true);
			i.putExtras(data);
			progressDialog.dismiss();
		     //  finish();
			startActivity(i);
			atvPlaces.setText("");
			endPlaces.setText("");
			
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode == ConnectionResult.SUCCESS) {
			/*
			 * Toast.makeText(getApplicationContext(),
			 * "isGooglePlayServicesAvailable SUCCESS",
			 * Toast.LENGTH_LONG).show();
			 */
		} else {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					RQS_GooglePlayServices);
		}
		turnGPSOff(RouteSearchActivity.this);

		
		

	}
	public static void turnGPSOff(Context context)
	{
	    String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	    if (provider.contains("gps"))
	    { //if gps is enabled
	        final Intent poke = new Intent();
	        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
	        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
	        poke.setData(Uri.parse("3")); 
	        context.sendBroadcast(poke);
	    }
	}
	
	
}

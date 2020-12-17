package com.yoobikwiti.sirka;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class SearchRouteActivity extends FragmentActivity implements
		OnLocationChangedListener, LocationListener, OnClickListener,
		OnMapReadyCallback {

	String Tag = "inside route drawer";
	ImageButton atv_clear, end_clear, start_navigation, stop_avigation;
	boolean flag = false, preference_stored = false;
	double srcLat, srcLang, desLat, desLang;
	SupportMapFragment fm;
	double latitude, longitude;
	private LocationManager locationManager;
	final int RQS_GooglePlayServices = 1;
	Location location;
	AutoCompleteTextView atvPlaces, endPlaces;
	String result = null, starting_address, end_address, surce_marker, destination_marker;
	AtvPlacesTask atvplacesTask;
	AtvParserTask atvparserTask;
	EndPlacesTask endplacesTask;
	EndParserTask endparserTask;
	private GoogleMap googleMap;
	MarkerOptions markerOptions;
	RelativeLayout layout_dist_duration;
	TextView distance_text, duration_text;
	public static final String MyPREFERENCES = "srcdest";
	//SharedPreferences sharedpreferences;
	RouteSessionManager routesession;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

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

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.searchroute);

		atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
		endPlaces = (AutoCompleteTextView) findViewById(R.id.end_places);
		start_navigation = (ImageButton) findViewById(R.id.start_navigation);
		layout_dist_duration = (RelativeLayout) findViewById(R.id.duration_distance_display);
		distance_text = (TextView) findViewById(R.id.distance_text);
		duration_text = (TextView) findViewById(R.id.duration_text);
		atv_clear = (ImageButton) findViewById(R.id.atv_clear);
		end_clear = (ImageButton) findViewById(R.id.end_clear);
		// stop_avigation=(ImageButton) findViewById(R.id.stop_navigation);

		end_clear.setOnClickListener(this);
		atv_clear.setOnClickListener(this);
		// stop_avigation.setOnClickListener(this);
		start_navigation.setOnClickListener(this);
		atvPlaces.setThreshold(2);
		endPlaces.setThreshold(2);
		/*MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		googleMap.setMyLocationEnabled(true);
		googleMap.getCameraPosition();*/


		// Getting LocationManager object from System Service LOCATION_SERVICE
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener = new SearchRouteActivity();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locListener);
		/*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
				PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
						PackageManager.PERMISSION_GRANTED) {
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		} else {
			Toast.makeText(this, "map permission error", Toast.LENGTH_LONG).show();
		}*/
		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location From GPS
		location = locationManager.getLastKnownLocation(provider);

		/*if (location != null) {
			onLocationChanged(location);
		}*/
//		LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
		/*googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));*/
		locationManager.requestLocationUpdates(provider, 2000, 0, this);
		/*googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));*/
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);

		/*Location lastKnownLocation = locationManager
				.getLastKnownLocation(locationManager.getBestProvider(criteria,
						true));*/
		//sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		markerOptions = new MarkerOptions();
		routesession = new RouteSessionManager(getApplicationContext());
		if (routesession.isRouteDisplaed()) {
			Log.i("in side if case", "login true");
		}
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

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	/**
	 * A method to download json data from url for source
	 */
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

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"SearchRoute Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.yoobikwiti.sirka/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"SearchRoute Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.yoobikwiti.sirka/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service for source
	private class AtvPlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyD3nAASUJ20XQ4ToaLWe10SZ_6j6MKXpm8";

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

	/**
	 * A class to parse the Google Places in JSON format
	 */
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

			String[] from = new String[]{"description"};
			int[] to = new int[]{android.R.id.text1};

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
			String key = "key=AIzaSyD3nAASUJ20XQ4ToaLWe10SZ_6j6MKXpm8";

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

	/**
	 * A class to parse the Google Places in JSON format
	 */
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

			String[] from = new String[]{"description"};
			int[] to = new int[]{android.R.id.text1};

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
					android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			endPlaces.setAdapter(adapter);
		}
	}

	void callLocationUpdate() {
		//googleMap = fm.getMap();

		// Enabling MyLocation Layer of Google Map
		//googleMap.setMyLocationEnabled(true);

		/*
		 * CheckInternetConnection intenercheck=new
		 * CheckInternetConnection(getActivity()); if
		 * (!intenercheck.CheckInternet()) { InternetCheckDialog dialog=new
		 * InternetCheckDialog(getActivity()); dialog.showDialog(); } else{
		 */
		// Getting LocationManager object from System Service LOCATION_SERVICE
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locListener = new SearchRouteActivity();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locListener);
		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			onLocationChanged(location);
		}

		locationManager.requestLocationUpdates(provider, 100, 0, this);

	}

	@Override
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub
		googleMap = map;
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
				Log.i("currently getting latitude ", "" + latitude + ""
						+ longitude);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String address = null;
		switch (v.getId()) {
			case R.id.start_navigation:
				CheckInternetConnection connection_ok = new CheckInternetConnection(this);
				if (connection_ok.CheckInternet()) {
					if (endPlaces.getText().length() <= 0) {
						endPlaces.setError("Please Select Destination");
					} else {
			/*if (flag) {
				googleMap.clear();
			}*/
						if (atvPlaces.getText().length() <= 0) {
							address = getLocationAddress(location.getLatitude(), location.getLongitude());
							atvPlaces.setText(address);
							//Log.i("current address", address);
						}
						//Log.i("current address", address);
			/*geoCodingSRC(atvPlaces.getText().toString());

			geoCodingDest(endPlaces.getText().toString());
			LatLng srcLatLng = new LatLng(srcLat, srcLang);
			LatLng destLatLng = new LatLng(desLat, desLang);
			
			ConnectAsyncTask _connectAsyncTask = new ConnectAsyncTask();
			_connectAsyncTask.execute();
			googleMap.addMarker(new MarkerOptions().position(srcLatLng).title(
					"Source")).showInfoWindow();;
			// destination marker
			googleMap.addMarker(new MarkerOptions().position(destLatLng).title(
					"Destination")).showInfoWindow();;*/
						Intent i = new Intent(SearchRouteActivity.this, RouteDisplayActivity.class);
						Bundle data = new Bundle();

						data.putString("source_address", atvPlaces.getText().toString());
						data.putString("destination_address", endPlaces.getText().toString());
						data.putBoolean("data_stored", true);
						i.putExtras(data);

						startActivity(i);
			/*atvPlaces.setText(atvPlaces.getText().toString());
			endPlaces.setText(endPlaces.getText().toString());*/
						endPlaces.setText("");
						atvPlaces.setText("");

					}
				} else {
					Toast.makeText(this, "check your Internet", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.atv_clear:
				atvPlaces.setText("");
				break;

			case R.id.end_clear:
				endPlaces.setText("");
				break;
			case R.id.stop_navigation:
				SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

				if (preference_stored) {
					String src = prefs.getString("src", null);//"No name defined" is the default value.
					String dest = prefs.getString("dest", null);
					String dura = prefs.getString("duration", null);
					String dist = prefs.getString("distance", null);
					Log.i("src", src);
					Log.i("dest", dest);
					Log.i("dura", dura);
					Log.i("dist", dist);

				}
				break;

		}


	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();

		// Getting longitude of the current location
		longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		try {
			/*googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			
			// Zoom in the Google Map
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
			
			getLocationAddress(latitude,longitude);*/
		} catch (NullPointerException e) {

		}
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

	private class ConnectAsyncTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(SearchRouteActivity.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub


			fetchData();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (doc != null) {

				NodeList _nodelist = doc.getElementsByTagName("status");

				Node node1 = _nodelist.item(0);
				String _status1 = node1.getChildNodes().item(0).getNodeValue();

				if (_status1.equalsIgnoreCase("OK")) {
					NodeList _nodelist_path = doc
							.getElementsByTagName("overview_polyline");

					Node node_path = _nodelist_path.item(0);

					Element _status_path = (Element) node_path;

					NodeList _nodelist_destination_path = _status_path
							.getElementsByTagName("points");

					Node _nodelist_dest = _nodelist_destination_path.item(0);

					String _path = _nodelist_dest.getChildNodes().item(0)
							.getNodeValue();
					GMapV2Direction gmapVDirection = new GMapV2Direction();

					List<LatLng> directionPoint = gmapVDirection.decodePoly(_path);

					PolylineOptions rectLine = new PolylineOptions().width(10)
							.color(Color.RED);

					for (int i = 0; i < directionPoint.size(); i++) {
						rectLine.add(directionPoint.get(i));

					}

					// Adding route on the map
					/*googleMap.addPolyline(rectLine);
					
					markerOptions.position(new LatLng(srcLat, srcLang));
					markerOptions.position(new LatLng(desLat, desLang));
					
					markerOptions.draggable(true);
					
					googleMap.addMarker(markerOptions);
					
					LatLng srcLatLng = new LatLng(srcLat, srcLang);
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							srcLatLng, 12));
					flag = true;
					GMapV2Direction gmv2=new GMapV2Direction();
					int distance=gmv2.getDistanceValue(doc);
					int duration=gmv2.getDurationValue(doc);
					 starting_address=gmv2.getStartAddress(doc);
					 end_address=gmv2.getEndAddress(doc);
					 surce_marker=starting_address;
					 destination_marker=end_address;
					String distance1=gmv2.getDistanceText(doc);
					String duration1=gmv2.getDurationText(doc);
					//ArrayList<LatLng> direction_address=gmv2.getDirection(doc);
					Log.i("distance", ""+distance);
					Log.i("duration", ""+duration);
				
					Log.i("distance", ""+distance1);
					Log.i("duration", ""+duration1);
					Log.i("starting_address", ""+starting_address);
					Log.i("end_address", ""+end_address);
					duration_text.setText(duration1);
					distance_text.setText(distance1);
					layout_dist_duration.setVisibility(View.VISIBLE);
					routesession=new RouteSessionManager(getApplicationContext());
					routesession.createRouteSession(true,starting_address, end_address, distance1, duration1);*/
					/*SharedPreferences.Editor editor = sharedpreferences.edit();

		            editor.putString("src", starting_address);
		            editor.putString("dest", end_address);
		            editor.putString("distance", distance1);
		            editor.putString("duration", duration1);
		            preference_stored=true;
		            editor.commit();*/
					/*ArrayList<LatLng> ll=gmv2.getDirection(doc);
					 for (int j = 0; j < ll.size(); j++) {
						 String ss3=getLocationAddress(ll.get(j).latitude,ll.get(j).longitude);
						 Log.i("ad", ss3);
		                    Log.i("inside for loop ", "latitude "+ll.get(j).latitude);
		                    Log.i("inside for loop ", "langitude "+ll.get(j).longitude);
		           
		                }*/
					/* ArrayList<String >ss1=getLocationAddress(ll.get(j).latitude,ll.get(j).longitude);
	                    for (int i = 0; i < ss1.size(); i++) {
							
	                    	Log.i("sample", ss1.get(i));
						}*/

				} else {
					showAlert("Unable to find the route");
				}

			} else {
				showAlert("Unable to find the route");
			}

			progressDialog.dismiss();

		}

	}

	Document doc = null;

	private void fetchData() {
		Log.i(Tag, " inside fetch sb before");
		StringBuilder urlString = new StringBuilder();
		urlString
				.append("http://maps.google.com/maps/api/directions/xml?origin=");
		urlString.append(srcLat);
		urlString.append(",");
		urlString.append(srcLang);
		urlString.append("&destination=");// to
		urlString.append(desLat);
		urlString.append(",");
		urlString.append(desLang);
		urlString.append("&sensor=true&mode=driving");

		HttpURLConnection urlConnection = null;
		URL url = null;
		try {

			url = new URL(urlString.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = (Document) db.parse(urlConnection.getInputStream());// Util.XMLfromString(response);


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showAlert(String message) {
		AlertDialog.Builder alert = new AlertDialog.Builder(SearchRouteActivity.this);
		alert.setTitle("Error");
		alert.setCancelable(false);
		alert.setMessage(message);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		alert.show();
	}

/*	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}*/
}
package com.yoobikwiti.sirka;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomNavigate extends FragmentActivity implements LocationListener,OnLocationChangedListener, OnClickListener,
OnMapReadyCallback {

	String TAG=getClass().getSimpleName().toString();
	double latitude, longitude;
	private LocationManager locationManager;
	Location location;LatLng latLng;
	CheckInternetConnection connection_ok;
	private GoogleMap googleMap;
	RouteSessionManager routesession;
	boolean bundle_data;
	MarkerOptions markerOptions;
	String Tag = "inside route drawer",
			s_source,s_destination,s_duration,s_distance,distance1,duration1;
	MapFragment mapFragment ;
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.custom_navigate);
		
		 mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map);
		mapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMap) {
				googleMap=googleMap;
			}
		});
		 //googleMap = getMapFragment().getMap();
		//googleMap = mapFragment.getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.setTrafficEnabled(true);
		googleMap.getCameraPosition();
		googleMap.getUiSettings().setZoomControlsEnabled(true);

	    // Enable / Disable my location button
	    googleMap.getUiSettings().setMyLocationButtonEnabled(true);

	    // Enable / Disable Compass icon
	    googleMap.getUiSettings().setCompassEnabled(true);

	    // Enable / Disable Rotate gesture
	    googleMap.getUiSettings().setRotateGesturesEnabled(true);

	    // Enable / Disable zooming functionality
	    googleMap.getUiSettings().setZoomGesturesEnabled(true);
	   
		markerOptions = new MarkerOptions();
		
	Criteria criteria = new Criteria();
		
		// Getting the name of the best provider
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);	
		LocationListener locListener = new CustomNavigate();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locListener);
		
	    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		   if (location != null) {
		       onLocationChanged(location);
		       

		    } else {
		      /*  Toast.makeText(
		                this,
		                "Location Null", Toast.LENGTH_SHORT).show();*/
		    	callLocationUpdate();
		       
		    }
		   //latLng = new LatLng(latitude, longitude);
		
		  /* latLng = new LatLng(location.getLatitude(),location.getLongitude());
		   
		   googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));*/
	}

	/*private MapFragment getMapFragment() {
		FragmentManager fm = null;

		Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT);
		Log.d(TAG, "release: " + Build.VERSION.RELEASE);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			Log.d(TAG, "using getFragmentManager");
			fm = getFragmentManager();
		} else {
			Log.d(TAG, "using getChildFragmentManager");
			fm = getChildFragmentManager();
		}

		return (MapFragment) fm.findFragmentById(R.id.google_map);
	}*/
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	
         
		latitude = location.getLatitude();
		
		// Getting longitude of the current location
		 longitude = location.getLongitude();		
		
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		// Showing the current location in Google Map
		try{
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		
		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
		
		
		}catch(NullPointerException e){
			
		}
			 
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	
	callLocationUpdate();
	 	
	  
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
	public void onMapReady(GoogleMap map) {
		// TODO Auto-generated method stub
		googleMap = map;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	
	void callLocationUpdate(){
		//googleMap =
				mapFragment.getMapAsync(new OnMapReadyCallback() {
					@Override
					public void onMapReady(GoogleMap googleMap) {
						googleMap=googleMap;
					}
				});
				//.getMap();
		 		
		 		// Enabling MyLocation Layer of Google Map
		 		googleMap.setMyLocationEnabled(true);				
		 				
		 		CheckInternetConnection intenercheck=new CheckInternetConnection(this);
				if (!intenercheck.CheckInternet()) {
					Toast.makeText(this, "please check the internet", Toast.LENGTH_LONG).show();
					
				}
				else{
		 		 // Getting LocationManager object from System Service LOCATION_SERVICE
		 		  locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		 		 LocationListener locListener = new CustomNavigate();
		 		 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
		         // Creating a criteria object to retrieve provider
		         Criteria criteria = new Criteria();

		         // Getting the name of the best provider
		         String provider = locationManager.getBestProvider(criteria, true);

		         // Getting Current Location
		          location = locationManager.getLastKnownLocation(provider);
		         

		         if(location!=null){
		                 onLocationChanged(location);
		         }

		         locationManager.requestLocationUpdates(provider, 100, 0, this);
				}
		  		
		    }

}

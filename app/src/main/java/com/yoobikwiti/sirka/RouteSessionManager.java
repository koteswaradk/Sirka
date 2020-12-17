package com.yoobikwiti.sirka;


import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class RouteSessionManager {
	// Shared Preferences

		SharedPreferences pref;

		// Editor for Shared preferences
		Editor editor;

		// Context
		Context _context;

		// Shared pref mode
		int PRIVATE_MODE = 0;
		private static final String PREF_NAME = "routesearch";

		// All Shared Preferences Keys
		private static final String IS_ROUTE_DISPLAYED = "IsRouteDisplayed";
		// All Shared Preferences Keys
		public static final String KEY_SOURCE = "source";
		public static final String KEY_DESTINATION = "destination";
		//userdetails 
		public static final String KEY_DURATION = "duration";
		public static final String KEY_DISTANCE = "distance";
		
		public RouteSessionManager(Context context){
			this._context = context;
			pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
			editor = pref.edit();
		}
		public void createRouteSession( boolean yes,String source, String destination , String distance,
				String duration){
			// Storing login value as TRUE
			editor.putBoolean(IS_ROUTE_DISPLAYED, yes);

			// Storing name in pref
			editor.putString(KEY_SOURCE, source);

			// Storing email in pref
			editor.putString(KEY_DESTINATION, destination);

			editor.putString(KEY_DISTANCE, distance);

			editor.putString(KEY_DURATION, duration);
			
			// commit changes
			editor.commit();
		} 
		
		public void checkLogin(){
			// Check login status
			if(!this.isRouteDisplaed()){
				// user is not logged in redirect him to Login Activity
			/*	Intent i = new Intent(_context, LoginMain.class);
				// Closing all the Activities
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				// Add new Flag to start new Activity
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				// Staring Login Activity
				_context.startActivity(i);
			}else{
				Intent i = new Intent(_context, SalesGirlsSearch.class);
				_context.startActivity(i);*/
			}


		}
		// Get Login State
		
		public boolean isRouteDisplaed(){
			return pref.getBoolean(IS_ROUTE_DISPLAYED, false);


		}
		
		public void logoutUser(){
			// Clearing all data from Shared Preferences
			editor.clear();
			editor.commit();

			
		}
		
		public HashMap<String, String> getRoutSessionDetails(){
			HashMap<String, String> user = new HashMap<String, String>();
			// user name
			
			user.put(KEY_SOURCE, pref.getString(KEY_SOURCE, null));

			user.put(KEY_DESTINATION, pref.getString(KEY_DESTINATION, null));

			user.put(KEY_DISTANCE, pref.getString(KEY_DISTANCE, null));

			user.put(KEY_DURATION, pref.getString(KEY_DURATION, null));


			// return user
			return user;
		}
		
		
}

package com.yoobikwiti.sirka;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class SplashScreen extends FragmentActivity{
	RouteSessionManager routesession;
	private boolean activity_status=true;
	private int splash_time=1000;
	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.splash);
		//String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
	
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			activity_status=false;
		}
		
		return true; 
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("resume splash", "inside on resume");
		CheckInternetConnection connection_ok=new CheckInternetConnection(this);
		if(connection_ok.CheckInternet()){
		  routesession=new RouteSessionManager(getApplicationContext());
		if (routesession.isRouteDisplaed()) {
				startActivity(new Intent(SplashScreen.this, RouteDisplayActivity.class));
		}else{
			Thread splash= new Thread(new Runnable() {
				
				
				public void run() {
					// TODO Auto-generated method stub
					
					try {
						int wait_id=0;
						
						while(activity_status && wait_id<splash_time)
						{	
						Thread.sleep(500);
							if(activity_status)
							{
								wait_id+=100;
							}
						} 
						}
					
					catch (Exception e) 
					{
						// TODO: handle exception
					}
					finally
					{
						
						startActivity(new Intent(SplashScreen.this,SearchRouteActivity.class));
						//startActivity(new Intent(SplashScreen.this, RouteSearchActivity.class));
						finish();
					}
			};
			
			});
			splash.start();
		}
	}else{
		Toast.makeText(SplashScreen.this, "Turn On Internet And Re Open", Toast.LENGTH_LONG).show();
	}
	}
}
package com.yoobikwiti.sirka;

import android.content.Context;
import android.net.ConnectivityManager;



public class CheckInternetConnection {
	Context context;
	public CheckInternetConnection(Context c) {
		// TODO Auto-generated constructor stub
		context=c;
	}
	public boolean CheckInternet() 
	{
		
	    ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    if (wifi.isConnected()||mobile.isConnected()) {
	        return true;
	    } 
	    return false;
	}
}

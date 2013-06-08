package com.snda.inote.util;import android.content.Context;import android.net.ConnectivityManager;import android.net.NetworkInfo;public class ConnectUtil {	public static boolean isAvailable(Context context){		ConnectivityManager cManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);		NetworkInfo info = cManager.getActiveNetworkInfo();        return info != null && info.isAvailable();    }		public static boolean isWifi(Context context){		ConnectivityManager cManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);		NetworkInfo info = cManager.getActiveNetworkInfo();		if(info != null){			if(info.isConnectedOrConnecting()){				return info.getTypeName().equalsIgnoreCase("WIFI");			}		}		return false;	}		public static boolean isMobile(Context context){		ConnectivityManager cManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);		NetworkInfo info = cManager.getActiveNetworkInfo();		if(info != null){			if(info.isConnectedOrConnecting()){				return info.getTypeName().equalsIgnoreCase("MOBILE");			}		}		return false;	}}
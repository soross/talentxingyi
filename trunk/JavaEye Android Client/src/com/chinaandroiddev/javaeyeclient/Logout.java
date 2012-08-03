package com.chinaandroiddev.javaeyeclient;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.chinaandroiddev.javaeyeclient.api.LocalAccessor;
import com.chinaandroiddev.javaeyeclient.model.User;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

public class Logout extends Activity {

	private static final String LOGOUT_TAG = "Logout";
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		user = LocalAccessor.getInstance(this).getUser();
		if (user.name != null && user.password != null) {
			// user logout
			user.name = null;
			user.password = null;
			Map<String, String> map = new HashMap<String, String>();
			map.put("logout_desc", "username [" + user.name + "] logout");
			FlurryAgent.onEvent("user Logout", map);
			doLogout();
		}
	}

	private void doLogout() {
		new Thread() {
			public void run() {
				try {
					LocalAccessor.getInstance(Logout.this).updateUser(user);
					Intent i = new Intent(Logout.this, Login.class);
					Map<String, String> map = new HashMap<String, String>();
					map.put("source", LOGOUT_TAG);
					FlurryAgent.onEvent("Main", map);
					FlurryAgent.onEvent("Logout Success");
					startActivity(i);
					finish();
				} catch (Exception e) {
					// Log.e(LOGOUT_TAG, e.getMessage());
					FlurryAgent.onError("doLogout() Error", e.getMessage(),
							LOGOUT_TAG);
				}
			}
		}.start();
	}

	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_API_KEY);
	}

	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}

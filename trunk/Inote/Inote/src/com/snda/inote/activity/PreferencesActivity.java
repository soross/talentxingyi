package com.snda.inote.activity;

import com.snda.inote.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * @Author KevinComo@gmail.com
 * 2010-6-30
 */

public class PreferencesActivity extends PreferenceActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
	
	public static void show(Context context){
		Intent intent = new Intent();
		intent.setClass(context, PreferencesActivity.class);
		context.startActivity(intent);
	}
}

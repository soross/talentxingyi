package com.chinaandroiddev.javaeyeclient;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.chinaandroiddev.javaeyeclient.misc.BounceInterpolator;
import com.chinaandroiddev.javaeyeclient.misc.EasingType.Type;
import com.chinaandroiddev.javaeyeclient.ui.Favorites;
import com.chinaandroiddev.javaeyeclient.ui.Messages;
import com.chinaandroiddev.javaeyeclient.ui.Twitters;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

public class Main extends Activity {

    private static final String LOG_TAG = "Main";
    private Animation animLeft, animRight;
    private Interpolator interpolator;
    private LinearLayout twitterRow, messageRow, bookmarkRow, aboutRow,logoutRow, exitRow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        animLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        animRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        interpolator = new BounceInterpolator(Type.IN);
        animLeft.setInterpolator(interpolator);
        animRight.setInterpolator(interpolator);
        
        twitterRow = (LinearLayout)findViewById(R.id.twitter_row);
        twitterRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Main.this, Twitters.class);
                Map<String, String> map = new HashMap<String, String>();
                map.put("source", LOG_TAG);
                FlurryAgent.onEvent("Twitters", map);
                startActivity(i);
            }
        });
        
        messageRow = (LinearLayout)findViewById(R.id.message_row);
        messageRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Main.this, Messages.class);
                Map<String, String> map = new HashMap<String, String>();
                map.put("source", LOG_TAG);
                FlurryAgent.onEvent("Messages", map);
                startActivity(i);
            }
        });
        
        bookmarkRow = (LinearLayout)findViewById(R.id.bookmark_row);
        bookmarkRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Main.this, Favorites.class);
                Map<String, String> map = new HashMap<String, String>();
                map.put("source", LOG_TAG);
                FlurryAgent.onEvent("Favorites", map);
                startActivity(i);
            }
        });
        
        aboutRow = (LinearLayout)findViewById(R.id.about_row);
        aboutRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Main.this, About.class);
                Map<String, String> map = new HashMap<String, String>();
                map.put("source", LOG_TAG);
                FlurryAgent.onEvent("About", map);
                startActivity(i);
            }
        });
        
        logoutRow = (LinearLayout)findViewById(R.id.logout_row);
        logoutRow.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Intent i = new Intent(Main.this, Logout.class);
        		Map<String, String> map = new HashMap<String, String>();
        		map.put("source", LOG_TAG);
        		FlurryAgent.onEvent("Logout", map);
        		startActivity(i);
        	}
        });
        
        exitRow = (LinearLayout)findViewById(R.id.exit_row);
        exitRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("source", LOG_TAG);
                FlurryAgent.onEvent("Exit", map);
                finish();
            }
        });
        
        twitterRow.startAnimation(animRight);
        messageRow.startAnimation(animRight);
        bookmarkRow.startAnimation(animRight);
        aboutRow.startAnimation(animRight);
        logoutRow.startAnimation(animRight);
        exitRow.startAnimation(animRight);
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

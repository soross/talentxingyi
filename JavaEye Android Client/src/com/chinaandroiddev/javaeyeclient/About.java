package com.chinaandroiddev.javaeyeclient;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.chinaandroiddev.javaeyeclient.misc.BackInterpolator;
import com.chinaandroiddev.javaeyeclient.misc.EasingType.Type;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

public class About extends Activity {

    private static final String LOG_TAG = "About";
    private LinearLayout logoRow, lordhongRow, mqqqvpppmRow, kopRow;
    private Animation animLeft, animRight;
    private Interpolator interpolator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        animLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        animRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        interpolator = new BackInterpolator(Type.OUT, 0);
        animLeft.setInterpolator(interpolator);
        animRight.setInterpolator(interpolator);
        
        logoRow = (LinearLayout)findViewById(R.id.logo_row);
        logoRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.CHINAANDROIDDEV_URL));
                Map<String, String> map = new HashMap<String, String>();
                map.put("source", LOG_TAG);
                FlurryAgent.onEvent("Visit ChinaAndroidDev.com", map);
                startActivity(intent);
            }
        });
        lordhongRow = (LinearLayout)findViewById(R.id.lordhong_row);
        mqqqvpppmRow = (LinearLayout)findViewById(R.id.mqqqvpppm_row);
        kopRow = (LinearLayout)findViewById(R.id.kop_row);
        
        lordhongRow.startAnimation(animLeft);
        mqqqvpppmRow.startAnimation(animRight);
        kopRow.startAnimation(animRight);
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

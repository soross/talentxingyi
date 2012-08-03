package com.chinaandroiddev.javaeyeclient.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.chinaandroiddev.javaeyeclient.About;
import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

public class Twitters extends TabActivity {
    private TabHost tabs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitters);
        tabs = getTabHost();        
        TabHost.TabSpec listTab = tabs.newTabSpec("list");
        Intent list = new Intent(this, TwitterList.class);
        listTab.setContent(list);
        listTab.setIndicator("闲聊一下", this.getResources().getDrawable(R.drawable.message_32));
        list.putExtra("type", TwitterList.FOLLOWED);
        tabs.addTab(listTab);

        TabHost.TabSpec repliesTab = tabs.newTabSpec("replies");
        Intent replies = new Intent(this, TwitterList.class);
        repliesTab.setContent(replies);
        repliesTab.setIndicator("@我的", this.getResources().getDrawable(R.drawable.message_32));
        replies.putExtra("type", TwitterList.REPLIES);
        tabs.addTab(repliesTab);
/*
        TabHost.TabSpec myTab = tabs.newTabSpec("my");
        Intent my = new Intent(this, TwitterList.class);
        myTab.setContent(my);
        myTab.setIndicator("我的闲聊", this.getResources().getDrawable(R.drawable.je_icon));
        tabs.addTab(myTab);
*/
        TabHost.TabSpec allTab = tabs.newTabSpec("all");
        Intent all = new Intent(this, TwitterList.class);
        allTab.setContent(all);
        allTab.setIndicator("全站闲聊", this.getResources().getDrawable(R.drawable.message_32));
        all.putExtra("type", TwitterList.ALL);
        tabs.addTab(allTab);
        
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
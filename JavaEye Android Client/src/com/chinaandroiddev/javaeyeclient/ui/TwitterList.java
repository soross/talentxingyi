package com.chinaandroiddev.javaeyeclient.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.chinaandroiddev.javaeyeclient.About;
import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.api.JavaEyeApiAccessor;
import com.chinaandroiddev.javaeyeclient.api.LocalAccessor;
import com.chinaandroiddev.javaeyeclient.model.Update;
import com.chinaandroiddev.javaeyeclient.model.User;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

public class TwitterList extends Activity {
    
    private static final String LOG_TAG = "TwitterList";
    
    private static final int MENU_NEW_UPDATE = Menu.FIRST + 1;
    private static final int MENU_REFRESH = Menu.FIRST + 2;
    private static final int MENU_MESSAGES = Menu.FIRST + 3;
    private static final int MENU_FAVORITES = Menu.FIRST + 4;
    private static final int MENU_ABOUT = Menu.FIRST + 5;
    private static final int MENU_EXIT = Menu.FIRST + 6;
    
    public static final int MENU_REPLY = Menu.FIRST+7; 
    public static final int MENU_DELETE = Menu.FIRST+8;
    
    public static final int COMPOSE_UPDATE_REQUEST_CODE = 1339;
    
    public static final String FOLLOWED = "followed";
    public static final String REPLIES = "replies";
    public static final String ALL = "all";

    private Handler handler = new Handler();
    private ProgressDialog progressDialog = null;
    private ArrayList<Update> updates;
    private User user;
    private ListView updateList;
    private String type;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = (String) getIntent().getExtras().get("type");        
        setContentView(R.layout.twitter_list);
        updateList = (ListView)findViewById(R.id.update_list);
        registerForContextMenu(updateList);
        user = LocalAccessor.getInstance(this).getUser();
        getUpdates();        
    }
    
    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) { 
        menu.setHeaderTitle("闲聊操作菜单");
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        if (updates.get(info.position).user.name.equals(user.name)) {
            menu.add(0, MENU_REPLY, 0, "删除");
        } else {
            menu.add(0, MENU_REPLY, 0, "回复");
        }
    } 

    @Override 
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (updates.get(info.position).user.name.equals(user.name)) {
            FlurryAgent.onEvent("Twitter Delete Action");
            try {
                JavaEyeApiAccessor.deleteUpdate(updates.get(info.position));
                updates.remove(info.position);
                updateList();
            } catch (Exception e) {
//                Log.e(LOG_TAG, e.getMessage());
                FlurryAgent.onError("Twitter Delete Error", e.getMessage(), LOG_TAG);
            }
            return true;
        } else {
            FlurryAgent.onEvent("Twitter Compose Action");
            Intent i = new Intent(TwitterList.this, ComposeUpdate.class);
            i.putExtra("reply_to_id", updates.get(info.position).id);
            i.putExtra("reply_to_name", updates.get(info.position).user.name);
//            Log.e(LOG_TAG, "pos: " + info.position + " -- reply id: " + updates.get(info.position).id);
            startActivityForResult(i, COMPOSE_UPDATE_REQUEST_CODE);
            return true;
        }
//        return super.onOptionsItemSelected(item);
    } 

    
    private void getUpdates() {
        progressDialog = ProgressDialog.show(TwitterList.this, "请稍等...", "获取闲聊列表...", true); 
        
        new Thread() {
            public void run() {
                try {
                	if(type.equals(FOLLOWED)){
                		updates = JavaEyeApiAccessor.getFollowed(-1, -1);
                	}else if(type.equals(REPLIES)){
                		updates = JavaEyeApiAccessor.getReplies(-1, -1);	
                	}else if(type.equals(ALL)){
                		updates = JavaEyeApiAccessor.getAllUpdates(-1, -1);
                	}
                    if (updates != null && updates.size() > 0)
                        updateList();
                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage());
                    FlurryAgent.onError("Get Updates Error", e.getMessage(), LOG_TAG);
                }
                progressDialog.dismiss();
            }
        }.start();
    }
    
    private void updateList() {
        FlurryAgent.onEvent("TwitterList updateList");
        handler.post(new Runnable() {
            public void run() {
                updateList.setAdapter(new UpdateAdapter(TwitterList.this, updates));
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_UPDATE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                FlurryAgent.onEvent("Compose Update Callback on Okay");
                getUpdates();
            } else if (resultCode == RESULT_CANCELED) {
                // do nothing now
                FlurryAgent.onEvent("Compose Update Callback on Cancel");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_NEW_UPDATE, 0, "发布新闲聊")
                .setIcon(R.drawable.twitter)
                .setAlphabeticShortcut('N');
        menu.add(0, MENU_REFRESH, 0, "刷新")
                .setIcon(R.drawable.refresh)
                .setAlphabeticShortcut('R');
        menu.add(0, MENU_MESSAGES, 0, "站内短信")
                .setIcon(R.drawable.message)
                .setAlphabeticShortcut('I');
        menu.add(0, MENU_FAVORITES, 0, "收藏")
                .setIcon(R.drawable.bookmark)
                .setAlphabeticShortcut('F');
        menu.add(0, MENU_ABOUT, 0, "关于")
                .setIcon(R.drawable.android_cn)
                .setAlphabeticShortcut('A');
        menu.add(0, MENU_EXIT, 0, "退出")
                .setIcon(R.drawable.exit)
                .setAlphabeticShortcut('X');
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_NEW_UPDATE: {
                FlurryAgent.onEvent("TwitterList New Update Menu Click");
                Intent i = new Intent(TwitterList.this, ComposeUpdate.class);
                startActivityForResult(i, COMPOSE_UPDATE_REQUEST_CODE);
                return true;
            }
            case MENU_REFRESH: {
                FlurryAgent.onEvent("TwitterList Refresh Menu Click");
                getUpdates();
                return true;
            }
            case MENU_MESSAGES: {
                FlurryAgent.onEvent("TwitterList Messages Menu Click");
                Intent i = new Intent(TwitterList.this, Messages.class);
                startActivity(i);
                return true;
            }
            case MENU_FAVORITES: {
                FlurryAgent.onEvent("TwitterList Favorites Menu Click");
                Intent i = new Intent(TwitterList.this, Favorites.class);
                startActivity(i);
                return true;
            }
            case MENU_ABOUT: {
                FlurryAgent.onEvent("TwitterList About Menu Click");
                Intent i = new Intent(TwitterList.this, About.class);
                startActivity(i);
                return true;
            }
            case MENU_EXIT: {
                FlurryAgent.onEvent("TwitterList Exit Menu Click");
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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

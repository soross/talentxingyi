package com.chinaandroiddev.javaeyeclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.chinaandroiddev.javaeyeclient.About;
import com.chinaandroiddev.javaeyeclient.Main;
import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.api.JavaEyeApiAccessor;
import com.chinaandroiddev.javaeyeclient.api.LocalAccessor;
import com.chinaandroiddev.javaeyeclient.model.FavoriteItem;
import com.chinaandroiddev.javaeyeclient.model.Message;
import com.chinaandroiddev.javaeyeclient.model.Update;
import com.chinaandroiddev.javaeyeclient.model.User;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Favorites extends Activity {
	private static final String LOG_TAG = "Favorites";	    
	private static final int MENU_REFRESH = Menu.FIRST + 1; 
    private static final int MENU_TWITTER = Menu.FIRST + 2;
    private static final int MENU_MESSAGES = Menu.FIRST + 3;
    private static final int MENU_ABOUT = Menu.FIRST + 4;
    private static final int MENU_EXIT = Menu.FIRST + 5;
    
    private static final int MENU_EDIT = Menu.FIRST + 6;
    private static final int MENU_DELETE = Menu.FIRST + 7;
    
    private static final int PAGE_SIZE = 18;
    
    private int currentPageNo = 1;
	private Handler handler =  new Handler();    
	private ListView list;
	private Button preBtn;
	private Button nextBtn;
	private ProgressDialog progressDialog = null;    
	ArrayList<FavoriteItem> page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);
        list = (ListView)findViewById(R.id.favorite_list);        
        registerForContextMenu(list);         
        
        preBtn = (Button)findViewById(R.id.preBtn);  
        preBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
			    Map<String, String> map = new HashMap<String, String>();
                map.put("page_num", new Integer(currentPageNo).toString());
                FlurryAgent.onEvent("Favorite Pre Page", map);
				Favorites.this.currentPageNo--;
				updateList();
			}        	
        });
        
        nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			    Map<String, String> map = new HashMap<String, String>();
                map.put("page_num", new Integer(currentPageNo).toString());
                FlurryAgent.onEvent("Favorite Next Page", map);
				Favorites.this.currentPageNo++;
				updateList();
			}        	
        });
        
        if (LocalAccessor.getInstance(this).isFavoritesEmpty()) {
            Map<String, String> map = new HashMap<String, String>();
            FlurryAgent.onEvent("Favorite Server Refresh", map);
        	refresh();
        } else {
        	updateList();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//    	Log.d(LOG_TAG,"on call back");
        FlurryAgent.onEvent("Favorite Refresh on Callback");
    	updateList();
    }

    @Override 
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateOptionsMenu(menu);
        menu.setHeaderTitle("收藏操作菜单");
        menu.add(0, MENU_EDIT, 0, "编辑");
        menu.add(0, MENU_DELETE, 0, "删除");
    } 
   
    @Override 
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final int position = info.position; 
        switch (item.getItemId()) {
		case MENU_EDIT:
            FlurryAgent.onEvent("Favorite Edit Action");
//			progressDialog = ProgressDialog.show(Favorites.this, "请稍等...", "正在更新并同步到JavaEye...", true);
			Intent i = new Intent(Favorites.this, FavoriteEdit.class);
    		FavoriteItem favorite = (FavoriteItem) list.getItemAtPosition(position);
    		i.putExtra("id", favorite.id);
    		int requestCode = 1;
    		startActivityForResult(i, requestCode);            
			return true;
		case MENU_DELETE:
		    FlurryAgent.onEvent("Favorite Delete Action");
			progressDialog = ProgressDialog.show(Favorites.this, "请稍等...", "正在删除并同步到JavaEye...", true);
			new Thread() {
	            public void run() {	            	
	            	try {
	            		FavoriteItem favorite = (FavoriteItem) list.getItemAtPosition(position);
	            		LocalAccessor.getInstance(Favorites.this).deleteFavoriteItem(favorite.id);
	            		updateList();
	            		JavaEyeApiAccessor.deleteFavorite(favorite);
	            	} catch (Exception e) {
//	            		Log.e(LOG_TAG, e.getMessage());
	            	    FlurryAgent.onError("Favorite Delete Error", e.getMessage(), LOG_TAG);
	            	}
	                progressDialog.dismiss();
	            }
	        }.start();	
			return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_REFRESH, 0, "更新").setIcon(R.drawable.refresh).setAlphabeticShortcut('R');
        menu.add(0, MENU_TWITTER, 0, "闲聊").setIcon(R.drawable.twitter).setAlphabeticShortcut('T');;
        menu.add(0, MENU_MESSAGES, 0, "站内短信").setIcon(R.drawable.message).setAlphabeticShortcut('I');
        menu.add(0, MENU_ABOUT, 0, "关于").setIcon(R.drawable.android_cn).setAlphabeticShortcut('A');
        menu.add(0, MENU_EXIT, 0, "退出").setIcon(R.drawable.exit).setAlphabeticShortcut('X');
        return true;
    }    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case MENU_REFRESH:
	            FlurryAgent.onEvent("Favorites Refresh Menu Click");
	            refresh();
	            return true;
	        case MENU_TWITTER: {
	            FlurryAgent.onEvent("Favorites Twitters Menu Click");
                Intent i = new Intent(Favorites.this, Twitters.class);
                startActivity(i);
                return true;
            }
	        case MENU_MESSAGES: {
	            FlurryAgent.onEvent("Favorites Messages Menu Click");
                Intent i = new Intent(Favorites.this, Messages.class);
                startActivity(i);
                return true;
            }            
            case MENU_ABOUT: {
                FlurryAgent.onEvent("Favorites About Menu Click");
                Intent i = new Intent(Favorites.this, About.class);
                startActivity(i);
                return true;
            }
            case MENU_EXIT: {
                FlurryAgent.onEvent("Favorites Exit Menu Click");
                finish();
                return true;
            }	        
        }
		return super.onOptionsItemSelected(item);
    }
 
    private void refresh(){    	
        progressDialog = ProgressDialog.show(Favorites.this, "请耐心稍等...", "获取收藏列表...", true); 
        new Thread() {
            public void run() {
                try {                	
                	ArrayList<FavoriteItem> favorites = JavaEyeApiAccessor.getFavorites();
                	LocalAccessor.getInstance(Favorites.this).clearFavorites();
                	for (FavoriteItem item : favorites) {
                		LocalAccessor.getInstance(Favorites.this).updateFavoriteItem(item);
                	}                	
                    if (favorites != null && favorites.size() > 0)                    	
                        updateList();
                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage());
                    FlurryAgent.onError("Favorites getFavorites Error", e.getMessage(), LOG_TAG);
                }
                progressDialog.dismiss();
            }
        }.start();
    }
    
    private void updateList() {
        FlurryAgent.onEvent("Favorite Local Refresh/Update List");
    	 handler.post(new Runnable() {
    		 public void run() {             	
 				try {
 					page = LocalAccessor.getInstance(Favorites.this).getFavoritesByPage(Favorites.this.currentPageNo, Favorites.PAGE_SIZE);
 					list.setAdapter(new FavoriteAdapter(Favorites.this, page));
 					if (Favorites.this.currentPageNo == 1) {
 						preBtn.setEnabled(false);
 					} else {
 						preBtn.setEnabled(true);
 					}
 					//the next statements is hard code, i will refactory soon
 					ArrayList<FavoriteItem> next = LocalAccessor.getInstance(Favorites.this).getFavoritesByPage(Favorites.this.currentPageNo+1, Favorites.PAGE_SIZE);
 					if (next.isEmpty()) {
 						nextBtn.setEnabled(false);
 					} else {
 						nextBtn.setEnabled(true);
 					}
 				} catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage());
 				   FlurryAgent.onError("Favorites getFavoritesByPage Error", e.getMessage(), LOG_TAG);
 				}
             }
         });        
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

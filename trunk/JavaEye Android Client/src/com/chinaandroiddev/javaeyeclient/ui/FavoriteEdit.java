package com.chinaandroiddev.javaeyeclient.ui;

import java.util.HashMap;
import java.util.Map;

import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.api.JavaEyeApiAccessor;
import com.chinaandroiddev.javaeyeclient.api.LocalAccessor;
import com.chinaandroiddev.javaeyeclient.model.FavoriteItem;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.chinaandroiddev.javaeyeclient.util.StringUtils;
import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FavoriteEdit extends Activity{
    private static final String LOG_TAG = "FavoriteEdit";
    private EditText title, category, description;
    private Button okButton, cancelButton;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    private FavoriteItem item;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_edit);
        
        Bundle extras = getIntent().getExtras();
        long id = -1;
        if (extras != null) {
            id = extras.getLong("id");
        }
        
        title = (EditText)findViewById(R.id.favorite_edit_title);
        category = (EditText)findViewById(R.id.favorite_edit_catagory);
        description = (EditText)findViewById(R.id.favorite_edit_description);
        
        try {
        	item = LocalAccessor.getInstance(this).getFavoriteItem(id);
        	title.setText( item.title );
        	category.setText( StringUtils.combineByToken(item.categoryNames,",") );
        	description.setText(item.description);
        } catch (Exception e) {
//        	Log.e(LOG_TAG, e.getMessage());
            FlurryAgent.onError("Favorite Get Error", e.getMessage(), LOG_TAG);
        }
        
        
        okButton = (Button)findViewById(R.id.favorite_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	item.title = title.getText().toString().trim();
            	item.categoryNames = StringUtils.spliteByToken(category.getText().toString(),",");
            	item.description = description.getText().toString().trim();
            	
            	if (item.title == null || item.title.trim().equals("")) {
                    FlurryAgent.onError("Favorite Edit Warning", "favorite title cannot be empty", LOG_TAG);
            		new AlertDialog.Builder(FavoriteEdit.this)
                    .setTitle("警告")
                    .setMessage("收藏标题信息不能为空!")
                    .setPositiveButton("Okay", null).show();
            		return;
            	}
            	
            	progressDialog = ProgressDialog.show(FavoriteEdit.this, "请稍等...", "正在保存并同步到JavaEye...", true);
    			new Thread() {
    	            public void run() {
    	            	boolean success = true;
    	            	try {
    	            		LocalAccessor.getInstance(FavoriteEdit.this).updateFavoriteItem(item);
    	            		//there is bug in  java eye api, update favorite can not work , so i have to 
    	            		//remove first then add it.
//    	            		if (JavaEyeApiAccessor.deleteFavorite(item)) {
//	    	            		if (JavaEyeApiAccessor.addFavorite(item) == null) {
    	            		if(JavaEyeApiAccessor.updateFavorite(item) == null)
	    	            			success = false;
	    	            		
//    	            		}else{
//    	            			success = false;
//    	            		}
    	            	} catch (Exception e) {
//    	            		Log.e(LOG_TAG, e.getMessage());
    	                    FlurryAgent.onError("Favorite Edit Error", e.getMessage(), LOG_TAG);
    	            		success = false;
    	            	}
    	            	if (success) {
    	                    FlurryAgent.onEvent("Favorite Edit Success");
	    	                progressDialog.dismiss();
	    	                int resultCode = -1;
	    	                setResult(resultCode, null);
	    	                finish();
    	            	} else {
    	                    FlurryAgent.onEvent("Favorite Edit Failure");
    	            		handler.post(new Runnable() {
    	            		    public void run() {
    	            		        new AlertDialog.Builder(FavoriteEdit.this)
    	            		        .setMessage("同步到JavaEye时有错误产生!  请稍后再试!")
    	            		        .setPositiveButton("Okay", null)
    	            		        .show();  
    	            		    }
    	            		});
    	            	}
    	            }
    	        }.start();	
            }
        });
        
        cancelButton = (Button)findViewById(R.id.favorite_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FlurryAgent.onEvent("Favorite Edit Canceled");
                finish();
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


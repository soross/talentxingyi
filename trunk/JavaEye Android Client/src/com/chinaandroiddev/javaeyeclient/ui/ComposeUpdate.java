package com.chinaandroiddev.javaeyeclient.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chinaandroiddev.javaeyeclient.Login;
import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.api.JavaEyeApiAccessor;
import com.chinaandroiddev.javaeyeclient.api.LocalAccessor;
import com.chinaandroiddev.javaeyeclient.model.Update;
import com.chinaandroiddev.javaeyeclient.model.User;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

public class ComposeUpdate extends Activity {
    private static final String LOG_TAG = "NewUpdate";
    private EditText msg;
    private Button sendButton, cancelButton;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    private long replyToId = 0;
	private String replyToName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_update);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            replyToId = extras.getLong("reply_to_id");
            replyToName = extras.getString("reply_to_name");
        }
        
        msg = (EditText)findViewById(R.id.msg);
        msg.setText("@"+replyToName+" ");
        sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (msg.getText().toString().length() > 0 && msg.getText().toString().length() <= 140) {
                    createUpdate();
                } else {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("failure_desc", "message length is 0 or more than 140");
                    FlurryAgent.onEvent("Compose Update Failure", map);
                    new AlertDialog.Builder(ComposeUpdate.this)
                        .setTitle("警告")
                        .setMessage("闲聊信息不能为空, 字数不超过140个字!")
                        .setPositiveButton("Okay", null).show();
                }
            }
        });
        cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                FlurryAgent.onEvent("Compose Update Canceled");
                finish();
            }
        });
    }

    private void createUpdate() {
        progressDialog = ProgressDialog.show(ComposeUpdate.this, "请稍等...", "发布新闲聊中...", true); 
        new Thread() {
            public void run() {
                Update u = new Update();
                u.body = msg.getText().toString();
                if (replyToId > 0) {
                    FlurryAgent.onEvent("Compose Update Reply");
                    u.replyToId = replyToId;
                } else {
                    FlurryAgent.onEvent("Compose New Update");
                }
                boolean success = false;
                try {
                    success = JavaEyeApiAccessor.createUpdate(u);
                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage());
                    FlurryAgent.onError("Create Update Error", e.getMessage(), LOG_TAG);
                    success = false;
                }
                progressDialog.dismiss();
                if (!success) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("failure_desc", "Error Posting Update");
                    FlurryAgent.onEvent("Compose Update Failure", map);
                    handler.post(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(ComposeUpdate.this)
                            .setMessage("发布闲聊有错误产生!  请稍后再试!")
                            .setPositiveButton("Okay", null)
                            .show();  
                        }
                    });
                } else {
                    FlurryAgent.onEvent("Compose Update Success");
                    Intent data = new Intent();
                    setResult(RESULT_OK, data);
                    finish();
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

package com.chinaandroiddev.javaeyeclient.ui;

import java.util.HashMap;
import java.util.Map;

import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.api.JavaEyeApiAccessor;
import com.chinaandroiddev.javaeyeclient.model.Message;
import com.chinaandroiddev.javaeyeclient.util.Constants;
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

public class MessageNew extends Activity {
    private static final String LOG_TAG = "MessageNew";
    private EditText replyTo, title, msgReply;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private Button msgButtonSend, msgButtonCancel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_new);
        replyTo = (EditText) findViewById(R.id.reply_to);
        title = (EditText) findViewById(R.id.title);
        msgReply = (EditText) findViewById(R.id.msg_reply);
        msgButtonSend = (Button) findViewById(R.id.msg_button_send);
        msgButtonSend.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (msgReply.getText().toString().length() <= 0 || replyTo.getText().toString().length() <=0 || title.getText().toString().length() <=0) {
                    FlurryAgent.onError("New Message Error", "to/subject/body cannot be empty", LOG_TAG);
                    new AlertDialog.Builder(MessageNew.this)
                    .setTitle("警告")
                    .setMessage("短信收件人, 标题, 和短信内容不能为空!")
                    .setPositiveButton("Okay", null).show();
                } else {
                    sendMsg();
                }
            }
        });
        msgButtonCancel=(Button) findViewById(R.id.msg_button_canel);
        msgButtonCancel.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                FlurryAgent.onEvent("Send Message Canceled");
                finish();
            }
        });
    }
    
    private void sendMsg(){
        progressDialog=ProgressDialog.show(MessageNew.this, "请稍等...", "短信发送中...", true); 
        new Thread(){
            public void run(){
                Message msg = new Message();
                Message m = new Message();
//                boolean isWin=false;
                msg.body = msgReply.getText().toString();
                msg.receiver.name = replyTo.getText().toString();
                msg.title = title.getText().toString();
                
                try {
                    m = JavaEyeApiAccessor.sendMessage(msg);
                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage());
                    FlurryAgent.onError("Send Message Error", e.getMessage(), LOG_TAG);
                    m = null;
                }
                if(m == null) {
                    FlurryAgent.onEvent("Send Message Failure");
                    handler.post(new Runnable() {
                        public void run() {
                            new AlertDialog.Builder(MessageNew.this)
                            .setMessage("短信发送失败！  请稍后再试!")
                            .setPositiveButton("Okay", null)
                            .show();  
                        }
                    });
                } else {
                    FlurryAgent.onEvent("Send Message Success");
                    finish();
                }
                progressDialog.dismiss();
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

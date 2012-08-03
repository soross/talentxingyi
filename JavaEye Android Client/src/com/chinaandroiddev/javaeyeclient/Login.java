package com.chinaandroiddev.javaeyeclient;

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
import android.widget.ImageButton;

import com.chinaandroiddev.javaeyeclient.api.JavaEyeApiAccessor;
import com.chinaandroiddev.javaeyeclient.api.LocalAccessor;
import com.chinaandroiddev.javaeyeclient.model.User;
import com.chinaandroiddev.javaeyeclient.model.VerifiedInfo;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.flurry.android.FlurryAgent;

public class Login extends Activity {

    private static final String LOG_TAG = "Login";
    private User user;
    private EditText usernameField, passwordField;
    private ImageButton loginButton;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        user = LocalAccessor.getInstance(this).getUser();
        usernameField = (EditText)findViewById(R.id.username_field);
        passwordField = (EditText)findViewById(R.id.password_field);
        loginButton = (ImageButton)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user.name = usernameField.getText().toString();
                user.password = passwordField.getText().toString();
                if (user.name.length() == 0 || user.password.length() == 0) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("failure_desc", "username/password is empty");
                    FlurryAgent.onEvent("Login Failure", map);
                    new AlertDialog.Builder(Login.this)
                        .setMessage("用户名和密码不能为空!")
                        .setPositiveButton("Okay", null)
                        .show();  
                } else {
                    doLogin();
                }
            }
        });
        if (user.name != null && user.password != null) {
        	 usernameField.setText(user.name);
             doLogin(); 
        }
    }

    private void doLogin() {
        progressDialog = ProgressDialog.show(Login.this, "请稍等...", "登录JavaEye中...", true); 
        new Thread() {
            public void run() {
                try {
                	JavaEyeApiAccessor.user = user;
                    final VerifiedInfo vi = JavaEyeApiAccessor.verify();
                    if (vi.verifyCode == VerifiedInfo.VERIFY_SUCCESS) {
                        user.id = vi.id;
                    	LocalAccessor.getInstance(Login.this).updateUser(user);    
                        Intent i = new Intent(Login.this, Main.class);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("source", LOG_TAG);
                        FlurryAgent.onEvent("Main", map);
                        FlurryAgent.onEvent("Login Success");
                        startActivity(i);
                        finish();
                    } else {
                        handler.post(new Runnable() {
                            public void run() {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("failure_desc", vi.verifyMessage);
                                FlurryAgent.onEvent("Login Failure", map);
                                new AlertDialog.Builder(Login.this)
                                .setMessage(vi.verifyMessage)
                                .setPositiveButton("Okay", null)
                                .show();  
                            }
                        });
                    }
                } catch (Exception e) {
//                    Log.e(LOG_TAG, e.getMessage());
                    FlurryAgent.onError("doLogin() Error", e.getMessage(), LOG_TAG);
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

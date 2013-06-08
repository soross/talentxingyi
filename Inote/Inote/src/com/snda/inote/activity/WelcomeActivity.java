package com.snda.inote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.snda.inote.Inote;
import com.snda.inote.Login;
import com.snda.inote.R;
import com.snda.inote.io.Setting;
import com.snda.inote.model.User;
import com.snda.inote.util.ConnectUtil;

import java.io.File;

public class WelcomeActivity extends Activity {

    private Context context = WelcomeActivity.this;
    private final static int UPDATE_DIALOG = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        findViewById(R.id.btnSignUp).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SignUpActivity.show(WelcomeActivity.this);
                } catch (Error e) {
                    e.printStackTrace();
                    Login.show(WelcomeActivity.this);
                }
                finish();
            }
        });

        Inote.isConnected = ConnectUtil.isAvailable(WelcomeActivity.this);
        User user = Setting.getUser(WelcomeActivity.this);
        if (user.isOffLineUser()) {
            Button loginBtn = (Button) findViewById(R.id.btnLogin);
            loginBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    try {
                        LoginOAActivity.show(WelcomeActivity.this);
                    } catch (Error e) {
                        e.printStackTrace();
                        Login.show(WelcomeActivity.this);
                    }
                    finish();
                }
            });
        } else {
            Inote.setUser(user);
            MainActivity.show(WelcomeActivity.this);
            finish();
        }

//        SDOAnalyzeAgentInterface.onCreate(context);
//        SDOAnalyzeAgentInterface.onError(context);
//        SDOAnalyzeAgentInterface.setReportPolicy(ReportPolicy.REALTIME);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        SDOAnalyzeAgentInterface.onPause(context);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SDOAnalyzeAgentInterface.onDestroy(context);
//    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == UPDATE_DIALOG){
            return new AlertDialog.Builder(context)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.not_save_note_alert_title)
                .setMessage(getString(R.string.update_app_tip))
                .setPositiveButton(R.string.not_save_note_btn_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Inote.instance.openAttachFile(new File(Inote.appPath));
                    }
                })
                .setNegativeButton(R.string.not_save_note_btn_no, null).create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Inote.needUpdateApp) {
            //showDialog(UPDATE_DIALOG);
        }
//        SDOAnalyzeAgentInterface.onResume(context);
    }


    public static void show(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WelcomeActivity.class);
        context.startActivity(intent);
    }


}

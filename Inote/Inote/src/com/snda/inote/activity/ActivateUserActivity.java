package com.snda.inote.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.snda.inote.Inote;
import com.snda.inote.MaiKuHttpApiV1;
import com.snda.inote.R;
import com.snda.inote.io.Setting;
import com.snda.inote.util.Json;
import com.snda.inote.util.UserTask;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-21
 * Time: 下午2:55
 */
public class ActivateUserActivity extends Activity {
    private static final int DIALOG_LOADING = 0;
    private EditText userNameEditView;
    private Toast mToast;
    private Context context = ActivateUserActivity.this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activate);
        userNameEditView = (EditText) findViewById(R.id.user_name);

        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userName = userNameEditView.getText().toString();
                if (userName.length() == 0) {
                    showToast(getString(R.string.username_null_error));
                    userNameEditView.requestFocus();
                } else if (userName.length() != userName.trim().length()) {
                    showToast(getString(R.string.username_contain_space_error));
                } else {
                    showDialog(DIALOG_LOADING);
                    new ActivateMaiku().execute(userName);
                }
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOADING:
                ProgressDialog dialog2 = new ProgressDialog(context);
                dialog2.setMessage(getString(R.string.sync_activate));
                dialog2.setIndeterminate(true);
                dialog2.setCancelable(false);
                return dialog2;
            default:
                return null;
        }
    }


    private void showToast(String str) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void show(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ActivateUserActivity.class);
        context.startActivity(intent);
    }


    private class ActivateMaiku extends UserTask<String, Void, String> {
        @Override
        public String doInBackground(String... params) {
            try {
                Json json = MaiKuHttpApiV1.userActivate(params[0], 0, Inote.notActivatedUser.getToken());
                boolean success = json.getBoolean("Success");
                if (success) {
                    Inote.setUser(Inote.notActivatedUser);
                    Setting.setUser(context, Inote.notActivatedUser);
                    return null;
                } else {
                    return getString(R.string.username_already_use_error);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();

            }
        }

        public void onPostExecute(String result) {
            removeDialog(DIALOG_LOADING);
            if (result != null) {
                showToast(result);
            } else {
                MainActivity.show(context);
                finish();
            }
        }

    }

}
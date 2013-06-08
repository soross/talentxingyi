package com.snda.inote.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.snda.inote.Inote;
import com.snda.inote.MaiKuHttpApiV1;
import com.snda.inote.R;
import com.snda.inote.io.Setting;
import com.snda.inote.model.User;
import com.snda.inote.util.Json;
import com.snda.inote.util.UserTask;
import com.snda.sdw.woa.callback.CallBack;
import com.snda.sdw.woa.callback.LoginCallBack;
import com.snda.sdw.woa.callback.RegisterCallBack;
import com.snda.sdw.woa.interfaces.OpenAPI;
import org.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-20
 * Time: 上午6:53
 */
public class SignUpActivity extends Activity {
    private final static int DIALOG_LOADING = 0;
    private EditText loginNameEditView;
    private EditText pwdEditView;
    private boolean isStartOASucess;
    private Toast mToast;

    private final Activity context = SignUpActivity.this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        loginNameEditView = (EditText) findViewById(R.id.login_name);
        pwdEditView = (EditText) findViewById(R.id.login_pass);


        pwdEditView.addTextChangedListener(textWatcher);
        loginNameEditView.addTextChangedListener(textWatcher);


        initOA();


        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String loginName = loginNameEditView.getText().toString();
                final String pwd = pwdEditView.getText().toString();


                if (loginName.length() == 0) {
                    showToast(getString(R.string.snda_account_null_error));
                    loginNameEditView.requestFocus();
                }
                else if (loginName.length() < 4 || loginName.length() > 16) {
                    showToast(getString(R.string.snda_account_length_error));
                    loginNameEditView.requestFocus();
                } else if (pwd.length() == 0) {
                    showToast(getString(R.string.password_null_error));
                    pwdEditView.requestFocus();
                } else if (pwd.length() < 6 || pwd.length() > 10) {
                    showToast(getString(R.string.password_length_error));
                    pwdEditView.requestFocus();
                }
                else {
                    String one = loginName.substring(0, 1);
                    try {
                        Integer.parseInt(one);
                        showToast(getString(R.string.snda_account_start_not_with_letter_error));
                        return;
                    } catch (Exception e) {
//                        e.printStackTrace();
                        //do nothing
                    }

                    OpenAPI.registerForPTAccount(loginName, pwd, context, new RegisterCallBack() {
                        @Override
                        public void onSuccess(String s) {
                            OpenAPI.login(loginName, pwd, false, context, loginCallBack);
                        }

                        @Override
                        public void onAccountExist(String jsonMessage) {
                            showToast(getString(R.string.snda_account_already_use_error));
                        }

                        @Override
                        public void onFailure(String jsonMessage) {
                            try {
                                Json json = new Json(new JSONObject(jsonMessage));
                                String errorInfo = json.getString("Message");
                                if(errorInfo==null || "null".equalsIgnoreCase(errorInfo)){
                                    errorInfo=getString(R.string.register_network_error);
                                }
                                showToast(getString(R.string.register_error) + errorInfo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
      // SDOAnalyzeAgentInterface.onCreate(context);
    }
//
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
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SDOAnalyzeAgentInterface.onResume(context);
//    }

    private void initOA() {
        if (!Inote.instance.isConnected()) {
            showToast(getString(R.string.connection_error));
            return;
        }

        try {
            OpenAPI.startOA("", this, new CallBack() {
                @Override
                public void onSuccess(String s) {
                    isStartOASucess = true;
                }

                @Override
                public void onFailure(String jsonMessage) {
                    if (!Inote.instance.isConnected()) {
                        showToast(getString(R.string.connection_error));
                    } else {
                        showToast(getString(R.string.openapi_init_error));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
//            showToast("切换登陆模式");
//            Login.show(context);
//            finish();
        }
    }


     protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOADING:
                ProgressDialog dialog2 = new ProgressDialog(context);
                dialog2.setMessage(getString(R.string.sync_logining));
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

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 0) {
                return;
            }
            String str = editable.toString();
            if (!checkNumberAlphabet(str)) {
                editable.clear();
                editable.append(getFilterNumberAlphabet(str));
            }
        }
    };


    private boolean checkNumberAlphabet(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c < '0' || c > '9') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
//                if (c == '@' || c == '.' || c == '_') {
//
//                } else {
                return false;
                //}
            }
        }
        return true;
    }

    private String getFilterNumberAlphabet(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
//            if (c == '@' || c == '.' || c == '_') {
//                builder.append(c);
//            } else
            if ((c < '0' || c > '9') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                //discard this
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static void show(Context content) {
        Intent intent = new Intent(content, SignUpActivity.class);
        content.startActivity(intent);
    }
     @Override
    public void onBackPressed() {
        WelcomeActivity.show(context);
        super.onBackPressed();
    }


    private class LoginMaiku extends UserTask<String, Void, Integer> {
        @Override
        public Integer doInBackground(String... params) {
            int statusCode = 0;
            try {
                Json json = MaiKuHttpApiV1.userLogin(params[0], params[1]);
                String result = json.getString("Result");
                String ticket = json.getString("Token");
                String sndaID = json.getString("UserID");
                if ("NotActivated".equalsIgnoreCase(result)) {
                    User user = new User();
                    user.setToken(ticket);
                    user.setSndaID(sndaID);
                    Inote.notActivatedUser =user;
                    statusCode = 2;
                } else if ("Success".equalsIgnoreCase(result)) {
                    statusCode = 0;
                    User user = new User();
                    user.setToken(ticket);
                    user.setSndaID(sndaID);
                    Setting.setUser(context, user);
                    Inote.setUser(user);
                } else {
                    statusCode = 1; //Failed
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return statusCode;
        }

        public void onPostExecute(Integer statusCode) {
            removeDialog(DIALOG_LOADING);
            switch (statusCode){
                case 1:
                    showToast("登录失败,用户名或密码错误");
                    break;
                case 0:
                    MainActivity.show(context);
                    break;
                case 2:
                    ActivateUserActivity.show(context);
                    finish();
                    break;

            }
        }

    }

     LoginCallBack loginCallBack = new LoginCallBack() {
        @Override
        public void onSuccess(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String productID = jsonObject.getString("ProductID");
                String authID = jsonObject.getString("AuthenticID");
                showDialog(DIALOG_LOADING);
                new LoginMaiku().execute(productID, authID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailure(String jsonMessage) {
            showToast(getString(R.string.openapi_login_error));
        }
    };

}
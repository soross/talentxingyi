package com.snda.inote.activity;import android.app.Activity;import android.app.Dialog;import android.app.ProgressDialog;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.util.Log;import android.view.View;import android.view.View.OnClickListener;import android.widget.EditText;import android.widget.Toast;import com.snda.inote.Inote;import com.snda.inote.Login;import com.snda.inote.MaiKuHttpApiV1;import com.snda.inote.R;import com.snda.inote.io.Setting;import com.snda.inote.model.Category;import com.snda.inote.model.User;import com.snda.inote.util.Json;import com.snda.inote.util.UserTask;import com.snda.sdw.woa.callback.CallBack;import com.snda.sdw.woa.callback.LoginCallBack;import com.snda.sdw.woa.interfaces.OpenAPI;import org.json.JSONException;import org.json.JSONObject;public class LoginOAActivity extends Activity implements OnClickListener {    private static final String TAG = LoginOAActivity.class.getSimpleName();    private final static int DIALOG_LOADING = 0;    private Toast mToast;    private boolean isStartOASucess;    private Activity context = LoginOAActivity.this;    boolean isNotLoginRequest = false;    int intent_requestCode;    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.login_oa);        findViewById(R.id.login_button).setOnClickListener(this);        Intent intent = getIntent();        Bundle d = intent.getExtras();        if (d != null) {            int requestCode = d.getInt("Request_CODE");            if (requestCode == 1) {                isNotLoginRequest = true;                intent_requestCode = requestCode;            }        }        findViewById(R.id.switchButton).setOnClickListener(new OnClickListener() {            @Override            public void onClick(View view) {                Login.show(context);                finish();            }        });        initOA();    }    private void initOA() {        if (!Inote.instance.isConnected()) {            showToast(getString(R.string.connection_error));            return;        }        try {            OpenAPI.startOA("", this, new CallBack() {                @Override                public void onSuccess(String s) {                    isStartOASucess = true;                }                @Override                public void onFailure(String jsonMessage) {                    if (!Inote.instance.isConnected()) {                        showToast(getString(R.string.connection_error));                    } else {                        showToast(getString(R.string.openapi_init_error));                    }                }            });        } catch (Exception e) {            e.printStackTrace();            showToast(getString(R.string.switch_web_login));            Login.show(context);            finish();        }    }    public void onClick(View v) {        switch (v.getId()) {            case R.id.login_button:                String username = ((EditText) findViewById(R.id.login_name)).getText().toString();                String password = ((EditText) findViewById(R.id.login_pass)).getText().toString();                if (username.equals("") || password.equals("")) {                    showToast(getString(R.string.login_property_miss_error));                } else {                    if (isStartOASucess) {                        OpenAPI.login(username, password, false, context, loginCallBack);                    } else {                        initOA();                    }                }                break;        }    }    @Override    public void onBackPressed() {        WelcomeActivity.show(context);        super.onBackPressed();    }    LoginCallBack loginCallBack = new LoginCallBack() {        @Override        public void onSuccess(String s) {            try {                JSONObject jsonObject = new JSONObject(s);                String productID = jsonObject.getString("ProductID");                String authID = jsonObject.getString("AuthenticID");                showDialog(DIALOG_LOADING);                new LoginMaiku().execute(productID, authID); //Add a loading dialog            } catch (Exception e) {                e.printStackTrace();            }        }        @Override        public void onFailure(String jsonMessage) {            // {'Message':'您输入的盛大通行证和密码不匹配，请确认后重新输入','PTAccount':'d'}            Log.d(TAG, "loginCallBack onFailure,jsonMsg = " + jsonMessage);            String result = "";            try {                Json json = new Json(new JSONObject(jsonMessage));                result = json.getString("Message");            } catch (JSONException e) {                e.printStackTrace();            }            if ("null".equals(result)) {                showToast(getString(R.string.connection_error));            } else {                showToast(getString(R.string.openapi_login_error));            }        }    };    public static void show(Context context) {        Intent intent = new Intent();        intent.setClass(context, LoginOAActivity.class);        context.startActivity(intent);    }    protected Dialog onCreateDialog(int id) {        switch (id) {            case DIALOG_LOADING:                ProgressDialog dialog2 = new ProgressDialog(context);                dialog2.setMessage(getString(R.string.sync_logining));                dialog2.setIndeterminate(true);                dialog2.setCancelable(false);                return dialog2;            default:                return null;        }    }    private void showToast(String str) {        if (mToast != null) {            mToast.cancel();        }        mToast = Toast.makeText(context, str, Toast.LENGTH_LONG);        mToast.show();    }    private class LoginMaiku extends UserTask<String, Void, Integer> {        @Override        public Integer doInBackground(String... params) {            int statusCode = 0;            try {                Json json = MaiKuHttpApiV1.userLogin(params[0], params[1]);                String result = json.getString("Result");                String ticket = json.getString("Token");                String sndaID = json.getString("UserID");                if ("NotActivated".equalsIgnoreCase(result)) {                    User user = new User();                    user.setToken(ticket);                    user.setSndaID(sndaID);                    Inote.notActivatedUser = user;                    statusCode = 2;                } else if ("Success".equalsIgnoreCase(result)) {                    statusCode = 0;                    User user = new User();                    user.setToken(ticket);                    user.setSndaID(sndaID);                    Setting.setUser(context, user);                    Inote.setUser(user);                    if (isNotLoginRequest) {                        Category category = Inote.instance.getDefaultPrivateCategory();                        if (category.getNoteCategoryID() == null) {                            statusCode = 0; //because this user don't hava local category, need think about this time how do the better choose                        } else {                            statusCode = 3;                        }                    }                } else {                    statusCode = 1; //Failed                }            } catch (Exception e) {                e.printStackTrace();            }            return statusCode;        }        public void onPostExecute(Integer statusCode) {            removeDialog(DIALOG_LOADING);            switch (statusCode) {                case 1:                    showToast(getString(R.string.login_error));                    break;                case 0:                    MainActivity.show(context);                    finish();                    break;                case 2:                    ActivateUserActivity.show(context);                    finish();                    break;                case 3:                    setResult(Activity.RESULT_OK);                    finishActivity(intent_requestCode);                    finish();                    break;            }        }    }}
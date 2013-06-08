package com.snda.inote;import android.app.Activity;import android.app.AlertDialog;import android.app.Dialog;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.os.Bundle;import android.view.View;import android.webkit.*;import android.widget.Button;import android.widget.Toast;import com.snda.inote.activity.MainActivity;import com.snda.inote.activity.WelcomeActivity;import com.snda.inote.io.Setting;import com.snda.inote.model.User;public class Login extends Activity {    boolean isNotLoginRequest = false;    int intent_requestCode;    private final static int DIALOG_NOACTIVE = 0;    private Toast mToast;    private Context context = Login.this;    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.login);        findViewById(R.id.header_left_btn).setOnClickListener(handlerClickLisenter);        if (!Inote.instance.isConnected()) {            showToast(getString(R.string.sync_not_connection_error));            return;        }        Intent intent = getIntent();        Bundle d = intent.getExtras();        if (d != null) {            int requestCode = d.getInt("Request_CODE");            if (requestCode == 1) {                isNotLoginRequest = true;                intent_requestCode = requestCode;            }        }        WebView webView = (WebView) findViewById(R.id.iframeLogin);        WebSettings ws = webView.getSettings();        ws.setJavaScriptEnabled(true);        webView.setWebChromeClient(new WebChromeClient() {            @Override            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {                return super.onJsAlert(view, url, message, result);            }        });        webView.loadUrl(Consts.URL_IFRAME_LOGIN);        webView.addJavascriptInterface(new Object() {            public void setToken(String ticket, String userID) {                login(ticket, userID);            }            public void setResult(String result) {                if (result.equalsIgnoreCase("failed")) {                    Toast.makeText(Login.this, getString(R.string.snda_accout_login_error), Toast.LENGTH_SHORT).show();                } else if (result.equalsIgnoreCase("notactivated")) {                    showDialog(DIALOG_NOACTIVE);                }            }        }, "Inodroid");     //  SDOAnalyzeAgentInterface.onCreate(context);    }//    @Override//    protected void onPause() {//        super.onPause();//        SDOAnalyzeAgentInterface.onPause(context);//    }////    @Override//    protected void onDestroy() {//        super.onDestroy();//        SDOAnalyzeAgentInterface.onDestroy(context);//    }////    @Override//    protected void onResume() {//        super.onResume();//        SDOAnalyzeAgentInterface.onResume(context);//    }    Button.OnClickListener handlerClickLisenter = new Button.OnClickListener() {        public void onClick(View v) {            switch (v.getId()) {                case R.id.header_left_btn:                    WelcomeActivity.show(Login.this);                    break;            }        }    };    private void login(String ticket, String userID) {        try {            CookieSyncManager.createInstance(getApplicationContext());            CookieManager.getInstance().removeAllCookie();        } catch (Exception e) {            showToast(getString(R.string.clean_user_login_cache_error));            e.printStackTrace();        }        try {            User user = new User();            user.setToken(ticket);            user.setSndaID(userID);            Setting.setUser(Login.this, user);            Inote.setUser(user);            if (isNotLoginRequest) {                setResult(Activity.RESULT_OK);                finishActivity(intent_requestCode);            } else {                MainActivity.show(Login.this);            }        } catch (Exception e) {            showToast(getString(R.string.error_at_login_set_user));            e.printStackTrace();        }        finish();    }    public static void show(Context context) {        Intent intent = new Intent();        intent.setClass(context, Login.class);        context.startActivity(intent);    }    protected Dialog onCreateDialog(int id) {        switch (id) {            case DIALOG_NOACTIVE:                return new AlertDialog.Builder(Login.this)                        .setIcon(R.drawable.icon)                        .setTitle(R.string.app_name)                        .setMessage(getString(R.string.login_need_activate_warring))                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {                            public void onClick(DialogInterface dialog, int which) {                                WelcomeActivity.show(Login.this);                            }                        })                        .create();            default:                return null;        }    }    private void showToast(String str) {        if (mToast != null) {            mToast.cancel();        }        mToast = Toast.makeText(Login.this, str, Toast.LENGTH_LONG);        mToast.show();    }}
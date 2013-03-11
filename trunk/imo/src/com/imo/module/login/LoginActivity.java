package com.imo.module.login;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.json.JSONException;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.activity.IAppUpdate;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.organize.FirstLoadingActivity;
import com.imo.module.organize.NormalLoadingActivity;
import com.imo.network.Encrypt.StringUtils;
import com.imo.network.net.EngineConst;
import com.imo.network.net.NIOThread;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.CorpMaskItem;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.network.packages.GetCorpInfoInPacket;
import com.imo.network.packages.GetCorpInfoOutPacket;
import com.imo.network.packages.GetEmployeeProfileInPacket;
import com.imo.network.packages.GetEmployeeProfileOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.LoginInPacket;
import com.imo.network.packages.LoginOutPacket;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.IOUtil;
import com.imo.util.LogFactory;
import com.imo.util.PreferenceManager;
import com.imo.util.UpdateManager;

/**
 * 登陆界面
 */
public class LoginActivity extends AbsBaseActivityNetListener implements OnCheckedChangeListener, OnClickListener, IAppUpdate {

	private static LoginActivity instance;

	public static LoginActivity getActivity() {
		return instance;
	}

	/**
	 * 登录信息保存key
	 */
	public static final String LOGIN_NAME = "login_name";
	public static final String LOGIN_PWD = "login_pwd";
	public static final String LOGIN_REMBERPWD = "login_remberPwd";
	public static final String LOGIN_AUTOLOGIN = "login_antoLogin";
	public static final String LOGIN_KEEPONLINE = "login_keepOnline";
	public static final String REAL_NAME = "real_name";
	public static final String CORP_SHORT_NAME = "corp_short_name";
	public static final String INNER_GROUP_UC = "innergroupuc";
	public static final String INNER_GROUP_LIST_UC = "innnergrouplistuc";
	private final int requestOk = 2;

	private boolean currentUserIsFirstLogin = false;

	private String TAG = LoginActivity.class.getSimpleName();
	private final int aUntransID = 1;

	private int requestCount = 0;

	private ImageView corpLogo;
	private TextView organizationName, userName;// ---显示逻辑：

	private Button btn_delete;

	private EditText login_edit_account, login_edit_pwd;

	private CheckBox[] options;

	private int[] optionIds = {
			R.id.cb_rember_pwd, R.id.cb_redirect_login,
	};

	private ImageButton btnLogin;

	private TextView tv_register;

	private Dialog dialog;

	private DialogInterface.OnKeyListener dialogKeyListener;
	/** 此字段作用是控制界面是否接收数据，并完成跳转 */
	private boolean isAcceptData = true;
	/** 启动mNIOThread的线程对象 */
	Thread thread = new Thread(mNIOThread);

	private String[] nameAndDomain;

	public static void launch(Context c) {
		Intent intent = new Intent(c, LoginActivity.class);
		c.startActivity(intent);
	}

	@Override
	public void installViews() {
		setContentView(R.layout.login_activity);

		instance = this;

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_login_bg);
		relativeLayout.setBackgroundDrawable(new BitmapDrawable(getResources().openRawResource(R.drawable.welcome_bg)));

		corpLogo = (ImageView) findViewById(R.id.iv_corpLogo);

		organizationName = (TextView) findViewById(R.id.organizationName);
		userName = (TextView) findViewById(R.id.userName);

		btn_delete = (Button) findViewById(R.id.btn_delete);

		login_edit_account = (EditText) findViewById(R.id.login_edit_account);
		login_edit_pwd = (EditText) findViewById(R.id.login_edit_pwd);

		tv_register = (TextView) findViewById(R.id.tv_register);
		tv_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Functions.openBrowser(LoginActivity.this, "http://www.imoffice.com/reg/");
			}
		});
		options = new CheckBox[optionIds.length];

		for (int i = 0; i < optionIds.length; i++) {
			options[i] = (CheckBox) findViewById(optionIds[i]);
			options[i].setOnCheckedChangeListener(this);
		}

		btnLogin = (ImageButton) findViewById(R.id.btnLogin);

		initLoginData();

		dialogKeyListener = new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					stopLogin();
				}
				return false;
			}
		};

	}

	public void stopLogin() {
		updateViewState(btnLogin, true);
		EngineConst.isLoginSuccess = false;

		DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
		isLoginFailed = true;
		isAcceptData = false;
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
	}

	/**
	 * 在记住密码的情况下，需要初始化数据
	 */
	private void initLoginData() {
		isRemberPwd = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				LOGIN_REMBERPWD, isRemberPwd
		});
		if (isRemberPwd) {
			loginPwd = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
					LOGIN_PWD, new String()
			});
		}
		LogFactory.d(TAG, "isRemberPwd:" + isRemberPwd + ",loginPwd:" + loginPwd);

		loginName = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
				LOGIN_NAME, new String()
		});
		autoLogin = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				LOGIN_AUTOLOGIN, autoLogin
		});
		keepOnline = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				LOGIN_KEEPONLINE, keepOnline
		});
		LogFactory.d(TAG, "loginName:" + loginName + ",autoLogin:" + autoLogin + ",keepOnline:" + keepOnline);

		if (null != loginName) {
			login_edit_account.setText(loginName);
			login_edit_account.setSelection(login_edit_account.length());
		}
		if (null != loginPwd) {
			login_edit_pwd.setText(loginPwd);
			options[0].setChecked(true);
		}
		options[1].setChecked(autoLogin);

		// 加载公司信息
		String corp_name_temp = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
				CORP_SHORT_NAME, new String()
		});
		if (corp_name_temp != null && corp_name_temp.length() > 0)
			organizationName.setText(corp_name_temp);
		String name_temp = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
				REAL_NAME, new String()
		});
		if (name_temp != null && name_temp.length() > 0)
			userName.setText(name_temp);
		LogFactory.d(TAG, "corp_name_temp:" + corp_name_temp + ",name_temp:" + name_temp);

		// 加载公司logo
		byte[] b_logo = null;
		try {
			b_logo = IOUtil.readFile(Globe.corpLogo_file, this);
		} catch (Exception e) {}
		if (b_logo != null && b_logo.length > 0) {
			Bitmap bm_logo = BitmapFactory.decodeByteArray(b_logo, 0, b_logo.length);
			corpLogo.setImageBitmap(Functions.zoomImg(bm_logo, getResources().getDimension(R.dimen.login_logo_img)));
		}

		login_edit_account.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if (arg1) {
					btn_delete.setVisibility(View.VISIBLE);
				} else {
					if (login_edit_account.getText() != null && login_edit_account.getText().length() > 0) {
						btn_delete.setVisibility(View.INVISIBLE);
					}
				}
			}
		});

	}

	@Override
	public void registerEvents() {

		btn_delete.setOnClickListener(this);
		login_edit_account.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (login_edit_account.getText().toString().length() > 0) {
					btn_delete.setVisibility(View.VISIBLE);
				} else {
					btn_delete.setVisibility(View.INVISIBLE);
				}
				login_edit_pwd.setText("");

				loadAccount(login_edit_account.getText().toString().trim());

			}
		});

		if (null != dialog) {
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					updateViewState(btnLogin, true);
				}
			});
		}
	}

	/**
	 * 重置的位置：发生后，回到界面后重置
	 */
	private boolean hasHappendPwdError = false;

	@Override
	public void refresh(Object param) {
		Message msg = (Message) param;
		if (NotifyPacketArrived == msg.what) {
			HashMap<String, Object> netData = (HashMap<String, Object>) msg.obj;
			Short command = (Short) netData.get("cmd");
			switch (command) {
				case IMOCommand.IMO_LOGIN: {
					if (0 == (Short) netData.get("ret")) {
						if (!isAcceptData)
							return;
						getSelfInfo();
						getCorpInfo();
						getCorpLogo();
						getSelfHeadPic();
					} else if (115 == (Short) netData.get("ret")) {// 未测试
					} else {
						isLoginFailed = true;
						updateViewState(btnLogin, false);
						if (dialog != null && dialog.isShowing())
							dialog.dismiss();
						dialog = DialogFactory.alertDialog(mContext, "imo提示", "用户名或密码错误", new String[] {
							"确定"
						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								updateViewState(btnLogin, true);
							}
						});
						registerEvents();
						if (mGlobal.hasRunInBackground) {
							hasHappendPwdError = true;
						} else {
							dialog.show();
						}
					}

					break;
				}
				case IMOCommand.IMO_GET_CORP_INFO: {
					if (0 == (Short) netData.get("ret")) {
						organizationName.setText(Globe.corp.getShort_name());
					}
					goMainActivity();
					break;
				}
				case IMOCommand.IMO_GET_EMPLOYEE_PROFILE: {
					if (0 == (Short) netData.get("ret")) {
						userName.setText(Globe.myself.getName());
					}
					goMainActivity();
					break;
				}

				default:
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (hasHappendPwdError) {
			if (dialog != null && !dialog.isShowing()) {
				dialog.show();
				hasHappendPwdError = false;
			}
		}
	}

	private void goMainActivity() {
		if (!isAcceptData)
			return;
		// 控制是否跳转(有两处会调用到goMainActivity,以最后一次为主)
		requestCount++;
		if (requestCount < requestOk)
			return;
		String tag = (String) PreferenceManager.get("IMO-DATA" + EngineConst.uId, new String[] {
				"isFirst", "Yes"
		});
		if (tag.equals("Yes")) {
			FirstLoadingActivity.launch(mContext);
		} else {
			NormalLoadingActivity.launch(mContext);
		}
		EngineConst.isNetworkValid = true;
		if (null != dialog) {
			if (dialog.isShowing())
				dialog.dismiss();
		}

		LoginActivity.this.finish();
	}

	private void getSelfInfo() {
		int mask = 1 | (1 << 1) | (1 << 2) | (1 << 3) | (1 << 4) | (1 << 5) | (1 << 6) | (1 << 7) | (1 << 8) | (1 << 9) | (1 << 10) | (1 << 11) | (1 << 12) | (1 << 13) | (1 << 14);
		ByteBuffer bodyBuffer = GetEmployeeProfileOutPacket.GenerateEmployeeProfileBody(aUntransID, EngineConst.cId, EngineConst.uId, mask);
		GetEmployeeProfileOutPacket out = new GetEmployeeProfileOutPacket(bodyBuffer, IMOCommand.IMO_GET_EMPLOYEE_PROFILE, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	private void getCorpInfo() {// 请求公司信息
		int mask = 1 | (1 << 1) | (1 << 2) | (1 << 12) | (1 << 14) | (1 << 16) | (1 << 17) | (1 << 20);// 公司账号，公司简称，公司中文名称，公司地址，公司简介，公司电话，公司传真，公司网址
		ByteBuffer bodyBuffer = GetCorpInfoOutPacket.GenerateCorpInfoBody(EngineConst.cId, mask);
		GetCorpInfoOutPacket out = new GetCorpInfoOutPacket(bodyBuffer, IMOCommand.IMO_GET_CORP_INFO, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	private void getCorpLogo() {
		// 加载公司logo
		byte[] b_logo = null;
		try {
			b_logo = IOUtil.readFile(Globe.corpLogo_file, this);
		} catch (Exception e) {}
		// 文件中没有公司logo，则网络请求
		if (b_logo != null && b_logo.length > 0) {
			Bitmap bm_logo = BitmapFactory.decodeByteArray(b_logo, 0, b_logo.length);
			corpLogo.setImageBitmap(Functions.zoomImg(bm_logo, getResources().getDimension(R.dimen.login_logo_img)));
		} else {
			DownLoadLogoTask task = new DownLoadLogoTask();
			task.execute("");
		}
	}

	class DownLoadLogoTask extends AsyncTask<String, Void, byte[]> {
		@Override
		protected byte[] doInBackground(String... params) {
			try {
				byte[] b_logo = Functions.http_get(Functions.buildCorpLogoUrl(EngineConst.cId));
				if (b_logo != null) {
					IOUtil.saveFile(Globe.corpLogo_file, b_logo, IMOApp.getApp(), Context.MODE_PRIVATE);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}

	/** 加载个人头像 */
	private void getSelfHeadPic() {
		byte[] b_head = null;
		try {
			b_head = IOUtil.readFile(Globe.selfHeadPic_file, this);
		} catch (Exception e) {}
		// 文件中没有个人头像，则网络请求
		if (b_head != null && b_head.length > 0) {
			Globe.bm_head = BitmapFactory.decodeByteArray(b_head, 0, b_head.length);
		} else {
			DownLoadHeadPicTask task = new DownLoadHeadPicTask();
			task.execute("");
		}
	}

	class DownLoadHeadPicTask extends AsyncTask<String, Void, byte[]> {
		@Override
		protected byte[] doInBackground(String... params) {
			try {
				byte[] b_head = Functions.http_get(Functions.buildPersonPicUrl(EngineConst.cId, EngineConst.uId));
				if (b_head != null) {
					IOUtil.saveFile(Globe.selfHeadPic_file, b_head, IMOApp.getApp(), Context.MODE_PRIVATE);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}

	private String loginName;
	private String loginPwd;
	private boolean isRemberPwd = true;
	private boolean autoLogin = false;
	private boolean keepOnline = false;
	private boolean isLoginFailed = false;

	public void onClick_doLogin(View view) {
		updateViewState(btnLogin, false);
		if (isLoginFailed) {
			resetConnection();
			thread = new Thread(mNIOThread);
			isLoginFailed = false;
		}
		loginName = login_edit_account.getText().toString();
		loginPwd = login_edit_pwd.getText().toString();

		// <1> 非空验证
		if (Functions.isEmpty(loginName)) {
			updateViewState(btnLogin, false);
			dialog = DialogFactory.alertDialog(mContext, "imo提示", "用户名不能为空！", new String[] {
				"确定"
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					updateViewState(btnLogin, true);
				}

			});
			registerEvents();
			dialog.show();
			return;
		}
		if (Functions.isEmpty(loginPwd)) {
			updateViewState(btnLogin, false);
			dialog = DialogFactory.alertDialog(mContext, "imo提示", "密码不能为空！", new String[] {
				"确定"
			}, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					updateViewState(btnLogin, true);
				}

			});
			registerEvents();
			dialog.show();
			return;
		}
		int index = loginName.indexOf("@");
		if (index <= 0 || index == loginName.length() - 1 || index > 60 || loginName.length() - index >= 34) {
			updateViewState(btnLogin, false);
			dialog = DialogFactory.alertDialog(mContext, "imo提示", "用户名格式不正确！", new String[] {
				"确定"
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					updateViewState(btnLogin, true);
				}

			});
			registerEvents();
			dialog.show();
			return;
		}

		// <2> 判断当前网络的状态.
		if (!ConnectionChangeReceiver.isNetworkAvailable(mContext)) {
			dialog = DialogFactory.netErrorDialog(mContext);
			dialog.show();
			isLoginFailed = true;
			updateViewState(btnLogin, true);
			return;
		}

		if (currentUserIsFirstLogin && !Functions.isWifi()) {
			dialog = DialogFactory.alertDialog(this, "imo提示", "初次登录需要加载的数据量较大，会产生流量费用，建议使用Wi-Fi网络", new String[] {
					"继续", "取消"
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					updateViewState(btnLogin, true);
					dialog = DialogFactory.progressDialog(mContext, "正在验证，请稍后...");
					dialog.setOnKeyListener(dialogKeyListener);
					dialog.show();
					LoginTask loginTask = new LoginTask();
					loginTask.execute("");
				}
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					updateViewState(btnLogin, true);
				}
			});
			dialog.show();
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					updateViewState(btnLogin, true);
				}
			});
		} else {
			updateViewState(btnLogin, true);
			dialog = DialogFactory.progressDialog(mContext, "正在验证，请稍后...");
			dialog.setOnKeyListener(dialogKeyListener);
			dialog.show();
			LoginTask loginTask = new LoginTask();
			loginTask.execute("");
		}
	}

	private void startLoginConnection() {
		if (!ConnectionChangeReceiver.checkNet()) {
			Globe.canConnect = false;
			return;
		}
		// 启动通信线程
		if (!thread.isAlive())
			thread.start();
		requestCount = 0;
		isAcceptData = true;
		Globe.canConnect = true;
		getTcpConnection();
	}

	private void sendLogin() {
		if (!Globe.canConnect) {
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			dialog = DialogFactory.netErrorDialog(mContext);
			dialog.show();
			isLoginFailed = true;
			updateViewState(btnLogin, true);
			return;
		}
		if (!isAcceptData)
			return;
		nameAndDomain = loginName.split("@");// {"admin","4948997"}
		EngineConst.password = loginPwd;
		byte LoginType = 0;// 0代表公司账号登录，1代表域名登录
		if (nameAndDomain[1].indexOf(".") > -1)
			LoginType = 1;

		LogFactory.d(TAG, "LoginType:" + LoginType + ",nameAndDomain[1]:" + nameAndDomain[1] + ",nameAndDomain[0]:" + nameAndDomain[0]);
		ByteBuffer bodyBuffer = LoginOutPacket.GenerateLoginBody(LoginType, nameAndDomain[1], nameAndDomain[0].toLowerCase(), nameAndDomain[0].toLowerCase());
		LoginOutPacket out = new LoginOutPacket(bodyBuffer, IMOCommand.IMO_LOGIN, 0, 0);
		IMOApp.getDataEngine().addToObserverList(LoginActivity.this);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	private void updateViewState(View view, boolean clickable) {
		view.setClickable(clickable);
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean state) {
		switch (view.getId()) {
			case R.id.cb_rember_pwd:
				// 记住密码
				isRemberPwd = state;
				if (!isRemberPwd) {
					autoLogin = false;
					options[1].setChecked(autoLogin);
				}
				break;
			case R.id.cb_redirect_login:
				// 直接登录
				autoLogin = state;
				if (autoLogin) {
					isRemberPwd = true;
					options[0].setChecked(isRemberPwd);
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 保存数据
	 */
	private void saveLoginData() {
		if (isRemberPwd)
			PreferenceManager.save(Globe.SP_FILE, new String[] {
					LOGIN_PWD, loginPwd
			});

		PreferenceManager.save(Globe.SP_FILE, new String[] {
				LOGIN_NAME, loginName
		});
		PreferenceManager.save(Globe.SP_FILE, new Object[] {
				LOGIN_REMBERPWD, isRemberPwd
		});
		PreferenceManager.save(Globe.SP_FILE, new Object[] {
				LOGIN_AUTOLOGIN, autoLogin
		});
		PreferenceManager.save(Globe.SP_FILE, new Object[] {
				LOGIN_KEEPONLINE, keepOnline
		});
		PreferenceManager.save(Globe.SP_FILE, new String[] {
				CORP_SHORT_NAME, Globe.corp.getShort_name()
		});
		PreferenceManager.save(Globe.SP_FILE, new String[] {
				REAL_NAME, Globe.myself.getName()
		});
	}

	private void saveUIdAndCID() {
		PreferenceManager.save(resources.getString(R.string.init_file), new Object[] {
				resources.getString(R.string.uId), EngineConst.uId
		});
		PreferenceManager.save(resources.getString(R.string.init_file), new Object[] {
				resources.getString(R.string.cId), EngineConst.cId
		});
	}

	/**
	 * 保存所有登录过的账号
	 * 
	 * @throws JSONException
	 */
	private void saveAccount() throws JSONException {
		// 这里保存关键信息是用户的名字
		String accountInfo = Globe.myself.getName() + "+" + Globe.corp.getShort_name() + "+" + EngineConst.cId;
		PreferenceManager.save(Globe.ACCOUNT_FILE, new Object[] {
				loginName, accountInfo
		});
		// 这里保存的关键信息是用户的公司简称和公司Cid
		String corpInfo = Globe.corp.getShort_name() + "*" + EngineConst.cId;
		PreferenceManager.save(Globe.ACCOUNT_FILE, new Object[] {
				nameAndDomain[1], corpInfo
		});

	}

	private String corpName = null;

	/**
	 * 获取所有登录过的账号
	 * 
	 * @throws JSONException
	 */
	private void loadAccount(String name) {
		try {
			String account = (String) PreferenceManager.get(Globe.ACCOUNT_FILE, new Object[] {
					name, ""
			});
			if (account != null && account.length() > 1 && !account.contains("*")) {
				currentUserIsFirstLogin = false;
				String[] accountInfo = account.split("\\+");
				userName.setText(accountInfo[0]);
			} else {
				currentUserIsFirstLogin = true;
				userName.setText("用户姓名");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			int index = name.indexOf("@");
			if (name != null && index >= 0 && index < name.length()) {
				String corpAccount = name.substring(index + 1);
				if (name == corpName)
					return;
				String corpInfo = (String) PreferenceManager.get(Globe.ACCOUNT_FILE, new Object[] {
						corpAccount, ""
				});
				if (corpInfo != null && corpInfo.length() > 1) {
					String[] accountInfo = corpInfo.split("\\*");
					organizationName.setText(accountInfo[0]);

					int cid = Integer.parseInt(accountInfo[1]);
					// 加载公司logo
					byte[] b_logo = null;
					try {
						b_logo = IOUtil.readFile("corpLogo" + cid, this);
					} catch (Exception e) {

					}
					if (b_logo != null && b_logo.length > 0) {
						Bitmap bm_logo = BitmapFactory.decodeByteArray(b_logo, 0, b_logo.length);
						corpLogo.setImageBitmap(Functions.zoomImg(bm_logo, getResources().getDimension(R.dimen.login_logo_img)));
					}
					corpName = corpAccount;
				} else {
					organizationName.setText("组织简称");
					corpLogo.setImageResource(R.drawable.logo_top);
				}
			} else {
				organizationName.setText("组织简称");
				corpLogo.setImageResource(R.drawable.logo_top);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_delete:
				login_edit_account.setText("");
				login_edit_pwd.setText("");
				break;
			default:
				break;
		}
	}

	@Override
	public boolean CanAcceptHttpPacket() {
		return true;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		switch (command) {
			case IMOCommand.IMO_LOGIN:
				return true;
			case IMOCommand.IMO_GET_CORP_INFO:
				return true;
			case IMOCommand.IMO_GET_EMPLOYEE_PROFILE:
				return true;
			default:
				return false;
		}
	}

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {
		LogFactory.d(NIOThread.class.getSimpleName(), "NotifyPacketArrived and aConnectionId:" + aConnectionId + ",command:" + command);
		super.NotifyPacketArrived(aConnectionId, command);
		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId))
			switch (command) {
				case IMOCommand.IMO_LOGIN: {
					if (isAcceptData)
						analysisLoginPacket(command);
					break;
				}
				case IMOCommand.IMO_GET_CORP_INFO: {
					if (isAcceptData)
						analysisCorpInfoPacket(command);
					break;
				}
				case IMOCommand.IMO_GET_EMPLOYEE_PROFILE: {
					if (isAcceptData)
						analysisMyselfPacket(command);
					break;
				}
				default:
					break;
			}

	}

	private void analysisLoginPacket(short command) {
		LoginInPacket loginInPacket = (LoginInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		ByteBuffer buffer = loginInPacket.getBodyBuffer();

		short ret = buffer.getShort();
		try {
			if (ret == 0) {// 正常登录
				Globe.customList.clear();
				// 解析登陆结果
				EngineConst.cId = buffer.getInt();
				EngineConst.uId = buffer.getInt();
				LogFactory.d(TAG, "Login Success! cid: " + EngineConst.cId + ",uid: " + EngineConst.uId);

				// 初始化SP存储文件名
				Globe.SP_FILE = "SP" + EngineConst.uId;
				// 初始化公司logo存储文件名
				Globe.corpLogo_file = "corpLogo" + EngineConst.cId;
				Globe.selfHeadPic_file = "selfHeadPic" + EngineConst.uId;
				buffer.get(EngineConst.sessionKey);

				short num = buffer.getShort();
				Globe.ips = new String[num];
				Globe.ports = new int[num];
				for (int i = 0; i < num; i++) {
					long ip = buffer.getInt() & 0xFFFFFFFFL;
					int port = buffer.getShort();
					Globe.ips[i] = ip / 256 / 256 / 256 % 256 + "." + ip / 256 / 256 % 256 + "." + ip / 256 % 256 + "." + ip % 256;
					Globe.ports[i] = port;
					LogFactory.d(TAG, "Login Success! ip[" + i + "]:" + Globe.ips[i] + ",Globe.ports[" + i + "]:" + Globe.ports[i]);
				}
				buffer.clear();
				saveUIdAndCID();

				// 初始化数据库(由于数据库名称由UID组成，所以在这里初始化数据库)
				IMOApp.imoStorage.close();
				IMOApp.imoStorage.open(EngineConst.uId);
				EngineConst.isLoginSuccess = true;

				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.LOGINOVER);
			} else if (ret == 115) {// 强制更新{未测试}
				int temp_nameLen = buffer.getInt();
				byte[] temp_name_buffer = new byte[temp_nameLen];
				buffer.get(temp_name_buffer);
				String preUpdate_build = StringUtils.UNICODE_TO_UTF8(temp_name_buffer);
				Message msg1 = new Message();
				msg1.obj = preUpdate_build;
				mUpdateAppHandler.sendMessage(msg1);
			} else {// 无其他数据，登陆不正常

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HashMap<String, Object> netData = new HashMap<String, Object>();
			netData.put("ret", ret);
			netData.put("cmd", IMOCommand.IMO_LOGIN);
			sendMessage(NotifyPacketArrived, netData);
		}
	}

	private void analysisCorpInfoPacket(short command) {
		short ret = -1;
		try {
			GetCorpInfoInPacket getCorpInfoInPacket = (GetCorpInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
			CorpMaskItem maskItem = getCorpInfoInPacket.getMaskItem();
			ret = getCorpInfoInPacket.getRet();
			if (ret == 0) {
				Globe.corp = maskItem;
			}
			saveLoginData();
			saveAccount();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HashMap<String, Object> netData = new HashMap<String, Object>();
			netData.put("cmd", command);
			netData.put("ret", ret);
			sendMessage(NotifyPacketArrived, netData);
		}
	}

	private void analysisMyselfPacket(short command) {
		short ret = -1;
		try {
			GetEmployeeProfileInPacket getEmployeeProfileInPacket = (GetEmployeeProfileInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
			EmployeeProfileItem employeeProfileItem = getEmployeeProfileInPacket.getEmployeeItem();
			ret = getEmployeeProfileInPacket.getRet();
			if (ret == 0) {
				Globe.myself = employeeProfileItem;
				getHide_dept_ids(employeeProfileItem.getHide_dept_list());
				LogFactory.d(TAG, "隐藏的部门:" + employeeProfileItem.getHide_dept_list());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			HashMap<String, Object> netData = new HashMap<String, Object>();
			netData.put("cmd", command);
			netData.put("ret", ret);
			sendMessage(NotifyPacketArrived, netData);
		}
	}

	private void getHide_dept_ids(String hide_dept_list) {
		if (hide_dept_list == null || hide_dept_list.equals(""))
			return;
		String[] dept_ids = hide_dept_list.split("\\|");
		for (int i = 0; i < dept_ids.length; i++) {
			mGlobal.hide_dept_ids.add(Integer.parseInt(dept_ids[i]));
		}

	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
	}

	@Override
	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {
		LogFactory.d(NIOThread.class.getSimpleName(), "NotifyPacketProgress, aConnectionId = " + aConnectionId + ", command = " + command);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			IMOApp.getApp().setAppExit(true);
			Functions.backToDesk(IMOApp.getApp().mLastActivity);
			IMOApp.getApp().exitApp();
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		return super.onKeyDown(keyCode, event);
	}

	class LoginTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				startLoginConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			sendLogin();
			super.onPostExecute(result);
		}

	}

	@Override
	public void showUpdateDialog(Message msg) {
		if (msg.obj != null) {
			UpdateManager update = new UpdateManager(mContext, (String) msg.obj);
			update.showNoticeDialog();
		}
	}
}

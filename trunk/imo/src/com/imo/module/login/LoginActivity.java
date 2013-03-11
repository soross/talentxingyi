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
 * ��½����
 */
public class LoginActivity extends AbsBaseActivityNetListener implements OnCheckedChangeListener, OnClickListener, IAppUpdate {

	private static LoginActivity instance;

	public static LoginActivity getActivity() {
		return instance;
	}

	/**
	 * ��¼��Ϣ����key
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
	private TextView organizationName, userName;// ---��ʾ�߼���

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
	/** ���ֶ������ǿ��ƽ����Ƿ�������ݣ��������ת */
	private boolean isAcceptData = true;
	/** ����mNIOThread���̶߳��� */
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
	 * �ڼ�ס���������£���Ҫ��ʼ������
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

		// ���ع�˾��Ϣ
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

		// ���ع�˾logo
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
	 * ���õ�λ�ã������󣬻ص����������
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
					} else if (115 == (Short) netData.get("ret")) {// δ����
					} else {
						isLoginFailed = true;
						updateViewState(btnLogin, false);
						if (dialog != null && dialog.isShowing())
							dialog.dismiss();
						dialog = DialogFactory.alertDialog(mContext, "imo��ʾ", "�û������������", new String[] {
							"ȷ��"
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
		// �����Ƿ���ת(����������õ�goMainActivity,�����һ��Ϊ��)
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

	private void getCorpInfo() {// ����˾��Ϣ
		int mask = 1 | (1 << 1) | (1 << 2) | (1 << 12) | (1 << 14) | (1 << 16) | (1 << 17) | (1 << 20);// ��˾�˺ţ���˾��ƣ���˾�������ƣ���˾��ַ����˾��飬��˾�绰����˾���棬��˾��ַ
		ByteBuffer bodyBuffer = GetCorpInfoOutPacket.GenerateCorpInfoBody(EngineConst.cId, mask);
		GetCorpInfoOutPacket out = new GetCorpInfoOutPacket(bodyBuffer, IMOCommand.IMO_GET_CORP_INFO, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	private void getCorpLogo() {
		// ���ع�˾logo
		byte[] b_logo = null;
		try {
			b_logo = IOUtil.readFile(Globe.corpLogo_file, this);
		} catch (Exception e) {}
		// �ļ���û�й�˾logo������������
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

	/** ���ظ���ͷ�� */
	private void getSelfHeadPic() {
		byte[] b_head = null;
		try {
			b_head = IOUtil.readFile(Globe.selfHeadPic_file, this);
		} catch (Exception e) {}
		// �ļ���û�и���ͷ������������
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

		// <1> �ǿ���֤
		if (Functions.isEmpty(loginName)) {
			updateViewState(btnLogin, false);
			dialog = DialogFactory.alertDialog(mContext, "imo��ʾ", "�û�������Ϊ�գ�", new String[] {
				"ȷ��"
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
			dialog = DialogFactory.alertDialog(mContext, "imo��ʾ", "���벻��Ϊ�գ�", new String[] {
				"ȷ��"
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
			dialog = DialogFactory.alertDialog(mContext, "imo��ʾ", "�û�����ʽ����ȷ��", new String[] {
				"ȷ��"
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

		// <2> �жϵ�ǰ�����״̬.
		if (!ConnectionChangeReceiver.isNetworkAvailable(mContext)) {
			dialog = DialogFactory.netErrorDialog(mContext);
			dialog.show();
			isLoginFailed = true;
			updateViewState(btnLogin, true);
			return;
		}

		if (currentUserIsFirstLogin && !Functions.isWifi()) {
			dialog = DialogFactory.alertDialog(this, "imo��ʾ", "���ε�¼��Ҫ���ص��������ϴ󣬻�����������ã�����ʹ��Wi-Fi����", new String[] {
					"����", "ȡ��"
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					updateViewState(btnLogin, true);
					dialog = DialogFactory.progressDialog(mContext, "������֤�����Ժ�...");
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
			dialog = DialogFactory.progressDialog(mContext, "������֤�����Ժ�...");
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
		// ����ͨ���߳�
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
		byte LoginType = 0;// 0����˾�˺ŵ�¼��1����������¼
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
				// ��ס����
				isRemberPwd = state;
				if (!isRemberPwd) {
					autoLogin = false;
					options[1].setChecked(autoLogin);
				}
				break;
			case R.id.cb_redirect_login:
				// ֱ�ӵ�¼
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
	 * ��������
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
	 * �������е�¼�����˺�
	 * 
	 * @throws JSONException
	 */
	private void saveAccount() throws JSONException {
		// ���ﱣ��ؼ���Ϣ���û�������
		String accountInfo = Globe.myself.getName() + "+" + Globe.corp.getShort_name() + "+" + EngineConst.cId;
		PreferenceManager.save(Globe.ACCOUNT_FILE, new Object[] {
				loginName, accountInfo
		});
		// ���ﱣ��Ĺؼ���Ϣ���û��Ĺ�˾��ƺ͹�˾Cid
		String corpInfo = Globe.corp.getShort_name() + "*" + EngineConst.cId;
		PreferenceManager.save(Globe.ACCOUNT_FILE, new Object[] {
				nameAndDomain[1], corpInfo
		});

	}

	private String corpName = null;

	/**
	 * ��ȡ���е�¼�����˺�
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
				userName.setText("�û�����");
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
					// ���ع�˾logo
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
					organizationName.setText("��֯���");
					corpLogo.setImageResource(R.drawable.logo_top);
				}
			} else {
				organizationName.setText("��֯���");
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
			if (ret == 0) {// ������¼
				Globe.customList.clear();
				// ������½���
				EngineConst.cId = buffer.getInt();
				EngineConst.uId = buffer.getInt();
				LogFactory.d(TAG, "Login Success! cid: " + EngineConst.cId + ",uid: " + EngineConst.uId);

				// ��ʼ��SP�洢�ļ���
				Globe.SP_FILE = "SP" + EngineConst.uId;
				// ��ʼ����˾logo�洢�ļ���
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

				// ��ʼ�����ݿ�(�������ݿ�������UID��ɣ������������ʼ�����ݿ�)
				IMOApp.imoStorage.close();
				IMOApp.imoStorage.open(EngineConst.uId);
				EngineConst.isLoginSuccess = true;

				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.LOGINOVER);
			} else if (ret == 115) {// ǿ�Ƹ���{δ����}
				int temp_nameLen = buffer.getInt();
				byte[] temp_name_buffer = new byte[temp_nameLen];
				buffer.get(temp_name_buffer);
				String preUpdate_build = StringUtils.UNICODE_TO_UTF8(temp_name_buffer);
				Message msg1 = new Message();
				msg1.obj = preUpdate_build;
				mUpdateAppHandler.sendMessage(msg1);
			} else {// ���������ݣ���½������

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
				LogFactory.d(TAG, "���صĲ���:" + employeeProfileItem.getHide_dept_list());
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

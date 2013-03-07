package com.imo.module.welcome;

import java.nio.ByteBuffer;

import android.app.Dialog;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.activity.IAppUpdate;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.config.SystemSetActivity;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.NormalLoadingActivity;
import com.imo.network.Encrypt.StringUtils;
import com.imo.network.net.EngineConst;
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
import com.imo.util.LogFactory;
import com.imo.util.PreferenceManager;
import com.imo.util.SystemInfoManager;
import com.imo.util.UpdateManager;

/**
 * 欢迎界面
 */
public class WelcomeActivity extends AbsBaseActivityNetListener implements IAppUpdate {

	private final int basicId = 100;

	private ImageView iv_logo;
	private RelativeLayout rl_welcome;
	private TextView tv_text1;
	private TextView tv_text2;
	private TextView tv_text3;
	private AlphaAnimation anim;
	private Dialog dialog;
	private final int aUntransID = 4;

	private String curState;
	private String stateTrue;
	private String stateFalse;
	private String fileName;
	private String isFirstLogin;

	private String loginName;
	private String loginPwd;
	private boolean autoLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.getWindow().setFormat(PixelFormat.RGBA_8888);
		mGlobal.mScale = new SystemInfoManager().getScale(WelcomeActivity.this);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void installViews() {
		buildLayout();
		float fromAlpha = 0.1f;
		float toAlpha = 1.0f;
		anim = new AlphaAnimation(fromAlpha, toAlpha);
		anim.setDuration(2 * 1000);
		rl_welcome.startAnimation(anim);

		stateTrue = resources.getString(R.string.state_true);
		stateFalse = resources.getString(R.string.state_false);

		fileName = resources.getString(R.string.init_file);
		isFirstLogin = resources.getString(R.string.is_first_login);

		loadInitData();
	}

	private void buildLayout() {
		final double scale = IMOApp.getApp().mScale;

		rl_welcome = new RelativeLayout(this);
		rl_welcome.setBackgroundDrawable(new BitmapDrawable(getResources().openRawResource(R.drawable.welcome_bg)));
		rl_welcome.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		RelativeLayout rl_logo = new RelativeLayout(this);
		int iv_logo_id = basicId + 1;
		iv_logo = new ImageView(this);
		iv_logo.setId(iv_logo_id);
		iv_logo.setImageResource(R.drawable.welcome_logo);
		RelativeLayout.LayoutParams rl_params = new RelativeLayout.LayoutParams((int) (108 * scale), (int) (117 * scale));
		rl_params.topMargin = (int) (32 * scale);
		rl_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl_logo.addView(iv_logo, rl_params);

		ImageView Beta = new ImageView(this);
		Beta.setImageResource(R.drawable.welcome_beta);
		rl_params = new RelativeLayout.LayoutParams((int) (56 * scale), (int) (31 * scale));
		rl_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		rl_params.addRule(RelativeLayout.RIGHT_OF, iv_logo_id);
		rl_logo.addView(Beta, rl_params);

		// i'm office
		tv_text1 = new TextView(this);
		tv_text1.setText("i'm office");
		tv_text1.setTextSize(26);
		tv_text1.setTextColor(getResources().getColor(R.color.welcome_tv_text1_color));

		rl_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl_params.topMargin = (int) (10 * scale);
		rl_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl_params.addRule(RelativeLayout.BELOW, iv_logo_id);
		rl_logo.addView(tv_text1, rl_params);

		rl_params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rl_params.topMargin = (int) (200 * scale);
		rl_welcome.addView(rl_logo, rl_params);

		// CopyRight © 2006-2012 imoffice.com
		int tv_text3_id = basicId + 2;
		tv_text3 = new TextView(this);
		tv_text3.setId(tv_text3_id);

		tv_text3.setText(getResources().getString(R.string.copyright_info1));
		tv_text3.setTextSize(14);
		tv_text3.setTextColor(getResources().getColor(R.color.welcome_tv_text3_color));

		rl_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl_params.bottomMargin = (int) (6 * scale);
		rl_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl_welcome.addView(tv_text3, rl_params);

		// 上海易睦版权所有
		tv_text2 = new TextView(this);
		tv_text2.setText("易睦网络·版权所有");
		tv_text2.setTextSize(14);
		tv_text2.setTextColor(getResources().getColor(R.color.welcome_tv_text2_color));

		rl_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl_params.bottomMargin = (int) (6 * scale);
		rl_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl_params.addRule(RelativeLayout.ABOVE, tv_text3_id);
		rl_welcome.addView(tv_text2, rl_params);

		setContentView(rl_welcome);
	}

	private void loadInitData() {
		// 控制是否为第一次运行软件。
		curState = (String) com.imo.util.PreferenceManager.get(fileName, new String[] {
				isFirstLogin, stateFalse
		});
		EngineConst.uId = (Integer) com.imo.util.PreferenceManager.get(fileName, new Object[] {
				resources.getString(R.string.uId), 0
		});
		EngineConst.cId = (Integer) com.imo.util.PreferenceManager.get(fileName, new Object[] {
				resources.getString(R.string.cId), 0
		});

		Globe.SP_FILE = "SP" + EngineConst.uId;
		Globe.corpLogo_file = "corpLogo" + EngineConst.cId;
		Globe.selfHeadPic_file = "selfHeadPic" + EngineConst.uId;

		Globe.is_shock = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				SystemSetActivity.IS_SHOCK, Globe.is_shock
		});
		Globe.is_sound = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				SystemSetActivity.IS_SOUND, Globe.is_sound
		});
		Functions.initSoundPool();
		Globe.is_notification = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				SystemSetActivity.IS_NOTIFICATION, Globe.is_notification
		});

		loginName = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
				LoginActivity.LOGIN_NAME, new String()
		});
		loginPwd = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
				LoginActivity.LOGIN_PWD, new String()
		});
		autoLogin = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
				LoginActivity.LOGIN_AUTOLOGIN, autoLogin
		});
	}

	@Override
	protected boolean needObserver() {
		return false;
	}

	@Override
	protected void registerEvents() {
		anim.setAnimationListener(new WelcomeAnimListerner());
	}

	private class WelcomeAnimListerner implements Animation.AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (stateTrue.equals(curState)) {// 不是第一次
				if (autoLogin) {// 自动登录
					dialog = DialogFactory.progressDialog(mContext, "正在验证，请稍后...");
					dialog.show();
					waitServiceStart();
					autoLogin();
				} else {// 非自动登录
					LoginActivity.launch(mContext);
					finish();
				}
			} else {// 第一次使用软件
				com.imo.util.PreferenceManager.save(fileName, new String[] {
						isFirstLogin, stateTrue
				});
				NewFeaturesActivity.launch(mContext);
				finish();
			}
		}
	}

	private void waitServiceStart() {
		while (AppService.getService() == null) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mNIOThread = AppService.getService().getNIOThreadInstance();

		Globe.canConnect = true;
		getTcpConnection();
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

	private void autoLogin() {
		// 判断当前网络的状态.
		if (!ConnectionChangeReceiver.isNetworkAvailable(mContext)) {
			LoginActivity.launch(this);
			dialog.dismiss();
			finish();
			// dialog = DialogFactory.netErrorDialog(mContext);
			// dialog.show();
			return;
		}

		/* 启动mNIOThread的线程对象 */
		Thread thread = new Thread(mNIOThread);
		// 启动通信线程
		if (!thread.isAlive())
			thread.start();
		// 开始登录
		String[] nameAndDomain = loginName.split("@");
		EngineConst.password = loginPwd;
		// 0代表公司账号登录，1代表域名登录
		byte flag = 0;
		if (nameAndDomain[1].indexOf(".") > -1)
			flag = 1;
		ByteBuffer bodyBuffer = LoginOutPacket.GenerateLoginBody(flag, nameAndDomain[1], nameAndDomain[0].toLowerCase(), nameAndDomain[0].toLowerCase());
		LoginOutPacket out = new LoginOutPacket(bodyBuffer, IMOCommand.IMO_LOGIN, 0, 0);
		IMOApp.getDataEngine().addToObserverList(this);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	private void analysisLoginPacket(short command) {
		dialog.dismiss();
		LoginInPacket loginInPacket = (LoginInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		ByteBuffer buffer = loginInPacket.getBodyBuffer();
		short ret = buffer.getShort();
		try {
			if (ret == 0) {// 正常登录
				Globe.customList.clear();
				// 解析登陆结果
				EngineConst.cId = buffer.getInt();
				EngineConst.uId = buffer.getInt();
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
				}
				buffer.clear();

				// 初始化数据库(由于数据库名称由UID组成，所以在这里初始化数据库)
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
				return;
			} else {// 无其他数据，登陆不正常
				resetConnection();
				LoginActivity.launch(mContext);
				finish();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getSelfInfo();
	}

	private void getSelfInfo() {
		int mask = 1 | (1 << 1) | (1 << 2) | (1 << 3) | (1 << 4) | (1 << 5) | (1 << 6) | (1 << 7) | (1 << 8) | (1 << 9) | (1 << 10) | (1 << 11) | (1 << 12) | (1 << 13) | (1 << 14);
		ByteBuffer bodyBuffer = GetEmployeeProfileOutPacket.GenerateEmployeeProfileBody(aUntransID, EngineConst.cId, EngineConst.uId, mask);
		GetEmployeeProfileOutPacket out = new GetEmployeeProfileOutPacket(bodyBuffer, IMOCommand.IMO_GET_EMPLOYEE_PROFILE, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			goMainActivity();
		}
	}

	private void goMainActivity() {
		EngineConst.isNetworkValid = true;
		NormalLoadingActivity.launch(mContext);
		if (dialog.isShowing())
			dialog.dismiss();
		finish();
	}

	private void analysisMyselfPacket(short command) {
		short ret = -1;
		try {
			GetEmployeeProfileInPacket getEmployeeProfileInPacket = (GetEmployeeProfileInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
			EmployeeProfileItem employeeProfileItem = getEmployeeProfileInPacket.getEmployeeItem();
			ret = getEmployeeProfileInPacket.getRet();
			if (ret == 0) {
				Globe.myself = employeeProfileItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			getCorpInfo();
		}
	}

	private void getCorpInfo() {// 请求公司信息
		int mask = 1 | (1 << 1) | (1 << 2) | (1 << 12) | (1 << 14) | (1 << 16) | (1 << 17) | (1 << 20);// 公司账号，公司简称，公司中文名称，公司地址，公司简介，公司电话，公司传真，公司网址
		ByteBuffer bodyBuffer = GetCorpInfoOutPacket.GenerateCorpInfoBody(EngineConst.cId, mask);
		GetCorpInfoOutPacket out = new GetCorpInfoOutPacket(bodyBuffer, IMOCommand.IMO_GET_CORP_INFO, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {
		super.NotifyPacketArrived(aConnectionId, command);
		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId))
			switch (command) {
				case IMOCommand.IMO_LOGIN: {
					LogFactory.e("WelcomeActivity", "IMO_LOGIN");
					analysisLoginPacket(command);
					break;
				}
				case IMOCommand.IMO_GET_CORP_INFO: {
					analysisCorpInfoPacket(command);
					break;
				}
				case IMOCommand.IMO_GET_EMPLOYEE_PROFILE: {
					analysisMyselfPacket(command);
					break;
				}
				default:
					break;
			}

	}

	@Override
	protected boolean needSendRecoverNotice() {
		return false;
	}

	@Override
	public void refresh(Object param) {
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {

	}

	@Override
	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			// IMOApp.getApp().exitApp();
			resetConnection();
			LoginActivity.launch(mContext);
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void showUpdateDialog(Message msg) {
		if (msg.obj != null) {
			UpdateManager update = new UpdateManager(mContext, (String) msg.obj);
			update.showNoticeDialog();
		}
	}
}

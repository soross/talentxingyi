package org.talentware.android.module.base;

import org.talentware.android.comm.util.LogFactory;
import org.talentware.android.global.Globe;
import org.talentware.android.global.IMOApp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * 非网络通信Activity抽象基础类，实现界面的更新,封装回退逻辑。
 */
public abstract class AbsBaseActivity extends Activity {

	protected IMOApp mGlobal = IMOApp.getApp();

	protected Context mContext;

	protected Resources resources;

	protected Notification notice;

	protected NotificationManager notificationManager;

	@Override
	protected void onStart() {
		super.onStart();
		Globe.showActivityCount++;
		LogFactory.d("冯小卫", "onStart------------------>");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (needObserver()) {
			mGlobal.addAbsBaseActivity(this);
		}

		initBaseData();

		installViews();

		registerEvents();

	}

	/**
	 * 初始化连接 Empty Method
	 */
	protected void initConnection() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setContentView(int id) {
		// LayoutInflater inflater = LayoutInflater.from(AbsBaseActivity.this);
		//
		// inflater.inflate(id, baseLayout);
	}

	private void initBaseData() {
		mContext = this;
		resources = getResources();
		notificationManager = mGlobal.getNoticeManager();
	}

	/**
	 * 是否发送Activity恢复消息
	 * 
	 * @return
	 */
	protected boolean needSendRecoverNotice() {
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Globe.showActivityCount--;
		LogFactory.d("冯小卫", "onStop------------------>");
		if (Globe.showActivityCount == 0 && !mGlobal.isAppExit()) {
			if (needSendRecoverNotice()) {
				mGlobal.hasRunInBackground = true;// //标识未后台运行
			}
		}
	}

	@Override
	protected void onResume() {
		mGlobal.mLastActivity = this;

		mGlobal.hasRunInBackground = false;
		super.onResume();

//		mGlobal.clearAllNotice();
	}

	protected boolean needObserver() {
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (needObserver()) {
			IMOApp.getApp().removeAbsBaseActivity(this);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	protected abstract void installViews();

	protected abstract void registerEvents();

	public abstract void refresh(Object param);

}

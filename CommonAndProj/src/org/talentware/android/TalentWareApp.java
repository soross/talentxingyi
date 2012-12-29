package org.talentware.android;

import org.talentware.android.broadcast.ConnectionChangeReceiver;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class TalentWareApp extends Application {

	/** 监听网络状态改变类 */
	private ConnectionChangeReceiver mConnectionChangeReceiver;

	private TalentWareApp mInstance;

	public synchronized TalentWareApp getTalentWareApp() {
		if (mInstance == null) {
			mInstance = this;
		}
		return mInstance;
	}

	public void onCreate() {

		// 注册监听网络状态改变类
		mConnectionChangeReceiver = new ConnectionChangeReceiver();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mConnectionChangeReceiver, mFilter);
	}

}

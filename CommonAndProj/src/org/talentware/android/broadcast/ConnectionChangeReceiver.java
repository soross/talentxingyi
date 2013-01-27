package org.talentware.android.broadcast;

import org.talentware.android.util.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "ConnectionChangeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Logger.d(TAG, "网络状态已经改变");
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				String name = info.getTypeName();
				Logger.d(TAG, "info.getType()：" + info.getType());
				Logger.d(TAG, "info.getTypeName()：" + info.getTypeName());
				Logger.d(TAG, "info.getExtraInfo()：" + info.getExtraInfo());
				Logger.d(TAG, "info.getSubtype()：" + info.getSubtype());
				Logger.d(TAG, "info.getSubtypeName()：" + info.getSubtypeName());
				Logger.d(TAG, "info.getReason()：" + info.getReason());
				Logger.d(TAG, "info.getState()：" + info.getState());
				Logger.d(TAG, "info.getDetailedState()：" + info.getDetailedState());
			} else {
				Logger.d(TAG, "没有可用网络");
			}
		}
	}

}

package org.talentware.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StallReceiver extends BroadcastReceiver {

	private static final String TAG = "StallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {// install
			String packageName = intent.getDataString();
			Log.i(TAG, "安装了 :" + packageName);
		}
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {// uninstall
			String packageName = intent.getDataString();
			Log.i(TAG, "卸载了 :" + packageName);
		}
	}
}

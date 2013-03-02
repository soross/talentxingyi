package com.imo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imo.global.IMOApp;

/**
 * 关机广播:用于解决特殊的手机通知不自动关闭问题
 */
public class ShutdownReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		LogFactory.d("xxxShutdownxxx", "Shutdown ---------------------------->");

		if (IMOApp.getApp() != null && IMOApp.getApp().notificationManager != null) {
			IMOApp.getApp().notificationManager.cancel(NoticeManager.TYPE_NOTICE_APP_ONGOING);
			IMOApp.getApp().notificationManager.cancel(NoticeManager.TYPE_NOTICE_NEW_NEWS);
		}
	}
}

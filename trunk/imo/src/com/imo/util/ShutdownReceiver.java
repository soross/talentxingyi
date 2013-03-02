package com.imo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.imo.global.IMOApp;

/**
 * �ػ��㲥:���ڽ��������ֻ�֪ͨ���Զ��ر�����
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

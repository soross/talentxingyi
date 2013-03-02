package com.imo.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * �����㲥:���ڽ��������ֻ�֪ͨ���Զ��ر�����
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Toast.makeText(context, "BootCompletedReceiver", 1).show();
		Log.d("BootCompletedReceiver", "BootCompletedReceiver ---------------------------->");
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(0x7f030015);
		notificationManager.cancel(0x7f03000a);
	}
}

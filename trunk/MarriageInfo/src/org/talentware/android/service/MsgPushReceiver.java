package org.talentware.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import com.mobile.app.main.GEInstance;
import org.talentware.android.R;

public class MsgPushReceiver extends BroadcastReceiver {

    private static final String TAG = "MsgPushReceiver";

    // TODO 改掉,10分钟请求一次
    public static final int INTERVAL = 1000 * 60 * 60;

    @Override
    public void onReceive(final Context context, Intent intent) {
        GEInstance geInstance = new GEInstance();
        geInstance.loadPushAd();
        geInstance.setNotificationIcon(R.drawable.icon);
    }
}

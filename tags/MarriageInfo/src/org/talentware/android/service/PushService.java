package org.talentware.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-20
 * Time: 上午11:16
 * To change this template use File | Settings | File Templates.
 */
public class PushService extends Service {

    private TelephonyManager telephonyManager;

    private PhoneStateListener phoneStateListener;

    private BroadcastReceiver connectivityReceiver;

    private String TAG = "NotificationService";

    public static int CURRENT_TIME = 0;

    public void connect() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MsgPushReceiver.class);
        int requestCode = 0;
        PendingIntent pendIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 5秒后发送广播，然后每个10秒重复发广播。广播都是直接发到AlarmReceiver的
        long triggerAtTime = SystemClock.elapsedRealtime() + 5 * 1000;
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, MsgPushReceiver.INTERVAL, pendIntent);

    }


    public void disconnect() {
    }

    public PushService() {

    }

    public void onCreate() {
        registerConnectivityReceiver();
        connect();
    }

    private void registerConnectivityReceiver() {
//        connectivityReceiver = new ConnectivityReceiver(this);
//        phoneStateListener = new PhoneStateChangeListener(this);
//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(connectivityReceiver, filter);
    }

    private void unregisterConnectivityReceiver() {
//        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
//        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        unregisterConnectivityReceiver();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        disconnect();
        return super.onUnbind(intent);
    }
}

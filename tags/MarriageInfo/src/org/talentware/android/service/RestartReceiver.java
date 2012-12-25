package org.talentware.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * TODO need test
 *
 * @author XSS
 */
public class RestartReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";


    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.i("RestartReceiver", "==========================>>>>>>>>>>>>>>>>>>>>>>>>>" +
                "RestartReceiver Action is " + arg1.getAction());
        if (arg1.getAction().equals(ACTION)) {
            Log.i("RestartReceiver", "==========================>>>>>>>>>>>>>>>>>>>>>>>>>RestartReceiver Start");
            Intent intent = new Intent(context, PushService.class);
            intent.setAction(PushService.class.getName());
            context.startService(intent);
        }
    }

}

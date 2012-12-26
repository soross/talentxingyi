package org.talentware.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.igexin.sdk.Consts;

public class DianYingSdkMsgReceiver extends BroadcastReceiver {
	String TAG = "DianYingSdkMsgReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		switch (bundle.getInt(Consts.CMD_ACTION)) {
		case Consts.GET_CLIENTID:// 初始化SDK成功
			// get clientid
			// DianYingPushDemoActivity.cid.setText("clientid="+bundle.getString("clientid"));
			Toast.makeText(context,
					"init sdk success:" + bundle.getString("clientid"),
					Toast.LENGTH_SHORT).show();
			break;
		}
	}
}

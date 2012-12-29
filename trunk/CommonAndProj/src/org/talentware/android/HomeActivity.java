package org.talentware.android;

import org.talentware.android.broadcast.ConnectionChangeReceiver;

import android.app.Activity;
import android.os.Bundle;

public class HomeActivity extends Activity {

	/** 监听网络状态改变类 */
	private ConnectionChangeReceiver mConnectionChangeReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}
}

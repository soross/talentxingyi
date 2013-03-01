package com.imo.module.config;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.imo.R;
import com.imo.activity.AbsBaseActivity;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.view.SettingItemView;
import com.imo.view.SettingItemView.OnSettingItemClickListener;
import com.imo.view.TitleBar;

/**
 * Õ®÷™…Ë÷√
 * 
 * @author CaixiaoLong
 *
 */
public class NotificationSetActivity extends AbsBaseActivityNetListener implements OnSettingItemClickListener{
	
	private SettingItemView contact_msg_push;
	
	private SettingItemView notice_receiver_scope;
	
	public static void launch(Context c) {
		Intent intent = new Intent(c, NotificationSetActivity.class);
		c.startActivity(intent);
	}
	

	@Override
	protected void installViews() {
		
		setContentView(R.layout.notificationset_activity);
		
		contact_msg_push =(SettingItemView) findViewById(R.id.contact_msg_push);
		
		notice_receiver_scope =(SettingItemView) findViewById(R.id.notice_receiver_scope);
		

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back), 
				resources.getString(R.string.notice_setting));
		
		mTitleBar.setLeftBtnListene(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	@Override
	protected void registerEvents() {
		// TODO Auto-generated method stub
		contact_msg_push.setOnClickListener(this);
		notice_receiver_scope.setOnClickListener(this);
	}

	@Override
	public void refresh(Object param) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onSettingItemClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.contact_msg_push:
			
			break;
		case R.id.notice_receiver_scope:
			
			break;

		default:
			break;
		}
	}


	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub
		
	}

}

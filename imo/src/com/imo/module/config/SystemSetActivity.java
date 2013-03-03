package com.imo.module.config;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.activity.IAppUpdate;
import com.imo.global.Globe;
import com.imo.module.welcome.NewFeaturesActivity;
import com.imo.util.PreferenceManager;
import com.imo.util.UpdateManager;
import com.imo.view.SettingItemView;
import com.imo.view.SettingItemView.OnSettingItemClickListener;

/**
 * 系统设置
 * 
 * @author CaixiaoLong
 * 
 */
public class SystemSetActivity extends AbsBaseActivityNetListener implements OnSettingItemClickListener,IAppUpdate {

	private SettingItemView[] mSettingItemViews = new SettingItemView[7];
	public static final String IS_SHOCK = "is_shock";
	public static final String IS_SOUND = "is_sound";
	public static final String IS_NOTIFICATION = "is_notification";

	private int[] item_ids = { R.id.notice_setting, R.id.sound_prompt,
			R.id.vibrate_prompt, R.id.state_setting, R.id.new_features,
			R.id.app_update, R.id.about_app };

	public static void launch(Context c) {
		Intent intent = new Intent(c, SystemSetActivity.class);
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		setContentView(R.layout.systemset_activity);

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back),
				resources.getString(R.string.system_setting));

		mTitleBar.setLeftBtnListene(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		for (int i = 0; i < mSettingItemViews.length; i++) {
			mSettingItemViews[i] = (SettingItemView) findViewById(item_ids[i]);
		}
		
		mSettingItemViews[0].initCheckedState(Globe.is_notification);
		mSettingItemViews[1].initCheckedState(Globe.is_sound);
		mSettingItemViews[2].initCheckedState(Globe.is_shock);

	}
	
	@Override
	public void showUpdateDialog(Message msg) {
		if (msg.obj != null) {
			UpdateManager update = new UpdateManager(mContext, (String) msg.obj);
			update.showNoticeDialog();
		}else{
			hasRequested = false;
			Toast.makeText(mContext, getString(R.string.app_no_update),Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void registerEvents() {

		for (int i = 0; i < mSettingItemViews.length; i++) {
			mSettingItemViews[i].setOnClickListener(this);
		}

	}

	@Override
	public void refresh(Object param) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSettingItemClick(View v) {
		Bundle bundle;

		switch (v.getId()) {
		case R.id.notice_setting:
		{
			String tip = null;
			if (Globe.is_notification) {
				tip = "已经取消通知提示";
			} else {
				tip = "已经设置通知提示";
			}
			Globe.is_notification = !Globe.is_notification;
			PreferenceManager.save(Globe.SP_FILE, new Object[] { IS_NOTIFICATION,
					Globe.is_notification });
			Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.sound_prompt: {
			String tip = null;
			if (Globe.is_sound) {
				tip = "已经取消声音提示";
			} else {
				tip = "已经设置声音提示";
			}
			Globe.is_sound = !Globe.is_sound;
			PreferenceManager.save(Globe.SP_FILE, new Object[] { IS_SOUND,
					Globe.is_sound });
			Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.vibrate_prompt:
		{
			String tip = null;
			if (Globe.is_shock) {
				tip = "已经取消震动提示";
			} else {
				tip = "已经设置震动提示";
			}
			Globe.is_shock = !Globe.is_shock;
			PreferenceManager.save(Globe.SP_FILE, new Object[] { IS_SHOCK,
					Globe.is_shock });
			Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
			break;
		}
		case R.id.state_setting:

			StateSetActivity.launch(mContext);
			break;
		case R.id.new_features:
			bundle = new Bundle();
			bundle.putBoolean("isFromSetting", true);
			NewFeaturesActivity.launch(mContext, bundle);
			break;
		case R.id.app_update:
			if (!hasRequested) {
				hasRequested = !hasRequested;
				doRequestAppVersion();
			}
			break;
		case R.id.about_app:
			AboutActivity.launch(mContext);
			break;

		default:
			break;
		}
	}
	
	public static boolean hasRequested = false;


//	@Override
//	public boolean CanAcceptPacket(int command) {
//		
//		super.CanAcceptPacket(command);
//		
//		if (command == IMOCommand.IMO_UPDATE_VERSION) {
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub
	}

}

package com.imo.module.config;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.global.Globe;
import com.imo.network.net.EngineConst;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.util.ImageUtil;
import com.imo.util.LogFactory;
import com.imo.view.SettingItemView;
import com.imo.view.SettingItemView.OnSettingItemClickListener;

/**
 * 个人设置
 */
public class UserConfigActivity extends AbsBaseActivityNetListener implements OnSettingItemClickListener {

	private ImageView iv_userFace;

	private TextView tv_userName;

	private TextView tv_userPosition;

	private View layout_edit_worksign;

	private TextView tv_worksign;

	private SettingItemView my_profile;

	private SettingItemView system_config;

	private String work_sign = "";

	public static void launch(Context c) {
		Intent intent = new Intent(c, UserConfigActivity.class);
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		setContentView(R.layout.userconfig_activity);
		iv_userFace = (ImageView) findViewById(R.id.iv_user_face);
		tv_userName = (TextView) findViewById(R.id.tv_user_name);
		tv_userPosition = (TextView) findViewById(R.id.tv_user_position);

		findViewById(R.id.iv_edit_worksign);
		findViewById(R.id.worksign_view);
		layout_edit_worksign = findViewById(R.id.layout_edit_worksign);

		tv_worksign = (TextView) findViewById(R.id.tv_worksign_content);

		my_profile = (SettingItemView) findViewById(R.id.my_profile);
		system_config = (SettingItemView) findViewById(R.id.system_config);

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back), resources.getString(R.string.my_profil_setting));

	}

	@Override
	protected void onResume() {
		super.onResume();

		updateFace(EngineConst.isNetworkValid);
		work_sign = Globe.myself == null ? "" : Globe.myself.getSign();
		tv_worksign.setText(work_sign);
	}

	/**
	 * 更新头像
	 * 
	 * @param networkIsConnected
	 */
	private void updateFace(boolean networkIsConnected) {
		float face_width = getResources().getDimension(R.dimen.middle_title_face_height);
		ImageUtil.changeFaceByUidForNetState(iv_userFace, EngineConst.uId, face_width, networkIsConnected);
	}

	@Override
	protected void registerEvents() {

		initData();

		layout_edit_worksign.setOnClickListener(editWorksignListener());

		my_profile.setOnClickListener(this);

		system_config.setOnClickListener(this);

		mTitleBar.setLeftBtnListene(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	/**
	 * Edit WorkSign
	 * 
	 * @return
	 */
	private OnClickListener editWorksignListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("sign", work_sign);
				WorkSignActivity.launch(mContext, bundle);
			}
		};
	}

	EmployeeProfileItem loginUserInfo = Globe.myself;

	private String TAG = "UserConfig";

	private void initData() {

		if (Globe.bm_head != null) {
			LogFactory.d(TAG, "bm_head not null");
			iv_userFace.setImageBitmap(Globe.bm_head);
		}

		if (loginUserInfo != null) {
			tv_userName.setText(loginUserInfo.getName() != null ? loginUserInfo.getName() : "");
			tv_userPosition.setText(loginUserInfo.getPos() != null ? loginUserInfo.getPos() : "");
			work_sign = loginUserInfo.getSign() != null ? loginUserInfo.getSign() : "";
		}

	}

	@Override
	public void refresh(Object param) {

	}

	@Override
	public void onSettingItemClick(View v) {

		Bundle bundle = new Bundle();

		switch (v.getId()) {
			case R.id.my_profile:
				bundle.putInt("cid", EngineConst.cId);
				bundle.putInt("uid", EngineConst.uId);
				CardActivity.launch(mContext, bundle);
				break;
			case R.id.system_config:
				SystemSetActivity.launch(mContext);
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

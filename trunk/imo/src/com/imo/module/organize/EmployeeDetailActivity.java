package com.imo.module.organize;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.config.CardActivity;
import com.imo.module.dialogue.ChatRecordActivity;
import com.imo.module.dialogue.DialogueActivity;
import com.imo.network.net.EngineConst;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.network.packages.GetEmployeeProfileInPacket;
import com.imo.network.packages.GetEmployeeProfileOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.util.ImageUtil;
import com.imo.view.SettingItemView;
import com.imo.view.SettingItemView.OnSettingItemClickListener;

/**
 * 员工的详细信息
 */
public class EmployeeDetailActivity extends AbsBaseActivityNetListener implements OnSettingItemClickListener {

	private final int aUntransID = 11;
	private ImageView iv_userFace;

	private TextView tv_userName;

	private TextView tv_userPosition;

	private Button btn_begin_dialogue;

	private TextView tv_worksign_content;

	private SettingItemView see_ta_card;

	private SettingItemView see_dialogue_record;

	private int cid = EngineConst.cId;
	private int uid = EngineConst.uId;
	private String mName = "";

	// ==========================
	public static void launch(Context c) {
		Intent intent = new Intent(c, EmployeeDetailActivity.class);
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		setContentView(R.layout.employee_detail_activity);
		iv_userFace = (ImageView) findViewById(R.id.iv_user_face);
		tv_userName = (TextView) findViewById(R.id.tv_user_name);
		tv_userPosition = (TextView) findViewById(R.id.tv_user_position);
		btn_begin_dialogue = (Button) findViewById(R.id.btn_begin_dialogue);

		tv_worksign_content = (TextView) findViewById(R.id.tv_worksign_content);

		see_ta_card = (SettingItemView) findViewById(R.id.see_ta_card);

		see_dialogue_record = (SettingItemView) findViewById(R.id.see_dialogue_record);

		// ===============初始化数据===========================
		initBundleData();

		loadHeadPic();
		tv_userName.setText(mName);
		if (uid == EngineConst.uId) {
			btn_begin_dialogue.setVisibility(View.GONE);
		} else {
			btn_begin_dialogue.setVisibility(View.VISIBLE);
		}

		if (uid != EngineConst.uId) {
			getEmployeeInfo(cid, uid);
		} else {
			tv_userPosition.setText(Globe.myself.getPos());
			tv_worksign_content.setText(Globe.myself == null ? "" : Globe.myself.getSign());
		}
	}

	/**
	 * 记载头像
	 */
	private void loadHeadPic() {
		float face_width = getResources().getDimension(R.dimen.middle_title_face_height);
		ImageUtil.changeFaceByUid(iv_userFace, uid, face_width, false);
	}

	private void initBundleData() {
		Bundle data = getIntent().getExtras();
		if (data != null) {
			cid = data.getInt("cid");
			uid = data.getInt("uid");
			mName = data.getString("name");
		} else {
			if (Globe.myself != null) {
				cid = EngineConst.cId;
				uid = EngineConst.uId;
				mName = Globe.myself.getName();
				Globe.myself.getPos();
			}
		}
	}

	@Override
	protected void registerEvents() {
		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back), resources.getString(R.string.detail));

		mTitleBar.setLeftBtnListene(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		see_ta_card.setOnClickListener(this);
		see_dialogue_record.setOnClickListener(this);

		btn_begin_dialogue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DialogueActivity.class);
				intent.putExtras(getIntent().getExtras());
				startActivity(intent);
			}
		});
	}

	@Override
	public void refresh(Object param) {
		Message msg = (Message) param;
		Map<String, Object> result = (Map<String, Object>) msg.obj;
		if (NotifyPacketArrived == msg.what) {
			switch ((Short) result.get("cmd")) {
				case IMOCommand.IMO_GET_EMPLOYEE_PROFILE: {
					if ((Short) result.get("ret") == 0) {
						EmployeeProfileItem employeeProfileItem = (EmployeeProfileItem) result.get("employeeProfileItem");
						tv_userPosition.setText(employeeProfileItem.getPos());
						tv_worksign_content.setText(employeeProfileItem.getSign());
						Globe.employeeProfileItems.put(uid, employeeProfileItem);
					} else {
						Toast.makeText(this, "读取个人资料失败", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		}

	}

	@Override
	public void onSettingItemClick(View v) {

		Bundle dataBundle = null;

		switch (v.getId()) {
			case R.id.see_ta_card:
				dataBundle = new Bundle();
				dataBundle.putInt("cid", cid);
				dataBundle.putInt("uid", uid);
				dataBundle.putString("name", mName);
				// bundle1.putBoolean("sex", isBoy);
				CardActivity.launch(mContext, dataBundle);
				// CardActivity.launch(mContext, getIntent().getExtras());
				break;
			case R.id.see_dialogue_record:
				dataBundle = new Bundle();
				dataBundle.putInt("aboutCid", cid);
				dataBundle.putInt("aboutUid", uid);
				dataBundle.putString("aboutName", mName);
				// bundle.putBoolean("aboutSex", isBoy);
				Intent intent = new Intent(EmployeeDetailActivity.this, ChatRecordActivity.class);
				intent.putExtras(dataBundle);
				startActivity(intent);
				break;
			default:
				break;
		}
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {

	}

	@Override
	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {

	}

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {
		super.NotifyPacketArrived(aConnectionId, command);
		switch (command) {
			case IMOCommand.IMO_GET_EMPLOYEE_PROFILE: {
				short ret = -1;
				EmployeeProfileItem employeeProfileItem = null;
				try {
					GetEmployeeProfileInPacket getEmployeeProfileInPacket = (GetEmployeeProfileInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
					employeeProfileItem = getEmployeeProfileInPacket.getEmployeeItem();
					ret = getEmployeeProfileInPacket.getRet();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("cmd", command);
					result.put("ret", ret);
					result.put("employeeProfileItem", employeeProfileItem);
					sendMessage(NotifyPacketArrived, result);
				}

				break;
			}
			default:
				break;
		}
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		switch (command) {
			case IMOCommand.IMO_GET_EMPLOYEE_PROFILE:
				return true;
			default:
				return false;
		}
	}

	private void getEmployeeInfo(int cid, int uid) {
		EmployeeProfileItem employeeProfileItem = Globe.employeeProfileItems.get(uid);
		if (employeeProfileItem != null) {
			tv_userPosition.setText(employeeProfileItem.getPos());
			tv_worksign_content.setText(employeeProfileItem.getSign());
			return;
		}
		if (!ConnectionChangeReceiver.isNetworkAvailable(mContext)) {
			Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
			return;
		}

		int mask = (1 << 4) | 1 | (1 << 1) | (1 << 2) | (1 << 5) | (1 << 6) | (1 << 9) | (1 << 11) | (1 << 12);// 签名，用户账户，公司账户，姓名，手机，电子邮件，标志，职务，电话
		ByteBuffer bodyBuffer = GetEmployeeProfileOutPacket.GenerateEmployeeProfileBody(aUntransID, cid, uid, mask);
		GetEmployeeProfileOutPacket out = new GetEmployeeProfileOutPacket(bodyBuffer, IMOCommand.IMO_GET_EMPLOYEE_PROFILE, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
		Toast.makeText(this, "正在加载个人详情", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		IMOApp.getDataEngine().addToObserverList(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		IMOApp.getDataEngine().removeFromObserverList(this);
	}

}

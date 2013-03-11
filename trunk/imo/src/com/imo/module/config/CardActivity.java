package com.imo.module.config;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.ClipboardManager;
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
import com.imo.module.dialogue.DialogueActivity;
import com.imo.network.net.EngineConst;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.CorpMaskItem;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.network.packages.GetCorpInfoInPacket;
import com.imo.network.packages.GetCorpInfoOutPacket;
import com.imo.network.packages.GetEmployeeProfileInPacket;
import com.imo.network.packages.GetEmployeeProfileOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.util.Functions;
import com.imo.util.ImageUtil;
import com.imo.util.LogFactory;

/**
 * 名片
 */
public class CardActivity extends AbsBaseActivityNetListener implements OnClickListener {

	private static final int PRIVACY_FLAG_PUBLIC = 0;// 名片对所有人公开
	private static final int PRIVACY_FLAG_INNER_PUBLIC = 1;// 名片对内部联系人公开

	private ImageView iv_userFace;

	private TextView tv_userName;

	private TextView tv_userPosition;

	private TextView tv_worksign_content;

	private Button btn_begin_dialogue, copyNameCard;

	private final int aUntransID = 10;

	private int aboutCid;

	private int aboutUid;

	private String userName;

	private TextView[] tv_infos = new TextView[7];

	private int[] tv_ids = {
			R.id.tv_company_name, R.id.tv_address, R.id.tv_tel, R.id.tv_mobilephone, R.id.tv_fax, R.id.tv_user_account, R.id.tv_email
	};

	private String[] tv_content = new String[7];
	// {
	// "中国互联网办公室-imo运营中心",
	// "洛川中路840号B栋7楼",
	// "021-50184188",//*518",
	// "125-6358-4755",
	// "021-21262325",//*3236",
	// "mengxin@501824",
	// "mengxin@imoffice.com"
	// };

	private String TAG = CardActivity.class.getSimpleName();

	public static void launch(Context c, Bundle bundle) {
		Intent intent = new Intent(c, CardActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		setContentView(R.layout.card_activity);
		iv_userFace = (ImageView) findViewById(R.id.iv_user_face);
		tv_userName = (TextView) findViewById(R.id.tv_user_name);
		tv_userPosition = (TextView) findViewById(R.id.tv_user_position);
		tv_worksign_content = (TextView) findViewById(R.id.tv_worksign_content);
		tv_worksign_content.setVisibility(View.VISIBLE);

		copyNameCard = (Button) findViewById(R.id.copynamecard_btn);
		copyNameCard.setOnClickListener(this);
		btn_begin_dialogue = (Button) findViewById(R.id.btn_begin_dialogue);

		btn_begin_dialogue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, DialogueActivity.class);
				intent.putExtras(getIntent().getExtras());
				startActivity(intent);
				finish();
			}
		});

		for (int i = 0; i < tv_infos.length; i++) {
			tv_infos[i] = (TextView) findViewById(tv_ids[i]);
		}

		mTitleBar.initDefaultTitleBarForNameCard(resources.getString(R.string.back), resources.getString(R.string.card), null, R.drawable.titlebar_btn_back_bg2);

		mTitleBar.setLeftBtnListene(this);

		initIntentData();
		if (aboutUid != EngineConst.uId) {
			loadHeadPic();
		}
	}

	private void initIntentData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			aboutCid = bundle.getInt("cid");
			aboutUid = bundle.getInt("uid");
			userName = bundle.getString("name");

			if (userName != null) {
				tv_userName.setText(userName);
			}

			if (aboutUid != EngineConst.uId) {// 不是登录的uid则请求对应数据
				getEmployeeInfo(aboutCid, aboutUid);
				getCorpInfo(aboutCid);
				btn_begin_dialogue.setVisibility(View.VISIBLE);
			} else {
				btn_begin_dialogue.setVisibility(View.GONE);
				initLoginUserInfo();
			}
		} else {
			btn_begin_dialogue.setVisibility(View.GONE);
			initLoginUserInfo();
		}
	}

	private void getEmployeeInfo(int cid, int uid) {
		LogFactory.d(TAG, "Send EmployeeInfo , cid = " + cid + ", uid = " + uid);
		EmployeeProfileItem employeeProfileItem = Globe.employeeProfileItems.get(uid);
		LogFactory.d(TAG, "EmployeeProfileItem is null ? " + (employeeProfileItem == null));
		if (employeeProfileItem != null) {
			setEmployeeProfile(employeeProfileItem);
			tv_userPosition.setText(employeeProfileItem.getPos());
			tv_worksign_content.setText("签名：" + employeeProfileItem.getSign());
			updateUI();
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

	private void getCorpInfo(int cid) {
		CorpMaskItem corpMaskItem = Globe.corpMaskItems.get(cid);
		LogFactory.d(TAG, "CorpMaskItem is null ? " + (corpMaskItem == null));
		if (corpMaskItem != null) {
			tv_content[0] = corpMaskItem.getCn_name() != null ? corpMaskItem.getCn_name() : "";// 公司名称
			tv_content[1] = corpMaskItem.getAddr() != null ? corpMaskItem.getAddr() : "";// 地址
			tv_content[4] = corpMaskItem.getFax() != null ? corpMaskItem.getFax() : "";// 传真-取公司
			updateUI();
			return;
		}
		if (!ConnectionChangeReceiver.isNetworkAvailable(mContext)) {
			Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
			return;
		}

		int mask = (1 << 2) | (1 << 12) | (1 << 17);// 公司中文名称，公司地址，公司传真
		ByteBuffer bodyBuffer = GetCorpInfoOutPacket.GenerateCorpInfoBody(cid, mask);
		GetCorpInfoOutPacket out = new GetCorpInfoOutPacket(bodyBuffer, IMOCommand.IMO_GET_CORP_INFO, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
		Toast.makeText(this, "正在加载名片", Toast.LENGTH_LONG).show();
	}

	private void initLoginUserInfo() {
		loadHeadPic();

		EmployeeProfileItem loginUserInfo = Globe.myself;
		CorpMaskItem corp = Globe.corp;

		if (loginUserInfo != null) {
			LogFactory.d(TAG, "UsrName:" + loginUserInfo.getName() + ",UsrPosition:" + loginUserInfo.getPos() + ",UsrSign:" + loginUserInfo.getSign());

			tv_userName.setText(loginUserInfo.getName() != null ? loginUserInfo.getName() : "");
			tv_userPosition.setText(loginUserInfo.getPos() != null ? loginUserInfo.getPos() : "");
			tv_worksign_content.setText("签名：" + (loginUserInfo.getSign() != null ? loginUserInfo.getSign() : ""));
		}

		if (loginUserInfo != null) {
			tv_content[0] = corp.getCn_name() != null ? corp.getCn_name() : "";// 公司名称
			tv_content[1] = corp.getAddr() != null ? corp.getAddr() : "";// 地址
			tv_content[2] = loginUserInfo.getTel() != null ? loginUserInfo.getTel() : "";// 电话
			tv_content[3] = loginUserInfo.getMobile() != null ? Functions.formatPhone(loginUserInfo.getMobile()) : "";// 手机
			tv_content[4] = corp.getFax() != null ? corp.getFax() : "";// 传真-取公司
			tv_content[5] = loginUserInfo.getUser_account() + "@" + loginUserInfo.getCorp_account();// imo
			tv_content[6] = loginUserInfo.getEmail() != null ? loginUserInfo.getEmail() : "";// E-mail
			updateUI();
		}

	}

	@Override
	protected void registerEvents() {
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
						LogFactory.d(TAG, "得到的Privacy_flag：" + employeeProfileItem.getPrivacy_flag());
						setEmployeeProfile(employeeProfileItem);

						updateUI();
						Globe.employeeProfileItems.put(aboutUid, employeeProfileItem);
					} else {
						Toast.makeText(this, "读取个人资料失败", Toast.LENGTH_SHORT).show();
					}
					break;
				}
				case IMOCommand.IMO_GET_CORP_INFO: {
					if ((Short) result.get("ret") == 0) {
						CorpMaskItem corpMaskItem = (CorpMaskItem) result.get("corpMaskItem");
						tv_content[0] = corpMaskItem.getCn_name() != null ? corpMaskItem.getCn_name() : "";// 公司名称
						tv_content[1] = corpMaskItem.getAddr() != null ? corpMaskItem.getAddr() : "";// 地址
						tv_content[4] = corpMaskItem.getFax() != null ? corpMaskItem.getFax() : "";// 传真-取公司
						updateUI();
						Globe.corpMaskItems.put(aboutCid, corpMaskItem);
					} else {
						Toast.makeText(this, "读取公司资料失败", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		}
	}

	private void setEmployeeProfile(EmployeeProfileItem employeeProfileItem) {
		// 名片显示权限：0-全部公开；1-仅对内部联系人公开；2-全部隐藏
		int privacy_flag = employeeProfileItem.getPrivacy_flag();
		LogFactory.d(TAG, "Card_Show_Flag = " + privacy_flag);
		if (isShowCard(privacy_flag)) {
			tv_content[3] = employeeProfileItem.getMobile() != null ? Functions.formatPhone(employeeProfileItem.getMobile()) : "";// 手机
			tv_content[6] = employeeProfileItem.getEmail() != null ? employeeProfileItem.getEmail() : "";// E-mail
		} else {
			tv_content[3] = "（未公开）";// 手机
			tv_content[6] = "（未公开）";// E-mail
		}
		tv_content[2] = employeeProfileItem.getTel() != null ? employeeProfileItem.getTel() : "";// 电话
		tv_content[5] = employeeProfileItem.getUser_account() + "@" + employeeProfileItem.getCorp_account();// imo
		tv_userPosition.setText(employeeProfileItem.getPos());
		tv_worksign_content.setText("签名：" + employeeProfileItem.getSign());
	}

	private boolean isShowCard(int privacy_flag) {
		if (privacy_flag == PRIVACY_FLAG_PUBLIC)// 所有人显示
			return true;
		if (privacy_flag == PRIVACY_FLAG_INNER_PUBLIC && (aboutCid == EngineConst.cId))// 内部联系人显示
			return true;
		return false;
	}

	private void updateUI() {
		for (int i = 0; i < tv_infos.length; i++) {
			tv_infos[i].setText(tv_content[i]);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void loadHeadPic() {
		float face_width = getResources().getDimension(R.dimen.middle_title_face_height);
		ImageUtil.changeFaceByUid(iv_userFace, aboutUid, face_width, false);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.btn_left:
				finish();
				break;
			case R.id.btn_right:
				ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clip.setText(copyCard()); // 复制
				Toast.makeText(this, "名片已经复制到剪切板", Toast.LENGTH_SHORT).show();
				break;

			case R.id.copynamecard_btn:
				ClipboardManager clip2 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clip2.setText(copyCard()); // 复制
				Toast.makeText(this, "名片已经复制到剪切板", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}

	}

	private String copyCard() {
		StringBuffer result = new StringBuffer();
		result.append("姓名：" + (Functions.isEmpty(tv_userName.getText()) ? "" : tv_userName.getText()) + "\n");
		result.append("职位：" + (Functions.isEmpty(tv_userPosition.getText()) ? "" : tv_userPosition.getText()) + "\n");
		result.append((Functions.isEmpty(tv_worksign_content.getText()) ? "签名：" : tv_worksign_content.getText()) + "\n");
		result.append("公司：" + (Functions.isEmpty(tv_content[0]) ? "" : tv_content[0]) + "\n");
		result.append("地址：" + (Functions.isEmpty(tv_content[1]) ? "" : tv_content[1]) + "\n");
		result.append("电话：" + (Functions.isEmpty(tv_content[2]) ? "" : tv_content[2]) + "\n");
		result.append("手机：" + (Functions.isEmpty(tv_content[3]) ? "" : tv_content[3]) + "\n");
		result.append("传真：" + (Functions.isEmpty(tv_content[4]) ? "" : tv_content[4]) + "\n");
		result.append("imo：" + (Functions.isEmpty(tv_content[5]) ? "" : tv_content[5]) + "\n");
		result.append("E-Mail：" + (Functions.isEmpty(tv_content[6]) ? "" : tv_content[6]));
		return result.toString();
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
			case IMOCommand.IMO_GET_CORP_INFO: {
				short ret = -1;
				CorpMaskItem corpMaskItem = null;
				try {
					GetCorpInfoInPacket getCorpInfoInPacket = (GetCorpInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
					corpMaskItem = getCorpInfoInPacket.getMaskItem();
					ret = getCorpInfoInPacket.getRet();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("cmd", command);
					result.put("ret", ret);
					result.put("corpMaskItem", corpMaskItem);
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
			case IMOCommand.IMO_GET_CORP_INFO:
				return true;
			default:
				return false;
		}
	}

}

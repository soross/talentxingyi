package com.imo.module.dialogue;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.common.DoCallAndShortMessageHelper;
import com.imo.db.entity.MessageInfo;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.config.CardActivity;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.module.dialogue.recent.RecentContactInfo;
import com.imo.module.organize.struct.Node;
import com.imo.network.net.EngineConst;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.CommonOutPacket;
import com.imo.network.packages.DeleteOfflineMsgInPacket;
import com.imo.network.packages.DeleteOfflineMsgOutPacket;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.network.packages.GetEmployeeProfileInPacket;
import com.imo.network.packages.GetEmployeeProfileOutPacket;
import com.imo.network.packages.GetOffMsgFromContactorInPacket;
import com.imo.network.packages.GetOffMsgFromContactorOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.OfflineMsgItem;
import com.imo.network.packages.SendMsgOutPacket;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.IOUtil;
import com.imo.util.ImmUtils;
import com.imo.util.LogFactory;
import com.imo.util.MessageDataFilter;
import com.imo.util.NoticeManager;

public class DialogueActivity extends AbsBaseActivityNetListener {

	private static final int PRIVACY_FLAG_PUBLIC = 0;// 名片对所有人公开
	private static final int PRIVACY_FLAG_INNER_PUBLIC = 1;// 名片对内部联系人公开
//	private static final int PRIVACY_FLAG_PRIVATE = 2;// 名片不公开

	private String TAG = "DialogueActivity";
	private final int aUntransID = 2;
	private ListView dialogueList;
	private Button btn_emotion;
	private PopupWindow popupWindow;
	private EditText et_messgae;
	private ImageView btn_send;
	private DialogueListAdapter dialogueListAdapter;
	private Button btn_findHistory;
	private Button btn_call;
	private Button btn_short_message;
	public static DialogueActivity instance = null;
	public boolean isOnline = true;
	private DoCallAndShortMessageHelper doCallAndShortMessageHelper = null;

	private List<Map<String, Object>> message_list = new ArrayList<Map<String, Object>>();

	private int aboutCid;
	public int aboutUid;
	private String aboutName;

	private boolean from_notice = false;
	/**
	 * 男性为true，女性为false
	 */
	private boolean aboutSex;
	private Bitmap about_head;

	/**
	 * 数据库一共拥有的聊天记录数
	 */
	private int sum = 0;
	/**
	 * 当前页面最多显示的聊天记录数
	 */
	private final int capacity = 10;

	/**
	 * 默认加载的消息条数
	 */
	private final int initSum = 10;

	private boolean isAddRecentContact = false;

	/**
	 * 点击发送按钮可能触发多次Toast，本变量表示最后一次Toast显示的时间
	 */
	private long sendToastLastTime = 0;

	private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				if (popupWindow != null && popupWindow.isShowing())
					popupDismiss();
			}
		}
	};

	@Override
	protected void installViews() {
		setContentView(R.layout.dialogue_activity);
		instance = this;
		getInitData();
		loadHeadPics();

		dialogueList = (ListView) findViewById(R.id.lv_dialogueList);
		dialogueListAdapter = new DialogueListAdapter(this, message_list, R.layout.dialogue_list_iteml, null, null);

		btn_findHistory = (Button) findViewById(R.id.ib_findHistory);
		btn_findHistory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putInt("aboutCid", aboutCid);
				bundle.putInt("aboutUid", aboutUid);
				bundle.putString("aboutName", aboutName);
				// bundle.putBoolean("aboutSex", aboutSex);
				Intent intent = new Intent(DialogueActivity.this, ChatRecordActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		btn_call = (Button) findViewById(R.id.ib_call);
		btn_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (doCallAndShortMessageHelper != null)
					doCallAndShortMessageHelper.call();

			}
		});

		btn_short_message = (Button) findViewById(R.id.ib_short_message);
		btn_short_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (doCallAndShortMessageHelper != null)
					doCallAndShortMessageHelper.sendShortMessage();

			}
		});

		setDoCallAndShortMessageBtn();

		dialogueList.setAdapter(dialogueListAdapter);

		dialogueList.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					ImmUtils.hideKeyboard(mContext, et_messgae);
				}
				return false;
			}
		});

		dialogueList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				final TextView textView = (TextView) arg1.findViewById(R.id.tv_msg);
				char[] c = textView.getText().toString().toCharArray();
				final StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < c.length; i++) {
					if (((int) c[i]) != 65532)// 65532：系统自带图标从EditView里面得到的字符值
						buffer.append(c[i]);
				}
				DialogFactory.copyDialog(DialogueActivity.this, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							clip.setText(buffer.length() == 0 ? " " : buffer.toString()); // 复制
						}
					}
				});
				return true;
			}
		});

		btn_emotion = (Button) findViewById(R.id.btn_getPic);
		btn_emotion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupWindow.showAtLocation(findViewById(R.id.dialogue), Gravity.CENTER, 0, 0);
			}
		});

		et_messgae = (EditText) findViewById(R.id.et_message);

		et_messgae.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		btn_send = (ImageView) findViewById(R.id.btn_send);

		btn_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = et_messgae.getText().toString();
				if (Functions.isEmpty(message)) {
					Functions.showToast(mContext, "消息为空", sendToastLastTime);
					sendToastLastTime = System.currentTimeMillis();
				} else {
					try {
						sendMessage(aboutCid, aboutUid, message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});

		initPopWindow();
		loadLastChat(initSum);
		updateUI();
		startlookScreen();

		requstEmployeeInfo(aboutCid, aboutUid);
		// 判断是否存在脱机消息
		if (checkIsExistOfflineMSG(aboutUid)) {
			doRequestSignleOfflineMSG(aboutCid, aboutUid);
		}

		// 重置数据库所有信息标记为已读 (isRead=1)
		IMOApp.imoStorage.updateMessage(aboutUid);
	}

	private void setDoCallAndShortMessageBtn() {
		EmployeeProfileItem employeeProfileItem = Globe.employeeProfileItems.get(aboutUid);
		if (employeeProfileItem == null)
			return;

		doCallAndShortMessageHelper = new DoCallAndShortMessageHelper(isShowMobile(employeeProfileItem.getPrivacy_flag()) ? employeeProfileItem.getMobile() : "", employeeProfileItem.getTel(), this);
		if (doCallAndShortMessageHelper.canDoCall()) {
			btn_call.setEnabled(true);
		} else {
			btn_call.setEnabled(false);
		}
		if (doCallAndShortMessageHelper.canSendShortMessage()) {
			btn_short_message.setEnabled(true);
		} else {
			btn_short_message.setEnabled(false);
		}

	}

	private boolean isShowMobile(int privacy_flag) {
		if (privacy_flag == PRIVACY_FLAG_PUBLIC)// 所有人显示
			return true;
		if (privacy_flag == PRIVACY_FLAG_INNER_PUBLIC && (aboutCid == EngineConst.cId))// 内部联系人显示
			return true;
		return false;
	}

	/**
	 * 判断是否存在脱机消息
	 * 
	 * @param aboutUid
	 * @return
	 */
	private boolean checkIsExistOfflineMSG(int aboutUid) {
		if (mGlobal.mOfflineMsgMap.get(aboutUid) == null) {
			return false;
		} else {
			return true;
		}
	}

	// 监听屏幕是否锁屏
	private void startlookScreen() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mBatInfoReceiver, filter);
	}

	/**
	 * 加载最近count条聊天记录
	 * 
	 * @param count
	 */
	private void loadLastChat(int count) {
		try {
			sum = IMOApp.imoStorage.getMessageSum(aboutUid);
			int currentIndex = sum - count;
			currentIndex = currentIndex < 0 ? 0 : currentIndex;
			loadMessage(aboutUid, currentIndex, count);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void requstEmployeeInfo(int cid, int uid) {

		EmployeeProfileItem employeeProfileItem = Globe.employeeProfileItems.get(uid);
		if (employeeProfileItem != null) {
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
		System.out.println("已经发送请求");
	}

	private void loadMessage(int uId, int index, int count) {
		message_list.clear();
		ArrayList<MessageInfo> messageInfos = null;
		Map<String, Object> map = null;
		CharSequence show_message = null;
		try {
			messageInfos = IMOApp.imoStorage.getMessage(uId, index, count);

			for (MessageInfo messageInfo : messageInfos) {

				show_message = MessageDataFilter.jsonToCharSequence(new JSONObject(messageInfo.getText()));

				map = new HashMap<String, Object>();

				map.put("time", messageInfo.getTime());
				map.put("date", messageInfo.getDate());
				map.put("msg", show_message);
				if (messageInfo.getType() == MessageInfo.MessageInfo_From) {
					map.put("who", "from");
					map.put("head", about_head);
				} else {
					map.put("who", "to");
				}
				if (messageInfo.getIsFailed() == MessageInfo.MessageInfo_Failed)
					map.put("fail", "fail");
				message_list.add(map);
				mLastMessge = messageInfo.getText();
				mLastTime = messageInfo.getDate() + " " + messageInfo.getTime();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getInitData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		from_notice = bundle.getBoolean("from_notice");
		if (from_notice) {
			// /从通知跳转过来,需要reset 消息数据
			NoticeManager.count = 0;
		}

		aboutCid = bundle.getInt("cid");
		aboutUid = bundle.getInt("uid");
		if (IMOApp.getApp().getAppMode()) {
			if (aboutCid == 0 || aboutUid == 0) {
				Toast.makeText(this, "初始化错误", Toast.LENGTH_LONG).show();
			}
		}
		// aboutSex = bundle.getBoolean("sex");
		Node userNode = IMOApp.getApp().mNodeMap.get(aboutUid);

		if (userNode != null) {
			aboutSex = userNode.getNodeData().isBoy;
			aboutName = userNode.getNodeData().nodeName;
		} else {
			LogFactory.e("sex", "sex error, user default boy");
		}
		LogFactory.d("冯小卫", "" + (aboutSex ? "男人" : "女人"));
	}

	private void loadHeadPics() {
		try {
			// 加载聊天对象的头像
			byte[] b_about_head = null;
			try {
				b_about_head = IOUtil.readFile("HeadPic" + aboutUid, this);
			} catch (Exception e) {}
			if (b_about_head != null && b_about_head.length > 0) {
				// Bitmap bitmap = BitmapFactory.decodeByteArray(b_about_head,
				// 0,
				// b_about_head.length);
				// about_head = new BitmapDrawable(bitmap);
				about_head = BitmapFactory.decodeByteArray(b_about_head, 0, b_about_head.length);
				return;
			}

			String httpUrl = Functions.buildPersonPicUrl(aboutCid, aboutUid);
			DownLoadTask dTask = new DownLoadTask();
			dTask.execute(httpUrl);

			LogFactory.d("冯小卫", "加载：" + (aboutSex ? "男人" : "女人"));
			if (aboutSex) {
				// about_head = getResources().getDrawable(
				// R.drawable.imo_default_face_boy);
				about_head = BitmapFactory.decodeResource(getResources(), R.drawable.imo_default_face_boy);
			} else {
				// about_head = getResources().getDrawable(
				// R.drawable.imo_default_face_girl);
				about_head = BitmapFactory.decodeResource(getResources(), R.drawable.imo_default_face_girl);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(int cId, int uId, String message) throws Exception {
		// 判断输入字符数量，如果大于840，则不发送
		if (message.length() > 840) {
			Functions.showToast(mContext, "发送消息内容过长，请分条发送。", sendToastLastTime);
			sendToastLastTime = System.currentTimeMillis();
			return;
		}

		// 把用户输入的字符串替换，并转化为json格式
		JSONArray jsonData = MessageDataFilter.AnalyseStr2Json(MessageDataFilter.emotionFilter(message));

		if (jsonData == null)
			return;

		// JSONArray jsonData = MessageDataFilter.AnalyseStr2Json(message);
		// 判断消息是不是纯文本，如果含有图片，type为1
		int type = 0;
		if (jsonData.toString().indexOf(".gif") > -1)
			type = 1;
		// 组建最终发送的消息
		JSONObject jsonMessage = MessageDataFilter.buildMessage(jsonData, type);

		// 判断最终发送的消息，如果超过上限，不发送
		if (jsonMessage.toString().length() > 2100) {
			Functions.showToast(mContext, "发送消息内容过长，请分条发送。", sendToastLastTime);
			sendToastLastTime = System.currentTimeMillis();
			return;
		}
		// 清空输入框
		et_messgae.setText("");
		// 把最终的消息构建出可以显示的内容
		CharSequence showMessage = MessageDataFilter.jsonToCharSequence(jsonMessage);

		// 添加到ListView显示
		Map<String, Object> map = new HashMap<String, Object>();
		String time = Functions.getTime();
		String date = Functions.getDate();
		map.put("time", time);
		map.put("date", date);
		map.put("msg", showMessage);
		map.put("who", "to");
		if (!EngineConst.isNetworkValid) {
			map.put("fail", "fail");
		}
		message_list.add(map);
		if (message_list.size() > capacity) {
			message_list.remove(0);
		}

		updateUI();

		// 添加最近联系人
		addRecentContact();

		// 发送到服务器
		ByteBuffer bodyBuffer = SendMsgOutPacket.GenerateMsgTextBody(cId, uId, jsonMessage.toString());
		SendMsgOutPacket sendMsgOutPacket = new SendMsgOutPacket(bodyBuffer, IMOCommand.IMO_SEND_MESSAGE, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, sendMsgOutPacket, false);

		// 添加到数据库
		try {
			MessageInfo msg = new MessageInfo(0, EngineConst.uId, "", uId, "", Functions.getDate(), time, jsonMessage.toString(), MessageInfo.MessageInfo_To, MessageInfo.MessageInfo_MsgId, MessageInfo.MessageInfo_Readed, EngineConst.isNetworkValid ? MessageInfo.MessageInfo_UnFailed
					: MessageInfo.MessageInfo_Failed);
			IMOApp.imoStorage.addMessage(msg);
			mLastMessge = jsonMessage.toString();
			mLastTime = RecentContactActivity.getActivity().getCurTime();
			msgHasGenerated = true;
			msgFromLoginUser = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateUI() {
		dialogueListAdapter.notifyDataSetChanged();
		dialogueList.setSelection(message_list.size());
	}

	private void initPopWindow() {
		// 加载popupWindow的布局文件
		View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.emotion_popup, null);

		contentView.setBackgroundResource(R.drawable.round_win);

		GridView gridView = (GridView) contentView.findViewById(R.id.gv_emotion);
		gridView.setColumnWidth((int) (64 * IMOApp.getApp().mScale));

		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String emotionName = Functions.getEmotion().emotion_texts[arg2];
				// String emotionName =
				// Functions.getEmotion().emotion_indexs[arg2];
				int index = et_messgae.getSelectionStart();
				et_messgae.getText().insert(index, "/" + emotionName);
				popupDismiss();
			}
		});

		gridView.setAdapter(new ImageAdapter(this, Functions.getEmotion().emotion_ids));// 调用ImageAdapter.java

		gridView.setFocusableInTouchMode(true);

		gridView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
					popupDismiss();
				return false;
			}
		});
		// 声明一个弹出框
		popupWindow = new PopupWindow(findViewById(R.id.dialogue), (int) (400 * IMOApp.getApp().mScale), (int) (468 * IMOApp.getApp().mScale));
		// 为弹出框设定自定义的布局
		popupWindow.setContentView(contentView);

		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
	}

	private void popupDismiss() {
		popupWindow.dismiss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isOnline = mGlobal.isOnlineFindByUid(aboutUid);
		loadLastChat(initSum);
		updateUI();
	}

	@Override
	protected void registerEvents() {

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back), aboutName, resources.getString(R.string.card));

		mTitleBar.setLeftBtnListene(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mTitleBar.setRightBtnListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = getIntent().getExtras();
				CardActivity.launch(mContext, bundle);
				finish();
			}
		});
	}

	@Override
	public void refresh(Object param) {
		Message msg = (Message) param;
		Map<String, Object> result = (Map<String, Object>) msg.obj;
		if (NotifyPacketArrived == msg.what) {
			switch ((Short) result.get("cmd")) {
				case IMOCommand.IMO_GET_OFFLINE_MSG:
				case IMOCommand.IMO_SEND_MESSAGE: {
					loadLastChat(initSum);
					// 添加最近联系人
					addRecentContact();
					updateUI();
					break;
				}
				case IMOCommand.IMO_GET_EMPLOYEE_PROFILE: {
					if ((Short) result.get("ret") == 0) {
						EmployeeProfileItem employeeProfileItem = Globe.employeeProfileItems.get(aboutUid);
						System.out.println(employeeProfileItem.getMobile());
						System.out.println(employeeProfileItem.getTel());

						setDoCallAndShortMessageBtn();
					}
				}
				default:
					break;
			}
		}
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		switch (command) {
			case IMOCommand.IMO_SEND_MESSAGE:
				return true;
			case IMOCommand.IMO_GET_EMPLOYEE_PROFILE:
				return true;
			case IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS:
				return true;
				// case IMOCommand.IMO_GET_OFFLINE_MSG://old
				// return true;
			case IMOCommand.IMO_DELETE_OFFLINE_MSG:
				return true;
			default:
				return false;
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
		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId)) {
			switch (command) {
				case IMOCommand.IMO_GET_EMPLOYEE_PROFILE: {
					responseEmployeeInfo(command);
					break;
				}
				case IMOCommand.IMO_DELETE_OFFLINE_MSG: {
					responseDelRemoteOfflineMSG(command);
					break;
				}
				case IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS: {
					responseOfflineSingleMSG(command);
					break;
				}
				default:
					break;
			}
		}
	}

	private void responseEmployeeInfo(short command) {
		EmployeeProfileItem employeeProfileItem = null;
		System.out.println("已经获得数据");
		short ret = -1;
		try {
			GetEmployeeProfileInPacket getEmployeeProfileInPacket = (GetEmployeeProfileInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
			employeeProfileItem = getEmployeeProfileInPacket.getEmployeeItem();
			System.out.println("已经获得数据：" + employeeProfileItem.getMobile() + "   " + employeeProfileItem.getTel());
			ret = getEmployeeProfileInPacket.getRet();
			if (ret == 0) {
				Globe.employeeProfileItems.put(aboutUid, employeeProfileItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("cmd", command);
			result.put("ret", ret);
			sendMessage(NotifyPacketArrived, result);
		}

	}

	public void getMessage(short command) {
		msgHasGenerated = true;
		msgFromLoginUser = false;
		// 通知界面
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("cmd", command);
		sendMessage(NotifyPacketArrived, result);
	}

	private void addRecentContact() {
		if (!isAddRecentContact) {
			isAddRecentContact = true;
			try {
				IMOApp.imoStorage.addRecentContact(new RecentContactInfo(aboutCid, aboutUid, aboutName, "", Functions.getTime(), RecentContactInfo.NORMAL_TYPE), Functions.getDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean msgFromLoginUser = false;
	private boolean msgHasGenerated = false;
	private String mLastMessge = "";
	private String mLastTime = "";

	private void updateRecentMap() {
		LogFactory.d("xxx", "updateRecentMap in the DialogueActivity...");

		if (mLastMessge == null || mLastMessge.equals("")) {
			return;
		}

		RecentContactInfo info = new RecentContactInfo(aboutCid, aboutUid, aboutName, mLastMessge, mLastTime, 0, RecentContactInfo.NORMAL_TYPE);

		info.isFromLoginUser = msgFromLoginUser;

		if (RecentContactActivity.getActivity() != null) {
			RecentContactActivity.getActivity().addOrUpdate(info, true);
		} else {
			LogFactory.d(TAG, "RecentContactActivity has not lunched ....");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (msgHasGenerated) {
			updateRecentMap();
		} else {
			if (RecentContactActivity.getActivity() != null) {
				RecentContactActivity.getActivity().resetMsgCount(aboutUid);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(mBatInfoReceiver);
		} catch (Exception e) {}
	}

	class DownLoadTask extends AsyncTask<String, Void, byte[]> {

		@Override
		protected byte[] doInBackground(String... params) {
			try {
				HttpGet httpRequest = new HttpGet(params[0]);
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = null;
				httpResponse = httpclient.execute(httpRequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					byte[] bTemp = EntityUtils.toByteArray(httpResponse.getEntity());
					if (bTemp != null && bTemp.length > 0)
						return bTemp;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(byte[] result) {
			if (result != null) {
				about_head = BitmapFactory.decodeByteArray(result, 0, result.length);
				try {
					IOUtil.saveFile("HeadPic" + aboutUid, result, mContext, Context.MODE_PRIVATE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}

	}

	public Handler mFaceHandler = new Handler() {

		public void handleMessage(Message msg) {
			if (msg.what == TYPE_UPDATEFACE) {
				setOnline(msg.arg1, (Boolean) msg.obj);
			}
		};
	};

	public static final int TYPE_UPDATEFACE = 1;

	/**
	 * 更新头像的状态
	 * 
	 * @param uid
	 * @param isOnline
	 */
	public void setOnline(int uid, boolean isOnline) {
		if (uid == aboutUid) {
			this.isOnline = isOnline;
			updateUI();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		from_notice = bundle.getBoolean("from_notice");
		if (from_notice) {
			// /从通知跳转过来,需要reset 消息数据
			NoticeManager.count = 0;
		} else {
			super.onNewIntent(intent);
			return;
		}
		aboutCid = bundle.getInt("cid");
		aboutUid = bundle.getInt("uid");
		aboutName = bundle.getString("name");
		// aboutSex = bundle.getBoolean("sex");

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back), aboutName, resources.getString(R.string.card));
		Node userNode = IMOApp.getApp().mNodeMap.get(aboutUid);

		if (userNode != null) {
			aboutSex = userNode.getNodeData().isBoy;
		} else {
			LogFactory.e("sex", "sex error, user default boy");
		}
		LogFactory.d("冯小卫", "" + (aboutSex ? "男人" : "女人"));

		loadHeadPics();
		IMOApp.imoStorage.updateMessage(aboutUid);
		loadLastChat(initSum);
		updateUI();
		super.onNewIntent(intent);
	}

	/**
	 * 临时存储数据
	 */
	ArrayList<OfflineMsgItem> offlineMsgItemList = new ArrayList<OfflineMsgItem>();

	/**
	 * 1-请求单个人的脱机消息
	 */
	private void doRequestSignleOfflineMSG(int aFromcid, int aFromuid) {

		LogFactory.d(TAG, "doRequestSignleOfflineMSG  + aFromcid =" + aFromcid + " aFromuid =" + aFromuid);

		offlineMsgItemList.clear();

		ByteBuffer bufferBody = GetOffMsgFromContactorOutPacket.GenerateOffMsgFormContInfoBody(aFromcid, aFromuid);
		CommonOutPacket outPacket = new CommonOutPacket(bufferBody, IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS, EngineConst.cId, EngineConst.uId);

		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
			IMOApp.getDataEngine().addToObserverList(this);
		}
	}

	private RecentContactInfo curRecentContactInfo = new RecentContactInfo();

	/**
	 * 响应单个人的脱机消息
	 * 
	 * @param command
	 */
	private void responseOfflineSingleMSG(short command) {

		LogFactory.d(TAG, "------------responseOfflineSingleMSG----->");

		GetOffMsgFromContactorInPacket inPacket = (GetOffMsgFromContactorInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		short commandRet = inPacket.getRet();
		byte endflag = inPacket.getEndflag();

		LogFactory.d(TAG, "commandRet = " + commandRet);

		if (commandRet == 0) {

			OfflineMsgItem[] temp_offlineMsgArray = inPacket.getOfflineMsgArray();

			for (int i = 0; i < temp_offlineMsgArray.length; i++) {
				offlineMsgItemList.add(temp_offlineMsgArray[i]);
				// LogFactory.d(TAG+"OfflineMSG",
				// temp_offlineMsgArray[i].toString());
			}
		}

		if (endflag == 1) {

			if (offlineMsgItemList.size() >= 1) {

				sortSingleOfflineMSG();
				OfflineMsgItem item = offlineMsgItemList.get(0);
				// DB Save Success
				if (doSave2DB()) {
					// 请求删除服务器端
					doRequestDelRemoteOfflineMSG(aboutCid, aboutUid, item.getMsgid());
				}
			}
		}
	}

	/**
	 * Sort Single User Offline MSG to get the max msgId
	 */
	private void sortSingleOfflineMSG() {
		LogFactory.d("sort", "sort Single User Offline MSG...");

		for (int i = 0; i < offlineMsgItemList.size(); i++) {
			LogFactory.d("sort", offlineMsgItemList.get(i).toString());
		}

		Collections.sort(offlineMsgItemList, new Comparator<OfflineMsgItem>() {

			@Override
			public int compare(OfflineMsgItem lhs, OfflineMsgItem rhs) {

				return -(lhs.getMsgid() - rhs.getMsgid());
			}

		});

		// sort over
		LogFactory.d("sort", "--------------------------------------");

		for (int i = 0; i < offlineMsgItemList.size(); i++) {
			LogFactory.d("sort", offlineMsgItemList.get(i).toString());
		}

	}

	/**
	 * 2-请求：删除服务器脱机消息
	 */
	private void doRequestDelRemoteOfflineMSG(int aFromcid, int aFromuid, int aFromid) {

		LogFactory.d(TAG, "doRequestDelRemoteOfflineMSG ");

		ByteBuffer bufferBody = DeleteOfflineMsgOutPacket.GenerateDeleteOffMsgBody(aFromcid, aFromuid, aFromid);

		CommonOutPacket outPacket = new CommonOutPacket(bufferBody, IMOCommand.IMO_DELETE_OFFLINE_MSG, EngineConst.cId, EngineConst.uId);
		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
			IMOApp.getDataEngine().addToObserverList(this);
		}
	}

	/**
	 * 响应删除服务器 脱机消息
	 * 
	 * @param command
	 */
	private void responseDelRemoteOfflineMSG(short command) {

		LogFactory.d(TAG, "------------responseDelRemoteOfflineMSG----->");

		DeleteOfflineMsgInPacket inPacket = (DeleteOfflineMsgInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		short commandRet = inPacket.getRet();
		int fromcid = inPacket.getFromcid();
		int fromuid = inPacket.getFromuid();
		int id = inPacket.getId(); // 消息Id

		LogFactory.d(TAG, "commandRet = " + commandRet);

		if (commandRet == 0) {
			OfflineMsgItem item = offlineMsgItemList.get(0);

			if (fromcid == curRecentContactInfo.getCid() && fromuid == curRecentContactInfo.getUid() && id == item.getMsgid()) {
				// Update 数据库状态 表示删除成功
				LogFactory.d(TAG, "response-MsgId--->" + id);
				LogFactory.d(TAG, "response---->DelRemoteOfflineMSG  success ! ");
			}

		}
	}

	/**
	 * 将逻辑消息存储到数据库中
	 */
	private boolean doSave2DB() {

		boolean flag = true;
		try {
			IMOApp.imoStorage.addMessages(offlineMsgItemList, aboutCid, aboutUid);
			IMOApp.imoStorage.updateMessage(aboutUid);
			mGlobal.mOfflineMsgMap.remove(aboutUid);
		} catch (Exception e) {

			e.printStackTrace();
			LogFactory.d(TAG, "OfflineMsg Save DB Failed !!");
			flag = false;
		}

		LogFactory.d(TAG, "OfflineMsg Save DB result = " + flag);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("cmd", IMOCommand.IMO_GET_OFFLINE_MSG);
		result.put("ret", 0);
		sendMessage(NotifyPacketArrived, result);

		return flag;
	}

}

package com.imo.module.dialogue.recent;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.MainActivityGroup;
import com.imo.module.dialogue.DialogueActivity;
import com.imo.module.organize.StateHandle;
import com.imo.module.organize.struct.Node;
import com.imo.network.net.EngineConst;
import com.imo.network.net.TCPConnection;
import com.imo.network.packages.CommonOutPacket;
import com.imo.network.packages.EmployeeInfoItem;
import com.imo.network.packages.GetOfflineMsgProfileInPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.OfflineMsgProfileItem;
import com.imo.network.packages.UserStatusChangeInPacket;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.LogFactory;
import com.imo.util.MessageDataFilter;
import com.imo.util.NoticeManager;

/**
 * �����ϵ�˽���
 */
public class RecentContactActivity extends AbsBaseActivityNetListener {

	private String TAG = "RecentContact";
	private String TAG1 = "RecentContactDBData";

	private static RecentContactActivity mActivity = null;

	private boolean curIsEditState = false;

	private RecentContactAdapter adapter = null;

	private ListView mListView = null;

	private boolean hasOfflineMSG = false;

	/**
	 * Have no RecentContactRecord
	 */
	private View iv_tip = null;

	// Category Data for ListView DataSource
	private List<String> mCategoryList = new ArrayList<String>();
	// All Data for ListView DataSource
	private List<RecentContactInfo> mDataList = new ArrayList<RecentContactInfo>();

	/**
	 * ��������ϵ��Activityʵ������
	 * 
	 * @return
	 */
	public static RecentContactActivity getActivity() {
		return mActivity;
	}

	@Override
	public void refresh(Object param) {

		dialog.dismiss();

		updateShowList(true);
	}

	/**
	 * �����ϵ��Map
	 */
	public HashMap<Integer, RecentContactInfo> mRecentContactMap = new HashMap<Integer, RecentContactInfo>();

	@Override
	protected void installViews() {
		if (mNIOThread == null) {
			finish();
		} else {

			mActivity = this;

			setContentView(R.layout.recent_dialogue_activity);
			iv_tip = findViewById(R.id.iv_tip);

			dialog = DialogFactory.progressDialog(mContext, "���ڻ�ȡ�ѻ���Ϣ�����Ժ�...");

			// 1- ���ر��������ϵ�˼�¼ (����м�¼����ϵ��)
			loadRecentContactData();
			// 2- �������� (����м�¼����ϵ��)
			buildShowItemInfo();

			mListView = (ListView) findViewById(R.id.recent_contact_list);
			adapter = new RecentContactAdapter(this, mCategoryList, mDataList);
			mListView.setAdapter(adapter);

		}
	}

	@Override
	protected void registerEvents() {

		if (AppService.getService() != null) {
			/**
			 * 1-����ý����״������ѻ�ժҪ��Ϣ
			 */
			beginLoading(TYPE_OFFLINEMSG_PROFILE);
		} else {
			super.finish();
		}

		// �����Լ���״̬ login over: ��Ĭ�Ͻ���Ϊ�����ϵ�˽����ʱ����á�
		// doRequestOwnState();
		try {
			mListView.setOnItemClickListener(onItemClickListener);
		} catch (Exception e) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}

		mTitleBar.setRightBtnListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				curIsEditState = !curIsEditState;
				adapter.setEditState(curIsEditState);
				updateTitleBar();
			}

		});

		mTitleBar.setLeftBtnListene(new OnClickListener() {

			@Override
			public void onClick(View v) {

				doRefresh();
			}
		});
	}

	public void reset2Normal() {
		curIsEditState = false;
		adapter.setEditState(curIsEditState);
		updateTitleBar();
	}

	private Intent intent = null;

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rowId) {

			final Bundle data = new Bundle();

			final RecentContactInfo info = (RecentContactInfo) view.getTag(R.layout.recent_contact_view);

			if (info != null && info.getCid() != -1) {

				// �ѻ�С���
				if (info.getCount() - getMsgCount > 0) {
					if (IMOApp.sendMsgUserId.contains(info.getUid())) {
						IMOApp.sendMsgUserId.remove(info.getUid());
					}
				}

				goToDialogue(data, info);

				info.setCount(0);

				startActivity(intent);
			} else {
				Toast.makeText(mContext, "��ˢ������", 1).show();
			}
		}

		/**
		 * @param intent
		 * @param data
		 * @param info
		 */
		private void goToDialogue(Bundle data, RecentContactInfo info) {
			// curRecentContactInfo = info;/////
			intent = new Intent(mContext, DialogueActivity.class);
			data.putInt("cid", info.getCid());
			data.putInt("uid", info.getUid());
			data.putString("name", info.getName());

			Node tempNode = getNodeByUid(info.getUid());
			if (tempNode != null) {
				data.putBoolean("sex", tempNode.getNodeData().isBoy);
			}

			intent = intent.putExtras(data);
		}
	};

	/**
	 * ����TitleBar����ʾ
	 */
	private void updateTitleBar() {

		if (curIsEditState) {
			mTitleBar.initDefaultTitleBar(resources.getString(R.string.dialgue_title), resources.getString(R.string.ok), true);
		} else {
			mTitleBar.initDefaultTitleBar(resources.getString(R.string.refresh), resources.getString(R.string.dialgue_title), resources.getString(R.string.edit));
		}
	}

	private ProgressDialog dialog;

	/**
	 * �������е��ѻ���ϢժҪ
	 */
	private final int TYPE_OFFLINEMSG_PROFILE = 1;

	/**
	 * ���ظ��˵��ѻ���Ϣ
	 */
	private final int TYPE_OFFLINEMSG_SINGLE = 2;

	/**
	 * ��ʼ��ȡ�ѻ���Ϣ��ժҪ
	 */
	private void beginLoading(int type) {
		switch (type) {
			case TYPE_OFFLINEMSG_PROFILE:
				Toast.makeText(mContext, "���ڻ�ȡ���ݣ����Ժ�...", Toast.LENGTH_SHORT).show();
				doRequestOfflineMSGProfile();
				break;
			case TYPE_OFFLINEMSG_SINGLE:
				break;
			default:
				dialog.dismiss();
				break;
		}
	}

	/**
	 * ���µ�ǰ�������ʾ�߼�
	 * 
	 * @param isEmpty
	 */
	public void show2State(boolean isEmpty) {

		if (isEmpty) {
			reset2Normal();

			iv_tip.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			mTitleBar.initDefaultTitleBar(resources.getString(R.string.refresh), resources.getString(R.string.dialgue_title), null);
		} else {
			mListView.setVisibility(View.VISIBLE);
			iv_tip.setVisibility(View.GONE);

			updateTitleBar();
		}
	}

	/**
	 * �����ѻ���ϢժҪ
	 */
	public void doRequestOfflineMSGProfile() {

		LogFactory.d(TAG, "doRequestOfflineMSGProfile ");

		offlineMsgProfileItemList.clear();

		CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE, EngineConst.cId, EngineConst.uId);

		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);

			IMOApp.getDataEngine().addToObserverList(this);
		}
	}

	/**
	 * ִ��ˢ�²���
	 */
	private void doRefresh() {
		if (EngineConst.isNetworkValid && AppService.getService().getTcpConnection() != null && AppService.getService().getTcpConnection().isConnected()) {
			mGlobal.mOfflineMsgMap.clear();
			beginLoading(TYPE_OFFLINEMSG_PROFILE);
		} else {
			Toast.makeText(mContext, getResources().getString(R.string.net_connected_failed), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean CanAcceptHttpPacket() {
		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		if (
		// IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS == command ||
		IMOCommand.IMO_UPDATE_STATUS == command || IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE == command || IMOCommand.IMO_UPDATE_USER_STATUS == command)
			return true;

		return false;
	}

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {

		super.NotifyPacketArrived(aConnectionId, command);

		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId)) {
			Message msg1 = new Message();
			msg1.arg1 = command;
			mCommandHandler.sendMessage(msg1);
		}

	}

	public Handler mCommandHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			short command = (short) msg.arg1;
			switch (command) {
				case IMOCommand.IMO_GET_OFFLINE_MSG: {
					responseOfflineMSG(command);
					break;
				}
				case IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE: {
					responseOfflineMSGProfile(command);
					break;
				}

				case IMOCommand.IMO_SEND_MESSAGE: {
					responseServerMSG(command);
					break;
				}
				case IMOCommand.IMO_UPDATE_STATUS:
					LogFactory.d(TAG, "-------->Login Over");
					break;
				case IMOCommand.IMO_UPDATE_USER_STATUS:
					LogFactory.d(TAG + "PushState", "-------->Pull State Received");
					responseServerPullEmployeeState(command);
					break;
				default:
					break;
			}
		};
	};

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
	}

	/**
	 * ��Ӧ �ѻ���Ϣ���� �� �ɰ汾����Ӧ���е��ѻ���Ϣ
	 * 
	 * @param command
	 */
	private void responseOfflineMSG(short command) {
	}

	/***
	 * ���� �ѻ���ϢժҪ �� Uid��cid��ŵ�ȫ��
	 */
	ArrayList<OfflineMsgProfileItem> offlineMsgProfileItemList = new ArrayList<OfflineMsgProfileItem>();

	/**
	 * ��Ӧ�ѻ���ϢժҪ
	 * 
	 * @param command
	 */
	private void responseOfflineMSGProfile(short command) {
		LogFactory.d(TAG, "------------responseOfflineMSGProfile----->");

		GetOfflineMsgProfileInPacket inPacket = (GetOfflineMsgProfileInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (inPacket == null) {
			return;
		}
		short commandRet = inPacket.getRet();
		byte endflag = inPacket.getEndflag();

		if (commandRet == 0) {

			OfflineMsgProfileItem[] temp_offlineMsgProfileArray = inPacket.getOfflineMsgArray();

			for (int i = 0; i < temp_offlineMsgProfileArray.length; i++) {
				OfflineMsgProfileItem temp_Item = temp_offlineMsgProfileArray[i];

				offlineMsgProfileItemList.add(temp_Item);
				// �鿴�ѻ���Ϣ����־
				LogFactory.d(TAG + "OfflineMSGProfile", temp_Item.toString());

				// �����ѻ�ժҪ��Ϣ(uid, fromCid);
				mGlobal.mOfflineMsgMap.put(temp_Item.getFromuid(), temp_Item.getFromcid());

				// lee �������з��͹��û���Զ��id
				if (!IMOApp.sendMsgUserId.contains(temp_Item.getFromuid())) {
					IMOApp.sendMsgUserId.add(temp_Item.getFromuid());
				}
			}
		}

		if (endflag == 1) {
			if (offlineMsgProfileItemList.size() == 0) {
				// ���û��������Ϣ
				refresh(null);
				LogFactory.d(TAG, "-------------> Have no OfflineMSGProfile!!");
			} else {
				LogFactory.d(TAG, "------------endflag--> OfflineMSGProfile hava got !! ");
				LogFactory.d(TAG, "  offlineMsg Profile count =" + offlineMsgProfileItemList.size());

				// �����������Ϣ
				if (offlineMsgProfileItemList.size() >= 1) {
					hasOfflineMSG = true;

					// ���ѻ���ϢժҪ���ϣ������ݿ��е����ݵ�����ʾ
					mergeData(offlineMsgProfileItemList);

					// ������֪ͨ
					Functions.msgNotification();

					// �յ��ѻ���Ϣ���µײ��˵�
					mHasReceivedNew = true;
					((MainActivityGroup) RecentContactActivity.this.getParent()).mHandler.sendEmptyMessage(1);
				}
			}
		}
	}

	/**
	 * �ϲ��ѻ���ϢժҪ����������
	 * 
	 * @param offlineMsgProfileItemList
	 */
	private void mergeData(ArrayList<OfflineMsgProfileItem> offlineMsgProfileItemList) {
		String MERGE = "mergeData";

		// dateList : ����
		// id_time_list ��Id
		// recentContactMap
		LogFactory.d(TAG, "Begin mergeDate...  offlineMsgProfileItemList size =" + offlineMsgProfileItemList.size());

		OfflineMsgProfileItem item;
		RecentContactInfo info;

		for (int i = 0; i < offlineMsgProfileItemList.size(); i++) {

			item = offlineMsgProfileItemList.get(i);
			LogFactory.d(TAG + "item", item.toString());

			info = new RecentContactInfo(item.getFromcid(), item.getFromuid(), "",// /name,getView��ʱ���ȡ
																					// getEmployeeInfoByUid(item.getFromuid())!=null?getEmployeeInfoByUid(item.getFromuid()).getName():"",///name
					item.getLastmsg(), formatServerTime(item.getTime()), item.getCount(), RecentContactInfo.NORMAL_TYPE);
			info.setHasOfflineMSG(false);

			System.out.println("��װ���info��Ϣ��-----" + info.toString());
			LogFactory.d("info", "info == null  " + (info == null));

			LogFactory.d(TAG, "date = " + info.getTime().split(" ")[0]);

			String date = info.getTime().split(" ")[0];

			// have not exist this date, execute add
			if (!dateList.contains(date)) {
				dateList.add(date);
			}

			// update map uid time
			System.out.println("id_time_Map----" + id_time_Map);
			if (id_time_Map.get(info.getUid()) != null) {
				RecentContactInfo recentContactInfo = delSameInDifferentCategory(info);
				boolean hasOfflineMSG = recentContactInfo != null ? recentContactInfo.hasOfflineMSG() : false;
				info.setCount(getMsgCount + info.getCount());
				info.setHasOfflineMSG(info.hasOfflineMSG() || hasOfflineMSG);

				if (recentContactInfo != null) {
					try {
						LogFactory.d(MERGE, "offline Time " + info.getTime());
						LogFactory.d(MERGE, "local   Time " + recentContactInfo.getTime());

						if (stringToDate(info.getTime()).before(stringToDate(recentContactInfo.getTime()))) {
							info.setInfo(recentContactInfo.getInfo());
							info.setTime(recentContactInfo.getTime());
						};
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			id_time_Map.put(info.getUid(), info.getTime());
			HashMap<Integer, RecentContactInfo> idMap = null;
			if (recentContactMap.get(date) != null) {
				idMap = recentContactMap.get(date);
			} else {
				idMap = new HashMap<Integer, RecentContactInfo>();
			}
			idMap.put(item.getFromuid(), info);
			recentContactMap.put(date, idMap);
			LogFactory.d("null", "Info == null  \t " + (info == null) + "  date = " + date);
		}
		LogFactory.d(TAG, "finish mergeDate...");

		mContactHandle.sendEmptyMessage(TYPE_REFRESH);
	}

	private RecentContactInfo delSameInDifferentCategory(RecentContactInfo info) {

		RecentContactInfo recentContactInfo = null;
		HashMap<Integer, RecentContactInfo> idMap = null;

		for (int j = 0; j < dateList.size(); j++) {
			if (recentContactMap.get(dateList.get(j)).get(info.getUid()) != null) {
				idMap = recentContactMap.get(dateList.get(j));
				recentContactInfo = idMap.remove(info.getUid());
				recentContactMap.put(dateList.get(j), idMap);
				break;
			} else {
				continue;
			}
		}

		return recentContactInfo;
	}

	/**
	 * ����Uid����: �޸�bug
	 * 
	 * @param uid
	 */
	public void resetMsgCount(int uid) {

		RecentContactInfo recentContactInfo = null;

		for (int j = 0; j < dateList.size(); j++) {
			String category = dateList.get(j);
			recentContactInfo = recentContactMap.get(category).get(uid);
			if (recentContactInfo != null) {

				mTotalCount -= recentContactInfo.getCount();
				recentContactInfo.setCount(0);
				// updateBottomBar();

				break;
			} else {
				continue;
			}
		}
	}

	public Handler mContactHandle = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case TYPE_REFRESH:
					refresh(null);
					break;
				case TYPE_UPDATESHOWVIEW:
					updateShowView();
					break;
				case TYPE_UPDATESTATEBYPULLSTATE:
					updateStateByPullState();
					break;
				default:
					break;
			}
		};
	};

	/**
	 * ɾ���ڴ��������ϵ��
	 * 
	 * @param cid
	 * @param uid
	 */
	public void deleteRecentUser(int cid, int uid, String date) {

		id_time_Map.remove(uid);
		recentContactMap.get(date).remove(uid);
		// updateShowList(false);
	}

	public void deleteRecentUser(int uid) {

		id_time_Map.remove(uid);
		for (String category : dateList) {
			if (recentContactMap.get(category).get(uid) != null) {
				recentContactMap.get(category).remove(uid);
			}
		}

		boolean result = true;
		try {
			IMOApp.getApp();
			result = IMOApp.imoStorage.deleteRecentContact(RecentContactInfo.NORMAL_TYPE, uid);
		} catch (Exception e) {

			result = false;
			LogFactory.d(TAG, "db delete failed...");
			e.printStackTrace();
		}
	}

	@Override
	protected boolean needObserver() {
		return false;
	}

	/**
	 * ����UId ���� Ա��������
	 * 
	 * @param uid
	 * @return
	 */
	public EmployeeInfoItem getEmployeeInfoByUid(int uid) {
		for (Integer deptId : mGlobal.deptUserInfoMap.keySet()) {
			EmployeeInfoItem temp = mGlobal.deptUserInfoMap.get(deptId).get(uid);
			if (temp != null) {
				return temp;
			}
		}
		return null;
	}

	public Node getNodeByUid(int uid) {
		return mGlobal.mNodeMap.get(uid);
	}

	public int mTotalCount = 0;
	protected int getMsgCount = 0;
	public boolean mHasReceivedNew = false; // ////����ֻ����ʾһ�� �������û�е�����Ϣ���Ժ�Ͳ�����ʾ����

	@Override
	protected void onResume() {
		LogFactory.d("onResume", TAG + ": onResume....");
		hasOfflineMSG = false;

		super.onResume();

		refreshHandler.sendEmptyMessage(0);
	}

	private Handler refreshHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}

			mHasReceivedNew = false; // Reset

			deleteEmptyCategory();
			updateShowList(true); // sort
		};
	};

	@Override
	protected void onPause() {
		LogFactory.e("RecentContactActivity", "onPause");

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogFactory.e("RecentContactActivity", "onDestroy");

		super.onDestroy();
	}

	@Override
	protected void onStop() {
		LogFactory.e("RecentContactActivity", "onStop");
		super.onStop();
	}

	private ArrayList<String> dateList = null;
	// �����ϵ��Id
	private ArrayList<Integer> idList = null;
	// RecentContactMap
	public HashMap<String, HashMap<Integer, RecentContactInfo>> recentContactMap = null;

	private HashMap<Integer, String> id_time_Map = null;

	/**
	 * ���ر��������ϵ������
	 */
	private void loadRecentContactData() {

		dateList = new ArrayList<String>();
		idList = new ArrayList<Integer>();
		recentContactMap = new HashMap<String, HashMap<Integer, RecentContactInfo>>();

		try {
			IMOApp.imoStorage.getRecentContactData(dateList, idList, recentContactMap);

			for (int i = 0; i < dateList.size(); i++) {
				String date = dateList.get(i);
				LogFactory.d(TAG1, "date=" + date + "-------------------");

				HashMap<Integer, RecentContactInfo> itemMap = recentContactMap.get(date);
				for (Integer uid : recentContactMap.get(date).keySet()) {
					LogFactory.d(TAG1, "recentContactMap---->" + itemMap.get(uid).toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// reset
			recentContactMap = new HashMap<String, HashMap<Integer, RecentContactInfo>>();
		}
	}

	/**
	 * ������ʾ�б�:���������Ҫ��������
	 */
	private void updateShowList(boolean needSort) {

		System.out.println("updateShowList-----------");
		if (needSort) {
			sortDate(); // 1-
			sortId(); // 2-
		}

		mCategoryList.clear();
		mDataList.clear();

		RecentContactInfo recentContactInfo = null;

		String category = null;

		int curIndex = 0;

		for (int i = 0; i < dateList.size(); i++) {// ���

			category = dateList.get(i);

			recentContactInfo = new RecentContactInfo(category);
			mCategoryList.add(recentContactInfo.toString());
			mDataList.add(recentContactInfo);

			if (id_time_list != null) {

				for (int j = 0; j < recentContactMap.get(category).keySet().size(); j++) {
					if (curIndex < id_time_list.size()) {
						int uid = id_time_list.get(curIndex).getKey();
						mDataList.add(recentContactMap.get(category).get(uid));
					}
					curIndex++;
				}
			}
		}
		// updateShowView();
		mContactHandle.sendEmptyMessage(TYPE_UPDATESHOWVIEW);
	}

	private void updateShowView() {

		if (mDataList.size() == mCategoryList.size() || mDataList.size() <= 1) {
			mCategoryList.clear();
			mDataList.clear();

			dateList.clear();
			id_time_Map.clear();
			recentContactMap.clear();

			show2State(true);
		} else {
			show2State(false);
			adapter.notifyDataSetChanged();

			// ///����BottomBar����ʾ
			updateBottomBar();
		}
	}

	/**
	 * ���µײ��˵�����ʾ��ˢ��new
	 */
	private void updateBottomBar() {
		boolean hasNewMSG = false;

		if (hasOfflineMSG) {
			hasNewMSG = true;
		} else {
			if (mTotalCount > 0) {
				hasNewMSG = true;
			} else {
				hasNewMSG = false;
			}
		}

		((MainActivityGroup) RecentContactActivity.this.getParent()).updateShowNewMSG(hasNewMSG);
	}

	/**
	 * ���������ϵ������Դ
	 */
	public void addOrUpdate(RecentContactInfo info, Boolean isFromDialogue) {
		// ���ڶ�����棬��Ҫ���������ϵ����ʾ�� ��� or������ʾ��λ��

		getMsgCount = info.getCount();
		if (info.getCount() > 0) {
			mHasReceivedNew = true;
			mTotalCount += 1;
			if (mTotalCount > 0) {
				((MainActivityGroup) RecentContactActivity.getActivity().getParent()).mHandler.sendEmptyMessage(1);
			} else {
				((MainActivityGroup) RecentContactActivity.getActivity().getParent()).mHandler.sendEmptyMessage(0);
			}
		}

		// 1- have not exist this date, execute add
		String mCategory = info.getTime().split(" ")[0];
		if (!dateList.contains(mCategory)) {
			dateList.add(mCategory);
		}

		// 2-update map uid time
		if (id_time_Map.get(info.getUid()) != null) {
			if (!isFromDialogue) {
				RecentContactInfo recentContactInfo = delSameInDifferentCategory(info);
				int oldCount = recentContactInfo != null ? recentContactInfo.getCount() : 0;
				boolean hasOfflineMSG = recentContactInfo != null ? recentContactInfo.hasOfflineMSG() : false;
				info.setCount(oldCount + info.getCount());
				info.setHasOfflineMSG(info.hasOfflineMSG() || hasOfflineMSG);

				if (recentContactInfo != null) {
					try {
						if (stringToDate(info.getTime()).before(stringToDate(recentContactInfo.getTime()))) {
							info.setInfo(recentContactInfo.getInfo());
							info.setTime(recentContactInfo.getTime());
						};
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else {
				info.setCount(0);
			}
		}
		id_time_Map.put(info.getUid(), info.getTime());

		String date = info.getTime().split(" ")[0];
		HashMap<Integer, RecentContactInfo> idMap = null;
		if (recentContactMap.get(date) != null) {
			idMap = recentContactMap.get(date);
		} else {
			idMap = new HashMap<Integer, RecentContactInfo>();
		}

		idMap.put(info.getUid(), info);
		recentContactMap.put(date, idMap);

		LogFactory.d("null", "Info == null" + (info == null) + "  date = " + date);

		if (IMOApp.getApp().mLastActivity instanceof RecentContactActivity) {
			refreshHandler.sendEmptyMessage(0);
		}

		// ��ǰ���ں�̨����״̬
		if (mGlobal.hasRunInBackground) {

			boolean needNotice = !info.isFromLoginUser;

			if (Globe.is_notification) {

				NoticeManager.count++;// ///�����յ�����Ϣ
				CharSequence showLastMessage = "";
				try {
					if (info.getInfo() != null && !info.getInfo().equals("")) {
						showLastMessage = MessageDataFilter.jsonToText(new JSONObject(info.getInfo()));
					} else {
						showLastMessage = "";
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showLastMessage = "Message Error";
				}

				if (needNotice) {
					NoticeManager.updateNewsNotice(notificationManager, notice, "", showLastMessage, info.getCid(), info.getUid(), info.getName());
				}

			}
		}
	}

	/**
	 * �� �Ի�����
	 * 
	 * @param info
	 */
	public void addOrUpdate(RecentContactInfo info) {
		addOrUpdate(info, false);
	}

	private final int TYPE_REFRESH = 0; // ˢ��
	private final int TYPE_UPDATESHOWVIEW = 1; // ������ʾ��View
	public static final int TYPE_UPDATESTATEBYPULLSTATE = 2; // ����״̬

	/**
	 * ��̬���������ϵ��״̬
	 */
	public void updateStateByPullState() {
		adapter.notifyDataSetChanged();
	}

	/**
	 * �����в������κ�����,��Ҫɾ���÷���.
	 */
	private void deleteEmptyCategory() {
		LogFactory.d("category", "--------------------->deleteEmptyCategory");

		ArrayList<String> emptyList = new ArrayList<String>();

		for (int i = 0; i < dateList.size(); i++) {
			String category = dateList.get(i);
			if (recentContactMap.get(category) == null || recentContactMap.get(category).keySet().size() == 0) {
				emptyList.add(category);
				recentContactMap.remove(category);
				LogFactory.d("category", "empty category " + dateList.get(i));
			}
		}
		dateList.removeAll(emptyList);
	}

	/**
	 * build Show Item
	 */
	private void buildShowItemInfo() {

		id_time_Map = new HashMap<Integer, String>();

		RecentContactInfo recentContactInfo = null;

		for (int j = 0; j < idList.size(); j++) {

			int id = idList.get(j);

			LogFactory.d("build", "id = " + id);

			for (int k = 0; k < dateList.size(); k++) {

				String category = dateList.get(k);

				HashMap<Integer, RecentContactInfo> temp_category_Map = recentContactMap.get(category);

				recentContactInfo = temp_category_Map.get(id);

				if (recentContactInfo != null) {
					id_time_Map.put(id, recentContactInfo.getTime());
					LogFactory.d(TAG, "id_time_map------item:" + id + " > " + recentContactInfo.getTime());
					break;
				} else {
					LogFactory.d(TAG, "------go to next --------------");
					// û���ҵ�������£���Ҫ����������һ�����
					continue;
				}
			}
		}

		// must add map turn 2 list
		id_time_list = new ArrayList<Map.Entry<Integer, String>>(id_time_Map.entrySet());
	}

	/**
	 * sortDate
	 */
	private void sortDate() {
		LogFactory.d("sort", "sort date...");

		if (dateList != null && dateList.size() > 0) {

			Collections.sort(dateList, new Comparator<String>() {

				@Override
				public int compare(String lhs, String rhs) {

					Date date1 = null;
					Date date2 = null;

					try {
						date1 = stringToDate2(lhs);
						date2 = stringToDate2(rhs);
					} catch (Exception e) {
						e.printStackTrace();
						return 0;
					}

					if (date1.before(date2)) {
						return 1;
					} else if (date1.after(date2)) {
						return -1;
					} else {
						return 0;
					}
				}
			});
		}
	}

	ArrayList<Map.Entry<Integer, String>> id_time_list = null;

	/**
	 * sortId
	 */
	private void sortId() {
		LogFactory.d("sort", "sort id...");

		id_time_list = new ArrayList<Map.Entry<Integer, String>>(id_time_Map.entrySet());

		for (int i = 0; i < id_time_list.size(); i++) {
			LogFactory.d("sort", id_time_list.get(i).toString());
		}

		Collections.sort(id_time_list, new Comparator<Map.Entry<Integer, String>>() {

			@Override
			public int compare(Map.Entry<Integer, String> item1, Map.Entry<Integer, String> item2) {

				Date date1 = null;
				Date date2 = null;

				try {
					date1 = stringToDate(item1.getValue());
					date2 = stringToDate(item2.getValue());
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}

				if (date1.before(date2)) {
					return 1;
				} else if (date1.after(date2)) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		LogFactory.d("sort", "--------------------------------------");
		for (int i = 0; i < id_time_list.size(); i++) {
			LogFactory.d("sort", id_time_list.get(i).toString());
		}
	}

	private Date stringToDate(String str) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String[] tempArray = str.split(" ");
		if (tempArray.length == 3) {
			str = tempArray[1] + " " + tempArray[2];
		}
		Date date = sdf.parse(str);
		return date;
	}

	private Date stringToDate2(String str) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(str);
		return date;
	}

	/**
	 * ��ʽ��ʱ��
	 * 
	 * @param second
	 * @return
	 * @throws Exception
	 */
	public String formatServerTime(int second) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = "";
		try {
			date = sdf.format(new Date(second * 1000L));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public String getCurTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = "";
		try {
			date = sdf.format(new Date(System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * ��Ӧ���˷�����������Ϣ
	 * 
	 * @param command
	 */
	private void responseServerMSG(short command) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (isFinishing()) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			LogFactory.d(TAG, "KEYCODE_BACK has clicked .");
			DialogFactory.promptExit(mContext).show();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ʵʱ��������Ա��
	 * 
	 * @param command
	 */
	private void responseServerPullEmployeeState(short command) {

		LogFactory.d(TAG + "Push", "----------responseServerPullEmployeeState ---------");

		TCPConnection con = getTcpConnection();
		if (con == null) {
			return;
		}

		UserStatusChangeInPacket inPacket = (UserStatusChangeInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (inPacket == null) {
			return;
		}
		// private int cid;
		Integer uid = inPacket.getUid();
		Integer status = inPacket.getStatus() + 0;
		LogFactory.d("Push", "uid = " + uid + "status---->" + status);

		StateHandle.getInstance().updateUI(uid, status);
	}
}

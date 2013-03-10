package com.imo.module.organize;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.dataengine.DataEngine;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.MainActivityGroup;
import com.imo.module.config.SystemSetActivity;
import com.imo.module.config.UserConfigActivity;
import com.imo.module.contact.OuterContactBasicInfo;
import com.imo.module.contact.OuterContactItem;
import com.imo.module.dialogue.DialogueActivity;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;
import com.imo.module.organize.struct.NodeManager;
import com.imo.module.organize.view.OrganizeAdapter;
import com.imo.module.organize.view.SearchResultAdapter;
import com.imo.module.organize.view.StructNavView;
import com.imo.network.Log.ConnectionLog;
import com.imo.network.net.EngineConst;
import com.imo.network.net.TCPConnection;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.CommonOutPacket;
import com.imo.network.packages.DeptMaskItem;
import com.imo.network.packages.EmployeeInfoItem;
import com.imo.network.packages.GetEmployeesStatusInPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.InnerContactorItem;
import com.imo.network.packages.OuterContactorItem;
import com.imo.network.packages.ReloginOutPacket;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.ImageUtil;
import com.imo.util.LogFactory;
import com.imo.util.NoticeManager;
import com.imo.util.PreferenceManager;
import com.imo.util.SystemInfoManager;
import com.imo.view.ChangeStatePop;
import com.imo.view.ChangeStatePop.OnStateClickListener;
import com.imo.view.SearchBar;
import com.imo.view.SearchBar.OnSearchListener;

/**
 * 组织架构界面
 * 
 * @author CaixiaoLong
 * 
 */
public class OrganizeActivity extends AbsBaseActivityNetListener {

	private String TAG = "Organize";

	private OrganizeAdapter adapter;

	private StructNavView mStructNavView;

	private SearchBar mSearchBar;

	private ListView mListView;

	private ListView mSearchRestltListView;

	private View organizeView;

	private SearchResultAdapter mSearchResultAdapter;

	private ChangeStatePop mChangeStatePop;

	public View pop_view;

	private static OrganizeActivity mActivity;

	/**
	 * 获得Activity实例对象
	 * 
	 * @return
	 */
	public static OrganizeActivity getActivity() {
		return mActivity;
	}

	public static void launch(Context c) {
		Intent intent = new Intent(c, OrganizeActivity.class);
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		mActivity = this;

		setContentView(R.layout.organize_activity);

		organizeView = findViewById(R.id.organize);

		mTitleBar.initOrganizeTitlebar();

		mTitleBar.setOrganizeTitleData(Globe.myself == null ? null : Globe.myself.getName(), Globe.myself == null ? null : Globe.myself.getSign());

		pop_view = findViewById(R.id.pop_view);

		pop_view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				pop_view.setVisibility(View.GONE);
				mSearchBar.updateCursorState(false);
				SystemInfoManager.hideKeyBoard(getActivity());
			}
		});

		mSearchBar = (SearchBar) findViewById(R.id.searchbar);

		mStructNavView = (StructNavView) findViewById(R.id.structNavView);
		mStructNavView.setVisibility(View.GONE);

		mListView = (ListView) findViewById(R.id.organizeListView);

		mSearchRestltListView = (ListView) findViewById(R.id.searchResultListView);

		mSearchResultAdapter = new SearchResultAdapter(mContext);

		mChangeStatePop = new ChangeStatePop(LayoutInflater.from(mContext).inflate(R.layout.changestate_dialog, null, true));

	}

	protected boolean needObserver() {
		return false;
	}

	/**
	 * 更新头像
	 */
	private void updateFace(boolean networkIsConnected) {
		LogFactory.e("updateFace", "" + DataEngine.getInstance().getLogicStatus());

		mTitleBar.updateStateIcon(networkIsConnected);

		if (networkIsConnected) {

			if (Globe.bm_head != null) {
				LogFactory.d(TAG, "bm_head not null");
				mTitleBar.setFaceBitmap(Globe.bm_head);
			} else {
				mTitleBar.setFaceDefault(mThisUidIsBoy(EngineConst.uId));
			}
			// //显示onLine图标

		} else {
			// / 头像跳灰色
			if (Globe.bm_head != null) {
				ImageUtil.setFaceImg(mTitleBar.getFaceView(), Globe.bm_head, 0, -1);
			} else {
				mTitleBar.setOfflineFaceDefault();
			}
			// //显示offlineLine图标
		}
	}

	/**
	 * titleBar中的头像更新Handler
	 */
	public Handler titleBarHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			System.out.println("titleBarHandler1:" + EngineConst.isNetworkValid);
			System.out.println("titleBarHandler2:" + EngineConst.isReloginSuccess);

			ConnectionLog.MusicLogInstance().addLog("titleBarHandler isNetworkValid:" + EngineConst.isNetworkValid + ",isReloginSuccess :" + EngineConst.isReloginSuccess);

			updateFace(EngineConst.isNetworkValid && EngineConst.isReloginSuccess);
		}
	};

	@Override
	protected void onPause() {
		LogFactory.e("OrganizeActivity", "onPause");

		super.onPause();
	}

	@Override
	protected void onStop() {
		LogFactory.e("OrganizeActivity", "onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		LogFactory.d("onResume", TAG + ": onResume....");

		super.onResume();
		mTitleBar.updateSign(Globe.myself == null ? null : Globe.myself.getSign());

		updateFace(EngineConst.isNetworkValid && EngineConst.isReloginSuccess);

		handlerTree.sendEmptyMessage(OrganizeActivity.TYPE_UPDATEPULLSTATE);
	}

	ArrayList<Integer> special_hide_dept_ids = new ArrayList<Integer>();

	private void get_special_hide_dept_ids(int uid) {
		Integer did = findDidByUid(uid);
		DeptMaskItem deptMaskItem = null;
		while (did != null && did != 0) {
			special_hide_dept_ids.add(did);
			deptMaskItem = IMOApp.getApp().deptInfoMap.get(did);
			if (deptMaskItem == null)
				return;
			did = deptMaskItem.getParent_dept_id();
		}
	}

	@Override
	protected void registerEvents() {
		String isFirstLoading = (String) PreferenceManager.get("IMOLoading", new String[] {
				"isFirstLoading", "no"
		});
		MainActivityGroup.getActivityGroup().updateTipShow(!isFirstLoading.equals("yes"));

		if (getTcpConnection() == null) {
			resetConnection();
			if (getTcpConnection() != null) {
				IMOApp.getDataEngine().addToObserverList(this);
			}
		} else {
			IMOApp.getDataEngine().addToObserverList(this);
		}

		// //1- 更新自己的状态 login over
		doRequestOwnState();
		// / 2- 发送CrashReport
		sendCrashMSG();

		getOrganizeTree();

		getContactTree();

		mTitleBar.setSystemSetListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					SystemSetActivity.launch(OrganizeActivity.this);
					return true;
				}

				return true;
			}
		});

		mTitleBar.setFaceOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UserConfigActivity.launch(mContext);
			}
		});

		// ///状态弹窗显示
		mTitleBar.setStateOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mChangeStatePop.popUp(getWindow().getDecorView());
			}
		});

		mChangeStatePop.setListener(new OnStateClickListener() {

			@Override
			public void onClickAction() {
				Globe.canConnect = false;
				requestLogOut(false);
			}
		});

		mChangeStatePop.setReLoginListener(new OnStateClickListener() {

			@Override
			public void onClickAction() {
				if (EngineConst.isNetworkValid && EngineConst.isReloginSuccess) {
					return;
				} else if (ConnectionChangeReceiver.pingAddress()) {
					System.out.println("shoudong old connection id = " + EngineConst.IMO_CONNECTION_ID);
					EngineConst.isReloginSuccess = false;
					String oldConnectionID = EngineConst.IMO_CONNECTION_ID;

					EngineConst.IMO_CONNECTION_ID = EngineConst.GenerateRandomString();

					System.out.println("shoudong new connection id :" + EngineConst.IMO_CONNECTION_ID + ",old connectionID :" + oldConnectionID);

					AppService.getService().setTcpConnection((TCPConnection) AppService.getService().getNIOThreadInstance().newTCPConnection(EngineConst.IMO_CONNECTION_ID, EngineConst.IMO_SERVER_ADDRESS, true));

					AppService.getService().getNIOThreadInstance().release(oldConnectionID, false);

					ReloginOutPacket out = new ReloginOutPacket(ByteBuffer.wrap(EngineConst.sessionKey), IMOCommand.IMO_GET_RELOGIN, EngineConst.cId, EngineConst.uId);
					AppService.getService().getNIOThreadInstance().send(EngineConst.IMO_CONNECTION_ID, out, false);
				}
			}
		});

		mSearchBar.setOnClickEvent();

		mSearchBar.setOnSearchListener(new OnSearchListener() {

			@Override
			public void onSearch(View v) {
				LogFactory.d(TAG, "Dynamic key = " + ((EditText) v).getText());

				doSearch(((EditText) v).getText().toString().trim());
			}

			private void doSearch(String key) {
				// 查询的结果UIDList
				update2State(true);
				// 数据库中查询结果
				ArrayList<Integer> deptUserId = IMOApp.imoStorage.search(key);

				if (deptUserId != null) {
					mSearchResultAdapter.setShowData(getSearchResult(deptUserId));
					mSearchRestltListView.setAdapter(mSearchResultAdapter);
				} else {
					mSearchResultAdapter.setEmpty();
				}
			}

			@Override
			public void initState() {
				update2State(false);
			}
		});

		mSearchRestltListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				if (mSearchResultAdapter.getItem(position).getId() == EngineConst.uId) {
					EmployeeDetailActivity.launch(mContext);

				} else {
					Intent intent = new Intent(mContext, DialogueActivity.class);
					Bundle data = new Bundle();
					Node curNode = mSearchResultAdapter.getItem(position);
					data.putInt("cid", curNode.getCid());
					data.putInt("uid", curNode.getId());
					data.putString("name", curNode.getNodeData().nodeName);
					data.putBoolean("sex", curNode.getNodeData().isBoy);
					startActivity(intent.putExtras(data));
				}
			}
		});
	}

	public void getContactTree() {
		// 1- 初始化内部联系Group_EmployeeInfoList MAP
		initInnerGroupMap();

		buildContactTree();
	}

	/**
	 * 根据ContactId查找组织结构数据
	 * 
	 * @param innerGroupIdMap
	 */
	public void initInnerGroupMap() {
		mGlobal.mInnerGroupContactMap = new HashMap<Integer, ArrayList<EmployeeInfoItem>>();

		ArrayList<EmployeeInfoItem> employeeInfoItemList = null;

		for (Integer groupId : mGlobal.innerGroupIdMap.keySet()) {
			// init group contactInfoList
			employeeInfoItemList = new ArrayList<EmployeeInfoItem>();

			ArrayList<Integer> contactIdList = mGlobal.innerGroupContactMap.get(groupId);

			if (contactIdList != null) {
				for (int i = 0; i < contactIdList.size(); i++) {
					employeeInfoItemList.add(RecentContactActivity.getActivity().getEmployeeInfoByUid(contactIdList.get(i)));
				}
				// 构建内部联系人组和对应的 联系人列表的映射
				mGlobal.mInnerGroupContactMap.put(groupId, employeeInfoItemList);
			} else {
				LogFactory.d(TAG, " groupId = " + groupId + " is empty...");
			}
		}
	}

	/**
	 * 构建完整的Tree
	 * 
	 * 构建InnerContact 构建OuterContact
	 */
	public void buildContactTree() {
		buildEmptyTree();
		buildInnerContactTree();
		buildOuterContactTree();
	}

	/**
	 * build Empty Tree
	 */
	private void buildEmptyTree() {
		mGlobal.rootNodeContact.setDept(true);
		mGlobal.rootNodeInner.setDept(true);
		mGlobal.rootNodeOuter.setDept(true);
		NodeManager.addChildNode(mGlobal.rootNodeContact, mGlobal.rootNodeInner);
		NodeManager.addChildNode(mGlobal.rootNodeContact, mGlobal.rootNodeOuter);
	}

	/**
	 * build Inner Contact Tree
	 */
	private void buildInnerContactTree() {
		NodeManager.clearChildNode(mGlobal.rootNodeInner);
		// /根据数据Map构建Node对象，添加到AllNodeList中去。只是添加分组Node
		for (Integer groupId : mGlobal.innerGroupIdMap.keySet()) {// //mGlobal.
																	// 使用全局数据
			InnerContactorItem item = mGlobal.innerGroupIdMap.get(groupId);
			Node innerGroupNode = new Node(new NodeData(item.getGroupName(), ""));
			innerGroupNode.setId(item.getGroupID());
			innerGroupNode.setDept(true);
			NodeManager.addChildNode(mGlobal.rootNodeInner, innerGroupNode);

			ArrayList<EmployeeInfoItem> employeeInfoItems = mGlobal.mInnerGroupContactMap.get(groupId);

			if (employeeInfoItems != null) {
				for (int i = 0; i < employeeInfoItems.size(); i++) {
					EmployeeInfoItem info = employeeInfoItems.get(i);
					String sign = "";
					if (info != null) {
						sign = mGlobal.innerContactWorkSignMap.get(info.getUid());
					}
					if (sign == null || sign.trim().equals("")) {
						sign = "";
					} else {
						sign = " — " + sign;
					}

					if (info != null) {
						Node contactNode = new Node(new NodeData(isBoy(info.getGender()), (info.getName() + sign)));
						contactNode.setId(info.getUid());
						NodeManager.addChildNode(innerGroupNode, contactNode);
					} else {
						LogFactory.d(TAG, " EmployeeInfoItem == null");
					}
				}
			} else {
				LogFactory.d(TAG, "groupId = " + groupId + " is Empty...");
			}
		}
	}

	/**
	 * build Outer Contact Tree
	 * 
	 * flag = 0x40
	 */
	private void buildOuterContactTree() {

		NodeManager.clearChildNode(mGlobal.rootNodeOuter);

		for (Integer groupId : mGlobal.outerGroupIdMap.keySet()) {

			OuterContactorItem item = mGlobal.outerGroupIdMap.get(groupId);
			Node outerGroupNode = new Node(new NodeData(item.getGroupName(), ""));
			outerGroupNode.setId(item.getGroupID());
			outerGroupNode.setDept(true);
			NodeManager.addChildNode(mGlobal.rootNodeOuter, outerGroupNode);

			ArrayList<OuterContactItem> outerContact_ID_Items = mGlobal.outerGroupContactMap.get(groupId);

			if (outerContact_ID_Items != null) {
				for (int i = 0; i < outerContact_ID_Items.size(); i++) {
					int uid = outerContact_ID_Items.get(i).getUid();
					OuterContactBasicInfo info = mGlobal.outerContactInfoMap.get(uid);

					String cropName = "";
					if (info != null) {
						cropName = mGlobal.outerContactCorpMap.get(info.getCid());
					}

					if (cropName == null || cropName.trim().equals("")) {
						cropName = "";
					} else {
						cropName = " — " + cropName;
					}

					if (info != null) {
						Node contactNode = new Node(new NodeData(isBoy(info.getGender()), (info.getName() + cropName)));
						contactNode.setCid(info.getCid());
						contactNode.setId(info.getUid());
						contactNode.setNeedShow(isNeedShow(groupId, uid));
						mGlobal.mNodeMap.put(contactNode.getId(), contactNode);
						NodeManager.addChildNode(outerGroupNode, contactNode);
					} else {
						LogFactory.d(TAG, " OuterContactInfoItem == null");
					}
				}
			} else {
				LogFactory.d(TAG, "outer groupId = " + groupId + " is Empty...");
			}
		}
	}

	private boolean isNeedShow(Integer groupId, int uid) {
		ArrayList<OuterContactItem> list = mGlobal.outerGroupContactMap.get(groupId);
		for (OuterContactItem outerContactItem : list) {
			if (outerContactItem.getUid() == uid && outerContactItem.getFlag() == 0x4D) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see com.imo.activity.AbsBaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		LogFactory.e("OrganizeActivity", "onDestroy");

		if (EngineConst.isConnected) {
			CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_EXIT, EngineConst.cId, EngineConst.uId);

			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
		}
		super.onDestroy();
	}

	/**
	 * 更新显示List的状态
	 */
	private void update2State(boolean state) {

		mSearchBar.updateCursorState(state);
		// state = !state;
		if (!state) {
			organizeView.setVisibility(View.VISIBLE);
			mSearchRestltListView.setVisibility(View.GONE);
		} else {
			// 显示搜索结果状态
			mSearchRestltListView.setVisibility(View.VISIBLE);
			organizeView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean CanAcceptHttpPacket() {
		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		if (IMOCommand.IMO_UPDATE_STATUS == command)
			return true;
		if (IMOCommand.IMO_GET_EMPLOYEE_STATUS == command)
			return true;

		if (IMOCommand.IMO_EXIT == command)
			return true;

		return false;
	}

	public Handler mCommandHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			short command = (short) msg.arg1;
			switch (command) {
				case IMOCommand.IMO_UPDATE_STATUS:
					LogFactory.d(TAG, "-------->Login Over");
					break;
				case IMOCommand.IMO_GET_EMPLOYEE_STATUS:
					responseState(command);
					break;
				default:
					break;
			}
		};
	};

	// ================实现循环的去请求部门数据=====================
	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {

		super.NotifyPacketArrived(aConnectionId, command);

		Message msg1 = new Message();
		msg1.arg1 = command;
		mCommandHandler.sendMessage(msg1);

	}

	// ==================状态重新获取==============================
	private int parent_hasRequestCount = 0; // 已经请求的次数
	private int parent_StateRequestCount = 0; // / 请求的总次数

	/**
	 * 响应：员工的在线状态
	 * 
	 * 一次请求最大值为100
	 * 
	 * @param command
	 */
	private void responseState(short command) {

		LogFactory.d("EmployeeState", "----------responseEmployeeState ---------");
		GetEmployeesStatusInPacket inPacket = (GetEmployeesStatusInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (inPacket == null) {
			return;
		}

		inPacket.getCid();
		int[] uid = inPacket.getUid();
		int[] status = inPacket.getStatus();

		for (int i = 0; i < uid.length; i++) {
			mGlobal.userStateMap.put(uid[i], status[i]);
		}

		// 当前部门的请求是否都已经完成了
		if (parent_hasRequestCount < parent_StateRequestCount) {

			Object[] idArrays = getRequestUserIdArray();
			if (idArrays != null) {
				doEmployeeState(((int[]) idArrays[0]).length, ((int[]) idArrays[0]), ((int[]) idArrays[1]));
			}
		} else {
			LogFactory.d("EmployeeState", "状态请求完成。。。。。");
			// /更新
			StateHandle.getInstance().updateAllState();
		}
	}

	private int PARENT_MAX_STATE_REQUEST_COUNT = 100;

	/**
	 * 状态请求数组ID
	 * 
	 * @return
	 */
	private Object[] getRequestUserIdArray() {

		int[] requestCIds = null;
		int[] requestUIds = null;

		if (parent_StateRequestCount == 0) {
			return null;
		}

		if (parent_StateRequestCount - 1 > parent_hasRequestCount) {

			requestCIds = new int[PARENT_MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[PARENT_MAX_STATE_REQUEST_COUNT];

			LogFactory.d("State--HasRequestCount", "hasRequestCount = " + parent_hasRequestCount);

			System.arraycopy(mGlobal.getAllCidArray(), parent_hasRequestCount * PARENT_MAX_STATE_REQUEST_COUNT, requestCIds, 0, PARENT_MAX_STATE_REQUEST_COUNT);
			System.arraycopy(mGlobal.getAllUidArray(), parent_hasRequestCount * PARENT_MAX_STATE_REQUEST_COUNT, requestUIds, 0, PARENT_MAX_STATE_REQUEST_COUNT);

			this.parent_hasRequestCount++;

		} else {
			LogFactory.d("StateHasRequestCount", " State has Request Completed !! hasRequestCount = " + parent_hasRequestCount);

			requestCIds = new int[mGlobal.getAllCidArray().length - parent_hasRequestCount * PARENT_MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[mGlobal.getAllUidArray().length - parent_hasRequestCount * PARENT_MAX_STATE_REQUEST_COUNT];

			System.arraycopy(mGlobal.getAllCidArray(), parent_hasRequestCount * PARENT_MAX_STATE_REQUEST_COUNT, requestCIds, 0, requestUIds.length);
			System.arraycopy(mGlobal.getAllUidArray(), parent_hasRequestCount * PARENT_MAX_STATE_REQUEST_COUNT, requestUIds, 0, requestUIds.length);

			this.parent_hasRequestCount++;

		}

		return new Object[] {
				requestCIds, requestUIds
		};
	}

	// ==================状态重新获取==============================

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object param) {

	}

	/**
	 * Login Over 发送当前用户状态 请求包
	 */
	private void doRequestOwnState() {

		LogFactory.d(TAG, "doRequestOwnState ");

		if (mNIOThread != null) {
			// 修改command
			CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_UPDATE_STATUS, EngineConst.cId, EngineConst.uId);

			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
		} else {
			finish();
		}

	}

	public static final int TYPE_GETTREE = 0;
	public static final int TYPE_UPDATEPULLSTATE = 1;

	public Handler handlerTree = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case TYPE_GETTREE:
					getOrganizeTree();
					break;
				case TYPE_UPDATEPULLSTATE:
					if (adapter != null)
						adapter.notifyDataSetChanged();
					break;

				default:
					break;
			}

		};
	};

	/**
	 * All User IDs
	 */
	int[] mAllUserIds = null;
	int[] mAllCIds = null;

	HashMap<Integer, Integer> curUserStateMap;

	private void getOrganizeTree() {
		get_special_hide_dept_ids(EngineConst.uId);

		if (mGlobal.deptid != null) {
			buildOrganizeTree();

			addUser2Tree();

			rootNodeDept = nodeMap.get(0);

			adapter = new OrganizeAdapter(mContext, rootNodeDept);

			mListView.setAdapter(adapter);

			mListView.setOnItemClickListener(mItemClickListener);

		}

		if (rootNodeDept != null) {
			int size = rootNodeDept.getChildNodes() != null ? rootNodeDept.getChildNodes().size() : 0;
			LogFactory.d("rootNodeDept", "root child count = " + size);
		}

	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			mSearchBar.updateCursorState(false);

			Node curNode = adapter.getItem(position);

			if (!curNode.isDept()) {

				if (adapter.getItem(position).getId() == EngineConst.uId) {
					EmployeeDetailActivity.launch(mContext);

				} else {
					Intent intent = new Intent(mContext, DialogueActivity.class);
					Bundle data = new Bundle();
					data.putInt("cid", EngineConst.cId);
					data.putInt("uid", curNode.getId());
					data.putString("name", curNode.getNodeData().nodeName);
					data.putBoolean("sex", curNode.getNodeData().isBoy);

					LogFactory.d("data", "cid=" + EngineConst.cId + "  uid =" + curNode.getId() + "  mName= " + curNode.getNodeData().nodeName);

					Integer myMsgUserid = curNode.getId();
					if (!IMOApp.sendMsgUserId.isEmpty()) {
						if (IMOApp.sendMsgUserId.contains(curNode.getId())) {
							IMOApp.sendMsgUserId.remove(myMsgUserid);
							System.out.println("剩余的 msg id-----" + IMOApp.sendMsgUserId);
							if (IMOApp.sendMsgUserId.isEmpty()) {
								MainActivityGroup.getActivityGroup().mHandler.sendEmptyMessage(0);
							}
						}
					}
					startActivity(intent.putExtras(data));

				}
			} else {

				mStructNavView.setVisibility(View.VISIBLE);
				adapter.showChildNodes(curNode);
				mListView.setSelection(0);// / for selected first node
				if (mStructNavView.getViewGroupChildCount() == 0) {
					mStructNavView.addItem(rootNodeDept.getNodeData().nodeName, rootNodeDept);
				}
				mStructNavView.addItem(curNode.getNodeData().nodeName, curNode);
				// 实现导航点击事件的封装。
				mStructNavView.setOnItemClickListener(adapter);
			}

		}
	};

	/**
	 * 测试请求到的数据，开始构造组织架构树
	 */
	Node rootNodeDept;

	// DeptMaskItem rootDeptMask ;

	Node curNode;

	/**
	 * 部门节点 Map
	 */
	HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();
	HashMap<Integer, Integer> deptNodeNextSiblingMap = new HashMap<Integer, Integer>();

	// 部门Id List
	ArrayList<Integer> deptIdList = new ArrayList<Integer>();

	/**
	 * 构建组织架构部门树
	 */
	private void buildOrganizeTree() {
		// 1-构建DeptNode
		if (mGlobal.deptid == null) {
			return;
		}

		for (int i = 0; i < mGlobal.deptid.length; i++) {
			int id = mGlobal.deptid[i];

			DeptMaskItem deptInfo = mGlobal.deptInfoMap.get(id);
			if (deptInfo == null) {
				IMOApp.getApp().turn2LoginForLogout();
				return;
			}
			Node deptNode = null;
			if (deptInfo.getDept_id() == 0) {
				deptNode = new Node(new NodeData("组织结构"));
			} else {
				deptNode = new Node(new NodeData(deptInfo.getName()));
			}
			deptNode.setId(deptInfo.getDept_id());
			deptNode.setDept(true);
			// if (currentDeptNeedHide(id))
			// deptNode.setNeedShow(false);
			nodeMap.put(deptInfo.getDept_id(), deptNode);
			deptNodeNextSiblingMap.put(deptInfo.getDept_id(), deptInfo.getNext_sibling());
			deptIdList.add(deptInfo.getDept_id());
		}

		for (int i = 0; i < mGlobal.deptid.length; i++) {
			int id = mGlobal.deptid[i];
			Node curDeptNode = nodeMap.get(id);
			if (id != 0) {
				DeptMaskItem deptInfo = mGlobal.deptInfoMap.get(id);

				int parentId = deptInfo.getParent_dept_id();
				Node parentNode = nodeMap.get(parentId);

				if (parentNode != null) {
					NodeManager.addChildNode(parentNode, curDeptNode);
				}

				int firstChildId = deptInfo.getFirst_child();
				if (firstChildId != -1) {
					// 添加child
					Node firstChildNode = nodeMap.get(firstChildId);
					NodeManager.addChildNode(curDeptNode, firstChildNode);
				}
			}
		}
		// 隐藏部门
		for (Integer did : IMOApp.getApp().hide_dept_ids) {
			if (nodeMap == null)
				break;
			Node node = nodeMap.get(did);
			if (node == null)
				continue;
			node.setNeedShow(false);
			ArrayList<Node> nodes = node.getChildNodes();
			for (Node node1 : nodes) {
				node1.setNeedShow(false);
			}
		}
		// 显示自己
		System.out.println("显示部门列表：" + Arrays.toString(special_hide_dept_ids.toArray()));
		for (Integer did : special_hide_dept_ids) {
			if (nodeMap == null)
				break;
			Node node = nodeMap.get(did);
			if (node == null)
				continue;
			node.setNeedShow(true);
		}

		Node rootNode = nodeMap.get(0);
		sortDeptNode(rootNode);
	}

	private Integer findDidByUid(int uid) {
		return IMOApp.imoStorage.findDidByUid(uid);
	}

	/**
	 * 使用递归实现DeptNode的排序
	 */
	public void sortDeptNode(Node node) {
		// ArrayList<Node> childNodes = node.getChildNodes();
		ArrayList<Node> tempNodes = new ArrayList<Node>();
		// //实现排序
		int deptId = node.getId();
		int firstChildId = mGlobal.deptInfoMap.get(deptId).getFirst_child();
		Node curDeptNode1 = nodeMap.get(firstChildId);
		while (curDeptNode1 != null) {
			tempNodes.add(curDeptNode1);
			int nextId = deptNodeNextSiblingMap.get(curDeptNode1.getId());
			curDeptNode1 = nodeMap.get(nextId);
		}
		node.setChildNodes(tempNodes);

		for (int i = 0; i < tempNodes.size(); i++) {
			Node tempNode = tempNodes.get(i);
			if (NodeManager.isLeaf(tempNode)) {
				continue;
			} else {
				sortDeptNode(tempNode);
			}
		}

	}

	/**
	 * 添加员工到指定的部门
	 */
	private void addUser2Tree() {

		for (int i = 0; i < deptIdList.size(); i++) {

			Node deptNode = nodeMap.get(deptIdList.get(i)); // 部门节点
			deptNode.setId(deptIdList.get(i));

			if (mGlobal.deptUserInfoMap.get(deptIdList.get(i)) != null && mGlobal.deptUserInfoMap.get(deptIdList.get(i)).keySet().size() != 0) {
				addDeptUser(deptNode, mGlobal.deptInfoMap.get(deptIdList.get(i)));// 添加该部门下的所有User
																					// leafNode

				for (int j = 0; j < deptNode.getChildNodes().size(); j++) {
					Node temNode = deptNode.getChildNodes().get(j);
					if (!temNode.isDept()) {

						mGlobal.mNodeMap.put(temNode.getId(), temNode);
					}
				}
			}
		}
	}

	private void addDeptUser(Node deptNode, DeptMaskItem deptMaskItem) {

		int dept_first_userId = deptMaskItem.getFfirst_user();

		if (dept_first_userId != -1) {// ////////

			// userNodeMapItem = new HashMap<Integer, Node>();//////

			EmployeeInfoItem curEmployeeInfo = mGlobal.deptUserInfoMap.get(deptMaskItem.getDept_id()).get(dept_first_userId);

			// build the first user node
			Node curNode = new Node(new NodeData(isBoy(curEmployeeInfo.getGender()), curEmployeeInfo.getName()));
			curNode.setId(curEmployeeInfo.getUid());// ====
			curNode.setOnLineState(mGlobal.userStateMap.get(curEmployeeInfo.getUid()));
			NodeManager.addChildNode(deptNode, curNode);

			// userNodeMapItem.put(curEmployeeInfo.getUid(), curNode);//////

			// 添加单个部门的员工UserInfo Map
			HashMap<Integer, EmployeeInfoItem> userInfoMap = mGlobal.deptUserInfoMap.get(deptMaskItem.getDept_id());

			HashMap<Integer, Integer> deptUserSiblingMapItem = mGlobal.deptUserSiblingMap.get(deptMaskItem.getDept_id());

			// 在该部门中依次添加用户节点
			for (int j = 0; j < mGlobal.deptUserIdsMap.get(deptMaskItem.getDept_id()).length - 1; j++) {
				curEmployeeInfo = addUserNode(deptNode, curEmployeeInfo, userInfoMap, deptUserSiblingMapItem);
				if (curEmployeeInfo == null) {
					break;
				}
			}

			// userNodeMap.put(deptNode.getId(), userNodeMapItem);//////
		}

	}

	/**
	 * @param parentNode
	 *        部门节点
	 * @param curEmployeeInfo
	 *        当前的用户信息
	 * @param deptUserInfoMap
	 *        部门UserInfoMap
	 */
	private EmployeeInfoItem addUserNode(Node parentNode, EmployeeInfoItem curEmployeeInfo, HashMap<Integer, EmployeeInfoItem> deptUserInfoMap, HashMap<Integer, Integer> deptUserSiblingMapItem) {

		curEmployeeInfo.getUid();

		int siblingId = deptUserSiblingMapItem.get(curEmployeeInfo.getUid());

		EmployeeInfoItem employeeInfoItem = null;

		if (siblingId != -1) {

			// LogFactory.d("siblingId", "siblingId= " + siblingId);

			employeeInfoItem = deptUserInfoMap.get(siblingId);// /nullpoint
																// employeeInfoItem
																// = null

			boolean isBoy = isBoy(employeeInfoItem.getGender());

			String nodeName = employeeInfoItem.getName();

			Node leafNode = new Node(new NodeData(isBoy, nodeName));
			if (mGlobal.userStateMap != null) {
				leafNode.setOnLineState(mGlobal.userStateMap.get(employeeInfoItem.getUid()));
			}

			leafNode.setId(employeeInfoItem.getUid());

			NodeManager.addChildNode(parentNode, leafNode);

			// userNodeMapItem.put(curEmployeeInfo.getUid(), leafNode);//////

			this.curNode = leafNode;

		}
		return employeeInfoItem;
	}

	/**
	 * 获得查询结果 Node List
	 * 
	 * @param uidList
	 * @return
	 */
	private ArrayList<Node> getSearchResult(ArrayList<Integer> uidList) {

		Integer uid = null;

		ArrayList<Node> searchResultList = new ArrayList<Node>();

		for (int i = 0; i < uidList.size(); i++) {
			uid = uidList.get(i);

			Node node = mGlobal.mNodeMap.get(uid);

			if (node != null && node.getParentNode() != null && node.getParentNode().isNeedShow()) {
				searchResultList.add(node);
			}
		}

		return searchResultList;
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

			if (mStructNavView.getViewGroupChildCount() == 2) {
				mStructNavView.removeAllItemView();
				adapter.showChildNodes(rootNodeDept);
			} else {
				Node node = mStructNavView.getLastChildNode();
				if (node != null) {
					adapter.showChildNodes(node);
				} else {
					boolean keepOnline = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
							LoginActivity.LOGIN_KEEPONLINE, false
					});
					if (!keepOnline) {
						DialogFactory.promptExit(mContext).show();
					} else {
						IMOApp.getApp().hasRunInBackground = true;
						NoticeManager.updateRecoverAppNotice(notificationManager);
						Functions.backToDesk(this);
					}
				}
			}
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 更新员工状态
	 */
	public void parent_UpdateState() {

		parent_hasRequestCount = 0;
		int allSize = mGlobal.getAllUidArray().length;
		parent_StateRequestCount = (allSize % PARENT_MAX_STATE_REQUEST_COUNT == 0) ? (allSize / PARENT_MAX_STATE_REQUEST_COUNT) : (allSize / PARENT_MAX_STATE_REQUEST_COUNT + 1);

		LogFactory.d("StateCount", "一共请求的次数 = " + parent_StateRequestCount);

		Object[] idArrays = getRequestUserIdArray();

		if (idArrays != null) {
			LogFactory.d("SendUid", Arrays.toString((int[]) idArrays[1]));
			doEmployeeState(((int[]) idArrays[0]).length, ((int[]) idArrays[0]), ((int[]) idArrays[1]));
		}
	}
}

package com.imo.module.organize;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.MainActivityGroup;
import com.imo.module.contact.OuterContactBasicInfo;
import com.imo.module.contact.OuterContactItem;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;
import com.imo.module.organize.struct.NodeManager;
import com.imo.network.Log.ConnectionLog;
import com.imo.network.net.EngineConst;
import com.imo.network.packages.CommonOutPacket;
import com.imo.network.packages.ContactorGroupInPacket;
import com.imo.network.packages.ContactorGroupUCInPacket;
import com.imo.network.packages.ContactorGroupUCOutPacket;
import com.imo.network.packages.CorpMaskItem;
import com.imo.network.packages.DeptMaskItem;
import com.imo.network.packages.EmployeeInfoItem;
import com.imo.network.packages.EmployeeProfileItem;
import com.imo.network.packages.GetAllEmployeesInfoInPacket;
import com.imo.network.packages.GetAllEmployeesInfoOutPacket;
import com.imo.network.packages.GetAllEmployeesUIDInPacket;
import com.imo.network.packages.GetAllEmployeesUIDOutPacket;
import com.imo.network.packages.GetCorpInfoInPacket;
import com.imo.network.packages.GetCorpInfoOutPacket;
import com.imo.network.packages.GetDeptInfoInPacket;
import com.imo.network.packages.GetDeptInfoOutPacket;
import com.imo.network.packages.GetDeptUCInPacket;
import com.imo.network.packages.GetDeptUCOutPacket;
import com.imo.network.packages.GetEmployeeProfileInPacket;
import com.imo.network.packages.GetEmployeeProfileOutPacket;
import com.imo.network.packages.GetEmployeesStatusInPacket;
import com.imo.network.packages.GetEmployeesStatusOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.InnerContactorItem;
import com.imo.network.packages.InnerContactorListInPacket;
import com.imo.network.packages.OuterBasicInfoInPacket;
import com.imo.network.packages.OuterBasicInfoOutPacket;
import com.imo.network.packages.OuterContactorItem;
import com.imo.network.packages.OuterContactorListInPacket;
import com.imo.util.LogFactory;
import com.imo.util.PreferenceManager;

/**
 * 第一次拉取--组织架构数据
 * 
 * @author CaixiaoLong
 * 
 */
public class FirstLoadingActivity extends AbsBaseActivityNetListener {

	private String TAG = "FirstLoading";

	private int mPos = 0;

	private boolean stopLoad = false;

	public static void launch(Context c) {

		Intent intent = new Intent(c, FirstLoadingActivity.class);
		c.startActivity(intent);
	}

	private View loading;
	private TextView tip;
	private Button btn_close;

	// protected void installViews1() {
	//
	// setContentView(R.layout.loading_data_activity);
	//
	// loading = findViewById(R.id.loading);
	// tip = (TextView) findViewById(R.id.tip);
	// tip.setText(R.string.loading);
	// btn_close = (Button) findViewById(R.id.btn_close);
	// btn_close.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// EngineConst.isLoginSuccess = false;
	// IMOApp.getApp().turn2LoginForLogout();
	// }
	// });
	//
	// loading.setBackgroundResource(R.drawable.loading);
	// final AnimationDrawable frameAnimation = (AnimationDrawable)
	// loading.getBackground();
	//
	// loading.post(new Runnable() {
	// @Override
	// public void run() {
	// frameAnimation.start();
	// }
	// });
	// }

	@Override
	protected void installViews() {
		stopLoad = false;
		setContentView(R.layout.loading_data_activity);

		View root = findViewById(R.id.root);
		root.setBackgroundDrawable(new BitmapDrawable(getResources()
				.openRawResource(R.drawable.loading_bg)));

		loading = findViewById(R.id.loading);
		tip = (TextView) findViewById(R.id.tip);
		tip.setText(R.string.loading);
		btn_close = (Button) findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stopLoad = true;
				EngineConst.isLoginSuccess = false;
				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
				IMOApp.getApp().turn2LoginForLogout();
			}
		});

		// loading.setBackgroundResource(R.drawable.loading);
		// final AnimationDrawable frameAnimation = (AnimationDrawable)
		// loading.getBackground();
		//
		// loading.post(new Runnable() {
		// @Override
		// public void run() {
		// frameAnimation.start();
		// }
		// });
	}

	// int index = 0;
	int n = 0;
	private Handler loadingHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 0: {
				Drawable localDrawable = loading.getBackground();
				if (n > 10000) {
					n -= 10000;
				} else {
					n += 160;
				}

				localDrawable.setLevel(n);
				if (!isFinishing()) {
					loadingHandler.sendEmptyMessage(0);
				}
			}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void registerEvents() {

		loadingHandler.sendEmptyMessage(0);

		//初始化内部联系人和外部联系人 map
		resetGroupMap();

		beginLoading();
		
		//beginLoadingContact();
	}

	@Override
	public boolean CanAcceptHttpPacket() {

		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		if (IMOCommand.IMO_GET_DEPT_UC == command
				|| IMOCommand.IMO_GET_DEPT_INFO == command
				|| IMOCommand.IMO_GET_ALL_EMPLOYEE_UID == command
				|| IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO == command
				|| IMOCommand.IMO_GET_EMPLOYEE_STATUS == command
				|| IMOCommand.IMO_UPDATE_STATUS == command
				|| IMOCommand.IMO_INNER_CONTACTOR_GROUP_UC == command
				|| IMOCommand.IMO_INNER_CONTACTOR_LIST_UC == command
				|| IMOCommand.IMO_OUTER_CONTACTOR_GROUP_UC == command
				|| IMOCommand.IMO_OUTER_CONTACTOR_LIST_UC == command
				|| IMOCommand.IMO_INNER_CONTACTOR_GROUP == command
				|| IMOCommand.IMO_INNER_CONTACTOR_LIST == command
				|| IMOCommand.IMO_OUTER_CONTACTOR_GROUP == command
				|| IMOCommand.IMO_OUTER_CONTACTOR_LIST == command
				|| IMOCommand.IMO_OUTER_BASIC_INFO == command
				|| IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO == command
				|| IMOCommand.IMO_GET_EMPLOYEE_PROFILE == command
				|| IMOCommand.IMO_GET_EMPLOYEE_STATUS == command
				|| IMOCommand.IMO_GET_CORP_INFO == command)
			return true;

		return false;
	}

	public Handler mCommandHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			short command = (short) msg.arg1;
			switch (command) {
			// 1-获得部门UC
			case IMOCommand.IMO_GET_DEPT_UC: {
				responseDeptUC(command);
				break;
			}
			// 2-获得部门Info
			case IMOCommand.IMO_GET_DEPT_INFO: {
				responseDeptInfo(command);
				break;
			}
			// 3-获取所有的员工UID
			case IMOCommand.IMO_GET_ALL_EMPLOYEE_UID:
				responseAllEmployeeUid(command);
				break;
			// 4-获取所有的员工基本信息
			case IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO:
				responseAllEmployeeInfo(command);
				break;
			// 5-获取员工的状态
			case IMOCommand.IMO_GET_EMPLOYEE_STATUS:
				if (forOrganize) {
					responseEmployeeState(command);
				} else {
					responseOuterContactState(command);
				}
				break;
			// 6-更新员工自身的状态
			case IMOCommand.IMO_UPDATE_STATUS:
				break;

			case IMOCommand.IMO_INNER_CONTACTOR_GROUP_UC:
				responseInnerGroupUC(command);
				break;
			case IMOCommand.IMO_INNER_CONTACTOR_LIST_UC:
				responseInnerContactUC(command);
				break;
			case IMOCommand.IMO_INNER_CONTACTOR_GROUP:
				responseInnerGroupInfo(command);
				break;
			case IMOCommand.IMO_INNER_CONTACTOR_LIST:
				responseInnerContactId(command);
				break;
			case IMOCommand.IMO_GET_EMPLOYEE_PROFILE:
				responseInnerContactWorkSign(command);
				break;
			case IMOCommand.IMO_OUTER_CONTACTOR_GROUP_UC:
				responseOuterContactGroupUC(command);
				break;
			case IMOCommand.IMO_OUTER_CONTACTOR_LIST_UC:
				responseOuterContactUC(command);
				break;
			case IMOCommand.IMO_OUTER_CONTACTOR_GROUP:
				responseOuterGroupInfo(command);
				break;
			case IMOCommand.IMO_OUTER_CONTACTOR_LIST:
				responseOuterContactId(command);
				break;
			case IMOCommand.IMO_OUTER_BASIC_INFO:
				responseOuterBasicInfo(command);
				break;
			case IMOCommand.IMO_GET_CORP_INFO:
				responseOuterCorpInfo(command);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {

		super.NotifyPacketArrived(aConnectionId, command);
		if (stopLoad)
			return;
		Message msg1 = new Message();
		msg1.arg1 = command;
		mCommandHandler.sendMessage(msg1);
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void NotifyPacketProgress(String aConnectionId, short command,
			short aTotalLen, short aSendedLen) {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Object param) {
		// //加载联系人数据
		beginLoadingContact();

		// dialog.dismiss();
		// MainActivityGroup.launch(mContext);
		// FirstLoadingActivity.this.finish();
	}

	/**
	 * 开始拉取数据
	 */
	private void beginLoading() {

		doRequestDeptUC();
	}

	/**
	 * 状态请求包接受次数
	 */
	private int stateResponseCount = 0;
	private int stateResponseCountEx = 0;

	/**
	 * 更新员工状态
	 */
	private void updateUserState() {
		System.out.println("updateUserState-----");

		stateResponseCount = 0;

		mStateRequestCount = initUserStateRequest(deptUserIdsMap);

		LogFactory.d(TAG + "StateCount", "Total mStateRequestCount = "
				+ mStateRequestCount);

		for (hasConcurrentSended = 0; hasRequestCount < mStateRequestCount
				&& hasConcurrentSended < concurrent_request_status_limit; hasConcurrentSended++) {
			Object[] idArrays = getRequestUserIdArray();
			LogFactory.d("SendUid", Arrays.toString((int[]) idArrays[1]));
			LogFactory.e("hasConcurrentSended", "hasConcurrentSended :"
					+ hasConcurrentSended);
			doRequestEmployeeState(((int[]) idArrays[0]).length,
					((int[]) idArrays[0]), ((int[]) idArrays[1]));
		}
	}

	/**
	 * 状态请求数组ID
	 * 
	 * @return
	 */
	private Object[] getRequestUserIdArray() {

		int[] requestCIds = null;
		int[] requestUIds = null;

		if (mStateRequestCount == 0) {
			return null;
		}

		if (mStateRequestCount - 1 > hasRequestCount) {

			requestCIds = new int[MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[MAX_STATE_REQUEST_COUNT];

			// LogFactory.d(TAG+"State--HasRequestCount","hasRequestCount = " +
			// hasRequestCount );

			requestIds = new int[MAX_STATE_REQUEST_COUNT];

			System.arraycopy(mAllCIds, hasRequestCount
					* MAX_STATE_REQUEST_COUNT, requestCIds, 0,
					MAX_STATE_REQUEST_COUNT);
			System.arraycopy(mAllUserIds, hasRequestCount
					* MAX_STATE_REQUEST_COUNT, requestUIds, 0,
					MAX_STATE_REQUEST_COUNT);

			this.hasRequestCount++;

			LogFactory.d(TAG + "State--HasRequestCount", "hasRequestCount = "
					+ hasRequestCount);

		} else {
			LogFactory.d(TAG + "StateHasRequestCount",
					" State has Request Completed !! hasRequestCount = "
							+ hasRequestCount);

			requestCIds = new int[mAllUserIds.length - hasRequestCount
					* MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[mAllUserIds.length - hasRequestCount
					* MAX_STATE_REQUEST_COUNT];

			System.arraycopy(mAllCIds, hasRequestCount
					* MAX_STATE_REQUEST_COUNT, requestCIds, 0,
					requestUIds.length);
			System.arraycopy(mAllUserIds, hasRequestCount
					* MAX_STATE_REQUEST_COUNT, requestUIds, 0,
					requestUIds.length);

			this.hasRequestCount++;

		}

		return new Object[] { requestCIds, requestUIds };
	}

	// ====================================================
	/**
	 * 1-发送DeptUC请求包
	 */
	private void doRequestDeptUC() {

		GetDeptUCOutPacket outPacket = new GetDeptUCOutPacket(
				ByteBuffer.allocate(0), IMOCommand.IMO_GET_DEPT_UC,
				EngineConst.cId, EngineConst.uId);

		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
			IMOApp.getDataEngine().addToObserverList(this);
		} else {
			finish();
		}
	}

	// ====================================================
	/**
	 * 2-发送DeptInfo请求包
	 */
	private void doRequestDeptInfo(int deptid, int dept_uc) {
		// 请求DeptInfo
		// LogFactory.d(TAG, "doRequestDeptInfo :[ deptid = "+ deptid
		// +"\t dept_uc = " + dept_uc +"]");

		int mask = 1;
		mask |= (mask << 1);
		mask |= (mask << 2);
		mask |= (mask << 3);
		mask |= (mask << 4);
		mask |= (mask << 5);
		mask |= (mask << 6);
		mask |= (mask << 7);
		mask |= (mask << 8);
		mask |= (mask << 9);
		mask |= (mask << 10);
		mask |= (mask << 11);
		mask |= (mask << 12);
		mask |= (mask << 13);

		ByteBuffer bufferBody = GetDeptInfoOutPacket.GenerateDeptInfoBody(
				deptid, dept_uc, mask);

		GetDeptInfoOutPacket outPacket = new GetDeptInfoOutPacket(bufferBody,
				IMOCommand.IMO_GET_DEPT_INFO, EngineConst.cId, EngineConst.uId);
		int header_seq = outPacket.get_header_seq();
		dept_loadinfo_pack_map.put(header_seq, LOAD_STAT.LOADING);
		LogFactory.d(TAG, "doRequestDeptInfo :[ deptid = " + deptid
				+ "\t dept_uc = " + dept_uc + "]" + ", seq=" + header_seq);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);

	}

	// ====================================================
	/**
	 * 3-发送AllEmployeeUid请求包： 获得部门下的所有员工的UId
	 * 
	 * @param aDeptID
	 *            部门
	 */
	private void doRequestAllEmployeeUid(int aDeptID) {

		// LogFactory.d(TAG, "doRequestAllEmployeeUid  deptId = " + aDeptID);
		ByteBuffer bufferBody = GetAllEmployeesUIDOutPacket
				.GenerateEmplyeesUIDBody(aDeptID);
		GetAllEmployeesUIDOutPacket outPacket = new GetAllEmployeesUIDOutPacket(
				bufferBody, IMOCommand.IMO_GET_ALL_EMPLOYEE_UID,
				EngineConst.cId, EngineConst.uId);

		int header_seq = outPacket.get_header_seq();
		dept_loaduids_pack_map.put(header_seq, LOAD_STAT.LOADING);
		LogFactory.d(TAG, "doRequestAllEmployeeUid  deptId = " + aDeptID
				+ ", seq=" + header_seq);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	// ====================================================

	private boolean isCompletedSingleDept = false; // 单个部门是否已经请求完成

	private int hasRequestCount = 0; // 需要请求的次数

	private int hasConcurrentSended = 0;

	private int[] requestIds; // 每次请求的Id数组

	private int USERINFO_REQUEST_MAX_COUNT = 20; // 员工信息，单个请求最大的个数

	/**
	 * 4-发送AllEmployeeInfo请求包,
	 * 
	 * 登陆后取联系人列表用,一次最多取20个联系人
	 * 
	 * @param aContactorsNum
	 *            联系人的个数
	 * @param aContactorUidArray
	 *            联系人的Uid数组
	 */
	private void doRequestAllEmployeeInfo(int aContactorsNum,
			int[] aContactorUidArray) {

		LogFactory.d(
				TAG + "2","doRequestAllEmployeeInfo : uids = "
						+ Arrays.toString(aContactorUidArray));

		ByteBuffer bufferBody = GetAllEmployeesInfoOutPacket
				.GenerateEmployeesBasicInfoBody(aContactorsNum,aContactorUidArray);

		LogFactory.d(TAG + "2", "bufferBody = " + bufferBody.toString());

		GetAllEmployeesInfoOutPacket outPacket = new GetAllEmployeesInfoOutPacket(
				bufferBody, IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO,
				EngineConst.cId, EngineConst.uId);
		int header_seq = outPacket.get_header_seq();
		dept_loademployeeinfo_pack_map.put(header_seq, LOAD_STAT.LOADING);
		LogFactory.d(TAG, "doRequestAllEmployeeInfo :[ aContactorsNum = "
				+ aContactorsNum + ", aContactorUidArray = "
				+ aContactorUidArray + "]" + ", seq=" + header_seq);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	// ====================================================
	private boolean forOrganize = true;

	/**
	 * 状态请求的总次数
	 */
	private int mStateRequestCount = 0;

	/**
	 * 5-发送AllEmployeeInfo请求包
	 * 
	 * @param aContactorsNum
	 *            请求的员工数量 个数
	 * @param aContactorCidArray
	 *            内部联系人 、外部联系人 CidArray
	 * @param aContactorUidArray
	 *            用户 UIdArray
	 * 
	 */
	private void doRequestEmployeeState(int aContactorsNum,
			int[] aContactorCidArray, int[] aContactorUidArray) {

		LogFactory.d(TAG, "doRequestEmployeeState ");

		ByteBuffer bufferBody = GetEmployeesStatusOutPacket
				.GenerateEmployeesStatusBody(aContactorsNum,
						aContactorCidArray, aContactorUidArray);
		GetEmployeesStatusOutPacket outPacket = new GetEmployeesStatusOutPacket(
				bufferBody, IMOCommand.IMO_GET_EMPLOYEE_STATUS,
				EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * 发送当前用户状态 请求包
	 */
	private void doRequestOwnState() {

		LogFactory.d(TAG, "doRequestOwnState ");
		// 修改command
		CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0),
				IMOCommand.IMO_UPDATE_STATUS, EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * 将数据保存到数据库中去 <br>
	 * 更新全局数据 添加到数据库 记录状态 更新自己的状态【需要调整位置】 更新员工的的状态
	 * 
	 * @throws Exception
	 */
	private void doSaveData2DB() {
		mGlobal.updateData(
				(int[]) deptid.clone(),
				(int[]) dept_uc.clone(),
				(int[]) dept_user_uc.clone(),
				(HashMap<Integer, DeptMaskItem>) deptInfoMap.clone(),
				(HashMap<Integer, int[]>) deptUserIdsMap.clone(),
				(HashMap<Integer, int[]>) deptUserNextSiblingMap.clone(),
				(HashMap<Integer, HashMap<Integer, Integer>>) deptUserSiblingMap
						.clone(),
				(HashMap<Integer, HashMap<Integer, EmployeeInfoItem>>) deptUserInfoMap
						.clone());
		new SaveDataThread().start();// /存数据库

		// try {
		//
		// mGlobal.updateData(
		// (int[])deptid.clone(), (int[])dept_uc.clone(),
		// (int[])dept_user_uc.clone(),
		// (HashMap<Integer, DeptMaskItem>)deptInfoMap.clone(),
		// (HashMap<Integer, int[]>)deptUserIdsMap.clone(), (HashMap<Integer,
		// int[]>)deptUserNextSiblingMap.clone(),
		// (HashMap<Integer, HashMap<Integer,
		// Integer>>)deptUserSiblingMap.clone(),
		// (HashMap<Integer,
		// HashMap<Integer,EmployeeInfoItem>>)deptUserInfoMap.clone());
		//
		// IMOApp.imoStorage.add(
		// deptid, dept_uc, dept_user_uc,
		// deptInfoMap,
		// deptUserIdsMap, deptUserNextSiblingMap,
		// deptUserInfoMap);
		//
		// PreferenceManager.save("IMO-DATA"+EngineConst.uId, new String[] {
		// "isFirst","No" });
		//
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }

		// doRequestOwnState();
		LogFactory.e("updateUserState starttime :", "" + System.currentTimeMillis());
		updateUserState();

		// sendMessage(null);
	}

	// ===============部门UC表对应 字段===============================
	private int[] deptid = null;
	private int[] dept_uc = null;
	private int[] dept_user_uc = null;

	private Vector<Integer> deptid_vector = new Vector<Integer>();
	private Vector<Integer> dept_uc_vector = new Vector<Integer>();
	private Vector<Integer> dept_user_uc_vector = new Vector<Integer>();

	// 下面变量用于控制并发包的并发数量
	private int concurrent_deptinfo_loadreq_limit = EngineConst.CONCURRENT_MAX_VALUE;
	private int concurrent_deptinfo_loading_cnt = 0;
	private int concurrent_deptuids_loadreq_limit = EngineConst.CONCURRENT_MAX_VALUE;
	private int concurrent_deptuids_loading_cnt = 0;
	private int concurrent_dept_employeeinfo_loadreq_limit = EngineConst.CONCURRENT_MAX_VALUE;
	private int concurrent_dept_employeeinfo_loading_cnt = 0;

	private int concurrent_request_status_limit = EngineConst.CONCURRENT_MAX_VALUE;

	// TODO: Review info: add for concurrent data load --reviewed by davidfan
	// 2012-may-24
	// dept_loadinfo_map 标识当前所有deptid的deptinfo的获取情况，有未载入，载入中，已载入3个状态
	private Map<Integer, Integer> dept_loadinfo_map = new HashMap<Integer, Integer>();
	// dept_loadinfo_map 标识当前所有deptid的uids成员列表的获取情况，有未载入，载入中，已载入3个状态
	private Map<Integer, Integer> dept_loaduids_map = new HashMap<Integer, Integer>();

	// 缓存一个部门的uids，nextsibling，员工信息
	private Map<Integer, int[]> dept_uids_map = new HashMap<Integer, int[]>();
	private Map<Integer, int[]> dept_nextSibling_map = new HashMap<Integer, int[]>();
	private Map<Integer, EmployeeInfoItem[]> dept_EmployeeInfo_map = new HashMap<Integer, EmployeeInfoItem[]>();

	// 存储dept uc值
	private Map<Integer, Integer> dept_uc_map = new HashMap<Integer, Integer>();
	// 存储dept user uc值
	private Map<Integer, Integer> dept_user_uc_map = new HashMap<Integer, Integer>();
	// 标示dept_loadinfo请求包的情况，如果收到endflag，则为LOADED，否则为LOADING
	private Map<Integer, Integer> dept_loadinfo_pack_map = new HashMap<Integer, Integer>();
	// 标示dept_loaduids请求包的情况，如果收到endflag，则为LOADED，否则为LOADING
	private Map<Integer, Integer> dept_loaduids_pack_map = new HashMap<Integer, Integer>();
	// 标示dept_employeeinfo请求包的情况，如果收到endflag，则为LOADED，否则为LOADING
	private Map<Integer, Integer> dept_loademployeeinfo_pack_map = new HashMap<Integer, Integer>();
	// 标示每一个uid的info的请求的情况，则为LOADED，否则为LOADING
	private Map<Integer, Integer> dept_uid_loademployeeinfo_map = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> uid_dept_map = new HashMap<Integer, Integer>();

	// 上面几个map可以合并，不熟悉java写法，以后再优化

	private class LOAD_STAT {
		static final int NOT_LOAD = 0;
		static final int LOADING = 1;
		static final int LOADED = 2;
		static final int LOAD_FAIL = 3;
	}

	int dept_uc_load = LOAD_STAT.NOT_LOAD;
	int dept_info_load = LOAD_STAT.NOT_LOAD;
	int dept_uids_load = LOAD_STAT.NOT_LOAD;

	int employee_info_load = LOAD_STAT.NOT_LOAD;

	/**
	 * <1>响应 部门UC 数据
	 * 
	 * @param command
	 */
	private void responseDeptUC(short command) {

		GetDeptUCInPacket inPacket = (GetDeptUCInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (inPacket == null)
			return;
		short commandRet = inPacket.getCommandRet();

		LogFactory.d(TAG, "commandRet = " + commandRet);

		byte endflag = inPacket.getEndflag();

		if (commandRet == 0) {

			int[] temp_deptid = inPacket.getDeptid();
			int[] temp_dept_uc = inPacket.getDept_uc();
			int[] temp_dept_user_uc = inPacket.getDept_user_uc();

			for (int i = 0; i < temp_deptid.length; i++) {
				deptid_vector.add(new Integer(temp_deptid[i]));
			}

			for (int i = 0; i < temp_dept_uc.length; i++) {
				dept_uc_vector.add(new Integer(temp_dept_uc[i]));
			}

			for (int i = 0; i < temp_dept_user_uc.length; i++) {
				dept_user_uc_vector.add(new Integer(temp_dept_user_uc[i]));
			}
		}

		if (endflag == 1) {
			deptid = new int[deptid_vector.size()];
			dept_uc = new int[dept_uc_vector.size()];
			dept_user_uc = new int[dept_user_uc_vector.size()];

			for (int i = 0; i < deptid.length; i++) {
				deptid[i] = deptid_vector.elementAt(i);
			}
			for (int i = 0; i < dept_uc.length; i++) {
				dept_uc[i] = dept_uc_vector.elementAt(i);
			}
			for (int i = 0; i < dept_user_uc.length; i++) {
				dept_user_uc[i] = dept_user_uc_vector.elementAt(i);
			}

			// 此处已经取出所有dept以及其对应的uc，下面需要并发发起loadinfo，loaduids请求
			dept_uc_load = LOAD_STAT.LOADED;
			LogFactory.d(TAG, "------> DeptUC取出成功 \t  TotalDeptCount="
					+ deptid.length);
			LogFactory.d(TAG, "------> deptid=" + Arrays.toString(deptid));
			LogFactory.d(TAG, "------> dept_uc=" + Arrays.toString(dept_uc));
			LogFactory.d(TAG,
					"------> dept_user_uc=" + Arrays.toString(dept_user_uc));
			if (deptid.length == 0) {
				// // 组织结构中没有部门存在，没有任何数据
				LogFactory.d(TAG, "------> !!!!!组织结构中没有部门存在，没有任何数据!!!!!");
				return;
			}

			// 初始化各数据map
			for (int i = 0; i < this.deptid.length; i++) {
				dept_uc_map.put(this.deptid[i], this.dept_uc[i]);
				dept_user_uc_map.put(this.deptid[i], this.dept_user_uc[i]);
				dept_loadinfo_map.put(this.deptid[i], LOAD_STAT.NOT_LOAD);
				dept_loaduids_map.put(this.deptid[i], LOAD_STAT.NOT_LOAD);
				LogFactory.d(TAG,
						"------> dept_uc_map size=" + dept_uc_map.size()
								+ ", dept_loadinfo_map size="
								+ dept_loadinfo_map.size()
								+ ", dept_loaduids_map size="
								+ dept_loaduids_map.size());
			}

			// 开始并发请求deptinfo
			do_concurrent_dept_info_req();
			// 开始并发请求deptuids
			do_concurrent_dept_uids_req();
		}
	}

	/******************************************************************************
	 * FirstLoadingActivity.do_concurrent_dept_info_req - 发起多个deptinfo
	 * get请求，并返回发起请求的个数 DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int do_concurrent_dept_info_req() {
		// 开始并发请求deptinfo
		int init_cnt = concurrent_deptinfo_loading_cnt;
		for (int i = 0; i < this.deptid.length
				&& concurrent_deptinfo_loading_cnt < concurrent_deptinfo_loadreq_limit; i++) {
			if (dept_loadinfo_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD) {
				doRequestDeptInfo(this.deptid[i],
						dept_uc_map.get(this.deptid[i]));
				dept_loadinfo_map.put(this.deptid[i], LOAD_STAT.LOADING);
				concurrent_deptinfo_loading_cnt++;
			} else {
			}
		}

		return concurrent_deptinfo_loading_cnt - init_cnt;
	}

	/******************************************************************************
	 * FirstLoadingActivity.do_concurrent_dept_uids_req - 发起多个deptuids
	 * get请求，并返回发起请求的个数 DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int do_concurrent_dept_uids_req() {
		// 开始并发请求deptuids
		int init_cnt = concurrent_deptuids_loading_cnt;
		for (int i = 0; i < this.deptid.length
				&& concurrent_deptuids_loading_cnt < concurrent_deptuids_loadreq_limit; i++) {
			if (dept_loaduids_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD) {
				doRequestAllEmployeeUid(this.deptid[i]);
				dept_loaduids_map.put(this.deptid[i], LOAD_STAT.LOADING);
				concurrent_deptuids_loading_cnt++;

				// LogFactory.d(TAG,
				// "------> do_concurrent_dept_uids_req do get deptuids req in responseDeptUC deptid:"+this.deptid[i]+",init_cnt:"
				// +init_cnt+", endcnt:"+concurrent_deptuids_loading_cnt);
			} else {
				// LogFactory.d(TAG,
				// "------> do_concurrent_dept_uids_req not do get deptuids req in responseDeptUC deptid:"+this.deptid[i]+",init_cnt:"
				// +init_cnt+", endcnt:"+concurrent_deptuids_loading_cnt);
			}
		}

		return concurrent_deptuids_loading_cnt - init_cnt;
	}

	/******************************************************************************
	 * FirstLoadingActivity.do_concurrent_dept_emyployeeinfo_req - 发起多个dept
	 * employee get请求，并返回发起请求的个数 DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int do_concurrent_dept_emyployeeinfo_req(int deptid) {
		if (null == deptUserIdsMap.get(deptid)) {
			LogFactory
					.d(TAG,
							"------> do_concurrent_dept_emyployeeinfo_req fail deptUserIdsMap get(deptid) null deptid:"
									+ deptid
									+ ", loadingcnt:"
									+ concurrent_dept_employeeinfo_loading_cnt);
			return 0;
		}

		int req_cnt = 0;
		int req_num = 0;
		int[] req_uids = new int[50];
		int[] deptuids = deptUserIdsMap.get(deptid);
		for (int i = 0; i < deptuids.length; i++) {
			if (null == dept_uid_loademployeeinfo_map.get(deptuids[i])) {
				req_uids[req_num++] = deptuids[i];
				dept_uid_loademployeeinfo_map.put(deptuids[i],
						LOAD_STAT.LOADING);
				uid_dept_map.put(deptuids[i], deptid);
			}

			if (req_num == USERINFO_REQUEST_MAX_COUNT) {
				doRequestAllEmployeeInfo(req_num, req_uids);
				req_cnt++;
				req_num = 0;
			}
		}

		if (req_num != 0) {
			doRequestAllEmployeeInfo(req_num, req_uids);
			req_cnt++;
		}
		concurrent_dept_employeeinfo_loading_cnt += req_cnt;
		LogFactory.d(TAG,
				"------> do_concurrent_dept_emyployeeinfo_req deptid:" + deptid
						+ "," + ", req_cnt:" + req_cnt + ", loading_cnt:"
						+ concurrent_dept_employeeinfo_loading_cnt);
		return req_cnt;
	}

	/******************************************************************************
	 * FirstLoadingActivity.check_dept_info_req_finish - 检查是否所有的deptinfo请求均完成
	 * DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int check_dept_info_req_finish() {
		// 开始并发请求deptuids
		int init_cnt = concurrent_deptinfo_loading_cnt;
		for (int i = 0; i < this.deptid.length
				&& concurrent_deptinfo_loading_cnt < concurrent_deptinfo_loadreq_limit; i++) {
			if (dept_loadinfo_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD
					|| dept_loadinfo_map.get(this.deptid[i]) == LOAD_STAT.LOADING) {

				// LogFactory.e("check_dept_info_req_finish",
				// "init_cnt :"+init_cnt+",deptid :"+deptid[i]+",dept_loadinfo_map keys:"+dept_loadinfo_map.keySet()+",dept_loadinfo_map values:"+dept_loadinfo_map.values());
				return 0;
			}
		}

		// 所有deptinfo请求均已完成，此处请求请求中使用到的各个map
		dept_info_load = LOAD_STAT.LOADED;
		dept_loadinfo_map.clear();
		dept_loadinfo_pack_map.clear();

		LogFactory.e("check_dept_info_req_finish", "over");

		// 开始存储数据到DB中去
		if (dept_info_load == LOAD_STAT.LOADED
				&& employee_info_load == LOAD_STAT.LOADED)
			doSaveData2DB();

		return 1;
	}

	/******************************************************************************
	 * FirstLoadingActivity.check_dept_uids_req_finish - 检查是否所有depot uids请求均已完成
	 * DESCRIPTION: - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	private int check_dept_uids_req_finish() {
		// 开始并发请求deptuids
		int init_cnt = concurrent_deptuids_loading_cnt;
		for (int i = 0; i < this.deptid.length
				&& concurrent_deptuids_loading_cnt < concurrent_deptuids_loadreq_limit; i++) {

			if (dept_loaduids_map.get(this.deptid[i]) == LOAD_STAT.NOT_LOAD
					|| dept_loaduids_map.get(this.deptid[i]) == LOAD_STAT.LOADING) {
				// LogFactory.e("check_dept_uids_req_finish",
				// "init_cnt :"+init_cnt+",deptid :"+deptid[i]+
				// ",dept_loaduids_map keys:"+dept_loaduids_map.keySet()+",dept_loaduids_map values:"
				// +dept_loaduids_map.values());

				return 0;
			}
		}

		// 所有deptinfo请求均已完成，此处请求请求中使用到的各个map
		dept_uids_load = LOAD_STAT.LOADED;
		dept_loaduids_map.clear();
		dept_loaduids_pack_map.clear();

		LogFactory.e("check_dept_uids_req_finish", "over");

		return 1;
	}

	private int check_employeeinfo_req_finish() {
		if (concurrent_dept_employeeinfo_loading_cnt == 0) {
			LogFactory.e("check_employeeinfo_req_finish", "over");
			return 1;
		}
		return 0;
	}

	// ===============部门Info表对应 字段===============================

	private HashMap<Integer, DeptMaskItem> deptInfoMap = new HashMap<Integer, DeptMaskItem>();

	/**
	 * 响应 部门Info 数据
	 * 
	 * @param command
	 */
	private void responseDeptInfo(short command) {

		GetDeptInfoInPacket inPacket = (GetDeptInfoInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (inPacket == null)
			return;

		DeptMaskItem mDeptMaskItem = inPacket.getMaskItem();

		// deptInfoMap.put(this.deptid[mPos], mDeptMaskItem);
		deptInfoMap.put(inPacket.getDeptid(), mDeptMaskItem);
		int seq = inPacket.getSequence();
		dept_loadinfo_pack_map.put(seq, LOAD_STAT.LOADED);
		concurrent_deptinfo_loading_cnt--;

		LogFactory.d(TAG, "DeptInfo Loaded Deptid=" + inPacket.getDeptid()
				+ " mDeptMaskItem =" + mDeptMaskItem.toString()
				+ ", concurrent_deptinfo_loading_cnt:"
				+ concurrent_deptinfo_loading_cnt + "seq:" + seq);
		ConnectionLog.MusicLogInstance().addLog("FirstLoadingActivity mDeptMaskItem =" + mDeptMaskItem.toString());

		dept_loadinfo_map.put(inPacket.getDeptid(), LOAD_STAT.LOADED);
		int send_cnt = do_concurrent_dept_info_req();

		if (send_cnt == 0) {
			// 如果发出的请求为0，则代表所有的deptid都已经发出，此时可以检查deptinfo请求是否已经全部完成
			check_dept_info_req_finish();
		}

	}

	// ===============部门员工Uid表对应 字段===============================

	int[] uid = null;

	int[] nextSibling = null;

	/**
	 * 部门下的所有员工Id Map映射
	 */
	private HashMap<Integer, int[]> deptUserIdsMap = new HashMap<Integer, int[]>();

	private HashMap<Integer, int[]> deptUserNextSiblingMap = new HashMap<Integer, int[]>();

	/**
	 * 部门请求用户次数的Map映射
	 */
	private HashMap<Integer, Integer> deptUserInfoRequestCountMap = new HashMap<Integer, Integer>();

	/**
	 * 部门员工SiblingId
	 */
	private HashMap<Integer, HashMap<Integer, Integer>> deptUserSiblingMap = new HashMap<Integer, HashMap<Integer, Integer>>();

	/**
	 * 响应部门的员工Id
	 * 
	 * @param command
	 */
	private void responseAllEmployeeUid(short command) {

		GetAllEmployeesUIDInPacket inPacket = (GetAllEmployeesUIDInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (inPacket == null)
			return;
		int seq = inPacket.getSequence();

		short commandRet = inPacket.getCommandRet();

		int deptid = inPacket.getDeptid();

		byte endflag = inPacket.getEndflag();
		LogFactory.d(TAG, "Deptuids Loaded Deptid=" + inPacket.getDeptid()
				+ ", concurrent_deptuids_loading_cnt:"
				+ concurrent_deptuids_loading_cnt + "seq:" + seq
				+ "commandRet:" + commandRet + "endflag:" + endflag);

		if (commandRet == 0) {

			// 需要将本次请求到的数据缓存到dept_uids_map，dept_nextSibling_map里面，这里请东风写下，不太会Java
			// 容器的语法

			int[] temp_uids = inPacket.getUid();
			int[] temp_nextSibling = inPacket.getNextSibling();

			if (dept_uids_map.containsKey(deptid)) {
				int[] dept_uids_array = dept_uids_map.get(deptid);
				dept_uids_array = mergeIntArray(dept_uids_array, temp_uids);
				dept_uids_map.put(deptid, dept_uids_array);
			} else {
				dept_uids_map.put(deptid, temp_uids);
			}

			if (dept_nextSibling_map.containsKey(deptid)) {
				int[] dept_nextSibling_array = dept_nextSibling_map.get(deptid);
				dept_nextSibling_array = mergeIntArray(dept_nextSibling_array,
						temp_nextSibling);
				dept_nextSibling_map.put(deptid, dept_nextSibling_array);
			} else {
				dept_nextSibling_map.put(deptid, temp_nextSibling);
			}

			LogFactory.e(
					TAG,
					"responseAllEmployeeUid deptid :" + deptid
							+ ",dept_nextSibling_map size:"
							+ dept_nextSibling_map.get(deptid).length
							+ ",dept_uids_map size :"
							+ dept_uids_map.get(deptid).length);

			/*
			 * if (uid == null) {
			 * 
			 * //for the first time uid = temp_uids; nextSibling =
			 * temp_nextSibling;
			 * 
			 * }else {
			 * 
			 * // for the next time uid = mergeIntArray(uid, temp_uids);
			 * nextSibling = mergeIntArray(nextSibling, temp_nextSibling); }
			 */
		} else // 请求失败，需要进行清理
		{
			concurrent_deptuids_loading_cnt--;
			dept_loaduids_map.put(inPacket.getDeptid(), LOAD_STAT.LOAD_FAIL);
			dept_loaduids_pack_map.put(seq, LOAD_STAT.LOAD_FAIL);
		}

		// 控制需要请求的次数
		if (endflag == 1) {

			LogFactory.d(TAG, "------> Dept下的  UserId 拉取成功 \t  deptid="
					+ deptid);

			// 一个deptid的uids请求完毕，结束该请求的seq监控，并将并发请求--
			concurrent_deptuids_loading_cnt--;
			dept_loaduids_pack_map.put(seq, LOAD_STAT.LOADED);
			dept_loaduids_map.put(deptid, LOAD_STAT.LOADED);
			// 从缓存map中取出数据
			int[] tmp_uids = dept_uids_map.get(deptid);
			int[] tmp_nextSiblings = dept_nextSibling_map.get(deptid);

			if (!(tmp_uids == null || tmp_uids.length == 0)) {
				// ===============================================下面code将缓存数据写入DB
				HashMap<Integer, Integer> deptUserSiblingMapItem = new HashMap<Integer, Integer>();
				for (int i = 0; i < tmp_nextSiblings.length; i++) {
					deptUserSiblingMapItem
							.put(tmp_uids[i], tmp_nextSiblings[i]);
				}
				deptUserSiblingMap.put(deptid, deptUserSiblingMapItem);
				// ===============================================
				int deptUserCount = tmp_uids.length;

				// 存放deptId 和 该部门下的userId的映射
				deptUserIdsMap.put(deptid, tmp_uids);
				deptUserNextSiblingMap.put(deptid, tmp_nextSiblings);

				// 请求总次数的映射
				deptUserInfoRequestCountMap.put(deptid, deptUserCount
						% USERINFO_REQUEST_MAX_COUNT == 0 ? deptUserCount
						/ USERINFO_REQUEST_MAX_COUNT : deptUserCount
						/ USERINFO_REQUEST_MAX_COUNT + 1);

				// LogFactory.d(TAG+"1", " deptUserCount =" + deptUserCount +
				// "  deptId = " + this.deptid[mPos] );
				// LogFactory.d(TAG+"1", " deptUser needRequestTimes = " +
				// deptUserInfoRequestCountMap.get(this.deptid[mPos]));
				LogFactory.d(
						TAG + "1",
						"deptUserIdsMap size:" + deptUserIdsMap.size()
								+ " deptAllEmployeeUid="
								+ Arrays.toString(tmp_uids));
				// LogFactory.d(TAG+"1", " deptAllEmployee- nextSibling Uid=" +
				// Arrays.toString(nextSibling));

				// 当前部门的所有uid已经完全取到，开始取uid的info
				do_concurrent_dept_emyployeeinfo_req(deptid);

			}

			int send_cnt = do_concurrent_dept_uids_req();
			if (send_cnt == 0) {
				// 如果发出的请求为0，则代表所有的deptid都已经发出，此时可以检查deptinfo请求是否已经全部完成
				int finish = check_dept_uids_req_finish();

				// 获取员工信息请求暂不改为并发
				// 获取员工信息请求暂不改为并发
				if (finish != 0) {
					LogFactory.d(TAG, " all dept uids get done");
				}
			}
		}
		return;
	}

	/**
	 * 获得当前用户信息请求的数组
	 * 
	 * @param srcIds
	 * @param totalRequestCount
	 * @param hasRequestCount
	 * @return
	 */
	private int[] getRequestDeptUserIds(int[] srcIds, int totalRequestCount,
			int hasRequestCount) {

		int[] requestIds = null;

		if (totalRequestCount - 1 > hasRequestCount) {

			this.hasRequestCount++;

			LogFactory.d(TAG + "hasRequestCount", "hasRequestCount = "
					+ hasRequestCount + " \t  deptid = " + this.deptid[mPos]);

			requestIds = new int[USERINFO_REQUEST_MAX_COUNT];

			System.arraycopy(srcIds, hasRequestCount
					* USERINFO_REQUEST_MAX_COUNT, requestIds, 0,
					USERINFO_REQUEST_MAX_COUNT);

			isCompletedSingleDept = false;

		} else {

			requestIds = new int[srcIds.length - hasRequestCount
					* USERINFO_REQUEST_MAX_COUNT];

			System.arraycopy(srcIds, hasRequestCount
					* USERINFO_REQUEST_MAX_COUNT, requestIds, 0, srcIds.length
					- hasRequestCount * USERINFO_REQUEST_MAX_COUNT);

			isCompletedSingleDept = true;

			// 请求完成后，需要重置
			this.hasRequestCount = 0;
		}

		return requestIds;
	}

	// ===============部门员工Info表对应 字段==============================endflag

	private EmployeeInfoItem[] employeeInfoArray = null;

	/**
	 * 部门下的所有用户信息Map -------------------- 需要多次的请求，叠加数据
	 */
	private HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap = new HashMap<Integer, HashMap<Integer, EmployeeInfoItem>>();

	/**
	 * 响应部门的员工基本信息
	 * 
	 * @param command
	 */
	private void responseAllEmployeeInfo(short command) {

		LogFactory.d(TAG + "2","----------responseAllEmployeeInfo -----------deptID = "
						+ this.deptid[mPos]);

		GetAllEmployeesInfoInPacket inPacket = (GetAllEmployeesInfoInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (inPacket == null)
			return;
		int seq = inPacket.getSequence();

		HashMap<Integer, EmployeeInfoItem> deptUserInfoMapItem = null;

		short commandRet = inPacket.getCommandRet();
		int deptid = 0;
		if (commandRet != 0) {
			concurrent_dept_employeeinfo_loading_cnt--;
		} else {
			byte endflag = inPacket.getEndflag();
			EmployeeInfoItem[] temp_employeeInfoArray = inPacket.getEmployeesInfoArray();
			if (null == temp_employeeInfoArray
					|| temp_employeeInfoArray.length == 0) {
				LogFactory.d(TAG,
						"responseAllEmployeeInfo------> no data return for seq =" + seq);
				// int[] deptUidArray = deptUserIdsMap.get(this.deptid[mPos]);
			} else {
				deptid = uid_dept_map.get(temp_employeeInfoArray[0].getUid());
				EmployeeInfoItem[] cache_employeeInfoArray = dept_EmployeeInfo_map
						.get(deptid);
				if (null == cache_employeeInfoArray) {
					dept_EmployeeInfo_map.put(deptid, temp_employeeInfoArray);
				} else {
					cache_employeeInfoArray = mergeUserArray(
							cache_employeeInfoArray, temp_employeeInfoArray);
					dept_EmployeeInfo_map.put(deptid, cache_employeeInfoArray);
				}
			}

			// 一个完整的请求返回，需要保存数据，检查是否所有请求均完成
			if (endflag == 1) {
				concurrent_dept_employeeinfo_loading_cnt--;
				LogFactory.d(TAG,
						"------> 一次Dept下的  EmployeeInfo请求成功 \t  deptId ="
								+ deptid);
			}
		}

		int finish = check_employeeinfo_req_finish();
		if (finish != 0) {
			// 从缓存读出数组数据转换为map存入deptUserInfoMap中
			Iterator<Map.Entry<Integer, EmployeeInfoItem[]>> it = dept_EmployeeInfo_map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, EmployeeInfoItem[]> entry = (Map.Entry<Integer, EmployeeInfoItem[]>) it
						.next();
				int cur_deptid = (Integer) entry.getKey();
				EmployeeInfoItem[] cache_employeeInfoArray = (EmployeeInfoItem[]) entry
						.getValue();
				if (deptUserInfoMap.get(cur_deptid) != null) {
					// 同一个部门的UserId-Info-Map
					deptUserInfoMapItem = deptUserInfoMap.get(cur_deptid);

					for (int i = 0; i < cache_employeeInfoArray.length; i++) {
						// deptUserInfoMapItem.put(deptUidArray[i],employeeInfoArray[i]);
						deptUserInfoMapItem.put(
								cache_employeeInfoArray[i].getUid(),
								cache_employeeInfoArray[i]);
					}

					deptUserInfoMap.put(cur_deptid, deptUserInfoMapItem);

				} else {
					deptUserInfoMapItem = new HashMap<Integer, EmployeeInfoItem>();

					for (int i = 0; i < cache_employeeInfoArray.length; i++) {

						deptUserInfoMapItem.put(
								cache_employeeInfoArray[i].getUid(),
								cache_employeeInfoArray[i]);
					}

					deptUserInfoMap.put(cur_deptid, deptUserInfoMapItem);
				}
			}

			// 清空之前的临时map
			dept_EmployeeInfo_map.clear();
			uid_dept_map.clear();
			dept_uid_loademployeeinfo_map.clear();
			// 当前请求的数据

			Log.d(TAG, "all EmployeeInfo get done Response count,time 1:"
					+ System.currentTimeMillis());
			// 所有的数据全部拉取成功。
			mPos = 0;

			employee_info_load = LOAD_STAT.LOADED;
			// 开始存储数据到DB中去
			if (dept_info_load == LOAD_STAT.LOADED
					&& employee_info_load == LOAD_STAT.LOADED)
				doSaveData2DB();

			Log.d(TAG, "all EmployeeInfo get done Response count,time 2:"
					+ System.currentTimeMillis());
		}
		return;
	}

	private int updatePos(int pos) {

		if (pos == deptUserIdsMap.keySet().size()) { // -1
			// 没有部门需要请求了
			return -1;
		}

		if (deptUserIdsMap.get((this.deptid[pos])) != null
				&& (deptUserIdsMap.get((this.deptid[pos])).length != 0)) {
			return pos;
		} else {
			return updatePos(++pos);
		}
	}

	/**
	 * 用户状态 Map对象,存放用户的在线状态。
	 */
	HashMap<Integer, Integer> userStateMap = new HashMap<Integer, Integer>();

	// 状态最大的请求长度
	private int MAX_STATE_REQUEST_COUNT = 100;

	// 统计在线总人数
	private int onLineTotalCount = 0;

	// private void initStateRequest(HashMap<Integer, int[]> deptUserIdsMap) {
	//
	// Iterator<Entry<Integer, int[]>> iterator = deptUserIdsMap.entrySet()
	// .iterator();
	//
	// while (iterator.hasNext()) {
	// Map.Entry<Integer, int[]> entry = (Map.Entry<Integer, int[]>) iterator
	// .next();
	//
	// int count = entry.getValue().length % MAX_STATE_REQUEST_COUNT == 0 ?
	// entry
	// .getValue().length / MAX_STATE_REQUEST_COUNT
	// : entry.getValue().length / MAX_STATE_REQUEST_COUNT + 1;
	//
	// deptUserStateRequestCountMap.put(entry.getKey(), count);
	// }
	// }

	/**
	 * All User IDs
	 */
	int[] mAllUserIds = null;
	int[] mAllCIds = null;

	/**
	 * 首先得到所有的员工。得到请求的总次数。
	 */
	private int initUserStateRequest(HashMap<Integer, int[]> deptUserIdsMap) {

		mAllUserIds = null;

		// Iterator<Entry<Integer, int[]>> iterator =
		// deptUserIdsMap.entrySet().iterator();
		//
		// while (iterator.hasNext()) {
		// Map.Entry<Integer, int[]> entry = (Map.Entry<Integer, int[]>)
		// iterator.next();
		//
		// int[] temp_UserId = entry.getValue();
		//
		// LogFactory.d("UID", Arrays.toString(temp_UserId));
		//
		// if (mAllUserIds == null) {
		//
		// mAllUserIds = temp_UserId;
		// }else {
		//
		// mAllUserIds = mergeIntArray(mAllUserIds, temp_UserId);
		// }
		// LogFactory.d("UID-mAllUserIds", Arrays.toString(mAllUserIds));
		// }
		LogFactory.d("initUserStateRequest", "deptUserIdsMap size:" + deptUserIdsMap.size());
		
		for (int i = 0; i < this.deptid.length; i++) {

			int[] temp_UserId = deptUserIdsMap.get(this.deptid[i]);
			if (temp_UserId == null)
				continue;
			// LogFactory.d("UID", Arrays.toString(temp_UserId));

			if (mAllUserIds == null) {

				mAllUserIds = temp_UserId;
			} else {

				mAllUserIds = mergeIntArray(mAllUserIds, temp_UserId);
			}
			// LogFactory.d("UID-mAllUserIds", Arrays.toString(mAllUserIds));
		}

		// ==============外部联系人的时候特殊对待======================
		mAllCIds = new int[mAllUserIds.length];
		for (int i = 0; i < mAllCIds.length; i++) {
			mAllCIds[i] = EngineConst.cId;
		}

		return mAllUserIds.length % MAX_STATE_REQUEST_COUNT == 0 ? mAllUserIds.length
				/ MAX_STATE_REQUEST_COUNT
				: mAllUserIds.length / MAX_STATE_REQUEST_COUNT + 1;
	}

	/**
	 * 响应：员工的在线状态
	 * 
	 * 一次请求最大值为100 没有endFlag限制
	 * 
	 * 
	 * @param command
	 */
	private void responseEmployeeState(short command) {

		LogFactory.d(TAG + "3", "----------responseEmployeeState ---------");

		GetEmployeesStatusInPacket inPacket = (GetEmployeesStatusInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (inPacket == null)
			return;
		// 获得当前请求包 数组
		// int[] cid = inPacket.getCid();
		int[] uid = inPacket.getUid();
		int[] status = inPacket.getStatus(); // 状态

		for (int i = 0; i < uid.length; i++) {

			// 添加用户状态的映射
			userStateMap.put(uid[i], status[i]);
			// if (status[i] == 1) {
			// onLineTotalCount++;
			// }
		}

		// 当前请求的数据
		LogFactory.d(TAG, "responseEmployeeState single Response count ="
				+ uid.length);

		// 打印出当前请求返回的 员工状态 数据
		// for (int i = 0; i < uid.length; i++) {
		// LogFactory.d(TAG, " uid =" + uid[i] + "  state = " + status[i]);
		// }

		// 当前部门的请求是否都已经完成了
		// if (hasRequestCount < mStateRequestCount) {
		//
		// Object[] idArrays = getRequestUserIdArray();
		// if (idArrays != null) {
		// doRequestEmployeeState( ((int[])idArrays[0]).length,
		// ((int[])idArrays[0]), ((int[])idArrays[1]));
		//
		// }

		// } else

		// /收到包后计数器加1
		stateResponseCount++;
		stateResponseCountEx++;

		LogFactory.e("stateResponseCount", "stateResponseCount: "
				+ stateResponseCount);

		if (hasConcurrentSended == stateResponseCount) {
			updateUserState();
		}

		if (stateResponseCountEx == mStateRequestCount) {
			mPos = 0;
			LogFactory.e(TAG, "User State request has completed !!! \n "
					+ "responseEmployeeState single Response count ="
					+ uid.length);
			System.out.println("User State request has completed !!! \n ");

			forOrganize = false;// / 组织架构状态获取完成

			LogFactory.d(TAG, "onLineTotalCount = " + onLineTotalCount);

			mGlobal.updateStateMap((HashMap<Integer, Integer>) userStateMap
					.clone());

			LogFactory.e("requestState over", "time :" + System.currentTimeMillis());

			refresh(null);

			LogFactory.e("refresh", "time :" + System.currentTimeMillis());
		}

	}

	// ==============================================

	private void getTree() {

		if (deptInfoMap != null) {
			rootDeptMask = deptInfoMap.get(0);
		}

		buildTree(rootDeptMask, null);

		addUser2Tree();

		if (nodeMap != null) {
			rootNodeDept = nodeMap.get(0);
		}
		

		// adapter = new OrganizeAdapter(mContext,rootNodeDept);

		/*
		 * mListView.setAdapter(adapter);
		 * 
		 * mListView.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View
		 * view,int position, long id) {
		 * 
		 * mStructNavView.setVisibility(View.VISIBLE);
		 * 
		 * Node curNode = adapter.getItem(position);
		 * 
		 * if (NodeManager.isLeaf(curNode)) { Toast.makeText(mContext,
		 * "点击的是叶子节点", 1).show(); }else { adapter.showChildNodes(curNode); if
		 * (mStructNavView.getViewGroupChildCount()==0) {
		 * mStructNavView.addItem(
		 * rootNodeDept.getNodeData().nodeName,rootNodeDept); }
		 * mStructNavView.addItem(curNode.getNodeData().nodeName,curNode); //
		 * 实现导航点击事件的封装。 mStructNavView.setOnItemClickListener(adapter); }
		 * 
		 * } });
		 */

		doRequestOwnState();

		LogFactory.d("rootNodeDept", "root child count = "
				+ rootNodeDept.getChildNodes().size());

	}

	/**
	 * 测试请求到的数据，开始构造组织架构树
	 */
	Node rootNodeDept;

	DeptMaskItem rootDeptMask;

	Node curNode;

	/**
	 * 部门节点 Map
	 */
	HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();

	// 部门Id List
	ArrayList<Integer> deptIdList = new ArrayList<Integer>();

	/**
	 * 构建 :部门节点结构树
	 * 
	 * true 表示构建完成
	 */
	private boolean buildTree(DeptMaskItem deptInfo, Node curNode) {

		// =====================Test====================
		// showAllData();

		Node node = null;
		
		if (deptInfo.getDept_id() == 0) {

			node = new Node(new NodeData("组织结构数据", "（92/143）"));

		} else {
			node = new Node(new NodeData(deptInfo.getName(), "（10/100）"));
		}

		// node.setId(deptInfo.getDept_id());
		node.setDept(true);
		nodeMap.put(deptInfo.getDept_id(), node);

		deptIdList.add(deptInfo.getDept_id());

		if (deptInfo.getDept_id() != 0) {
			NodeManager.addChildNode(curNode, node);
		}

		if (deptInfo.getFirst_child() == -1) {
			if (deptInfo.getNext_sibling() == -1) {
				if (NodeManager.isRoot(curNode)) {
					return true;
				}

				Object[] args = getCurNode(deptInfo, node.getParentNode());
				if (args != null) {
					return buildTree(deptInfoMap.get((Integer) args[0]),
							(Node) args[1]);
				} else
					return true;

				// return buildTree(deptInfoMap.get(
				// deptInfoMap.get(deptInfo.getParent_dept_id()).getNext_sibling()),
				// getCurNode(deptInfo, node.getParentNode())
				// // node.getParentNode().getParentNode()
				// );
			} else {
				curNode = node.getParentNode();
				return buildTree(deptInfoMap.get(deptInfo.getNext_sibling()),
						curNode);
			}
		} else {
			return buildTree(deptInfoMap.get(deptInfo.getFirst_child()), node);
		}

	}

	private Object[] getCurNode(DeptMaskItem deptInfo, Node leafParent) {

		Node curNode = null;

		if (deptInfo != null) {
			Integer id = deptInfoMap.get(deptInfo.getParent_dept_id())
					.getNext_sibling();

			if (id != -1) {
				curNode = leafParent.getParentNode();
			} else {
				if (NodeManager.isRoot(leafParent)) {
					return null;
				}
				return getCurNode(deptInfoMap.get(deptInfo.getParent_dept_id()),
						leafParent.getParentNode());
			}
			return new Object[] { id, curNode };
		}
		return null;
	}

	/**
	 * 添加员工到指定的部门
	 */
	private void addUser2Tree() {

		// for (int i = 1; i < this.deptid.length; i++) {
		for (int i = 0; i < deptIdList.size(); i++) {

			// Node deptNode = nodeMap.get(this.deptid[i]); //部门节点
			Node deptNode = nodeMap.get(deptIdList.get(i)); // 部门节点
			deptNode.setId(deptIdList.get(i));

			// =========================================||
			if (deptUserInfoMap.get(deptIdList.get(i)) != null
					&& deptUserInfoMap.get(deptIdList.get(i)).keySet().size() != 0) {
				addDeptUser(deptNode, deptInfoMap.get(deptIdList.get(i)));// 添加该部门下的所有User
																			// leafNode

				userNodeMapItem = new HashMap<Integer, Node>();
				for (int j = 0; j < deptNode.getChildNodes().size(); j++) {
					Node temNode = deptNode.getChildNodes().get(j);
					if (!temNode.isDept()) {

						userNodeMapItem.put(temNode.getId(), temNode);
					}

				}
				userNodeMap.put(deptNode.getId(), userNodeMapItem);// ////
			}
			// =========================================||

			/*
			 * LogFactory.d("deptNameMapSize", "All dept count = " +
			 * nodeMap.keySet().size()); // LogFactory.d("deptName",
			 * "this dept id = " + nodeMap.get(this.deptid[i]));
			 * 
			 * // ==================================== //get first_user id int
			 * dept_first_userId =
			 * this.deptInfoMap.get(this.deptid[i]).getFfirst_user() ; //get
			 * first_user userInfo EmployeeInfoItem curEmployeeInfo =
			 * deptUserInfoMap.get(this.deptid[i]).get(dept_first_userId);
			 * 
			 * //build the first user node Node curNode = new Node(new
			 * NodeData(isBoy
			 * (curEmployeeInfo.getGender()),curEmployeeInfo.getName()));
			 * //获得兄弟Node NodeManager.addChildNode(deptNode, curNode);
			 * 
			 * //添加单个部门的员工UserInfo Map //
			 * HashMap<Integer,GetAllEmployeesInfoInPacket.EmployeeInfoItem>
			 * userInfoMap = deptUserInfoMap.get(this.deptid[i]);
			 * HashMap<Integer,GetAllEmployeesInfoInPacket.EmployeeInfoItem>
			 * userInfoMap = deptUserInfoMap.get(deptIdList.get(i));
			 * 
			 * HashMap<Integer, Integer> deptUserSiblingMapItem =
			 * deptUserSiblingMap.get(this.deptid[i]);
			 * 
			 * // Node leafNode; // int[] userIdArray =
			 * this.deptUserIdsMap.get(this.deptid[i]);
			 * 
			 * //在该部门中依次添加用户节点 for (int j = 0; j <
			 * this.deptUserIdsMap.get(this.deptid[i]).length -1; j++) {
			 * curEmployeeInfo = addUserNode(deptNode, curEmployeeInfo,
			 * userInfoMap,deptUserSiblingMapItem); if (curEmployeeInfo == null)
			 * { break; } }
			 */
		}
	}

	HashMap<Integer, HashMap<Integer, Node>> userNodeMap = new HashMap<Integer, HashMap<Integer, Node>>();// ////

	HashMap<Integer, Node> userNodeMapItem = null;// ////

	private void addDeptUser(Node deptNode, DeptMaskItem deptMaskItem) {

		int dept_first_userId = deptMaskItem.getFfirst_user();

		if (dept_first_userId != -1) {// ////////

			// userNodeMapItem = new HashMap<Integer, Node>();//////

			EmployeeInfoItem curEmployeeInfo = deptUserInfoMap.get(
					deptMaskItem.getDept_id()).get(dept_first_userId);

			// build the first user node
			Node curNode = new Node(new NodeData(
					isBoy(curEmployeeInfo.getGender()),
					curEmployeeInfo.getName()));
			curNode.setId(curEmployeeInfo.getUid());// ====
			curNode.setOnLineState(userStateMap.get(curEmployeeInfo.getUid()));
			NodeManager.addChildNode(deptNode, curNode);

			// userNodeMapItem.put(curEmployeeInfo.getUid(), curNode);//////

			// 添加单个部门的员工UserInfo Map
			HashMap<Integer, EmployeeInfoItem> userInfoMap = deptUserInfoMap
					.get(deptMaskItem.getDept_id());

			HashMap<Integer, Integer> deptUserSiblingMapItem = deptUserSiblingMap
					.get(deptMaskItem.getDept_id());

			// 在该部门中依次添加用户节点
			for (int j = 0; j < this.deptUserIdsMap.get(deptMaskItem.getDept_id()).length - 1; j++) {
				curEmployeeInfo = 
						addUserNode(deptNode, curEmployeeInfo,userInfoMap, deptUserSiblingMapItem);
				if (curEmployeeInfo == null) {
					break;
				}
			}

			// userNodeMap.put(deptNode.getId(), userNodeMapItem);//////
		}

	}

	/**
	 * @param parentNode
	 *            部门节点
	 * @param curEmployeeInfo
	 *            当前的用户信息
	 * @param deptUserInfoMap
	 *            部门UserInfoMap
	 */
	private EmployeeInfoItem addUserNode(Node parentNode,
			EmployeeInfoItem curEmployeeInfo,
			HashMap<Integer, EmployeeInfoItem> deptUserInfoMap,
			HashMap<Integer, Integer> deptUserSiblingMapItem) {

		// int colleagueId = curEmployeeInfo.getColleague_uid();
		int colleagueId = curEmployeeInfo.getUid();

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

			leafNode.setOnLineState(userStateMap.get(employeeInfoItem.getUid()));
			leafNode.setId(employeeInfoItem.getUid());

			NodeManager.addChildNode(parentNode, leafNode);

			// userNodeMapItem.put(curEmployeeInfo.getUid(), leafNode);//////

			this.curNode = leafNode;

		}
		return employeeInfoItem;
	}

	/**
	 * 获取查询结果
	 * 
	 * @param key
	 * @return
	 */
	private ArrayList<Node> getSearchResult(ArrayList<int[]> deptUserId) {

		// 0-deptId ; 1-userId
		// ArrayList<int[]> deptUserId = null ;
		int[] temp_item = null;

		ArrayList<Node> searchResultList = new ArrayList<Node>();

		for (int i = 0; i < deptUserId.size(); i++) {

			temp_item = deptUserId.get(i);

			LogFactory.d("SearchResult", "deptId = " + temp_item[0]
					+ "UserId =" + temp_item[1]);

			if (userNodeMap.get(temp_item[0]).get(temp_item[1]) != null) {

				searchResultList.add(userNodeMap.get(temp_item[0]).get(
						temp_item[1]));

				LogFactory.d(
						"SearchResult",
						"NodeDate = "
								+ userNodeMap.get(temp_item[0])
										.get(temp_item[1]).getNodeData()
										.toString());
			} else {
				LogFactory.d("SearchResult", "Null------->deptId = "
						+ temp_item[0] + "UserId =" + temp_item[1]);
			}

			// searchResultList.add(userNodeMap.get(temp_item[0]).get(temp_item[1]));

		}

		return searchResultList;

		// //0-deptId ; 1-userId
		// // ArrayList<int[]> deptUserId = null ;
		// int[] temp_item = null;
		//
		// ArrayList<Node> searchResultList = new ArrayList<Node>();
		//
		// for (int i = 0; i < deptUserId.size(); i++) {
		//
		// temp_item = deptUserId.get(i);
		//
		// LogFactory.d("SearchResult", "deptId = " +temp_item[0] +
		// "UserId ="+temp_item[1]);
		// LogFactory.d("SearchResult", "NodeDate = "
		// +userNodeMap.get(temp_item[0]).get(temp_item[1]).getNodeData().toString());
		//
		// searchResultList.add(userNodeMap.get(temp_item[0]).get(temp_item[1]));
		//
		// }
		//
		//
		// return searchResultList;
	}

	private void showAllData() {
		Set<Integer> keys = deptUserInfoMap.keySet();
		LogFactory.d("AlldeptId", "===========>" + keys.toString());
		Iterator<Integer> it = keys.iterator();
		while (it.hasNext()) {
			Integer dId = it.next();

			LogFactory.d("AlldeptId", "deptId = " + dId);

			Set<Integer> userIDs = deptUserInfoMap.get(dId).keySet();

			for (Iterator<Integer> userItem = userIDs.iterator(); userItem
					.hasNext();) {
				Integer userID = userItem.next();
				EmployeeInfoItem employeeInfoItem = deptUserInfoMap.get(dId)
						.get(userID);

				LogFactory.d("AlldeptIdUserInfo", employeeInfoItem.getUid()
						+ "\t" + employeeInfoItem.getName());
			}

		}
	}

	/**
	 * 数组的合并
	 * 
	 * @param curArray
	 * @param tempArray
	 * @return
	 */
	private int[] mergeIntArray(int[] curArray, int[] tempArray) {
		int[] destArray = new int[curArray.length + tempArray.length];

		System.arraycopy(curArray, 0, destArray, 0, curArray.length);
		System.arraycopy(tempArray, 0, destArray, curArray.length,
				tempArray.length);
		curArray = tempArray = null;
		// System.gc();
		return destArray;
	}

	/**
	 * 合并员工数组
	 * 
	 * @param curArray
	 * @param tempArray
	 * @return
	 */
	private EmployeeInfoItem[] mergeUserArray(EmployeeInfoItem[] curArray,
			EmployeeInfoItem[] tempArray) {

		EmployeeInfoItem[] destArray = new EmployeeInfoItem[curArray.length
				+ tempArray.length];

		System.arraycopy(curArray, 0, destArray, 0, curArray.length);
		System.arraycopy(tempArray, 0, destArray, curArray.length,
				tempArray.length);
		curArray = tempArray = null;
		// System.gc();
		return destArray;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		return super.onKeyDown(keyCode, event);
	}

	// ==========================================================================================
	/** 更新列表的显示 */
	private final int TYPE_REFRESH_TREE = 1;

	public Handler mContactHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case TYPE_REFRESH_TREE:
				turn2Main();
				break;

			default:
				break;
			}

		}

	};

	/**
	 * 数据全部请求完成后：跳转主界面
	 */
	private void turn2Main() {
		LogFactory.e(TAG, "trun2Main---------------> hasGetInnerContactId :"
				+ hasGetInnerContactId + ", hasGetInnerGroupInfo :"
				+ hasGetInnerGroupInfo + ", hasGetInnerContactWorkSign :"
				+ hasGetInnerContactWorkSign + ", hasGetOuterGroupInfo :"
				+ hasGetOuterGroupInfo + ", hasGetOuterContactId :"
				+ hasGetOuterContactId + ", hasGetOuterCropName :"
				+ hasGetOuterCropName + ", hasGetOuterContactBasicInfoState :"
				+ hasGetOuterContactBasicInfoState
				+ ", hasGetOuterContactState_State :"
				+ hasGetOuterContactState_State);

		if (hasGetInnerContactId && hasGetInnerGroupInfo
				&& hasGetInnerContactWorkSign && hasGetOuterGroupInfo
				&& hasGetOuterContactId && hasGetOuterCropName
				&& hasGetOuterContactBasicInfoState
				&& hasGetOuterContactState_State) {

			// /1- 更新全局数据
			// HashMap<Integer,InnerContactorItem> innerGroupIdMap,
			// HashMap<Integer,ArrayList<Integer>> innerGroupContactMap,
			// HashMap<Integer, String> innerContactWorkSignMap,
			// HashMap<Integer,OuterContactorItem> outerGroupIdMap ,
			// HashMap<Integer,ArrayList<OuterContactItem>>
			// outerGroupContactMap,
			// HashMap<Integer, String> outerContactCorpMap,
			// HashMap<Integer, OuterContactBasicInfo> outerContactInfoMap

			LogFactory.e(TAG,
					"innerGroupIdMap :" + innerGroupIdMap.size()
							+ ", innerGroupContactMap :"
							+ innerGroupContactMap.size()
							+ ", innerContactWorkSignMap :"
							+ innerContactWorkSignMap.size()
							+ ", outerGroupIdMap :" + outerGroupIdMap.size()
							+ ", outerGroupContactMap :"
							+ outerGroupContactMap.size()
							+ ", outerContactCorpMap :"
							+ outerContactCorpMap.size()
							+ ", outerContactInfoMap :"
							+ outerContactInfoMap.size());
			mGlobal.updateContactData(
					(HashMap<Integer, InnerContactorItem>) innerGroupIdMap
							.clone(),
					(HashMap<Integer, ArrayList<Integer>>) innerGroupContactMap
							.clone(),
					(HashMap<Integer, String>) innerContactWorkSignMap.clone(),
					(HashMap<Integer, OuterContactorItem>) outerGroupIdMap
							.clone(),
					(HashMap<Integer, ArrayList<OuterContactItem>>) outerGroupContactMap
							.clone(),
					(HashMap<Integer, String>) outerContactCorpMap.clone(),
					(HashMap<Integer, OuterContactBasicInfo>) outerContactInfoMap
							.clone());
			// /2- 存数据库
			if (doSaveData2Local()) {
				// /3- 跳转页面
				// dialog.dismiss();
				MainActivityGroup.launch(mContext);
				FirstLoadingActivity.this.finish();
			} else {
				LogFactory.d(TAG, "数据存取失败。...。");
			}
		}
	}

	// ============================begin========================================================
	private int untransID = -1;
	private int innerGroupUC = -1;
	private int innerGroupListUC = -1;
	private int outerGroupUC = -1;
	private int outerGroupListUC = -1;

	/** 内部联系人Group Map */
	private HashMap<Integer, InnerContactorItem> innerGroupIdMap;

	/** 内部联系人Group_ContactId_Map */
	private HashMap<Integer, ArrayList<Integer>> innerGroupContactMap = new HashMap<Integer, ArrayList<Integer>>();

	/** 外部联系人Group Map */
	private HashMap<Integer, OuterContactorItem> outerGroupIdMap;

	/** 外部联系人Group_Contact_Map */
	private HashMap<Integer, ArrayList<OuterContactItem>> outerGroupContactMap = new HashMap<Integer, ArrayList<OuterContactItem>>();

	/**
	 * reset innerGroup and outer Group
	 */
	private void resetGroupMap() {
		innerGroupIdMap = new HashMap<Integer, InnerContactorItem>();
		innerGroupIdMap.put(0, new InnerContactorItem(0, getResources()
				.getString(R.string.uncategorized_inner_contact)));
		// innerGroupContactMap.put(0, new ArrayList<Integer>());

		outerGroupIdMap = new HashMap<Integer, OuterContactorItem>();
		outerGroupIdMap.put(0, new OuterContactorItem(0, getResources()
				.getString(R.string.uncategorized_outer_contact)));
		// outerGroupContactMap.put(0, null);
	}

	// private ProgressDialog dialog = null;

	private boolean innerGroupInfoNeedUpdate = true;
	private boolean innerContactNeedUpdate = true;
	private boolean outerGroupInfoNeedUpdate = true;
	private boolean outerContactNeedUpdate = true;

	/**
	 * 开始加载数据：InnerGroupUC ,OuterGroupUC
	 */
	private void beginLoadingContact() {
		// dialog = DialogFactory.progressDialog(mContext, "正在请求数据，请稍后...");
		// dialog.show();

		IMOApp.getDataEngine().addToObserverList(this);

		doRequestInnerGroupUC();
		doRequestInnerContactUC();

		doRequestOuterGroupUC();
		//doRequestOuterContactUC();
	}

	private boolean hasGetInnerGroupInfo = false;

	/**
	 * 1-请求内部联系人 GroupUC
	 */
	private void doRequestInnerGroupUC() {

		LogFactory.d(TAG, "doRequestInnerGroupUC ................");

		hasGetInnerGroupInfo = false;
		Random random = new Random();
		untransID = random.nextInt();
		ByteBuffer buffer = ContactorGroupUCOutPacket
				.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacket = new ContactorGroupUCOutPacket(
				buffer, IMOCommand.IMO_INNER_CONTACTOR_GROUP_UC,
				EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	private boolean hasGetInnerContactId = false;

	/**
	 * 2-请求内部联系人 ContactUC
	 */
	private void doRequestInnerContactUC() {

		LogFactory.d(TAG, "doRequestInnerContactUC ................");

		hasGetInnerContactId = false;
		Random random = new Random();
		untransID = random.nextInt();
		ByteBuffer buffer = ContactorGroupUCOutPacket
				.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacket = new ContactorGroupUCOutPacket(
				buffer, IMOCommand.IMO_INNER_CONTACTOR_LIST_UC,
				EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * 请求所有的InnerGroupInfo
	 */
	private void doRequestInnerGroupInfo() {
		CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0),
				IMOCommand.IMO_INNER_CONTACTOR_GROUP, EngineConst.cId,
				EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * 请求内部联系人的所有Id
	 */
	private void doRequestInnerContactId() {

		innerGroupContactMap.clear();

		CommonOutPacket outPacketList = new CommonOutPacket(
				ByteBuffer.allocate(0), IMOCommand.IMO_INNER_CONTACTOR_LIST,
				EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacketList, false);
	}

	private boolean hasGetOuterContactState_State = false;

	/**
	 * request outer contact user state
	 * 
	 * max= 100
	 * 
	 * @param aContactorsNum
	 * @param aContactorCidArray
	 * @param aContactorUidArray
	 */
	private void doRequestOuterContactState(int aContactorsNum,
			int[] aContactorCidArray, int[] aContactorUidArray) {

		LogFactory.d(TAG,
				"......................doRequest Outer Contact State ");

		hasGetOuterContactState_State = false;

		ByteBuffer bufferBody = 
				GetEmployeesStatusOutPacket.GenerateEmployeesStatusBody(aContactorsNum,aContactorCidArray, aContactorUidArray);
		GetEmployeesStatusOutPacket outPacket = new GetEmployeesStatusOutPacket(
				bufferBody, IMOCommand.IMO_GET_EMPLOYEE_STATUS,
				EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	// //has get inner contact work sign
	private boolean hasGetInnerContactWorkSign = false;

	/**
	 * 获得内部联系人签名 :依次去请求，否则无法控制，是否已经全部收到。
	 * 
	 * @param cid
	 * @param uid
	 */
	private void doRequestInnerContactWorkSign(int cid, int uid) {

		LogFactory.d(TAG, "doRequestInnerContactWorkSign ................");

		// if (curPos_InnerWorkSign == 0) {
		// innerContactWorkSignMap.clear();
		// }

		hasGetInnerContactWorkSign = false;

		int mask = (1 << 4);// 签名
		Random random = new Random();
		untransID = random.nextInt();

		ByteBuffer bodyBuffer = GetEmployeeProfileOutPacket
				.GenerateEmployeeProfileBody(untransID, cid, uid, mask);
		GetEmployeeProfileOutPacket out = new GetEmployeeProfileOutPacket(
				bodyBuffer, IMOCommand.IMO_GET_EMPLOYEE_PROFILE,
				EngineConst.cId, EngineConst.uId);
		
		outer_loadinfo_seq_map.put(out.get_header_seq(), uid);
		
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	// // [内部联系人]
	// private void doRequestAllEmployeeInfo(int aContactorsNum,int[]
	// aContactorUidArray){
	//
	// ByteBuffer bufferBody =
	// GetAllEmployeesInfoOutPacket.GenerateEmployeesBasicInfoBody(aContactorsNum,
	// aContactorUidArray);
	//
	// GetAllEmployeesInfoOutPacket outPacket = new
	// GetAllEmployeesInfoOutPacket(bufferBody,
	// IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO, EngineConst.cId,
	// EngineConst.uId);
	//
	// mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	// }

	private boolean hasGetOuterGroupInfo = false;

	/**
	 * Request Outer GroupUC
	 */
	private void doRequestOuterGroupUC() {
		LogFactory.d(TAG, "................doRequestOuterGroupUC ");

		Random random = new Random();
		untransID = random.nextInt();

		ByteBuffer bufferList = ContactorGroupUCOutPacket
				.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacketList = new ContactorGroupUCOutPacket(
				bufferList, IMOCommand.IMO_OUTER_CONTACTOR_GROUP_UC,
				EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacketList, false);
	}

	private boolean hasGetOuterContactId = false;

	/**
	 * Request Outer ContactUC
	 */
	private void doRequestOuterContactUC() {

		LogFactory.d(TAG, "................doRequestOuterContactUC ");

		Random random = new Random();
		untransID = random.nextInt();
		ByteBuffer buffer = ContactorGroupUCOutPacket
				.GenerateGroupUCBody(untransID);
		ContactorGroupUCOutPacket outPacket = new ContactorGroupUCOutPacket(
				buffer, IMOCommand.IMO_OUTER_CONTACTOR_LIST_UC,
				EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	/**
	 * Request Outer GroupInfo
	 */
	private void doRequestOuterGroupInfo() {

		LogFactory.d(TAG, "doRequestOuterGroupInfo ................");

		CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0),
				IMOCommand.IMO_OUTER_CONTACTOR_GROUP, EngineConst.cId,
				EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	private void doRequestOuterContactId() {

		LogFactory.d(TAG, "doRequestOuterContactId ................");

		CommonOutPacket outPacketList = new CommonOutPacket(
				ByteBuffer.allocate(0), IMOCommand.IMO_OUTER_CONTACTOR_LIST,
				EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacketList, false);
	}

	private boolean hasGetOuterContactBasicInfoState = false;

	/**
	 * do request outer BasicInfo
	 * 
	 * @param cid
	 * @param uid
	 */
	private void doRequestOuterContactBasicInfo(int cid, int uid) {

		LogFactory.d(TAG, 
				" ................doRequestOuterBasicInfo cid=" + cid + " uid = " + uid);

		hasGetOuterContactBasicInfoState = false;
		ByteBuffer bodyBuffer = OuterBasicInfoOutPacket.GenerateOuterBasicInfoBody(cid, uid);
		OuterBasicInfoOutPacket out = new OuterBasicInfoOutPacket(bodyBuffer,
				IMOCommand.IMO_OUTER_BASIC_INFO, EngineConst.cId,
				EngineConst.uId);
		outer_basic_info_seq_map.put(out.get_header_seq(), uid);
		
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	/** has get outer crop */
	private boolean hasGetOuterCropName = false;

	/**
	 * do request outer corp info
	 * 
	 * @param cid
	 *            company id
	 */
	private void doRequestOuterCorpInfo(int cid) {

		Log.d(TAG,
				"................doRequestOuterCorpInfo ................cid =" + cid);

		hasGetOuterCropName = false;
		int mask = (1 << 1);// 公司简介
		ByteBuffer bodyBuffer = GetCorpInfoOutPacket.GenerateCorpInfoBody(cid,mask);
		GetCorpInfoOutPacket out = new GetCorpInfoOutPacket(bodyBuffer,
				IMOCommand.IMO_GET_CORP_INFO, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
	}

	// -----------------------------------------------

	/** 外部联系人：公司信息 */
	private HashMap<Integer, String> outerContactCorpMap = new HashMap<Integer, String>();

	/**
	 * response Outer Corp info
	 * 
	 * @param command
	 */
	private void responseOuterCorpInfo(short command) {

		GetCorpInfoInPacket getCorpInfoInPacket = 
				(GetCorpInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		
		if (getCorpInfoInPacket == null)
			return;
		short commandRet = getCorpInfoInPacket.getRet();

		if (commandRet == 0) {

			CorpMaskItem maskItem = getCorpInfoInPacket.getMaskItem();
			String shortName = "";
			if (maskItem != null) {
				shortName = maskItem.getShort_name();
			}
			if (shortName == null) {
				shortName = "";
			}
			outerContactCorpMap.put(getCorpInfoInPacket.getContactorID(),
					shortName);

			LogFactory.e(TAG, "cid = " + getCorpInfoInPacket.getContactorID()
					+ ",shortName = " + shortName);
		}

		/*
		 * if (curPos_OuterCropName < outerCidList.size()) { // / the next
		 * request
		 * doRequestOuterCorpInfo(outerCidList.get(curPos_OuterCropName++)); }
		 * else { updateGetOuterCropNameState();
		 * LogFactory.d(TAG,"........................has got all Out crop Name "
		 * ); }
		 */
		outer_corp_loadinfo_map.put(getCorpInfoInPacket.getContactorID(),LOAD_STAT.LOADED);
		concurrent_outer_corp_loading_count--;

		int send_cnt = do_concurrent_outer_corp_info_req();

		if (send_cnt == 0) {
			check_outer_corp_info_req_finish();
		}
	}

	private int check_outer_corp_info_req_finish() {
		// 开始并发请求deptuids
		int init_cnt = concurrent_outer_corp_loading_count;
		for (int i = 0; i < this.outerCidList.size()
				&& concurrent_outer_corp_loading_count < MAX_REQUEST_CONCURRENT_OUTER_CORP_NUMBER; i++) {
			if (outer_corp_loadinfo_map.get(outerCidList.get(i)) == LOAD_STAT.NOT_LOAD
					|| outer_corp_loadinfo_map.get(outerCidList.get(i)) == LOAD_STAT.LOADING) {

				return 0;
			}
		}

		// 所有deptinfo请求均已完成，此处清除请求中使用到的各个map
		outer_corp_loadinfo_map.clear();

		LogFactory.e("check_outer_corp_info_req_finish", "over");

		updateGetOuterCropNameState();

		return 1;
	}

	/** Outer Contact Basic info Map */
	private HashMap<Integer, OuterContactBasicInfo> outerContactInfoMap = new HashMap<Integer, OuterContactBasicInfo>();

	/**
	 * response outer basic info
	 * 
	 * @param command
	 */
	private void responseOuterBasicInfo(short command) {

		LogFactory.d(TAG, "........................responseOuterBasicInfo");

		OuterBasicInfoInPacket outerContactorBasicInfo = (OuterBasicInfoInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (outerContactorBasicInfo == null)
			return;

		short commandRet = outerContactorBasicInfo.getRet();

		if (commandRet == 0) {
			int cid = outerContactorBasicInfo.getContactor_cid();
			int uid = outerContactorBasicInfo.getContactor_uid();
			String corpAccount = outerContactorBasicInfo.getCorp_account();
			String userAccount = outerContactorBasicInfo.getUser_account();
			String name = outerContactorBasicInfo.getName();
			int gender = outerContactorBasicInfo.getGender();

			OuterContactBasicInfo basicInfo = new OuterContactBasicInfo(cid,
					uid, corpAccount, userAccount, name, gender);

			// // add to the map
			outerContactInfoMap.put(uid, basicInfo);

			LogFactory.e(TAG, ".......................外部联系人基本资料:name =" + name
					+ "  userAccount = " + userAccount);
		}

		/*
		if (curPos_OuterBasicInfo < outerContactItemList.size()) {
			OuterContactItem requestItem = outerContactItemList
					.get(curPos_OuterBasicInfo++);
			doRequestOuterContactBasicInfo(requestItem.getCid(),
					requestItem.getUid());
		} else {
			LogFactory.d(TAG, "has got all outer basic info ........");
			updateGetOuterContactBasicInfoState();
		}
		**/

		if(outer_basic_info_map.containsKey(outerContactorBasicInfo.getContactor_uid()))
		{
			outer_basic_info_map.put(outerContactorBasicInfo.getContactor_uid(),LOAD_STAT.LOADED);
		}
		else
		{
			int uid = outer_basic_info_seq_map.get(outerContactorBasicInfo.get_header_seq());
			outer_basic_info_map.put(uid, LOAD_STAT.LOADED);
		}
		
		LogFactory.e(TAG, "outer_basic_info_map end : "+outer_basic_info_map.keySet()+",value :"+outer_basic_info_map.values());
		concurrent_outer_basic_info_loading_count--;

		int send_cnt = do_concurrent_outer_basic_info_req();

		if (send_cnt == 0) {
			check_outer_basic_info_req_finish();
		}
	}

	private int check_outer_basic_info_req_finish() {
		// 开始并发请求deptuids
		int init_cnt = concurrent_outer_basic_info_loading_count;
		for (int i = 0; i < this.outerContactItemList.size()
				&& concurrent_outer_basic_info_loading_count < MAX_REQUEST_CONCURRENT_OUTER_BASIC_INFO_NUMBER; i++) {
			if (outer_basic_info_map.get(outerContactItemList.get(i).getUid()) == LOAD_STAT.NOT_LOAD
					|| outer_basic_info_map.get(outerContactItemList.get(i).getUid()) == LOAD_STAT.LOADING) {

				return 0;
			}
		}

		// 所有deptinfo请求均已完成，此处清除请求中使用到的各个map
		outer_basic_info_map.clear();
		outer_basic_info_seq_map.clear();

		LogFactory.e("check_outer_basic_info_req_finish", "over");

		updateGetOuterContactBasicInfoState();

		return 1;
	}

	/** Outer Conctact Crop cid_name Map :for get the crop name */
	private HashMap<Integer, String> outerContactCropMap = new HashMap<Integer, String>();
	/** Outer Conctact Crop id List */
	private ArrayList<Integer> outerCidList = new ArrayList<Integer>();
	private Integer curPos_OuterCropName = 0;

	/** Outer Conctact Id --[cid,uid,group,id] */
	private ArrayList<OuterContactItem> outerContactItemList = new ArrayList<OuterContactItem>();
	private Integer curPos_OuterBasicInfo = 0;
	private Integer curPos_OuterContactState = 0;// / outer contact state

	// // outer contact request total count for state
	private int outerStateRequestTotalCount = 0;

	private int MAX_REQUEST_CONCURRENT_OUTER_CORP_NUMBER = 100;
	private int concurrent_outer_corp_loading_count = 0;
	// outer_loadinfo_map 标识当前所有deptid的deptinfo的获取情况，有未载入，载入中，已载入3个状态
	private Map<Integer, Integer> outer_corp_loadinfo_map = new HashMap<Integer, Integer>();

	private int do_concurrent_outer_corp_info_req() {
		// 开始并发请求deptinfo
		int init_cnt = concurrent_outer_corp_loading_count;
		for (int i = 0; i < this.outerCidList.size()
				&& concurrent_outer_corp_loading_count < MAX_REQUEST_CONCURRENT_OUTER_CORP_NUMBER; i++) {

			if (outer_corp_loadinfo_map.get(outerCidList.get(i)) == LOAD_STAT.NOT_LOAD) {
				doRequestOuterCorpInfo(outerCidList.get(i));
				outer_corp_loadinfo_map.put(outerCidList.get(i),LOAD_STAT.LOADING);
				concurrent_outer_corp_loading_count++;
			} else {
			}
		}

		return concurrent_outer_corp_loading_count - init_cnt;
	}

	private int MAX_REQUEST_CONCURRENT_OUTER_BASIC_INFO_NUMBER = 100;
	private int concurrent_outer_basic_info_loading_count = 0;
	// outer_loadinfo_map 标识当前所有deptid的deptinfo的获取情况，有未载入，载入中，已载入3个状态
	private Map<Integer, Integer> outer_basic_info_map = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> outer_basic_info_seq_map = new HashMap<Integer, Integer>();
	

	private int do_concurrent_outer_basic_info_req() {
		// 开始并发请求deptinfo
		int init_cnt = concurrent_outer_basic_info_loading_count;
		for (int i = 0; i < this.outerContactItemList.size()
				&& concurrent_outer_basic_info_loading_count < MAX_REQUEST_CONCURRENT_OUTER_BASIC_INFO_NUMBER; i++) {

			if (outer_basic_info_map.get(this.outerContactItemList.get(i).getUid()) == LOAD_STAT.NOT_LOAD) {

				OuterContactItem requestItem = outerContactItemList.get(i);
				doRequestOuterContactBasicInfo(requestItem.getCid(),requestItem.getUid());

				outer_basic_info_map.put(requestItem.getUid(),LOAD_STAT.LOADING);
				LogFactory.e(TAG, "outer_basic_info_map changed : "+outer_basic_info_map.keySet()+",value :"+outer_basic_info_map.values());
				concurrent_outer_basic_info_loading_count++;
			} else {
			}
		}

		return concurrent_outer_basic_info_loading_count - init_cnt;
	}

	/**
	 * response outer Contact Id
	 * 
	 * @param command
	 */
	private void responseOuterContactId(short command) {

		LogFactory.e(TAG + "Outer", "................responseOuterContactId.");

		OuterContactorListInPacket outerContactorList = (OuterContactorListInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (outerContactorList == null)
			return;
		short commandRet = outerContactorList.getRet();
		byte endflag = outerContactorList.getEndflag();

		if (commandRet == 0) {
			int[] outertempContactorCid = outerContactorList.getContactor_cid();
			int[] outertempContactorUid = outerContactorList.getContactor_uid();
			int[] outertempInnerGroupId = outerContactorList.getGroup_id();
			int[] outFlag = outerContactorList.getFlag();

			LogFactory.e(TAG, "responseOuterContactId size : " + outertempContactorCid.length);

			for (int i = 0; i < outertempContactorUid.length; i++) {

				OuterContactItem item = new OuterContactItem(
						outertempContactorCid[i], outertempContactorUid[i],
						outertempInnerGroupId[i], outFlag[i]);
				outerContactItemList.add(item);// All Outer Contact id info for
												// basic info

				outer_basic_info_map.put(outertempContactorUid[i],LOAD_STAT.NOT_LOAD);
				
				LogFactory.e(TAG, "outer_basic_info_map initail : "+outer_basic_info_map.keySet()+",value :"+outer_basic_info_map.values());

				outerContactCropMap.put(outertempContactorCid[i], ""); //
																		// update
																		// outer
																		// contact
																		// cropId

				if (outerGroupIdMap.containsKey(outertempInnerGroupId[i])) {
					ArrayList<OuterContactItem> outerContactList = 
							outerGroupContactMap.get(outertempInnerGroupId[i]);
					if (outerContactList == null) {
						outerContactList = new ArrayList<OuterContactItem>();
					}
					outerContactList.add(item);

					outerGroupContactMap.put(outertempInnerGroupId[i],outerContactList);
				} else {
					item.setGroupId(0);

					ArrayList<OuterContactItem> outerContactList = outerGroupContactMap.get(0);
					if (outerContactList == null) {
						outerContactList = new ArrayList<OuterContactItem>();
					}
					outerContactList.add(item);

					outerGroupContactMap.put(0, outerContactList);
				}
			}
		}

		if (1 == endflag) {

			LogFactory.e(TAG,
					"outerContactCropMap size : " + outerContactCropMap.size());

			// //has got all outer contact id
			LogFactory.e(TAG + "Outer",
					"................外部联系人ContactorID数据-请求完毕！");

			updateGetOuterContactIdState();

			// 1- get all cId, and begin doRequestCropName
			for (Integer cId : outerContactCropMap.keySet()) {
				outerCidList.add(cId);
				outer_corp_loadinfo_map.put(cId, LOAD_STAT.NOT_LOAD);
			}

			/*
			 * curPos_OuterCropName = 0; // // first request outer crop info if
			 * (outerCidList.size() > 0) {
			 * doRequestOuterCorpInfo(outerCidList.get(curPos_OuterCropName++));
			 * } else { updateGetOuterCropNameState(); }
			 */

			if (outerCidList.size() > 0) {
				LogFactory.e(TAG, "outerCidList size : " + outerCidList.size());
				do_concurrent_outer_corp_info_req();
			} else {
				updateGetOuterCropNameState();
			}

			// 2- begin doRequestOuterContactBasicInfo
			/*
			 * if (outerContactItemList.size() > 0) { OuterContactItem
			 * requestItem = outerContactItemList.get(curPos_OuterBasicInfo++);
			 * doRequestOuterContactBasicInfo
			 * (requestItem.getCid(),requestItem.getUid()); } else {
			 * updateGetOuterContactBasicInfoState(); }
			 */

			if (outerContactItemList.size() > 0) {
				do_concurrent_outer_basic_info_req();
			} else {
				updateGetOuterContactBasicInfoState();
			}

			// 3- begin doRequestOuterContactState
			outerStateRequestTotalCount = outerContactItemList.size()
					% MAX_STATE_REQUEST_COUNT == 0 ? outerContactItemList
					.size() / MAX_STATE_REQUEST_COUNT : outerContactItemList
					.size() / MAX_STATE_REQUEST_COUNT + 1;

			LogFactory.e(TAG, "................outerStateRequestTotalCount = "
					+ outerStateRequestTotalCount);

			if (outerStateRequestTotalCount > 0) {
				Object[] objs = getRequestOuterContactIdArray();
				doRequestOuterContactState(((int[]) objs[0]).length,
						(int[]) objs[0], (int[]) objs[1]);
			} else {
				// /没有员工，无效请求state
				updateGetOuterContactState_State();
			}
		}
	}

	// private int MAX_STATE_REQUEST_COUNT = 100;
	/**
	 * 状态请求外部联系人数组ID
	 * 
	 * @return
	 */
	private Object[] getRequestOuterContactIdArray() {

		int[] requestCIds = null;
		int[] requestUIds = null;

		if (outerStateRequestTotalCount - 1 > curPos_OuterContactState) {
			// /request max
			requestCIds = new int[MAX_STATE_REQUEST_COUNT];
			requestUIds = new int[MAX_STATE_REQUEST_COUNT];

			LogFactory.d(TAG + "State--HasRequestCount",
					"curPos_OuterContactState = " + curPos_OuterContactState);

			int start = MAX_STATE_REQUEST_COUNT * curPos_OuterContactState;
			int end = MAX_STATE_REQUEST_COUNT * curPos_OuterContactState
					+ MAX_STATE_REQUEST_COUNT;

			List<OuterContactItem> tempList = outerContactItemList.subList(start, end);

			LogFactory.d(TAG, "request state arraySize = "
					+ MAX_STATE_REQUEST_COUNT);

			for (int i = 0; i < tempList.size(); i++) {
				requestCIds[i] = tempList.get(i).getCid();
				requestUIds[i] = tempList.get(i).getUid();
			}

			curPos_OuterContactState++;

		} else {
			LogFactory.d(TAG + "State--HasRequestCount",
					"curPos_OuterContactState = " + curPos_OuterContactState);
			int arraySize = outerContactItemList.size()
					- curPos_OuterContactState * MAX_STATE_REQUEST_COUNT;

			LogFactory.d(TAG, "last request state arraySize = " + arraySize);

			if (arraySize > 0) {
				requestCIds = new int[arraySize];
				requestUIds = new int[arraySize];

				int start = MAX_STATE_REQUEST_COUNT * curPos_OuterContactState;
				int end = outerContactItemList.size();

				List<OuterContactItem> tempList = outerContactItemList.subList(
						start, end);

				for (int i = 0; i < tempList.size(); i++) {
					requestCIds[i] = tempList.get(i).getCid();
					requestUIds[i] = tempList.get(i).getUid();
				}

				curPos_OuterContactState++;
			} else {
				return null;
			}
		}

		return new Object[] { requestCIds, requestUIds };
	}

	/**
	 * response outer contact group info
	 * 
	 * @param command
	 */
	private void responseOuterGroupInfo(short command) {

		LogFactory.d(TAG, "................responseOuterContactGroupInfo.");

		ContactorGroupInPacket outerGroupInPacket = (ContactorGroupInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (outerGroupInPacket == null)
			return;
		short commandRet = outerGroupInPacket.getRet();

		byte endflag = outerGroupInPacket.getEndflag();

		if (commandRet == 0) {

			int[] outertempGroupId = outerGroupInPacket.getGroup_id();
			String[] outertempGroupName = outerGroupInPacket.getGroup_name();

			for (int i = 0; i < outertempGroupName.length; i++) {
				LogFactory.e(TAG, "outertempGroupId = " + outertempGroupId[i]
						+ "  outertempGroupName" + outertempGroupName[i]);
				outerGroupIdMap.put(outertempGroupId[i],
						new OuterContactorItem(outertempGroupId[i],
								outertempGroupName[i]));
			}
		}
		if (endflag == 1) {
			updateGetOuterGroupInfoState();
		}
	}

	/**
	 * response outer Contact UC
	 * 
	 * @param command
	 */
	private void responseOuterContactUC(short command) {

		LogFactory.d(TAG, "................responseOuterContactUC");

		ContactorGroupUCInPacket outerlistinPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (outerlistinPacket == null)
			return;
		// /获得本地Contact Uc
		outerGroupListUC = (Integer) PreferenceManager.get(Globe.SP_FILE,
				new Object[] { OUTER_GROUP_LIST_UC, outerGroupListUC });

		int curOuterGroupListUC = outerlistinPacket.getUnContactorGroupListUC();

		LogFactory.d(TAG, "curOuterGroupListUC = " + curOuterGroupListUC);

		if (curOuterGroupListUC != outerGroupListUC) {
			// outer Contact need update
			outerGroupListUC = curOuterGroupListUC;
			doRequestOuterContactId();
		} else {
			// /outer Contact needn't update , get data from local
			outerContactNeedUpdate = false;
			// //1- get local date
			outerGroupContactMap.clear();
			outerContactCorpMap.clear();
			outerContactInfoMap.clear();
			// /////获得本地的3大数据
			try {
				mGlobal.imoStorage.getAllOuterContactListInfo(
						outerGroupContactMap, outerContactCorpMap,
						outerContactInfoMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateGetOuterContactIdState();
			updateGetOuterCropNameState();
			updateGetOuterContactBasicInfoState();

			// 3- begin doRequestOuterContactState
			outerContactItemList.clear();
			for (Integer groupId : outerGroupContactMap.keySet()) {
				ArrayList<OuterContactItem> itemList = outerGroupContactMap
						.get(groupId);
				if (itemList != null) {
					outerContactItemList.addAll(itemList);
				}
			}
			outerStateRequestTotalCount = outerContactItemList.size()
					% MAX_STATE_REQUEST_COUNT == 0 ? outerContactItemList
					.size() / MAX_STATE_REQUEST_COUNT : outerContactItemList
					.size() / MAX_STATE_REQUEST_COUNT + 1;

			LogFactory.d(TAG, "................outerStateRequestTotalCount = "
					+ outerStateRequestTotalCount);

			if (outerStateRequestTotalCount > 0) {
				Object[] objs = getRequestOuterContactIdArray();
				if (objs != null) {
					doRequestOuterContactState(((int[]) objs[0]).length,
							(int[]) objs[0], (int[]) objs[1]);
				}
			} else {
				// /没有员工，无需请求state
				updateGetOuterContactState_State();
			}
		}
	}

	/**
	 * response outer contactor group uc
	 * 
	 * @param command
	 */
	private void responseOuterContactGroupUC(short command) {

		LogFactory.d(TAG, "................responseOuterContactGroupUC");

		ContactorGroupUCInPacket outerinPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (outerinPacket == null)
			return;
		// /本地数据
		outerGroupUC = (Integer) PreferenceManager.get(Globe.SP_FILE,
				new Object[] { OUTER_GROUP_UC, outerGroupUC });

		int curOuterGroupUC = outerinPacket.getUnContactorGroupListUC();

		LogFactory.d(TAG, "curOuterGroupUC = " + curOuterGroupUC);

		if (curOuterGroupUC != outerGroupUC) {
			// /Group info need update
			outerGroupUC = curOuterGroupUC;
			doRequestOuterGroupInfo();
		} else {
			outerGroupInfoNeedUpdate = false;
			// /GroupInfo needn't update , get data from local
			outerGroupIdMap.clear();
			try {
				mGlobal.imoStorage.getOuterGroupInfo(outerGroupIdMap);// //
																		// 添加数据库接口：获得所有的OuterGroupInfo
			} catch (Exception e) {
				e.printStackTrace();
			}

			updateGetOuterGroupInfoState();
		}
	}

	// /**
	// * response All Employee info
	// *
	// * 内部联系人不需要使用
	// *
	// * @param command
	// */
	// private void responseAllEmployeeInfo(short command) {
	// GetAllEmployeesInfoInPacket employeesbasicinfoInPac =
	// (GetAllEmployeesInfoInPacket)
	// tcpConnection.getInPacketByCommand(command);
	// EmployeeInfoItem[] itemArray =
	// employeesbasicinfoInPac.getEmployeesInfoArray();
	// Log.e("ContactActivity item array :", ""+itemArray.toString());
	//
	// int[] cidArray = new int[itemArray.length];
	// int[] uidArray = new int[itemArray.length];
	// for (int i = 0; i < itemArray.length; i++)
	// {
	// cidArray[i] = EngineConst.cId;
	// uidArray[i] = itemArray[i].getUid();
	// }
	// // doRequestOuterContactState(itemArray.length,cidArray,uidArray);
	// }

	/**
	 * Outer contact state map
	 */
	private HashMap<Integer, Integer> outerId_State_Map = new HashMap<Integer, Integer>();

	/**
	 * response Employee state
	 * 
	 * 内部联系人不需要请求state
	 * 
	 * @param command
	 */
	private void responseOuterContactState(short command) {

		LogFactory.d(TAG, "...............responseOuterContactState");

		GetEmployeesStatusInPacket statusInPacket = (GetEmployeesStatusInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (statusInPacket == null)
			return;
		LogFactory.d(TAG, "Outer Contact 状态" + statusInPacket.toString());

		int[] uid = statusInPacket.getUid();
		int[] status = statusInPacket.getStatus(); // 状态

		for (int i = 0; i < uid.length; i++) {
			LogFactory.d(TAG, "uid =" + uid[i] + "  status =" + status[i]);
			outerId_State_Map.put(uid[i], status[i]);
		}

		if (curPos_OuterContactState < outerStateRequestTotalCount) {
			Object[] objs = getRequestOuterContactIdArray();
			if (objs != null) {
				doRequestOuterContactState(((int[]) objs[0]).length,
						(int[]) objs[0], (int[]) objs[1]);
			} else {
				updateGetOuterContactState_State();
			}
		} 

		//fxw wait outer contact state has got 
		if(outerId_State_Map.size()==outerContactItemList.size()){
			LogFactory.d(TAG,
					"...............outer contact state has got .....");
			updateGetOuterContactState_State();
		}
	}

	/** 内部联系人工作签名映射 */
	private HashMap<Integer, String> innerContactWorkSignMap = new HashMap<Integer, String>();

	/**
	 * response Inner Contact work sign
	 * 
	 * @param command
	 */
	private void responseInnerContactWorkSign(short command) {

		LogFactory.e(TAG, "responseInnerContactWorkSign ----------------->");

		EmployeeProfileItem employeeProfileItem = null;

		String sign = "";

		GetEmployeeProfileInPacket getEmployeeProfileInPacket = (GetEmployeeProfileInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (getEmployeeProfileInPacket == null)
			return;

		short commandRet = getEmployeeProfileInPacket.getRet();

		if (commandRet == 0) {
			int uid = getEmployeeProfileInPacket.getUid();

			employeeProfileItem = getEmployeeProfileInPacket.getEmployeeItem();

			if (employeeProfileItem != null) {
				sign = employeeProfileItem.getSign();
			}
			LogFactory.d(TAG, "WorkSign --------->uid=" + uid + "  sign="
					+ sign);

			innerContactWorkSignMap.put(uid, sign);// / add work sign
		}

		concurrent_outer_loading_count--;

		LogFactory.e(TAG, "Outer Loaded cid=" + getEmployeeProfileInPacket.getCid()
				+ ",employeeProfileItem = , concurrent_outer_loading_count:"
				+ concurrent_outer_loading_count + ",seq:"
				+ getEmployeeProfileInPacket.getSequence());

		if(outer_loadinfo_map.containsKey(getEmployeeProfileInPacket.getUid()))
		{
			outer_loadinfo_map.put(getEmployeeProfileInPacket.getUid(), LOAD_STAT.LOADED);
		}
		else
		{
			int uid = outer_loadinfo_seq_map.get(getEmployeeProfileInPacket.get_header_seq());
			outer_loadinfo_map.put(uid, LOAD_STAT.LOADED);
		}
		
		int send_cnt = do_concurrent_outer_info_req();

		if (send_cnt == 0) {
			// 如果发出的请求为0，则代表所有的deptid都已经发出，此时可以检查deptinfo请求是否已经全部完成
			// curPos_InnerWorkSign = 0;
			// has got all sign
			check_InnerContact_WorkSign_req_finish();
		}

		/*
		 * if (curPos_InnerWorkSign < innerContactIdList.size()) {
		 * doRequestInnerContactWorkSign
		 * (EngineConst.cId,innerContactIdList.get(curPos_InnerWorkSign++)); }
		 * else { curPos_InnerWorkSign = 0; // //has got all sign
		 * updateGetInnerContactWorkSignState(); }
		 */
	}

	private int check_InnerContact_WorkSign_req_finish() {
		// 开始并发请求deptuids
		int init_cnt = concurrent_outer_loading_count;
		for (int i = 0; i < this.innerContactIdList.size()
				&& concurrent_outer_loading_count < MAX_REQUEST_CONCURRENT_INNER_NUMBER; i++) {
			if (outer_loadinfo_map.get(innerContactIdList.get(i)) == LOAD_STAT.NOT_LOAD
					|| outer_loadinfo_map.get(innerContactIdList.get(i)) == LOAD_STAT.LOADING) {

				return 0;
			}
		}

		// 所有deptinfo请求均已完成，此处清除请求中使用到的各个map
		outer_loadinfo_map.clear();
		outer_loadinfo_seq_map.clear();

		LogFactory.e("check_InnerContact_WorkSign_req_finish", "over");

		updateGetInnerContactWorkSignState();

		return 1;
	}

	/**
	 * response Inner GroupInfo
	 * 
	 * @param command
	 */
	private void responseInnerGroupInfo(short command) {

		LogFactory.d(TAG, "responseInnerGroupInfo ................");

		ContactorGroupInPacket innerGroupInPacket = (ContactorGroupInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (innerGroupInPacket == null)
			return;
		short commandRet = innerGroupInPacket.getRet();

		if (commandRet == 0) {

			byte endflag = innerGroupInPacket.getEndflag();

			int[] tempGroupId = innerGroupInPacket.getGroup_id();
			String[] tempGroupName = innerGroupInPacket.getGroup_name();

			for (int i = 0; i < tempGroupName.length; i++) {
				LogFactory.d(TAG, "GroupId = " + tempGroupId[i]
						+ "  GroupName =" + tempGroupName[i]);

				innerGroupIdMap.put(tempGroupId[i], new InnerContactorItem(
						tempGroupId[i], tempGroupName[i]));
			}

			if (endflag == 1) {
				updateGetInnerGroupInfoState();
			}
		}
	}

	// private int curPos_InnerWorkSign = 0;
	/** inner Contact Id List */
	private ArrayList<Integer> innerContactIdList = new ArrayList<Integer>();

	/**
	 * response InnerContactId
	 * 
	 * @param command
	 */
	private void responseInnerContactId(short command) {

		LogFactory.d(TAG, "responseInnerContactId ................");

		InnerContactorListInPacket innerContactorList = (InnerContactorListInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (innerContactorList == null)
			return;
		int commandRet = innerContactorList.getRet();

		byte endflag = innerContactorList.getEndflag();

		if (commandRet == 0) {
			int[] tempInnerGroupId = innerContactorList.getGroup_id();
			int[] tempContactorId = innerContactorList.getContactor_uid();
			// //当group下没有联系人
			if (tempInnerGroupId != null && tempContactorId != null) {

				// //Add contact id to the corresponding GroupId
				for (int i = 0; i < tempContactorId.length; i++) {

					LogFactory.d(TAG, "InnerGroupId = " + tempInnerGroupId[i]
							+ "  ContactId = " + tempContactorId[i]);

					ArrayList<Integer> groupContactIdList = innerGroupContactMap
							.get(tempInnerGroupId[i]);
					if (groupContactIdList == null) {
						groupContactIdList = new ArrayList<Integer>();
					}
					groupContactIdList.add(tempContactorId[i]);

					innerGroupContactMap.put(tempInnerGroupId[i],
							groupContactIdList);
				}
			}
		}

		if (1 == endflag) {
			LogFactory.d(TAG, "内部联系人ContactorID数据,请求完毕！");
			updateGetInnerContactIdState();
		}
	}

	/**
	 * response InnerContactUC
	 * 
	 * @param command
	 */
	private void responseInnerContactUC(short command) {

		LogFactory.d(TAG, "responseInnerContactUC ................");

		ContactorGroupUCInPacket listinPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (listinPacket == null)
			return;
		// /get local innerGroupListUC
		innerGroupListUC = (Integer) PreferenceManager.get(Globe.SP_FILE,
				new Object[] { INNER_GROUP_LIST_UC, innerGroupListUC });
		int curInnerGroupListUC = listinPacket.getUnContactorGroupListUC();

		LogFactory.d(TAG, "curInnerGroupListUC = " + curInnerGroupListUC);

		if (curInnerGroupListUC != innerGroupListUC) {
			// inner Contact need update
			innerGroupListUC = curInnerGroupListUC;
			doRequestInnerContactId();
		} else {
			// /inner Contact needn't update , get data from local
			innerContactNeedUpdate = false;
			// //1- get local date
			innerGroupContactMap.clear();
			try {
				mGlobal.imoStorage
						.getAllInnerContactListInfo(innerGroupContactMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateGetInnerContactIdState();
		}
	}

	/**
	 * 1-响应InnerGroupUC 和本地数据比较
	 * 
	 * @param command
	 */
	private void responseInnerGroupUC(short command) {

		LogFactory.d(TAG, "responseInnerGroupUC ................");

		ContactorGroupUCInPacket inPacket = (ContactorGroupUCInPacket) IMOApp.getDataEngine()
				.getInPacketByCommand(command);
		if (inPacket == null)
			return;
		// 本地innerGroupUC
		innerGroupUC = (Integer) PreferenceManager.get(Globe.SP_FILE,
				new Object[] { INNER_GROUP_UC, innerGroupUC });

		int curInnerGroupUC = inPacket.getUnContactorGroupListUC();

		LogFactory.d(TAG, "curInnerGroupUC = " + curInnerGroupUC);

		if (curInnerGroupUC != innerGroupUC) {
			// /Group info need update
			innerGroupUC = curInnerGroupUC;
			doRequestInnerGroupInfo();
		} else {
			innerGroupInfoNeedUpdate = false;
			// /GroupInfo needn't update , get data from local
			innerGroupIdMap.clear();
			try {
				mGlobal.imoStorage.getInnerGroupInfo(innerGroupIdMap);
			} catch (Exception e) {
				e.printStackTrace();
			}// // 添加数据库接口：获得所有的InnerGroupInfo
			updateGetInnerGroupInfoState();
		}
	}

	// [联系人]
	public static String INNER_GROUP_UC = "innergroupuc";
	public static String INNER_GROUP_LIST_UC = "innnergrouplistuc";
	public static String OUTER_GROUP_UC = "outergroupuc";
	public static String OUTER_GROUP_LIST_UC = "outergrouplistuc";

	/**
	 * 执行 数据保存到本地
	 */
	private boolean doSaveData2Local() {

		// /保存数据
		boolean save2DbResult = save2DB();

		// / When DB save success!
		if (save2DbResult) {

			// 1- save innerGroupUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					INNER_GROUP_UC, innerGroupUC });
			// 2- save innerContactUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					INNER_GROUP_LIST_UC, innerGroupListUC });
			// 3- save outerGroupUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					OUTER_GROUP_UC, outerGroupUC });
			// 4- save outerContactUC
			PreferenceManager.save(Globe.SP_FILE, new Object[] {
					OUTER_GROUP_LIST_UC, outerGroupListUC });

		}
		return save2DbResult;
	}

	/**
	 * save to db
	 * 
	 * @return
	 */
	private boolean save2DB() {

		try {
//			if (innerGroupInfoNeedUpdate) {
//				// Map<Integer,InnerContactorItem> innerGroupIdMap
//				mGlobal.imoStorage.putInnerGroupInfo(innerGroupIdMap);
//			}
//
//			if (innerContactNeedUpdate) {
//				// Map<Integer,ArrayList<Integer>> innerGroupContactMap
//				mGlobal.imoStorage
//						.putInnerContactListInfo(innerGroupContactMap);
//			}
//
//			if (outerGroupInfoNeedUpdate) {
//				// Map<Integer,OuterContactorItem> outerGroupIdMap
//				mGlobal.imoStorage.putOuterGroupInfo(outerGroupIdMap);
//			}
//
//			if (outerContactNeedUpdate) {
//				// Map<Integer,ArrayList<OuterContactItem>> outerGroupContactMap
//				// HashMap<Integer, OuterContactBasicInfo>
//				// outerContactInfoMap///昨天忘了写的
//				// HashMap<Integer, String> outerContactCorpMap ///公司信息
//				mGlobal.imoStorage
//						.putOuterContactListInfo(outerGroupContactMap);
//				mGlobal.imoStorage
//						.putOuterContactBasicInfo(outerContactInfoMap);
//				mGlobal.imoStorage.putOuterCorpInfo(outerContactCorpMap);
//			}

			IMOApp.imoStorage.putContactAndGroupInfo(innerGroupInfoNeedUpdate,
					innerContactNeedUpdate, outerGroupInfoNeedUpdate,
					outerContactNeedUpdate, innerGroupIdMap,
					innerGroupContactMap, outerGroupIdMap,
					outerGroupContactMap, outerContactInfoMap,
					outerContactCorpMap);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 更新getInnerGroupInfo的状态。
	 */
	private void updateGetInnerGroupInfoState() {
		hasGetInnerGroupInfo = true;
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);

		// doRequestInnerContactUC();
	}

	/**
	 * 更新getInnerConnectId的状态。
	 */

	private void updateGetInnerContactIdState() {
		hasGetInnerContactId = true;
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);

		// /// begin request worksign
		// curPos_InnerWorkSign = 0;
		innerContactIdList.clear();
		for (Integer groupId : innerGroupContactMap.keySet()) {
			// ///如果group中为空的情况，上面处理过了，这边不可能出现了
			innerContactIdList.addAll(innerGroupContactMap.get(groupId));
		}

		// 初始化out_loadinfo_map的状态
		for (int i = 0; i < innerContactIdList.size(); i++) {
			outer_loadinfo_map.put(innerContactIdList.get(i),
					LOAD_STAT.NOT_LOAD);
		}
		// [End]

		// innerContactIdList for worksign

		if (innerContactIdList.size() > 0) {
			// 请求InnerContactWorkSign
			// doRequestInnerContactWorkSign(EngineConst.cId,innerContactIdList.get(curPos_InnerWorkSign++));

			// 修改为并发请求
			do_concurrent_outer_info_req();
		} else {
			updateGetInnerContactWorkSignState();
		}
	}

	private int MAX_REQUEST_CONCURRENT_INNER_NUMBER = 100;
	private int concurrent_outer_loading_count = 0;
	// outer_loadinfo_map 标识当前所有deptid的deptinfo的获取情况，有未载入，载入中，已载入3个状态
	private Map<Integer, Integer> outer_loadinfo_map = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> outer_loadinfo_seq_map = new HashMap<Integer, Integer>();
	
	private int do_concurrent_outer_info_req() {
		// 开始并发请求deptinfo
		int init_cnt = concurrent_outer_loading_count;
		for (int i = 0; i < this.innerContactIdList.size()
				&& concurrent_outer_loading_count < MAX_REQUEST_CONCURRENT_INNER_NUMBER; i++) {

			if (outer_loadinfo_map.get(innerContactIdList.get(i)) == LOAD_STAT.NOT_LOAD) {
				doRequestInnerContactWorkSign(EngineConst.cId,
						innerContactIdList.get(i));
				outer_loadinfo_map.put(innerContactIdList.get(i),
						LOAD_STAT.LOADING);
				concurrent_outer_loading_count++;
			} else {
			}
		}

		return concurrent_outer_loading_count - init_cnt;
	}

	/**
	 * 更新getInnerConnectWorkSign的状态。
	 */
	private void updateGetInnerContactWorkSignState() {
		hasGetInnerContactWorkSign = true;
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * 更新getOuterGroupInfo的状态。
	 */
	private void updateGetOuterGroupInfoState() {
		hasGetOuterGroupInfo = true;
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
		doRequestOuterContactUC();
	}

	/**
	 * 更新getOuterConnectId的状态。
	 */
	private void updateGetOuterContactIdState() {
		hasGetOuterContactId = true;
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * 更新getOuterCropName的状态。
	 */
	private void updateGetOuterCropNameState() {
		hasGetOuterCropName = true;
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * 更新getOuterContactBasicInfoState的状态。
	 */
	private void updateGetOuterContactBasicInfoState() {
		hasGetOuterContactBasicInfoState = true;
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	/**
	 * 更新getOuterState--State的状态。
	 */
	private void updateGetOuterContactState_State() {
		hasGetOuterContactState_State = true;
		// //update global data
		mGlobal.outerUserStateMap = (HashMap<Integer, Integer>) outerId_State_Map
				.clone();
		LogFactory.e("OuterState", "======================================");
		// for (Integer uid :mGlobal.outerUserStateMap.keySet()) {
		// LogFactory.d("OuterState", "uid= "+ uid +"  state = " +
		// mGlobal.outerUserStateMap.get(uid)+"" );
		// }
		// /// send msg to refresh contact
		mContactHandler.sendEmptyMessage(TYPE_REFRESH_TREE);
	}

	// ==================================End=====================================================
	// ==========================================================================================
}

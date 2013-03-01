package com.imo.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Message;
import android.widget.Toast;

import com.imo.R;
import com.imo.dataengine.DataEngine;
import com.imo.db.sql.IMOStorage;
import com.imo.module.MainActivityGroup;
import com.imo.module.contact.OuterContactBasicInfo;
import com.imo.module.contact.OuterContactItem;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.StateHandle;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;
import com.imo.module.welcome.WelcomeActivity;
import com.imo.network.net.EngineConst;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.DeptMaskItem;
import com.imo.network.packages.EmployeeInfoItem;
import com.imo.network.packages.InnerContactorItem;
import com.imo.network.packages.OuterContactorItem;
import com.imo.util.Functions;
import com.imo.util.LogFactory;
import com.imo.util.NoticeManager;

/**
 * 全局控制类 <br>
 * 生命周期的维持范围：应用程序启动后创建，直到应用程序退出
 * 
 * @author CaixiaoLong
 * 
 */
public class IMOApp extends Application {

	/**
	 * 当前项目的模式： true-开发模式 ； false-发布模式
	 */
//	private boolean mAppMode = false;
	private boolean mAppMode = true;

	public Activity mLastActivity = null;

	private static IMOApp appInstance;

	public boolean hasLoadingAllData = false;

	// public boolean reLoginTimeOut = false;

	/** 已经接包失败 */
	public boolean hasPackageFailed = false;

	public int radius = 4;

	public HashMap<Integer, Node> mNodeMap = new HashMap<Integer, Node>();

	/**
	 * 脱机摘要全局数据:key= UId ; Value = CId
	 */
	public HashMap<Integer, Integer> mOfflineMsgMap = new HashMap<Integer, Integer>();
	
	/**
	 * 隐藏部门数组
	 */
	public ArrayList<Integer> hide_dept_ids = new ArrayList<Integer>();
	
	/**
	 * 后台消息读取用户id缓存
	 */
	public static List<Integer> sendMsgUserId = new ArrayList<Integer>();
	
	/**
	 * 程序退出标记
	 */
	private boolean isAppExit = false;

	public boolean isAppExit() {
		return isAppExit;
	}

	public void closeAllActivity() {
		Intent intent = new Intent();
		intent.setAction("kill_me");
		sendBroadcast(intent);
	}

	public void setAppExit(boolean isAppExit) {
		this.isAppExit = isAppExit;
	}

	public double mScale = 1; // //手机尺寸放缩

	public boolean hasRunInBackground = false;// ///已经点击了Home，后台运行状态。

	// private static ArrayList<AbsBaseActivity> mAbsBaseActivityList;
	private static ArrayList<Activity> mAbsBaseActivityList;

	private ArrayList<IMOTask> mIMOTaskList;

	public NotificationManager notificationManager;

	public static IMOStorage imoStorage;

	/**
	 * 接收群消息
	 */
	private boolean receiverGroup;

	public MainActivityGroup mainActivityGroup = null;

	public static DataEngine getDataEngine() {
		return DataEngine.getInstance();
	}

	/**
	 * 根据Uid查找用户是否在线 for 对话界面
	 * 
	 * @param uid
	 * @return
	 */
	public Boolean isOnlineFindByUid(int uid) {

		Integer state = 0;
		if (userStateMap != null) {
			state = userStateMap.get(uid);
			if (state == null && outerUserStateMap != null) {
				state = outerUserStateMap.get(uid);
			}
		}
		if (state != null) {
			state = state & 0x000000FF;
		} else {
			state = 0;
		}
		return (state != 0);
	}

	public void updateStateForGetMSG(int uid) {
		Integer state = 0;
		if (userStateMap != null) {
			state = userStateMap.get(uid);
			if (state == null) {
				if (outerUserStateMap != null) {
					state = outerUserStateMap.get(uid);
					if (state == null) {
						return;
					}
				}
			}
		}

		state = state & 0x000000FF;

		if (state == 0) {
			StateHandle.getInstance().updateUI(uid, 1);
		}
		return;
	}

	public Integer getUserStateByUid(int uid) {
		Integer state = 0;
		if (userStateMap != null) {
			state = userStateMap.get(uid);
			if (state == null) {
				if (outerUserStateMap != null) {
					state = outerUserStateMap.get(uid);
					if (state == null) {
						state = 0;
					}
				}
			}
		}

		return state;
	}

	// private EmployeeInfoItem loginEmployee = null;
	//
	// public EmployeeInfoItem getLoginEmployee(){
	// if (loginEmployee == null) {
	//
	// if (deptUserInfoMap != null) {
	// for (Integer deptId: deptUserInfoMap.keySet()) {
	// EmployeeInfoItem temp = deptUserInfoMap.get(deptId).get(EngineConst.uId);
	// if (temp != null) {
	// loginEmployee = temp;
	// return loginEmployee;
	// }
	// }
	// }
	// }
	//
	// return loginEmployee;
	// }

	// ============组织结构数据源构建： begin===========================================
	public int[] deptid = null;
	public int[] dept_uc = null;
	public int[] dept_user_uc = null;

	public HashMap<Integer, DeptMaskItem> deptInfoMap = null;

	public HashMap<Integer, int[]> deptUserIdsMap;
	public HashMap<Integer, int[]> deptUserNextSiblingMap;
	public HashMap<Integer, HashMap<Integer, Integer>> deptUserSiblingMap;

	public HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap;

	public HashMap<Integer, Integer> userStateMap;

	public void updateData(int[] deptid, int[] dept_uc, int[] dept_user_uc,
			HashMap<Integer, DeptMaskItem> deptInfoMap,
			HashMap<Integer, int[]> deptUserIdsMap,
			HashMap<Integer, int[]> deptUserNextSiblingMap,
			HashMap<Integer, HashMap<Integer, Integer>> deptUserSiblingMap,
			HashMap<Integer, HashMap<Integer, EmployeeInfoItem>> deptUserInfoMap) {
		this.deptid = deptid;
		this.dept_uc = dept_uc;
		this.dept_user_uc = dept_user_uc;

		this.deptInfoMap = deptInfoMap;

		this.deptUserIdsMap = deptUserIdsMap;
		this.deptUserNextSiblingMap = deptUserNextSiblingMap;
		this.deptUserSiblingMap = deptUserSiblingMap;

		LogFactory.d("3333", "deptUserIdsMap size = " + deptUserIdsMap.size());

		this.deptUserInfoMap = deptUserInfoMap;
	}

	public void updateStateMap(HashMap<Integer, Integer> userStateMap) {
		this.userStateMap = userStateMap;
	}

	private int[] mAllCidArray = null;
	private int[] mAllUidArray = null;

	/**
	 * 初始化全局Cid Uid 列表
	 */
	private void initUidCidArray() {

		int size = 0;

		try {
			size = mNodeMap.keySet().size();
		} catch (Exception e) {
		}

		mAllCidArray = new int[size];
		mAllUidArray = new int[size];
		int i = 0;
		for (Integer uid : mNodeMap.keySet()) {
			mAllUidArray[i] = uid;
			mAllCidArray[i] = mNodeMap.get(uid).getCid();
			i++;
		}
	}

	/**
	 * 获得所有的Cid
	 * 
	 * @return
	 */
	public int[] getAllCidArray() {
		if (mAllCidArray == null) {
			initUidCidArray();
		}
		return mAllCidArray;
	}

	/**
	 * 获得所有的Uid
	 * 
	 * @return
	 */
	public int[] getAllUidArray() {
		if (mAllUidArray == null) {
			initUidCidArray();
		}
		return mAllUidArray;
	}

	// ============组织结构数据源构建： end===========================================

	/**
	 * 注销的时候 清空全局数据
	 */
	public void resetGlobalData() {
		deptid = null;
		dept_uc = null;
		dept_user_uc = null;

		if (deptInfoMap != null) {
			deptInfoMap.clear();
			deptInfoMap = null;
		}

		if (deptUserIdsMap != null) {
			deptUserIdsMap.clear();
			deptUserIdsMap = null;
		}

		if (deptUserNextSiblingMap != null) {
			deptUserNextSiblingMap.clear();
			deptUserNextSiblingMap = null;
		}

		if (deptUserSiblingMap != null) {
			deptUserSiblingMap.clear();
			deptUserSiblingMap = null;
		}

		if (deptUserInfoMap != null) {
			deptUserInfoMap.clear();
			deptUserInfoMap = null;
		}

		if (userStateMap != null) {
			userStateMap.clear();
			userStateMap = null;
		}

		if (innerGroupIdMap != null) {
			innerGroupIdMap.clear();
		}

		if (innerGroupContactMap != null) {
			innerGroupContactMap.clear();
		}

		if (innerContactWorkSignMap != null) {
			innerContactWorkSignMap.clear();
		}

		if (outerGroupIdMap != null) {
			outerGroupIdMap.clear();
			outerGroupIdMap = null;
		}

		if (outerGroupContactMap != null) {
			outerGroupContactMap.clear();
		}

		if (outerContactCorpMap != null) {
			outerContactCorpMap.clear();
			outerContactCorpMap = null;
		}

		if (outerContactInfoMap != null) {
			outerContactInfoMap.clear();
			outerContactInfoMap = null;
		}

		if (outerUserStateMap != null) {
			outerUserStateMap.clear();
		}

		if (mNodeMap != null) {
			mNodeMap.clear();
		}

		Globe.bm_head = null;

		// fengxiaowei
		Globe.corp = null;
		Globe.myself = null;
		hide_dept_ids.clear();
		Globe.employeeProfileItems.clear();
		Globe.corpMaskItems.clear();
		Globe.customList.clear();
		System.gc();
	}

	// ============联系人数据源构建： begin=========================================
	/** 内部联系人Group Map */
	public HashMap<Integer, InnerContactorItem> innerGroupIdMap;

	/** 内部联系人Group_ContactId_Map */
	public HashMap<Integer, ArrayList<Integer>> innerGroupContactMap = new HashMap<Integer, ArrayList<Integer>>();
	/** 内部联系人工作签名映射 */
	public HashMap<Integer, String> innerContactWorkSignMap = new HashMap<Integer, String>();
	/** 外部联系人Group Map */
	public HashMap<Integer, OuterContactorItem> outerGroupIdMap;

	/** 外部联系人Group_Contact_Map */
	public HashMap<Integer, ArrayList<OuterContactItem>> outerGroupContactMap = new HashMap<Integer, ArrayList<OuterContactItem>>();
	/** 外部联系人：公司信息 */
	public HashMap<Integer, String> outerContactCorpMap;
	/** 外部联系人：员工信息Map */
	public HashMap<Integer, OuterContactBasicInfo> outerContactInfoMap;

	/** 外部联系人状态信息 */
	public HashMap<Integer, Integer> outerUserStateMap = new HashMap<Integer, Integer>();

	/**
	 * 得到内部联系人的 EmployeeInfo Map : 在进入该界面的时候构建
	 */
	public HashMap<Integer, ArrayList<EmployeeInfoItem>> mInnerGroupContactMap = null;

	/** 联系人根节点 */
	public Node rootNodeContact = new Node(new NodeData("联系人", ""));

	/** 内部联系人根节点 */
	public Node rootNodeInner = new Node(new NodeData("内部联系人", ""));

	/** 外部联系人根节点 */
	public Node rootNodeOuter = new Node(new NodeData("外部联系人", ""));

	/**
	 * 更新外部联系人使用到的数据
	 * 
	 * @param innerGroupIdMap
	 * @param innerGroupContactMap
	 * @param outerGroupIdMap
	 * @param outerGroupContactMap
	 */
	public void updateContactData(
			HashMap<Integer, InnerContactorItem> innerGroupIdMap,
			HashMap<Integer, ArrayList<Integer>> innerGroupContactMap,
			HashMap<Integer, String> innerContactWorkSignMap,
			HashMap<Integer, OuterContactorItem> outerGroupIdMap,
			HashMap<Integer, ArrayList<OuterContactItem>> outerGroupContactMap,
			HashMap<Integer, String> outerContactCorpMap,
			HashMap<Integer, OuterContactBasicInfo> outerContactInfoMap) {
		this.innerGroupIdMap = innerGroupIdMap;
		this.innerGroupContactMap = innerGroupContactMap;
		this.innerContactWorkSignMap = innerContactWorkSignMap;

		this.outerGroupIdMap = outerGroupIdMap;
		this.outerGroupContactMap = outerGroupContactMap;

		this.outerContactCorpMap = outerContactCorpMap;
		this.outerContactInfoMap = outerContactInfoMap;
	}

	// ============联系人数据源构建： end===========================================
	ConnectionChangeReceiver connectionChangeReceiver = null;

	// ShutdownReceiver shutdownReceiver = null;

	@Override
	public void onCreate() {
		super.onCreate();
		System.setProperty("java.net.preferIPv6Addresses", "false");
		appInstance = this;
		mAbsBaseActivityList = new ArrayList<Activity>();
		// mAbsBaseActivityList = new ArrayList<AbsBaseActivity>();
		mIMOTaskList = new ArrayList<IMOTask>();
		imoStorage = IMOStorage.getInstance(this);
		// 启动网络通信服务
		// startService(new Intent(getApplicationContext(), AppService.class));
		bindService(new Intent(getApplicationContext(), AppService.class),
				null, Context.BIND_AUTO_CREATE);

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		connectionChangeReceiver = new ConnectionChangeReceiver();
		registerReceiver(connectionChangeReceiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		// shutdownReceiver = new ShutdownReceiver();
		// registerReceiver(shutdownReceiver, new
		// IntentFilter("android.intent.action.ACTION_SHUTDOWN"));

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();

		clearAllNotice();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();

		clearAllNotice();
	}

	public NotificationManager getNoticeManager() {
		return notificationManager;
	}

	public boolean isReceiverGroup() {
		return receiverGroup;
	}

	public void setReceiverGroup(boolean receiverGroup) {
		this.receiverGroup = receiverGroup;
	}

	// =============★获得Application对象★===============
	/**
	 * 获得Application对象
	 * 
	 * @return
	 */
	public static IMOApp getApp() {
		return appInstance;
	}

	/**
	 * 获得APP的当前模式<br>
	 * 
	 * @return true:开发模式， false:发布模式
	 */
	public boolean getAppMode() {
		return mAppMode;
	}

	public void setAppMode(boolean mode) {
		mAppMode = mode;
	}

	// =====================================================
	/**
	 * 添加AbsBaseActivity
	 * 
	 * @param ac
	 */
	public void addAbsBaseActivity(Activity ac) {
		// public void addAbsBaseActivity(AbsBaseActivity ac){
		if (!mAbsBaseActivityList.contains(ac)) {
			mAbsBaseActivityList.add(ac);
		}
		// if (findActivityByName(ac.getClass().getSimpleName())== null) {
		// mAbsBaseActivityList.add(ac);
		// }

	}

	/**
	 * 根据Activity的名称在列表中查找AbsBaseActivity
	 * 
	 * @param activityName
	 *            AbsBaseActivity名称。
	 * @return
	 */
	public Activity findActivityByName(String activityName) {
		// public AbsBaseActivity findActivityByName(String activityName){
		Activity tempActivity = null;
		// AbsBaseActivity tempActivity = null;
		for (Activity ac : mAbsBaseActivityList) {
			// for (AbsBaseActivity ac : mAbsBaseActivityList) {
			if (ac.getClass().getSimpleName().equals(activityName)) {
				tempActivity = ac;
				break;
			}
		}
		return tempActivity;
	}

	/**
	 * 销毁之后所有activity
	 * 
	 * @param activityName
	 */
	public void destoryActivityFrom(String activityName) {
		Activity ac = null;
		for (int i = mAbsBaseActivityList.size() - 1; i >= 0; i--) {
			ac = mAbsBaseActivityList.get(i);

			if (ac.getClass().getSimpleName().equals(activityName)) {
				ac.finish();
				break;
			} else {
				ac.finish();
			}
		}
	}

	/**
	 * 从列表中删除指定的AbsBaseActivity
	 * 
	 * @param ac
	 */
	public void removeAbsBaseActivity(Activity ac) {
		// public void removeAbsBaseActivity(AbsBaseActivity ac){
		mAbsBaseActivityList.remove(ac);
	}

	/**
	 * 删除所有的AbsBaseActivity
	 */
	public void removeAllAbsBaseActivity() {
		mAbsBaseActivityList.clear();
	}

	/**
	 * 区分于注销操作
	 */
	public Boolean isExitApp = null;

	public void turn2LoginForLogout() {
		// 需要删除前面堆栈中的Activity
		// destoryActivityFrom("LoginActivity");
		EngineConst.isNetworkValid = true;

		EngineConst.isReloginSuccess = true;
		clearAllNotice();
		closeAllActivity();
		AppService.getService().reset();
		IMOApp.getApp().resetGlobalData();
		
		DataEngine.getInstance().clearInQueue();
		DataEngine.getInstance().clearSendQueue();
		DataEngine.getInstance().clearTimeoutQueue();

		LoginActivity.launch(mLastActivity);
	}

	/**
	 * 注销失败提示
	 */
	public void showLogoutFailed() {
		Toast.makeText(mLastActivity,
				getResources().getString(R.string.logoutError), 1).show();
	}

	/**
	 * 网络中断,重新登录提示
	 */
	public void showLoadingFailed() {
		Toast.makeText(mLastActivity,
				getResources().getString(R.string.loadingError), 1).show();
	}

	/**
	 * 退出应用程序
	 */
	public void exitApp() {
		setAppExit(true);

		LogFactory.d("ExitApp", "mAbsBaseActivityList size = "
				+ mAbsBaseActivityList.size());
		String TIME = "timexxx";
		LogFactory.d(TIME, "Time-1:" + System.currentTimeMillis());
		imoStorage.close();

		// for (Activity ac : mAbsBaseActivityList) {
		// // for (AbsBaseActivity ac : mAbsBaseActivityList) {
		// ac.finish();
		// }

		// for (int i = mAbsBaseActivityList.size() - 1; i >= 0; i--) {
		// mAbsBaseActivityList.get(i).finish();
		// }

		LogFactory.d(TIME, "Time0:" + System.currentTimeMillis());
		AppService.getService().reset();
		// resetGlobalData();
		LogFactory.d(TIME, "Time1:" + System.currentTimeMillis());
		closeAllActivity();
		LogFactory.d(TIME, "Time2:" + System.currentTimeMillis());
		notificationManager.cancel(NoticeManager.TYPE_NOTICE_APP_ONGOING);
		LogFactory.d(TIME, "Time3:" + System.currentTimeMillis());
		notificationManager.cancel(NoticeManager.TYPE_NOTICE_NEW_NEWS);
		LogFactory.d(TIME, "Time4:" + System.currentTimeMillis());
		// stopService(new Intent(getApplicationContext(), AppService.class));
		LogFactory.d(TIME, "Time5:" + System.currentTimeMillis());

		// AppService.appService = null;
		// ActivityManager am = (ActivityManager)getSystemService
		// (Context.ACTIVITY_SERVICE);
		// am.restartPackage(getPackageName());

		if (connectionChangeReceiver != null) {
			try {
				unregisterReceiver(connectionChangeReceiver);
			} catch (Exception e) {
			}
		}
		LogFactory.d(TIME, "Time6:" + System.currentTimeMillis());
		android.os.Process.killProcess(android.os.Process.myPid());
		LogFactory.d(TIME, "Time7:" + System.currentTimeMillis());
	}

	/**
	 * 清空通知
	 */
	public void clearAllNotice() {
		NoticeManager.count = 0;
		notificationManager.cancel(NoticeManager.TYPE_NOTICE_APP_ONGOING);
		notificationManager.cancel(NoticeManager.TYPE_NOTICE_NEW_NEWS);
	}

	// ====================================任务操作============================================
	/**
	 * 获得当前的任务列表
	 * 
	 * @return
	 */
	public ArrayList<IMOTask> getTaskList() {
		return mIMOTaskList;
	}

	/**
	 * 添加新的
	 * 
	 * @param IMOTask
	 */
	public void addNewTask(IMOTask IMOTask) {
		mIMOTaskList.add(IMOTask);
	}

	/**
	 * 从任务列表中移除指定的任务
	 * 
	 * @param IMOTask
	 */
	public void removeTask(IMOTask IMOTask) {
		mIMOTaskList.remove(IMOTask);
	}

	/**
	 * 删除任务列表中所有任务
	 */
	public void removeAll() {
		mIMOTaskList.clear();
	}

	/**
	 * 重启程序：依靠程序中的关键变量，如果被第三方软件做了内存处理，则此关键变量为null，此时，进行程序重启
	 */
	public boolean reStartProgram(Activity activity) {
		if (Globe.SP_FILE == null || Globe.SP_FILE.equals("")) {
			clearAllNotice();
			Intent intent = new Intent(activity, WelcomeActivity.class);
			activity.startActivity(intent);
			activity.finish();
			// android.os.Process.killProcess(android.os.Process.myPid());
			return true;
		} else {
			return false;
		}
	}
}

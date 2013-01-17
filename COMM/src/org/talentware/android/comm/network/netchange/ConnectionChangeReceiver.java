package org.talentware.android.comm.network.netchange;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	private final String TAG = "NetworkStateReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
//		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) && EngineConst.isLoginSuccess && !(IMOApp.getApp().mLastActivity instanceof LoginActivity) && !(IMOApp.getApp().mLastActivity instanceof WelcomeActivity)) {
//
//			LogFactory.e(TAG, "network state changed.");
//
//			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
//
//			if (pingAddress()) {
//				EngineConst.isNetworkValid = true;
//				DataEngine.getInstance().reconnectServer();
//			} else {
//				EngineConst.isNetworkValid = false;
//
//				LogFactory.e("NetChanged", "old connection id = " + EngineConst.IMO_CONNECTION_ID);
//				EngineConst.isReloginSuccess = false;
//
//				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
//			}
//
//			LogFactory.e("NetChanged", "" + EngineConst.isNetworkValid);
//
//			Message msg = new Message();
//			msg.obj = EngineConst.isNetworkValid;
//			final Activity lastActivity = IMOApp.getApp().mLastActivity;
//			if (lastActivity instanceof OrganizeActivity) {
//				if (OrganizeActivity.getActivity() != null) {
//					OrganizeActivity.getActivity().titleBarHandler.sendMessage(msg);
//				}
//			} else if (lastActivity instanceof ContactActivity) {
//				if (ContactActivity.getActivity() != null) {
//					ContactActivity.getActivity().titleBarHandler.sendMessage(msg);
//				}
//			} else if (lastActivity instanceof FirstLoadingActivity || lastActivity instanceof NormalLoadingActivity) {
//				IMOApp.getApp().showLoadingFailed();
//				EngineConst.isLoginSuccess = false;
//
//				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
//
//				IMOApp.getApp().turn2LoginForLogout();
//			}
//		}
	}

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
//		if (info != null) {
//			for (int i = 0; i < info.length; i++) {
//				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//					EngineConst.isStartRelogin = true;
//					return true;
//				} else {
//					EngineConst.isStartRelogin = false;
//				}
//			}
//		}
		return false;
	}

	// [Ping网络是否可用]
	public static boolean pingAddress() {

		int timeOut = 5 * 1000; // 定义超时，表明该时间内连不上即认定为不可达，超时值不能太小
		boolean status = false;
//		try {
//			// ping功能
//			status = InetAddress.getByName(EngineConst.hostIP).isReachable(timeOut);
//			LogFactory.e("pingAddress", "Status = " + status);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated
//			e.printStackTrace();
//		}

		return status;
	}

	// [End]

	public static boolean checkNet() {
//		if (isNetworkAvailable(IMOApp.getApp()))
//			if (pingAddress()) {
//				return true;
//			}
		return false;
	}

}

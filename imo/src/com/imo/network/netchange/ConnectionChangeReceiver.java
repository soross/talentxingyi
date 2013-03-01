package com.imo.network.netchange;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.util.Log;

import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.AppService;
import com.imo.global.IMOApp;
import com.imo.module.contact.ContactActivity;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.FirstLoadingActivity;
import com.imo.module.organize.NormalLoadingActivity;
import com.imo.module.organize.OrganizeActivity;
import com.imo.module.welcome.WelcomeActivity;
import com.imo.network.net.EngineConst;
import com.imo.network.net.TCPConnection;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.ReloginOutPacket;
import com.imo.util.LogFactory;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	private final String TAG = "NetworkStateReceiver";
	private String oldConnectionID = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		// if( intent.getAction() != Intent.ACTION_BOOT_COMPLETED ){
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())
				&& EngineConst.isLoginSuccess
				&& !(IMOApp.getApp().mLastActivity instanceof LoginActivity)
				&& !(IMOApp.getApp().mLastActivity instanceof WelcomeActivity)) {

			LogFactory.e(TAG, "network state changed.");

			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
			
			if (/* isNetworkAvailable(context)&&* */pingAddress()) {
				EngineConst.isNetworkValid = true;
				DataEngine.getInstance().reconnectServer();
			} else {
				EngineConst.isNetworkValid = false;

				LogFactory.e("NetChanged", "old connection id = " + EngineConst.IMO_CONNECTION_ID);
				EngineConst.isReloginSuccess = false;
				
				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
			}

			LogFactory.e("NetChanged", "" + EngineConst.isNetworkValid);

			Message msg = new Message();
			msg.obj = EngineConst.isNetworkValid;
			if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
				if (OrganizeActivity.getActivity() != null) {
					OrganizeActivity.getActivity().titleBarHandler.sendMessage(msg);
				}
			} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
				if (ContactActivity.getActivity() != null) {
					ContactActivity.getActivity().titleBarHandler.sendMessage(msg);
				}
			} else if (IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity) {
				IMOApp.getApp().showLoadingFailed();
				EngineConst.isLoginSuccess = false;
				
				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
				
				IMOApp.getApp().turn2LoginForLogout();
			} else if (IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity) {
				IMOApp.getApp().showLoadingFailed();
				EngineConst.isLoginSuccess = false;
				
				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
				
				IMOApp.getApp().turn2LoginForLogout();
			}
		}
	}

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					EngineConst.isStartRelogin = true;
					return true;
				} else {
					EngineConst.isStartRelogin = false;
				}
			}
		}
		return false;
	}

	// [Ping网络是否可用]
	public static boolean pingAddress() {
		
		 int timeOut = 5*1000; // 定义超时，表明该时间内连不上即认定为不可达，超时值不能太小
		 boolean status = false; 
		 try { 
			 // ping功能 
			 //status =InetAddress.getByName(EngineConst.hostName).isReachable(timeOut);
			 status = InetAddress.getByName(EngineConst.hostIP).isReachable(timeOut);
			 LogFactory.e("pingAddress", "Status = " + status); 
		 }catch(UnknownHostException e) 
		 { 
			 // TODO Auto-generated catch block
			 e.printStackTrace(); 
		 } catch (IOException e) { 
			 // TODO Auto-generated
			 e.printStackTrace(); 
		 }
		  
		 return status;

		/*
		boolean mPingIpAddrResult = false;
		try {
			// This is hardcoded IP addr. This is for testing purposes.
			// We would need to get rid of this before release.
			Log.e("ping", "pingAddress 1");
			Process p = Runtime.getRuntime().exec(
					"ping -c 1 -w 3 " + EngineConst.hostIP);
			Log.e("ping", "pingAddress 2");
			int status = p.waitFor();
			Log.e("ping", "pingAddress 3");
			if (p != null)
				p.destroy();
			Log.e("ping", "pingAddress 4");
			if (status == 0) {
				mPingIpAddrResult = true;

				Log.e("pingAddress", "Status = " + mPingIpAddrResult);

				return mPingIpAddrResult;
			} else {
				mPingIpAddrResult = false;

				Log.e("pingAddress", "Status = " + mPingIpAddrResult);

				return mPingIpAddrResult;
			}
		} catch (IOException e) {
			mPingIpAddrResult = false;

			Log.e("pingAddress", "Status = " + mPingIpAddrResult);

			return mPingIpAddrResult;
		} catch (InterruptedException e) {
			mPingIpAddrResult = false;

			Log.e("pingAddress", "Status = " + mPingIpAddrResult);

			return mPingIpAddrResult;
		}
		**/
	}

	// [End]

	public static boolean checkNet() {
		if (isNetworkAvailable(IMOApp.getApp()))
			if (pingAddress()) {
				return true;
			}
		return false;
	}

}

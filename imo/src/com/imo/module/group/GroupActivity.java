package com.imo.module.group;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.login.LoginActivity;
import com.imo.network.net.EngineConst;
import com.imo.network.packages.IMOCommand;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.LogFactory;
import com.imo.util.NoticeManager;
import com.imo.util.PreferenceManager;

/**
 * 群
 * 
 * @author CaixiaoLong
 * 
 */
public class GroupActivity extends AbsBaseActivityNetListener {

	private String TAG = "GroupActivity";

	public static void launch(Context c) {
		Intent intent = new Intent(c, GroupActivity.class);
		c.startActivity(intent);
	}
	
    @Override
    protected void onPause() {
    	LogFactory.e("GroupActivity", "onPause");
    	
    	super.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	LogFactory.e("GroupActivity", "onDestroy");
    	
    	super.onDestroy();
    }
    
    @Override
    protected void onStop() {
    	LogFactory.e("GroupActivity", "onStop");
    	super.onStop();
    }
    
	@Override
	protected void installViews() {
		setContentView(R.layout.group_activity);
		
	}
	
	protected boolean needObserver() {
		return false;
	}

	@Override
	protected void registerEvents() {
		
		mTitleBar.initDefaultTitleBar(null,
				getResources().getString(R.string.group),null);
	}

	@Override
	public void refresh(Object param) {

	}

	@Override
	public boolean CanAcceptHttpPacket() {
		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		switch (command) {
		/**
		 * 2.5.1 获取用户多人会话列表UC  = 9109
		 */
		case IMOCommand.IMO_GET_USER_NGROUP_LIST_UC:  return true;

		/**
		 * 2.5.2 新批量获取多人会话信息  = 9110;
		 */
		case IMOCommand.IMO_GET_USER_NGROUP_LIST_UC_NEW:  return true;

		/**
		 * 2.5.4 登录多人会话  = 9036;
		 */
		case IMOCommand.IMO_CLIENT_LOGIN_NGROUPD:  return true;

		/**
		 * 2.5.6 同意加入多人会话  = 9003;
		 */
		case IMOCommand.IMO_AGREE_JOIN_NGROUP:  return true;

		/**
		 * 2.5.7 多人会话 通知其他用户 有新人加入  = 9005;
		 */
		case IMOCommand.IMO_NOTICE_USER_JOIN_NGROUP:  return true;

		/**
		 * 2.5.9 多人会话公告更新通知  = 9007;
		 */
		case IMOCommand.IMO_NGROUP_MODIFY_ANNOUNCEMENT_NOTICE:  return true;

		/**
		 * 2.5.10 获取用户多人会话信息= 9008;
		 */
		case IMOCommand.IMO_GET_NGROUP_INFO :  return true;

		/**
		 * 2.5.11 获取用户多人会话列表= 9009;
		 */
		case IMOCommand.IMO_GET_USER_NGROUP_LIST :  return true;

		/**
		 * 2.5.12 获取多人会话用户列表= 9010;
		 */
		case IMOCommand.IMO_GET_NGROUP_USER_LIST :  return true;

		/**
		 * 2.5.13 获取多人会话用户状态 = 9011;
		 */
		case IMOCommand.IMO_GET_NGROUP_USERS_STATUS:  return true;

		/**
		 * 2.5.14 多人会话聊天= 9012;
		 */
		case IMOCommand.IMO_NGOURP_CHAT :  return true;

		/**
		 * 2.5.15 新消息到达通知= 9013;
		 */
		case IMOCommand.IMO_NEW_MSG_NOTICE_NGROUP :  return true;

		/**
		 * 2.5.16 状态改变通知 = 9031;
		 */
		case IMOCommand.IMO_NGROUP_USER_CHANGE_STATUS_NOTICE:  return true;

		/**
		 * 2.5.17 退出多人会话= 9015;
		 */
		case IMOCommand.IMO_EXIT_NGROUP :  return true;

		/**
		 * 2.5.18 退出多人会话通知= 9016;
		 */
		case IMOCommand.IMO_EXIT_NGROUP_NOTICE_USERS :  return true;

		/**
		 * 2.5.20 多人会话标题修改通知 = 9018;
		 */
		case IMOCommand.IMO_MODIFY_NGROUP_NAME_NOTICE:  return true;

		/**
		 * 2.5.22 踢人通知 = 9022;
		 */
		case IMOCommand.IMO_NGROUP_KICK_USER_NOTICE:  return true;

		/**
		 * 2.5.23 拉脱机多人会话聊天消息 = 9023;
		 */
		case IMOCommand.IMO_GET_OFFLINE_NGROUP_CHAT_MSG:  return true;

		/**
		 * 2.5.26 多人会话操作通知消息= 9000;
		 */
		case IMOCommand.IMO_NGROUP_NOTICE :  return true;
		default: return false;
		}
	}
	

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {
		
		super.NotifyPacketArrived(aConnectionId,command);
		
		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId))
			
			switch (command) {
			
			}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (isFinishing()) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			
			LogFactory.d(TAG, "KEYCODE_BACK has clicked .");
			boolean keepOnline = (Boolean) PreferenceManager.get(Globe.SP_FILE,
					new Object[] { LoginActivity.LOGIN_KEEPONLINE, false });
			if (!keepOnline){
				DialogFactory.promptExit(mContext).show();
			}else {
				IMOApp.getApp().hasRunInBackground = true;
				NoticeManager.updateRecoverAppNotice(notificationManager);
				Functions.backToDesk(this);
			}
			
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		return super.onKeyDown(keyCode, event);
	}

}

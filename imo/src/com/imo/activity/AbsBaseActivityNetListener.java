package com.imo.activity;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.imo.R;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.db.entity.MessageInfo;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.MainActivityGroup;
import com.imo.module.config.SystemSetActivity;
import com.imo.module.config.WorkSignActivity;
import com.imo.module.contact.ContactActivity;
import com.imo.module.dialogue.DialogueActivity;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.module.dialogue.recent.RecentContactInfo;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.FirstLoadingActivity;
import com.imo.module.organize.NormalLoadingActivity;
import com.imo.module.organize.OrganizeActivity;
import com.imo.module.organize.struct.Node;
import com.imo.module.welcome.WelcomeActivity;
import com.imo.network.Encrypt.StringUtils;
import com.imo.network.Log.CustomExceptionHandler;
import com.imo.network.Observer.PacketsObserver;
import com.imo.network.net.EngineConst;
import com.imo.network.net.NIOThread;
import com.imo.network.net.TCPConnection;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.CommonOutPacket;
import com.imo.network.packages.GetEmployeesStatusOutPacket;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.LoginInPacket;
import com.imo.network.packages.LoginOutPacket;
import com.imo.network.packages.ReloginInPacket;
import com.imo.network.packages.ReportErrorInfoOutPacket;
import com.imo.network.packages.SendMsgAckOutPacket;
import com.imo.network.packages.SendMsgInPacket;
import com.imo.network.packages.ServerPromptInPacket;
import com.imo.network.packages.UpdateVersionInPacket;
import com.imo.network.packages.UpdateVersionOutPacket;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.LogFactory;
import com.imo.util.PreferenceManager;
import com.imo.util.SystemInfoManager;
import com.imo.util.TimeOutHandle;

/**
 * 网络通信抽象基础类 <br>
 * 使用流程: ====================================== <br>
 * <br>
 * 1-继承AbsBaseActivityNetListener <br>
 * 2-实际逻辑事件中调用 super.mNIOThread.send 发请求包【包单独编写】 <br>
 * 3-NotifyPacketArrived方法中逻辑：【1）buffer--->obj 2)调用super.sendMessage(type,obj)】 <br>
 * 4-refresh方法中更新UI的数据操作， <br>
 * 
 * @author CaixiaoLong
 * 
 */
public abstract class AbsBaseActivityNetListener extends AbsBaseActivity implements PacketsObserver {

	/**
	 * 数据请求UI更新的Type类型
	 */
	protected final int NotifyPacketTimeOut = 1;
	protected final int NotifyPacketProgress = 2;
	protected final int NotifyPacketFailed = 3;
	protected final int NotifyHttpPacketArrived = 4;
	protected final int NotifyPacketArrived = 5;
	protected final int ErrorRemoteData = 6;

	protected final int SEND_MESSAGE = 7;
	protected final int SERVER_PROMPT = 8;

	protected final int FORCE_EXIT = -1;

	// 标示为新连接对象
	protected static boolean isNewConnection = false;

	private Dialog absDialog;
	private boolean canShow = true;

	protected Handler mParentHandler;

	/**
	 * 已经显示了一个对话框
	 */
	public boolean hasShowTimeOutDialog = false;
	protected TimeOutHandle mTimeOutHandle = null;
	protected NIOThread mNIOThread = AppService.getService() != null ? AppService.getService().getNIOThreadInstance() : null;

	// private TCPConnection tcpConnection = getTcpConnection();

	protected TCPConnection getTcpConnection() {
		return AppService.getService() != null ? AppService.getService().getTcpConnection() : null;
	}

	protected void resetConnection() {

		if (AppService.getService() != null) {
			AppService.getService().reset();
			mNIOThread = AppService.getService().getNIOThreadInstance();
			// tcpConnection = AppService.getService().getTcpConnection();
			getTcpConnection();
		}

		EngineConst.isStartRelogin = true;
		LogFactory.e("AbsBaseActivity", "isStartRelogin :" + EngineConst.isStartRelogin);
	}

	static {
		if (AppService.getService() == null || AppService.getService().getCurNIOThreadInstance() == null) {
			isNewConnection = true;
		} else {
			isNewConnection = false;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!(this instanceof WelcomeActivity))
			if (IMOApp.getApp().reStartProgram(this)) {
				return;
			}

		DataEngine.getInstance().addToObserverList(this);

		registerFinishBroadcast();

		mTimeOutHandle = new TimeOutHandle();

		handlerMessage();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTimeOutHandle != null) {
			mTimeOutHandle.dismissDialog();
		}
	}

	private void registerFinishBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("kill_me");
		registerReceiver(mFinishBroadcast, filter);
	}

	protected BroadcastReceiver mFinishBroadcast = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				unregisterReceiver(this);
			} catch (Exception e) {}
			((Activity) context).finish();
		}
	};

	protected boolean needNewConnection() {
		return true;
	}

	@Override
	public boolean CanAcceptHttpPacket() {

		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		return true;
	}

	@Override
	public void NotifyPacketArrived(String aConnectionId, short command) {
		switch (command) {
			case IMOCommand.IMO_SEND_MESSAGE: {
				// if (!(mGlobal.mLastActivity instanceof FirstLoadingActivity
				// ||
				// mGlobal.mLastActivity instanceof NormalLoadingActivity)) {
				// getChatMessage(command);
				// }

				LogFactory.e("NotifyPacketArrived", "" + command);
				sendMessage(SEND_MESSAGE, command);
				break;
			}

			case IMOCommand.IMO_SERVER_PROMPT: {
				sendMessage(SERVER_PROMPT, command);
				break;
			}
			case IMOCommand.IMO_FORCE_EXIT: {
				sendMessage(FORCE_EXIT, "");
				LogFactory.d("xxx1", "----IMO_FORCE_EXIT");
				break;
			}
			case IMOCommand.IMO_UPDATE_VERSION: {
				responseAppVersion(command);
				break;
			}
			case IMOCommand.IMO_GET_RELOGIN: {
				reLogin(command);

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
				}
				break;
			}
			case IMOCommand.IMO_LOGIN: {
				LogFactory.e("AbsBaseActivity", "IMO_LOGIN");
				if (this instanceof LoginActivity || this instanceof WelcomeActivity) {} else {
					analysisLoginPacket(command);
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
					}
				}
				break;
			}
			default:
				break;
		}

	}

	/**
	 * 发送AllEmployeeInfo请求包
	 * 
	 * @param aContactorsNum
	 *        请求的员工数量 个数
	 * @param aContactorCidArray
	 *        内部联系人 、外部联系人 CidArray
	 * @param aContactorUidArray
	 *        用户 UIdArray
	 * 
	 */
	protected void doEmployeeState(int aContactorsNum, int[] aContactorCidArray, int[] aContactorUidArray) {

		LogFactory.d("EmployeeState", "doRequestEmployeeState ");

		ByteBuffer bufferBody = GetEmployeesStatusOutPacket.GenerateEmployeesStatusBody(aContactorsNum, aContactorCidArray, aContactorUidArray);
		GetEmployeesStatusOutPacket outPacket = new GetEmployeesStatusOutPacket(bufferBody, IMOCommand.IMO_GET_EMPLOYEE_STATUS, EngineConst.cId, EngineConst.uId);

		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
	}

	private void analysisLoginPacket(short command) {
		LoginInPacket loginInPacket = (LoginInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (loginInPacket == null)
			return;
		ByteBuffer buffer = loginInPacket.getBodyBuffer();
		short ret = buffer.getShort();
		try {
			if (ret == 0) {// 正常登录
				Globe.customList.clear();
				// 解析登陆结果
				EngineConst.cId = buffer.getInt();
				EngineConst.uId = buffer.getInt();
				// 初始化SP存储文件名
				Globe.SP_FILE = "SP" + EngineConst.uId;
				// 初始化公司logo存储文件名
				Globe.corpLogo_file = "corpLogo" + EngineConst.cId;
				Globe.selfHeadPic_file = "selfHeadPic" + EngineConst.uId;

				LogFactory.e("old sessionKey", "" + EngineConst.sessionKey);
				buffer.get(EngineConst.sessionKey);
				LogFactory.e("new sessionKey", "" + EngineConst.sessionKey);

				short num = buffer.getShort();
				Globe.ips = new String[num];
				Globe.ports = new int[num];
				for (int i = 0; i < num; i++) {
					long ip = buffer.getInt() & 0xFFFFFFFFL;
					int port = buffer.getShort();
					Globe.ips[i] = ip / 256 / 256 / 256 % 256 + "." + ip / 256 / 256 % 256 + "." + ip / 256 % 256 + "." + ip % 256;
					Globe.ports[i] = port;
				}
				buffer.clear();

				EngineConst.isLoginSuccess = true;

				DataEngine.getInstance().setLogicStatus(LOGICSTATUS.LOGINOVER);

				// /发送状态包
				if (OrganizeActivity.getActivity() != null) {
					OrganizeActivity.getActivity().parent_UpdateState();
				} else {
					LogFactory.e("error", "OrganizeActivity not launch");
				}

				// [发送状态改变通知]
				CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_UPDATE_STATUS, EngineConst.cId, EngineConst.uId);
				mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
				// [End]
				// [请求脱机摘要]
				if (RecentContactActivity.getActivity() != null)
					RecentContactActivity.getActivity().doRequestOfflineMSGProfile();

				EngineConst.isReloginSuccess = true;
				EngineConst.isNetworkValid = true;
			} else if (ret == 115) {
				int temp_nameLen = buffer.getInt();
				byte[] temp_name_buffer = new byte[temp_nameLen];
				buffer.get(temp_name_buffer);
				String preUpdate_build = StringUtils.UNICODE_TO_UTF8(temp_name_buffer);
				Message msg1 = new Message();
				msg1.obj = preUpdate_build;
				mUpdateAppHandler.sendMessage(msg1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void reLogin(short command) {
		// LogFactory.e("old TCPConnection Tag:", "" + tcpConnection.getId());
		// tcpConnection = getTcpConnection();
		// LogFactory.e("new TCPConnection Tag:", "" + tcpConnection.getId());

		ReloginInPacket reloginInPacket = (ReloginInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		short ret = reloginInPacket.getRet();

		if (ret == 17) {// 重新登录
			String loginName = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
					LoginActivity.LOGIN_NAME, new String()
			});
			String loginPwd = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
					LoginActivity.LOGIN_PWD, new String()
			});
			String[] nameAndDomain = loginName.split("@");
			EngineConst.password = loginPwd;
			// 0代表公司账号登录，1代表域名登录
			byte flag = 0;
			if (nameAndDomain[1].indexOf(".") > -1)
				flag = 1;
			ByteBuffer bodyBuffer = LoginOutPacket.GenerateLoginBody(flag, nameAndDomain[1], nameAndDomain[0].toLowerCase(), nameAndDomain[0].toLowerCase());
			LoginOutPacket out = new LoginOutPacket(bodyBuffer, IMOCommand.IMO_LOGIN, 0, 0);
			IMOApp.getDataEngine().addToObserverList(this);
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, out, false);
		} else {
			EngineConst.isReloginSuccess = true;
			EngineConst.isNetworkValid = true;
			// [发送状态改变通知]
			CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_UPDATE_STATUS, EngineConst.cId, EngineConst.uId);
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
			// [End]
		}
	}

	// protected boolean isExitApp = false;
	/**
	 * 发送下线请求
	 */
	public void requestLogOut(boolean isExitApp) {

		LogFactory.d("AbsBase", "-------->requestLogOut  ............isExitApp " + isExitApp);

		mGlobal.isExitApp = isExitApp;

		if (!EngineConst.isNetworkValid) {
			// Toast.makeText(mContext,getResources().getString(R.string.logoutError),Toast.LENGTH_SHORT).show();
			responseLogout((short) 0, (short) 0);
		} else {
			LogFactory.e("Logout time1:", "" + System.currentTimeMillis());
			CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_EXIT, EngineConst.cId, EngineConst.uId);

			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
			LogFactory.e("Logout time2:", "" + System.currentTimeMillis());

			mExitHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}

	private boolean hasExecute = false;

	private Handler mExitHandler = new Handler() {
		public void handleMessage(Message msg) {
			if ((!isFinishing()) && hasExecute == false) {
				responseLogout((short) 0, (short) 0);
			}
		};
	};

	@Override
	public void NotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {

		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId))

			switch (command) {
				case IMOCommand.IMO_EXIT:
					LogFactory.e("Logout time3:", "" + System.currentTimeMillis());
					sendMessage(NotifyPacketProgress, "");
					break;
				case IMOCommand.ERROR_REMOTE_DATA:
					LogFactory.e("ERROR_REMOTE_DATA", "ERROR_REMOTE_DATA------------------->");
					sendMessage(ErrorRemoteData, "");
					break;
				default:
					break;
			}

	}

	public void responseLogout(short aTotalLen, short aSendedLen) {

		hasExecute = true;

		if (aTotalLen == aSendedLen) {

			EngineConst.isLoginSuccess = false;

			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			LogFactory.d("AbsBase", "-------->Logout  ............isExitApp " + mGlobal.isExitApp);

			if (mGlobal.isExitApp != null && mGlobal.isExitApp) {
				IMOApp.getApp().exitApp();
			} else {
				LogFactory.e("Logout time4:", "" + System.currentTimeMillis());
				IMOApp.getApp().turn2LoginForLogout();
				LogFactory.e("Logout time5:", "" + System.currentTimeMillis());
			}
		}
	}

	private void getChatMessage(short command) {
		SendMsgInPacket sendMsgInPacket = (SendMsgInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
		if (sendMsgInPacket != null) {
			int fromCid = sendMsgInPacket.getFromcid();
			int fromUid = sendMsgInPacket.getFromuid(); // 发消息的用户id
			int server_msg_id = sendMsgInPacket.getServer_msg_id();
			String message = sendMsgInPacket.getMsg();

			LogFactory.d("冯小卫", "消息" + server_msg_id);
			if (Globe.customList.contains(server_msg_id)) {
				LogFactory.d("冯小卫", "来了重复消息");
				sendACK(fromCid, fromUid, server_msg_id);
				return;
			}

			Globe.customList.add(server_msg_id);

			try {
				if (!Functions.checkMessage(message)) {// 纯自定义图片，不存储，不显示
					// 发送ACK
					LogFactory.d("冯小卫", "纯自定义图片");
					sendACK(fromCid, fromUid, server_msg_id);
					return;
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			// 判断是否已读，逻辑如下：如果聊天界面是当前显示的界面，则认为已读，否者认为未读。
			int isRead = MessageInfo.MessageInfo_UnRead;
			// if ("DialogueActivity".equals(Functions.getCurrentActivity()))

			// 增加接收到用户信息后的用户id缓存功能
			if (!mGlobal.sendMsgUserId.contains(fromUid)) {
				mGlobal.sendMsgUserId.add(fromUid);
				System.out.println("当前增加的聊天用户id-------" + mGlobal.sendMsgUserId);
			}

			if (mGlobal.mLastActivity instanceof DialogueActivity) {
				if (DialogueActivity.instance.aboutUid != fromUid) {
					isRead = MessageInfo.MessageInfo_UnRead;
				} else {
					isRead = MessageInfo.MessageInfo_Readed;
				}
			}
			// 存放到数据库
			try {
				MessageInfo msg = new MessageInfo(0, fromUid, "", EngineConst.uId, "", Functions.getDate(), Functions.getTime(), message, MessageInfo.MessageInfo_From, MessageInfo.MessageInfo_MsgId, isRead, MessageInfo.MessageInfo_UnFailed);
				IMOApp.imoStorage.addMessage(msg);
				LogFactory.d("冯小卫", "存放到数据库");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			// 消息的声音和震动提示设计上，开始分为前后台，现在不分了。
			// if (IMOApp.getApp().hasRunInBackground) {
			//
			// } else {
			// Functions.msgNotification();
			// }
			Functions.msgNotification(); // 铃声振动

			// /来消息的时候更新状态
			IMOApp.getApp().updateStateForGetMSG(fromUid);

			// 通知界面显示
			// if ("DialogueActivity".equals(Functions.getCurrentActivity())) {
			if (mGlobal.mLastActivity instanceof DialogueActivity) {
				DialogueActivity.instance.getMessage(command);
				LogFactory.d("冯小卫", "通知界面显示1");
				if (DialogueActivity.instance.aboutUid != fromUid) {
					if (MainActivityGroup.getActivityGroup() != null) {
						LogFactory.d("冯小卫", "通知界面显示2");
						MainActivityGroup.getActivityGroup().mHandler.sendEmptyMessage(1);
					}
				}
			}

			LogFactory.d("冯小卫", "通知界面显示3");
			updateRecentMap(fromCid, fromUid, getNameByUid(fromUid), message, Functions.getFullTime(), isRead);
			// Functions.getDate() + " " + Functions.getTime(),
			// 发送ACK
			sendACK(fromCid, fromUid, server_msg_id);
		}
	}

	protected String getNameByUid(int uid) {
		String name = "";
		if (mGlobal.mNodeMap != null) {
			Node tempNode = mGlobal.mNodeMap.get(uid);
			name = tempNode != null ? tempNode.getNodeData().nodeName : "";
		}
		return name;

	}

	private void updateRecentMap(int fromCid, int fromUid, String name, String message, String time, int isRead) {
		LogFactory.d("xxx", "updateRecentMap in the DialogueActivity...");

		RecentContactInfo info = new RecentContactInfo(fromCid, fromUid, name, message, time, isRead == 0 ? 1 : 0, RecentContactInfo.NORMAL_TYPE);

		try {
			// RecentContactInfo temp_info = new
			// RecentContactInfo(info.getCid(), info.getUid(), info.getName(),
			// info.getInfo(), info.getTime(), info.getCount(),
			// RecentContactInfo.NORMAL_TYPE);
			IMOApp.imoStorage.updateRecentContact(info, info.getTime().split(" ")[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (RecentContactActivity.getActivity() != null) {
			RecentContactActivity.getActivity().addOrUpdate(info);
		} else {
			LogFactory.d("xxx", "RecentContactActivity has not lunched ....");
		}
	}

	private void sendACK(int fromCid, int fromUid, int server_msg_id) {
		ByteBuffer aBody = SendMsgAckOutPacket.GenerateMsgTextAckBody(fromCid, fromUid, server_msg_id);
		SendMsgAckOutPacket sendMsgAckOutPacket = new SendMsgAckOutPacket(aBody, IMOCommand.IMO_SEND_MESSAGE_ACK, EngineConst.cId, EngineConst.uId);
		mNIOThread.send(EngineConst.IMO_CONNECTION_ID, sendMsgAckOutPacket, false);
	}

	/**
	 * 发送消息
	 * 
	 * @param type
	 * @param obj
	 */
	protected void sendMessage(int type, Object obj) {

		Message msg = new Message();

		msg.what = type;
		msg.obj = obj;
		mParentHandler.sendMessage(msg);
	}

	protected void handlerMessage() {

		mParentHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
					case FORCE_EXIT:
						// mGlobal.destoryActivityFrom("LoginActivity");
						// AppService.getService().reset();
						// LoginActivity.launch(mContext);
						EngineConst.isLoginSuccess = false;

						DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

						IMOApp.getApp().turn2LoginForLogout();
						break;
					case NotifyPacketTimeOut:
						handleTimeOut(msg);
						break;
					case NotifyPacketFailed:
						handleFailed(msg);
						break;
					case NotifyPacketProgress: {
						if (mGlobal.mLastActivity instanceof OrganizeActivity || mGlobal.mLastActivity instanceof ContactActivity) {
							if ((!isFinishing()) && hasExecute == false) {
								responseLogout((short) 0, (short) 0);
							}
						}
						break;
					}
					case NotifyHttpPacketArrived:
					case NotifyPacketArrived:
						LogFactory.e("AbsBaseActivityNetListener", "1----" + "packet arrived");
						refresh(msg);
						break;
					case ErrorRemoteData:
						// android.os.Process.killProcess(android.os.Process.myPid());
						IMOApp.getApp().exitApp();
						break;
					case SEND_MESSAGE:
						if (!(mGlobal.mLastActivity instanceof FirstLoadingActivity || mGlobal.mLastActivity instanceof NormalLoadingActivity)) {
							getChatMessage(IMOCommand.IMO_SEND_MESSAGE);
						}
						break;
					case SERVER_PROMPT:
						handleServerPrompt(msg);
						break;

					default:
						break;
				}

			}

			private void handleServerPrompt(Message msg) {
				short command = (Short) msg.obj;
				ServerPromptInPacket serverPromptInPacket = (ServerPromptInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
				int unType = serverPromptInPacket.getUnType();
				LogFactory.d("", "服务器维护号：" + unType);
				if (unType == 0 || unType == 1) {// 服务器维护
					LogFactory.d("", "收到服务器维护信息");
					Dialog dialog = DialogFactory.serverPromptDialog(IMOApp.getApp().mLastActivity);
					if (dialog != null)
						dialog.show();
				}

			}

			/**
			 * 处理失败操作
			 * 
			 * @param msg
			 */
			private void handleFailed(Message msg) {

				if (IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity || IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity) {
					IMOApp.getApp().showLoadingFailed();
					EngineConst.isLoginSuccess = false;

					DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

					IMOApp.getApp().turn2LoginForLogout();
					return;
				}
				// ==========头像变灰=============================
				// /标识网络不可用
				EngineConst.isNetworkValid = false;
				Message failedMSG = new Message();
				// failedMSG.obj = false;
				if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
					if (OrganizeActivity.getActivity() != null) {
						OrganizeActivity.getActivity().titleBarHandler.sendMessage(failedMSG);
					}
				} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
					if (ContactActivity.getActivity() != null) {
						ContactActivity.getActivity().titleBarHandler.sendMessage(failedMSG);
					}
				}
				// =======================================
				if (IMOApp.getApp().hasRunInBackground)
					return;

				// 没有网络的时候，发送数据，通信模块直接抛出通信失败
				if (aErrorCode == IMOCommand.ERROR_CONNECTION_BROKEN) {
					Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
					return;
				}

				if (!canShow)
					return;

				if (EngineConst.isLoginSuccess)
					return;

				canShow = false;

				absDialog = DialogFactory.alertDialog(/* mContext* */getParent(), resources.getString(R.string.warn), (String) msg.obj, new String[] {
					resources.getString(R.string.ok)
				}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mContext instanceof LoginActivity) {
							AppService.getService().reset();
							LoginActivity.launch(mContext);
						}
					}
				});
				absDialog.show();
				absDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						canShow = true;
					}
				});
			}
		};

		// msg = new Message();
	}

	/**
	 * 超时处理
	 * 
	 * @param msg
	 */
	private void handleTimeOut(Message msg) {
		short command = (Short) msg.obj;
		// if (IMOApp.getApp().mLastActivity instanceof RecentContactActivity
		// ||IMOApp.getApp().mLastActivity instanceof OrganizeActivity
		// ||IMOApp.getApp().mLastActivity instanceof ContactActivity
		// ||IMOApp.getApp().mLastActivity instanceof GroupActivity
		// ){
		// Toast.makeText(mContext, "数据请求超时！", Toast.LENGTH_SHORT).show();
		// }else{
		// mTimeOutHandle.showDialog(mContext);
		// }

		// /重登陆只有超时
		if (command == IMOCommand.IMO_GET_RELOGIN) {
			// //再次打开失败接受大门
			IMOApp.getApp().hasPackageFailed = false;
			Toast.makeText(mContext, "网络异常，请您重新登录。", Toast.LENGTH_SHORT).show();
			EngineConst.isLoginSuccess = false;
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			// 重登陆超时，设置状态机为DISCONNECTED
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			if (IMOApp.getApp().mLastActivity instanceof LoginActivity) {
				((LoginActivity) IMOApp.getApp().mLastActivity).stopLogin();
			} else if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
				if (OrganizeActivity.getActivity() != null) {
					OrganizeActivity.getActivity().titleBarHandler.sendEmptyMessage(0);
				}
			} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
				if (ContactActivity.getActivity() != null) {
					ContactActivity.getActivity().titleBarHandler.sendEmptyMessage(0);
				}
			}
			// 原先的逻辑：重登陆超时，跳转到登陆界面
			// IMOApp.getApp().turn2LoginForLogout();
			return;
		}

		if (command == IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE || command == IMOCommand.IMO_GET_EMPLOYEE_STATUS) {
			// /脱机摘要、获取用户状态不提示网络超时
			return;
		} else if (command == IMOCommand.IMO_GET_EMPLOYEE_PROFILE && !(IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity || IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity)) {
			// /获取名片，不提示网络超时
			return;
		} else if (command == IMOCommand.IMO_EDIT_PROFILE) {
			// /编辑工作签名超时
			if (IMOApp.getApp().mLastActivity instanceof WorkSignActivity) {
				WorkSignActivity.getActivity().dismissDialog();
				Toast.makeText(mContext, mContext.getString(R.string.notifypacket_timeout), Toast.LENGTH_SHORT).show();
			}
			return;
		}

		mTimeOutHandle.showDialog(mContext);
	}

	/*
	 * protected Handler mFailedHandler = new Handler() { public void
	 * handleMessage(Message msg) { IMOApp.getApp().showLoadingFailed();
	 * EngineConst.isLoginSuccess = false;
	 * IMOApp.getApp().turn2LoginForLogout(); //
	 * if(IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity) // { //
	 * LoginActivity.launch(IMOApp.getApp().mLastActivity); //
	 * IMOApp.getApp().mLastActivity.finish(); // } else
	 * if(IMOApp.getApp().mLastActivity instanceof // NormalLoadingActivity) {
	 * // LoginActivity.launch(IMOApp.getApp().mLastActivity); //
	 * IMOApp.getApp().mLastActivity.finish(); // } }; };
	 */

	// private void initDialog() {
	// absDialog = DialogFactory.alertDialog(mContext,
	// resources.getString(R.string.warn),
	// resources.getString(R.string.notifypacket_timeout),
	// new String[] { resources.getString(R.string.ok) },
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// if (mContext instanceof LoginActivity) {
	// AppService.getService().reset();
	// LoginActivity.launch(mContext);
	// }
	// }
	// });
	// }

	/**
	 * 打开软件的时候发送CrashMSG信息
	 */
	public void sendCrashMSG() {

		String mobileInfo = CustomExceptionHandler.getMobileInfo();
		String versioninfo = CustomExceptionHandler.getVersionInfo();
		String errorinfo = CustomExceptionHandler.checkLogSize();

		LogFactory.e("mobileInfo", mobileInfo);
		LogFactory.e("versioninfo", versioninfo);

		if (errorinfo != null) {
			LogFactory.e("errorinfo", errorinfo);

			JSONObject message = new JSONObject();
			try {
				message.put("deviceType", mobileInfo);
				message.put("platForm", versioninfo);

				int len = message.toString().length();

				if (errorinfo.length() > CustomExceptionHandler.MEMORY_LOG_FILE_MAX_SIZE - len - 64)
					message.put("errorInfo", errorinfo.substring(errorinfo.length() - (CustomExceptionHandler.MEMORY_LOG_FILE_MAX_SIZE - len - 64)));
				else {
					message.put("errorInfo", errorinfo);
				}

				LogFactory.e("message error", "" + message.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			ByteBuffer dataBuf = ReportErrorInfoOutPacket.GenerateErrorInfoBody(message.toString());
			ReportErrorInfoOutPacket out = new ReportErrorInfoOutPacket(dataBuf, IMOCommand.IMO_REPORT_ERROR, EngineConst.cId, EngineConst.uId);
			AppService.getService().getNIOThreadInstance().send(EngineConst.IMO_CONNECTION_ID, out, false);
		}
	}

	// /* TCP Packet arrived **/
	// @Override
	// public void NotifyPacketArrived(String aConnectionId, short command){
	//
	// }
	//
	// /* HTTP Packet arrived **/
	// @Override
	// public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer
	// aBuffer){
	//
	// }

	// /* Packet progress **/
	// @Override
	// public void NotifyPacketProgress(String aConnectionId, short
	// command,short aTotalLen,short aSendedLen){
	//
	// }

	/* Packet Timeout * */
	@Override
	public void NotifyPacketTimeOut(String aConnectionId, short aErrorCode) {
		LogFactory.e("Timeout", "timeout packet command = " + aErrorCode);
		// if (aErrorCode == IMOCommand.IMO_GET_RELOGIN) {
		// mGlobal.reLoginTimeOut = true;
		// IMOApp.getApp().destoryActivityFrom("LoginActivity");
		// AppService.getService().reset();
		// LoginActivity.launch(mContext);
		// return;
		// }
		sendMessage(NotifyPacketTimeOut, aErrorCode);
	}

	private short aErrorCode;

	/* Packet Failed * */
	@Override
	public void NotifyPacketFailed(String aConnectionId, short aErrorCode) {

		this.aErrorCode = aErrorCode;
		LogFactory.d("NotifyPacketFailed", aErrorCode + "1111");

		if (!mGlobal.hasPackageFailed) {
			if (aErrorCode == IMOCommand.ERROR_NETWORK) {

				boolean temp = ConnectionChangeReceiver.checkNet();

				if (temp) {
					mGlobal.hasPackageFailed = true;
					mConnectionHandler.sendEmptyMessage(aErrorCode);
					sendMessage(NotifyPacketFailed, resources.getString(R.string.notifypacket_failed));

					return;
				} else {
					EngineConst.isNetworkValid = false;
					sendMessage(NotifyPacketFailed, resources.getString(R.string.notifypacket_failed));

					return;
				}
			} else {
				sendMessage(NotifyPacketFailed, resources.getString(R.string.notifypacket_failed));
			}
		}

	}

	protected Handler mConnectionHandler = new Handler() {
		public void handleMessage(Message msg) {
			DataEngine.getInstance().reconnectServer();
			mGlobal.hasPackageFailed = false;
		};

	};

	@Override
	public void finish() {
		// if (getTcpConnection() != null) {
		// getTcpConnection().removeFromObserverList(this);
		// }

		IMOApp.getDataEngine().removeFromObserverList(this);

		try {
			unregisterReceiver(mFinishBroadcast);
		} catch (Exception e) {}
		super.finish();
	}

	/**
	 * 版本更新请求
	 */
	protected void doRequestAppVersion() {

		if (mGlobal.mLastActivity instanceof SystemSetActivity) {
			Toast.makeText(mContext, getString(R.string.app_update_loading), Toast.LENGTH_SHORT).show();
		}

		SystemInfoManager info = new SystemInfoManager();

		byte aVersion = info.getVersion();
		short aBuild = info.getBuild();
		byte aDevNo = 1;
		String aDevType = "Android";

		ByteBuffer bufferBody = UpdateVersionOutPacket.GenerateUpdateVersionBody(aVersion, aBuild, aDevNo, aDevType);

		UpdateVersionOutPacket outPacket = new UpdateVersionOutPacket(bufferBody, IMOCommand.IMO_UPDATE_VERSION, EngineConst.cId, EngineConst.uId);

		if (mNIOThread != null) {
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
		}
	}

	/**
	 * 响应 版本更新请求
	 * 
	 * @param command
	 */
	protected void responseAppVersion(short command) {

		UpdateVersionInPacket inPacket = (UpdateVersionInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		LogFactory.d("xxUpdate", "responseAppVersion----------------->");
		LogFactory.d("xxUpdate", inPacket.toString());

		byte flag = inPacket.getFlag();
		LogFactory.d("xxUpdate", flag + "");

		int flag0 = (flag & 0x01);// 最后一位代表示否需要升级， 1：需要 0： 不需要
		int flag1 = (flag & 0x02) >> 1;// 倒数第二位表示是否强制升级，1：强制， 0：不强制
		int flag2 = (flag & 0x04) >> 2;// 倒数第三位表示是否提醒升级，1：提醒， 0：不提醒

		Message msg = new Message();

		if (flag0 == 1 && flag2 == 1) {
			byte version = inPacket.getVersion();
			short build = inPacket.getBuild();
			int devNo = inPacket.getDevNo();
			String info = inPacket.getInfo();
			String downloadURL = inPacket.getDownloadURL();
			msg.obj = downloadURL;
		} else {
			msg.obj = null;
		}

		mUpdateAppHandler.sendMessage(msg);
	}

	public Handler mUpdateAppHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LogFactory.d("xxUpdate", "mUpdateAppHandler-------------> handler message");
			if (mGlobal.mLastActivity instanceof LoginActivity) {
				((LoginActivity) mGlobal.mLastActivity).stopLogin();
			}
			if (mGlobal.mLastActivity instanceof IAppUpdate) {
				((IAppUpdate) mGlobal.mLastActivity).showUpdateDialog(msg);
			}
		};
	};

}

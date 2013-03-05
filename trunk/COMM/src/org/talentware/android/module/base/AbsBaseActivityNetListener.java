package org.talentware.android.module.base;

import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;
import org.talentware.android.R;
import org.talentware.android.comm.dataengine.DataEngine;
import org.talentware.android.comm.dataengine.DataEngine.LOGICSTATUS;
import org.talentware.android.comm.net.EngineConst;
import org.talentware.android.comm.net.NIOThread;
import org.talentware.android.comm.net.TCPConnection;
import org.talentware.android.comm.packet.CommonOutPacket;
import org.talentware.android.comm.packet.command.IMOCommand;
import org.talentware.android.comm.packet.packetsobserver.PacketsObserver;
import org.talentware.android.comm.util.LogFactory;
import org.talentware.android.global.Globe;
import org.talentware.android.global.IMOApp;
import org.talentware.android.service.AppService;
import org.w3c.dom.Node;

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
import android.preference.PreferenceManager;
import android.widget.Toast;

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
	
	protected NIOThread mNIOThread = AppService.getService() != null ? AppService.getService().getNIOThreadInstance() : null;

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

		DataEngine.getInstance().addToObserverList(this);

		registerFinishBroadcast();
		
		handlerMessage();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
			} catch (Exception e) {
			}
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
//		switch (command) {
//		case IMOCommand.IMO_SEND_MESSAGE: {
//			LogFactory.d("NotifyPacketArrived", "" + command);
//			sendMessage(SEND_MESSAGE, command);
//			break;
//		}
//
//		case IMOCommand.IMO_SERVER_PROMPT: {
//			sendMessage(SERVER_PROMPT, command);
//			break;
//		}
//		case IMOCommand.IMO_FORCE_EXIT: {
//			sendMessage(FORCE_EXIT, "");
//			LogFactory.d("xxx1", "----IMO_FORCE_EXIT");
//			break;
//		}
//		case IMOCommand.IMO_GET_RELOGIN: {
//			reLogin(command);
//
//			Message msg = new Message();
//			msg.obj = EngineConst.isNetworkValid;
//			if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
//				if (OrganizeActivity.getActivity() != null) {
//					OrganizeActivity.getActivity().titleBarHandler.sendMessage(msg);
//				}
//			} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
//				if (ContactActivity.getActivity() != null) {
//					ContactActivity.getActivity().titleBarHandler.sendMessage(msg);
//				}
//			}
//			break;
//		}
//		case IMOCommand.IMO_LOGIN: {
//			LogFactory.e("AbsBaseActivity", "IMO_LOGIN");
//			if (this instanceof LoginActivity || this instanceof WelcomeActivity) {
//				// 若是此两界面不作处理，分别交给这两个界面来解析
//			} else {
//				analysisLoginPacket(command);
//				Message msg = new Message();
//				msg.obj = EngineConst.isNetworkValid;
//				if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
//					if (OrganizeActivity.getActivity() != null) {
//						OrganizeActivity.getActivity().titleBarHandler.sendMessage(msg);
//					}
//				} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
//					if (ContactActivity.getActivity() != null) {
//						ContactActivity.getActivity().titleBarHandler.sendMessage(msg);
//					}
//				}
//			}
//			break;
//		}
//		default:
//			break;
//		}

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

//			if (mGlobal.isExitApp != null && mGlobal.isExitApp) {
//				IMOApp.getApp().exitApp();
//			} else {
//				LogFactory.e("Logout time4:", "" + System.currentTimeMillis());
//				IMOApp.getApp().turn2LoginForLogout();
//				LogFactory.e("Logout time5:", "" + System.currentTimeMillis());
//			}
		}
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
					EngineConst.isLoginSuccess = false;
					DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
//					IMOApp.getApp().turn2LoginForLogout();
					break;
				case NotifyPacketTimeOut:
					handleTimeOut(msg);
					break;
				case NotifyPacketFailed:
					handleFailed(msg);
					break;
				case NotifyPacketProgress: {
//					if (mGlobal.mLastActivity instanceof OrganizeActivity || mGlobal.mLastActivity instanceof ContactActivity) {
//						if ((!isFinishing()) && hasExecute == false) {
//							responseLogout((short) 0, (short) 0);
//						}
//					}
					break;
				}
				case NotifyHttpPacketArrived:
				case NotifyPacketArrived:
					LogFactory.e("AbsBaseActivityNetListener", "1----" + "packet arrived");
					refresh(msg);
					break;
				case ErrorRemoteData:
					// android.os.Process.killProcess(android.os.Process.myPid());
//					IMOApp.getApp().exitApp();
					break;
				case SEND_MESSAGE:
//					if (!(mGlobal.mLastActivity instanceof FirstLoadingActivity || mGlobal.mLastActivity instanceof NormalLoadingActivity)) {
//						getChatMessage(IMOCommand.IMO_SEND_MESSAGE);
//					}
					break;
				case SERVER_PROMPT:
					handleServerPrompt(msg);
					break;

				default:
					break;
				}

			}

			private void handleServerPrompt(Message msg) {
//				short command = (Short) msg.obj;
//				ServerPromptInPacket serverPromptInPacket = (ServerPromptInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);
//				int unType = serverPromptInPacket.getUnType();
//				LogFactory.d("", "服务器维护号：" + unType);
//				if (unType == 0 || unType == 1) {// 服务器维护
//					LogFactory.d("", "收到服务器维护信息");
//					Dialog dialog = DialogFactory.serverPromptDialog(IMOApp.getApp().mLastActivity);
//					if (dialog != null)
//						dialog.show();
//				}

			}

			/**
			 * 处理失败操作
			 * 
			 * @param msg
			 */
			private void handleFailed(Message msg) {

//				if (IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity || IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity) {
//					IMOApp.getApp().showLoadingFailed();
//					EngineConst.isLoginSuccess = false;
//
//					DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
//
//					IMOApp.getApp().turn2LoginForLogout();
//					return;
//				}
//				// ==========头像变灰=============================
//				// /标识网络不可用
//				EngineConst.isNetworkValid = false;
//				Message failedMSG = new Message();
//				// failedMSG.obj = false;
//				if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
//					if (OrganizeActivity.getActivity() != null) {
//						OrganizeActivity.getActivity().titleBarHandler.sendMessage(failedMSG);
//					}
//				} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
//					if (ContactActivity.getActivity() != null) {
//						ContactActivity.getActivity().titleBarHandler.sendMessage(failedMSG);
//					}
//				}
//				// =======================================
//				if (IMOApp.getApp().hasRunInBackground)
//					return;
//
//				// 没有网络的时候，发送数据，通信模块直接抛出通信失败
//				if (aErrorCode == IMOCommand.ERROR_CONNECTION_BROKEN) {
//					Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
//					return;
//				}
//
//				if (!canShow)
//					return;
//
//				if (EngineConst.isLoginSuccess)
//					return;
//
//				canShow = false;
//
//				absDialog = DialogFactory.alertDialog(/* mContext* */getParent(), resources.getString(R.string.warn), (String) msg.obj, new String[] { resources.getString(R.string.ok) }, new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						if (mContext instanceof LoginActivity) {
//							AppService.getService().reset();
//							LoginActivity.launch(mContext);
//						}
//					}
//				});
//				absDialog.show();
//				absDialog.setOnDismissListener(new OnDismissListener() {
//					@Override
//					public void onDismiss(DialogInterface dialog) {
//						canShow = true;
//					}
//				});
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
//		// 重登陆只有超时
//		if (command == IMOCommand.IMO_GET_RELOGIN) {
//			// //再次打开失败接受大门
//			IMOApp.getApp().hasPackageFailed = false;
//			Toast.makeText(mContext, "网络异常，请您重新登录。", Toast.LENGTH_SHORT).show();
//			EngineConst.isLoginSuccess = false;
//			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
//
//			// 重登陆超时，设置状态机为DISCONNECTED
//			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
//			
//			return;
//		}
//
//		if (command == IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE || command == IMOCommand.IMO_GET_EMPLOYEE_STATUS) {
//			// /脱机摘要、获取用户状态不提示网络超时
//			return;
//		} else if (command == IMOCommand.IMO_GET_EMPLOYEE_PROFILE && !(IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity || IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity)) {
//			// /获取名片，不提示网络超时
//			return;
//		} else if (command == IMOCommand.IMO_EDIT_PROFILE) {
//			// 编辑工作签名超时
//			if (IMOApp.getApp().mLastActivity instanceof WorkSignActivity) {
//				WorkSignActivity.getActivity().dismissDialog();
//				Toast.makeText(mContext, mContext.getString(R.string.notifypacket_timeout), Toast.LENGTH_SHORT).show();
//			}
//			return;
//		}
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
		sendMessage(NotifyPacketTimeOut, aErrorCode);
	}

	private short aErrorCode;

	/* Packet Failed * */
	@Override
	public void NotifyPacketFailed(String aConnectionId, short aErrorCode) {

		this.aErrorCode = aErrorCode;
		LogFactory.d("NotifyPacketFailed", aErrorCode + "1111");

		if (!mGlobal.hasPackageFailed) {
//			if (aErrorCode == IMOCommand.ERROR_NETWORK) {
//
//				boolean temp = ConnectionChangeReceiver.checkNet();
//
//				if (temp) {
//					mGlobal.hasPackageFailed = true;
//					mConnectionHandler.sendEmptyMessage(aErrorCode);
//					sendMessage(NotifyPacketFailed, resources.getString(R.string.notifypacket_failed));
//
//					return;
//				} else {
//					EngineConst.isNetworkValid = false;
//					sendMessage(NotifyPacketFailed, resources.getString(R.string.notifypacket_failed));
//
//					return;
//				}
//			} else {
//				sendMessage(NotifyPacketFailed, resources.getString(R.string.notifypacket_failed));
//			}
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
		IMOApp.getDataEngine().removeFromObserverList(this);

		try {
			unregisterReceiver(mFinishBroadcast);
		} catch (Exception e) {
		}
		super.finish();
	}
}

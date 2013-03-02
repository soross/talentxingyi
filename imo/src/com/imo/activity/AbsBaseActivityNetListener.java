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
 * ����ͨ�ų�������� <br>
 * ʹ������: ====================================== <br>
 * <br>
 * 1-�̳�AbsBaseActivityNetListener <br>
 * 2-ʵ���߼��¼��е��� super.mNIOThread.send �����������������д�� <br>
 * 3-NotifyPacketArrived�������߼�����1��buffer--->obj 2)����super.sendMessage(type,obj)�� <br>
 * 4-refresh�����и���UI�����ݲ����� <br>
 * 
 * @author CaixiaoLong
 * 
 */
public abstract class AbsBaseActivityNetListener extends AbsBaseActivity implements PacketsObserver {

	/**
	 * ��������UI���µ�Type����
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

	// ��ʾΪ�����Ӷ���
	protected static boolean isNewConnection = false;

	private Dialog absDialog;
	private boolean canShow = true;

	protected Handler mParentHandler;

	/**
	 * �Ѿ���ʾ��һ���Ի���
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
	 * ����AllEmployeeInfo�����
	 * 
	 * @param aContactorsNum
	 *        �����Ա������ ����
	 * @param aContactorCidArray
	 *        �ڲ���ϵ�� ���ⲿ��ϵ�� CidArray
	 * @param aContactorUidArray
	 *        �û� UIdArray
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
			if (ret == 0) {// ������¼
				Globe.customList.clear();
				// ������½���
				EngineConst.cId = buffer.getInt();
				EngineConst.uId = buffer.getInt();
				// ��ʼ��SP�洢�ļ���
				Globe.SP_FILE = "SP" + EngineConst.uId;
				// ��ʼ����˾logo�洢�ļ���
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

				// /����״̬��
				if (OrganizeActivity.getActivity() != null) {
					OrganizeActivity.getActivity().parent_UpdateState();
				} else {
					LogFactory.e("error", "OrganizeActivity not launch");
				}

				// [����״̬�ı�֪ͨ]
				CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_UPDATE_STATUS, EngineConst.cId, EngineConst.uId);
				mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
				// [End]
				// [�����ѻ�ժҪ]
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

		if (ret == 17) {// ���µ�¼
			String loginName = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
					LoginActivity.LOGIN_NAME, new String()
			});
			String loginPwd = (String) PreferenceManager.get(Globe.SP_FILE, new String[] {
					LoginActivity.LOGIN_PWD, new String()
			});
			String[] nameAndDomain = loginName.split("@");
			EngineConst.password = loginPwd;
			// 0����˾�˺ŵ�¼��1����������¼
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
			// [����״̬�ı�֪ͨ]
			CommonOutPacket outPacket = new CommonOutPacket(ByteBuffer.allocate(0), IMOCommand.IMO_UPDATE_STATUS, EngineConst.cId, EngineConst.uId);
			mNIOThread.send(EngineConst.IMO_CONNECTION_ID, outPacket, false);
			// [End]
		}
	}

	// protected boolean isExitApp = false;
	/**
	 * ������������
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
			int fromUid = sendMsgInPacket.getFromuid(); // ����Ϣ���û�id
			int server_msg_id = sendMsgInPacket.getServer_msg_id();
			String message = sendMsgInPacket.getMsg();

			LogFactory.d("��С��", "��Ϣ" + server_msg_id);
			if (Globe.customList.contains(server_msg_id)) {
				LogFactory.d("��С��", "�����ظ���Ϣ");
				sendACK(fromCid, fromUid, server_msg_id);
				return;
			}

			Globe.customList.add(server_msg_id);

			try {
				if (!Functions.checkMessage(message)) {// ���Զ���ͼƬ�����洢������ʾ
					// ����ACK
					LogFactory.d("��С��", "���Զ���ͼƬ");
					sendACK(fromCid, fromUid, server_msg_id);
					return;
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			// �ж��Ƿ��Ѷ����߼����£������������ǵ�ǰ��ʾ�Ľ��棬����Ϊ�Ѷ���������Ϊδ����
			int isRead = MessageInfo.MessageInfo_UnRead;
			// if ("DialogueActivity".equals(Functions.getCurrentActivity()))

			// ���ӽ��յ��û���Ϣ����û�id���湦��
			if (!mGlobal.sendMsgUserId.contains(fromUid)) {
				mGlobal.sendMsgUserId.add(fromUid);
				System.out.println("��ǰ���ӵ������û�id-------" + mGlobal.sendMsgUserId);
			}

			if (mGlobal.mLastActivity instanceof DialogueActivity) {
				if (DialogueActivity.instance.aboutUid != fromUid) {
					isRead = MessageInfo.MessageInfo_UnRead;
				} else {
					isRead = MessageInfo.MessageInfo_Readed;
				}
			}
			// ��ŵ����ݿ�
			try {
				MessageInfo msg = new MessageInfo(0, fromUid, "", EngineConst.uId, "", Functions.getDate(), Functions.getTime(), message, MessageInfo.MessageInfo_From, MessageInfo.MessageInfo_MsgId, isRead, MessageInfo.MessageInfo_UnFailed);
				IMOApp.imoStorage.addMessage(msg);
				LogFactory.d("��С��", "��ŵ����ݿ�");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			// ��Ϣ������������ʾ����ϣ���ʼ��Ϊǰ��̨�����ڲ����ˡ�
			// if (IMOApp.getApp().hasRunInBackground) {
			//
			// } else {
			// Functions.msgNotification();
			// }
			Functions.msgNotification(); // ������

			// /����Ϣ��ʱ�����״̬
			IMOApp.getApp().updateStateForGetMSG(fromUid);

			// ֪ͨ������ʾ
			// if ("DialogueActivity".equals(Functions.getCurrentActivity())) {
			if (mGlobal.mLastActivity instanceof DialogueActivity) {
				DialogueActivity.instance.getMessage(command);
				LogFactory.d("��С��", "֪ͨ������ʾ1");
				if (DialogueActivity.instance.aboutUid != fromUid) {
					if (MainActivityGroup.getActivityGroup() != null) {
						LogFactory.d("��С��", "֪ͨ������ʾ2");
						MainActivityGroup.getActivityGroup().mHandler.sendEmptyMessage(1);
					}
				}
			}

			LogFactory.d("��С��", "֪ͨ������ʾ3");
			updateRecentMap(fromCid, fromUid, getNameByUid(fromUid), message, Functions.getFullTime(), isRead);
			// Functions.getDate() + " " + Functions.getTime(),
			// ����ACK
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
	 * ������Ϣ
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
				LogFactory.d("", "������ά���ţ�" + unType);
				if (unType == 0 || unType == 1) {// ������ά��
					LogFactory.d("", "�յ�������ά����Ϣ");
					Dialog dialog = DialogFactory.serverPromptDialog(IMOApp.getApp().mLastActivity);
					if (dialog != null)
						dialog.show();
				}

			}

			/**
			 * ����ʧ�ܲ���
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
				// ==========ͷ����=============================
				// /��ʶ���粻����
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

				// û�������ʱ�򣬷������ݣ�ͨ��ģ��ֱ���׳�ͨ��ʧ��
				if (aErrorCode == IMOCommand.ERROR_CONNECTION_BROKEN) {
					Toast.makeText(mContext, "�����쳣", Toast.LENGTH_SHORT).show();
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
	 * ��ʱ����
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
		// Toast.makeText(mContext, "��������ʱ��", Toast.LENGTH_SHORT).show();
		// }else{
		// mTimeOutHandle.showDialog(mContext);
		// }

		// /�ص�½ֻ�г�ʱ
		if (command == IMOCommand.IMO_GET_RELOGIN) {
			// //�ٴδ�ʧ�ܽ��ܴ���
			IMOApp.getApp().hasPackageFailed = false;
			Toast.makeText(mContext, "�����쳣���������µ�¼��", Toast.LENGTH_SHORT).show();
			EngineConst.isLoginSuccess = false;
			DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);

			// �ص�½��ʱ������״̬��ΪDISCONNECTED
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
			// ԭ�ȵ��߼����ص�½��ʱ����ת����½����
			// IMOApp.getApp().turn2LoginForLogout();
			return;
		}

		if (command == IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE || command == IMOCommand.IMO_GET_EMPLOYEE_STATUS) {
			// /�ѻ�ժҪ����ȡ�û�״̬����ʾ���糬ʱ
			return;
		} else if (command == IMOCommand.IMO_GET_EMPLOYEE_PROFILE && !(IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity || IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity)) {
			// /��ȡ��Ƭ������ʾ���糬ʱ
			return;
		} else if (command == IMOCommand.IMO_EDIT_PROFILE) {
			// /�༭����ǩ����ʱ
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
	 * �������ʱ����CrashMSG��Ϣ
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
	 * �汾��������
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
	 * ��Ӧ �汾��������
	 * 
	 * @param command
	 */
	protected void responseAppVersion(short command) {

		UpdateVersionInPacket inPacket = (UpdateVersionInPacket) IMOApp.getDataEngine().getInPacketByCommand(command);

		LogFactory.d("xxUpdate", "responseAppVersion----------------->");
		LogFactory.d("xxUpdate", inPacket.toString());

		byte flag = inPacket.getFlag();
		LogFactory.d("xxUpdate", flag + "");

		int flag0 = (flag & 0x01);// ���һλ����ʾ����Ҫ������ 1����Ҫ 0�� ����Ҫ
		int flag1 = (flag & 0x02) >> 1;// �����ڶ�λ��ʾ�Ƿ�ǿ��������1��ǿ�ƣ� 0����ǿ��
		int flag2 = (flag & 0x04) >> 2;// ��������λ��ʾ�Ƿ�����������1�����ѣ� 0��������

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

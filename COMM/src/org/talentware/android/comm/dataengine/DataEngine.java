package org.talentware.android.comm.dataengine;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.talentware.android.comm.net.EngineConst;
import org.talentware.android.comm.packet.InPacket;
import org.talentware.android.comm.packet.OutPacket;
import org.talentware.android.comm.packet.command.IMOCommand;
import org.talentware.android.comm.packet.exception.PacketParseException;
import org.talentware.android.comm.packet.packetsobserver.PacketsObserver;
import org.talentware.android.comm.util.LogFactory;

public class DataEngine {

	/** 发送队列 */
	protected Queue<OutPacket> sendQueue;
	/** 接收队列 */
	protected Queue<InPacket> inQueue;
	/** 观察者队列 */
	protected Queue<PacketsObserver> observerQueue;
	/** 超时队列 */
	protected Map<Integer, OutPacket> timeoutQueue;

	protected LOGICSTATUS current_logic_status = LOGICSTATUS.DISCONNECTED;

	/** 程序状态机 */
	public enum LOGICSTATUS {
		DISCONNECTED, CHECKING, CONNECTING, CONNECTED, LOGINOVER;

		public String toString() {
			switch (this) {
				case DISCONNECTED:
					return "DISCONNECTED";
				case CHECKING:
					return "CHECKING";
				case CONNECTING:
					return "CONNECTING";
				case CONNECTED:
					return "CONNECTED";
				case LOGINOVER:
					return "LOGINOVER";
				default:
					return "Unknow";
			}
		}
	}

	public DataEngine() {
		sendQueue = new ConcurrentLinkedQueue<OutPacket>();
		inQueue = new ConcurrentLinkedQueue<InPacket>();
		observerQueue = new ConcurrentLinkedQueue<PacketsObserver>();
		timeoutQueue = new ConcurrentHashMap<Integer, OutPacket>();
	}

	public LOGICSTATUS getLogicStatus() {
		return current_logic_status;
	}

	public void setLogicStatus(LOGICSTATUS aLogicStatus) {
		current_logic_status = aLogicStatus;
	}

	private static DataEngine mDataEngine;

	public synchronized static DataEngine getInstance() {
		if (mDataEngine == null) {
			mDataEngine = new DataEngine();
		}

		return mDataEngine;
	}

	public Queue<PacketsObserver> getObserverQueue() {
		return observerQueue;
	}

	public void clearObserverQueue() {
		observerQueue.clear();
	}

	public Queue<OutPacket> getOutQueue() {
		return sendQueue;
	}

	public void clearSendQueue() {
		sendQueue.clear();
	}

	public Queue<InPacket> getInQueue() {
		return inQueue;
	}

	public void clearInQueue() {
		inQueue.clear();
	}

	public Map<Integer, OutPacket> getTimeoutQueue() {
		return timeoutQueue;
	}

	public void clearTimeoutQueue() {
		timeoutQueue.clear();
	}

	public void removePacketFromTimeoutQueue(int aSeq) {
		LogFactory.e("removePacketFromTimeoutQueue", "removePacketFromTimeoutQueue seq = :" + aSeq);
		timeoutQueue.remove(aSeq);
	}

	/**
	 * 得到超时队列的第一个包，并把它从队列中删除
	 * 
	 * @return 超时队列的第一个包，如果没有，返回null
	 */
	public OutPacket getPacketFromTimeoutQueue(int aSeq) {
		return timeoutQueue.get(aSeq);
	}

	public OutPacket getPacketByCommand(short aCommand) {
		Iterator<OutPacket> itrSendQueue = sendQueue.iterator();
		while (itrSendQueue.hasNext()) {
			OutPacket out = itrSendQueue.next();
			if (out.getCommand() == aCommand)
				return out;
		}
		return null;
	}

	public void add(OutPacket packet) {
		if (packet.getCommand() == IMOCommand.IMO_HEART_BEAT)
			EngineConst.HEARTBEAT_SEND_COUNT++;
		sendQueue.offer(packet);
	}

	public boolean isEmpty() {
		return sendQueue.isEmpty();
	}

	public OutPacket remove() {
		return sendQueue.poll();
	}

	public OutPacket retrieves() {
		return sendQueue.peek();
	}

	public void reconnectServer() {
		if (current_logic_status == LOGICSTATUS.DISCONNECTED) {
			// 暂时去掉
			// LogFactory.e("ERROR_NETWORK", "old connection id = " +
			// EngineConst.IMO_CONNECTION_ID);
			//
			// EngineConst.isReloginSuccess = false;
			// String oldConnectionID = EngineConst.IMO_CONNECTION_ID;
			//
			// EngineConst.IMO_CONNECTION_ID =
			// EngineConst.GenerateRandomString();
			//
			// LogFactory.e("ERROR_NETWORK", "new connection id :" +
			// EngineConst.IMO_CONNECTION_ID + ",old connectionID :" +
			// oldConnectionID);
			//
			// AppService.getService().setTcpConnection((TCPConnection)
			// AppService.getService().getNIOThreadInstance().newTCPConnection(EngineConst.IMO_CONNECTION_ID,
			// EngineConst.IMO_SERVER_ADDRESS, true));
			//
			// AppService.getService().getNIOThreadInstance().release(oldConnectionID,
			// false);
			//
			// ReloginOutPacket out = new
			// ReloginOutPacket(ByteBuffer.wrap(EngineConst.sessionKey),
			// IMOCommand.IMO_GET_RELOGIN, EngineConst.cId, EngineConst.uId);
			// AppService.getService().getNIOThreadInstance().send(EngineConst.IMO_CONNECTION_ID,
			// out, false);
		}
	}

	private Object lock_observerNotifyPacketFailed = new Object();

	public void observerNotifyPacketFailed(String aConnectionId, short aErrorCode) {
		synchronized (lock_observerNotifyPacketFailed) {
			Iterator<PacketsObserver> itrObserver = observerQueue.iterator();
			while (itrObserver.hasNext()) {
				PacketsObserver observer = itrObserver.next();
				observer.NotifyPacketFailed(aConnectionId, aErrorCode);
			}
		}
	}

	private Object lock_observerNotifyPacketTimeOut = new Object();

	public void observerNotifyPacketTimeOut(String aConnectionId, short aErrorCode) {
		synchronized (lock_observerNotifyPacketTimeOut) {
			Iterator<PacketsObserver> itrObserver = observerQueue.iterator();
			while (itrObserver.hasNext()) {
				PacketsObserver observer = itrObserver.next();
				observer.NotifyPacketTimeOut(aConnectionId, aErrorCode);
			}
		}
	}

	private Object lock_getInPacketByCommand = new Object();

	public InPacket getInPacketByCommand(short command) {
		synchronized (lock_getInPacketByCommand) {
			Iterator<InPacket> itr = inQueue.iterator();
			while (itr.hasNext()) {
				InPacket in = itr.next();
				if (command == in.getCommand()) {
					inQueue.remove(in);
					return in;
				}
			}
			return null;
		}
	}

	public void addToObserverList(PacketsObserver aObserver) {
		if (!hasExistObserver(aObserver))
			observerQueue.add(aObserver);
	}

	public boolean hasExistObserver(PacketsObserver aObserver) {
		return observerQueue.contains(aObserver);
	}

	public void removeFromObserverList(PacketsObserver aObserver) {
		if (hasExistObserver(aObserver))
			observerQueue.remove(aObserver);
	}

	public void removeAllObservers() {
		observerQueue.clear();
	}

	private Object lock_observerNotifyPacketArrived = new Object();

	public void observerNotifyPacketArrived(String aConnectionId, short command) {
		synchronized (lock_observerNotifyPacketArrived) {
			Iterator<PacketsObserver> itrObserver = observerQueue.iterator();
			while (itrObserver.hasNext()) {
				PacketsObserver observer = itrObserver.next();
				if (command == IMOCommand.IMO_FORCE_EXIT || command == IMOCommand.IMO_HEART_BEAT || command == IMOCommand.IMO_SEND_MESSAGE || command == IMOCommand.IMO_MODIFY_QGROUP_NAME_NOTICE || command == IMOCommand.IMO_GET_RELOGIN || command == IMOCommand.IMO_LOGIN
						|| command == IMOCommand.IMO_EXIT_QGROUP_NOTICE_USERS || command == IMOCommand.IMO_QGOURP_CHAT || command == IMOCommand.IMO_UPDATE_VERSION || command == IMOCommand.IMO_QGROUP_DESTROY_NOTICE_USER || command == IMOCommand.IMO_NEW_MSG_NOTICE_QGROUP
						|| command == IMOCommand.IMO_QGROUP_USER_CHANGE_STATUS_NOTICE || command == IMOCommand.IMO_QGROUP_NOTICE || command == IMOCommand.IMO_QGROUP_KICK_USER_NOTICE || command == IMOCommand.IMO_NOTICE_USER_JOIN_QGROUP || command == IMOCommand.IMO_QGROUP_MODIFY_ANNOUNCEMENT_NOTICE
						|| command == IMOCommand.IMO_SERVER_PROMPT) {
					LogFactory.d("observerNotifyPacketArrived", "" + command);
					observer.NotifyPacketArrived(aConnectionId, command);
					break;
				} else {
					// 暂时去掉
					// if (((AbsBaseActivityNetListener)
					// (IMOApp.getApp().mLastActivity)).CanAcceptPacket(command))
					// {
					// ((AbsBaseActivityNetListener)
					// (IMOApp.getApp().mLastActivity)).NotifyPacketArrived(aConnectionId,
					// command);
					// break;
					// }
					if (observer.CanAcceptPacket(command)) {
						observer.NotifyPacketArrived(aConnectionId, command);
						break;
					}
				}
			}
		}
	}

	public void observerNotifyHttpPacketArrived(String aConnectionId, ByteBuffer buffer) {
	}

	private Object lock_observerNotifyPacketProgress = new Object();

	public void observerNotifyPacketProgress(String aConnectionId, short command, short aTotalLen, short aSendedLen) {
		synchronized (lock_observerNotifyPacketProgress) {
			Iterator<PacketsObserver> itrObserver = observerQueue.iterator();
			while (itrObserver.hasNext()) {
				PacketsObserver observer = itrObserver.next();
				if (observer.CanAcceptPacket(command)) {
					observer.NotifyPacketProgress(aConnectionId, command, aTotalLen, aSendedLen);
					break;
				}
			}
		}
	}

	public InPacket parseIn(ByteBuffer buf, int length) throws PacketParseException {
		// 保存当前位置
		int offset = buf.position() + 2;
		// 解析包
		InPacket ret = parseIncoming(buf, offset, length);

		return ret;
	}

	/**
	 * 得到包的命令和序号
	 * 
	 * @param buf
	 */
	private short getCommand(ByteBuffer buf, int offset) {
		return buf.getShort(offset);
	}

	public InPacket parseIncoming(ByteBuffer buf, int offset, int length) throws PacketParseException {
		short commandRet = getCommand(buf, offset);

		LogFactory.e("parseIncoming packet: ", "" + commandRet);

		return null;

		// 暂时去掉
		// ConnectionLog.MusicLogInstance().addLog("parseIncoming packet: ," +
		// commandRet);

		// switch (commandRet) {
		// case IMOCommand.IMO_LOGIN:
		// LoginInPacket loginInPac = new LoginInPacket(buf, length);
		// loginInPac.setCommand(IMOCommand.IMO_LOGIN);
		// removePacketFromTimeoutQueue(loginInPac.get_header_seq());
		// return loginInPac;
		// case IMOCommand.IMO_HEART_BEAT:
		// HeartBeatInPacket heartbeatInPac = new HeartBeatInPacket(buf,
		// length);
		// heartbeatInPac.setCommand(IMOCommand.IMO_HEART_BEAT);
		// return heartbeatInPac;
		// case IMOCommand.IMO_FORCE_EXIT:
		// ForceLogoutInPacket forcelogoutInPac = new
		// ForceLogoutInPacket(buf,
		// length);
		// forcelogoutInPac.setCommand(IMOCommand.IMO_FORCE_EXIT);
		// return forcelogoutInPac;
		// case IMOCommand.IMO_GET_CORP_INFO:
		// GetCorpInfoInPacket corpinfoInPac = new
		// GetCorpInfoInPacket(buf,
		// length);
		// corpinfoInPac.setCommand(IMOCommand.IMO_GET_CORP_INFO);
		// removePacketFromTimeoutQueue(corpinfoInPac.get_header_seq());
		// return corpinfoInPac;
		// case IMOCommand.IMO_GET_DEPT_UC:
		// GetDeptUCInPacket deptucInPac = new GetDeptUCInPacket(buf,
		// length);
		// deptucInPac.setCommand(IMOCommand.IMO_GET_DEPT_UC);
		// removePacketFromTimeoutQueue(deptucInPac.get_header_seq());
		// return deptucInPac;
		// case IMOCommand.IMO_GET_DEPT_INFO:
		// GetDeptInfoInPacket deptinfoInPac = new
		// GetDeptInfoInPacket(buf,
		// length);
		// deptinfoInPac.setCommand(IMOCommand.IMO_GET_DEPT_INFO);
		// removePacketFromTimeoutQueue(deptinfoInPac.get_header_seq());
		// return deptinfoInPac;
		// case IMOCommand.IMO_GET_ALL_EMPLOYEE_UID:
		// GetAllEmployeesUIDInPacket employeesuidInPac = new
		// GetAllEmployeesUIDInPacket(buf, length);
		// employeesuidInPac.setCommand(IMOCommand.IMO_GET_ALL_EMPLOYEE_UID);
		// removePacketFromTimeoutQueue(employeesuidInPac.get_header_seq());
		// return employeesuidInPac;
		// case IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO:
		// GetAllEmployeesInfoInPacket employeesbasicinfoInPac = new
		// GetAllEmployeesInfoInPacket(buf, length);
		// employeesbasicinfoInPac.setCommand(IMOCommand.IMO_GET_ALL_EMPLOYEE_BASIC_INFO);
		// removePacketFromTimeoutQueue(employeesbasicinfoInPac.get_header_seq());
		// return employeesbasicinfoInPac;
		// case IMOCommand.IMO_GET_EMPLOYEE_STATUS:
		// GetEmployeesStatusInPacket employeesStatusInPac = new
		// GetEmployeesStatusInPacket(buf, length);
		// employeesStatusInPac.setCommand(IMOCommand.IMO_GET_EMPLOYEE_STATUS);
		// removePacketFromTimeoutQueue(employeesStatusInPac.get_header_seq());
		// return employeesStatusInPac;
		// case IMOCommand.IMO_GET_RELOGIN:
		// ReloginInPacket reloginInPac = new ReloginInPacket(buf,
		// length);
		// reloginInPac.setCommand(IMOCommand.IMO_GET_RELOGIN);
		//
		// removePacketFromTimeoutQueue(reloginInPac.get_header_seq());
		// return reloginInPac;
		// case IMOCommand.IMO_SEND_MESSAGE:
		// SendMsgInPacket sendmsgInPac = new SendMsgInPacket(buf,
		// length);
		// sendmsgInPac.setCommand(IMOCommand.IMO_SEND_MESSAGE);
		// removePacketFromTimeoutQueue(sendmsgInPac.get_header_seq());
		// return sendmsgInPac;
		// case IMOCommand.IMO_UPDATE_STATUS:
		// CommonInPacket commonmsgInPac = new CommonInPacket(buf,
		// length);
		// commonmsgInPac.setCommand(IMOCommand.IMO_UPDATE_STATUS);
		// return commonmsgInPac;
		// case IMOCommand.IMO_GET_OFFLINE_MSG:
		// GetOfflineMsgInPacket offlinemsgInPac = new
		// GetOfflineMsgInPacket(buf, length);
		// offlinemsgInPac.setCommand(IMOCommand.IMO_GET_OFFLINE_MSG);
		// removePacketFromTimeoutQueue(offlinemsgInPac.get_header_seq());
		// return offlinemsgInPac;
		// case IMOCommand.IMO_GET_EMPLOYEE_PROFILE:
		// GetEmployeeProfileInPacket employeeprofInPac = new
		// GetEmployeeProfileInPacket(buf, length);
		// employeeprofInPac.setCommand(IMOCommand.IMO_GET_EMPLOYEE_PROFILE);
		// removePacketFromTimeoutQueue(employeeprofInPac.get_header_seq());
		// return employeeprofInPac;
		// case IMOCommand.IMO_UPDATE_USER_STATUS:
		// UserStatusChangeInPacket statusChangeInPac = new
		// UserStatusChangeInPacket(buf, length);
		// statusChangeInPac.setCommand(IMOCommand.IMO_UPDATE_USER_STATUS);
		// LogFactory.e("user_status changed :", "cid:" +
		// statusChangeInPac.getCid() + ",uid :" +
		// statusChangeInPac.getUid() +
		// ",status :" +
		// Integer.toHexString(statusChangeInPac.getStatus()));
		// removePacketFromTimeoutQueue(statusChangeInPac.get_header_seq());
		// // [接到状态改变后发送ACK包]
		// if (statusChangeInPac.getServer_msg_id() != 0) {
		// ByteBuffer statusACKBuf =
		// StatusAckOutPacket.GenerateStatusAckBody(statusChangeInPac.getServer_msg_id());
		// StatusAckOutPacket statusAckOutPac = new
		// StatusAckOutPacket(statusACKBuf, IMOCommand.IMO_STATUS_ACK,
		// EngineConst.cId, EngineConst.uId);
		// add(statusAckOutPac);
		// }
		// return statusChangeInPac;
		// case IMOCommand.IMO_EDIT_PROFILE:
		// EditProfileInPacket editProfileInPac = new
		// EditProfileInPacket(buf,
		// length);
		// editProfileInPac.setCommand(IMOCommand.IMO_EDIT_PROFILE);
		// removePacketFromTimeoutQueue(editProfileInPac.get_header_seq());
		// return editProfileInPac;
		// case IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE:
		// GetOfflineMsgProfileInPacket offMsgProfileInPac = new
		// GetOfflineMsgProfileInPacket(buf, length);
		// offMsgProfileInPac.setCommand(IMOCommand.IMO_GET_OFFLINE_MSG_PROFILE);
		// removePacketFromTimeoutQueue(offMsgProfileInPac.get_header_seq());
		// return offMsgProfileInPac;
		// case IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS:
		// GetOffMsgFromContactorInPacket offMsgFromContactorInPac = new
		// GetOffMsgFromContactorInPacket(buf, length);
		// offMsgFromContactorInPac.setCommand(IMOCommand.IMO_GET_OFFLINE_MSG_CONTENTS);
		// removePacketFromTimeoutQueue(offMsgFromContactorInPac.get_header_seq());
		// return offMsgFromContactorInPac;
		// case IMOCommand.IMO_DELETE_OFFLINE_MSG:
		// DeleteOfflineMsgInPacket deleteMsgInPac = new
		// DeleteOfflineMsgInPacket(buf, length);
		// deleteMsgInPac.setCommand(IMOCommand.IMO_DELETE_OFFLINE_MSG);
		// removePacketFromTimeoutQueue(deleteMsgInPac.get_header_seq());
		// return deleteMsgInPac;
		// case IMOCommand.IMO_INNER_CONTACTOR_GROUP_UC:
		// case IMOCommand.IMO_OUTER_CONTACTOR_GROUP_UC:
		// case IMOCommand.IMO_INNER_CONTACTOR_LIST_UC:
		// case IMOCommand.IMO_OUTER_CONTACTOR_LIST_UC:
		// ContactorGroupUCInPacket groupUCInPac = new
		// ContactorGroupUCInPacket(buf, length);
		// groupUCInPac.setCommand(commandRet);
		// removePacketFromTimeoutQueue(groupUCInPac.get_header_seq());
		// return groupUCInPac;
		// case IMOCommand.IMO_INNER_CONTACTOR_GROUP:
		// case IMOCommand.IMO_OUTER_CONTACTOR_GROUP:
		// ContactorGroupInPacket groupInPac = new
		// ContactorGroupInPacket(buf,
		// length);
		// groupInPac.setCommand(commandRet);
		// removePacketFromTimeoutQueue(groupInPac.get_header_seq());
		// return groupInPac;
		// case IMOCommand.IMO_INNER_CONTACTOR_LIST:
		// InnerContactorListInPacket innerlistInPac = new
		// InnerContactorListInPacket(buf, length);
		// innerlistInPac.setCommand(IMOCommand.IMO_INNER_CONTACTOR_LIST);
		// removePacketFromTimeoutQueue(innerlistInPac.get_header_seq());
		// return innerlistInPac;
		// case IMOCommand.IMO_OUTER_CONTACTOR_LIST:
		// OuterContactorListInPacket outerlistInPac = new
		// OuterContactorListInPacket(buf, length);
		// outerlistInPac.setCommand(IMOCommand.IMO_OUTER_CONTACTOR_LIST);
		// removePacketFromTimeoutQueue(outerlistInPac.get_header_seq());
		// return outerlistInPac;
		// case IMOCommand.IMO_OUTER_BASIC_INFO:
		// OuterBasicInfoInPacket outerbasicinfoInPac = new
		// OuterBasicInfoInPacket(buf, length);
		// outerbasicinfoInPac.setCommand(IMOCommand.IMO_OUTER_BASIC_INFO);
		// removePacketFromTimeoutQueue(outerbasicinfoInPac.get_header_seq());
		// return outerbasicinfoInPac;
		// case IMOCommand.IMO_UPDATE_VERSION:
		// UpdateVersionInPacket updateInPac = new
		// UpdateVersionInPacket(buf,
		// length);
		// updateInPac.setCommand(IMOCommand.IMO_UPDATE_VERSION);
		// return updateInPac;
		// case IMOCommand.IMO_SERVER_PROMPT:
		// ServerPromptInPacket serverPromptInPac = new
		// ServerPromptInPacket(buf, length);
		// serverPromptInPac.setCommand(IMOCommand.IMO_SERVER_PROMPT);
		// return serverPromptInPac;
		// case IMOCommand.IMO_LOGIN_QGROUP:
		// LoginQGroupInPacket loginGroupInPacket = new
		// LoginQGroupInPacket(buf,
		// length);
		// loginGroupInPacket.setCommand(IMOCommand.IMO_LOGIN_QGROUP);
		// return loginGroupInPacket;
		// case IMOCommand.IMO_GET_USER_QGROUP_LIST:
		// GetUserQGroupListInPacket getUserQGroupListInPacket = new
		// GetUserQGroupListInPacket(buf, length);
		// getUserQGroupListInPacket.setCommand(IMOCommand.IMO_GET_USER_QGROUP_LIST);
		// return getUserQGroupListInPacket;
		// case IMOCommand.IMO_GET_QGROUP_INFO:
		// GetQGroupInfoInPacket getQGroupInfoInPacket = new
		// GetQGroupInfoInPacket(buf, length);
		// getQGroupInfoInPacket.setCommand(IMOCommand.IMO_GET_QGROUP_INFO);
		// return getQGroupInfoInPacket;
		// case IMOCommand.IMO_GET_QGROUP_USER_LIST:
		// GetQGroupUserListInPacket getQGroupUserListInPacket = new
		// GetQGroupUserListInPacket(buf, length);
		// getQGroupUserListInPacket.setCommand(IMOCommand.IMO_GET_QGROUP_USER_LIST);
		// return getQGroupUserListInPacket;
		// case IMOCommand.IMO_GET_USER_NGROUP_LIST_UC:
		// GetUserNgGroupListUcInPacket getUserNgGroupListUcInPacket =
		// new
		// GetUserNgGroupListUcInPacket(buf, length);
		// getUserNgGroupListUcInPacket.setCommand(IMOCommand.IMO_GET_QGROUP_USER_LIST);
		// return getUserNgGroupListUcInPacket;
		// case IMOCommand.IMO_GET_USER_NGROUP_LIST_UC_NEW:
		// GetUserNgGroupListUcNewInPacket
		// getUserNgGroupListUcNewInPacket = new
		// GetUserNgGroupListUcNewInPacket(buf, length);
		// getUserNgGroupListUcNewInPacket.setCommand(IMOCommand.IMO_GET_USER_NGROUP_LIST_UC_NEW);
		// return getUserNgGroupListUcNewInPacket;
		// case IMOCommand.IMO_CLIENT_LOGIN_NGROUPD:
		// GetClientLoginNgroupdInPacket getClientLoginNgroupdInPacket =
		// new
		// GetClientLoginNgroupdInPacket(buf, length);
		// getClientLoginNgroupdInPacket.setCommand(IMOCommand.IMO_CLIENT_LOGIN_NGROUPD);
		// return getClientLoginNgroupdInPacket;
		// case IMOCommand.IMO_AGREE_JOIN_NGROUP:
		// GetAgreeJoinNgroupInPacket getAgreeJoinNgroupInPacket = new
		// GetAgreeJoinNgroupInPacket(buf, length);
		// getAgreeJoinNgroupInPacket.setCommand(IMOCommand.IMO_AGREE_JOIN_NGROUP);
		// return getAgreeJoinNgroupInPacket;
		// case IMOCommand.IMO_NOTICE_USER_JOIN_NGROUP:
		// GetNoticeUserJoinNgroupInPacket
		// getNoticeUserJoinNgroupInPacket = new
		// GetNoticeUserJoinNgroupInPacket(buf, length);
		// getNoticeUserJoinNgroupInPacket.setCommand(IMOCommand.IMO_NOTICE_USER_JOIN_NGROUP);
		// return getNoticeUserJoinNgroupInPacket;
		// case IMOCommand.IMO_NGROUP_MODIFY_ANNOUNCEMENT_NOTICE:
		// GetNgroupModifyAnnouncementNoticeInPacket
		// getNgroupModifyAnnouncementNoticeInPacket = new
		// GetNgroupModifyAnnouncementNoticeInPacket(buf, length);
		// getNgroupModifyAnnouncementNoticeInPacket.setCommand(IMOCommand.IMO_NGROUP_MODIFY_ANNOUNCEMENT_NOTICE);
		// return getNgroupModifyAnnouncementNoticeInPacket;
		// case IMOCommand.IMO_GET_NGROUP_INFO:
		// GetNgroupInfoInPacket getNgroupInfoInPacket = new
		// GetNgroupInfoInPacket(buf, length);
		// getNgroupInfoInPacket.setCommand(IMOCommand.IMO_GET_NGROUP_INFO);
		// return getNgroupInfoInPacket;
		// case IMOCommand.IMO_GET_USER_NGROUP_LIST:
		// GetUserNgroupListInPacket getUserNgroupListInPacket = new
		// GetUserNgroupListInPacket(buf, length);
		// getUserNgroupListInPacket.setCommand(IMOCommand.IMO_GET_USER_NGROUP_LIST);
		// return getUserNgroupListInPacket;
		// case IMOCommand.IMO_GET_NGROUP_USER_LIST:
		// GetNgroupUserListInPacket getNgroupUserListInPacket = new
		// GetNgroupUserListInPacket(buf, length);
		// getNgroupUserListInPacket.setCommand(IMOCommand.IMO_GET_NGROUP_USER_LIST);
		// return getNgroupUserListInPacket;
		// case IMOCommand.IMO_GET_NGROUP_USERS_STATUS:
		// GetNgroupUsersStatusInPacket getNgroupUsersStatusInPacket =
		// new
		// GetNgroupUsersStatusInPacket(buf, length);
		// getNgroupUsersStatusInPacket.setCommand(IMOCommand.IMO_GET_NGROUP_USERS_STATUS);
		// return getNgroupUsersStatusInPacket;
		// case IMOCommand.IMO_NGOURP_CHAT:
		// GetNgroupChatInPacket getNgroupChatInPacket = new
		// GetNgroupChatInPacket(buf, length);
		// getNgroupChatInPacket.setCommand(IMOCommand.IMO_NGOURP_CHAT);
		// return getNgroupChatInPacket;
		// case IMOCommand.IMO_NEW_MSG_NOTICE_NGROUP:
		// GetNewMsgNoticeNgroupInPacket getNewMsgNoticeNgroupInPacket =
		// new
		// GetNewMsgNoticeNgroupInPacket(buf, length);
		// getNewMsgNoticeNgroupInPacket.setCommand(IMOCommand.IMO_NEW_MSG_NOTICE_NGROUP);
		// return getNewMsgNoticeNgroupInPacket;
		// case IMOCommand.IMO_NGROUP_USER_CHANGE_STATUS_NOTICE:
		// GetNgroupUserChangeStatusNoticeInPacket
		// getNgroupUserChangeStatusNoticeInPacket = new
		// GetNgroupUserChangeStatusNoticeInPacket(buf, length);
		// getNgroupUserChangeStatusNoticeInPacket.setCommand(IMOCommand.IMO_NGROUP_USER_CHANGE_STATUS_NOTICE);
		// return getNgroupUserChangeStatusNoticeInPacket;
		// case IMOCommand.IMO_EXIT_NGROUP:
		// GetExitNgroupInPacket getExitNgroupInPacket = new
		// GetExitNgroupInPacket(buf, length);
		// getExitNgroupInPacket.setCommand(IMOCommand.IMO_EXIT_NGROUP);
		// return getExitNgroupInPacket;
		// case IMOCommand.IMO_EXIT_NGROUP_NOTICE_USERS:
		// GetExitNgroupNoticeUsersInPacket
		// getExitNgroupNoticeUsersInPacket =
		// new GetExitNgroupNoticeUsersInPacket(buf, length);
		// getExitNgroupNoticeUsersInPacket.setCommand(IMOCommand.IMO_EXIT_NGROUP_NOTICE_USERS);
		// return getExitNgroupNoticeUsersInPacket;
		// case IMOCommand.IMO_MODIFY_NGROUP_NAME_NOTICE:
		// GetModifyNgroupNameNoticeInPacket
		// getModifyNgroupNameNoticeInPacket =
		// new GetModifyNgroupNameNoticeInPacket(buf, length);
		// getModifyNgroupNameNoticeInPacket.setCommand(IMOCommand.IMO_MODIFY_NGROUP_NAME_NOTICE);
		// return getModifyNgroupNameNoticeInPacket;
		// case IMOCommand.IMO_NGROUP_KICK_USER_NOTICE:
		// GetNgroupKickUserNoticeInPacket
		// getNgroupKickUserNoticeInPacket = new
		// GetNgroupKickUserNoticeInPacket(buf, length);
		// getNgroupKickUserNoticeInPacket.setCommand(IMOCommand.IMO_NGROUP_KICK_USER_NOTICE);
		// return getNgroupKickUserNoticeInPacket;
		// case IMOCommand.IMO_GET_OFFLINE_NGROUP_CHAT_MSG:
		// GetOffLineNgroupChatMsgInPacket
		// getOffLineNgroupChatMsgInPacket = new
		// GetOffLineNgroupChatMsgInPacket(buf, length);
		// getOffLineNgroupChatMsgInPacket.setCommand(IMOCommand.IMO_GET_OFFLINE_NGROUP_CHAT_MSG);
		// return getOffLineNgroupChatMsgInPacket;
		// case IMOCommand.IMO_NGROUP_NOTICE:
		// GetNgroupNoticeInPacket getNgroupNoticeInPacket = new
		// GetNgroupNoticeInPacket(buf, length);
		// getNgroupNoticeInPacket.setCommand(IMOCommand.IMO_NGROUP_NOTICE);
		// return getNgroupNoticeInPacket;
		// case IMOCommand.IMO_QGOURP_CHAT:
		// QGroupChatInPacket qGroupChatInPacket = new
		// QGroupChatInPacket(buf,
		// length);
		// qGroupChatInPacket.setCommand(IMOCommand.IMO_QGOURP_CHAT);
		// return qGroupChatInPacket;
		// case IMOCommand.IMO_NEW_MSG_NOTICE_QGROUP:
		// NewMsgNoticeQGroupInPacket newMsgNoticeQGroupInPacket = new
		// NewMsgNoticeQGroupInPacket(buf, length);
		// newMsgNoticeQGroupInPacket.setCommand(IMOCommand.IMO_NEW_MSG_NOTICE_QGROUP);
		// return newMsgNoticeQGroupInPacket;
		// case IMOCommand.IMO_QGROUP_USER_CHANGE_STATUS_NOTICE:
		// QGroupUserChangeStatusNoticeInPacket
		// qGroupUserChangeStatusNoticeInPacket = new
		// QGroupUserChangeStatusNoticeInPacket(buf, length);
		// qGroupUserChangeStatusNoticeInPacket.setCommand(IMOCommand.IMO_QGROUP_USER_CHANGE_STATUS_NOTICE);
		// return qGroupUserChangeStatusNoticeInPacket;
		// case IMOCommand.IMO_EXIT_QGROUP:
		// ExitQgroupInPacket exitQgroupInPacket = new
		// ExitQgroupInPacket(buf,
		// length);
		// exitQgroupInPacket.setCommand(IMOCommand.IMO_EXIT_QGROUP);
		// return exitQgroupInPacket;
		// case IMOCommand.IMO_EXIT_QGROUP_NOTICE_USERS:
		// ExitQgroupNoticeUsersInPacket exitQgroupNoticeUsersInPacket =
		// new
		// ExitQgroupNoticeUsersInPacket(buf, length);
		// exitQgroupNoticeUsersInPacket.setCommand(IMOCommand.IMO_EXIT_QGROUP_NOTICE_USERS);
		// return exitQgroupNoticeUsersInPacket;
		// case IMOCommand.IMO_MODIFY_QGROUP_NAME_NOTICE:
		// ModifyQgroupNameNoticeInPacket modifyQgroupNameNoticeInPacket
		// = new
		// ModifyQgroupNameNoticeInPacket(buf, length);
		// modifyQgroupNameNoticeInPacket.setCommand(IMOCommand.IMO_MODIFY_QGROUP_NAME_NOTICE);
		// return modifyQgroupNameNoticeInPacket;
		// case IMOCommand.IMO_DESTORY_THE_QGROUP:
		// DestoryTheQgroupInPacket destoryTheQgroupInPacket = new
		// DestoryTheQgroupInPacket(buf, length);
		// destoryTheQgroupInPacket.setCommand(IMOCommand.IMO_DESTORY_THE_QGROUP);
		// return destoryTheQgroupInPacket;
		// case IMOCommand.IMO_QGROUP_KICK_USER_NOTICE:
		// QGroupKickUserNoticeInPacket qcGroupKickUserNoticeInPacket =
		// new
		// QGroupKickUserNoticeInPacket(buf, length);
		// qcGroupKickUserNoticeInPacket.setCommand(IMOCommand.IMO_QGROUP_KICK_USER_NOTICE);
		// return qcGroupKickUserNoticeInPacket;
		// case IMOCommand.IMO_GET_QGROUP_OFFLINE:
		// GetQGroupOfflineInPacket getQGroupOfflineInPacket = new
		// GetQGroupOfflineInPacket(buf, length);
		// getQGroupOfflineInPacket.setCommand(IMOCommand.IMO_GET_QGROUP_OFFLINE);
		// return getQGroupOfflineInPacket;
		// case IMOCommand.IMO_GET_OFFLINE_QGROUP_CHAT_MSG:
		// GetOfflineQGroupChatMsgInPacket
		// getOfflineQGroupChatMsgInPacket = new
		// GetOfflineQGroupChatMsgInPacket(buf, length);
		// getOfflineQGroupChatMsgInPacket.setCommand(IMOCommand.IMO_GET_OFFLINE_QGROUP_CHAT_MSG);
		// return getOfflineQGroupChatMsgInPacket;
		// case IMOCommand.IMO_DELETE_QGROUPD_OFFLINE:
		// DeleteQGroupOfflineInPacket deleteQGroupOfflineInPacket = new
		// DeleteQGroupOfflineInPacket(buf, length);
		// deleteQGroupOfflineInPacket.setCommand(IMOCommand.IMO_DELETE_QGROUPD_OFFLINE);
		// return deleteQGroupOfflineInPacket;
		// case IMOCommand.IMO_QGROUP_DESTROY_NOTICE_USER:
		// QgroupDestroyNoticeUserInPacket
		// qgroupDestroyNoticeUserInPacket = new
		// QgroupDestroyNoticeUserInPacket(buf, length);
		// qgroupDestroyNoticeUserInPacket.setCommand(IMOCommand.IMO_QGROUP_DESTROY_NOTICE_USER);
		// return qgroupDestroyNoticeUserInPacket;
		// default:
		// ErrorPacket error = new ErrorPacket();
		// error.setCommand(IMOCommand.IMO_ERROR_PACKET);
		// return null;
		// }
	}
}

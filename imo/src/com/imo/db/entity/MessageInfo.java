package com.imo.db.entity;

public class MessageInfo {
	public static final int MessageInfo_To = 1;
	public static final int MessageInfo_From = 0;

	public static final int MessageInfo_UnRead = 0;
	public static final int MessageInfo_Readed = 1;

	public static final int MessageInfo_UnFailed = 0;
	public static final int MessageInfo_Failed = 1;
	/**
	 * È±Ê¡µÄID
	 */
	public static final int MessageInfo_MsgId = -1;

	private int sessionId;
	private int fromUid;
	private String fromName;
	private int toUid;
	private String toName;
	private String date;
	private String time;
	private String text;
	private int type;
	private int msgId;
	private int isRead;
	private int isFailed;

	public MessageInfo() {
	}

	public MessageInfo(int sessionId, int fromUid, String fromName, int toUid, String toName, String date, String time, String text, int type, int msgId, int isRead, int isFailed) {
		super();
		this.sessionId = sessionId;
		this.fromUid = fromUid;
		this.fromName = fromName;
		this.toUid = toUid;
		this.toName = toName;
		this.date = date;
		this.time = time;
		this.text = text;
		this.type = type;
		this.msgId = msgId;
		this.isRead = isRead;
		this.isFailed = isFailed;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getFromUid() {
		return fromUid;
	}

	public void setFromUid(int fromUid) {
		this.fromUid = fromUid;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public int getToUid() {
		return toUid;
	}

	public void setToUid(int toUid) {
		this.toUid = toUid;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return sessionId + "   " + fromUid + "   " + fromName + "   " + toUid + "   " + toName + "   " + date + "   " + time + "   " + text + "   " + type;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public int getIsFailed() {
		return isFailed;
	}

	public void setIsFailed(int isFailed) {
		this.isFailed = isFailed;
	}

}

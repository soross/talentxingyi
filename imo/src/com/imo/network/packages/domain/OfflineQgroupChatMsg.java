package com.imo.network.packages.domain;

public class OfflineQgroupChatMsg {
	private int type;
	private int from_cid;
	private int from_uid;
	private int time;
	private int msgid;
	private String msg;

	public OfflineQgroupChatMsg() {
	}

	public OfflineQgroupChatMsg(int type, int from_cid, int from_uid, int time, int msgid, String msg) {
		super();
		this.type = type;
		this.from_cid = from_cid;
		this.from_uid = from_uid;
		this.time = time;
		this.msgid = msgid;
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "OfflineQgroupChatMsg [type=" + type + ", from_cid=" + from_cid + ", from_uid=" + from_uid + ", time=" + time + ", msgid=" + msgid + ", msg=" + msg + "]";
	}

	public int getType() {
		return type;
	}

	public int getFrom_cid() {
		return from_cid;
	}

	public int getFrom_uid() {
		return from_uid;
	}

	public int getTime() {
		return time;
	}

	public int getMsgid() {
		return msgid;
	}

	public String getMsg() {
		return msg;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setFrom_cid(int from_cid) {
		this.from_cid = from_cid;
	}

	public void setFrom_uid(int from_uid) {
		this.from_uid = from_uid;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setMsgid(int msgid) {
		this.msgid = msgid;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}

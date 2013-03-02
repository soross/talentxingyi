package com.imo.network.packages.domain;

public class QgroupOffline {
	private int msg_id;
	private int msgType;
	private int host_cid;
	private int host_uid;
	private int qgroupid;
	private int query_groupid;
	private String group_name;
	private String text;

	public QgroupOffline() {
	}

	public QgroupOffline(int msg_id, int msgType, int host_cid, int host_uid, int qgroupid, int query_groupid, String group_name, String text) {
		super();
		this.msg_id = msg_id;
		this.msgType = msgType;
		this.host_cid = host_cid;
		this.host_uid = host_uid;
		this.qgroupid = qgroupid;
		this.query_groupid = query_groupid;
		this.group_name = group_name;
		this.text = text;
	}

	@Override
	public String toString() {
		return "QgroupOffline [msg_id=" + msg_id + ", msgType=" + msgType + ", host_cid=" + host_cid + ", host_uid=" + host_uid + ", qgroupid=" + qgroupid + ", query_groupid=" + query_groupid + ", group_name=" + group_name + ", text=" + text + "]";
	}

	public int getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(int msg_id) {
		this.msg_id = msg_id;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getHost_cid() {
		return host_cid;
	}

	public void setHost_cid(int host_cid) {
		this.host_cid = host_cid;
	}

	public int getHost_uid() {
		return host_uid;
	}

	public void setHost_uid(int host_uid) {
		this.host_uid = host_uid;
	}

	public int getQgroupid() {
		return qgroupid;
	}

	public void setQgroupid(int qgroupid) {
		this.qgroupid = qgroupid;
	}

	public int getQuery_groupid() {
		return query_groupid;
	}

	public void setQuery_groupid(int query_groupid) {
		this.query_groupid = query_groupid;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}

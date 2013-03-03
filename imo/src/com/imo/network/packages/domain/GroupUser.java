package com.imo.network.packages.domain;

public class GroupUser {
	
	private int cid;
	private int uid;
	private int status;
	
	public GroupUser(int cid, int uid) {
		this.cid = cid;
		this.uid = uid;
	}

	public GroupUser(int cid, int uid, int status) {
		this.cid = cid;
		this.uid = uid;
		this.status = status;
	}
	
	
	public int getCid() {
		return cid;
	}

	public int getUid() {
		return uid;
	}

	public int getStatus() {
		return status;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "GroupUser [cid=" + cid + ", uid=" + uid + ", status=" + status
				+ "]";
	}

	
	
}

package com.imo.network.packages.domain;

/**
 * 2.5.2	新批量获取多人会话信息  会话信息类
 * @author lee
 *
 */
public class DialogueInfo {
	private int unNgroup_id;
	private int unSubRet;
	private int unNgroup_sessionid;
	private String sNgroup_name;
	private String sNgroup_announcement;
	private int unHost_cid;
	private int unHost_uid;
	
	
	
	public DialogueInfo(int unNgroup_id, int unSubRet, int unNgroup_sessionid,
			String sNgroup_name, String sNgroup_announcement, int unHost_cid,
			int unHost_uid) {
		super();
		this.unNgroup_id = unNgroup_id;
		this.unSubRet = unSubRet;
		this.unNgroup_sessionid = unNgroup_sessionid;
		this.sNgroup_name = sNgroup_name;
		this.sNgroup_announcement = sNgroup_announcement;
		this.unHost_cid = unHost_cid;
		this.unHost_uid = unHost_uid;
	}
	
	public int getUnNgroup_id() {
		return unNgroup_id;
	}
	public void setUnNgroup_id(int unNgroup_id) {
		this.unNgroup_id = unNgroup_id;
	}
	public int getUnSubRet() {
		return unSubRet;
	}
	public void setUnSubRet(int unSubRet) {
		this.unSubRet = unSubRet;
	}
	public int getUnNgroup_sessionid() {
		return unNgroup_sessionid;
	}
	public void setUnNgroup_sessionid(int unNgroup_sessionid) {
		this.unNgroup_sessionid = unNgroup_sessionid;
	}
	public String getsNgroup_name() {
		return sNgroup_name;
	}
	public void setsNgroup_name(String sNgroup_name) {
		this.sNgroup_name = sNgroup_name;
	}
	public String getsNgroup_announcement() {
		return sNgroup_announcement;
	}
	public void setsNgroup_announcement(String sNgroup_announcement) {
		this.sNgroup_announcement = sNgroup_announcement;
	}
	public int getUnHost_cid() {
		return unHost_cid;
	}
	public void setUnHost_cid(int unHost_cid) {
		this.unHost_cid = unHost_cid;
	}
	public int getUnHost_uid() {
		return unHost_uid;
	}
	public void setUnHost_uid(int unHost_uid) {
		this.unHost_uid = unHost_uid;
	}
	@Override
	public String toString() {
		return "DialogueInfo [unNgroup_id=" + unNgroup_id + ", unSubRet="
				+ unSubRet + ", unNgroup_sessionid=" + unNgroup_sessionid
				+ ", sNgroup_name=" + sNgroup_name + ", sNgroup_announcement="
				+ sNgroup_announcement + ", unHost_cid=" + unHost_cid
				+ ", unHost_uid=" + unHost_uid + "]";
	}
	
	
}

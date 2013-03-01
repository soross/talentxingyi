package com.imo.module.contact;

import com.imo.util.LogFactory;

/**
 * Outer Contact Item msg
 * 
 * @author CaixiaoLong
 *
 */
public class OuterContactItem{
	
	private int cid;
	private int uid;
	private int groupId;
	private int flag;
	
	
	
	public OuterContactItem() {
	}
	
	public OuterContactItem(int cid, int uid, int groupId, int flag) {
		this.cid = cid;
		this.uid = uid;
		this.groupId = groupId;
		this.flag = flag;
		
		LogFactory.d("Outer", toString());//// outer  contact item info 
	}
	public int getCid() {
		return cid;
	}
	public void setCid(int cid) {
		this.cid = cid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	
	@Override
	public String toString() {
		return "OuterContactItem [cid=" + cid + ", uid=" + uid + ", groupId="
				+ groupId + ", flag=" + flag + "]";
	}
	
	
	
}
package com.imo.module.contact;

import com.imo.util.LogFactory;

/**
 * Outer Contact BasicInfo 
 * 
 * @author CaixiaoLong
 *
 */
public class OuterContactBasicInfo {
	
	private int cid;
	private int uid;
	private String corpAccount;
	private String userAccount;
	private String name;
	private int gender;
	
	public OuterContactBasicInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public OuterContactBasicInfo(int cid, int uid, String corpAccount,
			String userAccount, String name, int gender) {
		this.cid = cid;
		this.uid = uid;
		this.corpAccount = corpAccount;
		this.userAccount = userAccount;
		this.name = name;
		this.gender = gender;
		LogFactory.d("OuterBasicInfo", toString());
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
	public String getCorpAccount() {
		return corpAccount;
	}
	public void setCorpAccount(String corpAccount) {
		this.corpAccount = corpAccount;
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	
	
	
}

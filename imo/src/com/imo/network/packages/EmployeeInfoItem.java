package com.imo.network.packages;

public class EmployeeInfoItem
{
	private int uid;
	private int flag;
	private String user_account;
	private String name;
	private int gender;
	
	public int getUid() {
		return uid;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public String getUser_account() {
		return user_account;
	}
	
	public String getName() {
		return name;
	}
	
	public int getGender() {
		return gender;
	}
	
	public EmployeeInfoItem(int aUid,int aFlag,String aUser_account,String aName,int aGender)
	{
		this.uid = aUid;
		this.flag = aFlag;
		this.user_account = aUser_account;
		this.name = aName;
		this.gender = aGender;
	}

	@Override
	public String toString() {
		return "EmployeeInfoItem [uid=" + uid
				+ ", flag=" + flag + ", user_account=" + user_account
				+ ", name=" + name + ", gender=" + gender + "]";
	}
	
	
}
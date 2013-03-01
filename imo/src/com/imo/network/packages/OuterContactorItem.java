package com.imo.network.packages;

import java.util.ArrayList;

public class OuterContactorItem {
	private int groupID;
	private String groupName;
	
	private ArrayList<Integer> contactorCid = new ArrayList<Integer>();
	private ArrayList<Integer> contactorUid = new ArrayList<Integer>();
	private ArrayList<Integer> flag = new ArrayList<Integer>();
	
	public int getGroupID() {
		return groupID;
	}

	public String getGroupName() {
		return groupName;
	}
	
	
	public ArrayList<Integer> getContactorCid() {
		return contactorCid;
	}

	public ArrayList<Integer> getContactorUid() {
		return contactorUid;
	}

	public ArrayList<Integer> getFlag() {
		return flag;
	}
	
	public OuterContactorItem(int aGroupID,String aGroupName)
	{
		this.groupID = aGroupID;
		this.groupName = aGroupName;
	}

}

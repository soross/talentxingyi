package com.imo.network.packages;

import java.util.ArrayList;

public class InnerContactorItem {
	
	private int groupID;
	private String groupName;
	private ArrayList<Integer> contactorID = new ArrayList<Integer>();
	
	public int getGroupID() {
		return groupID;
	}

	public String getGroupName() {
		return groupName;
	}

	public ArrayList<Integer> getContactorID() {
		return contactorID;
	}
	
	public InnerContactorItem(int aGroupID,String aGroupName)
	{
		this.groupID = aGroupID;
		this.groupName = aGroupName;
	}

}

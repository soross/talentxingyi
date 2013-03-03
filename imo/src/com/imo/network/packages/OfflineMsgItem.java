package com.imo.network.packages;

public class OfflineMsgItem {
	
	private int fromcid;
	private int fromuid;
	private int time;
	private int msgid;
	private String msg;
	
	public int getFromcid() {
		return fromcid;
	}
	
	public int getFromuid() {
		return fromuid;
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
	
	public OfflineMsgItem(int aFromcid,int aFromuid,int aTime,int aMsgid,String aMsg)
	{
		fromcid = aFromcid;
		fromuid = aFromuid;
		time = aTime;
		msgid = aMsgid;
		msg = aMsg;
	}
	
	public OfflineMsgItem(int aTime,int aMsgid,String aMsg)
	{
		time = aTime;
		msgid = aMsgid;
		msg = aMsg;
	}
}


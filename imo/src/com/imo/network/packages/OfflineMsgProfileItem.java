package com.imo.network.packages;


public class OfflineMsgProfileItem {
	
	private int subresult;
	private int count;
	private int fromcid;
	private int fromuid;
	private int time;
	private String lastmsg;
	
	public int getSubresult() {
		return subresult;
	}

	public int getCount() {
		return count;
	}

	public int getFromcid() {
		return fromcid;
	}

	public int getFromuid() {
		return fromuid;
	}

	public int getTime() {
		return time;
	}

	public String getLastmsg() {
		return lastmsg;
	}
	
	public OfflineMsgProfileItem(int aSubResult,int aCount,int aFromcid,int aFromuid,int aTime,String aLastMsg)
	{
		subresult = aSubResult;
		count = aCount;
		fromcid = aFromcid;
		fromuid = aFromuid;
		time = aTime;
		lastmsg =  aLastMsg;
	}

	@Override
	public String toString() {
		return "OfflineMsgProfileItem [subresult=" + subresult + ", count="
				+ count + ", fromcid=" + fromcid + ", fromuid=" + fromuid
				+ ", time=" + time + ", lastmsg=" + lastmsg + "]";
	}

	
	

}

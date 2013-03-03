package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetAllEmployeesUIDInPacket extends CommonInPacket{
	
	private short commandRet;
	private int deptid;
	private byte endflag;
	private int tnum;
	private int num;
	
	private int[] uid;
	private int[] nextSibling;
	
	

	public short getCommandRet() {
		return commandRet;
	}



	public int getDeptid() {
		return deptid;
	}



	public byte getEndflag() {
		return endflag;
	}



	public int getTnum() {
		return tnum;
	}



	public int getNum() {
		return num;
	}



	public int[] getUid() {
		return uid;
	}



	public int[] getNextSibling() {
		return nextSibling;
	}



	public GetAllEmployeesUIDInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		commandRet = body.getShort();
		if (commandRet != 0)
			return;
		
		deptid = body.getInt();
		endflag = body.get();
		tnum = body.getInt();
		num = body.getInt();
		
		uid = new int[num];
		nextSibling =  new int[num];
		
		for(int i = 0; i < num; i++)
		{
			uid[i] = body.getInt();
			nextSibling[i] = body.getInt();
		}
		
	}

}

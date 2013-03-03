package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetDeptUCInPacket extends CommonInPacket{

	private short commandRet = -1;
	private byte endflag;
	private int tnum;
	private int num;
	private int[] deptid;
	private int[] dept_uc;
	private int[] dept_user_uc;
	
	public short getCommandRet() {
		return commandRet;
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

	public int[] getDeptid() {
		return deptid;
	}

	public int[] getDept_uc() {
		return dept_uc;
	}

	public int[] getDept_user_uc() {
		return dept_user_uc;
	}

	public GetDeptUCInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		
		// TODO Auto-generated constructor stub
		commandRet = body.getShort();
		
		if(commandRet != 0)
			return;
		
		endflag = body.get();
		tnum = body.getInt();
		num = body.getInt();
		
		deptid = new int[num];
		dept_uc = new int[num];
		dept_user_uc = new int[num];
		
		for( int i = 0;  i < num; i++ )
		{
			deptid[i] = body.getInt();
			dept_uc[i] = body.getInt();
			dept_user_uc[i] = body.getInt();
		}
	}

}

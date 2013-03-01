package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetDeptInfoInPacket extends CommonInPacket{

	private short commandRet;
	private int   deptid;
	private int   dept_uc;
	private int   mask;
	
	
	public short getCommandRet() {
		return commandRet;
	}


	public int getDeptid() {
		return deptid;
	}


	public int getDept_uc() {
		return dept_uc;
	}


	public int getMask() {
		return mask;
	}

	public DeptMaskItem getMaskItem(){return new DeptMaskItem(mask, body);}

	public GetDeptInfoInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		commandRet = body.getShort();
		if(commandRet != 0)
			return;
		
		deptid = body.getInt();
		dept_uc = body.getInt();
		mask = body.getInt();
	}

}

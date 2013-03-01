package com.imo.network.packages;

import java.nio.ByteBuffer;

import android.util.Log;

public class GetEmployeeProfileInPacket extends CommonInPacket{

	public int getUnTrandsID() {
		return unTrandsID;
	}

	public short getRet() {
		return ret;
	}

	public int getCid() {
		return cid;
	}

	public int getUid() {
		return uid;
	}

	public int getMask() {
		return mask;
	}
	
	public EmployeeProfileItem getEmployeeItem() {
		return employeeItem;
	}

	private int unTrandsID;
	private short ret;
	private int cid;
	private int uid;
	private int mask;
	private EmployeeProfileItem employeeItem;

	public GetEmployeeProfileInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		unTrandsID = body.getInt();
		ret = body.getShort();
		
		if (ret != 0)
			return;
		
		cid = body.getInt();
		uid = body.getInt();
		
		Log.e("GetEmployeeProfileInPacket", "cid :"+cid+", uid :"+uid);
		
		mask = body.getInt();
		employeeItem = new EmployeeProfileItem(mask, body);
	}

}

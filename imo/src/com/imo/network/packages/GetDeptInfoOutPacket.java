package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetDeptInfoOutPacket extends CommonOutPacket{

	public GetDeptInfoOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateDeptInfoBody(int aDeptID,int aDeptUC,int aMask)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(64);
		dataBuffer.putInt(aDeptID);
		dataBuffer.putInt(aDeptUC);
		dataBuffer.putInt(aMask);
		
		return dataBuffer;
	}
}

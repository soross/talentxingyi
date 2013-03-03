package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetAllEmployeesInfoOutPacket extends CommonOutPacket{

	public GetAllEmployeesInfoOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateEmployeesBasicInfoBody(int aContactorsNum,int[] aContactorUidArray)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(512);
		dataBuffer.putInt(aContactorsNum);
		
		for(int i = 0; i < aContactorsNum; i++)
		{
			dataBuffer.putInt(aContactorUidArray[i]);	
		}
		
		return dataBuffer;
	}
}

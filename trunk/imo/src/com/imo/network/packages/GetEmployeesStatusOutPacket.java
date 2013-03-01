package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetEmployeesStatusOutPacket extends CommonOutPacket{

	public GetEmployeesStatusOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	
	public static ByteBuffer GenerateEmployeesStatusBody(int aContactorsNum,int[] aContactorCidArray,int[] aContactorUidArray)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(aContactorsNum*8+64);
		dataBuffer.putInt(aContactorsNum);
		
		for(int i = 0; i < aContactorsNum; i++)
		{
			dataBuffer.putInt(aContactorCidArray[i]);	
			dataBuffer.putInt(aContactorUidArray[i]);
		}
		
		return dataBuffer;
	}

}

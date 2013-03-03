package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetAllEmployeesUIDOutPacket extends CommonOutPacket{

	public GetAllEmployeesUIDOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateEmplyeesUIDBody(int aDeptID)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(64);
		dataBuffer.putInt(aDeptID);
		
		return dataBuffer;
	}
}

package com.imo.network.packages;

import java.nio.ByteBuffer;


public class UserStatusChangeOutPacket extends CommonOutPacket{

	public UserStatusChangeOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateStatusChangeBody(short aStatus)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(4);
		
		dataBuffer.putShort(aStatus);
		
		return dataBuffer;
	}

}

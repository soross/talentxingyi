package com.imo.network.packages;

import java.nio.ByteBuffer;

public class ContactorGroupUCOutPacket extends CommonOutPacket{

	public ContactorGroupUCOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateGroupUCBody(int untransID)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(8);
		dataBuffer.putInt(untransID);
		
		return dataBuffer;
	}

}

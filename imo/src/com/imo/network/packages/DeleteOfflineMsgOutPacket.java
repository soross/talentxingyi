package com.imo.network.packages;

import java.nio.ByteBuffer;

public class DeleteOfflineMsgOutPacket extends CommonOutPacket{

	public DeleteOfflineMsgOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateDeleteOffMsgBody(int aFromcid,int aFromuid,int aFromid)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(32);
		dataBuffer.putInt(aFromcid);
		dataBuffer.putInt(aFromuid);
		dataBuffer.putInt(aFromid);
		
		return dataBuffer;
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetOffMsgFromContactorOutPacket extends CommonOutPacket{

	public GetOffMsgFromContactorOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateOffMsgFormContInfoBody(int aFromcid, int aFromuid) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(64);
		dataBuffer.putInt(aFromcid);
		dataBuffer.putInt(aFromuid);

		return dataBuffer;
	}

}

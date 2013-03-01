package com.imo.network.packages;

import java.nio.ByteBuffer;

public class StatusAckOutPacket extends CommonOutPacket{

	public StatusAckOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateStatusAckBody(int aServerMsgId) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(32);
		dataBuffer.putInt(aServerMsgId);

		return dataBuffer;
	}
}

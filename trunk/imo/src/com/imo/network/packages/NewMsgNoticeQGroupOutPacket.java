package com.imo.network.packages;

import java.nio.ByteBuffer;

public class NewMsgNoticeQGroupOutPacket extends CommonOutPacket {

	public NewMsgNoticeQGroupOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateNewMsgNoticeQGroupBody(int transid) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(4);
		dataBuffer.putInt(transid);
		return dataBuffer;
	}

}

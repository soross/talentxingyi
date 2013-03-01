package com.imo.network.packages;

import java.nio.ByteBuffer;

public class LoginQGroupOutPacket extends CommonOutPacket {

	public LoginQGroupOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateLoginGroupBody(int aTransid, int aGroupid,
			int aUnStatus) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(12);
		dataBuffer.putInt(aTransid);
		dataBuffer.putInt(aGroupid);
		dataBuffer.putInt(aUnStatus);

		return dataBuffer;
	}
}

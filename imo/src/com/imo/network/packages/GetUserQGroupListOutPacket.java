package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetUserQGroupListOutPacket extends CommonOutPacket {

	public GetUserQGroupListOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateUserQGroupListBody(int aTransid) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(4);
		dataBuffer.putInt(aTransid);
		return dataBuffer;
	}
}

package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetUserQGroupListUCOutPacket extends CommonOutPacket {

	public GetUserQGroupListUCOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateUserQGroupListUCBody(int aTransid) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(4);
		dataBuffer.putInt(aTransid);
		return dataBuffer;
	}
}

package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetQGroupUserListOutPacket extends CommonOutPacket {

	public GetQGroupUserListOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateQGroupUserListBody(int aTransid, int qgroup_id) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8);
		dataBuffer.putInt(aTransid);
		dataBuffer.putInt(qgroup_id);
		return dataBuffer;
	}
}

package com.imo.network.packages;

import java.nio.ByteBuffer;


public class GetQGroupOfflineOutPacket extends CommonOutPacket {

	public GetQGroupOfflineOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateQGroupOfflineBody(int aTransid,int qgroup_id) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8);
		dataBuffer.putInt(aTransid);
		dataBuffer.putInt(qgroup_id);
		return dataBuffer;
	}
}

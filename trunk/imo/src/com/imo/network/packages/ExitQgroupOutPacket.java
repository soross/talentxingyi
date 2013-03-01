package com.imo.network.packages;

import java.nio.ByteBuffer;

public class ExitQgroupOutPacket extends CommonOutPacket {

	public ExitQgroupOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateExitQgroupBody(int aTransid,int qgroup_id) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8);
		dataBuffer.putInt(aTransid);
		dataBuffer.putInt(qgroup_id);
		return dataBuffer;
	}
}

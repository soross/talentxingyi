package com.imo.network.packages;

import java.nio.ByteBuffer;

public class BatchGetUserQGroupListMulretOutPacket extends CommonOutPacket {

	public BatchGetUserQGroupListMulretOutPacket(ByteBuffer aBody,
			short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateUserQGroupListMulretBody(int aTransid,
			int qgroupNum, int[] qgroup_id) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8 + qgroupNum * 4);
		dataBuffer.putInt(aTransid);
		dataBuffer.putInt(qgroupNum);
		for (int i = 0; i < qgroupNum; i++) {
			dataBuffer.putInt(qgroup_id[i]);
		}
		return dataBuffer;
	}
}

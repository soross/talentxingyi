package com.imo.network.packages;

import java.nio.ByteBuffer;

public class DeleteQGroupOfflineOutPacket extends CommonOutPacket {

	public DeleteQGroupOfflineOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateQGroupOfflineBody(int aTransid, int num,
			int[] msg_id) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8 + num * 4);
		dataBuffer.putInt(aTransid);
		dataBuffer.putInt(num);
		for (int i = 0; i < num; i++) {
			dataBuffer.putInt(msg_id[i]);
		}
		return dataBuffer;
	}
}

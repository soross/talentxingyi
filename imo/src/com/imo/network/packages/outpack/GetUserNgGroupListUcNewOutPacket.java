package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;
import java.util.List;

import com.imo.network.packages.CommonOutPacket;

public class GetUserNgGroupListUcNewOutPacket extends CommonOutPacket{

	public GetUserNgGroupListUcNewOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateUserNgGroupListUcNewBody(int unTransID, int unNgroupNum,int[] unNgroupId) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8+ unNgroupId.length*4);
		dataBuffer.putInt(unTransID);
		dataBuffer.putInt(unNgroupNum);
		for (int i = 0; i < unNgroupId.length; i++) {
			dataBuffer.putInt(unNgroupId[i]);
		}
		return dataBuffer;
	}
}

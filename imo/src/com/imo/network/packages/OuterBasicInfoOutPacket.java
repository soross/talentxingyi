package com.imo.network.packages;

import java.nio.ByteBuffer;

public class OuterBasicInfoOutPacket extends CommonOutPacket{

	public OuterBasicInfoOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateOuterBasicInfoBody(int aCid,int aUid) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(32);
		dataBuffer.putInt(aCid);
		dataBuffer.putInt(aUid);

		return dataBuffer;
	}
}

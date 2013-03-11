package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetCorpInfoOutPacket extends CommonOutPacket {

	public GetCorpInfoOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateCorpInfoBody(int aContactorID, int aMask) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(64);
		dataBuffer.putInt(aContactorID);
		dataBuffer.putInt(aMask);

		return dataBuffer;
	}

}

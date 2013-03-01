package com.imo.network.packages;

import java.nio.ByteBuffer;

public class HeartBeatOutPacket extends CommonOutPacket {

	public HeartBeatOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetDeptUCOutPacket extends CommonOutPacket{

	public GetDeptUCOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

}

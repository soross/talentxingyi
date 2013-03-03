package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonOutPacket;

public class GetUserNgGroupListUcOutPacket extends CommonOutPacket{

	public GetUserNgGroupListUcOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateUserNgGroupListUcBody(int aTransid) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(4);
		dataBuffer.putInt(aTransid);
		return dataBuffer;
	}
	
}

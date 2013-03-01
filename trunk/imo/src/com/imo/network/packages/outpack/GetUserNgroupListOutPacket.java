package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonOutPacket;

public class GetUserNgroupListOutPacket extends CommonOutPacket{
	
	public GetUserNgroupListOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}


	public static ByteBuffer GenerateUserNgroupListBody(int transid ) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(4);
		dataBuffer.putInt(transid);
		return dataBuffer;
	}
}

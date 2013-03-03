package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonOutPacket;

public class GetClientLoginNgroupdOutPacket extends CommonOutPacket{

	public GetClientLoginNgroupdOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		
	}
	
	public static ByteBuffer GenerateClientLoginNgroupdBody(int transid , int ngroup_id , int unStatus) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(12);
		dataBuffer.putInt(transid);
		dataBuffer.putInt(ngroup_id);
		dataBuffer.putInt(unStatus);
		return dataBuffer;
	}
}

package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonOutPacket;

public class GetAgreeJoinNgroupOutPacket extends CommonOutPacket{

	public GetAgreeJoinNgroupOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateClientLoginNgroupdBody(int transid , int ngroup_id , int status) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(12);
		dataBuffer.putInt(transid);
		dataBuffer.putInt(ngroup_id);
		dataBuffer.putInt(status);
		return dataBuffer;
	}
}

package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonOutPacket;

public class GetNgroupUserListOutPacket extends CommonOutPacket{

	public GetNgroupUserListOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateNgroupUserListBody(int transid , int ngroup_id) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8);
		dataBuffer.putInt(transid);
		dataBuffer.putInt(ngroup_id);
		return dataBuffer;
	}
}
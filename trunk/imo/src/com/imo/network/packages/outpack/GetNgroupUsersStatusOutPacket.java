package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonOutPacket;

public class GetNgroupUsersStatusOutPacket extends CommonOutPacket{

	public GetNgroupUsersStatusOutPacket(ByteBuffer aBody, short aCommand,
			int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateUserNgGroupListUcNewBody(int transid, int ngroup_id) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8);
		dataBuffer.putInt(transid);
		dataBuffer.putInt(ngroup_id);
		return dataBuffer;
	}

}

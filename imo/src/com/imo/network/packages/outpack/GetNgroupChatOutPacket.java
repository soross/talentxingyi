package com.imo.network.packages.outpack;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonOutPacket;

public class GetNgroupChatOutPacket extends CommonOutPacket{

	public GetNgroupChatOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateNgroupChatBody(int transid , int ngroup_id, String msg) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(8+StringUtils.ToUnicode(msg).length);
		dataBuffer.putInt(transid);
		dataBuffer.putInt(ngroup_id);
		dataBuffer.putInt(ngroup_id);
		dataBuffer.putInt(StringUtils.ToUnicode(msg).length);
		dataBuffer.put(StringUtils.ToUnicode(msg));
		return dataBuffer;
	}
}

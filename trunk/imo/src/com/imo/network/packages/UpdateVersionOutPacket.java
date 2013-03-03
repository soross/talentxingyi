package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class UpdateVersionOutPacket extends CommonOutPacket{

	public UpdateVersionOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateUpdateVersionBody(byte aVersion,short aBuild,byte aDevNo,String aDevType) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(StringUtils.ToUnicode(aDevType).length+32);
		dataBuffer.put(aVersion);
		dataBuffer.putShort(aBuild);
		dataBuffer.put(aDevNo);
		dataBuffer.putInt(StringUtils.ToUnicode(aDevType).length);
		dataBuffer.put(StringUtils.ToUnicode(aDevType));

		return dataBuffer;
	}

}

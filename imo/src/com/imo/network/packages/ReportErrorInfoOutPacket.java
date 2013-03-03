package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class ReportErrorInfoOutPacket extends CommonOutPacket{

	public ReportErrorInfoOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateErrorInfoBody(String aJsonStr) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(StringUtils.ToUnicode(aJsonStr).length+32);
		dataBuffer.putInt(StringUtils.ToUnicode(aJsonStr).length);
		dataBuffer.put(StringUtils.ToUnicode(aJsonStr));

		return dataBuffer;
	}

}

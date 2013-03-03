package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class SendMsgOutPacket extends CommonOutPacket{

	public SendMsgOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}

	public static ByteBuffer GenerateMsgTextBody(int aTocid,int aTouid,String aMsg)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(StringUtils.ToUnicode(aMsg).length+32);
		dataBuffer.putInt(aTocid);
		dataBuffer.putInt(aTouid);
		dataBuffer.putInt(StringUtils.ToUnicode(aMsg).length);
		dataBuffer.put(StringUtils.ToUnicode(aMsg));
		
		return dataBuffer;
	}
	
}

package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class QGroupChatOutPacket extends CommonOutPacket{

	public QGroupChatOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateQGroupChatBody(int transid,int qgroup_id,String aMsg)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(StringUtils.ToUnicode(aMsg).length+32);
		dataBuffer.putInt(transid);
		dataBuffer.putInt(qgroup_id);
		dataBuffer.putInt(StringUtils.ToUnicode(aMsg).length);
		dataBuffer.put(StringUtils.ToUnicode(aMsg));
		
		return dataBuffer;
	}
	
}

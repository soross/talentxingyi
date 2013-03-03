package com.imo.network.packages;

import java.nio.ByteBuffer;

public class SendMsgAckOutPacket extends CommonOutPacket{

	public SendMsgAckOutPacket(ByteBuffer aBody, short aCommand, int aCid,
			int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
	}
	
	public static ByteBuffer GenerateMsgTextAckBody(int aTocid,int aTouid,int aServerMsgId)
	{
		ByteBuffer dataBuffer = ByteBuffer.allocate(32);
		dataBuffer.putInt(aTocid);
		dataBuffer.putInt(aTouid);
		dataBuffer.putInt(aServerMsgId);
		
		return dataBuffer;
	}

}

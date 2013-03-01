package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class SendMsgInPacket extends CommonInPacket{

	private int fromcid;
	private int fromuid;
	private int server_msg_id;
	private String msg;
	
	public int getFromcid() {
		return fromcid;
	}


	public int getFromuid() {
		return fromuid;
	}


	public int getServer_msg_id() {
		return server_msg_id;
	}


	public String getMsg() {
		return msg;
	}
	
	
	public SendMsgInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		fromcid = body.getInt();
		fromuid = body.getInt();
		server_msg_id = body.getInt();
		
		int msgLen = body.getInt();
		byte[] msgBuffer = new byte[msgLen];
		body.get(msgBuffer);
		msg = StringUtils.UNICODE_TO_UTF8(msgBuffer);
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;

public class UserStatusChangeInPacket extends CommonInPacket{

	public int getCid() {
		return cid;
	}

	public int getUid() {
		return uid;
	}

	public short getStatus() {
		return status;
	}

	public int getType() {
		return type;
	}
	
	public int getServer_msg_id() {
		return server_msg_id;
	}
	
	private int cid;
	private int uid;
	private short status;
	private int type;
	private int server_msg_id;
	

	public UserStatusChangeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		cid = body.getInt();
		uid = body.getInt();
		status = body.getShort();
		type = body.getInt();
		
		if( body.limit() > body.position())
			server_msg_id = body.getInt();
	}

}

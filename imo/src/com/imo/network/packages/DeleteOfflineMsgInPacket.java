package com.imo.network.packages;

import java.nio.ByteBuffer;

public class DeleteOfflineMsgInPacket extends CommonInPacket{


	private short ret;
	private int fromcid;
	private int fromuid;
	private int id;
	
	public short getRet() {
		return ret;
	}


	public int getFromcid() {
		return fromcid;
	}


	public int getFromuid() {
		return fromuid;
	}


	public int getId() {
		return id;
	}
	
	
	public DeleteOfflineMsgInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		ret = body.getShort();
		if(ret != 0)
			return;
		
		fromcid = body.getInt();
		fromuid = body.getInt();
		id = body.getInt();
	}

}

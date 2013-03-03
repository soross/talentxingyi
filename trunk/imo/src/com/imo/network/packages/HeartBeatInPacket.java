package com.imo.network.packages;

import java.nio.ByteBuffer;

public class HeartBeatInPacket extends CommonInPacket{

	private int time;
	
	public int getTime() {
		return time;
	}
	
	public HeartBeatInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		time = body.getInt();
	}

}

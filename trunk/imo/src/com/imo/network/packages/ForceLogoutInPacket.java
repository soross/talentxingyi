package com.imo.network.packages;

import java.nio.ByteBuffer;

public class ForceLogoutInPacket extends CommonInPacket{

	private short ret;
	
	public short getRet() {
		return ret;
	}

	public ForceLogoutInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		ret = body.getShort();
	}

}

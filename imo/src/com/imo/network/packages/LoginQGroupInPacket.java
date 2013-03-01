package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;

public class LoginQGroupInPacket extends CommonInPacket {
	private int transid;
	private int ret;

	public LoginQGroupInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		LogFactory.d("", this.toString());
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	@Override
	public String toString() {
		return "LoginGroupInPacket [transid=" + transid + ", ret=" + ret + "]";
	}
	
	
	
	
	
}

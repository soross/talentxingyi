package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetClientLoginNgroupdInPacket extends CommonInPacket{

	private int transid;
	private int ret;
	
	public GetClientLoginNgroupdInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		
		LogFactory.d("GetClientLoginNgroupdInPacket...", this.toString());
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	@Override
	public String toString() {
		return "GetClientLoginNgroupdInPacket [transid=" + transid + ", ret="
				+ ret + "]";
	}

	
}

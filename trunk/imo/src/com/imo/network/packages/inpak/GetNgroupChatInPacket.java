package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetNgroupChatInPacket extends CommonInPacket {

	private int transid;
	
	public GetNgroupChatInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		
		LogFactory.d("GetNgroupChatInPacket...", this.toString());
	}

	public int getTransid() {
		return transid;
	}

	@Override
	public String toString() {
		return "GetNgroupChatInPacket [transid=" + transid + "]";
	}


}

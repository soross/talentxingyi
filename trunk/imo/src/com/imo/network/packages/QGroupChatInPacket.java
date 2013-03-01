package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;

public class QGroupChatInPacket extends CommonInPacket {

	private int transid;

	public QGroupChatInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "QGroupChatInPacket [transid=" + transid + "]";
	}

	public int getTransid() {
		return transid;
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;
import com.imo.util.LogFactory;

public class DeleteQGroupOfflineInPacket extends CommonInPacket {
	private int transid;
	private int ret;

	public DeleteQGroupOfflineInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();

		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "DeleteQGroupOfflineInPacket [transid=" + transid + ", ret="
				+ ret + "]";
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

}

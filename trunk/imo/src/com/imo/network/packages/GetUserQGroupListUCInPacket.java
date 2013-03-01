package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;

public class GetUserQGroupListUCInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int userQgroupListUC;

	public GetUserQGroupListUCInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		userQgroupListUC = body.getInt();
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "GetUserQGroupListUCInPacket [transid=" + transid + ", ret="
				+ ret + ", userQgroupListUC=" + userQgroupListUC + "]";
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getUserQgroupListUC() {
		return userQgroupListUC;
	}

}

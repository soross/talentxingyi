package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;

public class ExitQgroupInPacket extends CommonInPacket{

	private int transid;
	private int ret;
	private int qgroup_id;
	
	public ExitQgroupInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		qgroup_id = body.getInt();
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "ExitQgroupInPacket [transid=" + transid + ", ret=" + ret
				+ ", qgroup_id=" + qgroup_id + "]";
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getQgroup_id() {
		return qgroup_id;
	}
	
	

}

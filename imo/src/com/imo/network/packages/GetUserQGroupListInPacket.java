package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.imo.util.LogFactory;

public class GetUserQGroupListInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int num;
	private int[] qgroup_ids;

	public GetUserQGroupListInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		num = body.getInt();
		qgroup_ids = new int[num];
		for (int i = 0; i < num; i++) {
			qgroup_ids[i] = body.getInt();
		}
		LogFactory.d("", this.toString());
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getNum() {
		return num;
	}

	public int[] getQgroup_ids() {
		return qgroup_ids;
	}

	@Override
	public String toString() {
		return "GetUserQGroupListInPacket [transid=" + transid + ", ret=" + ret
				+ ", num=" + num + ", qgroup_ids="
				+ Arrays.toString(qgroup_ids) + "]";
	}

}

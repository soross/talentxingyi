package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.imo.util.LogFactory;

public class GetQGroupUsersStatusInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int qgroup_id;
	private int num;
	private int[] cids;
	private int[] uids;
	private int[] status;

	public GetQGroupUsersStatusInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		qgroup_id = body.getInt();
		num = body.getInt();
		cids = new int[num];
		uids = new int[num];
		status = new int[num];
		for (int i = 0; i < num; i++) {
			cids[i] = body.getInt();
			uids[i] = body.getInt();
			status[i] = body.getInt();
		}

		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "GetQGroupUsersStatusInPacket [transid=" + transid + ", ret="
				+ ret + ", qgroup_id=" + qgroup_id + ", num=" + num + ", cids="
				+ Arrays.toString(cids) + ", uids=" + Arrays.toString(uids)
				+ ", status=" + Arrays.toString(status) + "]";
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

	public int getNum() {
		return num;
	}

	public int[] getCids() {
		return cids;
	}

	public int[] getUids() {
		return uids;
	}

	public int[] getStatus() {
		return status;
	}

}

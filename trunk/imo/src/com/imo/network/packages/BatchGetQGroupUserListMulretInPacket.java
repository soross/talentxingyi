package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.imo.util.LogFactory;

public class BatchGetQGroupUserListMulretInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int endflag;
	private int totalNum;
	private int num;
	private int[] cids;
	private int[] uids;

	public BatchGetQGroupUserListMulretInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		endflag = body.getInt();
		totalNum = body.getInt();
		num = body.getInt();
		cids = new int[num];
		uids = new int[num];
		for (int i = 0; i < num; i++) {
			cids[i] = body.getInt();
			uids[i] = body.getInt();
		}
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "BatchGetQGroupUserListMulretInPacket [transid=" + transid
				+ ", ret=" + ret + ", endflag=" + endflag + ", totalNum="
				+ totalNum + ", num=" + num + ", cids=" + Arrays.toString(cids)
				+ ", uids=" + Arrays.toString(uids) + "]";
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getEndflag() {
		return endflag;
	}

	public int getTotalNum() {
		return totalNum;
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

}

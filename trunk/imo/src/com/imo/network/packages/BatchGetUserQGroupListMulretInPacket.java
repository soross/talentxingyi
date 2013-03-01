package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class BatchGetUserQGroupListMulretInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int endflag;
	private int totalNum;
	private int num;
	private int[] qgroup_ids;
	private int[] subRets;
	private int[] qgroup_sessionids;
	private String[] qgroup_names;
	private String[] qgroup_announcements;
	private int[] host_cids;
	private int[] host_uids;

	public BatchGetUserQGroupListMulretInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		endflag = body.getInt();
		totalNum = body.getInt();
		num = body.getInt();
		qgroup_ids = new int[num];
		subRets = new int[num];
		qgroup_sessionids = new int[num];
		qgroup_names = new String[num];
		qgroup_announcements = new String[num];
		host_cids = new int[num];
		host_uids = new int[num];
		for (int i = 0; i < num; i++) {
			qgroup_ids[i] = body.getInt();
			subRets[i] = body.getInt();
			qgroup_sessionids[i] = body.getInt();

			int qgroup_name_length = body.getInt();
			byte[] qgroup_name_buffer = new byte[qgroup_name_length];
			body.get(qgroup_name_buffer);
			qgroup_names[i] = StringUtils.UNICODE_TO_UTF8(qgroup_name_buffer);

			int qgroup_announcement_length = body.getInt();
			byte[] qgroup_announcement_buffer = new byte[qgroup_announcement_length];
			body.get(qgroup_announcement_buffer);
			qgroup_announcements[i] = StringUtils
					.UNICODE_TO_UTF8(qgroup_announcement_buffer);

			host_cids[i] = body.getInt();
			host_uids[i] = body.getInt();
		}
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "BatchGetUserQGroupListMulretInPacket [transid=" + transid
				+ ", ret=" + ret + ", endflag=" + endflag + ", totalNum="
				+ totalNum + ", num=" + num + ", qgroup_ids="
				+ Arrays.toString(qgroup_ids) + ", subRets="
				+ Arrays.toString(subRets) + ", qgroup_sessionids="
				+ Arrays.toString(qgroup_sessionids) + ", qgroup_names="
				+ Arrays.toString(qgroup_names) + ", qgroup_announcements="
				+ Arrays.toString(qgroup_announcements) + ", host_cids="
				+ Arrays.toString(host_cids) + ", host_uids="
				+ Arrays.toString(host_uids) + "]";
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

	public int[] getQgroup_ids() {
		return qgroup_ids;
	}

	public int[] getSubRets() {
		return subRets;
	}

	public int[] getQgroup_sessionids() {
		return qgroup_sessionids;
	}

	public String[] getQgroup_names() {
		return qgroup_names;
	}

	public String[] getQgroup_announcements() {
		return qgroup_announcements;
	}

	public int[] getHost_cids() {
		return host_cids;
	}

	public int[] getHost_uids() {
		return host_uids;
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.domain.OfflineQgroupChatMsg;
import com.imo.util.LogFactory;

public class GetOfflineQGroupChatMsgInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int qgroup_id;
	private int endflag;
	private int totalNum;
	private int num;
	private OfflineQgroupChatMsg[] offlineQgroupChatMsgs;

	public GetOfflineQGroupChatMsgInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		qgroup_id = body.getInt();
		endflag = body.getInt();
		totalNum = body.getInt();
		num = body.getInt();
		offlineQgroupChatMsgs = new OfflineQgroupChatMsg[num];
		for (int i = 0; i < num; i++) {
			offlineQgroupChatMsgs[i].setType(body.getInt());
			offlineQgroupChatMsgs[i].setFrom_cid(body.getInt());
			offlineQgroupChatMsgs[i].setFrom_uid(body.getInt());
			offlineQgroupChatMsgs[i].setTime(body.getInt());
			offlineQgroupChatMsgs[i].setMsgid(body.getInt());

			int msg_length = body.getInt();
			byte[] msg_buffer = new byte[msg_length];
			body.get(msg_buffer);
			offlineQgroupChatMsgs[i].setMsg(StringUtils
					.UNICODE_TO_UTF8(msg_buffer));
		}

		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "GetOfflineQGroupChatMsgInPacket [transid=" + transid + ", ret="
				+ ret + ", qgroup_id=" + qgroup_id + ", endflag=" + endflag
				+ ", totalNum=" + totalNum + ", num=" + num
				+ ", offlineQgroupChatMsgs="
				+ Arrays.toString(offlineQgroupChatMsgs) + "]";
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

	public int getEndflag() {
		return endflag;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public int getNum() {
		return num;
	}

	public OfflineQgroupChatMsg[] getOfflineQgroupChatMsgs() {
		return offlineQgroupChatMsgs;
	}

}

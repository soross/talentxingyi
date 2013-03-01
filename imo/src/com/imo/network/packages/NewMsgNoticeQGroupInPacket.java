package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class NewMsgNoticeQGroupInPacket extends CommonInPacket {

	private int type;
	private int qgroup_id;
	private int from_cid;
	private int from_uid;
	private int time;
	private String msg;

	public NewMsgNoticeQGroupInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		type = body.getInt();
		qgroup_id = body.getInt();
		from_cid = body.getInt();
		from_uid = body.getInt();
		time = body.getInt();

		int msg_length = body.getInt();
		byte[] msg_buffer = new byte[msg_length];
		body.get(msg_buffer);
		msg = StringUtils.UNICODE_TO_UTF8(msg_buffer);
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "NewMsgNoticeQGroupInPacket [type=" + type + ", qgroup_id="
				+ qgroup_id + ", from_cid=" + from_cid + ", from_uid="
				+ from_uid + ", time=" + time + ", msg=" + msg + "]";
	}

	public int getType() {
		return type;
	}

	public int getQgroup_id() {
		return qgroup_id;
	}

	public int getFrom_cid() {
		return from_cid;
	}

	public int getFrom_uid() {
		return from_uid;
	}

	public int getTime() {
		return time;
	}

	public String getMsg() {
		return msg;
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class ModifyQgroupNameNoticeInPacket extends CommonInPacket {

	private int qgroup_id;
	private String qgroup_name;

	public ModifyQgroupNameNoticeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		qgroup_id = body.getInt();

		int qgroup_name_length = body.getInt();
		byte[] qgroup_name_buffer = new byte[qgroup_name_length];
		body.get(qgroup_name_buffer);
		qgroup_name = StringUtils.UNICODE_TO_UTF8(qgroup_name_buffer);
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "ModifyQgroupNameNoticeInPacket [qgroup_id=" + qgroup_id
				+ ", qgroup_name=" + qgroup_name + "]";
	}

	public int getQgroup_id() {
		return qgroup_id;
	}

	public String getQgroup_name() {
		return qgroup_name;
	}
}

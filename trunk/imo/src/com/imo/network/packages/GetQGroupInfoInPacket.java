package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class GetQGroupInfoInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int qgroup_id;
	private int qgroup_sessionid;
	private String qgroup_name;
	private String qgroup_announcement;
	private int host_cid;

	public int getQgroup_id() {
		return qgroup_id;
	}

	public int getQgroup_sessionid() {
		return qgroup_sessionid;
	}

	public String getQgroup_name() {
		return qgroup_name;
	}

	public String getQgroup_announcement() {
		return qgroup_announcement;
	}

	public int getHost_cid() {
		return host_cid;
	}

	public int getHost_uid() {
		return host_uid;
	}

	private int host_uid;

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public GetQGroupInfoInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		qgroup_id = body.getInt();
		qgroup_sessionid = body.getInt();

		int qgroup_name_length = body.getInt();
		byte[] qgroup_name_buffer = new byte[qgroup_name_length];
		body.get(qgroup_name_buffer);
		qgroup_name = StringUtils.UNICODE_TO_UTF8(qgroup_name_buffer);
		
		int qgroup_announcement_length = body.getInt();
		byte[] qgroup_announcement_buffer = new byte[qgroup_announcement_length];
		body.get(qgroup_announcement_buffer);
		qgroup_announcement = StringUtils.UNICODE_TO_UTF8(qgroup_announcement_buffer);
		
		host_cid = body.getInt();
		host_uid = body.getInt();

		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "GetQGroupInfoInPacket [transid=" + transid + ", ret=" + ret
				+ ", qgroup_id=" + qgroup_id + ", qgroup_sessionid="
				+ qgroup_sessionid + ", qgroup_name=" + qgroup_name
				+ ", qgroup_announcement=" + qgroup_announcement
				+ ", host_cid=" + host_cid + ", host_uid=" + host_uid + "]";
	}
}

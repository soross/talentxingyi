package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class QGroupNoticeInPacket extends CommonInPacket {
	private int magType;
	private int host_cid;
	private int host_uid;
	private int groupid;
	private int qgroup_session_id;
	private String group_name;
	private String text;

	public QGroupNoticeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		magType = body.getInt();
		host_cid = body.getInt();
		host_uid = body.getInt();
		groupid = body.getInt();
		qgroup_session_id = body.getInt();

		int group_name_length = body.getInt();
		byte[] group_name_buffer = new byte[group_name_length];
		body.get(group_name_buffer);
		group_name = StringUtils.UNICODE_TO_UTF8(group_name_buffer);

		int text_length = body.getInt();
		byte[] text_buffer = new byte[text_length];
		body.get(text_buffer);
		text = StringUtils.UNICODE_TO_UTF8(text_buffer);

		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "QGroupNoticeInPacket [magType=" + magType + ", host_cid="
				+ host_cid + ", host_uid=" + host_uid + ", groupid=" + groupid
				+ ", qgroup_session_id=" + qgroup_session_id + ", group_name="
				+ group_name + ", text=" + text + "]";
	}

	public int getMagType() {
		return magType;
	}

	public int getHost_cid() {
		return host_cid;
	}

	public int getHost_uid() {
		return host_uid;
	}

	public int getGroupid() {
		return groupid;
	}

	public int getQgroup_session_id() {
		return qgroup_session_id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public String getText() {
		return text;
	}

}

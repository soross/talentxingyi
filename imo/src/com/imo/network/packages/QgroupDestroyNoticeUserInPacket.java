package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class QgroupDestroyNoticeUserInPacket extends CommonInPacket {

	private int msgType;
	private int cid;
	private int uid;
	private int qgroup_id;
	private int qgroupSessionId;
	private String qgroupName;
	private String sAnnouncement;

	public QgroupDestroyNoticeUserInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		msgType = body.getInt();
		cid = body.getInt();
		uid = body.getInt();
		qgroup_id = body.getInt();
		qgroupSessionId = body.getInt();

		int qgroupName_length = body.getInt();
		byte[] qgroupName_buffer = new byte[qgroupName_length];
		body.get(qgroupName_buffer);
		qgroupName = StringUtils.UNICODE_TO_UTF8(qgroupName_buffer);

		int sAnnouncement_length = body.getInt();
		byte[] sAnnouncement_buffer = new byte[sAnnouncement_length];
		body.get(sAnnouncement_buffer);
		sAnnouncement = StringUtils.UNICODE_TO_UTF8(sAnnouncement_buffer);
	}

	@Override
	public String toString() {
		return "QgroupDestroyNoticeUserInPacket [msgType=" + msgType + ", cid="
				+ cid + ", uid=" + uid + ", qgroup_id=" + qgroup_id
				+ ", qgroupSessionId=" + qgroupSessionId + ", qgroupName="
				+ qgroupName + ", sAnnouncement=" + sAnnouncement + "]";
	}

	public int getMsgType() {
		return msgType;
	}

	public int getCid() {
		return cid;
	}

	public int getUid() {
		return uid;
	}

	public int getQgroup_id() {
		return qgroup_id;
	}

	public int getQgroupSessionId() {
		return qgroupSessionId;
	}

	public String getQgroupName() {
		return qgroupName;
	}

	public String getsAnnouncement() {
		return sAnnouncement;
	}

}

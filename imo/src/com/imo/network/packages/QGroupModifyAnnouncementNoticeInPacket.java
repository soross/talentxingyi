package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class QGroupModifyAnnouncementNoticeInPacket extends CommonInPacket {
	private int qgroup_id;
	private String announcement;

	public QGroupModifyAnnouncementNoticeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		qgroup_id = body.getInt();

		int announcement_length = body.getInt();
		byte[] announcement_buffer = new byte[announcement_length];
		body.get(announcement_buffer);
		announcement = StringUtils.UNICODE_TO_UTF8(announcement_buffer);
		
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "QGroupModifyAnnouncementNoticeInPacket [qgroup_id=" + qgroup_id
				+ ", announcement=" + announcement + "]";
	}

	public int getQgroup_id() {
		return qgroup_id;
	}

	public String getAnnouncement() {
		return announcement;
	}
	
}

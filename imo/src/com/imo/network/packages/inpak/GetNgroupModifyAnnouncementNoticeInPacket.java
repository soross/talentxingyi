package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetNgroupModifyAnnouncementNoticeInPacket extends CommonInPacket {

	private int ngroup_id ;
	private String announcement ;
	
	public GetNgroupModifyAnnouncementNoticeInPacket(ByteBuffer aData,
			int aBodyLen) {
		super(aData, aBodyLen);
		ngroup_id = body.getInt();
		int len = body.getInt();
		byte[] announce = new byte[len];
		body.get(announce);
		announcement =  StringUtils.UNICODE_TO_UTF8(announce);
		 
		LogFactory.d("GetNgroupModifyAnnouncementNoticeInPacket...", this.toString());
	}

	public int getNgroup_id() {
		return ngroup_id;
	}

	public String getAnnouncement() {
		return announcement;
	}

	@Override
	public String toString() {
		return "GetNgroupModifyAnnouncementNoticeInPacket [ngroup_id="
				+ ngroup_id + ", announcement=" + announcement + "]";
	}


	
}

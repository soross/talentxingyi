package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;
import java.util.List;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetNgroupInfoInPacket extends CommonInPacket {

	private int transid;
	private int ret;
	private int ngroup_id;
	private int ngroup_session_id;
	private String ngroup_name;
	private String ngroup_announcement;
	private int host_cid;
	private int host_uid;
	
	public GetNgroupInfoInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		ngroup_id = body.getInt();
		ngroup_session_id = body.getInt();
		int ngroup_nameLen = body.getInt();
		byte[] ngroup_nameByte = new byte[ngroup_nameLen];
		body.get(ngroup_nameByte);
		ngroup_name = StringUtils.UNICODE_TO_UTF8(ngroup_nameByte);
		
		int ngroup_announcementLen = body.getInt();
		byte[] ngroup_announcementByte = new byte[ngroup_announcementLen];
		body.get(ngroup_announcementByte);
		ngroup_announcement = StringUtils.UNICODE_TO_UTF8(ngroup_announcementByte);
		
		host_cid = body.getInt();
		host_uid = body.getInt();
		
		LogFactory.d("GetNgroupInfoInPacket...", this.toString());
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getNgroup_id() {
		return ngroup_id;
	}

	public int getNgroup_session_id() {
		return ngroup_session_id;
	}

	public String getNgroup_name() {
		return ngroup_name;
	}

	public String getNgroup_announcement() {
		return ngroup_announcement;
	}

	public int getHost_cid() {
		return host_cid;
	}

	public int getHost_uid() {
		return host_uid;
	}

	@Override
	public String toString() {
		return "GetNgroupInfoInPacket [transid=" + transid + ", ret=" + ret
				+ ", ngroup_id=" + ngroup_id + ", ngroup_session_id="
				+ ngroup_session_id + ", ngroup_name=" + ngroup_name
				+ ", ngroup_announcement=" + ngroup_announcement
				+ ", host_cid=" + host_cid + ", host_uid=" + host_uid + "]";
	}
	
}

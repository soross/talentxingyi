package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetModifyNgroupNameNoticeInPacket extends CommonInPacket{
	private int ngroup_id;
	private String ngroup_name;
	
	public GetModifyNgroupNameNoticeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		ngroup_id = body.getInt();
		int ngroup_nameLen = body.getInt();
		byte[] ngroup_nameByte = new byte[ngroup_nameLen];
		body.get(ngroup_nameByte);
		ngroup_name = StringUtils.UNICODE_TO_UTF8(ngroup_nameByte);
		
		LogFactory.d("GetModifyNgroupNameNoticeInPacket...", this.toString());
	}

	public int getNgroup_id() {
		return ngroup_id;
	}

	public String getNgroup_name() {
		return ngroup_name;
	}

	@Override
	public String toString() {
		return "GetModifyNgroupNameNoticeInPacket [ngroup_id=" + ngroup_id
				+ ", ngroup_name=" + ngroup_name + "]";
	}

}

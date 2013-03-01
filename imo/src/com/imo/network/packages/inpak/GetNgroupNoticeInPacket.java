package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetNgroupNoticeInPacket extends CommonInPacket {

	 private int MsgType;
	 private int host_cid;
	 private int host_uid;
	 private int ngroupid;
	 private int ngroup_session_id;
	 private String  ngroup_name;
	 private String  text;
	 
	 
	public GetNgroupNoticeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		MsgType = body.getInt();
		host_cid = body.getInt();
		host_uid = body.getInt();
		ngroupid = body.getInt();
		ngroup_session_id = body.getInt();
		int ngroup_nameLen = body.getInt();
		byte[] ngroup_nameByte = new byte[ngroup_nameLen];
		body.get(ngroup_nameByte);
		ngroup_name = StringUtils.UNICODE_TO_UTF8(ngroup_nameByte);
		
		int textLen = body.getInt();
		byte[] textByte = new byte[textLen];
		body.get(textByte);
		text = StringUtils.UNICODE_TO_UTF8(textByte);
		
		LogFactory.d("GetNgroupNoticeInPacket...", this.toString());
	}


	public int getMsgType() {
		return MsgType;
	}


	public int getHost_cid() {
		return host_cid;
	}


	public int getHost_uid() {
		return host_uid;
	}


	public int getNgroupid() {
		return ngroupid;
	}


	public int getNgroup_session_id() {
		return ngroup_session_id;
	}


	public String getNgroup_name() {
		return ngroup_name;
	}


	public String getText() {
		return text;
	}


	@Override
	public String toString() {
		return "GetNgroupNoticeInPacket [MsgType=" + MsgType + ", host_cid="
				+ host_cid + ", host_uid=" + host_uid + ", ngroupid="
				+ ngroupid + ", ngroup_session_id=" + ngroup_session_id
				+ ", ngroup_name=" + ngroup_name + ", text=" + text + "]";
	}

}

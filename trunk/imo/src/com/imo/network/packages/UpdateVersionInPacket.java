package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class UpdateVersionInPacket extends CommonInPacket{

	private byte flag;
	private byte version;
	private short build;
	private byte devNo;
	private String info;
	private String downloadURL;
	
	public byte getFlag() {
		return flag;
	}


	public byte getVersion() {
		return version;
	}


	public short getBuild() {
		return build;
	}


	public byte getDevNo() {
		return devNo;
	}


	public String getInfo() {
		return info;
	}


	public String getDownloadURL() {
		return downloadURL;
	}


	
	
	public UpdateVersionInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub

		flag = body.get();
		version = body.get();
		build = body.getShort();
		devNo = body.get();
		
		int infoLen = body.getInt();
		byte[] infoBuffer = new byte[infoLen];
		body.get(infoBuffer);
		info = StringUtils.UNICODE_TO_UTF8(infoBuffer);
		
		int downloadURLLen = body.getInt();
		byte[] downloadURLBuffer = new byte[downloadURLLen];
		body.get(downloadURLBuffer);
		downloadURL = StringUtils.UNICODE_TO_UTF8(downloadURLBuffer);
	}


	@Override
	public String toString() {
		return "UpdateVersionInPacket [flag=" + flag + ", version=" + version
				+ ", build=" + build + ", devNo=" + devNo + ", info=" + info
				+ ", downloadURL=" + downloadURL + "]";
	}
	
	

}

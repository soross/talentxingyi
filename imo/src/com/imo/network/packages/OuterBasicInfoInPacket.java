package com.imo.network.packages;

import java.nio.ByteBuffer;

import android.util.Log;

import com.imo.network.Encrypt.StringUtils;

public class OuterBasicInfoInPacket extends CommonInPacket{

	private short ret = -1;
	private int contactor_cid;
	private int contactor_uid;
	private String corp_account = "";
	private String user_account = "";
	private String name = "";
	private int gender;
	
	public short getRet() {
		return ret;
	}


	public int getContactor_cid() {
		return contactor_cid;
	}


	public int getContactor_uid() {
		return contactor_uid;
	}


	public String getCorp_account() {
		return corp_account;
	}


	public String getUser_account() {
		return user_account;
	}


	public String getName() {
		return name;
	}


	public int getGender() {
		return gender;
	}
	
	public OuterBasicInfoInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		ret = body.getShort();
		if (ret != 0)
			return;
		
		contactor_cid = body.getInt();
		contactor_uid = body.getInt();
		
		int corp_accountLen = body.getInt();
		byte[] corp_accountBuffer = new byte[corp_accountLen];
		body.get(corp_accountBuffer);
		corp_account = StringUtils.UNICODE_TO_UTF8(corp_accountBuffer);
		
		int user_accountLen = body.getInt();
		byte[] user_accountBuffer = new byte[user_accountLen];
		body.get(user_accountBuffer);
		user_account = StringUtils.UNICODE_TO_UTF8(user_accountBuffer);
		
		int nameLen = body.getInt();
		byte[] nameBuffer = new byte[nameLen];
		body.get(nameBuffer);
		name = StringUtils.UNICODE_TO_UTF8(nameBuffer);
		
		Log.e("OuterBasicInfoInPacket", "cid :"+contactor_cid+", uid :"+contactor_uid+", name :"+name+", user_account :"+user_account);
		
		gender = body.getInt();
	}

}

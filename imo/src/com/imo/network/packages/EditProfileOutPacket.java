package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class EditProfileOutPacket extends CommonOutPacket {

	public EditProfileOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateEdifProfileMsgBody(int aMask, String aProfile) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(16 + StringUtils.ToUnicode(aProfile).length);
		dataBuffer.putInt(aMask);
		dataBuffer.putInt(StringUtils.ToUnicode(aProfile).length);
		dataBuffer.put(StringUtils.ToUnicode(aProfile));

		return dataBuffer;
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.DigestUtils;
import com.imo.network.Encrypt.XXTEA;
import com.imo.network.net.EngineConst;

public class LoginInPacket extends InPacket{

	public LoginInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		
		// TODO Auto-generated constructor stub
		byte[] loginBinaryData = new byte[aBodyLen-EngineConst.IMO__PACKET_HEADER_SIZE];
		aData.get(loginBinaryData);
		
		byte[] encryptBody = new byte[loginBinaryData.length + 16];
		
        byte[] passwd = new byte[16];
        passwd = DigestUtils.md5(EngineConst.password.getBytes());
        
		int out_len = XXTEA.decipher(passwd, loginBinaryData, loginBinaryData.length, encryptBody, encryptBody.length);
		byte[] encryptedBody = new byte[out_len];
		System.arraycopy(encryptBody, 0, encryptedBody, 0, out_len);
		this.body = ByteBuffer.wrap(encryptedBody);
		
		loginBinaryData = null;
		encryptBody = null;
		encryptedBody = null;
		System.gc();
	}

}

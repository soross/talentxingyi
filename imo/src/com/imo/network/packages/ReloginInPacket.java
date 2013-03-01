package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.DigestUtils;
import com.imo.network.Encrypt.XXTEA;
import com.imo.network.net.EngineConst;

public class ReloginInPacket extends InPacket{

	private short ret;
	
	public short getRet() {
		return ret;
	}

	public ReloginInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		byte[] loginBinaryData = new byte[aBodyLen-EngineConst.IMO__PACKET_HEADER_SIZE];
		aData.get(loginBinaryData);
		
		byte[] encryptBody = new byte[loginBinaryData.length + 16];
        
		int out_len = XXTEA.decipher(ReloginOutPacket.randomKey, loginBinaryData, loginBinaryData.length, encryptBody, encryptBody.length);
		byte[] encryptedBody = new byte[out_len];
		System.arraycopy(encryptBody, 0, encryptedBody, 0, out_len);
		this.body = ByteBuffer.wrap(encryptedBody);
		ret = body.getShort();
		
		loginBinaryData = null;
		encryptBody = null;
		encryptedBody = null;
		System.gc();
	}

}

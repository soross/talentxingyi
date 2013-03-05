package org.talentware.android.comm.packet;

import java.nio.ByteBuffer;

import org.talentware.android.comm.net.EngineConst;

public class CommonInPacket extends InPacket {

	public CommonInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);

		// TODO Auto-generated constructor stub

//		if (aData.limit() != 0) {
//			byte[] loginBinaryData = new byte[aBodyLen - EngineConst.IMO__PACKET_HEADER_SIZE];
//			aData.get(loginBinaryData);
//
//			byte[] encryptBody = new byte[loginBinaryData.length + 16];
//
//			int out_len = XXTEA.decipher(EngineConst.sessionKey, loginBinaryData, loginBinaryData.length, encryptBody, encryptBody.length);
//			byte[] encryptedBody = new byte[out_len];
//			System.arraycopy(encryptBody, 0, encryptedBody, 0, out_len);
//			this.body = ByteBuffer.wrap(encryptedBody);
//		}
	}

}

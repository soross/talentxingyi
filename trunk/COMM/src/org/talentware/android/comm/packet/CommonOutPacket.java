package org.talentware.android.comm.packet;

import java.nio.ByteBuffer;

import org.talentware.android.comm.net.EngineConst;

public class CommonOutPacket extends OutPacket {

	public CommonOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);

		// TODO Auto-generated constructor stub
		aBody.flip();

//		if (aBody.limit() != 0) {
//			byte[] data = new byte[aBody.limit() - aBody.position()];
//			aBody.get(data);
//			ByteBuffer actualData = ByteBuffer.wrap(data);
//
//			byte[] encryptBody = new byte[actualData.limit() + 16];
//			int out_len = XXTEA.encipher(EngineConst.sessionKey, actualData.array(), actualData.array().length, encryptBody, encryptBody.length);
//			byte[] encryptedBody = new byte[out_len];
//			System.arraycopy(encryptBody, 0, encryptedBody, 0, out_len);
//
//			ByteBuffer temp = ByteBuffer.allocate(encryptedBody.length);
//
//			temp.put(encryptedBody);
//			temp.flip();
//
//			byte[] dataBinary = new byte[temp.limit() - temp.position()];
//			temp.get(dataBinary);
//
//			this.body = ByteBuffer.wrap(dataBinary);
//			this.body.flip();
//
//			this.dataLen = (short) (dataBinary.length + EngineConst.IMO__PACKET_HEADER_SIZE);
//		}

		MakeHeader();
	}

}

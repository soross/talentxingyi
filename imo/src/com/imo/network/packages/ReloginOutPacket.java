package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Random;

import com.imo.network.Encrypt.XXTEA;
import com.imo.network.net.EngineConst;

public class ReloginOutPacket extends OutPacket{

	public static byte[] randomKey;
	
	public ReloginOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub
		
		resendCountDown = EngineConst.IMO_SEND_TIME_NOACK_PACKET;
		
		ByteBuffer temp = ByteBuffer.allocate(256);
		
		byte[] encryptBody = new byte[aBody.limit() + 16];
		randomKey = GenerateRandomkey();
		int out_len = XXTEA.encipher(randomKey, aBody.array(), aBody.array().length, encryptBody, encryptBody.length);
		byte[] encryptedBody = new byte[out_len];
		System.arraycopy(encryptBody, 0, encryptedBody, 0, out_len);
		
		temp.put(randomKey);
		temp.put(encryptedBody);
		
		temp.flip();
		
		byte[] dataBinary = new byte[temp.limit()-temp.position()];
		temp.get(dataBinary);
		
		temp = null;
		
		this.body = ByteBuffer.wrap(dataBinary);
		this.body.flip();
		
		this.dataLen = (short)(dataBinary.length+EngineConst.IMO__PACKET_HEADER_SIZE);
		
		MakeHeader();
	}
	
	//Generate randomKey
	public byte[] GenerateRandomkey() {
		Random randkey = new Random();
		byte[] randomKey = new byte[16];
		randkey.nextBytes(randomKey);

		return randomKey;
	}

}

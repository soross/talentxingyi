package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Random;

import com.imo.network.Encrypt.DigestUtils;
import com.imo.network.Encrypt.StringUtils;
import com.imo.network.Encrypt.XXTEA;
import com.imo.network.net.EngineConst;
import com.imo.util.SystemInfoManager;

public class LoginOutPacket extends OutPacket {

	private byte[] randomKey;

	public LoginOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
		// TODO Auto-generated constructor stub

		byte[] encryptBody = new byte[aBody.limit() + 16];
		randomKey = GenerateRandomkey();
		int out_len = XXTEA.encipher(randomKey, aBody.array(), aBody.array().length, encryptBody, encryptBody.length);
		byte[] encryptedBody = new byte[out_len];
		System.arraycopy(encryptBody, 0, encryptedBody, 0, out_len);

		ByteBuffer temp = ByteBuffer.allocate(encryptedBody.length + randomKey.length + 32);

		temp.put(randomKey);
		temp.put(encryptedBody);

		temp.flip();

		byte[] dataBinary = new byte[temp.limit() - temp.position()];
		temp.get(dataBinary);

		temp = null;

		this.body = ByteBuffer.wrap(dataBinary);
		this.body.flip();

		this.dataLen = (short) (dataBinary.length + EngineConst.IMO__PACKET_HEADER_SIZE);

		MakeHeader();
	}

	// Generate randomKey
	public byte[] GenerateRandomkey() {
		Random randkey = new Random();
		byte[] randomKey = new byte[16];
		randkey.nextBytes(randomKey);

		return randomKey;
	}

	// Login Packet
	public static ByteBuffer GenerateLoginBody(byte aFlag, String aDomain, String aUserAccount, String aUserInfo) {
		ByteBuffer bodyBuffer = ByteBuffer.allocate(StringUtils.ToUnicode(aDomain).length + StringUtils.ToUnicode(aUserInfo).length + 256);
		bodyBuffer.put(aFlag);

		bodyBuffer.putInt(StringUtils.ToUnicode(aDomain).length);
		bodyBuffer.put(StringUtils.ToUnicode(aDomain));
		bodyBuffer.putInt(StringUtils.ToUnicode(aUserAccount).length);
		bodyBuffer.put(StringUtils.ToUnicode(aUserAccount));

		byte[] passwd = new byte[16];
		passwd = DigestUtils.md5(EngineConst.password);

		bodyBuffer.put(passwd);
		bodyBuffer.putShort((short) 257);

		SystemInfoManager info = new SystemInfoManager();

		byte aVersion = info.getVersion();
		short aBuild = info.getBuild();

		bodyBuffer.put(aVersion);
		bodyBuffer.putShort(aBuild);

		bodyBuffer.put((byte) 1);
		bodyBuffer.putInt(StringUtils.ToUnicode(aUserInfo).length);
		bodyBuffer.put(StringUtils.ToUnicode(aUserInfo));

		bodyBuffer.flip();

		byte[] data = new byte[bodyBuffer.limit() - bodyBuffer.position()];
		bodyBuffer.get(data);

		bodyBuffer = null;

		return ByteBuffer.wrap(data);
	}
	// [End]

}

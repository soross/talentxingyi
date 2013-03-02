package com.imo.network.Encrypt;

import java.nio.ByteBuffer;
import java.util.Random;

public class XXTEA {

	public static byte[] Int2Bytes(long num) {
		ByteBuffer buf = ByteBuffer.allocate(32);
		buf.putLong(num);

		byte[] byteStream = new byte[4];
		System.arraycopy(buf.array(), 4, byteStream, 0, 4);
		byte[] reversalBytes = new byte[4];

		for (int i = 0; i < 4; i++) {
			reversalBytes[i] = byteStream[3 - i];
		}
		return reversalBytes;
	}

	public static Long Bytes2Int(byte[] byteStream, int offset) {
		long result = 0L;
		for (int i = offset; i < 4 + offset; i++) {
			int tmpVal = (byteStream[i] << (8 * (i - offset)));
			switch (i - offset) {
				case 0:
					tmpVal = tmpVal & 0x000000FF;
					break;
				case 1:
					tmpVal = tmpVal & 0x0000FF00;
					break;
				case 2:
					tmpVal = tmpVal & 0x00FF0000;
					break;
				case 3:
					tmpVal = tmpVal & 0xFF000000;
					break;
			}
			result = result | tmpVal;
		}
		return result;
	}

	public static void encipher_block(int rounds, byte[] key, byte[] in, byte[] out) {
		long in0 = XXTEA.Bytes2Int(in, 0) & (0xFFFFFFFFL);
		long in1 = XXTEA.Bytes2Int(in, 4) & (0xFFFFFFFFL);;

		long delta = 0x9e3779b9L;
		long sum = 0L;
		int index = 0;
		long keyTemp = 0L;
		long leftShift = 0L;
		long rightShift = 0L;
		long cpmResult = 0L;
		long cmpReslut2 = 0L;

		for (int i = 0; i < rounds; i++) {
			index = (int) ((sum & 3) * 4);
			keyTemp = (sum + XXTEA.Bytes2Int(key, index)) & (0xFFFFFFFFL);
			leftShift = (in1 << 4) & (0xFFFFFFFFL);
			rightShift = (in1 >>> 5) & (0xFFFFFFFFL);
			cpmResult = ((leftShift ^ rightShift) + in1) & (0xFFFFFFFFL);
			cmpReslut2 = (cpmResult ^ keyTemp) & (0xFFFFFFFFL);
			in0 += cmpReslut2;
			in0 &= (0xFFFFFFFFL);
			sum += delta;
			sum &= (0xFFFFFFFFL);
			index = (int) ((((sum >>> 11) & (0xFFFFFFFFL)) & 3) * 4);
			keyTemp = (sum + XXTEA.Bytes2Int(key, index)) & (0xFFFFFFFFL);
			leftShift = (in0 << 4) & (0xFFFFFFFFL);
			rightShift = (in0 >>> 5) & (0xFFFFFFFFL);
			in1 += (((leftShift ^ rightShift) + in0) ^ keyTemp) & (0xFFFFFFFFL);
			in1 &= (0xFFFFFFFFL);
		}

		System.arraycopy(XXTEA.Int2Bytes(in0), 0, out, 0, 4);
		System.arraycopy(XXTEA.Int2Bytes(in1), 0, out, 4, 4);
	}

	public static void decipher_block(int rounds, byte[] key, byte[] in, byte[] out) {
		long in0 = XXTEA.Bytes2Int(in, 0) & (0xFFFFFFFFL);
		long in1 = XXTEA.Bytes2Int(in, 4) & (0xFFFFFFFFL);;

		long delta = 0x9e3779b9L;
		long sum = (delta * rounds) & (0xFFFFFFFFL);

		int index = 0;
		long keyTemp = 0L;
		long leftShift = 0L;
		long rightShift = 0L;
		long cpmResult = 0L;
		long cmpReslut2 = 0L;

		for (int i = 0; i < rounds; i++) {
			index = (int) ((((sum >>> 11) & (0xFFFFFFFFL)) & 3) * 4);
			keyTemp = (sum + XXTEA.Bytes2Int(key, index)) & (0xFFFFFFFFL);
			leftShift = (in0 << 4) & (0xFFFFFFFFL);
			rightShift = (in0 >>> 5) & (0xFFFFFFFFL);
			in1 -= (((leftShift ^ rightShift) + in0) ^ keyTemp) & (0xFFFFFFFFL);
			in1 &= (0xFFFFFFFFL);

			sum -= delta;
			sum &= (0xFFFFFFFFL);

			index = (int) ((sum & 3) * 4);
			keyTemp = (sum + XXTEA.Bytes2Int(key, index)) & (0xFFFFFFFFL);
			leftShift = (in1 << 4) & (0xFFFFFFFFL);
			rightShift = (in1 >>> 5) & (0xFFFFFFFFL);
			cpmResult = ((leftShift ^ rightShift) + in1) & (0xFFFFFFFFL);
			cmpReslut2 = (cpmResult ^ keyTemp) & (0xFFFFFFFFL);
			in0 -= cmpReslut2;
			in0 &= (0xFFFFFFFFL);
		}

		System.arraycopy(XXTEA.Int2Bytes(in0), 0, out, 0, 4);
		System.arraycopy(XXTEA.Int2Bytes(in1), 0, out, 4, 4);
	}

	// class XTEA
	public static int encipher(byte[] key, byte[] in, int in_len, byte[] out, int out_buf_len) {
		int out_len = 0;
		if (in_len <= 0) {
			return out_len;
		}

		// 计算padding
		int padding = 0;
		if (0 != (in_len % 8)) {
			padding = 8 - (in_len % 8);
		}

		if (out_buf_len < (1 + padding + in_len + 7)) {
			return out_len;
		}

		out_len = 1 + padding + in_len + 7;

		// 开始加密
		int in_index = 0;
		int out_index = 0;

		byte plain_temp[][] = new byte[2][8];
		byte cipher_temp[][] = new byte[2][8];
		byte[] last_plain, this_plain;
		byte[] last_cipher, this_cipher;

		// 1
		last_plain = null;
		this_plain = plain_temp[0];
		last_cipher = null;
		this_cipher = cipher_temp[0];

		this_plain[0] = (byte) (((byte) (new Random().nextInt()) & 0xf8) | (byte) padding);

		for (int i = 1; i <= padding; i++) {
			this_plain[i] = (byte) (new Random().nextInt());
		}
		for (int i = 1 + padding; i < 8; i++) {
			this_plain[i] = in[in_index++];
		}

		encipher_block(32, key, this_plain, this_cipher);

		last_plain = this_plain;
		last_cipher = this_cipher;

		this_plain = plain_temp[1];
		this_cipher = cipher_temp[1];

		// 2
		while ((in_len - in_index) >= 8) {
			System.arraycopy(in, in_index, this_plain, 0, 8);

			byte[] prepare_plain = new byte[8];
			for (int i = 0; i < 8; i++) {
				prepare_plain[i] = (byte) (this_plain[i] ^ last_cipher[i]);
			}

			encipher_block(32, key, prepare_plain, this_cipher);

			for (int i = 0; i < 8; i++) {
				this_cipher[i] ^= last_plain[i];
			}

			System.arraycopy(last_cipher, 0, out, out_index, 8);

			out_index += 8;

			byte[] tp = last_plain;
			byte[] tc = last_cipher;

			last_plain = this_plain;
			last_cipher = this_cipher;

			this_plain = tp;
			this_cipher = tc;

			in_index += 8;
		}

		// 3
		assert ((in_len - in_index) == 1);
		this_plain[0] = in[in_index];
		for (int i = 1; i < 8; i++) {
			this_plain[i] = 0;
		}

		byte[] prepare_plain = new byte[8];
		for (int i = 0; i < 8; i++) {
			prepare_plain[i] = (byte) (this_plain[i] ^ last_cipher[i]);
		}

		encipher_block(32, key, prepare_plain, this_cipher);

		for (int i = 0; i < 8; i++) {
			this_cipher[i] ^= last_plain[i];
		}

		System.arraycopy(last_cipher, 0, out, out_index, 8);
		out_index += 8;

		System.arraycopy(this_cipher, 0, out, out_index, 8);
		out_index += 8;

		assert (out_index == out_len);
		return out_len;
	}

	public static int decipher(byte[] key, byte[] in, int in_len, byte[] out, int out_buf_len) {
		int out_len = 0;
		if ((in_len <= 0) || (0 != (in_len % 8))) {
			return out_len;
		}

		// 开始解密
		int in_index = 0;
		int out_index = 0;

		byte[][] plain_temp = new byte[2][8];
		byte[][] cipher_temp = new byte[2][8];
		byte[] last_plain, this_plain;
		byte[] last_cipher, this_cipher;

		// 1
		last_cipher = null;
		this_cipher = cipher_temp[0];
		last_plain = null;
		this_plain = plain_temp[0];

		System.arraycopy(in, in_index, this_cipher, 0, 8);

		in_index += 8;

		decipher_block(32, key, this_cipher, this_plain);

		int padding = this_plain[0] & 0x07;

		if (out_buf_len < (in_len - 1 - padding - 7)) {
			return out_len;
		}
		out_len = in_len - 1 - padding - 7;

		System.arraycopy(this_plain, 1 + padding, out, out_index, 8 - 1 - padding);

		out_index += (8 - 1 - padding);

		last_plain = this_plain;
		last_cipher = this_cipher;

		this_plain = plain_temp[1];
		this_cipher = cipher_temp[1];

		// 2
		while ((in_len - in_index) > 8) {
			System.arraycopy(in, in_index, this_cipher, 0, 8);

			byte[] prepare_cipher = new byte[8];
			for (int i = 0; i < 8; i++) {
				prepare_cipher[i] = (byte) (this_cipher[i] ^ last_plain[i]);
			}

			decipher_block(32, key, prepare_cipher, this_plain);

			for (int i = 0; i < 8; i++) {
				this_plain[i] ^= last_cipher[i];
			}

			System.arraycopy(this_plain, 0, out, out_index, 8);

			out_index += 8;

			byte[] tp = last_plain;
			byte[] tc = last_cipher;

			last_plain = this_plain;
			last_cipher = this_cipher;

			this_plain = tp;
			this_cipher = tc;

			in_index += 8;
		}

		// 3
		assert ((in_len - in_index) == 8);

		System.arraycopy(in, in_index, this_cipher, 0, 8);

		byte[] prepare_cipher = new byte[8];
		for (int i = 0; i < 8; i++) {
			prepare_cipher[i] = (byte) (this_cipher[i] ^ last_plain[i]);
		}

		decipher_block(32, key, prepare_cipher, this_plain);

		for (int i = 0; i < 8; i++) {
			this_plain[i] ^= last_cipher[i];
		}

		System.arraycopy(this_plain, 0, out, out_index, 8 - 7);

		out_index += (8 - 7);

		for (int i = 1; i < 8; i++) {
			if (0 != this_plain[i]) {
				out_len = 0;
				return out_len;
			}
		}

		assert (out_index == out_len);
		return out_len;
	}

}

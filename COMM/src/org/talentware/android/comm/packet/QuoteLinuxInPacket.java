package org.talentware.android.comm.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.talentware.android.comm.util.LogFactory;

public class QuoteLinuxInPacket {

	private Hashtable<Integer, byte[]> mRespData = new Hashtable<Integer, byte[]>();

	public void parseInPacket(ByteBuffer buf) {
		analysisData(buf);
	}

	private void analysisData(ByteBuffer buf) {
		byte[] tmpdata = null;
		byte[] data = null;

		try {
			int v = buf.get();// '{'
			int type = buf.getShort();
			// 包属性 byte[2]
			byte arr1 = buf.get(); // 0 标志位
			buf.get(); // 0 标志位
			boolean needDecompress = ((arr1 & 0x02) != 0);

			if (type == 5000) {
				// 正文数据,结束符号一并读进来
				short pcksize = buf.getShort();
				buf.mark();
				data = new byte[pcksize + 1];

				buf.get(data);
				if (needDecompress) {
					data = decompress(data);
					buf.clear();
					buf.put(data);
					buf.flip();
				}
				analysisData(buf);
				return;
			} else {
				short pcksize = buf.getShort();
				data = new byte[pcksize];
				buf.get(data);
				if (needDecompress) {
					data = decompress(data);
				}
			}

			byte tmp = buf.get();
			if (tmp == '}') {
				putData(type, data);
			} else if (tmp == ':') {
				putData(type, data);
				analysisData(buf);
			} else {
				throw new IOException("BAD DATA:" + tmp);
			}
		} catch (IOException ex) {
			ex.printStackTrace();

		}
	}

	private byte[] decompress(byte[] data) {
		int originalSize = ((data[1] & 0xFF) << 8) | (data[0] & 0xFF);
		int compressedSize = ((data[3] & 0xFF) << 8) | (data[2] & 0xFF);
		Inflater decompresser = new Inflater();
		decompresser.setInput(data, 4, compressedSize);

		byte[] result = new byte[originalSize];

		try {
			decompresser.inflate(result);
		} catch (DataFormatException e) {
			LogFactory.e(QuoteLinuxInPacket.class.getSimpleName(), "Decompress Err:" + e.getMessage());
			e.printStackTrace();
			result = null;
		}
		decompresser.end();
		return result;
	}

	public void putData(int type, byte[] data) {
		String key = Integer.toString(type);
		if (mRespData.containsKey(key)) {// 处理key冲突
			byte[] tempData = (byte[]) mRespData.get(key);
			data = mergeTwoByteArray(tempData, data);
		}
		if (null != data && data.length != 0) {
			mRespData.put(Integer.valueOf(type), data);
		}
	}

	private byte[] mergeTwoByteArray(byte[] arr1, byte[] arr2) {// 合并两个byte数组
		int arr1Length = (arr1 == null) ? 0 : arr1.length;
		int arr2Length = (arr2 == null) ? 0 : arr2.length;
		int totalLength = arr1Length + arr2Length;

		if (totalLength < 1)
			return null;

		byte[] temp = new byte[totalLength];
		if (arr1Length > 0)
			System.arraycopy(arr1, 0, temp, 0, arr1Length);
		if (arr2Length > 0)
			System.arraycopy(arr2, 0, temp, arr1Length, arr2Length);

		return temp;
	}
}

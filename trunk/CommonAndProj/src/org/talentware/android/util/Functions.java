package org.talentware.android.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public class Functions {

	public static byte[] CompressData(byte[] data) {
		final int originalSize = data.length;

		final Deflater compresser = new Deflater();
		compresser.setLevel(Deflater.BEST_COMPRESSION);
		compresser.setInput(data);
		compresser.finish();

		final ByteArrayOutputStream bos = new ByteArrayOutputStream(originalSize);
		final byte[] compressBuf = new byte[originalSize];
		while (!compresser.finished()) {
			int count = compresser.deflate(compressBuf);
			bos.write(compressBuf, 0, count);
		}

		try {
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		compresser.end();

		final byte[] bytes = bos.toByteArray();
		final int compressedSize = bytes.length;
		final byte[] result = new byte[compressedSize + 4];
		result[0] = (byte) (originalSize & 0xff);
		result[1] = (byte) ((originalSize >>> 8) & 0xff);
		result[2] = (byte) (compressedSize & 0xff);
		result[3] = (byte) ((compressedSize >>> 8) & 0xff);
		System.arraycopy(bytes, 0, result, 4, compressedSize);
		// Log.d("compress", "cs:" + compressedSize);

		return result;
	}

}

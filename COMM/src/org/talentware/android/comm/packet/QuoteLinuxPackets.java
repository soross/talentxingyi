package org.talentware.android.comm.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;

import org.talentware.android.comm.util.LogFactory;

import android.util.Log;

public class QuoteLinuxPackets extends OutPacket {
	private static final byte START_FLAG = (byte) '{';

	private static final byte END_FLAG = (byte) '}';

	private static final byte OTHER_FLAG = (byte) ':';

	private static final int MIN_LEN_TO_COMPRESS = 64;

	public QuoteLinuxPackets(final Packet[] packets) {
		final boolean MultiPackets = (packets != null && packets.length > 1) ? true : false;

		ByteBuffer buffer = ByteBuffer.allocate(computePacketsTotalLength(packets));
		
		try {
			buffer.put(START_FLAG);
			for (int i = 0, size = packets.length; i < size; i++) {
				buffer.put((byte)(packets[i].getCommand() & 0xFF));
				buffer.put((byte)((packets[i].getCommand() >>> 8) & 0xFF));
				final byte[] content = packets[i].getBody();
				final int len = content.length;
				final boolean shouldCompress = !MultiPackets && len >= MIN_LEN_TO_COMPRESS;
				final int attr = (!MultiPackets ? 0x02 : 0) | ((shouldCompress && !MultiPackets) ? 0x04 : 0);
				buffer.put((byte) attr);
				buffer.put((byte) 0);
				final byte[] compressedData = (shouldCompress && !MultiPackets) ? compress(content) : content;
				buffer.put((byte)(compressedData.length & 0xFF));
				buffer.put((byte)((compressedData.length >>> 8) & 0xFF));
				buffer.put(compressedData);

				if (i < size - 1) {
					buffer.put(OTHER_FLAG);
				}
			}

			if (MultiPackets) {
				byte[] tempBytes = buffer.array();
				buffer.rewind();
				buffer.put(START_FLAG);
				buffer.put((byte)(5000 & 0xFF));
				buffer.put((byte)((5000 >>> 8) & 0xFF));

				final int len = tempBytes.length - 1;
				final boolean shouldCompress = len >= MIN_LEN_TO_COMPRESS;// 子包不压缩
				int attr = 0x02 | (shouldCompress ? 0x04 : 0);
				buffer.put((byte) attr);
				buffer.put((byte) 0);
				final byte[] bytes = shouldCompress ? compress(tempBytes) : tempBytes;
				buffer.put((byte)(bytes.length & 0xFF));
				buffer.put((byte)((bytes.length >>> 8) & 0xFF));
				buffer.put(bytes);
			}
			buffer.put(END_FLAG); // 校验符
		} catch (Exception e) {
			// TODO: handle exception
			LogFactory.e(QuoteLinuxPackets.class.getSimpleName(), "Construct QuoteLinuxPackets Err , Msg = " + e.getMessage());
		}

		buffer.flip();
		byte[] data = new byte[buffer.limit() - buffer.position()];
		data = buffer.array();
		
        String bbString = "";
        for (int j = 0; j < data.length; j++) {
            bbString += data[j];
            bbString += " ";
        }
        Log.e("ccccccc", "ccString:" + bbString);
		
		body = ByteBuffer.wrap(data);
		body.flip();
	}

	private int computePacketsTotalLength(final Packet[] packets) {
		int totalLength = 0;
		for (int i = 0, len = packets.length; i < len; i++) {
			totalLength += packets[i].getDataLen();
		}

		if (packets.length > 1) {
			totalLength += (6 * packets.length + (packets.length - 1) + 1 + 1 + 2);
		} else {
			totalLength += (6 + 1 + 1);
		}

		return totalLength;
	}

	private byte[] compress(byte[] data) {
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

		return result;
	}
}

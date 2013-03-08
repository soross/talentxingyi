package org.talentware.android.comm.packet;

import java.nio.ByteBuffer;

public class ServerTimePacket extends Packet {

	public ServerTimePacket(ByteBuffer aBody, short aCommand) {
		super(aBody, aCommand);
	}

	public static ByteBuffer GenerateServerTimeOutPacket() {
		ByteBuffer bodyBuffer = ByteBuffer.allocate(0);
		bodyBuffer.flip();

		byte[] data = new byte[bodyBuffer.limit() - bodyBuffer.position()];
		bodyBuffer.get(data);
		bodyBuffer = null;

		return ByteBuffer.wrap(data);
	}

	public static void GenerateServerInPacket() {

	}
}

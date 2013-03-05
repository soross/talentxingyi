package org.talentware.android.comm.packet;

import java.nio.ByteBuffer;

public class InPacket extends Packet {

	public InPacket(ByteBuffer aHeader, ByteBuffer aBody) {
		super(aHeader, aBody);
	}

	public InPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
	}

	public InPacket() {
		super();
	}
}

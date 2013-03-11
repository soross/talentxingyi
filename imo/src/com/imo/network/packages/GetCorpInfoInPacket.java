package com.imo.network.packages;

import java.nio.ByteBuffer;

// ret short
// contactor_id int
// mask int
public class GetCorpInfoInPacket extends CommonInPacket {

	private short commandRet = -1;
	private int contactor_id;
	private int mask;
	private CorpMaskItem maskItem;

	public GetCorpInfoInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);

		// TODO Auto-generated constructor stub
		commandRet = body.getShort();
		if (commandRet != 0)
			return;

		contactor_id = body.getInt();
		mask = body.getInt();
		maskItem = new CorpMaskItem(mask, body);
	}

	public short getRet() {
		return commandRet;
	}

	public int getContactorID() {
		return contactor_id;
	}

	public int getMask() {
		return mask;
	}

	public CorpMaskItem getMaskItem() {
		return maskItem;
	}
}

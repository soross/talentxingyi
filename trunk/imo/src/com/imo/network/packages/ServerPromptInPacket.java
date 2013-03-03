package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;


public class ServerPromptInPacket extends CommonInPacket {
	private static final String TAG = "ServerPromptInPacket";

	private int timeBegin;
	private int timeEnd;
	private int unType;

	public int getTimeBegin() {
		return timeBegin;
	}

	public int getTimeEnd() {
		return timeEnd;
	}

	public int getUnType() {
		return unType;
	}

	public ServerPromptInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub

		LogFactory.d(TAG, "�������յ�������ά����Ϣ");
		timeBegin = body.getInt();
		timeEnd = body.getInt();
		unType = body.getInt();
	}

	@Override
	public String toString() {
		return "ServerPromptInPacket [timeBegin=" + timeBegin + ", timeEnd="
				+ timeEnd + ", unType=" + unType + "]";
	}

}

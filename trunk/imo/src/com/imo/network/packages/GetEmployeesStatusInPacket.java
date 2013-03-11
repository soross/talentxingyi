package com.imo.network.packages;

import java.nio.ByteBuffer;

public class GetEmployeesStatusInPacket extends CommonInPacket {
	private int num;
	private int[] cid;
	private int[] uid;
	private int[] status;

	public int getNum() {
		return num;
	}

	public int[] getCid() {
		return cid;
	}

	public int[] getUid() {
		return uid;
	}

	public int[] getStatus() {
		return status;
	}

	public GetEmployeesStatusInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub

		num = body.getInt();

		cid = new int[num];
		uid = new int[num];
		status = new int[num];

		for (int i = 0; i < num; i++) {
			cid[i] = body.getInt();
			uid[i] = body.getInt();
			status[i] = body.getInt();
		}
	}

}

package com.imo.network.packages;

import java.nio.ByteBuffer;

// UntransID int
// CID		int
// UID		int
// Mask		int
public class GetEmployeeProfileOutPacket extends CommonOutPacket {

	public GetEmployeeProfileOutPacket(ByteBuffer aBody, short aCommand, int aCid, int aUid) {
		super(aBody, aCommand, aCid, aUid);
	}

	public static ByteBuffer GenerateEmployeeProfileBody(int aUntransID, int aCid, int aUid, int aMask) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(32);
		dataBuffer.putInt(aUntransID);
		dataBuffer.putInt(aCid);
		dataBuffer.putInt(aUid);
		dataBuffer.putInt(aMask);

		return dataBuffer;
	}

}

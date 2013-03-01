package com.imo.network.packages;

import java.nio.ByteBuffer;

public class ContactorGroupUCInPacket extends CommonInPacket{
	
	private int unTransID;
	private short unRet;
	private int unContactorGroupListUC;
	
	public int getUnTransID() {
		return unTransID;
	}

	public short getUnRet() {
		return unRet;
	}

	public int getUnContactorGroupListUC() {
		return unContactorGroupListUC;
	}

	public ContactorGroupUCInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		unTransID = body.getInt();
		unRet = body.getShort();
		unContactorGroupListUC = body.getInt();
	}

}

package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetUserNgGroupListUcInPacket extends CommonInPacket{

	private int unTransID ;
	private int unRet;
	private int unUserNgroupListUC;
	
	public GetUserNgGroupListUcInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		
		unTransID = body.getInt();
		unRet = body.getInt();
		unUserNgroupListUC = body.getInt();
		
		LogFactory.d("GetUserNgGroupListUcInPacket....", this.toString());
	}

	@Override
	public String toString() {
		return "GetUserNgGroupListUcInPacket [unTransID=" + unTransID
				+ ", unRet=" + unRet + ", unUserNgroupListUC="
				+ unUserNgroupListUC + "]";
	}

	public int getUnTransID() {
		return unTransID;
	}


	public int getUnRet() {
		return unRet;
	}


	public int getUnUserNgroupListUC() {
		return unUserNgroupListUC;
	}

}

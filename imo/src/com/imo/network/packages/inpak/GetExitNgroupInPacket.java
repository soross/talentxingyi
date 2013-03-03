package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetExitNgroupInPacket extends CommonInPacket {

	private int transid;
	private int unRet;
	private int ngroup_id;
	 
	 
	public GetExitNgroupInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		unRet = body.getInt();
		ngroup_id = body.getInt();
	
		LogFactory.d("GetExitNgroupInPacket...", this.toString());
	}


	public int getTransid() {
		return transid;
	}


	public int getUnRet() {
		return unRet;
	}


	public int getNgroup_id() {
		return ngroup_id;
	}


	@Override
	public String toString() {
		return "GetExitNgroupInPacket [transid=" + transid + ", unRet=" + unRet
				+ ", ngroup_id=" + ngroup_id + "]";
	}



	
}

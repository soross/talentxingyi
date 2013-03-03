package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetAgreeJoinNgroupInPacket extends CommonInPacket{

	private int transid;
	private int ret;
	private int ngroup_id;
	
	public GetAgreeJoinNgroupInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		transid = body.getInt();
		ret = body.getInt();
		ngroup_id = body.getInt();
		
		LogFactory.d("GetAgreeJoinNgroupInPacket...", this.toString());
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getNgroup_id() {
		return ngroup_id;
	}

	@Override
	public String toString() {
		return "GetAgreeJoinNgroupInPacket [transid=" + transid + ", ret="
				+ ret + ", ngroup_id=" + ngroup_id + "]";
	}

}

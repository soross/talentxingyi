package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetExitNgroupNoticeUsersInPacket extends CommonInPacket {
	private int ngroup_id;
	private int unCid;
	private int unUid;
	
	public GetExitNgroupNoticeUsersInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		ngroup_id = body.getInt();
		unCid = body.getInt();
		unUid = body.getInt();
		
		LogFactory.d("GetExitNgroupNoticeUsersInPacket...", this.toString());
	}

	public int getNgroup_id() {
		return ngroup_id;
	}

	public int getUnCid() {
		return unCid;
	}

	public int getUnUid() {
		return unUid;
	}

	@Override
	public String toString() {
		return "GetExitNgroupNoticeUsersInPacket [ngroup_id=" + ngroup_id
				+ ", unCid=" + unCid + ", unUid=" + unUid + "]";
	}

}

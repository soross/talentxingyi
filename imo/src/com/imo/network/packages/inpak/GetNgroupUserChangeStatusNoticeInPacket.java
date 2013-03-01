package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetNgroupUserChangeStatusNoticeInPacket extends CommonInPacket {

	private int ngroup_id;
	private int cid;
	private int uid;
	private int status;
	private int type;
	
	public GetNgroupUserChangeStatusNoticeInPacket(ByteBuffer aData,
			int aBodyLen) {
		super(aData, aBodyLen);
		ngroup_id = body.getInt();
		cid = body.getInt();
		uid = body.getInt();
		status = body.getInt();
		type = body.getInt();
		
		LogFactory.d("GetNgroupUserChangeStatusNoticeInPacket...", this.toString());
	}

	public int getNgroup_id() {
		return ngroup_id;
	}

	public int getCid() {
		return cid;
	}

	public int getUid() {
		return uid;
	}

	public int getStatus() {
		return status;
	}

	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		return "GetNgroupUserChangeStatusNoticeInPacket [ngroup_id="
				+ ngroup_id + ", cid=" + cid + ", uid=" + uid + ", status="
				+ status + ", type=" + type + "]";
	}


	
}

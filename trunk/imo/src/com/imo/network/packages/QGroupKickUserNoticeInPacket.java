package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;

public class QGroupKickUserNoticeInPacket extends CommonInPacket {
	private int qgroup_id;
	private int cid;
	private int uid;


	public QGroupKickUserNoticeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		
		qgroup_id = body.getInt();
		cid = body.getInt();
		uid = body.getInt();

		LogFactory.d("", this.toString());
	}


	@Override
	public String toString() {
		return "QGroupKickUserNoticeInPacket [qgroup_id=" + qgroup_id
				+ ", cid=" + cid + ", uid=" + uid + "]";
	}


	public int getQgroup_id() {
		return qgroup_id;
	}


	public int getCid() {
		return cid;
	}


	public int getUid() {
		return uid;
	}
	
	
}

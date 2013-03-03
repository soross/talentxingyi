package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;

public class QGroupUserChangeStatusNoticeInPacket extends CommonInPacket {
	private int qgroup_id;
	private int cid;
	private int uid;
	private int status;
	private int type;

	public QGroupUserChangeStatusNoticeInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		qgroup_id = body.getInt();
		cid = body.getInt();
		uid = body.getInt();
		status = body.getInt();
		type = body.getInt();
		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "QGroupUserChangeStatusNoticeInPacket [qgroup_id=" + qgroup_id
				+ ", cid=" + cid + ", uid=" + uid + ", status=" + status
				+ ", type=" + type + "]";
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

	public int getStatus() {
		return status;
	}

	public int getType() {
		return type;
	}

}

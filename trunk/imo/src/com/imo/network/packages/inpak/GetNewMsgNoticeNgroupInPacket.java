package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetNewMsgNoticeNgroupInPacket extends CommonInPacket {
	/**
	 * 1、type=0，普通聊天消息
		2、type=1，投票通知消息，客户端自己定义msg中的格式，服务端不关心内容，只做转发。
		3、其他待定	
	 */
	private int type;
	private int ngroup_id;
	private int from_cid;
	private int from_uid;
	private int time;
	private String msg;
	
	public GetNewMsgNoticeNgroupInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		type = body.getInt();
		ngroup_id = body.getInt();
		from_cid = body.getInt();
		from_uid = body.getInt();
		time = body.getInt();
		int msgLen = body.getInt();
		byte[] msgByte = new byte[msgLen];
		body.get(msgByte);
		msg = StringUtils.UNICODE_TO_UTF8(msgByte);
		
		LogFactory.d("GetNewMsgNoticeNgroupInPacket...", this.toString());
	}

	public int getType() {
		return type;
	}

	public int getNgroup_id() {
		return ngroup_id;
	}

	public int getFrom_cid() {
		return from_cid;
	}

	public int getFrom_uid() {
		return from_uid;
	}

	public int getTime() {
		return time;
	}

	public String getMsg() {
		return msg;
	}

	@Override
	public String toString() {
		return "GetNewMsgNoticeNgroupInPacket [type=" + type + ", ngroup_id="
				+ ngroup_id + ", from_cid=" + from_cid + ", from_uid="
				+ from_uid + ", time=" + time + ", msg=" + msg + "]";
	}

}

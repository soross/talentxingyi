package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonInPacket;
import com.imo.network.packages.domain.GroupOfflineMsg;
import com.imo.util.LogFactory;

public class GetOffLineNgroupChatMsgInPacket extends CommonInPacket {
	private int transid ;
	private int ret ;
	private int ngroup_id ;
	private int end_flag;
	private int unTotalNum ;
	private int unNum ;
	private List<GroupOfflineMsg> msgs;
	
	
	public GetOffLineNgroupChatMsgInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		ngroup_id = body.getInt();
		end_flag = body.getInt();
		unTotalNum = body.getInt();
		unNum = body.getInt();

		if (unNum != 0) {
			msgs = new ArrayList<GroupOfflineMsg>();
			for (int i = 0; i < unNum; i++) {
				int type = body.getInt();
				int from_cid = body.getInt();
				int from_uid = body.getInt();
				int time = body.getInt();
				int msgid = body.getInt();
				int msgLen = body.getInt();
				byte[] msgByte = new byte[msgLen];
				body.get(msgByte);
				String msgString = StringUtils.UNICODE_TO_UTF8(msgByte);
				GroupOfflineMsg msg = new GroupOfflineMsg(type, from_cid, from_uid, time, msgid, msgString);
				msgs.add(msg);
			}
		}
		
		LogFactory.d("GetOffLineNgroupChatMsgInPacket...", this.toString());
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


	public int getEnd_flag() {
		return end_flag;
	}


	public int getUnTotalNum() {
		return unTotalNum;
	}


	public int getUnNum() {
		return unNum;
	}


	public List<GroupOfflineMsg> getMsgs() {
		return msgs;
	}


	@Override
	public String toString() {
		return "GetOffLineNgroupChatMsgInPacket [transid=" + transid + ", ret="
				+ ret + ", ngroup_id=" + ngroup_id + ", end_flag=" + end_flag
				+ ", unTotalNum=" + unTotalNum + ", unNum=" + unNum + ", msgs="
				+ msgs + "]";
	}



	
	

}

package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.CommonInPacket;
import com.imo.network.packages.domain.DialogueInfo;
import com.imo.util.LogFactory;

public class GetUserNgGroupListUcNewInPacket extends CommonInPacket{

	private int unTransID ;
	private int unRet ;
	private int unEndflag ;
	private int unTotalNum ;
	private int unNum ;
	private List<DialogueInfo> infos;
	
	public GetUserNgGroupListUcNewInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		unTransID = body.getInt();
		unRet = body.getInt();
		unEndflag = body.getInt();
		unTotalNum = body.getInt();
		unNum = body.getInt();
		
		if (unNum != 0) {
			infos = new ArrayList<DialogueInfo>();
			for (int i = 0; i < unNum; i++) {
				 int unNgroup_id = body.getInt();
				 int unSubRet= body.getInt();
				 int unNgroup_sessionid= body.getInt();
				 int groupLength = body.getInt();
				 byte[] group_name = new byte[groupLength];
				 body.get(group_name);
				 String sNgroup_name= StringUtils.UNICODE_TO_UTF8(group_name);
				 
				 int announcementLength = body.getInt();
				 byte[] announcement = new byte[announcementLength];
				 body.get(announcement);
				 String sNgroup_announcement=  StringUtils.UNICODE_TO_UTF8(announcement);
				 int unHost_cid= body.getInt();
				 int unHost_uid= body.getInt();
				
				 DialogueInfo info = new DialogueInfo(unNgroup_id,unSubRet,unNgroup_sessionid,sNgroup_name,sNgroup_announcement,unHost_cid,unHost_uid);
				 infos.add(info);
				 
				 LogFactory.d("GetUserNgGroupListUcNewInPacket...", this.toString());
			}
		}
		
		
		
	}

	
	
	public int getUnTransID() {
		return unTransID;
	}


	public int getUnRet() {
		return unRet;
	}



	public int getUnEndflag() {
		return unEndflag;
	}


	public int getUnTotalNum() {
		return unTotalNum;
	}


	public int getUnNum() {
		return unNum;
	}


	public List<DialogueInfo> getInfos() {
		return infos;
	}


	@Override
	public String toString() {
		return "GetUserNgGroupListUcNewInPacket [unTransID=" + unTransID
				+ ", unRet=" + unRet + ", unEndflag=" + unEndflag
				+ ", unTotalNum=" + unTotalNum + ", unNum=" + unNum
				+ ", infos=" + infos + "]";
	}

}

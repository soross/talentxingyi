package com.imo.network.packages;

import java.nio.ByteBuffer;

import android.util.Log;

import com.imo.network.Encrypt.StringUtils;

public class GetOffMsgFromContactorInPacket extends CommonInPacket{

	private short ret;
	private byte endflag;
	private int fromcid;
	private int fromuid;
	private int tnum;
	private int num;
	private OfflineMsgItem[] offlineMsgArray;
	
	public short getRet() {
		return ret;
	}

	public byte getEndflag() {
		return endflag;
	}

	public int getFromcid() {
		return fromcid;
	}

	public int getFromuid() {
		return fromuid;
	}
	
	public int getTnum() {
		return tnum;
	}

	public int getNum() {
		return num;
	}

	public OfflineMsgItem[] getOfflineMsgArray() {
		return offlineMsgArray;
	}
	
	public GetOffMsgFromContactorInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		ret = body.getShort();
		
		if(ret != 0)
			return;
		
		fromcid = body.getInt();
		fromuid = body.getInt();
		
		Log.e("GetOffMsgContent:", "cid = "+fromcid+",uid = "+fromuid);
		
		endflag = body.get();
		tnum = body.getInt();
		num = body.getInt();
		
		offlineMsgArray = new OfflineMsgItem[num];
		
		for(int i = 0 ; i < num ; i++)
		{
			int time = body.getInt();
			int msgid = body.getInt();
			
			int msgLen = body.getInt();
			byte[] msgBuffer = new byte[msgLen];
			body.get(msgBuffer);
			String msg = StringUtils.UNICODE_TO_UTF8(msgBuffer);
			offlineMsgArray[i] = new OfflineMsgItem( time, msgid, msg);
			
			msgBuffer = null;
		}
	}
	
}

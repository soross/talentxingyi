package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class GetOfflineMsgProfileInPacket extends CommonInPacket{

	private short ret;
	private byte endflag;
	private int tnum;
	private int num;
	private OfflineMsgProfileItem[] offlineMsgArray;
	
	public short getRet() {
		return ret;
	}

	public byte getEndflag() {
		return endflag;
	}

	public int getTnum() {
		return tnum;
	}

	public int getNum() {
		return num;
	}

	public OfflineMsgProfileItem[] getOfflineMsgArray() {
		return offlineMsgArray;
	}
	
	public GetOfflineMsgProfileInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		ret = body.getShort();
		if(ret != 0)
			return;
		
		endflag = body.get();
		tnum = body.getInt();
		num = body.getInt();
		
		offlineMsgArray = new OfflineMsgProfileItem[num];
		
		for(int i = 0 ; i < num ; i++)
		{
			int subresult = body.getInt();
			int count = body.getInt();
			int fromcid = body.getInt();
			int fromuid = body.getInt();
			int time = body.getInt();
			
			int msgLen = body.getInt();
			byte[] msgBuffer = new byte[msgLen];
			body.get(msgBuffer);
			String msg = StringUtils.UNICODE_TO_UTF8(msgBuffer);
			offlineMsgArray[i] = new OfflineMsgProfileItem(subresult, count, fromcid, fromuid, time, msg);
			
			msgBuffer = null;
		}
	}

}

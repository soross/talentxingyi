package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.util.LogFactory;

public class OuterContactorListInPacket extends CommonInPacket{
	
	private short ret;
	private byte endflag;
	private int tnum;
	private int num;
	private int[] contactor_cid;
	private int[] contactor_uid;
	private int[] group_id;
	private int[] flag;
	
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
	
	public int[] getContactor_cid() {
		return contactor_cid;
	}

	public int[] getContactor_uid() {
		return contactor_uid;
	}
	
	public int[] getGroup_id() {
		return group_id;
	}

	public int[] getFlag() {
		return flag;
	}
	
	public OuterContactorListInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		ret = body.getShort();
		if(ret != 0)
			return;
		
		endflag = body.get();
		tnum = body.getInt();
		num = body.getInt();
		contactor_cid = new int[num];
		contactor_uid = new int[num];
		group_id = new int[num];
		flag = new int[num];
		
		for(int i = 0 ;i < num; i++)
		{
			contactor_cid[i] = body.getInt();
			contactor_uid[i] = body.getInt();
			group_id[i] = body.getInt();
			flag[i] = body.getInt();
			
			LogFactory.e("OuterContactorListInPacket", "group_id["+i+"] :"+group_id[i]+",contactor_uid["+i+"] :"+contactor_uid[i]);
		}
	}
}

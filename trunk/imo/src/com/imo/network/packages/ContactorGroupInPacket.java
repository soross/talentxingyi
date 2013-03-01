package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class ContactorGroupInPacket extends CommonInPacket{
	
	private short ret;
	private byte endflag;
	private int tnum;
	private int num;
	private int[] group_id;
	private String[] group_name;
	
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

	public int[] getGroup_id() {
		return group_id;
	}

	public String[] getGroup_name() {
		return group_name;
	}
	
	public ContactorGroupInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		ret = body.getShort();
		
		if( ret != 0 )
			return;
		
		endflag = body.get();
		tnum = body.getInt();
		num = body.getInt();
		group_id = new int[num];
		group_name = new String[num];
		
		for(int i = 0 ;i < num; i++)
		{
			group_id[i] = body.getInt();
			
			int temp_user_groupnameLen = body.getInt();			
			byte[] temp_user_groupname_buffer = new byte[temp_user_groupnameLen];
			body.get(temp_user_groupname_buffer);
			group_name[i] = StringUtils.UNICODE_TO_UTF8(temp_user_groupname_buffer);
			temp_user_groupname_buffer = null;
			
			LogFactory.e("联系人分组", "group_name["+i+"] :"+group_name[i]+",group_id["+i+"] :"+group_id[i]);
		}
	}

}

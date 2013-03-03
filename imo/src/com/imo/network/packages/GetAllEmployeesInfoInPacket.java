package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class GetAllEmployeesInfoInPacket extends CommonInPacket{

	private short commandRet;
	private byte endflag;
	private int tnum;
	private int num;
	private EmployeeInfoItem[] employeesInfoArray;
	
	
	public short getCommandRet() {
		return commandRet;
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

	public EmployeeInfoItem[] getEmployeesInfoArray() {
		return employeesInfoArray;
	}

	public GetAllEmployeesInfoInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		commandRet = body.getShort();
		
		if(commandRet != 0)
			return;
		
		endflag = body.get();
		tnum = body.getInt();
		num = body.getInt();
		employeesInfoArray = new EmployeeInfoItem[num];
		
		for(int i = 0 ; i < num; i++)
		{
			int temp_colleague_uid = body.getInt();
			int temp_flag = body.getInt();
			
			int temp_user_accountLen = body.getInt();			
			byte[] temp_user_account_buffer = new byte[temp_user_accountLen];
			body.get(temp_user_account_buffer);
			String temp_user_account = StringUtils.UNICODE_TO_UTF8(temp_user_account_buffer);
			
			int temp_nameLen = body.getInt();			
			byte[] temp_name_buffer = new byte[temp_nameLen];
			body.get(temp_name_buffer);
			String temp_name = StringUtils.UNICODE_TO_UTF8(temp_name_buffer);
			
			int temp_gender = body.getInt();
			
			employeesInfoArray[i] = new EmployeeInfoItem(temp_colleague_uid, temp_flag, temp_user_account, temp_name, temp_gender);
		}
	}
}

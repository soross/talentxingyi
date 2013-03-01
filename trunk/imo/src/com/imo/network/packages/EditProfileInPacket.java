package com.imo.network.packages;

import java.nio.ByteBuffer;

public class EditProfileInPacket extends CommonInPacket{

	private short ret;
	private int mask;
	private EmployeeProfileItem profileItem;
	
	public short getRet() {
		return ret;
	}

	public int getMask() {
		return mask;
	}

	public EmployeeProfileItem getProfileItem() {
		return profileItem;
	}
	
	public EditProfileInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		// TODO Auto-generated constructor stub
		
		ret = body.getShort();
		
		if (ret != 0 )
			return;
		
		mask = body.getInt();
		profileItem = new EmployeeProfileItem(mask, body);
	}

}

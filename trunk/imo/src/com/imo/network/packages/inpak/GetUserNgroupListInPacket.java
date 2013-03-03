package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;
import java.util.List;

import com.imo.network.packages.CommonInPacket;
import com.imo.util.LogFactory;

public class GetUserNgroupListInPacket extends CommonInPacket {

	private int transid;
	private int ret;
	private int num;
	private List<Integer> ngroup_id;
	
	public GetUserNgroupListInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		num = body.getInt();
		if (num != 0) {
			for (int i = 0; i < num; i++) {
				ngroup_id.add(body.getInt());
			}
		}
		
		LogFactory.d("GetUserNgroupListInPacket...", this.toString());
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getNum() {
		return num;
	}

	public List<Integer> getNgroup_id() {
		return ngroup_id;
	}

	@Override
	public String toString() {
		return "GetUserNgroupListInPacket [transid=" + transid + ", ret=" + ret
				+ ", num=" + num + ", ngroup_id=" + ngroup_id + "]";
	}
	

}

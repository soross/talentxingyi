package com.imo.network.packages.inpak;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.imo.network.packages.CommonInPacket;
import com.imo.network.packages.domain.GroupUser;
import com.imo.util.LogFactory;

public class GetNgroupUserListInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int ngroup_id;
	private int num;
	private List<GroupUser> users;
	
	public GetNgroupUserListInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		ngroup_id = body.getInt();
		num = body.getInt();
		if (num != 0) {
			users = new ArrayList<GroupUser>();
			for (int i = 0; i < num; i++) {
				int cid = body.getInt();
				int uid = body.getInt();
				GroupUser user = new GroupUser(cid, uid);
				users.add(user);
			}
		}
		
		LogFactory.d("GetNgroupUserListInPacket...", this.toString());
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

	public int getNum() {
		return num;
	}

	public List<GroupUser> getUsers() {
		return users;
	}

	@Override
	public String toString() {
		return "GetNgroupUserListInPacket [transid=" + transid + ", ret=" + ret
				+ ", ngroup_id=" + ngroup_id + ", num=" + num + ", users="
				+ users + "]";
	}

}

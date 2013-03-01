package com.imo.network.packages;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.imo.network.Encrypt.StringUtils;
import com.imo.network.packages.domain.QgroupOffline;
import com.imo.util.LogFactory;

public class GetQGroupOfflineInPacket extends CommonInPacket {
	private int transid;
	private int ret;
	private int endflag;
	private int totalNum;
	private int num;
	private QgroupOffline[] qgroupOfflines;

	public GetQGroupOfflineInPacket(ByteBuffer aData, int aBodyLen) {
		super(aData, aBodyLen);
		transid = body.getInt();
		ret = body.getInt();
		endflag = body.getInt();
		totalNum = body.getInt();
		num = body.getInt();
		qgroupOfflines = new QgroupOffline[num];
		for (int i = 0; i < num; i++) {
			qgroupOfflines[i].setMsg_id(body.getInt());
			qgroupOfflines[i].setMsgType(body.getInt());
			qgroupOfflines[i].setHost_cid(body.getInt());
			qgroupOfflines[i].setHost_uid(body.getInt());
			qgroupOfflines[i].setQgroupid(body.getInt());
			qgroupOfflines[i].setQuery_groupid(body.getInt());

			int group_name_length = body.getInt();
			byte[] group_name_buffer = new byte[group_name_length];
			body.get(group_name_buffer);
			qgroupOfflines[i].setGroup_name(StringUtils
					.UNICODE_TO_UTF8(group_name_buffer));

			int text_length = body.getInt();
			byte[] text_buffer = new byte[text_length];
			body.get(text_buffer);
			qgroupOfflines[i].setText(StringUtils.UNICODE_TO_UTF8(text_buffer));

		}

		LogFactory.d("", this.toString());
	}

	@Override
	public String toString() {
		return "GetQGroupOfflineInPacket [transid=" + transid + ", ret=" + ret
				+ ", endflag=" + endflag + ", totalNum=" + totalNum + ", num="
				+ num + ", qgroupOfflines=" + Arrays.toString(qgroupOfflines)
				+ "]";
	}

	public int getTransid() {
		return transid;
	}

	public int getRet() {
		return ret;
	}

	public int getEndflag() {
		return endflag;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public int getNum() {
		return num;
	}

	public QgroupOffline[] getQgroupOfflines() {
		return qgroupOfflines;
	}

}

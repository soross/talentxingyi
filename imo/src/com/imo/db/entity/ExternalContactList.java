package com.imo.db.entity;

/**
 * ӳ�����ݿ��ExternalContactListInfo
 * 
 * @author fengxiaowei
 * 
 */
public class ExternalContactList {
	private Integer gId;
	private Integer cId;
	private Integer uId;
	private Integer flag;

	public ExternalContactList() {
	}

	public ExternalContactList(Integer gId, Integer cId, Integer uId, Integer flag) {
		this.gId = gId;
		this.cId = cId;
		this.uId = uId;
		this.flag = flag;
	}

	public Integer getgId() {
		return gId;
	}

	public void setgId(Integer gId) {
		this.gId = gId;
	}

	public Integer getcId() {
		return cId;
	}

	public void setcId(Integer cId) {
		this.cId = cId;
	}

	public Integer getuId() {
		return uId;
	}

	public void setuId(Integer uId) {
		this.uId = uId;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return gId + "  " + cId + "  " + uId + "  " + flag;
	}

	@Override
	public boolean equals(Object o) {
		final ExternalContactList externalContactList = (ExternalContactList) o;

		return this.uId == externalContactList.uId;
	}

}

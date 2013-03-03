package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌDeptUserInfo
 */
public class DeptUser {
	private Integer dId;
	private Integer uId;
	private Integer nextSiblingUid;

	public DeptUser() {
	}

	public DeptUser(Integer dId, Integer uId, Integer nextSiblingUid) {
		this.dId = dId;
		this.uId = uId;
		this.nextSiblingUid = nextSiblingUid;
	}

	public Integer getdId() {
		return dId;
	}

	public void setdId(Integer dId) {
		this.dId = dId;
	}

	public Integer getuId() {
		return uId;
	}

	public void setuId(Integer uId) {
		this.uId = uId;
	}

	public Integer getNextSiblingUid() {
		return nextSiblingUid;
	}

	public void setNextSiblingUid(Integer nextSiblingUid) {
		this.nextSiblingUid = nextSiblingUid;
	}

	@Override
	public boolean equals(Object o) {
		final DeptUser deptUser = (DeptUser) o;
		return (this.dId == deptUser.dId) && (this.uId == deptUser.uId);
	}

	@Override
	public String toString() {
		return dId + "  " + uId + "  " + nextSiblingUid;
	}

}

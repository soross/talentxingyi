package com.imo.db.entity;
/**
 * ”≥…‰ ˝æ›ø‚±ÌInnerGroupInfo
 * 
 * @author fengxiaowei
 * 
 */
public class InnerGroup {
	private Integer gId;
	private String name;

	public InnerGroup() {
	}

	public InnerGroup(Integer gId, String name) {
		super();
		this.gId = gId;
		this.name = name;
	}

	public Integer getgId() {
		return gId;
	}

	public void setgId(Integer gId) {
		this.gId = gId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		final InnerGroup innerGroup = (InnerGroup) o;
		return this.gId == innerGroup.gId;
	}

	@Override
	public String toString() {
		return gId + "  " + name;
	}

}

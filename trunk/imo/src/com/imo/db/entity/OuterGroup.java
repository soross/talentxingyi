package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌOuterGroupInfo
 * 
 * @author fengxiaowei
 * 
 */
public class OuterGroup {
	private Integer gId;
	private String name;

	public OuterGroup() {
	}

	public OuterGroup(Integer gId, String name) {
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
		final OuterGroup outerGroup = (OuterGroup) o;
		return this.gId == outerGroup.gId;
	}

	@Override
	public String toString() {
		return gId + "  " + name;
	}

}

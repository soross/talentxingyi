package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌInnerGroupUC
 * 
 * @author fengxiaowei
 * 
 */
public class InnerGroupUc {
	private Integer innerGroupUC;

	public InnerGroupUc() {
	}

	public InnerGroupUc(Integer innerGroupUC) {
		this.innerGroupUC = innerGroupUC;
	}

	public Integer getInnerGroupUC() {
		return innerGroupUC;
	}

	public void setInnerGroupUC(Integer innerGroupUC) {
		this.innerGroupUC = innerGroupUC;
	}

	@Override
	public boolean equals(Object o) {
		final InnerGroupUc innerGroupUc = (InnerGroupUc) o;

		return this.innerGroupUC == innerGroupUc.innerGroupUC;
	}

	@Override
	public String toString() {
		return innerGroupUC + " ";
	}

}

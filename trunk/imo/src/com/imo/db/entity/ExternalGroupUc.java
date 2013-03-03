package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌExternalGroupUC
 * 
 * @author fengxiaowei
 * 
 */
public class ExternalGroupUc {
	private Integer externalGroupUC;

	public ExternalGroupUc() {
	}

	public ExternalGroupUc(Integer externalGroupUC) {
		this.externalGroupUC = externalGroupUC;
	}

	public Integer getExternalGroupUC() {
		return externalGroupUC;
	}

	public void setExternalGroupUC(Integer externalGroupUC) {
		this.externalGroupUC = externalGroupUC;
	}

	@Override
	public boolean equals(Object o) {
		final ExternalGroupUc externalGroupUc = (ExternalGroupUc) o;
		return this.externalGroupUC == externalGroupUc.externalGroupUC;
	}

	@Override
	public String toString() {
		return externalGroupUC + " ";
	}

}

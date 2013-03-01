package com.imo.db.entity;
/**
 * ”≥…‰ ˝æ›ø‚±ÌExternalContactUC
 * 
 * @author fengxiaowei
 * 
 */
public class ExternalContactorUC {

	private Integer ExternalContactUC;

	public ExternalContactorUC() {
	}

	public ExternalContactorUC(Integer externalContactUC) {
		ExternalContactUC = externalContactUC;
	}

	public Integer getExternalContactUC() {
		return ExternalContactUC;
	}

	public void setExternalContactUC(Integer externalContactUC) {
		ExternalContactUC = externalContactUC;
	}

	@Override
	public boolean equals(Object o) {
		final ExternalContactorUC externalContactorUC = (ExternalContactorUC) o;
		return this.ExternalContactUC == externalContactorUC.ExternalContactUC;
	}

	@Override
	public String toString() {
		return ExternalContactUC + "  ";
	}

}

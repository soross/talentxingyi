package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌInnerContactUC
 */
public class InnerContactorUC {
	private Integer innerContactUC;

	public InnerContactorUC() {
	}

	public InnerContactorUC(Integer innerContactUC) {
		super();
		this.innerContactUC = innerContactUC;
	}

	public Integer getInnerContactUC() {
		return innerContactUC;
	}

	public void setInnerContactUC(Integer innerContactUC) {
		this.innerContactUC = innerContactUC;
	}

	@Override
	public boolean equals(Object o) {
		final InnerContactorUC innerContactorUC = (InnerContactorUC) o;
		return this.innerContactUC == innerContactorUC.innerContactUC;
	}

	@Override
	public String toString() {
		return innerContactUC + " ";
	}

}

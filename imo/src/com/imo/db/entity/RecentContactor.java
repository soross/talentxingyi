package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌRecentContact
 * 
 * @author fengxiaowei
 * 
 */
public class RecentContactor {
	private Integer type;
	private Integer recentId;
	private Integer time;

	public RecentContactor() {
	}

	public RecentContactor(Integer type, Integer recentId, Integer time) {
		this.type = type;
		this.recentId = recentId;
		this.time = time;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getRecentId() {
		return recentId;
	}

	public void setRecentId(Integer recentId) {
		this.recentId = recentId;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return type + "  " + recentId + "  " + time;
	}

	@Override
	public boolean equals(Object o) {
		final RecentContactor recentContactor = (RecentContactor) o;
		return (this.type == recentContactor.type) && (this.recentId == recentContactor.recentId);
	}

}

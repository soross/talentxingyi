package com.imo.activity;

/**
 * ActivityTag ¿‡
 */
public class ActivityTag {

	private final Class<?> mActivityCls;

	private final String mId;

	public ActivityTag(Class<?> mActivityCls, String mId) {
		this.mActivityCls = mActivityCls;
		this.mId = mId;
	}

	public Class<?> getActivityClass() {
		return mActivityCls;
	}

	public String getId() {
		return mId;
	}

	@Override
	public String toString() {
		return mId;
	}
}

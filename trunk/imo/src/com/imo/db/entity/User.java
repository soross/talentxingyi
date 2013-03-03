package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌUserBaseInfo
 */
public class User {
	private Integer uId;
	private Integer cId;
	private String corpAccount;
	private String account;
	private String name;
	private Integer gender;

	public User() {
	};

	public User(Integer uId, Integer cId, String corpAccount, String account, String name, Integer gender) {
		this.uId = uId;
		this.cId = cId;
		this.corpAccount = corpAccount;
		this.account = account;
		this.name = name;
		this.gender = gender;
	}

	public Integer getuId() {
		return uId;
	}

	public void setuId(Integer uId) {
		this.uId = uId;
	}

	public Integer getcId() {
		return cId;
	}

	public void setcId(Integer cId) {
		this.cId = cId;
	}

	public String getCorpAccount() {
		return corpAccount;
	}

	public void setCorpAccount(String corpAccount) {
		this.corpAccount = corpAccount;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return uId + " " + cId + "  " + corpAccount + "  " + account + "  " + name + "  " + gender;
	}

	@Override
	public boolean equals(Object o) {
		final User user = (User) o;
		return this.uId == user.uId;
	}

}

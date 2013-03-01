package com.imo.db.entity;

/**
 * ”≥…‰ ˝æ›ø‚±ÌDeptInfo
 * 
 * @author fengxiaowei
 * 
 */
public class Dept {
	private Integer cId;
	private Integer dId;
	private String name;
	private Integer pDid;
	private Integer uC;
	private Integer deptUserUC;
	private Integer firstChild;
	private Integer nextSibling;
	private String desp;
	private String fax;
	private String hideDeptList;
	private String addr;
	private String tel;
	private String website;
	private Integer firstChildUser;

	public Dept() {

	}

	public Dept(Integer cId, Integer dId, String name, Integer pDid, Integer uC, Integer deptUserUC, Integer firstChild, Integer nextSibling,
			String desp, String fax, String hideDeptList, String addr, String tel, String website, Integer firstChildUser) {
		this.cId = cId;
		this.dId = dId;
		this.name = name;
		this.pDid = pDid;
		this.uC = uC;
		this.deptUserUC = deptUserUC;
		this.firstChild = firstChild;
		this.nextSibling = nextSibling;
		this.desp = desp;
		this.fax = fax;
		this.hideDeptList = hideDeptList;
		this.addr = addr;
		this.tel = tel;
		this.website = website;
		this.firstChildUser = firstChildUser;
	}

	public Integer getcId() {
		return cId;
	}

	public void setcId(Integer cId) {
		this.cId = cId;
	}

	public Integer getdId() {
		return dId;
	}

	public void setdId(Integer dId) {
		this.dId = dId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getpDid() {
		return pDid;
	}

	public void setpDid(Integer pDid) {
		this.pDid = pDid;
	}

	public Integer getuC() {
		return uC;
	}

	public void setuC(Integer uC) {
		this.uC = uC;
	}

	public Integer getDeptUserUC() {
		return deptUserUC;
	}

	public void setDeptUserUC(Integer deptUserUC) {
		this.deptUserUC = deptUserUC;
	}

	public Integer getFirstChild() {
		return firstChild;
	}

	public void setFirstChild(Integer firstChild) {
		this.firstChild = firstChild;
	}

	public Integer getNextSibling() {
		return nextSibling;
	}

	public void setNextSibling(Integer nextSibling) {
		this.nextSibling = nextSibling;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getHideDeptList() {
		return hideDeptList;
	}

	public void setHideDeptList(String hideDeptList) {
		this.hideDeptList = hideDeptList;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Integer getFirstChildUser() {
		return firstChildUser;
	}

	public void setFirstChildUser(Integer firstChildUser) {
		this.firstChildUser = firstChildUser;
	}

	@Override
	public String toString() {
		return cId + "  " + dId + "  " + name + "  " + pDid + "  " + uC + "  " + deptUserUC + "  " + firstChild + "  " + nextSibling + "  " + desp
				+ "  " + fax + "  " + hideDeptList + "  " + addr + "  " + tel + "  " + website + "  " + firstChildUser;
	}

	@Override
	public boolean equals(Object o) {
		final Dept dept = (Dept) o;

		return (this.cId == dept.cId) && (this.dId == dept.dId);
	}

}

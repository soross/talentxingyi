package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class DeptMaskItem {

	private int dept_id;
	private int parent_dept_id;
	private String name;
	private String desp;
	private int dept_uc;
	private String fax;
	private String hide_dept_list;
	private int first_child;
	private int next_sibling;
	private String Faddr;
	private String Ftel;
	private String Fwebsite;
	private int Ffirst_user;
	private int Fdept_user_uc;

	public DeptMaskItem() {
	}

	public DeptMaskItem(int dept_id, int parent_dept_id, String name,
			String desp, int dept_uc, String fax, String hide_dept_list,
			int first_child, int next_sibling, String faddr, String ftel,
			String fwebsite, int ffirst_user, int fdept_user_uc) {
		super();
		this.dept_id = dept_id;
		this.parent_dept_id = parent_dept_id;
		this.name = name;
		this.desp = desp;
		this.dept_uc = dept_uc;
		this.fax = fax;
		this.hide_dept_list = hide_dept_list;
		this.first_child = first_child;
		this.next_sibling = next_sibling;
		Faddr = faddr;
		Ftel = ftel;
		Fwebsite = fwebsite;
		Ffirst_user = ffirst_user;
		Fdept_user_uc = fdept_user_uc;
	}

	public int getDept_id() {
		return dept_id;
	}

	public int getParent_dept_id() {
		return parent_dept_id;
	}

	public String getName() {
		return name;
	}

	public String getDesp() {
		return desp;
	}

	public int getDept_uc() {
		return dept_uc;
	}

	public String getFax() {
		return fax;
	}

	public String getHide_dept_list() {
		return hide_dept_list;
	}

	public int getFirst_child() {
		return first_child;
	}

	public int getNext_sibling() {
		return next_sibling;
	}

	public String getFaddr() {
		return Faddr;
	}

	public String getFtel() {
		return Ftel;
	}

	public String getFwebsite() {
		return Fwebsite;
	}

	public int getFfirst_user() {
		return Ffirst_user;
	}

	public int getFdept_user_uc() {
		return Fdept_user_uc;
	}

	public DeptMaskItem(int aMask, ByteBuffer aBody) {
		String resultStr = Integer.toBinaryString(aMask);
		char[] maskArray = resultStr.toCharArray();

		for (int i = maskArray.length - 1; i > 0; i--) {
			if ('1' == maskArray[i]) {
				switch (i) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
				case 8:
					break;
				case 9:
					break;
				case 10:
					break;
				case 11:
					break;
				case 12:
					break;
				case 13:
					break;
				case 14:
					break;
				case 15:
					break;
				case 16:
					break;
				case 17:
					break;
				case 18:
					Fdept_user_uc = aBody.getInt();
					break;
				case 19:
					Ffirst_user = aBody.getInt();
					break;
				case 20:
					int FwebsiteLen = aBody.getInt();
					byte[] FwebsiteBuffer = new byte[FwebsiteLen];
					aBody.get(FwebsiteBuffer);
					Fwebsite = StringUtils.UNICODE_TO_UTF8(FwebsiteBuffer);
					break;
				case 21:
					int FtelLen = aBody.getInt();
					byte[] FtelBuffer = new byte[FtelLen];
					aBody.get(FtelBuffer);
					Ftel = StringUtils.UNICODE_TO_UTF8(FtelBuffer);
					break;
				case 22:
					int FaddrLen = aBody.getInt();
					byte[] FaddrBuffer = new byte[FaddrLen];
					aBody.get(FaddrBuffer);
					Faddr = StringUtils.UNICODE_TO_UTF8(FaddrBuffer);
					break;
				case 23:
					next_sibling = aBody.getInt();
					break;
				case 24:
					first_child = aBody.getInt();
					break;
				case 25:
					int hide_dept_listLen = aBody.getInt();
					byte[] hide_dept_listBuffer = new byte[hide_dept_listLen];
					aBody.get(hide_dept_listBuffer);
					hide_dept_list = StringUtils.UNICODE_TO_UTF8(hide_dept_listBuffer);
					break;
				case 26:
					int faxLen = aBody.getInt();
					byte[] faxBuffer = new byte[faxLen];
					aBody.get(faxBuffer);
					fax = StringUtils.UNICODE_TO_UTF8(faxBuffer);
					break;
				case 27:
					dept_uc = aBody.getInt();
					break;
				case 28:
					int despLen = aBody.getInt();
					byte[] despBuffer = new byte[despLen];
					aBody.get(despBuffer);
					desp = StringUtils.UNICODE_TO_UTF8(despBuffer);
					break;
				case 29:
					int nameLen = aBody.getInt();
					byte[] nameBuffer = new byte[nameLen];
					aBody.get(nameBuffer);
					name = StringUtils.UNICODE_TO_UTF8(nameBuffer);
					break;
				case 30:
					parent_dept_id = aBody.getInt();
					break;
				case 31:
					dept_id = aBody.getInt();
					break;
				}
			}
		}

	}

	@Override
	public String toString() {
		return "DeptMaskItem [dept_id=" + dept_id + ", parent_dept_id="
				+ parent_dept_id + ", name=" + name + ", desp=" + desp
				+ ", dept_uc=" + dept_uc + ", fax=" + fax + ", hide_dept_list="
				+ hide_dept_list + ", first_child=" + first_child
				+ ", next_sibling=" + next_sibling + ", Faddr=" + Faddr
				+ ", Ftel=" + Ftel + ", Fwebsite=" + Fwebsite
				+ ", Ffirst_user=" + Ffirst_user + ", Fdept_user_uc="
				+ Fdept_user_uc + "]";
	}
	
}

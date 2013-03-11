package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;
import com.imo.util.LogFactory;

public class EmployeeProfileItem {

	public String getUser_account() {
		return user_account;
	}

	public String getCorp_account() {
		return corp_account;
	}

	public String getName() {
		return name;
	}

	public int getGender() {
		return gender;
	}

	public String getSign() {
		return sign;
	}

	public String getMobile() {
		return mobile;
	}

	public String getEmail() {
		return email;
	}

	public int getRole_id() {
		return role_id;
	}

	public int getHead_pic() {
		return head_pic;
	}

	public int getPrivacy_flag() {
		return privacy_flag;
	}

	public int getBirth() {
		return birth;
	}

	public String getPos() {
		return pos;
	}

	public String getTel() {
		return tel;
	}

	public String getDesp() {
		return desp;
	}

	public String getHide_dept_list() {
		return hide_dept_list;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	private String user_account;
	private String corp_account;
	private String name;
	private int gender;
	private String sign;
	private String mobile;
	private String email;
	private int role_id;
	private int head_pic;
	private int privacy_flag;
	private int birth;
	private String pos;
	private String tel;
	private String desp;
	private String hide_dept_list;

	public EmployeeProfileItem() {
	}

	public EmployeeProfileItem(String user_account, String corp_account, String name, int gender, String sign, String mobile, String email, int role_id, int head_pic, int privacy_flag, int birth, String pos, String tel, String desp, String hide_dept_list) {
		this.user_account = user_account;
		this.corp_account = corp_account;
		this.name = name;
		this.gender = gender;
		this.sign = sign;
		this.mobile = mobile;
		this.email = email;
		this.role_id = role_id;
		this.head_pic = head_pic;
		this.privacy_flag = privacy_flag;
		this.birth = birth;
		this.pos = pos;
		this.tel = tel;
		this.desp = desp;
		this.hide_dept_list = hide_dept_list;
	}

	public EmployeeProfileItem(int aMask, ByteBuffer aBody) {
		String resultStr = Integer.toBinaryString(aMask);
		LogFactory.d(EmployeeProfileItem.class.getSimpleName(), "mask = " + resultStr);
		char[] charArray = resultStr.toCharArray();
		int strLen = charArray.length;

		char[] maskArray = new char[32];
		System.arraycopy(charArray, 0, maskArray, 32 - strLen, strLen);

		for (int i = maskArray.length - 1; i > 0; i--) {
			if ('1' == maskArray[i]) {
				LogFactory.d(EmployeeProfileItem.class.getSimpleName(), "i = " + i + "='1'");
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
						int hide_dept_listStrLen = aBody.getInt();
						byte[] hide_dept_listBuffer = new byte[hide_dept_listStrLen];
						aBody.get(hide_dept_listBuffer);
						hide_dept_list = StringUtils.UNICODE_TO_UTF8(hide_dept_listBuffer);
						break;
					case 18:
						int despStrLen = aBody.getInt();
						byte[] despBuffer = new byte[despStrLen];
						aBody.get(despBuffer);
						desp = StringUtils.UNICODE_TO_UTF8(despBuffer);
						break;
					case 19:
						int telStrLen = aBody.getInt();
						byte[] telBuffer = new byte[telStrLen];
						aBody.get(telBuffer);
						tel = StringUtils.UNICODE_TO_UTF8(telBuffer);
						break;
					case 20:
						int posStrLen = aBody.getInt();
						byte[] posBuffer = new byte[posStrLen];
						aBody.get(posBuffer);
						pos = StringUtils.UNICODE_TO_UTF8(posBuffer);
						break;
					case 21:
						birth = aBody.getInt();
						break;
					case 22:
						privacy_flag = aBody.getInt();
						break;
					case 23:
						head_pic = aBody.getInt();
						break;
					case 24:
						role_id = aBody.getInt();
						break;
					case 25:
						int emailStrLen = aBody.getInt();
						byte[] emailBuffer = new byte[emailStrLen];
						aBody.get(emailBuffer);
						email = StringUtils.UNICODE_TO_UTF8(emailBuffer);
						break;
					case 26:
						int mobileStrLen = aBody.getInt();
						byte[] mobileBuffer = new byte[mobileStrLen];
						aBody.get(mobileBuffer);
						mobile = StringUtils.UNICODE_TO_UTF8(mobileBuffer);
						break;
					case 27:
						int signStrLen = aBody.getInt();
						byte[] signBuffer = new byte[signStrLen];
						aBody.get(signBuffer);
						sign = StringUtils.UNICODE_TO_UTF8(signBuffer);
						break;
					case 28:
						gender = aBody.getInt();
						break;
					case 29:
						int nameStrLen = aBody.getInt();
						byte[] nameBuffer = new byte[nameStrLen];
						aBody.get(nameBuffer);
						name = StringUtils.UNICODE_TO_UTF8(nameBuffer);
						break;
					case 30:
						int corp_accountStrLen = aBody.getInt();
						byte[] corp_accountBuffer = new byte[corp_accountStrLen];
						aBody.get(corp_accountBuffer);
						corp_account = StringUtils.UNICODE_TO_UTF8(corp_accountBuffer);
						break;
					case 31:
						int user_accountStrLen = aBody.getInt();
						byte[] user_accountBuffer = new byte[user_accountStrLen];
						aBody.get(user_accountBuffer);
						user_account = StringUtils.UNICODE_TO_UTF8(user_accountBuffer);
						break;
				}
			}
		}
	}

}
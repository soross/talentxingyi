package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.Encrypt.StringUtils;

public class CorpMaskItem {
	private String corp_account;
	private String short_name;
	private String cn_name;
	private String domain;
	private int state;
	private int user_card;
	private int logo_type;
	private String en_name;
	private String nation;
	private String province;
	private String city;
	private String country;
	private String addr;
	private int type;
	private String desp;
	private int zipcode;
	private String tel;
	private String fax;
	private String contactor;
	private String email;
	private String website;
	private int reg_capital;
	private int employee_num;
	private int pc_num;
	private String slogan;
	private String config;
	
	public CorpMaskItem(String corp_account, String short_name, String cn_name,
			String domain, int state, int user_card, int logo_type,
			String en_name, String nation, String province, String city,
			String country, String addr, int type, String desp, int zipcode,
			String tel, String fax, String contactor, String email,
			String website, int reg_capital, int employee_num, int pc_num,
			String slogan, String config) {
		super();
		this.corp_account = corp_account;
		this.short_name = short_name;
		this.cn_name = cn_name;
		this.domain = domain;
		this.state = state;
		this.user_card = user_card;
		this.logo_type = logo_type;
		this.en_name = en_name;
		this.nation = nation;
		this.province = province;
		this.city = city;
		this.country = country;
		this.addr = addr;
		this.type = type;
		this.desp = desp;
		this.zipcode = zipcode;
		this.tel = tel;
		this.fax = fax;
		this.contactor = contactor;
		this.email = email;
		this.website = website;
		this.reg_capital = reg_capital;
		this.employee_num = employee_num;
		this.pc_num = pc_num;
		this.slogan = slogan;
		this.config = config;
	}


	public String getCorp_account() {
		return corp_account;
	}


	public String getShort_name() {
		return short_name;
	}


	public String getCn_name() {
		return cn_name;
	}


	public String getDomain() {
		return domain;
	}


	public int getState() {
		return state;
	}


	public int getUser_card() {
		return user_card;
	}


	public int getLogo_type() {
		return logo_type;
	}


	public String getEn_name() {
		return en_name;
	}


	public String getNation() {
		return nation;
	}


	public String getProvince() {
		return province;
	}


	public String getCity() {
		return city;
	}


	public String getCountry() {
		return country;
	}


	public String getAddr() {
		return addr;
	}


	public int getType() {
		return type;
	}


	public String getDesp() {
		return desp;
	}


	public int getZipcode() {
		return zipcode;
	}


	public String getTel() {
		return tel;
	}


	public String getFax() {
		return fax;
	}


	public String getContactor() {
		return contactor;
	}


	public String getEmail() {
		return email;
	}


	public String getWebsite() {
		return website;
	}


	public int getReg_capital() {
		return reg_capital;
	}


	public int getEmployee_num() {
		return employee_num;
	}


	public int getPc_num() {
		return pc_num;
	}


	public String getSlogan() {
		return slogan;
	}


	public String getConfig() {
		return config;
	}


	public CorpMaskItem(int aMask,ByteBuffer aBody)
	{
		String resultStr = Integer.toBinaryString(aMask);
		char[] charArray = resultStr.toCharArray();
		int strLen = charArray.length;
		
		char[] maskArray = new char[32];
		System.arraycopy(charArray, 0, maskArray, 32-strLen, strLen);
		
		for(int i = maskArray.length-1 ; i > 0; i--)
		{
			if ('1' == maskArray[i] )
			{
				switch(i)
				{
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
					int configStrLen = aBody.getInt();
					byte[] configBuffer = new byte[configStrLen];
					aBody.get(configBuffer);
					config = StringUtils.UNICODE_TO_UTF8(configBuffer);
					break;
				case 7:
					int sloganStrLen = aBody.getInt();
					byte[] sloganBuffer = new byte[sloganStrLen];
					aBody.get(sloganBuffer);
					slogan = StringUtils.UNICODE_TO_UTF8(sloganBuffer);
					break;
				case 8:
					pc_num = aBody.getInt();
					break;
				case 9:
					employee_num = aBody.getInt();
					break;
				case 10:
					reg_capital = aBody.getInt();
					break;
				case 11:
					int websiteStrLen = aBody.getInt();
					byte[] websiteBuffer = new byte[websiteStrLen];
					aBody.get(websiteBuffer);
					website = StringUtils.UNICODE_TO_UTF8(websiteBuffer);
					break;
				case 12:
					int emailStrLen = aBody.getInt();
					byte[] emailBuffer = new byte[emailStrLen];
					aBody.get(emailBuffer);
					email = StringUtils.UNICODE_TO_UTF8(emailBuffer);
					break;
				case 13:
					int contactorStrLen = aBody.getInt();
					byte[] contactorBuffer = new byte[contactorStrLen];
					aBody.get(contactorBuffer);
					contactor = StringUtils.UNICODE_TO_UTF8(contactorBuffer);
					break;
				case 14:
					int faxStrLen = aBody.getInt();
					byte[] faxBuffer = new byte[faxStrLen];
					aBody.get(faxBuffer);
					fax = StringUtils.UNICODE_TO_UTF8(faxBuffer);
					break;
				case 15:
					int telStrLen = aBody.getInt();
					byte[] telBuffer = new byte[telStrLen];
					aBody.get(telBuffer);
					tel = StringUtils.UNICODE_TO_UTF8(telBuffer);
					break;
				case 16:
					zipcode = aBody.getInt();
					break;
				case 17:
					int despLen = aBody.getInt();
					byte[] despArray = new byte[despLen];
					aBody.get(despArray);
					desp = StringUtils.UNICODE_TO_UTF8(despArray);
					break;
				case 18:
					type = aBody.getInt();
					break;
				case 19:
					int addrStrLen = aBody.getInt();
					byte[] addrBuffer = new byte[addrStrLen];
					aBody.get(addrBuffer);
					addr = StringUtils.UNICODE_TO_UTF8(addrBuffer);
					break;
				case 20:
					int countryStrLen = aBody.getInt();
					byte[] countryBuffer = new byte[countryStrLen];
					aBody.get(countryBuffer);
					country = StringUtils.UNICODE_TO_UTF8(countryBuffer);
					break;
				case 21:
					int cityStrLen = aBody.getInt();
					byte[] cityBuffer = new byte[cityStrLen];
					aBody.get(cityBuffer);
					city = StringUtils.UNICODE_TO_UTF8(cityBuffer);
					break;
				case 22:
					int provinceStrLen = aBody.getInt();
					byte[] provinceBuffer = new byte[provinceStrLen];
					aBody.get(provinceBuffer);
					province = StringUtils.UNICODE_TO_UTF8(provinceBuffer);
					break;
				case 23:
					int nationStrLen = aBody.getInt();
					byte[] nationBuffer = new byte[nationStrLen];
					aBody.get(nationBuffer);
					nation = StringUtils.UNICODE_TO_UTF8(nationBuffer);
					break;
				case 24:
					int en_nameStrLen = aBody.getInt();
					byte[] en_nameBuffer = new byte[en_nameStrLen];
					aBody.get(en_nameBuffer);
					en_name = StringUtils.UNICODE_TO_UTF8(en_nameBuffer);
					break;
				case 25:
					logo_type = aBody.getInt();
					break;
				case 26:
					user_card = aBody.getInt();
					break;
				case 27:
					state = aBody.getInt();
					break;
				case 28:
					int domainStrLen = aBody.getInt();
					byte[] domainBuffer = new byte[domainStrLen];
					aBody.get(domainBuffer);
					domain = StringUtils.UNICODE_TO_UTF8(domainBuffer);
					break;
				case 29:
					int cn_nameStrLen = aBody.getInt();
					byte[] cn_nameBuffer = new byte[cn_nameStrLen];
					aBody.get(cn_nameBuffer);
					cn_name = StringUtils.UNICODE_TO_UTF8(cn_nameBuffer);
					break;
				case 30:
					int short_nameStrLen = aBody.getInt();
					byte[] short_nameBuffer = new byte[short_nameStrLen];
					aBody.get(short_nameBuffer);
					short_name = StringUtils.UNICODE_TO_UTF8(short_nameBuffer);
					break;
				case 31:
					int corp_accountStrLen = aBody.getInt();
					byte[] corp_accountBuffer = new byte[corp_accountStrLen];
					aBody.get(corp_accountBuffer);
					corp_account = StringUtils.UNICODE_TO_UTF8(corp_accountBuffer);
					break;
				}
			}
		}
		
	}
	
}

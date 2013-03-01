package com.imo.module.dialogue.recent;

import com.imo.util.LogFactory;


/**
 * 最近联系人界面 List Item 对应的数据  
 * 
 * @author CaixiaoLong
 *
 */
public class RecentContactInfo {
	
	public static final int NORMAL_TYPE = 0;

	private String category;
	
	private String imgPath;
	
	public boolean isFromLoginUser = false;
	
	private Integer cid = -1;
	private Integer uid = -1;
	private String name;
	private String info="";
	private String time;
	
	private int count = 0;
	
	private int type = 0;
	
	private boolean hasOfflineMSG = false;
	
//	private CharSequence charMsg = null;
	
	public RecentContactInfo() {

	}

	/**
	 * for getData
	 * 
	 * @param cid
	 * @param uid
	 * @param name
	 * @param info
	 * @param time
	 * @param count
	 */
	public RecentContactInfo(Integer cid, Integer uid, String name,
			String info, String time, int count) {
		this.cid = cid;
		this.uid = uid;
		this.name = name;
		this.info = info;
		this.time = time;
		this.count = count;
	}
	
	

	public RecentContactInfo(Integer cid, Integer uid, String name,
			String info, String time, int count, int type) {
		this.cid = cid;
		this.uid = uid;
		this.name = name;
		this.info = info;
		this.time = time;
		this.count = count;
		this.type = type;
		
		LogFactory.d("112", toString());
	}
	
//	public RecentContactInfo(Integer cid, Integer uid, String name,
//			CharSequence charMsg, String time, int count, int type) {
//		this.cid = cid;
//		this.uid = uid;
//		this.name = name;
//		this.charMsg = charMsg;
//		this.time = time;
//		this.count = count;
//		this.type = type;
//		
//		LogFactory.d("112", toString());
//	}
//	
	
	public int getType() {
		return type;
	}

	public boolean hasOfflineMSG() {
		return hasOfflineMSG;
	}

	public void setHasOfflineMSG(boolean hasOfflineMSG) {
		this.hasOfflineMSG = hasOfflineMSG;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getCid() {
		return cid;
	}

	public Integer getUid() {
		return uid;
	}

	public RecentContactInfo(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}

	public RecentContactInfo( String name, String info,
			String time, int count) {
		this.name = name;
		this.info = info;
		this.time = time;
		this.count = count;
	}
	
	/**
	 * 获得头像的名称
	 * @return
	 */
	public String getImgPath() {
		return "HeadPic" + uid;
	}
	
	
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		if (category != null) {
			return category+ ";"  +name + ";"  + time+";"+count;
		} else
			return name + ";"  + time+";"+count;
	}
	
}

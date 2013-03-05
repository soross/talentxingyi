package org.talentware.android.global;

import java.util.HashMap;

import android.graphics.Bitmap;

public class Globe {
	/* SharedPreferences文件名，每个用户一个SharedPreferences文件 */
	public static String SP_FILE = "";
	/* 拉取头像的ip */
	public static String[] ips;
	/* 拉取头像的端口 */
	public static int[] ports;
//	/* 公司 */
//	public static CorpMaskItem corp;
//	/**
//	 * 自己
//	 */
//	public static EmployeeProfileItem myself;
	/* 公司logo文件名 */
	public static String corpLogo_file;
	/**
	 * 自己头像文件名字，头像名字的命名规范："HeadPic" + UId;
	 */
	public static String selfHeadPic_file;

	/* 自己的头像 */
	public static Bitmap bm_head = null;

	public static boolean is_notification = true;

	public static boolean is_shock = false;

	public static boolean is_sound = true;

	public static final String ACCOUNT_FILE = "account";

//	/**
//	 * 缓存员工详细信息
//	 */
//	public static final HashMap<Integer, EmployeeProfileItem> employeeProfileItems = new HashMap<Integer, EmployeeProfileItem>();
//	/**
//	 * 缓存公司详细信息
//	 */
//	public static final HashMap<Integer, CorpMaskItem> corpMaskItems = new HashMap<Integer, CorpMaskItem>();
	
	/**
	 * 控制什么时候连接到服务器
	 */
	public static boolean canConnect = false;
	
//	public static CustomList customList = new CustomList(100);
	
	/**
	 * 显示在屏幕上的Activity数量
	 */
	public static int showActivityCount = 0;

}

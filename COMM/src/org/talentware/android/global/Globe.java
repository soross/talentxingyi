package org.talentware.android.global;

import java.util.HashMap;

import android.graphics.Bitmap;

public class Globe {
	/* SharedPreferences�ļ�����ÿ���û�һ��SharedPreferences�ļ� */
	public static String SP_FILE = "";
	/* ��ȡͷ���ip */
	public static String[] ips;
	/* ��ȡͷ��Ķ˿� */
	public static int[] ports;
//	/* ��˾ */
//	public static CorpMaskItem corp;
//	/**
//	 * �Լ�
//	 */
//	public static EmployeeProfileItem myself;
	/* ��˾logo�ļ��� */
	public static String corpLogo_file;
	/**
	 * �Լ�ͷ���ļ����֣�ͷ�����ֵ������淶��"HeadPic" + UId;
	 */
	public static String selfHeadPic_file;

	/* �Լ���ͷ�� */
	public static Bitmap bm_head = null;

	public static boolean is_notification = true;

	public static boolean is_shock = false;

	public static boolean is_sound = true;

	public static final String ACCOUNT_FILE = "account";

//	/**
//	 * ����Ա����ϸ��Ϣ
//	 */
//	public static final HashMap<Integer, EmployeeProfileItem> employeeProfileItems = new HashMap<Integer, EmployeeProfileItem>();
//	/**
//	 * ���湫˾��ϸ��Ϣ
//	 */
//	public static final HashMap<Integer, CorpMaskItem> corpMaskItems = new HashMap<Integer, CorpMaskItem>();
	
	/**
	 * ����ʲôʱ�����ӵ�������
	 */
	public static boolean canConnect = false;
	
//	public static CustomList customList = new CustomList(100);
	
	/**
	 * ��ʾ����Ļ�ϵ�Activity����
	 */
	public static int showActivityCount = 0;

}

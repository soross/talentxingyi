package org.talentware.android.comm.util;

import android.util.Log;

/**
 * Log��־��ӡ������
 * 
 * @author CaixiaoLong
 * 
 */
public class LogFactory {

	private static final boolean RELEASE = false;

	/**
	 * ��ӡdebug ������־
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg) {
		if (!RELEASE) {
			Log.d(tag, msg);
		}
	}

	/**
	 * ��ӡerror ������־
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg) {
		if (!RELEASE) {
			Log.e(tag, msg);
		}
	}

	/**
	 * ��ӡv ������־
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg) {
		if (!RELEASE) {
			Log.v(tag, msg);
		}
	}

	/**
	 * ��ӡi ������־
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void i(String tag, String msg) {
		if (!RELEASE) {
			Log.i(tag, msg);
		}
	}

	/**
	 * ����View
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void view(String tag, String msg) {
		if (false) {
			Log.d(tag, msg);
		}
	}

}

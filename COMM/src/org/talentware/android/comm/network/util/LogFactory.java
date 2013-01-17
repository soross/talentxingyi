package org.talentware.android.comm.network.util;

import android.util.Log;

/**
 * Log日志打印工厂类
 * 
 * @author CaixiaoLong
 * 
 */
public class LogFactory {

	private static final boolean DEBUG = true;

	public static final String TAG = "IMO";

	/**
	 * 打印debug 调试日志
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	/**
	 * 打印error 调试日志
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}

	/**
	 * 打印v 调试日志
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}

	/**
	 * 测试View
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

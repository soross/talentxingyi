package org.talentware.android.comm.util;

import android.util.Log;

/**
 * Log日志打印工厂类
 * 
 * @author CaixiaoLong
 * 
 */
public class LogFactory {

	private static final boolean RELEASE = false;

	/**
	 * 打印debug 调试日志
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
	 * 打印error 调试日志
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
	 * 打印v 调试日志
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
	 * 打印i 调试日志
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

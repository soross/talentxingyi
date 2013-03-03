package com.imo.util;

import android.util.Log;

import com.imo.global.IMOApp;

/**
 * Log日志打印工厂类
 * 
 * @author CaixiaoLong
 *
 */
public class LogFactory {
	
	/**
	 * 打印debug 调试日志
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg){
			if (IMOApp.getApp().getAppMode()) {
				Log.d(tag, msg);
			}
	}
	
	/**
	 * 打印error 调试日志
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg){
		if (IMOApp.getApp().getAppMode()) {
			Log.e(tag, msg);
		}
	}
	
	/**
	 * 打印v 调试日志
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg){
		if (IMOApp.getApp().getAppMode()) {
			Log.v(tag, msg);
		}
	}
	
	/**
	 * 测试View
	 * @param tag
	 * @param msg
	 */
	public static void view(String tag, String msg){
		if (false) {
			Log.d(tag, msg);
		}
	}

}

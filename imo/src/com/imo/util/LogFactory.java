package com.imo.util;

import android.util.Log;

import com.imo.global.IMOApp;

/**
 * Log��־��ӡ������
 * 
 * @author CaixiaoLong
 *
 */
public class LogFactory {
	
	/**
	 * ��ӡdebug ������־
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg){
			if (IMOApp.getApp().getAppMode()) {
				Log.d(tag, msg);
			}
	}
	
	/**
	 * ��ӡerror ������־
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg){
		if (IMOApp.getApp().getAppMode()) {
			Log.e(tag, msg);
		}
	}
	
	/**
	 * ��ӡv ������־
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg){
		if (IMOApp.getApp().getAppMode()) {
			Log.v(tag, msg);
		}
	}
	
	/**
	 * ����View
	 * @param tag
	 * @param msg
	 */
	public static void view(String tag, String msg){
		if (false) {
			Log.d(tag, msg);
		}
	}

}

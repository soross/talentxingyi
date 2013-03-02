package com.imo.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;

import com.imo.global.IMOApp;

/**
 * 系统信息管理
 */
public class SystemInfoManager {

	private static final String TAG = SystemInfoManager.class.getSimpleName();

	/**
	 * 获得手机屏幕分辨率:放缩比例s
	 * 
	 * @param mActivity
	 * @return
	 */
	public double getScale(Activity mActivity) {
		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		LogFactory.d(TAG, "mobile display = " + width + " * " + height);
		LogFactory.d(TAG, "mobile density = " + density);

		if ((width == 480) && (height == 800)) {
			return 1;
		} else if ((width == 320) && (height == 480)) {
			return 0.67;
		} else if ((width == 240) && (height == 320)) {
			return 0.5;
		} else {
			return 1;
		}
	}

	/**
	 * 获得大版本号
	 * 
	 * @return
	 */
	public byte getVersion() {

		byte versionNO = 1;
		String version = "1";

		PackageManager packageManager = IMOApp.getApp().getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(IMOApp.getApp().getPackageName(), 0);
			version = packInfo.versionName;
			versionNO = Byte.parseByte(version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionNO;
	}

	/**
	 * 获得小版本Build
	 * 
	 * @return
	 */
	public short getBuild() {

		short build = 1000;
		PackageManager packageManager = IMOApp.getApp().getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(IMOApp.getApp().getPackageName(), 0);
			build = (short) packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return build;
	}

	// public byte getBuild(){
	//
	// byte code = 1;
	// PackageManager packageManager = IMOApp.getApp().getPackageManager();
	// PackageInfo packInfo;
	// try {
	// packInfo =
	// packageManager.getPackageInfo(IMOApp.getApp().getPackageName(),0);
	// code = (byte) packInfo.versionCode;
	// } catch (NameNotFoundException e) {
	// e.printStackTrace();
	// }
	// return code;
	// }

	public static void hideKeyBoard(Activity activity) {

		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		// InputMethodManager.HIDE_NOT_ALWAYS);
		imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		imm.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

}

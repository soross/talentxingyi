package org.talentware.android.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class Functions {

	private static final String TAG = "Functions";

	/** 获得正在运行的任务列表 */
	public static List<RunningTaskInfo> getRunningTasks(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return manager == null ? null : manager.getRunningTasks(Integer.MAX_VALUE);
	}

	/** 获得前台的任务 */
	public static RunningTaskInfo getCurrentRunningTask(Context context) {
		List<RunningTaskInfo> runningTask = getRunningTasks(context);
		return runningTask == null ? null : runningTask.get(0);
	}

	/** 当前任务是否是首页 */
	public static boolean isHome(Context context) {
		List<String> homes = getHomes(context);
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = null;
		try {
			rti = mActivityManager.getRunningTasks(1);
		} catch (SecurityException e) {
			// TODO: handle exception
			Logger.e(TAG, "SecurityException err:" + e.getMessage());
		}
		return homes.contains(rti.get(0).topActivity.getPackageName());
	}

	/** 获取所有首页列表 */
	public static List<String> getHomes(Context context) {
		List<String> packages = new ArrayList<String>();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfo != null) {
			for (ResolveInfo info : resolveInfo) {
				packages.add(info.activityInfo.packageName);
			}
		}
		return packages;
	}

	/** dp转px */
	public int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/** px转dp */
	public int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

}

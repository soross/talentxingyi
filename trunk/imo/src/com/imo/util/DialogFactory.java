package com.imo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.LayoutInflater;
import android.view.View;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.AppService;
import com.imo.global.IMOApp;
import com.imo.network.net.EngineConst;

/**
 * 对话框工厂类
 * 
 * @author CaixiaoLong
 * 
 */
public class DialogFactory {
	
	/**
	 * 生成进度条对话框
	 * 
	 * @return
	 */
	public static ProgressDialog progressDialog(Context context, String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		// 1. 设置进度条的样式。【STYLE_SPINNER，STYLE_HORIZONTAL】
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 2. 设置标题 和Icon
		// progressDialog.setTitle("");
		// progressDialog.setIcon(R.drawable.icon);
		// 3. 设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		// 4. 设置ProgressDialog 是否可以按退回按键取消
		progressDialog.setCancelable(true);
		progressDialog.setMessage(msg);
		return progressDialog;
	}

	/**
	 * 封转的动态显示按钮的1-3个
	 * 
	 * @param context
	 * @param title
	 * @param msg
	 * @param btnNames
	 * @param listeners
	 * @return
	 */
	public static AlertDialog alertDialog(Context context, String title,
			String msg, String[] btnNames,
			DialogInterface.OnClickListener... listeners) {

		DialogInterface.OnClickListener tempListener = null;

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		if (title != null) {
			builder.setTitle(title);
		}
		if (msg != null) {
			builder.setMessage(msg);
		}

		if (btnNames.length > 0) {
			if (listeners == null || listeners.length < 1) {
				builder.setPositiveButton(btnNames[0], null);
			} else {
				builder.setPositiveButton(btnNames[0], listeners[0]);
			}
		}
		if (btnNames.length > 1) {
			if (listeners == null || listeners.length < 2) {
				builder.setPositiveButton(btnNames[0], null);
			} else {
				builder.setNeutralButton(btnNames[1], listeners[1]);
			}
		}
		if (btnNames.length > 2) {
			if (listeners == null || listeners.length < 3) {
				builder.setPositiveButton(btnNames[0], null);
			} else {
				builder.setNegativeButton(btnNames[2], listeners[2]);
			}
		}

		return builder.create();
	}

	/**
	 * 网络连接失败 警告对话框
	 * 
	 * @param context
	 * @return
	 */
	public static AlertDialog netErrorDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("网络异常");
		builder.setMessage("网络连接异常，请设置网络或退出程序");
		// 跳转到系统的wifi设置界面
		builder.setPositiveButton("设置网络",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						((Activity) context)
								.startActivity(new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						dialog.cancel();
					}
				});
		builder.setNegativeButton("退出程序",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						IMOApp.getApp().setAppExit(true);
						Functions.backToDesk(IMOApp.getApp().mLastActivity);
						IMOApp.getApp().exitApp();
					}
				});
		return builder.create();
	}

	public static boolean serverPromptCanShow = true;
	/**
	 * 服务器维护对话框
	 * 
	 * @param context
	 * @return
	 */
	public static AlertDialog serverPromptDialog(final Context context) {
		if(!serverPromptCanShow)
			return null;
		serverPromptCanShow = !serverPromptCanShow;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("网络异常");
		builder.setMessage("服务器正在维护中，请稍后再试……");
		builder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EngineConst.isLoginSuccess = false;
						
						DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
						
						IMOApp.getApp().turn2LoginForLogout();
						dialog.cancel();
						serverPromptCanShow = !serverPromptCanShow;
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EngineConst.isLoginSuccess = false;
						
						DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
						
						IMOApp.getApp().turn2LoginForLogout();
						dialog.cancel();
						serverPromptCanShow = !serverPromptCanShow;
					}
				});
		return builder.create();
	}
	/**
	 * 退出程序对话框
	 * 
	 * @param context
	 */
	public static AlertDialog promptExit(final Context context) {
		LayoutInflater li = LayoutInflater.from(context);
		View exitV = li.inflate(R.layout.exitapp_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(exitV);
		builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
	IMOApp.getApp().setAppExit(true);
				Functions.backToDesk(IMOApp.getApp().mLastActivity);
				if (EngineConst.isNetworkValid
						&& AppService.getService().getTcpConnection()
								.isConnected()) {
					((AbsBaseActivityNetListener) IMOApp.getApp().mLastActivity)
							.requestLogOut(true);
				} else {
					IMOApp.getApp().exitApp();
				}
			}
		});
		builder.setNegativeButton("取消", null);
		return builder.create();
	}

	/**
	 * 电话选择对话框
	 */
	public static AlertDialog selectMobile(final Context context,
			String mobile, String tel, DialogInterface.OnClickListener listener) {
		return new AlertDialog.Builder(context)
				.setTitle("号码列表")
				.setItems(new String[] { "手机：" + Functions.formatPhone(mobile), "电话：" + tel },
						listener).setNegativeButton("取消", null).show();
	}
	/**
	 * 电话选择对话框
	 */
//	public static AlertDialog selectSingleMobile(final Context context,
//			boolean isMobile,String number ,DialogInterface.OnClickListener listener) {
//		String show = null;
//		if(isMobile){
//			show = "手机："+number;
//		}else {
//			show = "电话："+number;
//		}
//			
//		return new AlertDialog.Builder(context)
//		.setTitle("号码列表")
//		.setItems(new String[] { show},
//				listener).setNegativeButton("取消", null).show();
//	}
	
	
	/**
	 * 拨打电话对话框
	 * 
	 * @param context
	 */
	public static AlertDialog doCall(final Context context,final String tel) {
		LayoutInflater li = LayoutInflater.from(context);
		View exitV = li.inflate(R.layout.docall_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(exitV);
		builder.setPositiveButton("拨打", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ tel));
				context.startActivity(myIntentDial);
			}
		});
		builder.setNegativeButton("取消", null);
		return builder.create();
	}
	
	/**
	 * 复制对话框
	 */
	public static AlertDialog copyDialog(final Context context,
			DialogInterface.OnClickListener listener) {
		return new AlertDialog.Builder(context).setTitle("操作选项")
				.setItems(new String[] { "复制" }, listener).show();
	}
}

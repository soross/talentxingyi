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
 * �Ի��򹤳���
 * 
 * @author CaixiaoLong
 * 
 */
public class DialogFactory {
	
	/**
	 * ���ɽ������Ի���
	 * 
	 * @return
	 */
	public static ProgressDialog progressDialog(Context context, String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		// 1. ���ý���������ʽ����STYLE_SPINNER��STYLE_HORIZONTAL��
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 2. ���ñ��� ��Icon
		// progressDialog.setTitle("");
		// progressDialog.setIcon(R.drawable.icon);
		// 3. ����ProgressDialog �Ľ������Ƿ���ȷ
		progressDialog.setIndeterminate(false);
		// 4. ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
		progressDialog.setCancelable(true);
		progressDialog.setMessage(msg);
		return progressDialog;
	}

	/**
	 * ��ת�Ķ�̬��ʾ��ť��1-3��
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
	 * ��������ʧ�� ����Ի���
	 * 
	 * @param context
	 * @return
	 */
	public static AlertDialog netErrorDialog(final Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("�����쳣");
		builder.setMessage("���������쳣��������������˳�����");
		// ��ת��ϵͳ��wifi���ý���
		builder.setPositiveButton("��������",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						((Activity) context)
								.startActivity(new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS));
						dialog.cancel();
					}
				});
		builder.setNegativeButton("�˳�����",
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
	 * ������ά���Ի���
	 * 
	 * @param context
	 * @return
	 */
	public static AlertDialog serverPromptDialog(final Context context) {
		if(!serverPromptCanShow)
			return null;
		serverPromptCanShow = !serverPromptCanShow;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("�����쳣");
		builder.setMessage("����������ά���У����Ժ����ԡ���");
		builder.setPositiveButton("ȷ��",
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
		builder.setNegativeButton("ȡ��",
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
	 * �˳�����Ի���
	 * 
	 * @param context
	 */
	public static AlertDialog promptExit(final Context context) {
		LayoutInflater li = LayoutInflater.from(context);
		View exitV = li.inflate(R.layout.exitapp_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(exitV);
		builder.setPositiveButton("�˳�", new DialogInterface.OnClickListener() {
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
		builder.setNegativeButton("ȡ��", null);
		return builder.create();
	}

	/**
	 * �绰ѡ��Ի���
	 */
	public static AlertDialog selectMobile(final Context context,
			String mobile, String tel, DialogInterface.OnClickListener listener) {
		return new AlertDialog.Builder(context)
				.setTitle("�����б�")
				.setItems(new String[] { "�ֻ���" + Functions.formatPhone(mobile), "�绰��" + tel },
						listener).setNegativeButton("ȡ��", null).show();
	}
	/**
	 * �绰ѡ��Ի���
	 */
//	public static AlertDialog selectSingleMobile(final Context context,
//			boolean isMobile,String number ,DialogInterface.OnClickListener listener) {
//		String show = null;
//		if(isMobile){
//			show = "�ֻ���"+number;
//		}else {
//			show = "�绰��"+number;
//		}
//			
//		return new AlertDialog.Builder(context)
//		.setTitle("�����б�")
//		.setItems(new String[] { show},
//				listener).setNegativeButton("ȡ��", null).show();
//	}
	
	
	/**
	 * ����绰�Ի���
	 * 
	 * @param context
	 */
	public static AlertDialog doCall(final Context context,final String tel) {
		LayoutInflater li = LayoutInflater.from(context);
		View exitV = li.inflate(R.layout.docall_dialog, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(exitV);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ tel));
				context.startActivity(myIntentDial);
			}
		});
		builder.setNegativeButton("ȡ��", null);
		return builder.create();
	}
	
	/**
	 * ���ƶԻ���
	 */
	public static AlertDialog copyDialog(final Context context,
			DialogInterface.OnClickListener listener) {
		return new AlertDialog.Builder(context).setTitle("����ѡ��")
				.setItems(new String[] { "����" }, listener).show();
	}
}

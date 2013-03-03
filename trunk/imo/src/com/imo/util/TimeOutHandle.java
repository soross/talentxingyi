package com.imo.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.widget.Toast;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.dataengine.DataEngine;
import com.imo.dataengine.DataEngine.LOGICSTATUS;
import com.imo.global.AppService;
import com.imo.global.IMOApp;
import com.imo.module.config.WorkSignActivity;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.FirstLoadingActivity;
import com.imo.module.organize.NormalLoadingActivity;
import com.imo.network.net.EngineConst;

/**
 * 超时处理对话框
 * 
 * 逻辑调整: 每一个界面只存在一个超时对话框
 * 
 * @author CaixiaoLong
 * 
 */
public class TimeOutHandle {

//	private static TimeOutHandle instance = null;
	
	public static boolean hasShow = false;

	private Dialog mTimeOutDialog;

	public TimeOutHandle() {

	}

//	public static synchronized TimeOutHandle getInstance() {
//		if (instance == null) {
//			instance = new TimeOutHandle();
//		}
//		return instance;
//	}

	private void initDialog(final Context mContext) {
		
		mTimeOutDialog = DialogFactory.alertDialog(mContext,
				mContext.getString(R.string.warn),
				mContext.getString(R.string.notifypacket_timeout),
				new String[] { IMOApp.getApp().getString(R.string.ok) },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (IMOApp.getApp().mLastActivity instanceof LoginActivity) {
							return;
						}else{
							EngineConst.isLoginSuccess = false;
							
							DataEngine.getInstance().setLogicStatus(LOGICSTATUS.DISCONNECTED);
							
							IMOApp.getApp().turn2LoginForLogout();
						}
						
//						if (IMOApp.getApp().mLastActivity !=null) {
//								if (IMOApp.getApp().mLastActivity instanceof LoginActivity) {
//									AppService.getService().reset();
//									LoginActivity.launch(IMOApp.getApp().mLastActivity);
//								}else if(IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity) {
//									EngineConst.isLoginSuccess = false;
//									IMOApp.getApp().turn2LoginForLogout();
//								} else if(IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity) {
//									EngineConst.isLoginSuccess = false;
//									IMOApp.getApp().turn2LoginForLogout();
////								} else if(IMOApp.getApp().mLastActivity instanceof WorkSignActivity) {
////									WorkSignActivity.getActivity().dismissDialog();
//								}
//						}
						
					}
				});
		
		mTimeOutDialog.setCancelable(false);
		
//		if(IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity
//				|| IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity) {
//			mTimeOutDialog.setCancelable(false);
//		}
		
		mTimeOutDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				((AbsBaseActivityNetListener)mContext).hasShowTimeOutDialog = false;
			}
		});
	}
	
	
	/**
	 * 显示超时对话框
	 * 
	 * @param mContext
	 */
	public void showDialog(Context mContext){
		
/*		///重登陆超时，直接跳到登陆界面
		if (IMOApp.getApp().reLoginTimeOut) {
			////再次打开失败接受大门
			IMOApp.getApp().hasPackageFailed = false;
			
			if (!(IMOApp.getApp().mLastActivity instanceof LoginActivity)) {
				Toast.makeText(mContext, "网络异常，请您重新登录。", Toast.LENGTH_SHORT).show();
				EngineConst.isLoginSuccess = false;
				IMOApp.getApp().turn2LoginForLogout();
			}
			return;
		}*/
		
//		else{
			///非重登陆失败，需要弹出对话框
			if (!((AbsBaseActivityNetListener) mContext).hasShowTimeOutDialog) {
				((AbsBaseActivityNetListener) mContext).hasShowTimeOutDialog = true;
//				if (IMOApp.getApp().mLastActivity instanceof WorkSignActivity) {
//					WorkSignActivity.getActivity().dismissDialog();
//					Toast.makeText(mContext,mContext.getString(R.string.notifypacket_timeout), Toast.LENGTH_SHORT).show();
//				} else 
				if (IMOApp.getApp().mLastActivity instanceof LoginActivity) {
//     			   ((LoginActivity)mContext).stopLogin();
					if (LoginActivity.getActivity() !=null) {
						LoginActivity.getActivity().stopLogin();
					}
				    initDialog(mContext);
					mTimeOutDialog.show();
				} else if (IMOApp.getApp().mLastActivity instanceof FirstLoadingActivity) {
					initDialog(mContext);
					mTimeOutDialog.show();
				} else if (IMOApp.getApp().mLastActivity instanceof NormalLoadingActivity) {
					initDialog(mContext);
					mTimeOutDialog.show();
				} else {
					Toast.makeText(mContext,mContext.getString(R.string.notifypacket_timeout), Toast.LENGTH_SHORT).show();
				}
//			}
		}
		
		
		
//		if (IMOApp.getApp().hasRunInBackground)
//			return;
		
//		if (!hasShow) {
//			if (mTimeOutDialog != null && !mTimeOutDialog.isShowing()) {
//				TimeOutHandle.hasShow = true;
//				mTimeOutDialog.show();
//			}
//		}
		
	}
	
	/**
	 * 取消显示超时对话框
	 */
	public void dismissDialog(){
		if (mTimeOutDialog != null && mTimeOutDialog.isShowing()) {
			mTimeOutDialog.dismiss();
		}
	}

}

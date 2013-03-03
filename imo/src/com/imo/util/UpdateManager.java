package com.imo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.module.config.SystemSetActivity;
import com.imo.module.login.LoginActivity;
import com.imo.module.welcome.WelcomeActivity;

public class UpdateManager {
	
	private static final String TAG = "UpdateManager";
	
	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	
//	/* 保存解析的XML信息 */
//	HashMap<String, String> mHashMap;
	
	/* 下载保存路径 */
	private String mSavePath;
	/* 记录进度条数量 */
	private int progress;
	/** 是否取消更新 */
	private boolean isCancelUpdate = false;

	private Context mContext;
	/* 更新进度条 */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;

	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 正在下载
			case DOWNLOAD:
				// 设置进度条位置
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// 安装文件
				installApk();
				break;
			default:
				break;
			}
		};
	};
	
	private String downloadURL = "";

	public UpdateManager(Context context,String downloadURL) {
		this.mContext = context;
		this.downloadURL = downloadURL;
	}

	// /**
	// * 检测软件更新
	// */
	// public void checkUpdate()
	// {
	// if (isUpdate())
	// {
	// // 显示提示对话框
	// showNoticeDialog();
	// } else
	// {
	// Toast.makeText(mContext, R.string.soft_update_no,
	// Toast.LENGTH_LONG).show();
	// }
	// }

	/**
	 * 显示软件更新对话框
	 */
	public void showNoticeDialog() {
		
		LogFactory.d(TAG, "-------------------->showNoticeDialog");
		LogFactory.d(TAG, "-------------------->downloadURL="+ downloadURL);
		
		// 构造对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
		
		// 更新
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 显示下载对话框
						showDownloadDialog();
					}
				});
		if (IMOApp.getApp().mLastActivity instanceof SystemSetActivity) {
			// 稍后更新
			builder.setNegativeButton(R.string.soft_update_later,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}else{
			// 放弃更新
			builder.setNegativeButton(R.string.soft_update_cancel,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							IMOApp.getApp().setAppExit(true);
							Functions.backToDesk(IMOApp.getApp().mLastActivity);
							IMOApp.getApp().exitApp();
						}
					});
		}
		
		Dialog noticeDialog = builder.create();
		noticeDialog.setCancelable(false);
		noticeDialog.show();
		
		noticeDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mContext instanceof SystemSetActivity) {
					SystemSetActivity.hasRequested = false;
				}
			}
		});
	}

	/**
	 * 显示软件下载对话框
	 */
	private void showDownloadDialog() {
		
		if (!sdCardCheck()) {
			DialogFactory.alertDialog(mContext,
					mContext.getString(R.string.warn),
					mContext.getString(R.string.no_sdcard_tip),
					new String[] { "确定" }, null).show();
			return;
		};
		
		// 构造软件下载对话框
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// 给下载对话框增加进度条
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// 取消更新
		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置取消状态
						isCancelUpdate = true;
						
						if (mContext instanceof WelcomeActivity) {
							IMOApp.getApp().turn2LoginForLogout();
						}
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.setCancelable(false);
		mDownloadDialog.show();

		downloadApk();
	}

	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		// 启动新线程下载软件
		new DownloadApkThread().start();
	}
	
	private boolean sdCardCheck(){
		boolean hasSDCard = false;
		// 判断SD卡是否存在，并且是否具有读写权限
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			hasSDCard = true;
		}
		return hasSDCard;
	}

	/**
	 * 下载文件线程
	 */
	private class DownloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// 判断SD卡是否存在，并且是否具有读写权限
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// 获得存储卡的路径
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(downloadURL);
					// 创建连接
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// 获取文件大小
					int length = conn.getContentLength();
					// 创建输入流
					InputStream inStream = conn.getInputStream();

					File file = new File(mSavePath);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, APKNAME);
					
					if (apkFile.exists()) {
						apkFile.delete();
					}
					
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// 缓存
					byte buf[] = new byte[1024];
					// 写入到文件中
					do {
						int readSize = inStream.read(buf);
						count += readSize;
						// 计算进度条位置
						progress = (int) (((float) count / length) * 100);
						// 更新进度
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (readSize <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// 写入文件
						fos.write(buf, 0, readSize);
					} while (!isCancelUpdate);// 点击取消就停止下载.
					fos.close();
					inStream.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 取消下载对话框显示
			mDownloadDialog.dismiss();
		}
	};

	
	private String APKNAME = "imo.apk";
	/**
	 * 安装APK文件
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, APKNAME);
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}

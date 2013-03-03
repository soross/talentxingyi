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
	
	/* ������ */
	private static final int DOWNLOAD = 1;
	/* ���ؽ��� */
	private static final int DOWNLOAD_FINISH = 2;
	
//	/* ���������XML��Ϣ */
//	HashMap<String, String> mHashMap;
	
	/* ���ر���·�� */
	private String mSavePath;
	/* ��¼���������� */
	private int progress;
	/** �Ƿ�ȡ������ */
	private boolean isCancelUpdate = false;

	private Context mContext;
	/* ���½����� */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;

	private Handler mHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ��������
			case DOWNLOAD:
				// ���ý�����λ��
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
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
	// * ����������
	// */
	// public void checkUpdate()
	// {
	// if (isUpdate())
	// {
	// // ��ʾ��ʾ�Ի���
	// showNoticeDialog();
	// } else
	// {
	// Toast.makeText(mContext, R.string.soft_update_no,
	// Toast.LENGTH_LONG).show();
	// }
	// }

	/**
	 * ��ʾ������¶Ի���
	 */
	public void showNoticeDialog() {
		
		LogFactory.d(TAG, "-------------------->showNoticeDialog");
		LogFactory.d(TAG, "-------------------->downloadURL="+ downloadURL);
		
		// ����Ի���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
		
		// ����
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// ��ʾ���ضԻ���
						showDownloadDialog();
					}
				});
		if (IMOApp.getApp().mLastActivity instanceof SystemSetActivity) {
			// �Ժ����
			builder.setNegativeButton(R.string.soft_update_later,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}else{
			// ��������
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
	 * ��ʾ������ضԻ���
	 */
	private void showDownloadDialog() {
		
		if (!sdCardCheck()) {
			DialogFactory.alertDialog(mContext,
					mContext.getString(R.string.warn),
					mContext.getString(R.string.no_sdcard_tip),
					new String[] { "ȷ��" }, null).show();
			return;
		};
		
		// ����������ضԻ���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// ȡ������
		builder.setNegativeButton(R.string.soft_update_cancel,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// ����ȡ��״̬
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
	 * ����apk�ļ�
	 */
	private void downloadApk() {
		// �������߳��������
		new DownloadApkThread().start();
	}
	
	private boolean sdCardCheck(){
		boolean hasSDCard = false;
		// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			hasSDCard = true;
		}
		return hasSDCard;
	}

	/**
	 * �����ļ��߳�
	 */
	private class DownloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory()
							+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(downloadURL);
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream inStream = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, APKNAME);
					
					if (apkFile.exists()) {
						apkFile.delete();
					}
					
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int readSize = inStream.read(buf);
						count += readSize;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (readSize <= 0) {
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, readSize);
					} while (!isCancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					inStream.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	};

	
	private String APKNAME = "imo.apk";
	/**
	 * ��װAPK�ļ�
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, APKNAME);
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}

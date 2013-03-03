package com.imo.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.imo.R;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.organize.struct.Node;
import com.imo.module.welcome.WelcomeActivity;
import com.imo.util.LogFactory;
import com.imo.util.NoticeManager;
import com.imo.view.BottomBar;
import com.imo.view.TitleBar;

/**
 * ������ͨ��Activity��������࣬ʵ�ֽ���ĸ���,��װ�����߼���
 * 
 * @author CaixiaoLong
 * 
 */
public abstract class AbsBaseActivity extends Activity {

	protected IMOApp mGlobal = IMOApp.getApp();

	protected Context mContext;

	protected TitleBar mTitleBar;

	protected BottomBar mBottomBar;

//	protected Handler mParentHandler;

//	protected Message msg;

	protected Resources resources;

	protected Notification notice;;

	protected String mNoticeTitle;

	protected String mNoticeContent;

	protected NotificationManager notificationManager;

	private LinearLayout baseLayout;

	protected String[] bottomBarName = { "�Ի�", "��֯�ṹ", "��ϵ��", "Ⱥ��" };

	protected int[] bottomBarImgResIdSelected = {
			R.drawable.menu_dialogue_selected,
			R.drawable.menu_organize_selected,
			R.drawable.menu_contact_selected, R.drawable.menu_group_selected };

	protected int[] bottomBarImgResId = { R.drawable.menu_dialogue,
			R.drawable.menu_organize, R.drawable.menu_contact,
			R.drawable.menu_group };

	@Override
	protected void onStart() {
		super.onStart();
		Globe.showActivityCount++;
		LogFactory.d("��С��", "onStart------------------>");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!(this instanceof WelcomeActivity))
			if (IMOApp.getApp().reStartProgram(this)) {
				return;
			}

		if (needObserver()) {
			mGlobal.addAbsBaseActivity(this);
		}

		initBaseData();

//		handlerMessage();

		installViews();

		registerEvents();

	}

	/**
	 * ��ʼ������ Empty Method
	 */
	protected void initConnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentView(int id) {
		LayoutInflater inflater = LayoutInflater.from(AbsBaseActivity.this);

		inflater.inflate(id, baseLayout);
	}

	private void initBaseData() {
		mContext = this;
		resources = getResources();
		mNoticeTitle = Globe.myself == null ? "δ��¼" : Globe.myself.getName();
		mNoticeContent = resources.getString(R.string.online);
		notificationManager = mGlobal.getNoticeManager();

		super.setContentView(R.layout.base_activity);
		mTitleBar = (TitleBar) findViewById(R.id.titlebar);
		mBottomBar = (BottomBar) findViewById(R.id.bottombar);
		baseLayout = (LinearLayout) findViewById(R.id.content);
		registerBottomBarEvent();
	}

	/**
	 * �Ƿ���Activity�ָ���Ϣ
	 * 
	 * @return
	 */
	protected boolean needSendRecoverNotice() {
		return true;
	}

	private void registerBottomBarEvent() {

		// /init bottombar
		mBottomBar.setBottomBar(bottomBarImgResId, bottomBarImgResIdSelected,
				new int[4]);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// if(needSendRecoverNotice()){
		// NoticeManager.updateRecoverAppNotice(notificationManager);
		// }
	}

	@Override
	protected void onStop() {
		super.onStop();
		Globe.showActivityCount--;
		LogFactory.d("��С��", "onStop------------------>");
		if (Globe.showActivityCount == 0&&!mGlobal.isAppExit()) {
			if (needSendRecoverNotice()) {
				mGlobal.hasRunInBackground = true;// //��ʶδ��̨����
				NoticeManager.updateRecoverAppNotice(notificationManager);
			}
		}
	}

	/**
	 * @param gender
	 * @return
	 */
	protected boolean isBoy(int gender) {

		return gender == 0 ? false : true;
	}

	protected boolean mThisUidIsBoy(int uid) {
		Node userNode = IMOApp.getApp().mNodeMap.get(uid);
		boolean isBoy = true;
		if (userNode != null) {
			isBoy = userNode.getNodeData().isBoy;
		} else {
			LogFactory.e("sex", "sex error, user default boy");
		}
		return isBoy;
	}

	@Override
	protected void onResume() {
		mGlobal.mLastActivity = this;

		mGlobal.hasRunInBackground = false;
		super.onResume();

		mGlobal.clearAllNotice();
	}

//	protected void handlerMessage() {
//		mParentHandler = new Handler() {
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//
//				refresh(msg);
//			}
//
//		};
//
////		msg = new Message();
//	}

//	protected void sendMessage(Object obj) {
//		msg = new Message();
//		msg.obj = obj;
//		mParentHandler.sendMessage(msg);
//	}

	protected boolean needObserver() {
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (needObserver()) {
			IMOApp.getApp().removeAbsBaseActivity(this);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	//
	// if (keyCode == KeyEvent.KEYCODE_MENU) {
	//
	// }
	//
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	//
	// }
	//
	// if (keyCode == KeyEvent.KEYCODE_SEARCH) {
	//
	// }
	//
	// return super.onKeyDown(keyCode, event);
	// }

	protected abstract void installViews();

	protected abstract void registerEvents();

	public abstract void refresh(Object param);

}

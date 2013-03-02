package com.imo.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;

import com.imo.R;
import com.imo.db.entity.User;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.MainActivityGroup;
import com.imo.module.contact.ContactActivity;
import com.imo.module.dialogue.DialogueActivity;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.module.group.GroupActivity;
import com.imo.module.organize.OrganizeActivity;

/**
 * 通知管理类
 */
public class NoticeManager {

	// //标识APP正在运行
	public static int TYPE_NOTICE_APP_ONGOING = R.layout.main_activity;
	public static int TYPE_NOTICE_NEW_NEWS = R.layout.dialogue_activity;

	// /// has not read msg count
	public static int count = 0;

	/**
	 * Recover to last activity.
	 * 
	 * @param notificationManager
	 */
	public static void updateRecoverAppNotice(NotificationManager notificationManager) {
		notificationManager.cancel(TYPE_NOTICE_NEW_NEWS);
		String className = "com.imo.module.welcome.WelcomeActivity";

		MainActivityGroup activityGroup = MainActivityGroup.getActivityGroup();

		int index = -1;

		if (activityGroup != null) {
			if (IMOApp.getApp().mLastActivity instanceof RecentContactActivity) {
				index = 0;
			} else if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
				index = 1;
			} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
				index = 2;
			} else if (IMOApp.getApp().mLastActivity instanceof GroupActivity) {
				index = 3;
			} else {
				index = -1;
			}
		} else {
			index = -1;
		}

		if (index != -1) {
			className = activityGroup.getClass().getName();
		} else {
			className = IMOApp.getApp().mLastActivity.getClass().getName();
		}

		Intent intent = new Intent();
		intent.putExtra("index", index);// //传递Index

		intent.setClassName(IMOApp.getApp(), className);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(IMOApp.getApp(), 0, intent, 0);// Intent.FLAG_ACTIVITY_NEW_TASK

		Notification notice = new Notification(R.drawable.icon, IMOApp.getApp().getResources().getString(R.string.noticebar_on_going), System.currentTimeMillis());
		String hasNotLogin = IMOApp.getApp().getString(R.string.has_not_login);

		String content = IMOApp.getApp().getString(R.string.online);

		String loginName = Globe.myself == null ? hasNotLogin : Globe.myself.getName();
		String title = "手机i'm office" + " (" + loginName + ")";
		// if (loginName.equals(hasNotLogin)) {
		content = "";
		// }

		notice.setLatestEventInfo(IMOApp.getApp(), title, content, pendingIntent);

		notice.flags |= Notification.FLAG_ONGOING_EVENT;
		notice.flags |= Notification.FLAG_NO_CLEAR;

		notificationManager.notify(TYPE_NOTICE_APP_ONGOING, notice);
	}

	/**
	 * 更新新消息的通知
	 * 
	 * @param notificationManager
	 * @param notice
	 * @param msgTitle
	 * @param content
	 *        最后一条消息的内容
	 * 
	 * @param cid
	 * @param uid
	 * @param fromUserName
	 * @param isBoy
	 */
	public static void updateNewsNotice(NotificationManager notificationManager, Notification notice, String msgTitle, CharSequence content, int cid, int uid, String fromUserName) {
		notificationManager.cancel(TYPE_NOTICE_APP_ONGOING);

		LogFactory.d("Notice", "Notice: cid =" + cid + "   uid =" + uid + "  userName = " + fromUserName);
		Resources res = IMOApp.getApp().getResources();
		Intent intent = new Intent();
		// String className = "com.imo.module.dialogue.DialogueActivity";
		intent.putExtra("from_notice", true);// //if from notice,need reset
												// count.

		intent.putExtra("cid", cid);
		intent.putExtra("uid", uid);
		intent.putExtra("name", fromUserName);

		boolean isBoy = false;
		try {
			User user = IMOApp.imoStorage.getUser(uid);
			isBoy = user.getGender() == 0 ? false : true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		intent.putExtra("sex", isBoy);

		intent.setClass(IMOApp.getApp(), DialogueActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(IMOApp.getApp(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		// if (count != 0) {
		// msgTitle += " "+count+"条未读消息";
		// }

		// notice = new Notification(R.drawable.icon,content,
		// System.currentTimeMillis());

		String title = ""; // res.getString(R.string.notice_receive_news);
		title += count;
		title += res.getString(R.string.notice_new_news);

		content = fromUserName + ":" + content;

		notice = new Notification(R.drawable.icon, content, System.currentTimeMillis());

		notice.setLatestEventInfo(IMOApp.getApp(), title, content, pendingIntent);

		notice.flags |= Notification.FLAG_NO_CLEAR;
		notice.flags |= Notification.FLAG_AUTO_CANCEL;

		// if (Globe.is_shock) {
		// notice.defaults = Notification.DEFAULT_VIBRATE;
		// }
		// if (Globe.is_sound) {
		// Functions.ring();
		// }

		notificationManager.notify(TYPE_NOTICE_NEW_NEWS, notice);
	}

}

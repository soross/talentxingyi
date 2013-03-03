package com.imo.module.organize;

import android.os.Message;

import com.imo.global.IMOApp;
import com.imo.module.contact.ContactActivity;
import com.imo.module.dialogue.DialogueActivity;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.util.LogFactory;

/**
 * ״̬����UI���´�����
 */
public class StateHandle {

	private static String TAG = "StateHandle";

	private static StateHandle instance = null;

	private StateHandle() {
	}

	public static StateHandle getInstance() {
		if (instance == null) {
			instance = new StateHandle();
		}
		return instance;
	}

	/**
	 * ���½���
	 * 
	 * @param uid
	 * @param status
	 */
	public void updateUI(Integer uid, Integer status) {

		LogFactory.d("pull-state", "uid = " + uid + " status =" + status);

		if (IMOApp.getApp().userStateMap.get(uid) != null) {
			IMOApp.getApp().userStateMap.put(uid, status);
		} else {
			IMOApp.getApp().outerUserStateMap.put(uid, status);
		}

		/**
		 * �Ǻ�̨���е�ʱ�����
		 */
		if (!IMOApp.getApp().hasRunInBackground) {

			if (IMOApp.getApp().mLastActivity instanceof RecentContactActivity) {
				RecentContactActivity.getActivity().mContactHandle.sendEmptyMessage(RecentContactActivity.TYPE_UPDATESTATEBYPULLSTATE);
			} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
				ContactActivity.getActivity().mContactHandler.sendEmptyMessage(ContactActivity.TYPE_UPDATE_STATE);
			} else if (IMOApp.getApp().mLastActivity instanceof DialogueActivity) {
				Message msg = new Message();
				msg.what = DialogueActivity.TYPE_UPDATEFACE;
				msg.arg1 = uid;
				msg.obj = isOnlineForDialogue(status);
				DialogueActivity.instance.mFaceHandler.sendMessage(msg);
			} else if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
				OrganizeActivity.getActivity().handlerTree.sendEmptyMessage(OrganizeActivity.TYPE_UPDATEPULLSTATE);
			}
		}
	}

	/**
	 * �������е�״̬
	 */
	public void updateAllState() {

		/**
		 * �Ǻ�̨���е�ʱ�����
		 */
		if (!IMOApp.getApp().hasRunInBackground) {

			if (IMOApp.getApp().mLastActivity instanceof RecentContactActivity) {
				RecentContactActivity.getActivity().mContactHandle.sendEmptyMessage(RecentContactActivity.TYPE_UPDATESTATEBYPULLSTATE);
			} else if (IMOApp.getApp().mLastActivity instanceof ContactActivity) {
				ContactActivity.getActivity().mContactHandler.sendEmptyMessage(ContactActivity.TYPE_UPDATE_STATE);
			} else if (IMOApp.getApp().mLastActivity instanceof DialogueActivity) {
				int uid = DialogueActivity.instance.aboutUid;
				boolean isOnline = IMOApp.getApp().isOnlineFindByUid(uid);
				Message msg = new Message();
				msg.what = DialogueActivity.TYPE_UPDATEFACE;
				msg.arg1 = uid;
				msg.obj = isOnline;
				System.out.println("֪ͨ�������״̬��" + isOnline);
				DialogueActivity.instance.mFaceHandler.sendMessage(msg);
			} else if (IMOApp.getApp().mLastActivity instanceof OrganizeActivity) {
				OrganizeActivity.getActivity().handlerTree.sendEmptyMessage(OrganizeActivity.TYPE_UPDATEPULLSTATE);
			}
		}
	}

	private boolean isOnlineForDialogue(int status) {
		int state = status & 0x000000FF;
		return (state != 0);
	}
}

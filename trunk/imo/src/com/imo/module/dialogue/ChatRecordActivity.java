package com.imo.module.dialogue;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.db.entity.MessageInfo;
import com.imo.global.IMOApp;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.module.organize.struct.Node;
import com.imo.util.DialogFactory;
import com.imo.util.IOUtil;
import com.imo.util.LogFactory;
import com.imo.util.MessageDataFilter;

public class ChatRecordActivity extends AbsBaseActivityNetListener {
	private final String TAG = "ChatRecordActivity";
	private Dialog dialog;
	private ImageView chat_first;
	private ImageView chat_back;
	private ImageView chat_next;
	private ImageView chat_last;
	private ImageView chat_delete;
	private ListView chat_record_List;
	private DialogueListAdapter dialogueListAdapter;
	private List<Map<String, Object>> message_list = new ArrayList<Map<String, Object>>();

	private int sum = 0;
	private int currentIndex = 0;
	private final int capacity = 10;

	private int aboutUid;
	private String aboutName;
	/**
	 * 男性为true，女性为false
	 */
	private boolean aboutSex;
	private Bitmap about_head;

	@Override
	protected void installViews() {
		setContentView(R.layout.chat_record_activity);
		getInitData();
		loadHeadPics();
		chat_first = (ImageView) findViewById(R.id.iv_chat_first);
		chat_back = (ImageView) findViewById(R.id.iv_chat_back);
		chat_next = (ImageView) findViewById(R.id.iv_chat_next);
		chat_last = (ImageView) findViewById(R.id.iv_chat_last);
		chat_delete = (ImageView) findViewById(R.id.iv_chat_delete);
		chat_record_List = (ListView) findViewById(R.id.lv_chat_record_List);
		dialogueListAdapter = new DialogueListAdapter(this, message_list,
				R.layout.dialogue_list_iteml, null, null);
		chat_record_List.setAdapter(dialogueListAdapter);
		try {
			sum = IMOApp.imoStorage.getMessageSum(aboutUid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (sum == 0) {
			chat_first.setEnabled(false);
			chat_back.setEnabled(false);
			chat_next.setEnabled(false);
			chat_last.setEnabled(false);
			chat_delete.setEnabled(false);
			return;
		}
		if (sum <= capacity) {
			chat_first.setEnabled(false);
			chat_back.setEnabled(false);
			chat_next.setEnabled(false);
			chat_last.setEnabled(false);
		} else {
			chat_first.setEnabled(true);
			chat_back.setEnabled(true);
			chat_next.setEnabled(false);
			chat_last.setEnabled(false);
			int pages = sum / capacity;
			currentIndex = pages * capacity;
			if (currentIndex == sum)
				currentIndex = currentIndex - capacity;
		}
		IMOApp.imoStorage.updateMessage(aboutUid);
		getMessage(aboutUid, currentIndex, capacity);

		LogFactory.d(TAG, "sum:" + sum + "  currentIndex:" + currentIndex
				+ "  CurrentPage:" + getCurrentPage());

	}

	private int getSumPages() {
		if (sum == 0)
			return 1;
		if (sum % capacity == 0) {
			return sum / capacity;
		} else {
			return sum / capacity + 1;
		}
	}

	private int getCurrentPage() {
		if (sum == 0)
			return 1;
		if (currentIndex == sum)
			currentIndex = currentIndex - capacity;
		return currentIndex / capacity + 1;
	}

	private void getInitData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		aboutUid = bundle.getInt("aboutUid");
		aboutName = bundle.getString("aboutName");
		// aboutSex = bundle.getBoolean("aboutSex");
		Node userNode = IMOApp.getApp().mNodeMap.get(aboutUid);

		if (userNode != null) {
			aboutSex = userNode.getNodeData().isBoy;
		} else {
			LogFactory.e("sex", "sex error, user default boy");
		}
	}

	private void loadHeadPics() {
		try {
			// 加载聊天对象的头像
			byte[] b_about_head = null;
			try {
				b_about_head = IOUtil.readFile("HeadPic" + aboutUid, this);
			} catch (Exception e) {
			}
			if (b_about_head != null && b_about_head.length > 0) {
				// Bitmap bitmap = BitmapFactory.decodeByteArray(b_about_head,
				// 0,
				// b_about_head.length);
				// about_head = new BitmapDrawable(bitmap);
				about_head = BitmapFactory.decodeByteArray(b_about_head, 0,
						b_about_head.length);
				return;
			}
			if (aboutSex) {
				// about_head = getResources().getDrawable(
				// R.drawable.imo_default_face_boy);
				about_head = BitmapFactory.decodeResource(getResources(),
						R.drawable.imo_default_face_boy);
			} else {
				// about_head = getResources().getDrawable(
				// R.drawable.imo_default_face_girl);
				about_head = BitmapFactory.decodeResource(getResources(),
						R.drawable.imo_default_face_girl);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getMessage(int uId, int index, int count) {
		message_list.clear();
		ArrayList<MessageInfo> messageInfos = null;
		Map<String, Object> map = null;
		CharSequence show_message = null;
		try {
			messageInfos = IMOApp.imoStorage.getMessage(uId, index, count);
			for (MessageInfo messageInfo : messageInfos) {
				show_message = MessageDataFilter
						.jsonToCharSequence(new JSONObject(messageInfo
								.getText()));
				map = new HashMap<String, Object>();
				map.put("time", messageInfo.getTime());
				map.put("date", messageInfo.getDate());
				map.put("msg", show_message);
				if (messageInfo.getType() == MessageInfo.MessageInfo_From) {
					map.put("who", "from");
					map.put("head", about_head);
				} else {
					map.put("who", "to");
				}
				if (messageInfo.getIsFailed() == MessageInfo.MessageInfo_Failed)
					map.put("fail", "fail");
				message_list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dialogueListAdapter.notifyDataSetChanged();
		chat_record_List.setSelection(message_list.size());

	}

	@Override
	protected void registerEvents() {

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back),
				aboutName, null);

		mTitleBar.setLeftBtnListene(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mTitleBar.updateCenterInfo("记录（" + getCurrentPage() + "/"
				+ getSumPages() + "）");

		chat_first.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				currentIndex = 0;
				getMessage(aboutUid, currentIndex, capacity);
				mTitleBar.updateCenterInfo("记录（" + getCurrentPage() + "/"
						+ getSumPages() + "）");
				chat_back.setEnabled(false);
				chat_first.setEnabled(false);
				chat_last.setEnabled(true);
				chat_next.setEnabled(true);
				LogFactory.d(TAG, "sum:" + sum + "  currentIndex:"
						+ currentIndex + "  CurrentPage:" + getCurrentPage());
			}
		});
		chat_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				currentIndex = currentIndex - capacity;
				if (currentIndex <= 0) {
					currentIndex = 0;
					chat_back.setEnabled(false);
					chat_first.setEnabled(false);
					chat_last.setEnabled(true);
					chat_next.setEnabled(true);
				} else {
					chat_back.setEnabled(true);
					chat_first.setEnabled(true);
					chat_last.setEnabled(true);
					chat_next.setEnabled(true);
				}
				getMessage(aboutUid, currentIndex, capacity);
				mTitleBar.updateCenterInfo("记录（" + getCurrentPage() + "/"
						+ getSumPages() + "）");
				LogFactory.d(TAG, "sum:" + sum + "  currentIndex:"
						+ currentIndex + "  CurrentPage:" + getCurrentPage());
			}
		});
		chat_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				currentIndex = currentIndex + capacity;
				if (currentIndex >= sum) {
					currentIndex = currentIndex - capacity;
				}
				if (getCurrentPage() >= getSumPages()) {
					chat_back.setEnabled(true);
					chat_first.setEnabled(true);
					chat_last.setEnabled(false);
					chat_next.setEnabled(false);
				} else {
					chat_back.setEnabled(true);
					chat_first.setEnabled(true);
					chat_last.setEnabled(true);
					chat_next.setEnabled(true);
				}
				getMessage(aboutUid, currentIndex, capacity);
				mTitleBar.updateCenterInfo("记录（" + getCurrentPage() + "/"
						+ getSumPages() + "）");
				LogFactory.d(TAG, "sum:" + sum + "  currentIndex:"
						+ currentIndex + "  CurrentPage:" + getCurrentPage());
			}
		});
		chat_last.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int pages = sum / capacity;
				currentIndex = pages * capacity;
				if (currentIndex >= sum) {
					currentIndex = currentIndex - capacity;
				}
				getMessage(aboutUid, currentIndex, capacity);

				chat_back.setEnabled(true);
				chat_first.setEnabled(true);
				chat_last.setEnabled(false);
				chat_next.setEnabled(false);
				mTitleBar.updateCenterInfo("记录（" + getCurrentPage() + "/"
						+ getSumPages() + "）");
				LogFactory.d(TAG, "sum:" + sum + "  currentIndex:"
						+ currentIndex + "  CurrentPage:" + getCurrentPage());
			}
		});
		chat_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DialogInterface.OnClickListener tempListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						switch (arg1) {
						case DialogInterface.BUTTON1:// 删除数据
						{
							try {
								IMOApp.imoStorage.deleteMessage(aboutUid);
								RecentContactActivity.getActivity()
										.deleteRecentUser(aboutUid);
							} catch (Exception e) {
								e.printStackTrace();
							}
							chat_delete.setEnabled(false);
							chat_first.setEnabled(false);
							chat_back.setEnabled(false);
							chat_next.setEnabled(false);
							chat_last.setEnabled(false);
							message_list.clear();
							dialogueListAdapter.notifyDataSetChanged();
							mTitleBar.updateCenterInfo("记录（" + 1 + "/" + 1
									+ "）");
							break;
						}
						case DialogInterface.BUTTON2:// 取消

							break;

						default:
							break;
						}
						dialog.dismiss();
					}

				};
				dialog = DialogFactory.alertDialog(mContext, "imo提示",
						"确认删除该联系人的历史记录？", new String[] { "删除", "取消" },
						tempListener, tempListener);
				dialog.show();

			}
		});

	}

	@Override
	public void refresh(Object param) {

	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {

	}

	@Override
	public boolean CanAcceptPacket(int command) {
		return super.CanAcceptPacket(command);
	}

}

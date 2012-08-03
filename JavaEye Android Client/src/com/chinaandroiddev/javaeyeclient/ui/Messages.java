package com.chinaandroiddev.javaeyeclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.chinaandroiddev.javaeyeclient.About;
import com.chinaandroiddev.javaeyeclient.Logout;
import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.api.JavaEyeApiAccessor;
import com.chinaandroiddev.javaeyeclient.model.Message;
import com.chinaandroiddev.javaeyeclient.util.Constants;
import com.chinaandroiddev.javaeyeclient.util.Formatter;
import com.flurry.android.FlurryAgent;

public class Messages extends ExpandableListActivity {
	private static final String LOG_TAG = "Messages";
	
	private static final int MENU_COMPOSE = Menu.FIRST + 1;
    private static final int MENU_REFRESH = Menu.FIRST + 2; 
    private static final int MENU_TWITTER = Menu.FIRST + 3;
    private static final int MENU_FAVORITES = Menu.FIRST + 4;
    private static final int MENU_ABOUT = Menu.FIRST + 5;
    private static final int MENU_EXIT = Menu.FIRST + 6;
    
    private static final int MENU_REPLY = Menu.FIRST + 7;
    private static final int MENU_DELETE = Menu.FIRST + 8;
    private static final int MENU_LOGOUT = Menu.FIRST + 9;
    
    
	private ProgressDialog progressDialog;
	private ExpandableListAdapter expAdapter;
	private static List<Map<String, Object>> parentData = new ArrayList<Map<String, Object>>();
	private static List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
	private List<Message> messageList;
	private Handler handler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getMessages();
		registerForContextMenu(getExpandableListView());
	}

	private void getData() {
		parentData.clear();
		childData.clear();
		
		for (Message msg : messageList) {
			Map<String, Object> curGroupMap = new HashMap<String, Object>();
			parentData.add(curGroupMap);
			curGroupMap.put("sender_name", msg.sender.name);
			curGroupMap.put("title", msg.title);
			curGroupMap.put("time", Formatter.sdf.format(msg.createdTime));
			if (msg.isRead) {
				curGroupMap.put("isReader", R.drawable.mailopen);
			} else {
				curGroupMap.put("isReader", R.drawable.mailclose);
			}
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			Map<String, String> curChildMap = new HashMap<String, String>();
			children.add(curChildMap);
			curChildMap.put("body", msg.body);
			curChildMap.put("tip", "短信内容：");
			childData.add(children);
		}
	}

	private void getMessages() {
	    FlurryAgent.onEvent("Messages Get Messages");
		progressDialog = ProgressDialog.show(Messages.this, "请稍等...",
				"获取收件箱信息...", true);
		new Thread() {
			public void run() {
				try {
					messageList = JavaEyeApiAccessor.inBox(-1, -1);
					if (messageList != null && messageList.size() > 0) {
						updateList();
					}
				} catch (Exception e) {
//					Log.e(LOG_TAG, e.getMessage());
				    FlurryAgent.onError("Get Inbox Error", e.getMessage(), LOG_TAG);
				}
				progressDialog.dismiss();
			}
		}.start();
	}

	private void updateList() {
	    FlurryAgent.onEvent("Messages Update List");
		handler.post(new Runnable() {
			public void run() {
				getData();
				expAdapter = new MessagesAdapter(Messages.this, parentData,
						R.layout.messages_row, new String[] { "sender_name",
								"title", "time" }, new int[] {
								R.id.message_sender, R.id.message_title,
								R.id.message_createdTime }, childData,
						R.layout.messages_body, new String[] { "body", "tip" },
						new int[] { R.id.message_child, R.id.message_tip });
				setListAdapter(expAdapter);
			}
		});
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateOptionsMenu(menu);
		menu.setHeaderTitle("短信操作菜单");
		menu.add(0, MENU_REPLY, 0, "回复");
		menu.add(0, MENU_DELETE, 0, "删除");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
				.getMenuInfo();
		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		int groupPos=-1;
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			groupPos = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
		}
		switch (item.getItemId()) {
		case MENU_REPLY:
		    FlurryAgent.onEvent("Message Reply Action");
			Intent i=new Intent(Messages.this,MessageReply.class);
			i.putExtra("replyid", messageList.get(groupPos).id);
			i.putExtra("title", messageList.get(groupPos).title);
			startActivity(i);
			return true;
		case MENU_DELETE:
		    FlurryAgent.onEvent("Message Delete Action");
			Message msg=new Message();
			boolean flag=false;
			msg.id=messageList.get(groupPos).id;
			try {
				flag=JavaEyeApiAccessor.deleteMessage(msg);
			} catch (Exception e) {
				flag=false;
				Log.e(LOG_TAG, e.getMessage());
			}
			if(!flag){
				new AlertDialog.Builder(Messages.this)
                .setMessage("短信删除失败！  请稍后再试!")
                .setPositiveButton("Okay", null)
                .show();
			}else{
				new AlertDialog.Builder(Messages.this)
                .setMessage("短信删除成功！")
                .setPositiveButton("Okay", null)
                .show();
			}
			return true;
		}
		return false;
	}
	
	@Override 
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_COMPOSE, 0, "新短信").setIcon(R.drawable.message).setAlphabeticShortcut('N');
        menu.add(0, MENU_REFRESH, 0, "更新").setIcon(R.drawable.refresh).setAlphabeticShortcut('R');
        menu.add(0, MENU_TWITTER, 0, "闲聊").setIcon(R.drawable.twitter).setAlphabeticShortcut('T');;
        menu.add(0, MENU_FAVORITES, 0, "收藏").setIcon(R.drawable.bookmark).setAlphabeticShortcut('B');
        menu.add(0, MENU_ABOUT, 0, "关于").setIcon(R.drawable.android_cn).setAlphabeticShortcut('A');
        menu.add(0, MENU_LOGOUT, 0, "注销").setIcon(R.drawable.logout).setAlphabeticShortcut('L');
        menu.add(0, MENU_EXIT, 0, "退出").setIcon(R.drawable.exit).setAlphabeticShortcut('X');
        return true;
    } 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_COMPOSE: {
                FlurryAgent.onEvent("Messages Compose New Menu Click");
                Intent i = new Intent(Messages.this,MessageNew.class);
                startActivity(i);
                return true;
            }
            case MENU_REFRESH:
                FlurryAgent.onEvent("Messages Refresh Menu Click");
                getMessages();
                return true;
            case MENU_TWITTER: {
                FlurryAgent.onEvent("Messages Twitters Menu Click");
                Intent i = new Intent(Messages.this, Twitters.class);
                startActivity(i);
                return true;
            }
            case MENU_FAVORITES: {
                FlurryAgent.onEvent("Messages Favorites Menu Click");
                Intent i = new Intent(Messages.this, Favorites.class);
                startActivity(i);
                return true;
            }            
            case MENU_ABOUT: {
                FlurryAgent.onEvent("Messages About Menu Click");
                Intent i = new Intent(Messages.this, About.class);
                startActivity(i);
                return true;
            }
            case MENU_LOGOUT: {
                FlurryAgent.onEvent("Messages Logout Menu Click");
                Intent i = new Intent(Messages.this, Logout.class);
                startActivity(i);
                return true;
            }           
            case MENU_EXIT: {
                FlurryAgent.onEvent("Messages Exit Menu Click");
                finish();
                return true;
            }           
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Constants.FLURRY_API_KEY);
    }
    
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}

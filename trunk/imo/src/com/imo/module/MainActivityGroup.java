package com.imo.module;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.global.AppService;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.config.SystemSetActivity;
import com.imo.module.contact.ContactActivity;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.module.group.GroupActivity;
import com.imo.module.organize.OrganizeActivity;
import com.imo.module.welcome.WelcomeActivity;
import com.imo.network.net.EngineConst;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.LogFactory;
import com.imo.util.NoticeManager;
import com.imo.util.PreferenceManager;
import com.imo.view.BottomBar;

/**
 * MainActivityGroup
 * 
 * @author CaixiaoLong
 * 
 */
public class MainActivityGroup extends TabActivity {

	private String TAG = "MainActivityGroup";

	private BottomBar mBottomBar;

	public TabHost tabHost;

	private static MainActivityGroup activityGroup = null;

	private View first_tip;

	public Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 0:
				updateShowNewMSG(false);
				break;
			case 1:
				updateShowNewMSG(true);
				break;
			default:
				break;
			}

		};
	};

	private String[] bottomBarName = { "对话", "组织结构", "联系人", "群组" };

	private String[] bottomBarTag = 
		   {RecentContactActivity.class.getName(),
			OrganizeActivity.class.getName(), 
			ContactActivity.class.getName(),
			GroupActivity.class.getName() };

	private int[] bottomBarImgResIdSelected = {
			R.drawable.menu_dialogue_selected,
			R.drawable.menu_organize_selected,
			R.drawable.menu_contact_selected, R.drawable.menu_group_selected };

	private int[] bottomBarImgResId = { R.drawable.menu_dialogue,
			R.drawable.menu_organize, R.drawable.menu_contact,
			R.drawable.menu_group };

	/**
	 * 存在未读的新消息
	 */
	private int[] dialogueResId = { R.drawable.menu_dialogue,
			R.drawable.menu_dialogue_selected,
			R.drawable.menu_dialogue_new_msg,
			R.drawable.menu_dialogue_selected_new_msg };

	private boolean currentIsDialogue = false;

	/**
	 * 更新底部的菜单
	 * 
	 * @param hasNewMSG
	 */
	public void update2ShowNew(boolean hasNewMSG) {
		if (hasNewMSG) {
			bottomBarImgResId[0] = dialogueResId[0];
			bottomBarImgResIdSelected[0] = dialogueResId[1];
		} else {
			bottomBarImgResId[0] = dialogueResId[2];
			bottomBarImgResIdSelected[0] = dialogueResId[3];
		}
	}

	public static void launch(Context c) {
		Intent intent = new Intent(c, MainActivityGroup.class);
		c.startActivity(intent);
		// ((Activity) c).finish();
		// c=null;
		// System.gc();
	}

	// public static MainActivityGroup instance = null;
	//
	// public static MainActivityGroup getActivityGroup(){
	//
	// return instance;
	// }

	public static MainActivityGroup getActivityGroup() {
		return activityGroup;
	}

	@Override
	protected void onRestart() {
		LogFactory.d(TAG, "activity Group has onRestart.....");
		super.onRestart();
		int index = getIntent().getIntExtra("index", -1);
		IMOApp.getApp().hasRunInBackground = false;
		if (index != -1) {
			mBottomBar.setCurPos(index);
			tabHost.setCurrentTabByTag(generalTag(index));
		}
	}

	private NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (IMOApp.getApp().reStartProgram(this)) {
			return;
		}
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (IMOApp.getApp().isAppExit()) {
			finish();
		}

		notificationManager = IMOApp.getApp().getNoticeManager();

		activityGroup = this;

		setContentView(R.layout.main_activity);

		tabHost = this.getTabHost();

		installViews();

		registerEvents();

		IMOApp.getApp().addAbsBaseActivity(this);
	}

	private void installViews() {

		LogFactory.d(TAG, "MainActivityGroup create..............");

		mBottomBar = (BottomBar) findViewById(R.id.mBottombar);

		first_tip = findViewById(R.id.first_tip);

		addNewTabSpec(generalTag(0), bottomBarName[0],RecentContactActivity.class);
		addNewTabSpec(generalTag(1), bottomBarName[1], OrganizeActivity.class);
		addNewTabSpec(generalTag(2), bottomBarName[2], ContactActivity.class);
		addNewTabSpec(generalTag(3), bottomBarName[3], GroupActivity.class);
	}

	public void updateBottomBarPos(int pos) {
		mBottomBar.setCurPos(pos);
	}

	/**
	 * 底部菜单显示new
	 * 
	 * @param hasNewMSG
	 */
	public void updateShowNewMSG(boolean hasNewMSG) {
		mBottomBar.setHasNewMSG(hasNewMSG);
	}

	public String generalTag(int index) {
		return bottomBarTag[index];
	}

	public void updateTipShow(boolean needShow) {
		if (needShow) {
			first_tip.setVisibility(View.VISIBLE);
			PreferenceManager.save("IMOLoading", new String[] {"isFirstLoading", "yes" });
		} else {
			first_tip.setVisibility(View.GONE);
		}
	}

	protected void registerEvents() {

		first_tip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateTipShow(false);
			}
		});

		mBottomBar.setBottomBar(bottomBarImgResId, bottomBarImgResIdSelected,
				new int[4]);

		mBottomBar.setBottomBarListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				mBottomBar.setCurPos(position);
				tabHost.setCurrentTabByTag(generalTag(position));

				if (currentIsDialogue) {
					System.out.println("currentIsDialogue--" + currentIsDialogue);
					updateShowNewMSG(false);
				}

				if (position == 0) {
					currentIsDialogue = true;
				} else {
					currentIsDialogue = false;
				}

			}
		});

		// //默认进入组织结构界面
		mBottomBar.setCurPos(1);
		tabHost.setCurrentTabByTag(generalTag(1));
	}

	/**
	 * 添加新的TabSpec
	 * 
	 * @param tag
	 * 
	 * @param label
	 * 
	 * @param cls
	 * 
	 */
	private void addNewTabSpec(String tag, CharSequence label, Class<?> cls) {
		Intent intent = new Intent(this, cls);
		// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		TabSpec tabSpec = tabHost.newTabSpec(tag).setIndicator(label).setContent(intent);

		if (tabHost != null)
			tabHost.addTab(tabSpec);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Globe.showActivityCount++;
		LogFactory.d("冯小卫", "onStart------------------>");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (isFinishing()) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {

		}

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.getLocalActivityManager().getCurrentActivity().onKeyDown(keyCode, event);
			// IMOApp.getApp().exitApp();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		// return super.onKeyDown(keyCode, event);

		// if(keyCode==KeyEvent.KEYCODE_MENU) {
		// this.getLocalActivityManager().getCurrentActivity().openOptionsMenu();
		// }else if(keyCode == KeyEvent.KEYCODE_BACK){
		// DialogFactory.promptExit(this.getLocalActivityManager().getCurrentActivity()).show();
		// return true;
		// }
		//
		// return super.onKeyDown(keyCode, event);
		return true;
	}

	@Override
	protected void onPause() {
		LogFactory.e("MainActivityGroup", "onPause");

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		LogFactory.e("MainActivityGroup", "onStop");
		super.onStop();
		Globe.showActivityCount--;
		LogFactory.d("冯小卫", "onStop------------------>");
		if (Globe.showActivityCount == 0 && !IMOApp.getApp().isAppExit()) {
			IMOApp.getApp().hasRunInBackground = true;// //标识未后台运行
			NoticeManager.updateRecoverAppNotice(notificationManager);
		}
	}

	@Override
	protected void onDestroy() {
		LogFactory.e("MainActivityGroup", "onDestroy");

		getLocalActivityManager().removeAllActivities();

		IMOApp.getApp().removeAbsBaseActivity(this);

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_group_menu, menu);// 指定使用的XML
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch (item_id) {
		case R.id.system_set: {
			SystemSetActivity.launch(this);
			break;
		}
		case R.id.exit_system: {
			DialogFactory.promptExit(this).show();
			break;
		}
		}
		return true;
	}

}

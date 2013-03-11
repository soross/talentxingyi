package com.imo.module.contact;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.dataengine.DataEngine;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.config.SystemSetActivity;
import com.imo.module.config.UserConfigActivity;
import com.imo.module.dialogue.DialogueActivity;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.module.login.LoginActivity;
import com.imo.module.organize.EmployeeDetailActivity;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.view.OrganizeAdapter;
import com.imo.module.organize.view.SearchResultAdapter;
import com.imo.module.organize.view.StructNavView;
import com.imo.network.net.EngineConst;
import com.imo.network.netchange.ConnectionChangeReceiver;
import com.imo.network.packages.EmployeeInfoItem;
import com.imo.network.packages.IMOCommand;
import com.imo.util.DialogFactory;
import com.imo.util.Functions;
import com.imo.util.ImageUtil;
import com.imo.util.LogFactory;
import com.imo.util.NoticeManager;
import com.imo.util.PreferenceManager;
import com.imo.util.SystemInfoManager;
import com.imo.view.ChangeStatePop;
import com.imo.view.ChangeStatePop.OnStateClickListener;
import com.imo.view.SearchBar;
import com.imo.view.SearchBar.OnSearchListener;

/**
 * 联系人界面
 */
public class ContactActivity extends AbsBaseActivityNetListener {

	private String TAG = ContactActivity.class.getSimpleName();

	// 联系人适配器
	private OrganizeAdapter mContactAdapter;

	private SearchResultAdapter mSearchResultAdapter;

	private ListView mContactListView;

	private View contactView;

	private ListView mSearchRestltListView;

	private StructNavView mStructNavView;

	private SearchBar mSearchBar;

	private ChangeStatePop mChangeStatePop;

	/** 第一次构建树 */
	private final int TYPE_FIRST_BUILD_TREE = 0;
	/** 更新服务器Pull过来的状态 */
	public static final int TYPE_UPDATE_STATE = 2;

	private static ContactActivity mActivity = null;

	public View pop_view;

	/**
	 * 获得Activity实例对象
	 * 
	 * @return
	 */
	public static ContactActivity getActivity() {
		return mActivity;
	}

	public Handler mContactHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case TYPE_FIRST_BUILD_TREE:
					getContactTree();
					break;
				case TYPE_UPDATE_STATE:
					sortNode();
					break;
				default:
					break;
			}
		};
	};

	/**
	 * 得到内部联系人的 EmployeeInfo Map : 在进入该界面的时候构建
	 */
	private HashMap<Integer, ArrayList<EmployeeInfoItem>> mInnerGroupContactMap = null;

	/**
	 * 根据ContactId查找组织结构数据
	 * 
	 * @param innerGroupIdMap
	 */
	public void initInnerGroupMap() {
		mInnerGroupContactMap = new HashMap<Integer, ArrayList<EmployeeInfoItem>>();

		ArrayList<EmployeeInfoItem> employeeInfoItemList = null;

		for (Integer groupId : mGlobal.innerGroupIdMap.keySet()) {
			employeeInfoItemList = new ArrayList<EmployeeInfoItem>();

			ArrayList<Integer> contactIdList = mGlobal.innerGroupContactMap.get(groupId);

			if (contactIdList != null) {
				for (int i = 0; i < contactIdList.size(); i++) {
					employeeInfoItemList.add(RecentContactActivity.getActivity().getEmployeeInfoByUid(contactIdList.get(i)));
				}
				// 构建内部联系人组和对应的 联系人列表的映射
				mInnerGroupContactMap.put(groupId, employeeInfoItemList);
			}
		}
	}

	/**
	 * 重新绑定数据
	 */
	private void bindListView() {
		mContactAdapter = new OrganizeAdapter(mContext, mGlobal.rootNodeContact);
		mContactAdapter.forContact = true;
		mContactListView.setAdapter(mContactAdapter);
		mContactListView.setOnItemClickListener(mItemClickListener);

		Node node = mStructNavView.getCurChildNode();
		if (node != null) {
			mContactAdapter.showChildNodes(node);
		}
		mStructNavView.setOnItemClickListener(mContactAdapter);
	}

	/**
	 * 内部联系人排序
	 */
	private void sortInnerContactByState(ArrayList<Node> nodeList) {
		if (nodeList != null) {
			Collections.sort(nodeList, new Comparator<Node>() {
				@Override
				public int compare(Node item1, Node item2) {
					// TODO Auto-generated method stub
					return (item1.getId() - item2.getId());
				}
			});

			Collections.sort(nodeList, new Comparator<Node>() {

				@Override
				public int compare(Node item1, Node item2) {
					int state1 = Integer.MAX_VALUE;
					int state2 = Integer.MAX_VALUE;

					if (IMOApp.getApp().userStateMap.get(item1.getId()) != null) {
						state1 = (IMOApp.getApp().userStateMap.get(item1.getId())) & 0x000000FF;
					}

					if (IMOApp.getApp().userStateMap.get(item2.getId()) != null) {
						state2 = (IMOApp.getApp().userStateMap.get(item2.getId())) & 0x000000FF;
					}

					if (state1 == 0)
						state1 = Integer.MAX_VALUE;
					if (state2 == 0)
						state2 = Integer.MAX_VALUE;

					return (state1 - state2);
				}
			});
		}
	}

	/**
	 * 外部联系人排序
	 */
	private void sortOuterContactByState(ArrayList<Node> groupNodeList) {

		if (groupNodeList != null) {

			Collections.sort(groupNodeList, new Comparator<Node>() {

				@Override
				public int compare(Node item1, Node item2) {
					// TODO Auto-generated method stub
					return -(item1.getId() - item2.getId());
				}
			});

			Collections.sort(groupNodeList, new Comparator<Node>() {

				@Override
				public int compare(Node item1, Node item2) {
					int state1 = Integer.MAX_VALUE;
					int state2 = Integer.MAX_VALUE;
					if (IMOApp.getApp().outerUserStateMap.get(item1.getId()) != null) {
						state1 = IMOApp.getApp().outerUserStateMap.get(item1.getId()) & 0x000000FF;
					}
					if (IMOApp.getApp().outerUserStateMap.get(item2.getId()) != null) {
						state2 = IMOApp.getApp().outerUserStateMap.get(item2.getId()) & 0x000000FF;
					}

					if (state1 == 0)
						state1 = Integer.MAX_VALUE;
					if (state2 == 0)
						state2 = Integer.MAX_VALUE;

					return (state1 - state2);
				}
			});
		}
	}

	/**
	 * 构建完整的Tree
	 * 
	 * 构建InnerContact 构建OuterContact
	 */
	public void buildTree() {
		sortNode();
	}

	/**
	 * 内存Tree到界面的显示
	 * 
	 * 使用全局变量构建树。
	 */
	public void getContactTree() {
		buildTree();
	}

	public static void launch(Context c) {
		Intent intent = new Intent(c, ContactActivity.class);
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		setContentView(R.layout.contact_activity);

		mActivity = this;

		contactView = findViewById(R.id.organize);

		mTitleBar.initContactTitlebar();
		mTitleBar.setOrganizeTitleData(Globe.myself == null ? null : Globe.myself.getName(), Globe.myself == null ? null : Globe.myself.getSign());

		pop_view = findViewById(R.id.pop_view);
		pop_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pop_view.setVisibility(View.GONE);
				mSearchBar.updateCursorState(false);
				SystemInfoManager.hideKeyBoard(getActivity());
			}
		});

		mSearchBar = (SearchBar) findViewById(R.id.searchbar);

		mStructNavView = (StructNavView) findViewById(R.id.structNavView);
		mStructNavView.setVisibility(View.GONE);

		mContactListView = (ListView) findViewById(R.id.innerListView);

		mSearchRestltListView = (ListView) findViewById(R.id.searchResultListView);
		mSearchResultAdapter = new SearchResultAdapter(mContext);

		mChangeStatePop = new ChangeStatePop(LayoutInflater.from(mContext).inflate(R.layout.changestate_dialog, null, true));
	}

	protected boolean needObserver() {
		return false;
	}

	/** 更新头像 */
	private void updateFace(boolean networkIsConnected) {
		mTitleBar.updateStateIcon(networkIsConnected);
		if (networkIsConnected) {
			if (Globe.bm_head != null) {
				mTitleBar.setFaceBitmap(Globe.bm_head);
			} else {
				mTitleBar.setFaceDefault(mThisUidIsBoy(EngineConst.uId));
			}
		} else {
			// 头像跳灰色
			if (Globe.bm_head != null) {
				ImageUtil.setFaceImg(mTitleBar.getFaceView(), Globe.bm_head, 0, -1);
			} else {
				mTitleBar.setOfflineFaceDefault();
			}
		}
	}

	/** titleBar中的头像更新Handler */
	public Handler titleBarHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			updateFace(EngineConst.isNetworkValid && EngineConst.isReloginSuccess);
		}
	};

	@Override
	protected void registerEvents() {

		mContactHandler.sendEmptyMessage(TYPE_FIRST_BUILD_TREE);

		mTitleBar.setSystemSetListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					SystemSetActivity.launch(ContactActivity.this);
					return true;
				}

				return true;
			}
		});

		mTitleBar.setFaceOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UserConfigActivity.launch(mContext);
			}
		});

		// 状态弹窗显示
		mTitleBar.setStateOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mChangeStatePop.popUp(getParent().getWindow().getDecorView());
			}
		});

		mChangeStatePop.setListener(new OnStateClickListener() {

			@Override
			public void onClickAction() {
				Globe.canConnect = false;
				requestLogOut(false);
			}
		});

		mChangeStatePop.setReLoginListener(new OnStateClickListener() {

			@Override
			public void onClickAction() {
				if (EngineConst.isNetworkValid && EngineConst.isReloginSuccess) {
					return;
				} else if (ConnectionChangeReceiver.pingAddress()) {
					DataEngine.getInstance().reconnectServer();
				}
			}
		});

		mSearchBar.setOnClickEvent();

		mSearchBar.setOnSearchListener(new OnSearchListener() {

			@Override
			public void onSearch(View v) {
				LogFactory.d(TAG, "Dynamic key = " + ((EditText) v).getText());

				doSearch(((EditText) v).getText().toString().trim());
			}

			private void doSearch(String key) {

				update2State(true);

				ArrayList<Integer> deptUserId = IMOApp.imoStorage.search(key);

				if (deptUserId != null) {
					mSearchResultAdapter.setShowData(getSearchResult(deptUserId));
					mSearchRestltListView.setAdapter(mSearchResultAdapter);
				} else {
					mSearchResultAdapter.setEmpty();
				}
			}

			@Override
			public void initState() {
				update2State(false);
			}
		});

		mSearchRestltListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

				if (mSearchResultAdapter.getItem(position).getId() == EngineConst.uId) {
					EmployeeDetailActivity.launch(mContext);
				} else {
					Intent intent = new Intent(mContext, DialogueActivity.class);
					Bundle data = new Bundle();
					Node curNode = mSearchResultAdapter.getItem(position);
					data.putInt("cid", curNode.getCid());
					data.putInt("uid", curNode.getId());
					data.putString("name", curNode.getNodeData().nodeName);
					data.putBoolean("sex", curNode.getNodeData().isBoy);
					startActivity(intent.putExtras(data));
				}
			}
		});

	}

	/**
	 * sort node for new state
	 */
	private void sortNode() {
		ArrayList<Node> innerGroupNode = mGlobal.rootNodeInner.getChildNodes();
		for (Node groupNode : innerGroupNode) {
			sortInnerContactByState(groupNode.getChildNodes());
		}

		ArrayList<Node> outerGroupNode = mGlobal.rootNodeOuter.getChildNodes();
		for (Node groupNode : outerGroupNode) {
			sortOuterContactByState(groupNode.getChildNodes());
		}

		bindListView();
	}

	/**
	 * 获得查询结果 Node List
	 * 
	 * @param uidList
	 * @return
	 */
	private ArrayList<Node> getSearchResult(ArrayList<Integer> uidList) {

		Integer uid = null;

		ArrayList<Node> searchResultList = new ArrayList<Node>();

		for (int i = 0; i < uidList.size(); i++) {

			uid = uidList.get(i);
			Node node = mGlobal.mNodeMap.get(uid);

			if (node != null && node.getParentNode() != null && node.getParentNode().isNeedShow()) {
				searchResultList.add(node);
			}
		}

		return searchResultList;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mTitleBar.updateSign(Globe.myself == null ? null : Globe.myself.getSign());
		updateFace(EngineConst.isNetworkValid && EngineConst.isReloginSuccess);

		sortNode();
	}

	/**
	 * 更新显示List的状态
	 */
	private void update2State(boolean state) {
		mSearchBar.updateCursorState(state);
		if (!state) {
			contactView.setVisibility(View.VISIBLE);
			mSearchRestltListView.setVisibility(View.GONE);
		} else {
			// 显示搜索结果状态
			mSearchRestltListView.setVisibility(View.VISIBLE);
			contactView.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean CanAcceptHttpPacket() {
		return false;
	}

	@Override
	public boolean CanAcceptPacket(int command) {
		super.CanAcceptPacket(command);
		if (IMOCommand.IMO_EXIT == command)
			return true;

		return false;
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub
	}

	public void NotifyPacketArrived(String aConnectionId, short command) {
		super.NotifyPacketArrived(aConnectionId, command);

		if (EngineConst.IMO_CONNECTION_ID.equals(aConnectionId))

			switch (command) {
				default:
					break;
			}
	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			mSearchBar.updateCursorState(false);

			Node curNode = mContactAdapter.getItem(position);

			if (!curNode.isDept()) {
				Intent intent = new Intent(mContext, DialogueActivity.class);
				Bundle data = new Bundle();
				data.putInt("cid", curNode.getCid());
				data.putInt("uid", curNode.getId());
				String name = curNode.getNodeData().nodeName;
				if (name.indexOf("—") > 0) {
					name = name.split("—")[0];
				}
				data.putString("name", name);
				data.putBoolean("sex", curNode.getNodeData().isBoy);

				LogFactory.d("data", "cid=" + EngineConst.cId + "  uid =" + curNode.getId() + "  mName= " + curNode.getNodeData().nodeName);
				startActivity(intent.putExtras(data));
			} else {

				mStructNavView.setVisibility(View.VISIBLE);
				mContactAdapter.showChildNodes(curNode);
				if (mStructNavView.getViewGroupChildCount() == 0) {
					mStructNavView.addItem(mGlobal.rootNodeContact.getNodeData().nodeName, mGlobal.rootNodeContact);
				}
				mStructNavView.addItem(curNode.getNodeData().nodeName, curNode);
				// 实现导航点击事件的封装。
				mStructNavView.setOnItemClickListener(mContactAdapter);
			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (isFinishing()) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {}

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			LogFactory.d(TAG, "KEYCODE_BACK has clicked .");

			if (mStructNavView.getViewGroupChildCount() == 2) {
				mStructNavView.removeAllItemView();
				mContactAdapter.showChildNodes(mGlobal.rootNodeContact);
			} else {
				Node node = mStructNavView.getLastChildNode();
				// ///update
				if (node != null) {
					mContactAdapter.showChildNodes(node);
				} else {
					boolean keepOnline = (Boolean) PreferenceManager.get(Globe.SP_FILE, new Object[] {
							LoginActivity.LOGIN_KEEPONLINE, false
					});
					if (!keepOnline) {
						DialogFactory.promptExit(mContext).show();
					} else {
						IMOApp.getApp().hasRunInBackground = true;
						NoticeManager.updateRecoverAppNotice(notificationManager);
						Functions.backToDesk(this);
					}
				}
			}
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {

		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void refresh(Object param) {
		// TODO Auto-generated method stub

	}
}

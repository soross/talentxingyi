package com.imo.module.organize.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;
import com.imo.module.organize.struct.NodeManager;
import com.imo.network.net.EngineConst;
import com.imo.util.LogFactory;

/**
 * 组织结构适配器
 * 
 * @author CaixiaoLong
 * 
 */
public class OrganizeAdapter extends BaseAdapter {

	public boolean forContact = false;

	private OrganizeAdapter adapter;
	private Context mContext;

	private ArrayList<Node> allNodes = new ArrayList<Node>();
	private ArrayList<Node> showNodes = new ArrayList<Node>();
	private ArrayList<Node> searchResultNodes = new ArrayList<Node>();

	private int selectedPos = -1;

	public OrganizeAdapter getAdapter() {
		return adapter;
	}

	/**
	 * 收到一个消息后，需要更新数据
	 * 
	 * @param uid
	 * @param state
	 */
	public void updateDeptStateStatisticsMap(int uid, int state) {
		// /更新部门
		for (int i = 0; i < IMOApp.getApp().deptid.length; i++) {

		}

	}

	public void search(ArrayList<Integer> userId) {
		for (int i = 0; i < allNodes.size(); i++) {}
	}

	/**
	 * 在构造方法中实现树的数据源适配
	 * 
	 * @param context
	 *        上下文对象
	 * @param rootNode
	 *        [数据结构的一棵树对象]
	 */
	public OrganizeAdapter(Context context, Node rootNode) {
		adapter = this;
		this.mContext = context;
		// initBg();
		executeAdapter(rootNode);

		showFirstData(rootNode);
	}

	public OrganizeAdapter(Context context, Node rootNode, Boolean isRoot) {
		adapter = this;
		this.mContext = context;
		// initBg();
		executeAdapter(rootNode);

		// showFirstData(rootNode);
		showRootData(rootNode);
	}

	private void showRootData(Node rootNode) {

		showNodes.clear();
		showNodes.add(rootNode);

		adapter.notifyDataSetChanged();
	}

	private void showFirstData(Node rootNode) {

		showNodes.clear();
		showChildNodes(rootNode);
	}

	public void setSelectedPos(int pos) {
		this.selectedPos = pos;
	}

	/**
	 * 更新节点的状态
	 * 
	 * @param position
	 */
	public void updateNodeState(int position) {
		Node node = showNodes.get(position);
		if (node != null) {
			if (!NodeManager.isLeaf(node)) {
				node.setNodeState(!node.getNodeState());
				buildShowNodes();
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 执行树结构和ListView的适配 <br>
	 * 
	 * @param node
	 *        节点对象
	 */
	private void executeAdapter(Node node) {

		showNodes.add(node);
		allNodes.add(node);

		if (NodeManager.isLeaf(node))
			return;

		for (int i = 0; i < node.getChildNodes().size(); i++) {
			executeAdapter(node.getChildNodes().get(i));
		}

	}

	/**
	 * 构建当前需要显示节点的数据
	 * 
	 */
	private void buildShowNodes() {

		showNodes.clear();
		LogFactory.d("xx", "clear showNodes size = " + showNodes.size());
		LogFactory.d("xx", "allNodes size = " + allNodes.size());

		for (int i = 0; i < allNodes.size(); i++) {
			Node node = allNodes.get(i);
			if (!NodeManager.isParentCollapsed(node) || NodeManager.isRoot(node)) {
				if (node.isNeedShow()) {
					showNodes.add(node);
				}
			}
		}
		LogFactory.d("xx", "showNodes size = " + showNodes.size());
	}

	// =========================================================================
	/**
	 * 显示当前节点的孩子节点 <br>
	 * 
	 * @param node
	 */
	public void showChildNodes(Node node) {
		showNodes.clear();
		ArrayList<Node> childNodes = node.getChildNodes();
		showNodes = (ArrayList<Node>) childNodes.clone();
		// LogFactory.d("xx", "showChildNodes size = " + showNodes.size());
		for (Node tempNode : (ArrayList<Node>) childNodes.clone()) {
			if (!tempNode.isNeedShow()) {
				showNodes.remove(tempNode);
			}
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return showNodes.size();
	}

	@Override
	public Node getItem(int position) {
		return showNodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		boolean isInner = true;

		Bundle dataBundle = null;

		allCount = onLineCount = 0;

		NodeView view = null;
		Node node = null;
		NodeData data = null;
		// int totalCount = 0;
		// int onLineCount = 0;

		if (convertView == null) {
			// 使用开关控制后少一部分逻辑操作
			view = new NodeView(mContext, false);
			convertView = view;
			convertView.setTag(view);
			LogFactory.d("new", "create a new Vie");
		} else {
			view = (NodeView) convertView.getTag();
		}

		node = getItem(position);
		data = node.getNodeData();
		// =============控制字体=====================
		if (node.getId() == EngineConst.uId) {
			view.isMySelfNode = true;
		} else {
			view.isMySelfNode = false;
		}
		// ==================================

		if (forContact) {
			isInner = NodeManager.isParent(node, IMOApp.getApp().rootNodeInner) || (IMOApp.getApp().rootNodeInner == node);
		}
		// if (forContact) {
		// isInner = NodeManager.isParent(node,
		// ContactActivity.getActivity().rootNodeInner)
		// || (ContactActivity.getActivity().rootNodeInner == node);
		// }

		if (node != null) {
			if (NodeManager.isLeaf(node) && !node.isDept()) {
				view.builderViewByType(NodeView.TYPE_LEAF_NODE);
				// view.setLNData(data,node.getOnLineState());////first time
				Integer state = 0;
				if (forContact) {

					// isInner =
					// NodeManager.isParent(node,ContactActivity.getActivity().rootNodeInner);

					// if (isInner) {
					// state = IMOApp.getApp().userStateMap.get(node.getId());
					// }else{
					// state =
					// IMOApp.getApp().outerUserStateMap.get(node.getId());
					// }

					state = IMOApp.getApp().getUserStateByUid(node.getId());

					if (state == null) {
						state = 0;
						LogFactory.d("Outer", "..............Adapter outer state = null");
					}

					view.setLNData(data, state); // //Update User State Show

				} else {
					// / for Organize
					if (null != IMOApp.getApp().userStateMap && IMOApp.getApp().userStateMap.containsKey(node.getId())) {
						view.setLNData(data, IMOApp.getApp().userStateMap.get(node.getId())); // //Update
																								// User
																								// State
																								// Show
					}
				}

				// //部门下的第一个员工
				if (position == 0 || (position > 0 && getItem(position - 1).isDept())) {
					view.setBackgroundResource(R.drawable.leaf_node_first_bg);
				} else {
					view.setBackgroundResource(R.drawable.leaf_node_bg);
				}
				// view.setBackgroundDrawable(user_bg);

				dataBundle = new Bundle();
				dataBundle.putInt("cid", node.getCid());
				dataBundle.putInt("uid", node.getId());
				String name = node.getNodeData().nodeName;
				// LogFactory.d("name", "name = " + name);
				if (name.indexOf("—") > 0) {
					name = name.split("—")[0];
				}
				dataBundle.putString("name", name);
				dataBundle.putBoolean("sex", node.getNodeData().isBoy);
				view.setOnTriangleClickListener(dataBundle);
			} else {
				// /Dept
				view.builderViewByType(NodeView.TYPE_PARENT_NODE);
				view.setPNData(data);

				if (isInner) {
					getStatics(node);
				} else {
					getOuterStatics(node);
				}

				view.setPNStatistics(onLineCount + "/" + allCount);// / update
																	// statistics
																	// Data;
				view.updateNodeState(node.getNodeState());

				// view.setBackgroundDrawable(dept_bg);
				// view.setLayoutParams(new
				// LayoutParams(LayoutParams.FILL_PARENT, 68));

				view.setBackgroundResource(R.drawable.dept_node_bg);// /imo_dept_gradual_bg)
			}
			// view.setPadding((node.getLevel())*20, 3,3, 3);
		}

		if (selectedPos == position) {
			view.setBackgroundResource(R.drawable.leaf_selected);
		}

		// int height = (int)
		// (mContext.getResources().getDimension(R.dimen.dept_node_height) *
		// IMOApp.getApp().mScale);
		// ViewGroup.LayoutParams params =
		// (ViewGroup.LayoutParams)view.getLayoutParams();
		// // params.width = (int) (width * IMOApp.getApp().mScale);
		// params.height = height;
		// view.setLayoutParams(params);

		return view;
	}

	int allCount = 0;
	int onLineCount = 0;

	private void getStatics(Node node) {
		if (!node.isNeedShow())
			return;
		for (int i = 0; i < node.getChildNodes().size(); i++) {

			Node tempNode = node.getChildNodes().get(i);
			if (tempNode.isDept()) {
				if (tempNode.isNeedShow()) {
					getStatics(tempNode);
				}

			} else {
				allCount++;
				// LogFactory.d("Adapter", "User Node id = " +
				// tempNode.getId());
				if (IMOApp.getApp().userStateMap.get(tempNode.getId()) != null && IMOApp.getApp().userStateMap.get(tempNode.getId()) != 0) {
					// if (tempNode.getOnLineState() != 0) {
					onLineCount++;
				}
			}
		}
	}

	private void getOuterStatics(Node node) {

		for (int i = 0; i < node.getChildNodes().size(); i++) {

			Node tempNode = node.getChildNodes().get(i);

			if (!tempNode.isDept() && tempNode.isNeedShow()) {
				allCount++;
				if (IMOApp.getApp().outerUserStateMap.get(tempNode.getId()) != null && IMOApp.getApp().outerUserStateMap.get(tempNode.getId()) != 0 && tempNode.isNeedShow()) {
					onLineCount++;
				}
			} else {
				getOuterStatics(tempNode);
			}
		}
	}

	// ==============================================================
	// private LayerDrawable dept_bg = null;
	// private LayerDrawable user_bg = null;
	// private LayerDrawable first_user_bg = null;

	// /**
	// * 使用代码构建Bg
	// */
	// private void initBg(){
	// int top = (int)
	// (mContext.getResources().getDimension(R.dimen.dept_node_height) *
	// IMOApp.getApp().mScale);
	// Drawable[] drawableArray_dept = new Drawable[2];
	// // Bitmap bm =
	// BitmapFactory.decodeResource(mContext.getResources(),R.drawable.titlebar_bg);
	// drawableArray_dept[0] =
	// mContext.getResources().getDrawable(R.drawable.imo_dept_gradual_bg);
	// drawableArray_dept[1] =
	// mContext.getResources().getDrawable(R.drawable.dept_line0);
	// // drawableArray[0] =
	// mContext.getResources().getDrawable(R.drawable.imo_dept_gradual_bg);
	// // drawableArray[1] =
	// mContext.getResources().getDrawable(R.drawable.user_line1);
	// // drawableArray[1] =
	// mContext.getResources().getDrawable(R.drawable.user_line2);
	// // drawableArray[0] = new PaintDrawable(11);
	// // drawableArray[1] = new PaintDrawable(Color.WHITE);
	// // drawableArray[2] = new BitmapDrawable(bm);
	//
	// dept_bg = new LayerDrawable(drawableArray_dept);
	// dept_bg.setLayerInset(0, 0, 0, 0, 0);
	// dept_bg.setLayerInset(1, 0, 0, 0, (top-1));
	//
	// Drawable[] drawableArray_user = new Drawable[3];
	// drawableArray_user[0] =
	// mContext.getResources().getDrawable(R.drawable.imo_user_gradient_bg);
	// drawableArray_user[1] =
	// mContext.getResources().getDrawable(R.drawable.user_line1);
	// drawableArray_user[2] =
	// mContext.getResources().getDrawable(R.drawable.user_line2);
	// user_bg = new LayerDrawable(drawableArray_user);
	// user_bg.setLayerInset(0, 0, 0, 0, 2);
	// user_bg.setLayerInset(1, 0, (top-2), 0, 1);
	// user_bg.setLayerInset(2, 0, (top-1), 0, 0);
	// // imageView.setImageDrawable(ld);
	// }

}

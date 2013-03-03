package com.imo.module.organize.view;

import java.util.ArrayList;

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
 * ��֯�ṹ������
 */
public class OrganizeAdapter extends BaseAdapter {

	public boolean forContact = false;

	private OrganizeAdapter adapter;
	private Context mContext;

	private ArrayList<Node> allNodes = new ArrayList<Node>();
	private ArrayList<Node> showNodes = new ArrayList<Node>();

	private int selectedPos = -1;

	public OrganizeAdapter getAdapter() {
		return adapter;
	}

	/**
	 * �յ�һ����Ϣ����Ҫ��������
	 * 
	 * @param uid
	 * @param state
	 */
	public void updateDeptStateStatisticsMap(int uid, int state) {
		// /���²���
		for (int i = 0; i < IMOApp.getApp().deptid.length; i++) {

		}
	}

	public void search(ArrayList<Integer> userId) {
		for (int i = 0; i < allNodes.size(); i++) {}
	}

	/**
	 * �ڹ��췽����ʵ����������Դ����
	 * 
	 * @param context
	 *        �����Ķ���
	 * @param rootNode
	 *        [���ݽṹ��һ��������]
	 */
	public OrganizeAdapter(Context context, Node rootNode) {
		adapter = this;
		this.mContext = context;

		executeAdapter(rootNode);

		showFirstData(rootNode);
	}

	public OrganizeAdapter(Context context, Node rootNode, Boolean isRoot) {
		adapter = this;
		this.mContext = context;

		executeAdapter(rootNode);

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
	 * ���½ڵ��״̬
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
	 * ִ�����ṹ��ListView������ <br>
	 * 
	 * @param node
	 *        �ڵ����
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
	 * ������ǰ��Ҫ��ʾ�ڵ������
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
	 * ��ʾ��ǰ�ڵ�ĺ��ӽڵ� <br>
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
			// ʹ�ÿ��ؿ��ƺ���һ�����߼�����
			view = new NodeView(mContext, false);
			convertView = view;
			convertView.setTag(view);
			LogFactory.d("new", "create a new Vie");
		} else {
			view = (NodeView) convertView.getTag();
		}

		node = getItem(position);
		data = node.getNodeData();
		// =============��������=====================
		if (node.getId() == EngineConst.uId) {
			view.isMySelfNode = true;
		} else {
			view.isMySelfNode = false;
		}

		if (forContact) {
			isInner = NodeManager.isParent(node, IMOApp.getApp().rootNodeInner) || (IMOApp.getApp().rootNodeInner == node);
		}

		if (node != null) {
			if (NodeManager.isLeaf(node) && !node.isDept()) {
				view.builderViewByType(NodeView.TYPE_LEAF_NODE);
				Integer state = 0;
				if (forContact) {
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

				// �����µĵ�һ��Ա��
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
				if (name.indexOf("��") > 0) {
					name = name.split("��")[0];
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

				view.setBackgroundResource(R.drawable.dept_node_bg);// /imo_dept_gradual_bg)
			}
		}

		if (selectedPos == position) {
			view.setBackgroundResource(R.drawable.leaf_selected);
		}
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
				if (IMOApp.getApp().userStateMap.get(tempNode.getId()) != null && IMOApp.getApp().userStateMap.get(tempNode.getId()) != 0) {
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
}
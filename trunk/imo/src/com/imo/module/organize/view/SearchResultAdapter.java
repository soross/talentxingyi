package com.imo.module.organize.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.imo.R;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;
import com.imo.network.net.EngineConst;
import com.imo.util.LogFactory;

/**
 * 搜索结果 适配器
 * 
 * @author CaixiaoLong
 *
 */
public class SearchResultAdapter extends BaseAdapter {
	
	/**部门类型：*/
	private int  TYPE_DEPTINFO = 1;
	
	/** 用户类型 */
	private int  TYPE_USERINFO  = 2;
	
	private SearchResultAdapter adapter;
	
	private Context mContext;
	
	private ArrayList<Node> searchResultList = null;
	
	private int selectedPos = -1;
	
	public SearchResultAdapter( Context mContext) {
		adapter = this;
		this.mContext = mContext;
	}
	
	public SearchResultAdapter( Context mContext,ArrayList<Node> searchResultList) {
		adapter = this;
		this.mContext = mContext;
		this.searchResultList = searchResultList;
	}
	
	
	public SearchResultAdapter getAdapter(){
		return adapter;
	}
	
	public void setSelectedPos(int pos){
		this.selectedPos = pos;
	}
	
	/**
	 * 更新需要显示的结果信息
	 * 
	 * @param searchResultList
	 */
	public void setShowData(ArrayList<Node> searchResultList){
		
		this.searchResultList = searchResultList;
		
//		adapter.notifyDataSetChanged();
		
		LogFactory.d("SearchResult", "searchResultList = " + searchResultList.size());
	}
	
	
	/**
	 * 清空数据
	 */
	public void setEmpty(){
		if (searchResultList != null) {
			this.searchResultList.clear();
			adapter.notifyDataSetChanged();
		}
	}
	
	
	
	private int MAX_COUNT = 10;
	
	@Override
	public int getCount() {
		if (searchResultList!= null) {
			return searchResultList.size()>MAX_COUNT ? MAX_COUNT :searchResultList.size();
		}else{
			return 0;
		}
//		return searchResultList!= null ? searchResultList.size() : 0;
	}

	@Override
	public Node getItem(int position) {
		return searchResultList.get(position);
	}
	
//	/**
//	 * 获得节点的类型 [初步的逻辑：从数据库查所有的信息 | 现在的逻辑:从数据库中只查Id]
//	 * 
//	 * @param position
//	 * @return
//	 */
//	public int getType(int position){
//		
//		if (getItem(position) instanceof EmployeeInfoItem) {
//			return TYPE_USERINFO;
//		}else if (getItem(position) instanceof  DeptMaskItem) {
//			return TYPE_DEPTINFO;
//		}
//		return 0;
//	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
//	/**
//	 * Get Object Id 
//	 * 
//	 * @param position
//	 * @return
//	 */
//	public long getObjectId(int position) {
//		
//		if (getType(position) == TYPE_DEPTINFO) {
//			((DeptMaskItem)getItem(position)).getDept_id();
//		}else if (getType(position) == TYPE_USERINFO) {
//			((EmployeeInfoItem)getItem(position)).getUid();
//		}
//		return 0;
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
//		NodeView view = null;
//		NodeData data = null;
//		DeptMaskItem deptMaskItem = null;
//		EmployeeInfoItem employeeInfoItem = null;
//		if (convertView == null) {
//			view = new NodeView(mContext,false); 
//			convertView = view;
//			convertView.setTag(view);
//		}else{
//			view = (NodeView)convertView.getTag();
//		}
////		if (getType(position) == TYPE_DEPTINFO) {
////			deptMaskItem = ((DeptMaskItem)getItem(position));
////			data = new NodeData(deptMaskItem.getName(), "()");
////		}else if (getType(position) == TYPE_USERINFO) {
////			employeeInfoItem = ((EmployeeInfoItem)getItem(position));
////		}
//		return view;

		NodeView view = null;
		Node node = null;
		NodeData data = null;
		
		if (convertView == null) {
			//	使用开关控制后少一部分逻辑操作
			view = new NodeView(mContext,false); 
			convertView = view;
			convertView.setTag(view);
			
//			LogFactory.d("new", "create a new Vie");
		}else{
			view = (NodeView)convertView.getTag();
		}
		
		node = searchResultList.get(position);
		
		if (node.getId() == EngineConst.uId) {
			view.isMySelfNode = true;
		}else{
			view.isMySelfNode = false;
		}
		
		if (node == null) {
			LogFactory.d("SearchResult-getView", "Node == null postion: " + position);
		}
		
		LogFactory.d("SearchResult-getView", "NodeName = " + node.getNodeData().nodeName  + "\t postion = " + position);
		
		data = node.getNodeData();
		
		if(node != null){
			if(!node.isDept()){
				view.builderViewByType(NodeView.TYPE_LEAF_NODE);
				view.setLNData(data,node.getOnLineState());
				view.setBackgroundResource(R.drawable.leaf_node_bg);
				
				//////TriangleClickListener  
				Bundle dataBundle = null;
				dataBundle = new Bundle();
				dataBundle.putInt("cid", node.getCid());
				dataBundle.putInt("uid", node.getId());
				dataBundle.putString("name", node.getNodeData().nodeName);
				dataBundle.putBoolean("sex", node.getNodeData().isBoy);
				view.setOnTriangleClickListener(dataBundle);
				
			}else{ 
				view.builderViewByType(NodeView.TYPE_PARENT_NODE);
				view.setPNData(data);
				view.updateNodeState(node.getNodeState());
//				view.setBackgroundResource(R.drawable.parent_node_bg);
				view.setBackgroundResource(R.drawable.imo_dept_gradual_bg);
			}
//			view.setPadding((node.getLevel())*20, 3,3, 3);
		}
		
		if (selectedPos == position) {
			view.setBackgroundResource(R.drawable.leaf_selected);
		}
		
		return view;
	
	}
	

}

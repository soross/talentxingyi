package com.imo.module.organize.struct;

import java.util.ArrayList;

import com.imo.network.net.EngineConst;

import android.R.integer;

/**
 * 节点数据结构
 * 
 * @author CaixiaoLong
 * 
 */
public class Node {

	private int id = -1;

	private int cid = EngineConst.cId;

	private boolean isDept = false;

	private boolean nodeState = false;

	private boolean needShow = true;

	private int onLineState = 0;

	private NodeData nodeData;

	private Node parentNode;

	private ArrayList<Node> childNodes = new ArrayList<Node>();

	public Node() {
	}

	public Node(NodeData nodeData) {
		this.nodeData = nodeData;
	}

	public boolean isNeedShow() {
		return needShow;
	}

	public void setNeedShow(boolean needShow) {
		this.needShow = needShow;
	}

	public int getOnLineState() {
		return onLineState;
	}

	public void setOnLineState(Integer onLineState) {
		if (onLineState == null)
			return;
		this.onLineState = onLineState;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isDept() {
		return isDept;
	}

	public void setDept(boolean isDept) {
		this.isDept = isDept;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	/**
	 * 获得当前节点的状态：
	 * 
	 * @return
	 */
	public boolean getNodeState() {
		return nodeState;
	}

	public void setNodeState(boolean nodeState) {
		this.nodeState = nodeState;
	}

	public NodeData getNodeData() {
		return nodeData;
	}

	public void setNodeData(NodeData nodeData) {
		this.nodeData = nodeData;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public ArrayList<Node> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(ArrayList<Node> childNodes) {
		this.childNodes = childNodes;
	}

	/**
	 * 获得节点的级数
	 * 
	 * @return
	 */
	public int getLevel() {
		return null == parentNode ? 0 : parentNode.getLevel() + 1;
	}

	@Override
	public String toString() {

		return "" + nodeData.nodeName;
	}

}

package com.imo.module.organize.struct;

/**
 * 节点数据 类
 * 
 * @author CaixiaoLong
 * 
 */
public class NodeData {

	// 1-2
	public String nodeName;

	// 1-统计结果
	public String statistics;

	// 1.1需要显示图片的时候使用[for next version ]
	public int iconId;

	// 2.1-当前叶子节点的性别
	public boolean isBoy = true;

	public NodeData() {

	}

	/**
	 * for parent node
	 * 
	 * @param nodeName
	 * @param statistics
	 */
	public NodeData(String nodeName, String statistics) {
		this.nodeName = nodeName;
		this.statistics = statistics;
	}
	public NodeData(String deptName) {
		this.nodeName = deptName;
	}

	/**
	 * for leaf node
	 * 
	 * @param isBoy
	 * @param nodeName
	 */
	public NodeData(boolean isBoy, String nodeName) {
		this.isBoy = isBoy;
		this.nodeName = nodeName;
	}

	/**
	 * for next version
	 * 
	 * @param nodeName
	 * @param iconId
	 */
	public NodeData(boolean isBoy, String nodeName, int iconId) {
		this.isBoy = isBoy;
		this.nodeName = nodeName;
		this.iconId = iconId;
	}

	@Override
	public String toString() {
		return "NodeData [nodeName=" + nodeName + ", isboy = " + isBoy
				+ "]";
	}

	
	
}

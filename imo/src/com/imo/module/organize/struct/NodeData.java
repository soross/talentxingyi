package com.imo.module.organize.struct;

/**
 * �ڵ����� ��
 * 
 * @author CaixiaoLong
 * 
 */
public class NodeData {

	// 1-2
	public String nodeName;

	// 1-ͳ�ƽ��
	public String statistics;

	// 1.1��Ҫ��ʾͼƬ��ʱ��ʹ��[for next version ]
	public int iconId;

	// 2.1-��ǰҶ�ӽڵ���Ա�
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

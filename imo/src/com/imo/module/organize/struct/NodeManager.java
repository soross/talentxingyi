package com.imo.module.organize.struct;


/**
 * Node 管理类: 
 * 
 * @author CaixiaoLong
 * 
 */
public class NodeManager {
	
    /**
     * 是否为根节点
     * 
     * @return
     */
    public static boolean isRoot(Node node){
    	
    	return node.getParentNode() == null ? true : false;
    }
    
    
    /**
     * 是否为叶子节点
     * 
     * @return
     */
    public static boolean isLeaf(Node node){
    	
    	return node.getChildNodes().size() == 0 ? true : false;
    }
    
    
    /**
     * 给curNode节点添加子节点
     * <br>
     * @param curNode
     * @param childNodes
     */
    public static void addChildNode(Node curNode ,Node childNodes){
    	
    	if(!curNode.getChildNodes().contains(childNodes)){
    		childNodes.setParentNode(curNode);
    		curNode.getChildNodes().add(childNodes);
    	}
    }
    
    /**
     * 清除curNode节点所有子节点
     *
     * @param curNode 
     */
    public static void clearChildNode(Node curNode){
    	
    	curNode.getChildNodes().clear();
    }

    /**
     * 从curNode节点中删除一个childNode节点
     * 
     * @param curNode
     * @param childNode
     * @return
     */
    public static Boolean remove(Node curNode,Node childNode){
    	
    	if(curNode.getChildNodes().contains(childNode)){
    		curNode.getChildNodes().remove(childNode);
    		return true;
    	}
    	return false;
    }
    
    /** 
     * 删除当前节点指定位置的子节点
     * 
     * 
     * @param curNode
     * @param location
     * @return
     */
    public static Boolean remove(Node curNode,int location){
    	
    	if (curNode.getChildNodes().size()>location) {
    		curNode.getChildNodes().remove(location);
    		return true;
		}
    	return false;
    }
    
    
    /**
     *目标节点是否为当前节点的父节点。
     * @param curNode 
     * @param targetNode
     * @return
     */
    public static boolean isParent(Node curNode,Node targetNode){
    	
    	if(null==curNode.getParentNode())return false; 
    	
    	if(curNode.getParentNode() == targetNode)return true;
    	
    	return isParent(curNode.getParentNode(), targetNode);
    }
    
    
    /**
     * 父节点是否处于折叠状态
     * 
     * @return true:表示折叠状态  ； false:表示展开状态。
     */
    public static boolean isParentCollapsed(Node node){
    	
    	if(null == node.getParentNode()) return !node.getNodeState();
    	
    	if(node.getParentNode().getNodeState()== false) return true;
    	
    	return isParentCollapsed(node.getParentNode());
    	
    }

}

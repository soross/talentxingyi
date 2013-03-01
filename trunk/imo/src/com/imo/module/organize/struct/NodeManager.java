package com.imo.module.organize.struct;


/**
 * Node ������: 
 * 
 * @author CaixiaoLong
 * 
 */
public class NodeManager {
	
    /**
     * �Ƿ�Ϊ���ڵ�
     * 
     * @return
     */
    public static boolean isRoot(Node node){
    	
    	return node.getParentNode() == null ? true : false;
    }
    
    
    /**
     * �Ƿ�ΪҶ�ӽڵ�
     * 
     * @return
     */
    public static boolean isLeaf(Node node){
    	
    	return node.getChildNodes().size() == 0 ? true : false;
    }
    
    
    /**
     * ��curNode�ڵ�����ӽڵ�
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
     * ���curNode�ڵ������ӽڵ�
     *
     * @param curNode 
     */
    public static void clearChildNode(Node curNode){
    	
    	curNode.getChildNodes().clear();
    }

    /**
     * ��curNode�ڵ���ɾ��һ��childNode�ڵ�
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
     * ɾ����ǰ�ڵ�ָ��λ�õ��ӽڵ�
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
     *Ŀ��ڵ��Ƿ�Ϊ��ǰ�ڵ�ĸ��ڵ㡣
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
     * ���ڵ��Ƿ����۵�״̬
     * 
     * @return true:��ʾ�۵�״̬  �� false:��ʾչ��״̬��
     */
    public static boolean isParentCollapsed(Node node){
    	
    	if(null == node.getParentNode()) return !node.getNodeState();
    	
    	if(node.getParentNode().getNodeState()== false) return true;
    	
    	return isParentCollapsed(node.getParentNode());
    	
    }

}

package com.chinaandroiddev.javaeyeclient.model;

import java.util.ArrayList;
import java.util.Date;
/**
 * 
 * @author mqqqvpppm
 *
 */
public class FavoriteItem {
//the following is javaeye doc for FavoriteItem json-formatted	
//	id:     收藏ID  
//	url:        收藏的链接  
//	title:      收藏的标题  
//	description:    描述  
//	cached_tag_list:标签（多个标签用半角英文逗号隔开）  
//	public:     代表是公开收藏还是私人收藏  
//	created_at: 添加该收藏的时间  
	public long id;  
	public String url;  
	public String title;  
	public String description;  
	public ArrayList<String> categoryNames;
	public boolean isPublic = true;  
	public Date createdTime;
}

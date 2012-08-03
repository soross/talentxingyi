package com.chinaandroiddev.javaeyeclient.model;
/**
 * 
 * @author mqqqvpppm
 *
 */
import java.util.Date;
//the following is javaeye doc for update json-formatted
//id:         闲聊ID  
//created_at:     创建时间  
//body:       闲聊内容  
//user:       闲聊发布者，属性如下  
//    name:   用户名  
//    logo:   头像文件路径（最大宽高150x150），如果要获取缩略图，路径为xxx-thumb.jpg（最大宽高48x48）  
//    domain: 用户的博客子域名  
//receiver:   闲聊接收者，属性同user，可能为空  
//reply_to_id:    回复某条闲聊ID，可能为空  
//via:        通过什么发布  

/**
 * 
 * @author mqqqvpppm
 *
 */
public class Update {
	public long id = NULL_ID;
	public Date createdTime;
	public String body;
	public User user = new User();
	public User receiver = new User();
	public long replyToId = NULL_ID;
	public String via = "JEAndroid_Client";
	
	public static final long NULL_ID = -1;

}

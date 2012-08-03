package com.chinaandroiddev.javaeyeclient.model;

import java.util.Date;
/**
 * 
 * @author mqqqvpppm
 *
 */
public class Message {
	
//the following is javaeye doc for Message json-formatted
//	id:         短信ID  
//	created_at:     创建时间  
//	system_notice:  是否系统短信的标记  
//	has_read:   是否已读的标记  
//	attach:     是否有附件的标记  
//	title:      短信标题  
//	plain_body: 短信内容（已经去除HTML标签）  
//	sender:     短信发送者，属性如下  
//	    name:   用户名  
//	    logo:   头像文件路径（最大宽高150x150），如果要获取缩略图，路径为xxx-thumb.jpg（最大宽高48x48）  
//	    domain: 用户的博客子域名  
//	receiver:   短信接收者，属性同sender
	public long id = -1;
	public Date createdTime;
	public boolean isSystem;
	public boolean isRead;
	public boolean isAttached;
	public String title;
	public String body;
	public User sender = new User();
	public User receiver = new User();
	public long replyId = -1;
}

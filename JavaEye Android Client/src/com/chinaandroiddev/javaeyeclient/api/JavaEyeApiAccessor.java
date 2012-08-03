package com.chinaandroiddev.javaeyeclient.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.chinaandroiddev.javaeyeclient.model.FavoriteItem;
import com.chinaandroiddev.javaeyeclient.model.JEException;
import com.chinaandroiddev.javaeyeclient.model.Message;
import com.chinaandroiddev.javaeyeclient.model.Update;
import com.chinaandroiddev.javaeyeclient.model.User;
import com.chinaandroiddev.javaeyeclient.model.VerifiedInfo;


/**
 * 
 * @author mqqqvpppm
 *
 */
public class JavaEyeApiAccessor {
//	public static String userName;
//	public static String password;
	public static User user;
	private static final String LOG_TAG = "JavaEyeApiAccessor";
	//verify api
	public static VerifiedInfo verify() throws Exception{
        String url = "http://api.javaeye.com/api/auth/verify";
        String jsonString = null;
        try {
        	jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);            
        } catch (JEException e) {//如果登录信息不正确的话, 会出现这样的exception:"received authentication challenge is null"
        	if(e.getMessage().startsWith("java.io.IOException: Received authentication challenge is null")){
        		jsonString = "error.auth.fail";
//        		Log.e(LOG_TAG, e.getMessage());        		
        	}else{
        		throw e;
        	}
        }
        VerifiedInfo ret = new VerifiedInfo();
        if (jsonString.equals("error.auth.fail")) {
            ret.verifyCode = VerifiedInfo.VERIFY_ERROR;
            ret.verifyMessage = "登录失败! 请核对用户名和密码";
        } else if (jsonString.equals("error.auth.over.limit ")) {
            ret.verifyCode = VerifiedInfo.VERIFY_ERROR;
            ret.verifyMessage = "登录次数过多, 请稍后再试!";
        } else {
            JSONObject json = new JSONObject(jsonString);
            ret.domain = json.getString("domain");
            ret.name = json.getString("name");
            ret.id = json.getLong("id");
            ret.verifyCode = VerifiedInfo.VERIFY_SUCCESS;
        }
        return ret;
    }
	
	//twitter
	public static ArrayList<Update> getFollowed(long lastId, int pageNo) throws Exception{
		String url = "http://api.javaeye.com/api/twitters/list";			
		return getUpdatesList(url,lastId,pageNo);		
	}

	public static ArrayList<Update> getReplies(long lastId, int pageNo) throws Exception{
		String url = "http://api.javaeye.com/api/twitters/replies";
		return getUpdatesList(url,lastId,pageNo);		
	}
	
	public static ArrayList<Update> getAllUpdates(long lastId, int pageNo) throws Exception{
		String url = "http://api.javaeye.com/api/twitters/all";
		return getUpdatesList(url,lastId,pageNo);	
	}
	
	
	private static ArrayList<Update> getUpdatesList(String url,long lastId, int pageNo) throws Exception{
		url = appendParams(url, lastId, pageNo);
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);
//		Log.e(LOG_TAG, "json: " + jsonString);
		JSONArray jsonArray = new JSONArray(jsonString);		
		ArrayList<Update> ret = new ArrayList<Update>();
		for( int i = 0; i != jsonArray.length(); i++){
			JSONObject json = jsonArray.getJSONObject(i);
			Update update = jsonToUpdate(json);
			ret.add(update);
		}
		return ret;
	}
	
	
	private static Update jsonToUpdate(JSONObject json) throws JSONException{
		Update update = new Update();
		update.body = json.getString("body");
		update.createdTime = new Date(json.getString("created_at"));// json.get
		update.id = json.getLong("id");
//		String s = json.getString("reply_to_id");
		if(!json.isNull("reply_to_id")){
			update.replyToId = json.getLong("reply_to_id");
		}
		update.via = json.getString("via");
		
		
		JSONObject user = json.getJSONObject("user");
		update.user.domain = user.getString("domain");
		update.user.logo = user.getString("logo");
		update.user.name = user.getString("name");
		
		if(!json.isNull("receiver")){
			JSONObject receiver = json.getJSONObject("receiver");
			update.receiver.domain = receiver.getString("domain");
			update.receiver.logo = receiver.getString("logo");
			update.receiver.name = receiver.getString("name");
		}
		return update;
	}

	public static boolean createUpdate(Update update) throws Exception{
//		StringBuffer url = new StringBuffer("http://api.javaeye.com/api/twitters/create?");
//		url.append("body=").append(update.body);
//		if(update.replyToId != Update.NULL_ID){
//			url.append("&reply_to_id=").append(update.replyToId);
//		}
//		if(update.via != null && !update.via.isEmpty()){
//			url.append("&via").append(update.via);
//		}
		String url = "http://api.javaeye.com/api/twitters/create";
		HashMap map = new HashMap(); 
		map.put("body", update.body);
		if(update.replyToId != Update.NULL_ID){
			map.put("reply_to_id", String.valueOf(update.replyToId));
		}
		if(update.via != null && !update.via.equals("")){
			map.put("via", update.via);
		}
		String temp = BaseAuthenicationHttpClient.doRequest(url.toString(), user.name, user.password, map);
		if(temp.startsWith("error")){
			return false;
		}
		return true;
	}
	
	public static boolean deleteUpdate(Update update) throws Exception{
		String url = new String("http://api.javaeye.com/api/twitters/destroy?id="+update.id);		
		String temp = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);
		if(temp.startsWith("error")){
			return false;
		}
		return true;
	}
	

	public static Update getUpdateById(long id) throws Exception{
		String url = new String("http://api.javaeye.com/api/twitters/show?id="+id);
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);
		if(jsonString.startsWith("error")){
			return null;
//			return false;
		}		
		return jsonToUpdate(new JSONObject(jsonString));
	}
	
	public static ArrayList<Update> getUpdatesByIds(long[] ids) throws Exception{
		ArrayList<Update> ret = new ArrayList<Update>();
		String url = "http://api.javaeye.com/api/twitters/show";
		StringBuffer buf = new StringBuffer();
		for(long i : ids){			
			buf.append(",").append(i);			
		}
		buf.deleteCharAt(0);
		HashMap map = new HashMap();
		map.put("id", buf.toString());
		String jsonString = BaseAuthenicationHttpClient.doRequest(url.toString(), user.name, user.password,map);
		if(jsonString.startsWith("error")){
			return ret;
		}

		JSONArray jsonArray = new JSONArray(jsonString);		
		for( int i = 0; i != jsonArray.length(); i++){
			JSONObject json = jsonArray.getJSONObject(i);
			Update update = jsonToUpdate(json);
			ret.add(update);
		}
		
		return ret;
	}
//twitter end

	
	
// favorite api
	public static ArrayList<FavoriteItem> getFavorites() throws Exception{
		String url = "http://api.javaeye.com/api/user_favorites/list";
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);
		ArrayList<FavoriteItem> ret= new ArrayList<FavoriteItem>();
		JSONArray jsonArray = new JSONArray(jsonString);		
		for( int i = 0; i != jsonArray.length(); i++){
			JSONObject json = jsonArray.getJSONObject(i);
			FavoriteItem item = jsonToFavorite(json);			
			ret.add(item);
		}		
		return ret;
	}
	
	private static FavoriteItem jsonToFavorite(JSONObject json) throws JSONException {
		FavoriteItem item = new FavoriteItem();
		item.id = json.getLong("id");
		item.createdTime = new Date(json.getString("created_at"));
		item.description = json.getString("description");
		item.isPublic = json.getBoolean("public");
		item.title = json.getString("title");
		item.url = json.getString("url");
		item.categoryNames = new ArrayList<String>();
		//for java eye api bug, some times json will be description:"null"
		if(item.description != null && item.description.trim().equals("null")){
			item.description = "";
		}
		String temp = json.getString("cached_tag_list");
		if (temp==null || temp.length()==0 || temp.equals("null")) {
		    // bad tags
		} else {
//		    Log.e("@@@@", "temp: @@" + temp + "@@");
    		String[] names = temp.split(",");
    		item.categoryNames = new ArrayList<String>();
    		for(String name: names){
    			item.categoryNames.add(name.trim());
    		}
		}
		return item;
	}

	public static FavoriteItem addFavorite(FavoriteItem item) throws Exception{
		String url = "http://api.javaeye.com/api/user_favorites/create";
		HashMap params = new HashMap();
		params.put("url", item.url);
		params.put("title", item.title);
		params.put("share", String.valueOf(item.isPublic));
		if(item.description != null && !item.description.equals("")){
			params.put("description", item.description);
		}
		if(!item.categoryNames.isEmpty()){
//			url.append("&tag_list=");
			StringBuffer buf = new StringBuffer();
			for(String name : item.categoryNames){
				buf.append(",").append(name);
			}
			buf.deleteCharAt(0);
			params.put("tag_list", buf.toString());
		}
	
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password, params);
		
		return jsonToFavorite(new JSONObject(jsonString));
	}
	
	
	public static FavoriteItem updateFavorite(FavoriteItem item) throws Exception{
		String url = "http://api.javaeye.com/api/user_favorites/update";	
		HashMap params = new HashMap();
		params.put("id",String.valueOf(item.id));
		params.put("url", item.url);
		params.put("title", item.title);
		params.put("share", String.valueOf(item.isPublic));
		if(item.description != null && !item.description.equals("")){
			params.put("description", item.description);
		}
		if(!item.categoryNames.isEmpty()){
//			url.append("&tag_list=");
			StringBuffer buf = new StringBuffer();
			for(String name : item.categoryNames){
				buf.append(",").append(name);
			}
			buf.deleteCharAt(0);
			params.put("tag_list", buf.toString());
		}
		String jsonString = BaseAuthenicationHttpClient.doRequest(url.toString(), user.name, user.password,params);
		if(jsonString.startsWith("error")){
			return null;
		}
		return jsonToFavorite(new JSONObject(jsonString));
	}

	public static boolean deleteFavorite(FavoriteItem item) throws Exception{
		String url = "http://api.javaeye.com/api/user_favorites/destroy?id="+item.id;
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);
		if(jsonString.startsWith("error")){
			return false;
		}
		return true;
	}
//favorite end

	
//message start	
	public static ArrayList<Message> inBox(long lastId, int page) throws Exception{
		String url = "http://api.javaeye.com/api/messages/inbox";
		url = appendParams(url,lastId,page);
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);
		ArrayList<Message> ret = new ArrayList<Message>();
		JSONArray array = new JSONArray(jsonString);
		for(int i=0; i!= array.length() ; i++){
			JSONObject json = array.getJSONObject(i);
			Message msg =  jsonToMessage(json);
			ret.add(msg);
		}
		return ret;
	}

	private static Message jsonToMessage(JSONObject json) throws JSONException {
		Message msg = new Message();
		msg.body = json.getString("plain_body");
		msg.createdTime = new Date(json.getString("created_at"));
		msg.id = json.getLong("id");
		msg.isAttached = json.getBoolean("attach");
		msg.isRead = json.getBoolean("has_read");
		msg.isSystem = json.getBoolean("system_notice");
		msg.title=json.getString("title");
		JSONObject temp = json.getJSONObject("receiver");
		msg.receiver.domain = temp.getString("domain");
		msg.receiver.logo = temp.getString("logo");
		msg.receiver.name = temp.getString("name");
		temp = json.getJSONObject("sender");
		msg.sender.domain = temp.getString("domain");
		msg.sender.logo = temp.getString("logo");
		msg.sender.name = temp.getString("name");
		return msg;
	}

	public static Message sendMessage(Message message) throws Exception{
		String url = "http://api.javaeye.com/api/messages/create";
		HashMap map = new HashMap();
		map.put("title", message.title);
		map.put("body", message.body);
		map.put("receiver_name", message.receiver.name);
//		url.append("title=").append(message.title);
//		url.append("&body=").append(message.body);
//		url.append("&receiver_name=").append(message.receiver.name);
		
		String jsonString = BaseAuthenicationHttpClient.doRequest(url.toString(), user.name, user.password,map);
		if(jsonString.startsWith("error")){
			return null;
		}
		JSONObject json = new JSONObject(jsonString);		
		return jsonToMessage(json);
	}

	public static Message replyMessage(Message message) throws Exception{
		StringBuffer url = new StringBuffer("http://api.javaeye.com/api/messages/reply?");
		HashMap map = new HashMap();
		map.put("title", message.title);
		map.put("body", message.body);
		map.put("id", String.valueOf(message.replyId));
//		url.append("title=").append(message.title);
//		url.append("&body=").append(message.body);
//		url.append("&id=").append(message.replyId);
		
		String jsonString = BaseAuthenicationHttpClient.doRequest(url.toString(), user.name, user.password, map);
		if(jsonString.startsWith("error")){
			return null;
		}
		JSONObject json = new JSONObject(jsonString);		
		return jsonToMessage(json);
	}
	
	public static boolean deleteMessage(Message message) throws Exception{
		String url = "http://api.javaeye.com/api/messages/destroy?id=" + message.id;		
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, user.name, user.password);
		if(jsonString.startsWith("error")){
			return false;
		}
		return true;
	}
	//message end
	
	private static String appendParams(String url, long lastId, int pageNo) {
		if(lastId != -1){
			url = "?last_id=" + lastId;
		}
		if(pageNo != -1){
			url = "&pageNo=" + pageNo;
		}
		return url;
	}
	
	public static void main(String[] args){
		try {
//			userName = "mqqqvpppm";
//			password = "******";
//			Update update = new Update();
//			update.body = "sns社区:)";
			
//			Object ret = create(update);
//			Object ret = delete(update);
//			Object ret = getUpdateById(35843);
//			long[] ids = {35843,35840};
//			Object ret = getUpdatesByIds(ids);
//			Object ret = getFavorites();
//				FavoriteItem item = new FavoriteItem();
//				item.url="http://code.google.com";
//				item.isPublic = true;
//				item.title = "google code";
//				item.description="我式了玩";
//				ArrayList list = new ArrayList();
//				list.add("app");
//				item.categoryNames = list;
//				item.id = 16140;
//			Object ret = addFavorite(item);
//			item.id=11157;
//			Object ret = delete(item);
//			Object ret = updateFavorite(item);	
				
//			Object ret = inBox(-1,-1);
//			Message m = new Message();
//			m.id = 2420476;
//			Object ret = deleteMessage(m);
			
//			Message m = new Message();
//			m.body="只是试试";
//			m.title="test for android client";
//			m.receiver.name = "lordhong";
//			
//			Object ret = sendMessage(m);
//			System.out.println(ret);
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
}




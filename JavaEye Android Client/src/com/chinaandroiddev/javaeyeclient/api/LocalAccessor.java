package com.chinaandroiddev.javaeyeclient.api;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chinaandroiddev.javaeyeclient.model.FavoriteItem;
import com.chinaandroiddev.javaeyeclient.model.Message;
import com.chinaandroiddev.javaeyeclient.model.User;
import com.chinaandroiddev.javaeyeclient.util.IOUtils;
import com.chinaandroiddev.javaeyeclient.util.StringUtils;




public class LocalAccessor{

    private static final String LOG_TAG = "LocalAccessor";
	private static final String DATABASE_NAME = "javaeyeclient.db";	

	
	private static final String SQL_CREATE_TABLE_FAVORITE_ITEM = "CREATE TABLE IF NOT EXISTS FavoriteItem("
    + "id INTEGER PRIMARY KEY,"
    + "url TEXT,"
    + "title TEXT,"
    + "description TEXT,"
    + "categoryNames TEXT," // list of String, use "," as token
    + "createdTime TEXT,"
    + "ispublic BOOL"
    + ");";

	private static final String SQL_CREATE_TABLE_MESSAGE = "CREATE TABLE IF NOT EXISTS Message("
    + "id INTEGER PRIMARY KEY,"
    + "body TEXT,"
    + "title TEXT,"
    + "senderName TEXT,"
    + "senderLogo TEXT,"
    + "senderDomain TEXT,"
    + "receiverName TEXT,"
    + "receiverLogo TEXT,"
    + "receiverDomain TEXT,"
    + "replyId INTEGER,"
    + "isSystem BOOL,"
    + "isRead BOOL,"
    + "isAttached BOOL,"
    + "createdTime TEXT"
    + ");";

	private SharedPreferences prefs = null;	
	private Context ctx;
	
	

	private LocalAccessor(Context ctx){
		this.ctx = ctx;
		prefs = ctx.getSharedPreferences(IOUtils.PREFS_FILE, Context.MODE_PRIVATE);
		SQLiteDatabase db = openDB();
		db.execSQL(SQL_CREATE_TABLE_FAVORITE_ITEM);
		db.execSQL(SQL_CREATE_TABLE_MESSAGE);
		db.close();
//		testDBMessage();
//		testDBFavoriteItem();
	}	

	static private LocalAccessor accessor; 
	
	public static LocalAccessor getInstance(Context context){		
		if(accessor == null){
			accessor = new LocalAccessor(context);
		}
		return accessor;
	}
	
	private SQLiteDatabase openDB(){		
		return ctx.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
	}
	
	
	//insert or update(if exist) user in SharedPreferences
	public boolean updateUser(User user) throws Exception{
		SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", user.name);
        editor.putString("password", user.password);
        editor.putLong("id", user.id);
        editor.commit();
		return true;
	}
	
	//return null if no user saved
	public User getUser(){
		User ret = new User();
        ret.name = prefs.getString("username", null);
        ret.password = prefs.getString("password", null);
        ret.id = prefs.getLong("id", 0);
		return ret;
	}	
	
	//insert or update(if exist) Favorte Item in db
	public boolean updateFavoriteItem(FavoriteItem item) throws Exception{
		ContentValues values = new ContentValues();		
		values.put("url", item.url);
		values.put("title", item.title);
		values.put("description", item.description);
		values.put("isPublic", item.isPublic);
		values.put("createdTime", item.createdTime.toLocaleString());
		values.put("categoryNames", StringUtils.combineByToken(item.categoryNames,","));
		
		SQLiteDatabase db = openDB();
		if(this.getFavoriteItem(item.id)== null){//insert
			values.put("id", item.id);
			db.insertOrThrow("FavoriteItem", null, values);			
		}else{//update
		    db.update("FavoriteItem", values, "id=" + item.id, null);			
		}		
		db.close();
		return true;
	}
	
	public ArrayList<FavoriteItem> getFavoritesByPage(int pageNo, int size) throws Exception{
		SQLiteDatabase db = openDB();		
        Cursor c = db.query("FavoriteItem", null, null, null, null, null, "id");  
        
        ArrayList<FavoriteItem> ret = new ArrayList<FavoriteItem>();		
        int begin = c.getCount() - (pageNo - 1) * size - 1;
        if(c.moveToPosition(begin)){
        	ret.add(buildFavorite(c));
        }
        int i = 0;
        while(i != size - 1 && c.moveToPrevious()) {
        	ret.add(buildFavorite(c));
        	i++;
        }
        
        c.close();
        db.close();
        return ret;	
   }
	
	
	private FavoriteItem buildFavorite(Cursor c) {
		FavoriteItem ret = new FavoriteItem();            
        ret.id = c.getLong(0);
        ret.url = c.getString(1); 
        ret.title = c.getString(2);
        ret.description = c.getString(3);
        ret.categoryNames = StringUtils.spliteByToken(c.getString(4),",");
        ret.createdTime = new Date(c.getString(5));
        ret.isPublic = c.getInt(6) == 1 ? true:false;
		return ret; 
	}


	//return null if no item found
	public FavoriteItem getFavoriteItem(long id) throws Exception{
		FavoriteItem ret = null;
		SQLiteDatabase db = openDB();
        Cursor c = db.query("FavoriteItem", null, "id="+id, null, null, null, null);        
        if (c.moveToFirst()) {            
            ret = buildFavorite(c);
        }
        c.close();
        db.close();
        return ret;
	
	}	
	public int deleteFavoriteItem(long id) throws Exception{
		SQLiteDatabase db = openDB();
		int count = db.delete("FavoriteItem", "id="+id, null );
		db.close();
		return count;
	}
	
	public void clearFavorites() {
		SQLiteDatabase db = openDB();		
		db.delete("FavoriteItem", null, null);
		db.close();
		
	}
	
	public boolean isFavoritesEmpty(){
		SQLiteDatabase db = openDB();		
		Cursor c = db.query("FavoriteItem", null, null, null, null, null, null);
		boolean ret = c.getCount() == 0 ? true : false ;
		c.close();
		db.close();
		return ret;
	}

	//insert or update(if exist) Favorte Item
	public boolean updateMessage(Message message) throws Exception{
		ContentValues values = new ContentValues();
		values.put("body", message.body);
		values.put("title", message.title);
		values.put("senderName", message.sender.name);
		values.put("senderLogo", message.sender.logo);
		values.put("senderDomain", message.sender.domain);
		values.put("receiverName", message.receiver.name);
		values.put("receiverLogo", message.receiver.logo);
		values.put("receiverDomain", message.receiver.domain);
		values.put("replyId", message.replyId);
		values.put("isSystem", message.isSystem);
		values.put("isRead", message.isRead);
		values.put("isAttached", message.isAttached);
		values.put("createdTime", message.createdTime.toLocaleString());
		
		SQLiteDatabase db = openDB();
		if(this.getMessage(message.id)== null){//insert
			values.put("id", message.id);
			db.insertOrThrow("Message", null, values);			
		}else{//update
		    db.update("Message", values, "id=" + message.id, null);			
		}
		db.close();
		return true;
	}
	//return null if no Message found
	public Message getMessage(long id) throws Exception{
		Message ret = null;
		SQLiteDatabase db = openDB();
        Cursor c = db.query("Message", null, "id="+id, null, null, null, null);
        
        if (c.moveToFirst()) {            
            ret = new Message();
            ret.id = c.getLong(0);
            ret.body = c.getString(1);
    		ret.title = c.getString(2);
    		ret.sender.name = c.getString(3);
    		ret.sender.logo = c.getString(4);
    		ret.sender.domain = c.getString(5);
    		ret.receiver.name = c.getString(6);
    		ret.receiver.logo = c.getString(7);
    		ret.receiver.domain = c.getString(8);
    		ret.replyId = c.getLong(9);
    		ret.isSystem = c.getInt(10) == 1 ? true : false;
    		ret.isRead = c.getInt(11) == 1 ? true : false;
    		ret.isAttached = c.getInt(12) == 1 ? true : false;
    		ret.createdTime = new Date(c.getString(13));    		
        }
        
        c.close();
        db.close();
        return ret;	
	}
	
	public int deleteMessage(long id) throws Exception{
		SQLiteDatabase db = openDB();
		int count = db.delete("Message", "id=" + id, null);
		db.close();
		return count;
	}
	

	

	private void testDBMessage() {
		Message message = new Message();
		message.id = 1;
		message.body = "message body";
		message.title = "my title";
		message.sender.name= "li xuechen";
		message.sender.logo = "li xuechen' logo";
		message.sender.domain = "li xuechen's domain";
		message.receiver.name = "大美女";
		message.receiver.logo = "大美女的logo";
		message.receiver.domain = "大美女的domain";
		message.replyId = -1;
		message.isSystem = false;
		message.isRead = false;
		message.isAttached = false;
		message.createdTime = new Date();		
		
		try {
			updateMessage(message);			
			Message temp0 = getMessage(1);
			temp0.title = "i changed it";
			temp0.isRead = true;
			updateMessage(temp0);
			Message item3 = this.getMessage(1);
			int count = this.deleteMessage(1);
//			int count2 =this.deleteMessage(1);
			Message item4 = this.getMessage(1);
			item4 = null;
		} catch (Exception e) {
//			Log.e(LOG_TAG, e.getMessage());
		}
	}
	
	private void testDBFavoriteItem() {
		FavoriteItem item = new FavoriteItem();
		item.id = 1;
		item.isPublic = true;
		item.title = "my title";
		item.url = "http://www.google.com";
		item.createdTime = new Date();
		item.description = "我的标签";
		item.categoryNames = new ArrayList<String>();
		item.categoryNames.add("niceinc");
		item.categoryNames.add("mytype");
		
		try {
			this.updateFavoriteItem(item);
			FavoriteItem item2 = this.getFavoriteItem(1);
			item2.title = "i changed it";
			item2.isPublic = false;
			this.updateFavoriteItem(item2);
			FavoriteItem item3 = this.getFavoriteItem(1);
			int count = this.deleteFavoriteItem(1);
//			int count2 =this.deleteFavoriteItem(1);
			FavoriteItem item4 = this.getFavoriteItem(1);
			item4 = null;
		} catch (Exception e) {
//			Log.e(LOG_TAG, e.getMessage());
		}
	}



	
}

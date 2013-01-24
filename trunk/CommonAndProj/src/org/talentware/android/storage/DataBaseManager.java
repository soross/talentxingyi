package org.talentware.android.storage;

import org.talentware.android.util.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseManager extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	private static final String TAG = DataBaseManager.class.getSimpleName();

	public DataBaseManager(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);

		Logger.d(TAG, "Method Construct");
	}

	public DataBaseManager(Context context, String name) {
		this(context, name, VERSION);
	}

	public DataBaseManager(Context context, String name, int version) {
		this(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Logger.d(TAG, "CallBack onCreate");
		db.execSQL("create table user(id int,name varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Logger.d(TAG, "CallBack onUpgrade");
	}

}

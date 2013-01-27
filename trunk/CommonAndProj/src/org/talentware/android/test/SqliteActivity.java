package org.talentware.android.test;

import org.talentware.android.storage.DataBaseManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class SqliteActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void insertData() {
		ContentValues contentValues = new ContentValues();
		contentValues.put("id", 1);
		contentValues.put("name", "BruceZhang");
		DataBaseManager dbHelper = new DataBaseManager(SqliteActivity.this, "BruceZhang_sqlite");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insert("user", null, contentValues);
	}

	private void queryData() {
		DataBaseManager daHelper = new DataBaseManager(SqliteActivity.this, "BruceZhang_sqlite");
		SQLiteDatabase db = daHelper.getReadableDatabase();
		Cursor cursor = db.query("user", new String[] { "id", "name" }, "id=?", new String[] { "1" }, null, null, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("name"));
		}

		cursor.close();
	}

	private void updateData() {
		DataBaseManager daHelper = new DataBaseManager(SqliteActivity.this, "BruceZhang_sqlite");
		SQLiteDatabase dbDatabase = daHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", "ZhangHu");
		dbDatabase.update("user", contentValues, "id=?", new String[] { "1" });
	}

	private void updateDataBase() {
		DataBaseManager dbBaseHelper = new DataBaseManager(SqliteActivity.this, "BruceZhang_sqlite", 2);
		dbBaseHelper.getReadableDatabase();
	}
}

package org.talentware.android.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.talentware.android.bean.HotSearch;
import org.talentware.android.global.LBSApp;
import org.talentware.android.lbs.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-6-7
 * Time: 下午7:30
 * To change this template use File | Settings | File Templates.
 */
public class SQLiteManger {

    private static final String TAG = SQLiteManger.class.getSimpleName();

    private static SQLiteManger mInstance;

    private static final String DB_NAME = "lbs.db";
    private static final String DB_PATH = LBSApp.getAppContext().getFilesDir().getAbsolutePath();

    public synchronized static SQLiteManger getInstance() {
        if (mInstance == null) {
            mInstance = new SQLiteManger();
        }

        return mInstance;
    }

    public SQLiteDatabase openDatabase(Context context) {
        return openDatabase(context, DB_NAME, DB_PATH + "/");
    }

    private SQLiteDatabase openDatabase(Context context, String fileName, String path) {
        try {
            final String aDBFile = path + fileName;

            final File dir = new File(path);
            if (!dir.exists())
                dir.mkdir();

            // 如果在目录中不存在.db文件，则从res\raw目录中复制这个文件到目录
            if (!(new File(aDBFile)).exists()) {
                final InputStream is = context.getResources().openRawResource(R.raw.lbs);
                final FileOutputStream fos = new FileOutputStream(aDBFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                // 开始复制.db文件
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }

            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(aDBFile, null);
            return database;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<HotSearch> queryHotSearch(final SQLiteDatabase database) {
        List<HotSearch> result = new LinkedList<HotSearch>();
        Cursor cursor = queryHotSearch(database, 20);//TODO:20写死不大好
        Log.d(TAG, "cursor is null = " + (cursor == null));
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int hot_search_id = cursor.getInt(0);
                String hot_search_key = cursor.getString(1);
                String hot_search_tags = cursor.getString(2);
                String hot_search_displayname = cursor.getString(3);

                HotSearch item = new HotSearch(hot_search_id, hot_search_key, hot_search_tags, hot_search_displayname);
                Log.d(TAG, "HotSearch item = " + item);
                result.add(item);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return result;
    }

    private Cursor queryHotSearch(final SQLiteDatabase database, int aQuerySize) {
        final String sql = "select * from HotSearch;";

        return database.rawQuery(sql, null);
    }
}

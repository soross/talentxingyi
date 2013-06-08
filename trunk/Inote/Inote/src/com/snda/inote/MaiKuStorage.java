package com.snda.inote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.snda.inote.model.AttachFile;
import com.snda.inote.model.Category;
import com.snda.inote.model.Change;
import com.snda.inote.model.Note;
import com.snda.inote.sql.DataHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-15
 * Time: 上午8:58
 */
public class MaiKuStorage {

    private static String CATEGORY_TABLE = DataHelper.CATEGORY_TABLE;
    private static String NOTE_TABLE = DataHelper.NOTE_TABLE;
    private static String SYNC_TABLE = DataHelper.SYNC_TABLE;
    private static String ATTACH_TABLE = DataHelper.ATTACH_TABLE;

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private String uid;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public MaiKuStorage(Context context) {
        this.mContext = context;
        this.uid = Inote.getUser().getSndaID();
    }

    public void open() {
        DataHelper dataHelper = new DataHelper(mContext);
        this.mDatabase = dataHelper.getDb();
    }

    public void close() {
        this.mDatabase.close();
        this.mDatabase = null;
    }


    public long addCategory(String id, String name, int type, String pid, int _default, int count, int cached) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("uid", uid);
        values.put("name", name);
        values.put("type", type);
        values.put("pid", pid);
        values.put("count", count);
        values.put("ndefault", _default);
        values.put("cached", cached);
        return mDatabase.insert(CATEGORY_TABLE, null, values);
    }

    public long addCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put("id", category.getNoteCategoryID());
        values.put("uid", uid);
        values.put("name", category.getName());
        values.put("type", category.getAccessLevel());
        values.put("pid", category.getParentID());
        values.put("count", category.getNoteCount());
        values.put("ndefault", category.getIsDefault() ? 1 : 0);
        values.put("cached", 1);
        long insert = mDatabase.insert(CATEGORY_TABLE, null, values);
        category.set_ID((int) insert);
        return insert;
    }


    public Note addNote(Note note) {
        ContentValues value = new ContentValues();
        value.put("id", note.getNoteID());
        value.put("uid", uid);
        value.put("cateid", note.getCategoryID());
        value.put("title", note.getTitle());
        value.put("desc", note.getAbstract());
        value.put("time", note.getUpdateTime());
        value.put("content", note.getContent());
        value.put("tag", note.getTags());
        value.put("cached", 1);
        value.put("hasattachments", note.isHasAttachments() ? 1 : 0);
        int cate_id = note.getCate_id();
        if (cate_id == 0) {
            value.put("cate_id", getCateByCateId(note.getCategoryID()).get_ID());
        } else {
            value.put("cate_id", cate_id);
        }
        long insert = mDatabase.insert(NOTE_TABLE, null, value);
        note.set_ID((int) insert);
        return note;
    }

    public void addSyncChange(String entityId, int category, int operation, int type) {
        ContentValues value = new ContentValues();
        value.put("uid", uid);
        value.put("entityId", entityId);
        value.put("category", category);
        value.put("operation", operation);
        value.put("type", type);
        mDatabase.insert(SYNC_TABLE, null, value);
    }



    public void deleteNotesByCate_id(int _id) {
        mDatabase.delete(NOTE_TABLE, "cate_id= ?", new String[]{String.valueOf(_id)});
    }

    public void deleteCategory(int _id) {
        deleteNotesByCate_id(_id);
        mDatabase.delete(CATEGORY_TABLE, "_id= ?", new String[]{String.valueOf(_id)});
    }

    public void cleanCategoryByUserId() {
        mDatabase.delete(CATEGORY_TABLE, "uid = ?", new String[]{uid});
    }


    public void deleteNote(int _id) {
        mDatabase.delete(NOTE_TABLE, "_id = ?", new String[]{String.valueOf(_id)});
    }

    public void deleteNoteById(String noteid) {
        mDatabase.delete(NOTE_TABLE, "id = ?", new String[]{String.valueOf(noteid)});
    }


    public void addAttachFileToNote(Note note, AttachFile attachFile) {
        ContentValues value = new ContentValues();
        value.put("uid", uid);
        value.put("noteid", note.getNoteID());
        value.put("note_local_id", note.get_ID());
        value.put("filename", attachFile.getFileName());
        value.put("filepath", attachFile.getFileDownPath());
        value.put("filesize", attachFile.getFileSize());
        value.put("filetype", attachFile.getFileType());
        value.put("time", attachFile.getTime());
        mDatabase.insert(ATTACH_TABLE, null, value);
    }

    public void updateAttachFile(Note note, AttachFile attachFile) {
        ContentValues value = new ContentValues();
        value.put("uid", uid);
        value.put("noteid", note.getNoteID());
        value.put("note_local_id", note.get_ID());
        value.put("filename", attachFile.getFileName());
        value.put("filepath", attachFile.getFileDownPath());
        value.put("filesize", attachFile.getFileSize());
        value.put("filetype", attachFile.getFileType());
        value.put("time", attachFile.getTime());
        mDatabase.update(ATTACH_TABLE, value, "_id = ?", new String[]{String.valueOf(attachFile.getId())});
    }

    public void deleteAttachFileByNote_id(int _id) {
        mDatabase.delete(ATTACH_TABLE, "note_local_id= ? and uid = ?", new String[]{String.valueOf(_id), uid});
    }


    public List<AttachFile> getAttachFileListByNote_id(int _id) {
        List<AttachFile> list = new ArrayList<AttachFile>();
        Cursor cursor = mDatabase.query(ATTACH_TABLE, new String[]{"_id", "filename", "filepath", "filesize", "filetype", "time", "note_local_id", "noteid"}, "note_local_id='" + _id + "'", null, null, null, "_id");
        if (cursor.moveToFirst()) {
            do {
                AttachFile file = new AttachFile();
                file.setId(cursor.getInt(0));
                file.setFileName(cursor.getString(1));
                file.setFileDownPath(cursor.getString(2));
                file.setFileSize(cursor.getInt(3));
                file.setFileType(cursor.getInt(4));
                file.setTime(cursor.getString(5));
                file.setNote_id(cursor.getInt(6));
                file.setNoteId(cursor.getString(7));
                list.add(file);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public void deleteSync(int _id) {
        mDatabase.delete(SYNC_TABLE, "_id= ? ", new String[]{String.valueOf(_id)});
    }

    public void updateCategory(Category category) {
        ContentValues value = new ContentValues();
        value.put("name", category.getName());
        value.put("type", category.getAccessLevel());
        value.put("pid", category.getParentID());
        value.put("count", category.getNoteCount());
        mDatabase.update(CATEGORY_TABLE, value, "_id = ?", new String[]{String.valueOf(category.get_ID())});
    }

    public void updateCategoryCount(int count, int cate_id) {
        ContentValues value = new ContentValues();
        value.put("count", count);
        mDatabase.update(CATEGORY_TABLE, value, "_id = ?", new String[]{String.valueOf(cate_id)});
    }


    public long updateNote(Note note) {
        ContentValues value = new ContentValues();
        value.put("id", note.getNoteID());
        value.put("uid", uid);
        value.put("cateid", note.getCategoryID());
        value.put("title", note.getTitle());
        value.put("desc", note.getAbstract());
        value.put("time", note.getUpdateTime());
        value.put("content", note.getContent());
        value.put("tag", note.getTags());
        value.put("cached", 1);
        value.put("hasattachments", note.isHasAttachments() ? 1 : 0);
        if (note.getCate_id() == 0) {
            value.put("cate_id", getCateByCateId(note.getCategoryID()).get_ID());
        } else {
            value.put("cate_id", note.getCate_id());
        }

        return mDatabase.update(NOTE_TABLE, value, "_id = ?", new String[]{String.valueOf(note.get_ID())});
    }


    public void updateCategoryId(int _id, String categoryId) {
        ContentValues value = new ContentValues();
        value.put("id", categoryId);
        mDatabase.update(CATEGORY_TABLE, value, "_id=" + _id + "", null);

        value.remove("id");  //for update sub note category id
        value.put("cateid", categoryId);
        mDatabase.update(NOTE_TABLE, value, "cate_id = ?", new String[]{String.valueOf(_id)});
    }

    public void updateNoteId(int _id, String noteId) {
        ContentValues value = new ContentValues();
        value.put("id", noteId);
        mDatabase.update(NOTE_TABLE, value, "_id = ?", new String[]{String.valueOf(_id)});
    }

    public void updateNoteCategoryInfo(int _id, Category category) {
        ContentValues value = new ContentValues();
        value.put("cate_id", category.get_ID());
        value.put("cateid", category.getNoteCategoryID());
        mDatabase.update(NOTE_TABLE, value, "_id = ?", new String[]{String.valueOf(_id)});
    }

    public void updateCategoryCached(int _id) {
        ContentValues value = new ContentValues();
        value.put("cached", 1);
        mDatabase.update(CATEGORY_TABLE, value, "_id = ? ", new String[]{String.valueOf(_id)});
    }


    public List<Category> getCategoryList() {
        List<Category> list = new ArrayList<Category>();

        Cursor cursor = mDatabase.query(CATEGORY_TABLE, new String[]{"id", "name", "type", "ndefault", "count", "_id", "pid"}, "uid='" + uid + "'", null, null, null, "type, _id");
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setAccessLevel(cursor.getInt(2));
                category.setName(cursor.getString(1));
                category.setNoteCategoryID(cursor.getString(0));
                category.setIsDefault(cursor.getInt(3) == 1);
                category.setNoteCount(cursor.getInt(4));
                category.set_ID(cursor.getInt(5));
                category.setParentID(cursor.getString(6));
                list.add(category);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public Category getDefaultCategory() {
        Cursor cursor = mDatabase.query(CATEGORY_TABLE, new String[]{"id", "name", "type", "count", "_id", "pid"}, "uid='" + uid + "' and ndefault = 1 and type = 0", null, null, null, "_id");
        Category category = new Category();
        if (cursor.moveToFirst()) {
            category.setNoteCategoryID(cursor.getString(0));
            category.setName(cursor.getString(1));
            category.setAccessLevel(cursor.getInt(2));
            category.setNoteCount(cursor.getInt(3));
            category.set_ID(cursor.getInt(4));
            category.setParentID(cursor.getString(5));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return category;
    }

    public List<Category> getNoCachedCategoryList() {
        List<Category> list = new ArrayList<Category>();
        Cursor cursor = mDatabase.query(CATEGORY_TABLE, new String[]{"id", "name", "type", "count", "_id", "pid"}, "uid='" + uid + "' and cached = 0", null, null, null, "_id");
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setNoteCategoryID(cursor.getString(0));
                category.setName(cursor.getString(1));
                category.setAccessLevel(cursor.getInt(2));
                category.setNoteCount(cursor.getInt(3));
                category.set_ID(cursor.getInt(4));
                category.setParentID(cursor.getString(5));
                list.add(category);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }


    public List<Note> getNearlyNoteList(String max) {
        List<Note> list = new ArrayList<Note>();
        Cursor cursor = mDatabase.query(NOTE_TABLE, new String[]{"id", "title", "desc", "time", "_id", "hasattachments", "cateid", "cate_id"}, "uid='" + uid + "'", null, null, null, "time desc", max);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setNoteID(cursor.getString(0));
                note.setTitle(cursor.getString(1));
                note.setAbstract(cursor.getString(2));
                note.setUpdateTime(cursor.getString(3));
                note.set_ID(cursor.getInt(4));
                note.setHasAttachments(cursor.getInt(5) == 1);
                note.setCategoryID(cursor.getString(6));
                note.setCate_id(cursor.getInt(7));
                list.add(note);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }


    public Cursor getNoteListCursorByKey(String key) {
        String sqlKey = "%" + key + "%";
        return mDatabase.rawQuery("select id,title,desc,time, _id, hasattachments, cateid, cate_id  from " + NOTE_TABLE + " where uid = ? and title like ? or content like ? or tag like ? order by time desc", new String[]{uid, sqlKey, sqlKey, sqlKey});
    }

    public Cursor getNoteListCursorByCate_id(int cate_id) {
        return mDatabase.rawQuery("select id,title,desc,time, _id, hasattachments, cateid, cate_id from " + NOTE_TABLE + " where uid = ? and cate_id = ? order by time desc", new String[]{uid, String.valueOf(cate_id)});
    }


    public List<Change> getSyncList() {
        List<Change> list = new ArrayList<Change>();
        Cursor cursor = mDatabase.query(SYNC_TABLE, new String[]{"entityId", "category", "operation", "_id", "type"}, "uid='" + uid + "'", null, null, null, "_id");
        if (cursor.moveToFirst()) {
            do {
                Change change = new Change();
                change.setCategory(cursor.getInt(1));
                change.setEntityID(cursor.getString(0));
                change.setOperation(cursor.getInt(2));
                change.set_ID(cursor.getInt(3));
                change.setType(cursor.getInt(4));
                list.add(change);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return list;
    }

    public Category getCateBy_Id(int _id) {
        Category category = null;
        Cursor cursor = mDatabase.query(CATEGORY_TABLE, new String[]{"id", "name", "type", "count", "_id", "pid"}, "_id=" + _id + "", null, null, null, null);
        if (cursor.moveToFirst()) {
            category = new Category();
            category.setAccessLevel(cursor.getInt(2));
            category.setName(cursor.getString(1));
            category.setNoteCategoryID(cursor.getString(0));
            category.setNoteCount(cursor.getInt(3));
            category.set_ID(cursor.getInt(4));
            category.setParentID(cursor.getString(5));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return category;
    }

    public Category getCateByCateId(String cateId) {
        Category category = null;
        Cursor cursor = mDatabase.query(CATEGORY_TABLE, new String[]{"id", "name", "type", "count", "_id", "pid"}, "id ='" + cateId + "'", null, null, null, null);
        if (cursor.moveToFirst()) {
            category = new Category();
            category.setAccessLevel(cursor.getInt(2));
            category.setName(cursor.getString(1));
            category.setNoteCategoryID(cursor.getString(0));
            category.setNoteCount(cursor.getInt(3));
            category.set_ID(cursor.getInt(4));
            category.setParentID(cursor.getString(5));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return category;
    }

    public Note getNoteBy_Id(int _id) {
        Note note = null;
        Cursor cursor = mDatabase.query(NOTE_TABLE, new String[]{"id", "title", "content", "tag", "desc", "cateid", "_id", "hasattachments", "cate_id"}, "_id=" + _id + "", null, null, null, null);
        if (cursor.moveToFirst()) {
            note = new Note();
            note.setNoteID(cursor.getString(0));
            note.setTitle(cursor.getString(1));
            note.setContent(cursor.getString(2));
            note.setTags(cursor.getString(3));
            note.setAbstract(cursor.getString(4));
            note.setCategoryID(cursor.getString(5));
            note.set_ID(_id);
            note.setHasAttachments(cursor.getInt(7) == 1);
            note.setCate_id(cursor.getInt(8));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return note;
    }

    public Note getNoteByNodeId(String noteId) {
        Note note = null;
        Cursor cursor = mDatabase.query(NOTE_TABLE, new String[]{"id", "title", "content", "tag", "desc", "cateid", "_id", "time", "hasattachments", "cate_id"}, "id='" + noteId + "'", null, null, null, null);
        if (cursor.moveToFirst()) {
            note = new Note();
            note.setNoteID(cursor.getString(0));
            note.setTitle(cursor.getString(1));
            note.setContent(cursor.getString(2));
            note.setTags(cursor.getString(3));
            note.setAbstract(cursor.getString(4));
            note.setCategoryID(cursor.getString(5));
            note.set_ID(cursor.getInt(6));
            note.setUpdateTime(cursor.getString(7));
            note.setHasAttachments(cursor.getInt(8) == 1);
            note.setCate_id(cursor.getInt(9));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return note;
    }

    public int getNoteListCountByCateid(int cate_id) {
        int count = 0;
        Cursor cursor = mDatabase.rawQuery("select count(_id) from " + NOTE_TABLE + " where cate_id = " + cate_id, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return count;
    }

}

package com.snda.inote;

import com.snda.inote.controller.BaseController;
import com.snda.inote.controller.FileController;
import com.snda.inote.exception.NotConnectException;
import com.snda.inote.model.AttachFile;
import com.snda.inote.model.Category;
import com.snda.inote.model.Change;
import com.snda.inote.model.Note;
import com.snda.inote.util.HttpUtil;
import com.snda.inote.util.Json;
import com.snda.inote.util.StringUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-15
 * Time: 上午8:36
 */
public class MaiKuHttpApiV1 {

    private static Category resultToCategory(String result) throws Exception {
        JSONObject data = BaseController.getJsonData(result);
        Category category = new Category();
        category.setName(data.getString("Name"));
        category.setAccessLevel(data.getInt("AccessLevel"));
        category.setNoteCount(data.getInt("NoteCount"));
        category.setParentID(data.getString("ParentID"));
        category.setNoteCategoryID(data.getString("NoteCategoryID"));
        return category;
    }

    public static Category addCategoryRemote(Category category) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONObject param = new JSONObject();
        param.put("name", category.getName());
        param.put("accessLevel", category.getAccessLevel());
        param.put("parentId","");
        String result = HttpUtil.postJSON(Consts.URL_CATEGORY_ADD, param, Inote.getUser().getToken());
        return resultToCategory(result);
    }


    public static Category getCategoryRemote(String categoryId) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONObject params = new JSONObject();
        params.put("categoryId", categoryId);
        String result = HttpUtil.postJSON(Consts.URL_CATEGORY_GET, params, Inote.getUser().getToken());
        return resultToCategory(result);
    }

    public static Category updateCategoryRemote(Category cate) throws Exception {
        JSONObject param = new JSONObject();
        param.put("name", cate.getName());
        param.put("categoryId", cate.getNoteCategoryID());
        param.put("accessLevel", cate.getAccessLevel());
        param.put("parentId", cate.getParentID());
        String result = HttpUtil.postJSON(Consts.URL_CATEGORY_UPDATE, param, Inote.getUser().getToken());
        return resultToCategory(result);
    }

    public static void deleteCategoryRemote(String id) throws Exception {
        JSONObject param = new JSONObject();
        param.put("categoryId", id);
        HttpUtil.postJSON(Consts.URL_CATEGORY_DEL, param, Inote.getUser().getToken());
    }


    public static List<Category> getCategoryList(boolean getVersion) throws Exception { //Fix maybe use the local instead of the cache  or use the sync
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String result = HttpUtil.post(Consts.URL_CATEGORY_LIST, params, Inote.getUser().getToken());
        JSONArray data = BaseController.getArrayData(result, getVersion);

        List<Category> categories = new ArrayList<Category>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject jo = data.getJSONObject(i);
            Json json = new Json(jo);
            Category category = new Category(json.getString("NoteCategoryID"), json.getString("Name"), json.getInt("AccessLevel"), json.getString("ParentID"), json.getBoolean("IsDefault") ? 1 : 0, json.getInt("NoteCount"));
            categories.add(category);
        }
        return categories;
    }

    private static Note resultToNote(String result) throws Exception {
        JSONObject jo = BaseController.getJsonData(result);
        Json json = new Json(jo);
        String noteID = json.getString("NoteID");
        if (!StringUtil.hasText(noteID)) {
            return null;
        }
        Note note = new Note();
        String abstracts = json.getString("Abstract").replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " ");
        note.setAbstract(StringUtil.left(abstracts, 40, "..."));
        note.setCategoryID(json.getString("CategoryID"));
        note.setContent(json.getString("Content"));
        note.setTitle(json.getString("Title"));
        note.setUpdateTime(json.getString("UpdateTime"));
        note.setTags(json.getStringByArray("Tags"));

        note.setNoteID(noteID);
        note.setHasAttachments(json.getBoolean("HasAttachments"));
        return note;
    }

    public static Note getNoteRemote(String noteID) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONObject params = new JSONObject();
        params.put("noteId", noteID);
        params.put("password", "");
        String result = HttpUtil.postJSON(Consts.URL_NOTE_DETAIL, params, Inote.getUser().getToken());
        return resultToNote(result);
    }

    public static List<Note> getNoteListByCategoryId(String categroyId) throws Exception {
        JSONObject params = new JSONObject();
        List<Note> noteList = new ArrayList<Note>();
        params.put("categoryId", categroyId);
        params.put("start", 0);
        params.put("limit", "99999999");
        params.put("keywords", "");
        params.put("tag", "");
        String result = HttpUtil.postJSON(Consts.URL_NOTE_LIST, params, Inote.getUser().getToken());
        JSONObject data = BaseController.getJsonData(result);
        JSONArray list = data.getJSONArray("Data");
        for (int i = list.length() - 1; i >= 0; i--) {
            JSONObject jo = list.getJSONObject(i);
            Json json = new Json(jo);
            Note note = new Note();
            note.setNoteID(json.getString("NoteID"));
            noteList.add(note);
        }
        return noteList;
    }

    public static List<AttachFile> getNoteRemoteAttachmentList(Note note) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");

        JSONObject params = new JSONObject();
        params.put("noteId", note.getNoteID());
        params.put("password", "");

        String result = HttpUtil.postJSON(Consts.URL_NOTE_LISTATTACHMENTS, params, Inote.getUser().getToken());
        JSONArray jo = BaseController.getArrayData(result, false);
        
        List<AttachFile> attachFiles = new ArrayList<AttachFile>();
        for (int i = 0; i < jo.length(); i++) {
            JSONObject jsonObject = jo.getJSONObject(i);
            Json json = new Json(jsonObject);
            AttachFile file = new AttachFile();
            file.setFileName(json.getString("FileName"));
            file.setFileDownPath(json.getString("FilePath"));
            file.setFileSize(json.getDouble("FileSize"));
            file.setFileType(json.getInt("FileType"));
            file.setTime(json.getString("UploadTime"));
            attachFiles.add(file);
        }
        return attachFiles;
    }

    public static Note addNoteRemote(String title, String content, String tag, String categoryId) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONArray tagJA = new JSONArray();
        String temp;
        String[] tags = tag.split(",");
        for (String tag1 : tags) {
            temp = tag1.trim();
            if (temp.length() > 0)
                tagJA.put(temp);
        }

        JSONObject param = new JSONObject();
        param.put("title", title);
        param.put("content", content);
        param.put("tags", tagJA);
        param.put("categoryId", categoryId);


        String result = HttpUtil.postJSON(Consts.URL_NOTE_ADD, param, Inote.getUser().getToken());
        return resultToNote(result);

    }


    public static Note updateNoteRemote(String id, String title, String content, String tag, String categoryId, String password) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONArray tagJA = new JSONArray();
        String[] tags = tag.split(",");
        String temp;
        for (String tag1 : tags) {
            temp = tag1.trim();
            if (!"".equals(temp)) tagJA.put(temp);
        }
        JSONObject param = new JSONObject();
        param.put("noteId", id);
        param.put("title", title);
        param.put("content", content);
        param.put("tags", tagJA);
        param.put("categoryId", categoryId);
        param.put("password", password);

        String result = HttpUtil.postJSON(Consts.URL_NOTE_EDIT, param, Inote.getUser().getToken());
        return resultToNote(result);
    }

    public static void deleteNoteRemote(String id) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONObject param = new JSONObject();
        param.put("noteId", id);
        HttpUtil.postJSON(Consts.URL_NOTE_DEL, param, Inote.getUser().getToken());
    }

    public static Json userLogin(String productId, String authId) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONObject param = new JSONObject();
        param.put("productId", productId);
        param.put("authId", authId);
        String result = HttpUtil.postJSON(Consts.URL_LOGIN, param, Inote.getUser().getToken());
        JSONObject jo = BaseController.getJsonData(result);
        return new Json(jo);
    }

    public static Json userActivate(String nickName, int inviterId, String token) throws Exception {
        if (!Inote.instance.isConnected()) throw new NotConnectException("no connected");
        JSONObject param = new JSONObject();
        param.put("nickName", nickName);
        param.put("inviterId", inviterId);
        String result = HttpUtil.postJSON(Consts.URL_ACTIVATE, param, token);
        return new Json(BaseController.getStringData(result));
    }

    public static List<Change> getRemoteChangeList(String version) throws Exception {
        String url = Consts.URL_SYNC_CHANGE;
        JSONObject params = new JSONObject();
        params.put("syncVersion", version);
        String result = HttpUtil.postJSON(url, params, Inote.getUser().getToken());
        JSONArray data = BaseController.getArrayData(result, true);
        List<Change> changes = new ArrayList<Change>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject jo = data.getJSONObject(i);
            Json json = new Json(jo);
            Change change = new Change();
            change.setCategory(json.getInt("Category"));
            change.setEntityID(json.getString("EntityID"));
            change.setOperation(json.getInt("OperationType"));
            changes.add(change);
        }
        return changes;
    }


    public static JSONObject addAttachFileByNoteId(String noteId, File file) throws Exception {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair nvp = new BasicNameValuePair("noteId", noteId);  //this noteId why is null
        params.add(nvp);
        return FileController.attachFileUpload(new FileInputStream(file), Inote.getUser(), params, file.getName());
    }

}

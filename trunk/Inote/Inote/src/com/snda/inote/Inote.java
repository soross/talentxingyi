package com.snda.inote;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Xml;
import android.widget.Toast;
import com.snda.inote.exception.ApiRequestErrorException;
import com.snda.inote.io.Setting;
import com.snda.inote.model.*;
import com.snda.inote.service.MKSyncService;
import com.snda.inote.util.*;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Inote extends Application {
    private static User user;
    public static String SyncVersion;
    public static Inote instance;
    public static boolean isConnected = false;
    public static boolean needReloadCategory = false;
    public static String appPath;
    public static boolean needUpdateApp = false;
    public static boolean needreshowUpdateAppAlert = false;
    public static String needUpdateAppURL;
    public static String clientVesion;
    public static User notActivatedUser;

    private MaiKuStorage mMaiKuStorage;
    private Toast mToast;

    private Intent syncIntent;

    public static void setNeedReloadCategory(boolean needReloadCategory) {
        Inote.needReloadCategory = needReloadCategory;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initUserAgent();
        openMaikuStorage();
        syncIntent = new Intent(this, MKSyncService.class);
        syncIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    }

    public void startVersionCheck(){
        new getNewAppTask().execute();
    }

    private void initUserAgent() {
        try {
            ComponentName compontent = new ComponentName(this, Inote.class);
            PackageInfo pageinfo = this.getPackageManager().getPackageInfo(compontent.getPackageName(), 0);
            HttpManager.setUserAgent("inote_mobile_android/" + pageinfo.versionName);
            clientVesion = pageinfo.versionName;
        } catch (NameNotFoundException ignored) {
            clientVesion = "1.3.8";
        }
    }

    private void openMaikuStorage() {
        this.mMaiKuStorage = new MaiKuStorage(this);
        mMaiKuStorage.open();
    }

    public boolean isConnected() {
        boolean b;
        try {
            ConnectivityManager cManager = (ConnectivityManager) Inote.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            b = cManager.getActiveNetworkInfo() != null && cManager.getActiveNetworkInfo().isAvailable();
        } catch (Exception e) {
            return true;
        }
        return b;
    }

    public void startSync() {
        //stopService(syncIntent);
        startService(syncIntent);
    }

    public void stopSync() {
        stopService(syncIntent);
    }

    public static void setUser(User user) {
        Inote.user = user;
        if (user == null) {
            Inote.instance.mMaiKuStorage.setUid("-1");
        } else {
            Inote.instance.mMaiKuStorage.setUid(user.getSndaID());
        }
    }

    public static User getUser() {
        if (user != null) {
            return user;
        } else {
            return Setting.getUser(instance);
        }
    }

    @Override
    public void onTerminate() {
        stopSync();
        mMaiKuStorage.close();
        super.onTerminate();
    }


    //category part
    public void addCategory(Category category) {
        mMaiKuStorage.addCategory(category);
    }

    public void updateCategory(Category category) {
        mMaiKuStorage.updateCategory(category);
    }

    public void updateCategory2CacheStatusBy_id(int _id) {
        mMaiKuStorage.updateCategoryCached(_id);
    }

    public List<Category> getCategoryList() {
        return mMaiKuStorage.getCategoryList();
    }

    public Category getDefaultPrivateCategory() {
        return mMaiKuStorage.getDefaultCategory();
    }

    public Category getCategoryBy_id(int _id) {
        return mMaiKuStorage.getCateBy_Id(_id);
    }

    public List<Category> getNotCacheCategoryList() {
        return mMaiKuStorage.getNoCachedCategoryList();
    }

    public void addNoCacheCategoryList(List<Category> categories) {
        for (Category category : categories) {
            mMaiKuStorage.addCategory(category.getNoteCategoryID(), category.getName(), category.getAccessLevel(), category.getParentID(), category.getIsDefault() ? 1 : 0, category.getNoteCount(), 0);
        }
    }

    public void deleteCategory(int _id) {
        mMaiKuStorage.deleteCategory(_id);
    }

    public void cleanCategoryByUserId() {
        mMaiKuStorage.cleanCategoryByUserId();
    }

    public void addOrUpdateCategory(Category category) {
        Category categoryById = mMaiKuStorage.getCateByCateId(category.getNoteCategoryID());
        if (categoryById == null) {
            mMaiKuStorage.addCategory(category);
        } else {
            category.set_ID(categoryById.get_ID());
            mMaiKuStorage.updateCategory(category);
        }
    }

    //sync change part
    public void addSyncChangeLocal(String entityId, int category, int operation) {
        mMaiKuStorage.addSyncChange(entityId, category, operation, Consts.CHANGE_TYPE_LOCAL);
    }

    public void addSyncChange(String entityId, int category, int operation, int type) {
        mMaiKuStorage.addSyncChange(entityId, category, operation, type);
    }

    public List<Change> getSyncChangeList() {
        return mMaiKuStorage.getSyncList();
    }

    public void addOrUpdateNote(Note note) {
        Note noteByNodeId = mMaiKuStorage.getNoteByNodeId(note.getNoteID());
        if (noteByNodeId == null) {
            mMaiKuStorage.addNote(note);
        } else {
            note.set_ID(noteByNodeId.get_ID());
            note.setCate_id(noteByNodeId.getCate_id());
            mMaiKuStorage.updateNote(note);
        }
    }

    //note part
    public void deleteNoteListByCategoryId(int _id) {
        mMaiKuStorage.deleteNotesByCate_id(_id);
    }

    public Note getNoteBy_id(int _id) {
        return mMaiKuStorage.getNoteBy_Id(_id);
    }

    public void deleteNoteBy_id(int _id) {
        mMaiKuStorage.deleteNote(_id);
    }

    public void updateNote(Note note) {
        mMaiKuStorage.updateNote(note);
    }

    public Note addNote(Note note) {
        return mMaiKuStorage.addNote(note);
    }

    public Note addSimpleNote(String content, Category category) {
        String title;
        if (content.length() > 20) {
            title = content.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " ").substring(0, 20);
        } else {
            title = content;
        }
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTags("");
        note.setCate_id(category.get_ID());
        note.setCategoryID(category.getNoteCategoryID());
        note.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        return addNote(note);
    }


    //attach file part
    public void addAttachFileToNote(AttachFile file, Note note) {
        mMaiKuStorage.addAttachFileToNote(note, file);
    }

    public void updateAttachFile(AttachFile file, Note note) {
        mMaiKuStorage.updateAttachFile(note, file);
    }


    //change handler
    public void handlerLocalChange(Change change) throws Exception {
        if (change.getCategory() == Consts.CHANGE_CATEGORY_NOTE) {  //Note
            switch (change.getOperation()) {
                case Consts.CHANGE_CREATE:
                    Note note = mMaiKuStorage.getNoteBy_Id(Integer.parseInt(change.getEntityID()));
                    if (note != null) {
                        if (note.getNoteID() == null) {  //for fix when sumbie note success ,but upload attach file  error
                            Note note3;
                            try {
                                note3 = MaiKuHttpApiV1.addNoteRemote(note.getTitle(), note.getContent(), note.getTags(), note.getCategoryID());
                            } catch (ApiRequestErrorException e) {  //guess the remote category was delete
                                Category defaultCategory = getDefaultPrivateCategory();
                                note3 = MaiKuHttpApiV1.addNoteRemote(note.getTitle(), note.getContent(), note.getTags(), defaultCategory.getNoteCategoryID());
                                mMaiKuStorage.updateNoteCategoryInfo(note.get_ID(), defaultCategory);
                                e.printStackTrace();
                            }
                            mMaiKuStorage.updateNoteId(note.get_ID(), note3.getNoteID());
                            note.setNoteID(note3.getNoteID());
                        }
                        syncAttachFlieByNote(note);
                    }
                    break;
                case Consts.CHANGE_UPDATE:
                    Note noten = mMaiKuStorage.getNoteBy_Id(Integer.parseInt(change.getEntityID()));
                    if (noten != null) {
                        Note note1;
                        try {
                            note1 = MaiKuHttpApiV1.updateNoteRemote(noten.getNoteID(), noten.getTitle(), noten.getContent(), noten.getTags(), noten.getCategoryID(), "");
                            mMaiKuStorage.updateNoteId(noten.get_ID(), note1.getNoteID());//lazy do
                            syncAttachFlieByNote(noten);
                        } catch (ApiRequestErrorException e) {//guess the note is delete or category is delete, so add the remove note to default category
                            Category defaultCategory = getDefaultPrivateCategory();
                            note1 = MaiKuHttpApiV1.addNoteRemote(noten.getTitle(), noten.getContent(), noten.getTags(), defaultCategory.getNoteCategoryID());
                            note1.set_ID(noten.get_ID());
                            updateNote(note1);
                            e.printStackTrace();
                        }
                    }
                    break;
                case Consts.CHANGE_DELETE:
                    try {
                        MaiKuHttpApiV1.deleteNoteRemote(change.getEntityID());
                    } catch (ApiRequestErrorException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else if (change.getCategory() == Consts.CHANGE_CATEGORY_CATEGORY) {
            switch (change.getOperation()) {
                case Consts.CHANGE_CREATE:
                    Category category = mMaiKuStorage.getCateBy_Id(Integer.parseInt(change.getEntityID()));
                    if (category != null) {
                        Category category3 = MaiKuHttpApiV1.addCategoryRemote(category);
                        mMaiKuStorage.updateCategoryId(category.get_ID(), category3.getNoteCategoryID());
                    }
                    break;
                case Consts.CHANGE_UPDATE:
                    Category categoryn = mMaiKuStorage.getCateBy_Id(Integer.parseInt(change.getEntityID()));
                    if (categoryn != null) {
                        try {
                            Category category1 = MaiKuHttpApiV1.updateCategoryRemote(categoryn);
                            category1.set_ID(categoryn.get_ID());  // this is lazy...
                            updateCategory(category1);
                        } catch (ApiRequestErrorException e) {//suggest remote category is delete
                            Category category3 = MaiKuHttpApiV1.addCategoryRemote(categoryn);
                            category3.set_ID(categoryn.get_ID());
                            updateCategory(category3);
                            e.printStackTrace();
                        }
                    }
                    break;
                case Consts.CHANGE_DELETE:
                    try {
                        MaiKuHttpApiV1.deleteCategoryRemote(change.getEntityID());
                    } catch (ApiRequestErrorException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        mMaiKuStorage.deleteSync(change.get_ID());
    }

    public void handlerRemoteChange(Change change) throws Exception {
        String entityID = change.getEntityID();
        if (change.getCategory() == Consts.CHANGE_CATEGORY_NOTE) {
            switch (change.getOperation()) {
                case Consts.CHANGE_CREATE:
                    try {
                        Note note = MaiKuHttpApiV1.getNoteRemote(entityID);
                        if (note != null) {
                            addOrUpdateNote(note);
                            if (note.isHasAttachments()) {
                                syncAttachFlieByNote(note);
                            }
                        }
                    } catch (ApiRequestErrorException e) {
                        e.printStackTrace();
                    }
                    break;
                case Consts.CHANGE_UPDATE:
                    try {
                        Note note1 = MaiKuHttpApiV1.getNoteRemote(entityID);
                        if (note1 != null) {
                            addOrUpdateNote(note1);
                            if (note1.isHasAttachments()) {
                                syncAttachFlieByNote(note1);
                            }
                        }
                    } catch (ApiRequestErrorException e) {
                        e.printStackTrace();
                    }
                    break;
                case Consts.CHANGE_DELETE:
                    Note note = mMaiKuStorage.getNoteByNodeId(entityID);
                    if (note != null) {
                        mMaiKuStorage.deleteNoteById(entityID);
                    }
                    break;
            }
        } else if (change.getCategory() == Consts.CHANGE_CATEGORY_CATEGORY) {
            switch (change.getOperation()) {
                case Consts.CHANGE_CREATE:
                    Category category = MaiKuHttpApiV1.getCategoryRemote(entityID);
                    addOrUpdateCategory(category);
                    break;
                case Consts.CHANGE_UPDATE:
                    Category category1 = MaiKuHttpApiV1.getCategoryRemote(entityID);
                    addOrUpdateCategory(category1);
                    break;
                case Consts.CHANGE_DELETE:
                    Category cate = mMaiKuStorage.getCateByCateId(entityID);
                    if (cate != null) {
                        mMaiKuStorage.deleteCategory(cate.get_ID());
                    }
                    break;
            }
        }
        mMaiKuStorage.deleteSync(change.get_ID());
    }


    public Cursor getNoteListCursorByKey(String key) {
        return mMaiKuStorage.getNoteListCursorByKey(key);
    }

    public List<AttachFile> getNoteAttachFileList(int _id) {
        List<AttachFile> attachFileListByNote_id = mMaiKuStorage.getAttachFileListByNote_id(_id);
        for (AttachFile attachFile : attachFileListByNote_id) {
            File s = IOUtil.getExternalFile(Consts.PATH_FILE_CACHE + attachFile.getNoteId() + "/" + attachFile.getFileName());
            if (!s.exists()) {
                s = IOUtil.getExternalFile(Consts.PATH_FILE_LOCALCACHE + attachFile.getNote_id() + "/" + attachFile.getFileName());
            }
            attachFile.setFile(s);
        }
        return attachFileListByNote_id;
    }


    public List<Note> getNearlyNoteList(String count) {
        return mMaiKuStorage.getNearlyNoteList(count);
    }

    public Cursor getNoteListCursorByCateId(int cate_id) {
        return mMaiKuStorage.getNoteListCursorByCate_id(cate_id);
    }

    public void updateCategoryCount(Category category) {
        int count = mMaiKuStorage.getNoteListCountByCateid(category.get_ID());
        mMaiKuStorage.updateCategoryCount(count, category.get_ID());
    }

    public void deleteAttachFileByNote_Id(int _id) {
        mMaiKuStorage.deleteAttachFileByNote_id(_id);
    }


    public void syncAttachFlieByNote(Note note) throws Exception {
        String noteID = note.getNoteID();
        if (noteID == null) return;
        List<AttachFile> localAttachFileList = getNoteAttachFileList(note.get_ID());

        for (AttachFile localFile : localAttachFileList) {
            boolean needUpload = localFile.getFileDownPath() == null;
            if (needUpload) {
                File file = localFile.getFile();
                if (file.exists()) {
                    JSONObject jsonObject = MaiKuHttpApiV1.addAttachFileByNoteId(noteID, file);
                    Json json = new Json(jsonObject);
                    localFile.loadPropertyByJson(json);
                    File file1 = IOUtil.getExternalFile(Consts.PATH_FILE_CACHE + noteID + "/" + localFile.getFileName());
                    IOUtil.move(file, file1);
                    updateAttachFile(localFile, note);
                }
            }
        }

        List<AttachFile> noteRemoteAttachmentList = MaiKuHttpApiV1.getNoteRemoteAttachmentList(note);
        deleteAttachFileByNote_Id(note.get_ID());
        for (AttachFile file : noteRemoteAttachmentList) {
            addAttachFileToNote(file, note);
        }

    }


    public void openAttachFile(File file) {
        if (!file.exists()) return;
        Uri uri = Uri.fromFile(file);
        String result = file.getName();
        int beginIndex = result.lastIndexOf(".");
        String substring = beginIndex == -1 ? "" : result.substring(beginIndex + 1);
        String extension = Consts.nameAndMine.get(substring.toLowerCase());
        if (StringUtil.hasText(extension)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, extension.toLowerCase());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivity(intent);
            } catch (Exception e) {
                showToast(getString(R.string.alert_not_support_file));
                e.printStackTrace();
            }
        } else {
            showToast(getString(R.string.alert_not_support_file));
        }
    }

    public void showToast(String str) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        mToast.show();
    }


    public class getNewAppTask extends UserTask<Void, Void, Boolean> {
        @Override
        public Boolean doInBackground(Void... params) {
            if (!isConnected()) return false;
            try {
                String version = "";
                boolean needUpdate = false;
                String urlStr = Consts.URL_APP_VERSION_CHECK + "?ClientType=android&CurrentVersion=" + clientVesion + ".0";
                URL url = new URL(urlStr);
                URLConnection connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                InputStream in = httpConnection.getInputStream();

                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(in, "UTF-8");
                int event = parser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            if ("UpdateResult".equals(parser.getName())) {
                                int count = parser.getAttributeCount();
                                for (int i = 0; i < count; i++) {
                                    String key = parser.getAttributeName(i);
                                    if ("NeedUpdate".equals(key)) {
                                        needUpdate = "true".equals(parser.getAttributeValue(i));
                                    }
                                }
                            } else if ("FileUrl".equals(parser.getName())) {
                                int count = parser.getAttributeCount();
                                for (int i = 0; i < count; i++) {
                                    String key = parser.getAttributeName(i);
                                    if ("value".equals(key)) {
                                        needUpdateAppURL = parser.getAttributeValue(i);
                                    }
                                }
                            } else if ("CurrentVersion".equals(parser.getName())) {
                                int count = parser.getAttributeCount();
                                for (int i = 0; i < count; i++) {
                                    String key = parser.getAttributeName(i);
                                    if ("value".equals(key)) {
                                        version = parser.getAttributeValue(i);
                                    }
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    event = parser.next();
                }
                in.close();

                if (needUpdate) {
                    String apkFileName = "maiku" + version + ".apk";
                    File file = IOUtil.getExternalFile(Consts.PATH_FILE_CACHE + "update" + "/" + apkFileName);
                    if (file.exists()) {
                        appPath = file.getPath();
                    } else {
                        appPath = IOUtil.saveAttachFile2CacheFolder(needUpdateAppURL, apkFileName, "update", true);
                    }
                    needUpdateApp = true;
                    needreshowUpdateAppAlert = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onPostExecute(Boolean hasNewVersion) {

        }
    }

}

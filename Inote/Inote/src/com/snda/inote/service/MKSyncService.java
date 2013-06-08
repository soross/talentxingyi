package com.snda.inote.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import com.snda.inote.MaiKuHttpApiV1;
import com.snda.inote.R;
import com.snda.inote.exception.ApiRequestErrorException;
import com.snda.inote.exception.NotConnectException;
import com.snda.inote.io.Setting;
import com.snda.inote.model.Category;
import com.snda.inote.model.Change;
import com.snda.inote.model.Note;
import com.snda.inote.model.User;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-28
 * Time: 上午8:43
 */
public class MKSyncService extends Service {

    public static final String SYNC_BROADCAST_KEY = "com.sdo.note.sync";

    private MKBinder mkBinder = new MKBinder();
    ExecutorService execPool = Executors.newSingleThreadExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        return mkBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        execPool.submit(mTasks);
     
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        execPool.shutdown();
    }


    private void sendMessage(String key, String value) {
        Intent i = new Intent(SYNC_BROADCAST_KEY);
        i.putExtra("key", key);
        if (value != null)
            i.putExtra("value", value);
        sendBroadcast(i);
    }

    public class MKBinder extends Binder {
        MKSyncService getService() {
            return MKSyncService.this;
        }
    }


    private Runnable mTasks = new Runnable() {
        @Override
        public void run() {
            sendMessage(Consts.SERVICE_BEGIN, null);
            if (!Inote.instance.isConnected()) {
                sendMessage(Consts.SERVICE_NOT_CONNECTION_ERROR, getString(R.string.sync_not_connection_error));
                sendMessage(Consts.SERVICE_END, null);
                return;
            }
            User user = Inote.getUser();
            if(user==null || user.isOffLineUser()){
                sendMessage(Consts.SERVICE_END, null);
                return;
            }

            try {
                String syncVersion = Setting.getSyncVersion();
                List<Category> categories = Inote.instance.getCategoryList();
                boolean needDown = false;
                if (syncVersion == null || categories.size() == 0) needDown = true;
                if (needDown) {
                    try {
                        Inote.instance.cleanCategoryByUserId();
                        List<Category> categoryList = MaiKuHttpApiV1.getCategoryList(true);
                        Inote.instance.addNoCacheCategoryList(categoryList);
                        sendMessage(Consts.SERVICE_MSG_CATEGORY, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendMessage(Consts.SERVICE_MSG_ERROR, getString(R.string.sync_needdown_error));
                    }
                }

                loadNoteCacheCategory();

                if (!needDown) {
                    syncChange(syncVersion);
                }
                Setting.setSyncVersion();
                sendMessage(Consts.SERVICE_MSG_ALL, null);
            } catch (ApiRequestErrorException e) {
                e.printStackTrace();
                sendMessage(Consts.SERVICE_MSG_REQUEST_ERROR, e.toString());
            } catch (SocketException e) {
                e.printStackTrace();
                sendMessage(Consts.SERVICE_MSG_IOERROR, e.toString());
            } catch (NotConnectException e) {
                e.printStackTrace();
                sendMessage(Consts.SERVICE_NOT_CONNECTION_ERROR, e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                sendMessage(Consts.SERVICE_MSG_IOERROR, e.toString());
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(Consts.SERVICE_MSG_ERROR, e.toString());
            } finally {
                sendMessage(Consts.SERVICE_END, null);
                MKSyncService.this.stopSelf();
            }
        }
    };

    private void syncChange(String syncVersion) throws Exception {
        List<Change> remoteChangeList = MaiKuHttpApiV1.getRemoteChangeList(syncVersion);
        for (Change change : remoteChangeList) {
            Inote.instance.addSyncChange(change.getEntityID(), change.getCategory(), change.getOperation(), Consts.CHANGE_TYPE_REMOTE);
        }
        List<Change> changeList = Inote.instance.getSyncChangeList();
        for (Change change : changeList) {
            try {
                if (change.getType() == Consts.CHANGE_TYPE_LOCAL) {
                    Inote.instance.handlerLocalChange(change);
                } else {
                    Inote.instance.handlerRemoteChange(change);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(Consts.SERVICE_MSG_ERROR, getString(R.string.sync_change_error));
            }
        }

        if (changeList != null && changeList.size() > 0) {
            List<Category> newCategoryList = Inote.instance.getCategoryList();
            for (Category category : newCategoryList) {
                Inote.instance.updateCategoryCount(category);
            }
        }
    }

    private void loadNoteCacheCategory() {
        List<Category> noCachedList = Inote.instance.getNotCacheCategoryList();
        for (Category category : noCachedList) {
            if (category.getNoteCount() == 0) {
                Inote.instance.updateCategory2CacheStatusBy_id(category.get_ID());
                continue;
            }
            try {
                Inote.instance.deleteNoteListByCategoryId(category.get_ID());
                List<Note> noCacheNoteList = MaiKuHttpApiV1.getNoteListByCategoryId(category.getNoteCategoryID());
                for (Note note : noCacheNoteList) {
                    Note noteRemote = MaiKuHttpApiV1.getNoteRemote(note.getNoteID());
                    noteRemote.setCate_id(category.get_ID());
                    Inote.instance.addNote(noteRemote);
                    if (noteRemote.isHasAttachments()) {
                        Inote.instance.syncAttachFlieByNote(noteRemote);
                    }
                }
                Inote.instance.updateCategory2CacheStatusBy_id(category.get_ID()); //if sync this category error, will reload at next time
            } catch (Exception e) {
                sendMessage(Consts.SERVICE_MSG_ERROR, getString(R.string.sync_downNoteList_error));
                e.printStackTrace();
            }
        }
    }

}

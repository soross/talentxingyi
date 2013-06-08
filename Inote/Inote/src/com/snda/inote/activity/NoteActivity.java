package com.snda.inote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import com.snda.inote.R;
import com.snda.inote.adapter.AttachFileAdapter;
import com.snda.inote.io.Setting;
import com.snda.inote.model.AttachFile;
import com.snda.inote.model.Note;
import com.snda.inote.service.MKSyncService;
import com.snda.inote.util.IOUtil;
import com.snda.inote.util.StringUtil;
import com.snda.inote.util.UserTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoteActivity extends Activity {
    private static final int DIALOG_LOADING = 0;
    private static final int DIALOG_DELETE = 1;
    private static final int DIALOG_DOWNLOAD = 2;
    private static final int DIALOG_OPEN = 3;
    private static final int DIALOG_DOWNLOAD_LOADING = 4;

    private WebView txtNoteContent;
    private TextView txtNoteTitle;
    private TextView txtNoteTag;
    private View viewLoading;
    private View viewHeader;
    private View viewFooter;
    private TextView attachName;
    private Toast mToast;

    private Note note;

    private AttachFile attachFile;
    private boolean isFullScreen = false;
    private SyncServiceReceiver syncReceiver;

    private final Activity context = NoteActivity.this;

    private int note_id;
    private AttachFileAdapter attachFileAdapter;

    private Gallery attachview;
    private View AttachLayoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);
        setTitle(R.string.title_note_detail);
        viewHeader = findViewById(R.id.header);
        viewFooter = findViewById(R.id.footer);
        attachName = (TextView) findViewById(R.id.attachname);
        AttachLayoutView = findViewById(R.id.attachlayout);
        attachview = (Gallery) findViewById(R.id.attachview);
        attachview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                attachFile = (AttachFile) attachFileAdapter.getItem(position);
                File file = attachFile.getFile();
                if (file.exists()) {
                    Inote.instance.openAttachFile(file);
                } else {
                    showDialog(DIALOG_DOWNLOAD);
                }
            }
        });
        attachFileAdapter = new AttachFileAdapter(this);
        attachview.setAdapter(attachFileAdapter);
        attachview.setOnItemSelectedListener(new Gallery.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                attachName.setText(((AttachFile) adapterView.getItemAtPosition(i)).getFileName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findViewById(R.id.fontSizeChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = txtNoteContent.zoomIn();
                if (!b) {
                    while (txtNoteContent.zoomOut()) {
                    }
                }
            }
        });

        viewLoading = findViewById(R.id.load);
        txtNoteTitle = (TextView) findViewById(R.id.txtNoteTitle);
        txtNoteTag = (TextView) findViewById(R.id.txtNoteTag);
        initContentWebView();

        note_id = getIntent().getIntExtra("id", 0);
        findViewById(R.id.header_right_btn).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.btnFresh).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.btnShare).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.btnEdit).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.btnDel).setOnClickListener(handlerClickLisenter);
//        SDOAnalyzeAgentInterface.onCreate(context);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//      //  SDOAnalyzeAgentInterface.onPause(context);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
       // SDOAnalyzeAgentInterface.onDestroy(context);
        unregisterReceiver(syncReceiver);
    }


    @Override
    protected void onResume() {
        super.onResume();
   //     SDOAnalyzeAgentInterface.onResume(context);
        refresh();
    }


    private void initContentWebView() {
        txtNoteContent = (WebView) findViewById(R.id.txtNoteContent);
        txtNoteContent.setVerticalScrollBarEnabled(false);
        txtNoteContent.setHorizontalScrollBarEnabled(false);
        txtNoteContent.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String desc, String failUrl) {
                viewLoading.setVisibility(View.GONE);
                Toast.makeText(context, R.string.web_view_error, Toast.LENGTH_SHORT).show();
            }

            public void onPageFinished(WebView view, String url) {
                viewLoading.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

        });
        WebSettings webSettings = txtNoteContent.getSettings();
        webSettings.setDefaultFontSize(18);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportMultipleWindows(false);
        txtNoteContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return false;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return false;
            }

        });
        txtNoteContent.addJavascriptInterface(new Object() {
            public void full() {
                handler.post(runReasult);
            }
        }, "Inodroid");

        syncReceiver = new SyncServiceReceiver();
        registerReceiver(syncReceiver, new IntentFilter(MKSyncService.SYNC_BROADCAST_KEY));

    }

    @Override
    protected void onNewIntent(Intent intent) {
        note_id = intent.getIntExtra("id", 0);
        super.onNewIntent(intent);
    }

    public class SyncServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String key = bundle.getString("key");
            if (key.equalsIgnoreCase(Consts.SERVICE_MSG_ALL) || key.equalsIgnoreCase(Consts.SERVICE_MSG_CHANGE)) {  //FIX nullpoint exception
                refresh();
            }
        }
    }


    private void refresh() {
        new getLocalNote().execute(note_id);
    }

    private void sync() {
        showToast(getString(R.string.sync_begin));
        Inote.instance.startSync();
    }

    //FIX load image is not sync
    private void showData(Note item, String body) {
        if (item == null) return;
        String title = item.getTitle();
        title = title == null ? "" : title;
        setTitle(title);
        findViewById(R.id.layoutNoteTitle).setVisibility(View.VISIBLE);
        try {
            txtNoteTitle.setText(title);
            if (!"".equalsIgnoreCase(item.getTags())) {
                findViewById(R.id.layoutNoteTag).setVisibility(View.VISIBLE);
                txtNoteTag.setText(item.getTags());
            }
            txtNoteContent.loadDataWithBaseURL("about:blank", body, "text/html", "utf-8", null);
            if (!Setting.getFullScreenTip(context)) {
                Setting.setFullScreenTip(context);
            }

            findViewById(R.id.attachlayout).setVisibility(item.isHasAttachments() ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String processContent(Note nNote) {
        String content = nNote.getContent();
        if (content == null) {
            return "";
        }
        content = nNote.getContent().replaceAll("src=\"(http://files.\\w+.sdo.com/[^\"]+)\"", "src=\"$1?auth=" + Inote.getUser().getToken() + "\"");
        Pattern pt = Pattern.compile("src=\"(http://files.\\w+.sdo.com/[^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pt.matcher(content);
        List<String> stringList = new ArrayList<String>();
        while (matcher.find()) {
            String str = matcher.group(1);
            stringList.add(str);
        }

        for (String url : stringList) {
            String localPath = "";
            try {
                localPath = IOUtil.saveImageFileTo2CacheFolder(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            content = content.replace(url, "content://com.android.htmlfileprovider/sdcard/maiku/cache/.image/" + localPath);
        }

        return content + "\n<script>window.onclick=function(){window.Inodroid.full();}</script>";
    }


    public static void show(Context context, int _id) {
        Intent intent = new Intent();
        intent.putExtra("id", _id);
        intent.setClass(context, NoteActivity.class);
        context.startActivity(intent);
    }


    Button.OnClickListener handlerClickLisenter = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_right_btn:
                    if (note == null) return;
                    AddFullActivity.show(context, note.getCate_id());
                    break;
                case R.id.btnFresh:
                    if (note == null) return;
                    sync();
                    break;
                case R.id.btnShare:
                    if (note == null) return;
                    Browser.sendString(context, note.getTitle() + "\r\n" + note.getContent());
                    break;
                case R.id.btnEdit:
                    if (note == null) return;
                    EditFullActivity.show(context, note);
                    break;
                case R.id.btnDel:
                    if (note == null) return;
                    showDialog(DIALOG_DELETE);
                    break;
            }
        }
    };

    final Handler handler = new Handler();
    final Runnable runReasult = new Runnable() {
        public void run() {
            fullScreen();
        }
    };

    private void fullScreen() {
        if (!isFullScreen) {
            isFullScreen = true;
            viewHeader.setVisibility(View.GONE);
            viewFooter.setVisibility(View.GONE);
            findViewById(R.id.layoutNoteTitle).setVisibility(View.GONE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            isFullScreen = false;
            viewHeader.setVisibility(View.VISIBLE);
            viewFooter.setVisibility(View.VISIBLE);
            findViewById(R.id.layoutNoteTitle).setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (note.isHasAttachments()) {
            AttachLayoutView.setVisibility(!isFullScreen ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit: {
                if (note != null) {
                    EditFullActivity.show(context, note);
                }
                break;
            }
            case R.id.menu_delete: {
                showDialog(DIALOG_DELETE);
                break;
            }
            case R.id.menu_send: {
                if (note != null) {
                    Browser.sendString(context, note.getTitle() + "\r\n" + note.getContent());
                }
                break;
            }
        }
        return true;
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DELETE:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.title_note_delete)
                        .setMessage(R.string.title_note_deleteTip)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog(DIALOG_LOADING);
                                new deleteNote().execute();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            case DIALOG_DOWNLOAD:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.title_note_down)
                        .setMessage(getString(R.string.title_note_downTip, attachFile.getFileName()))
                        .setPositiveButton(R.string.down, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog(DIALOG_DOWNLOAD_LOADING);
                                new downloadAttachFiles().execute(attachFile);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            case DIALOG_OPEN:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.title_note_open)
                        .setMessage(getString(R.string.title_note_openTip, attachFile.getFileName()))
                        .setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog(DIALOG_LOADING);
                                new downloadAttachFiles().execute(attachFile);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            case DIALOG_LOADING:
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage(getString(R.string.sync_loading));
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            case DIALOG_DOWNLOAD_LOADING:
                ProgressDialog dialog2 = new ProgressDialog(context);
                dialog2.setMessage(getString(R.string.sync_downloading));
                dialog2.setIndeterminate(true);
                dialog2.setCancelable(false);
                return dialog2;
            default:
                return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_DOWNLOAD: {
                ((AlertDialog) dialog).setMessage(getString(R.string.title_note_downTip, attachFile.getFileName()));
            }
            break;
        }
        super.onPrepareDialog(id, dialog);
    }

    private class getLocalNote extends UserTask<Object, Void, Map<Object, Object>> {
        @Override
        public Map<Object, Object> doInBackground(Object... params) {
            try {
                Integer _id = (Integer) params[0];
                Map<Object, Object> map = new HashMap<Object, Object>();
                note = Inote.instance.getNoteBy_id(_id);

                if (note.isHasAttachments()) {
                    new getNoteAttachFiles().execute();
                }
                map.put("body", processContent(note));
                return map;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(Map<Object, Object> result) {
            if (result != null) {
                showData(note, result.get("body").toString());
            }

        }
    }


    //just for show new attachFiles list
    private class getNoteAttachFiles extends UserTask<Object, Void, List<AttachFile>> {
        @Override
        public List<AttachFile> doInBackground(Object... params) {
            try {
                return Inote.instance.getNoteAttachFileList(note.get_ID());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(List<AttachFile> result) {
            if (result != null) {
                showAttachFile(result);
            }
        }
    }

    private void showAttachFile(List<AttachFile> items) {
        if (items == null || items.size() == 0) {
            findViewById(R.id.attachlayout).setVisibility(View.GONE);
            attachview.setVisibility(View.GONE);
            return;
        }
        attachview.setVisibility(View.VISIBLE);
        findViewById(R.id.attachlayout).setVisibility(View.VISIBLE);
        attachFileAdapter.setAttachFileList(items);
        attachFileAdapter.notifyDataSetChanged();
        //   attachview.setSelection(0);
    }

    private class downloadAttachFiles extends UserTask<AttachFile, Void, String> {
        @Override
        public String doInBackground(AttachFile... params) {
            try {
                AttachFile file = params[0];
                if (file != null) {
                    String noteID = note.getNoteID();
                    String flag = (StringUtil.hasText(noteID)) ? noteID : String.valueOf(note.get_ID());
                    String name = file.getFileName();

                    String s = null;
                    try {
                        s = IOUtil.saveAttachFile2CacheFolder(Consts.FILE_URL_BASE + Uri.encode(file.getFileDownPath(), "\\"), name, flag, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(String result) {
            if (result != null) {
                File file = new File(result);
                Inote.instance.openAttachFile(file);
            } else {
                showToast(getString(R.string.alert_download_attach_error));
            }
            removeDialog(DIALOG_DOWNLOAD_LOADING);
        }

    }

    private class deleteNote extends UserTask<String, Void, Boolean> {
        @Override
        public Boolean doInBackground(String... params) {
            Inote.setNeedReloadCategory(true);
            String noteID = note.getNoteID();
            try {
                Inote.instance.deleteNoteBy_id(note_id);
                if (StringUtil.hasText(noteID)) {
                    Inote.instance.addSyncChangeLocal(String.valueOf(note.getNoteID()), Consts.CHANGE_CATEGORY_NOTE, Consts.CHANGE_DELETE);
                }
                List<AttachFile> noteAttachFileList = Inote.instance.getNoteAttachFileList(note_id);
                for (AttachFile attachFile : noteAttachFileList) {
                    File file = attachFile.getFile();
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                }
                Inote.instance.deleteAttachFileByNote_Id(note_id);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Inote.instance.updateCategoryCount(Inote.instance.getCategoryBy_id(note.getCate_id()));
            }
            return false;
        }

        public void onPostExecute(Boolean n) {
            sync();
            removeDialog(DIALOG_LOADING);
            finish();
        }
    }

    private void showToast(String str) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        mToast.show();
    }


}

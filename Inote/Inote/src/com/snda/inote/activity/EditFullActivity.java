package com.snda.inote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import com.snda.inote.R;
import com.snda.inote.adapter.AddViewListAdapter;
import com.snda.inote.adapter.AttachFileAdapter;
import com.snda.inote.fileselector.FileSelector;
import com.snda.inote.model.AttachFile;
import com.snda.inote.model.Category;
import com.snda.inote.model.Note;
import com.snda.inote.util.IOUtil;
import com.snda.inote.util.IntentUtils;
import com.snda.inote.util.StringUtil;
import com.snda.inote.util.UserTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class EditFullActivity extends Activity {

    private final static int DIALOG_SAVE_NOTE = 0;
    private final static int DIALOG_COPY_LOCAL_FILE = 1;
    private static final int DIALOG_DOWNLOAD = 2;
    private final static int IMAGE_CAPTURE_REQUEST = 3;
    private final static int AUDIO_RECORD_REQUEST = 4;
    private static final int DIALOG_LOADING = 5;
    private static final int FILE_SELECTOR_REQUEST = 6;
    private final static int DIALOG_CANCEL_ALERT = 8;
    private final static int DIALOG_APPEND_ATTACH_ALERT = 12;

    private final Activity context = EditFullActivity.this;
    private EditText noteTitleView;
    private EditText noteBodyView;
    private Spinner spnCategory;
    private EditText iptNoteTag;
    private Toast mToast;
    private AttachFileAdapter attachFileAdapter;
    private TextView attachName;
    private Gallery attachview;
    private View attachLayoutView;

    private List<Category> categoryList;
    private boolean isRichText;
    private Note _note;
    private List<AttachFile> attachFiles = new ArrayList<AttachFile>();

    private List<AttachFile> appendAttachFiles = new ArrayList<AttachFile>();
    private AttachFile selectAttachFile;


    private String path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noteadd);

        TextView alertTextView = (TextView) findViewById(R.id.alertTextView);
        Intent intent = getIntent();
        _note = (Note) intent.getSerializableExtra("note");

        noteTitleView = (EditText) findViewById(R.id.iptNoteTitle);
        noteBodyView = (EditText) findViewById(R.id.iptNoteBody);
        attachLayoutView = findViewById(R.id.attachlayout);
        noteBodyView.requestFocus();
        spnCategory = (Spinner) findViewById(R.id.spnNoteCategory);
        iptNoteTag = (EditText) findViewById(R.id.iptNoteTag);
        findViewById(R.id.btnNoteSubmit).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.btnNoteCancel).setOnClickListener(handlerClickLisenter);

        initAttachView();

        if (_note.isHasAttachments()) {
            new getNoteAttachFiles().execute();
        }

        new getCategoryData().execute();
        noteTitleView.setText(_note.getTitle());
        iptNoteTag.setText(_note.getTags());
        String s = _note.getContent().replaceAll("<br/?>", "\n").replaceAll("<p/?>", "\n").replaceAll("</p>","").replaceAll("&nbsp;"," ");
        isRichText = StringUtil.isRichText(s);
        if (!isRichText) {
            alertTextView.setVisibility(View.GONE);
            noteBodyView.append(s);
        } else {
            alertTextView.setText(getString(R.string.richtext_warning));
        }

//        SDOAnalyzeAgentInterface.onCreate(context);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        SDOAnalyzeAgentInterface.onPause(context);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SDOAnalyzeAgentInterface.onDestroy(context);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        SDOAnalyzeAgentInterface.onResume(context);
//    }

    private void initAttachView() {
        attachName = (TextView) findViewById(R.id.attachname);
        attachview = (Gallery) findViewById(R.id.attachview);
        attachview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                selectAttachFile = (AttachFile) attachFileAdapter.getItem(position);
                File file = selectAttachFile.getFile();
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
//        attachview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//                contextMenu.add(Menu.FIRST, 0, 0, getString(R.string.menu_remove_attach));
//            }
//        });
    }


    public static void show(Context context, Note note) {
        Intent intent = new Intent();
        intent.putExtra("note", note);
        intent.setClass(context, EditFullActivity.class);
        context.startActivity(intent);
    }


    Button.OnClickListener handlerClickLisenter = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnNoteSubmit:
                    onSubmit();
                    break;
                case R.id.btnNoteCancel:
                    if (needShowAlert()) {
                        showDialog(DIALOG_CANCEL_ALERT);
                    } else {
                        finish();
                    }
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener appendItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            int id = Consts.APPEND_TEXTS[position];
            switch (id) {
                case R.string.add_file_note:
                    IntentUtils.openFileSelector(context, FILE_SELECTOR_REQUEST);
                    break;
                case R.string.add_camera_note:
                    path = IntentUtils.openCamera(context, IMAGE_CAPTURE_REQUEST);
                    break;
                case R.string.add_voice_file_note:
                    IntentUtils.openAudioRecord(context, AUDIO_RECORD_REQUEST);
                    break;
            }
        }
    };


    private void onSubmit() {
        String noteTitle = noteTitleView.getText().toString();
        String noteBody = noteBodyView.getText().toString().replaceAll("\n", "<br/>");
        String noteTag = iptNoteTag.getText().toString();

        if (noteTitle.trim().equals("") && noteBody.trim().equals("")) {
            Toast.makeText(EditFullActivity.this, getString(R.string.empty_note_title_and_content_warring), Toast.LENGTH_SHORT).show();
        } else {
            showDialog(DIALOG_SAVE_NOTE);
            new submitData().execute(noteTitle, noteBody, noteTag, categoryList.get(spnCategory.getSelectedItemPosition()));
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_COPY_LOCAL_FILE:
                ProgressDialog dialog = new ProgressDialog(EditFullActivity.this);
                dialog.setMessage(getString(R.string.note_copy_file));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            case DIALOG_DOWNLOAD:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.title_note_down)
                        .setMessage(getString(R.string.title_note_downTip, selectAttachFile.getFileName()))
                        .setPositiveButton(R.string.down, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog(DIALOG_LOADING);
                                new downloadAttachFiles().execute(selectAttachFile);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            case DIALOG_APPEND_ATTACH_ALERT:
                Dialog appendDialog = new Dialog(context, R.style.customDialog);
                appendDialog.setContentView(R.layout.add);
                ListView list = (ListView) appendDialog.findViewById(R.id.menulist);
                TextView title = (TextView) appendDialog.findViewById(R.id.header_title);
                title.setText(getString(R.string.menu_add_attach));
                list.setOnItemClickListener(appendItemClickListener);
                list.setAdapter(new AddViewListAdapter(this, 1));
                return appendDialog;
            case DIALOG_SAVE_NOTE:
                ProgressDialog dialog1 = new ProgressDialog(EditFullActivity.this);
                dialog1.setMessage(getString(R.string.note_save));
                dialog1.setIndeterminate(true);
                dialog1.setCancelable(true);
                return dialog1;
            case DIALOG_LOADING:
                ProgressDialog dialog2 = new ProgressDialog(context);
                dialog2.setMessage(getString(R.string.sync_downloading));
                dialog2.setIndeterminate(true);
                dialog2.setCancelable(false);
                return dialog2;
            case DIALOG_CANCEL_ALERT:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.not_save_note_alert_title)
                        .setMessage(R.string.not_save_note_alert)
                        .setPositiveButton(R.string.not_save_note_btn_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onSubmit();
                            }
                        })
                        .setNegativeButton(R.string.not_save_note_btn_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECTOR_REQUEST:
                if (resultCode == FileSelector.SUCCESS) {
                    removeDialog(DIALOG_APPEND_ATTACH_ALERT);
                    removeDialog(DIALOG_COPY_LOCAL_FILE);
                    String fileName = FileSelector.getResult(data);
                    new createUploadFile().execute(fileName);
                }
                break;
            case IMAGE_CAPTURE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    removeDialog(DIALOG_APPEND_ATTACH_ALERT);
                    try {
                        File file = IOUtil.getExternalFile(path);
                        if (file.exists()) {
                            showDialog(DIALOG_COPY_LOCAL_FILE);
                            new createUploadFile().execute(file.getPath());
                        }
                    } catch (Exception e) {
                        removeDialog(DIALOG_COPY_LOCAL_FILE);
                    }
                }
                break;
            case AUDIO_RECORD_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    removeDialog(DIALOG_APPEND_ATTACH_ALERT);
                    Uri uri = data.getData();
                    if (uri != null) {
                        showDialog(DIALOG_COPY_LOCAL_FILE);
                        new createAudioFile().execute(uri);
                    }
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (needShowAlert()) {
            showDialog(DIALOG_CANCEL_ALERT);
        } else {
            super.onBackPressed();
        }
    }

    private boolean needShowAlert() {
        String noteTitle = this.noteTitleView.getText().toString();
        String noteBody = this.noteBodyView.getText().toString();
        if (isRichText) {
            return !noteTitle.equals(_note.getTitle()) || noteBody.length() != 0;
        }

        String newContent = noteBody.replace("\n", "<br/>");
        return !noteTitle.equals(_note.getTitle()) || !newContent.equals(_note.getContent());
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AttachFile selectedItem = (AttachFile) attachview.getSelectedItem();
        attachFiles.remove(selectedItem);

        File file = IOUtil.getExternalFile(Consts.PATH_TEMP + selectedItem.getFileName());
        if (file.exists()) {
            file.delete();
        }
        if (attachFiles.size() == 0) {
            attachLayoutView.setVisibility(View.GONE);
        } else {
            attachFileAdapter.notifyDataSetChanged();
            AttachFile attachFile = (AttachFile) attachview.getSelectedItem();
            attachName.setText(attachFile.getFileName());
        }
        return super.onContextItemSelected(item);
    }

    private class getCategoryData extends UserTask<Object, Void, List<Category>> {
        @Override
        public List<Category> doInBackground(Object... params) {
            try {
                return Inote.instance.getCategoryList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(List<Category> items) {
            ArrayAdapter<String> adapter;
            categoryList = items;
            if (items != null) {
                List<String> values = new ArrayList<String>();
                for (Category item : items) {
                    values.add(item.getName() + (item.getAccessLevel() == 0 ? getString(R.string.category_private_type) : getString(R.string.category_public_type)));
                }
                adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnCategory.setAdapter(adapter);
                int cate_id = _note.getCate_id();
                int index = 0;
                for (Category category : categoryList) {
                    if (cate_id == category.get_ID()) break;
                    index++;
                }
                spnCategory.setSelection(index);
            }
        }
    }

    private class getNoteAttachFiles extends UserTask<Object, Void, List<AttachFile>> {
        @Override
        public List<AttachFile> doInBackground(Object... params) {
            try {
                return Inote.instance.getNoteAttachFileList(_note.get_ID());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(List<AttachFile> result) {
            if (result != null) {
                attachFiles = result;
                showAttachFile(attachFiles);
            }
        }
    }


    private class submitData extends UserTask<Object, Void, Boolean> {
        boolean successForNote = false;

        @Override
        public Boolean doInBackground(Object... params) {
            Category category = (Category) params[3];
            String newContent = isRichText ? _note.getContent() + "<br/>" + params[1].toString().replace("\n", "<br/>") : params[1].toString();
            String title = params[0].toString().length() == 0 ? StringUtil.left(newContent, 20, "") : params[0].toString();
            String tag = params[2].toString();
            int _nid = category.get_ID();
            Inote.setNeedReloadCategory(true);
            int cate_id = _note.getCate_id();
            try {
                _note.setTitle(title);
                _note.setContent(newContent);
                _note.setAbstract(StringUtil.left(newContent.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "), 40, "..."));
                _note.setTags(tag);
                _note.setCategoryID(category.getNoteCategoryID());
                _note.setCate_id(_nid);
                _note.setUpdateTime(String.valueOf(System.currentTimeMillis()));
                Inote.instance.updateNote(_note);
                if (cate_id != _nid) {
                    Inote.instance.updateCategoryCount(category);
                    Inote.instance.updateCategoryCount(Inote.instance.getCategoryBy_id(cate_id));
                }
                Inote.instance.addSyncChangeLocal(String.valueOf(_note.get_ID()), Consts.CHANGE_CATEGORY_NOTE, Consts.CHANGE_UPDATE);
                successForNote = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                for (AttachFile attachFile : appendAttachFiles) {
                    File file = attachFile.getFile();
                    int _id = _note.get_ID();
                    File file1 = IOUtil.getExternalFile(Consts.PATH_FILE_LOCALCACHE + _id + "/" + attachFile.getFileName());
                    try {
                        IOUtil.move(file, file1);
                        Inote.instance.addAttachFileToNote(attachFile, _note);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return successForNote;
        }

        public void onPostExecute(Boolean bool) {
            Inote.instance.startSync();
            removeDialog(DIALOG_SAVE_NOTE);
            if (bool) {
                showToast(getString(R.string.note_save_success));
            } else {
                showToast(getString(R.string.note_save_error));
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.noteadd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_append_attach: {
                showDialog(DIALOG_APPEND_ATTACH_ALERT);
                break;
            }
            case R.id.menu_save: {
                onSubmit();
                break;
            }
            case R.id.menu_cancel: {
                finish();
                break;
            }
        }
        return true;
    }

    private void showToast(String str) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        mToast.show();
    }

    private class createUploadFile extends UserTask<String, Void, File> {
        @Override
        public File doInBackground(String... params) {
            try {
                File file1 = new File(params[0]);
                File tempFile = IOUtil.copyFileToLocalTempFolder(new FileInputStream(file1), file1.getName());
                file1.delete();
                return tempFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(File file) {
            removeDialog(DIALOG_COPY_LOCAL_FILE);
            if (file == null) {
                showToast(getString(R.string.file_copy_error));
            } else {
                addFileToAttachFileList(file);
            }
        }
    }

    private class createAudioFile extends UserTask<Uri, Void, File> {
        @Override
        public File doInBackground(Uri... params) {
            try {
                ContentResolver cr = getContentResolver();
                InputStream inputStream = cr.openInputStream(params[0]);
                return IOUtil.copyFileToLocalTempFolder(inputStream, StringUtil.getDataFormatFileName("audio_") + ".amr");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(File file) {
            removeDialog(DIALOG_COPY_LOCAL_FILE);
            if (file == null) {
                showToast(getString(R.string.file_copy_error));
            } else {
                addFileToAttachFileList(file);
            }
        }
    }

    private void addFileToAttachFileList(File file) {
        AttachFile attachFile = new AttachFile();
        attachFile.setFileName(file.getName());
        attachFile.setFile(file);
        attachFiles.add(attachFile);
        appendAttachFiles.add(attachFile);
        if (attachFiles.size() == 1) {
            noteTitleView.setText(file.getName());
        }
        showAttachFile(attachFiles);
    }

    private void showAttachFile(List<AttachFile> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        attachLayoutView.setVisibility(View.VISIBLE);
        attachFileAdapter.setAttachFileList(items);
        attachFileAdapter.notifyDataSetChanged();
    }


    private class downloadAttachFiles extends UserTask<AttachFile, Void, String> {
        @Override
        public String doInBackground(AttachFile... params) {
            try {
                AttachFile file = params[0];
                if (file != null) {
                    String noteID = _note.getNoteID();
                    String flag = (StringUtil.hasText(noteID)) ? noteID : String.valueOf(_note.get_ID());
                    String name = file.getFileName();
                    return IOUtil.saveAttachFile2CacheFolder(Consts.FILE_URL_BASE + Uri.encode(file.getFileDownPath(), "\\"), name, flag, false);
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
            removeDialog(DIALOG_LOADING);
        }

    }

}

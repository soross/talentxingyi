package com.snda.inote.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
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
import com.snda.inote.model.User;
import com.snda.inote.util.IOUtil;
import com.snda.inote.util.IntentUtils;
import com.snda.inote.util.StringUtil;
import com.snda.inote.util.UserTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class AddFullActivity extends Activity {

    private final static int DIALOG_SAVE_NOTE = 0;
    private final static int DIALOG_COPY_LOCAL_FILE = 1;
    private final static int DIALOG_CANCEL_ALERT = 2;
    private final static int DIALOG_APPEND_ATTACH_ALERT = 12;
    private final static int NEED_LOGIN_REQUEST = 10;

    private final static int IMAGE_CAPTURE_REQUEST = 3;
    private final static int AUDIO_RECORD_REQUEST = 4;
    private static final int FILE_SELECTOR_REQUEST = 6;

    private EditText noteTitleView;
    private EditText noteBodyView;
    private Spinner spnCategory;
    private EditText iptNoteTag;
    private TextView attachName;
    private Gallery attachview;
    private View attachLayoutView;
    private Dialog addDialog;
    private Toast mToast;

    private List<Category> categoryList;
    private final Activity context = AddFullActivity.this;

    private int cate_id;
    private String path;

    private List<AttachFile> attachFiles = new ArrayList<AttachFile>();

    private AttachFileAdapter attachFileAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noteadd);

        User user = Inote.getUser();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        noteTitleView = (EditText) findViewById(R.id.iptNoteTitle);
        noteBodyView = (EditText) findViewById(R.id.iptNoteBody);
        noteBodyView.requestFocus();

        spnCategory = (Spinner) findViewById(R.id.spnNoteCategory);
        iptNoteTag = (EditText) findViewById(R.id.iptNoteTag);
        attachLayoutView = findViewById(R.id.attachlayout);
        initAttachView();


        //showAttachFile(attachFiles);
        findViewById(R.id.btnNoteSubmit).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.btnNoteCancel).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.alertTextView).setVisibility(View.GONE);

        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            if (user == null || user.isOffLineUser()) {
                showToast(getString(R.string.alert_login));
                Bundle bundle = new Bundle();
                bundle.putInt("Request_CODE", 1);
                Intent intent1 = new Intent(context, LoginOAActivity.class);
                intent1.putExtras(bundle);
                startActivityForResult(intent1, NEED_LOGIN_REQUEST);
            }
            if (extras != null) {
                String title = extras.getString(Intent.EXTRA_TEXT);
                String content = extras.getString(Intent.EXTRA_SUBJECT);
                noteTitleView.setText(title == null ? "" : title);
                noteBodyView.setText(content == null ? "" : content);
            }
        } else {
            cate_id = intent.getIntExtra("cate_id", 0);
            showAddDialog();
        }

        if (!user.isOffLineUser()) {
            new getCategoryData().execute();
        }
        //SDOAnalyzeAgentInterface.onCreate(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //   SDOAnalyzeAgentInterface.onResume(context);
        User user = Inote.getUser();
        if (user == null || user.isOffLineUser()) {
            WelcomeActivity.show(context);
            finish();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SDOAnalyzeAgentInterface.onDestroy(context);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        SDOAnalyzeAgentInterface.onPause(context);
//    }

    private void initAttachView() {
        attachName = (TextView) findViewById(R.id.attachname);
        attachview = (Gallery) findViewById(R.id.attachview);
        attachview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                AttachFile attachFile = (AttachFile) attachFileAdapter.getItem(position);
                File file = attachFile.getFile();
                if (file.exists()) {
                    Inote.instance.openAttachFile(file);
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


        attachview.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.FIRST, 0, 0, getString(R.string.menu_remove_attach));
            }
        });

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


    private void showAddDialog() {
        addDialog = new Dialog(context, R.style.customDialog);
        addDialog.setContentView(R.layout.add);
        addDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cleanAndFinishActivity();
            }
        });
        ListView list = (ListView) addDialog.findViewById(R.id.menulist);
        list.setOnItemClickListener(onItemClickListener);
        list.setAdapter(new AddViewListAdapter(this));
        addDialog.show();
    }


    public static void show(Context context, int cate_id) {
        Intent intent = new Intent();
        intent.setClass(context, AddFullActivity.class);
        intent.putExtra("cate_id", cate_id);
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
                        cleanAndFinishActivity();
                    }
                    break;
            }
        }
    };

    private void onSubmit() {
        String noteTitle = this.noteTitleView.getText().toString();
        String noteBody = this.noteBodyView.getText().toString().replaceAll("\n", "<br/>");
        String noteTag = iptNoteTag.getText().toString();

        int pos = spnCategory.getSelectedItemPosition();

        if (noteTitle.trim().equals("") && noteBody.trim().equals("")) {
            Toast.makeText(AddFullActivity.this, getString(R.string.error_note_null), Toast.LENGTH_SHORT).show();
        } else {
            showDialog(DIALOG_SAVE_NOTE);
            Category category = categoryList.get(pos);
            new submitData().execute(noteTitle, noteBody, noteTag, category);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_COPY_LOCAL_FILE:
                ProgressDialog dialog = new ProgressDialog(AddFullActivity.this);
                dialog.setMessage(getString(R.string.note_copy_file));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
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
                ProgressDialog dialog1 = new ProgressDialog(AddFullActivity.this);
                dialog1.setMessage(getString(R.string.note_save));
                dialog1.setIndeterminate(true);
                dialog1.setCancelable(true);
                return dialog1;
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
                                cleanAndFinishActivity();
                            }
                        })
                        .create();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEED_LOGIN_REQUEST: {
                if (resultCode == Activity.RESULT_OK) {
                    new getCategoryData().execute();
                } else {
                    cleanAndFinishActivity();
                }
                break;
            }
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

        return noteTitle.trim().length() > 0 || noteBody.trim().length() > 0;
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
                int selectCate_id = cate_id;

                if (selectCate_id == 0) {
                    selectCate_id = Inote.instance.getDefaultPrivateCategory().get_ID();
                }

                int index = 0;
                for (Category category : categoryList) {
                    if (selectCate_id == category.get_ID()) break;
                    index++;
                }
                spnCategory.setSelection(index);
            }
        }
    }


    private class submitData extends UserTask<Object, Void, Integer> {
        boolean successForNote;

        @Override
        public Integer doInBackground(Object... params) {
            Integer step = 0;
            Category category = (Category) params[3];
            String newContent = params[1].toString();
            String title = params[0].toString().length() == 0 ? StringUtil.left(newContent, 20, "") : params[0].toString();
            String tag = params[2].toString();
            int _nid = category.get_ID();
            Inote.setNeedReloadCategory(true);
            Note note = new Note();
            boolean hasAttachFile = attachFiles.size() > 0;
            try {
                note.setHasAttachments(hasAttachFile);
                note.setTitle(title.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "));
                note.setContent(newContent);
                note.setTags(tag);
                note.setUpdateTime(String.valueOf(System.currentTimeMillis()));
                note.setCate_id(_nid);
                note.setCategoryID(category.getNoteCategoryID());
                note.setAbstract(StringUtil.left(newContent.replaceAll("\r\n", " ").replaceAll("\r", " ").replaceAll("\n", " "), 40, "..."));
                Inote.instance.addNote(note);
                Inote.instance.addSyncChangeLocal(String.valueOf(note.get_ID()), Consts.CHANGE_CATEGORY_NOTE, Consts.CHANGE_CREATE);
                successForNote = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Inote.instance.updateCategoryCount(category);
            }


            try {
                for (AttachFile attachFile : attachFiles) {
                    File file = attachFile.getFile();
                    int _id = note.get_ID();
                    File file1 = IOUtil.getExternalFile(Consts.PATH_FILE_LOCALCACHE + _id + "/" + attachFile.getFileName());
                    try {
                        IOUtil.move(file, file1);
                        Inote.instance.addAttachFileToNote(attachFile, note);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return note.get_ID();
        }

        public void onPostExecute(Integer _id) {
            Inote.instance.startSync();
            removeDialog(DIALOG_SAVE_NOTE);
            showToast(getString(R.string.note_save_success));
            NoteActivity.show(context, _id);
            cleanAndFinishActivity();
        }


    }

    private void cleanAndFinishActivity() {
        for (AttachFile attachFile : attachFiles) {
            File file = IOUtil.getExternalFile(Consts.PATH_TEMP + attachFile.getFileName());
            if (file.exists()) {
                file.delete();
            }
        }
        finish();
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
                cleanAndFinishActivity();
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


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            int id = Consts.ADD_TEXTS[position];
            switch (id) {
                case R.string.add_simple_note:
                    addDialog.dismiss();
                    AddSimpleActivity.show(context, cate_id);
                    cleanAndFinishActivity();
                    break;
                case R.string.add_full_note:
                    addDialog.dismiss();
                    break;
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

    private class createUploadFile extends UserTask<String, Void, File> {
        @Override
        public File doInBackground(String... params) {
            try {
                addDialog.dismiss();
                File file1 = new File(params[0]);
                return IOUtil.copyFileToLocalTempFolder(new FileInputStream(file1), file1.getName());
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
                addDialog.dismiss();
                ContentResolver cr = getContentResolver();
                Uri uri = params[0];
                Cursor cursor = cr.query(uri, new String[]{MediaStore.MediaColumns.TITLE}, null, null, null);
                String extension = "amr";
                if (cursor.moveToFirst()) {
                    do {
                       String fileName = cursor.getString(0);
                        if(fileName!=null){
                            String [] arr = fileName.split("\\.");
                            if(arr.length>1){
                                extension = arr[arr.length-1];
                            }
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();

                InputStream inputStream = cr.openInputStream(uri);
                return IOUtil.copyFileToLocalTempFolder(inputStream, StringUtil.getDataFormatFileName("audio_") + "."+extension);
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


}

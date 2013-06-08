package com.snda.inote.activity;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import com.snda.inote.R;
import com.snda.inote.model.AttachFile;
import com.snda.inote.model.Category;
import com.snda.inote.model.Note;
import com.snda.inote.service.MKSyncService;
import com.snda.inote.util.StringUtil;
import com.snda.inote.util.UserTask;

import java.io.File;
import java.util.List;


public class NoteListActivity extends ListActivity {
    private static final int DIALOG_LOADING = 0;
    private static final int DIALOG_DELETE = 1;


    private View viewLoading;
    private View viewEmpty;
    private Integer selectedPosition;
    private Toast mToast;
    private SyncServiceReceiver syncReceiver;
    private final Activity context = NoteListActivity.this;
    private Category category;

    private Cursor noteCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notelist);
        Intent intent = this.getIntent();
        int _id = intent.getIntExtra("id", 0);
        category = Inote.instance.getCategoryBy_id(_id);
        String categoryName = category.getName();

        TextView titleView = (TextView) findViewById(R.id.header_title);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListView().setSelection(0); //scroll to top
            }
        });

        titleView.setText(StringUtil.left(categoryName, 8, "..."));
        viewLoading = findViewById(R.id.load);
        viewEmpty = findViewById(R.id.empty);
        getListView().setEmptyView(viewLoading);
        getListView().setDividerHeight(1);
        getListView().setOnItemClickListener(onCursorItemClickListener);
        getListView().setOnCreateContextMenuListener(this);
        findViewById(R.id.header_left_btn).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.header_right_btn).setOnClickListener(handlerClickLisenter);

        syncReceiver = new SyncServiceReceiver();
        registerReceiver(syncReceiver, new IntentFilter(MKSyncService.SYNC_BROADCAST_KEY));


//        SDOAnalyzeAgentInterface.onCreate(context);
        //refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        //   SDOAnalyzeAgentInterface.onResume(context);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(syncReceiver);
        super.onDestroy();
        //    SDOAnalyzeAgentInterface.onDestroy(context);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//  //      SDOAnalyzeAgentInterface.onPause(context);
//    }

    public class SyncServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bunde = intent.getExtras();
            String key = bunde.getString("key");
            if (key.equalsIgnoreCase(Consts.SERVICE_MSG_ALL) || key.equalsIgnoreCase(Consts.SERVICE_MSG_CHANGE)) {
                refresh();
            }
        }
    }


    private void showData(Cursor cursor) {
        if (noteCursor == null) {
            noteCursor = cursor;
            ListAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.noteitem, // Use a template
                    noteCursor, // Give the cursor to the list adapter
                    new String[]{"title", "desc"}, // Map the NAME column in the
                    new int[]{R.id.note_title, R.id.note_abstract}); // The "text1" view defined in
            setListAdapter(adapter);
        } 

        if (noteCursor.getCount() == 0) {
            viewLoading.setVisibility(View.GONE);
            getListView().setEmptyView(viewEmpty);
        }
    }

    private void refresh() {
        if (noteCursor == null) {
            new loadLocalNoteListCursor().execute();
        } else {
            noteCursor.requery();
            ((SimpleCursorAdapter) getListAdapter()).notifyDataSetChanged();
            if (noteCursor.getCount() == 0) {
                viewLoading.setVisibility(View.GONE);
                getListView().setEmptyView(viewEmpty);
            }
        }
    }


    public static void show(Context context, int id) {
        Intent intent = new Intent();
        intent.putExtra("id", id);
        intent.setClass(context, NoteListActivity.class);
        context.startActivity(intent);
    }

    Button.OnClickListener handlerClickLisenter = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_right_btn:
                    AddFullActivity.show(context, category.get_ID());
                    break;
                case R.id.header_left_btn:
                    finish();
                    break;
            }
        }
    };


    private OnItemClickListener onCursorItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long arg3) {
            Note note = getNoteFromCursor(parent, position);
            NoteActivity.show(context, note.get_ID());
        }
    };

    private Note getNoteFromCursor(AdapterView<?> parent, int position) {
        if (position >= 0) {
            final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            Note note = new Note();
            note.setNoteID(cursor.getString(0));
            note.setTitle(cursor.getString(1));
            note.setAbstract(cursor.getString(2));
            note.setUpdateTime(cursor.getString(3));
            note.set_ID(cursor.getInt(4));
            note.setHasAttachments(cursor.getInt(5) == 1);
            note.setCate_id(cursor.getInt(6));
            return note;
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notelist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fresh: {
                sync();
                break;
            }
            case R.id.menu_new: {
                AddFullActivity.show(context, category.get_ID());
                break;
            }
            case R.id.menu_search: {
                startSearch("", false, null, false);
                break;
            }
        }
        return true;
    }

    public void sync() {
        showToast(getString(R.string.sync_begin));
        Inote.instance.startSync();
    }

    public enum NoteMenu {
        DETAIL, EDIT, DELETE
    }

    @Override
    public void onCreateContextMenu(ContextMenu conMenu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedPosition = info.position;
        conMenu.add(Menu.FIRST, NoteMenu.DETAIL.ordinal(), NoteMenu.DETAIL.ordinal(), getString(R.string.view_note));
        conMenu.add(Menu.FIRST, NoteMenu.EDIT.ordinal(), NoteMenu.EDIT.ordinal(), getString(R.string.edit_note));
        conMenu.add(Menu.FIRST, NoteMenu.DELETE.ordinal(), NoteMenu.DELETE.ordinal(), getString(R.string.del_note));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Note note = getNoteFromCursor(getListView(), selectedPosition);
        note = Inote.instance.getNoteBy_id(note.get_ID());
        if (item.getItemId() == NoteMenu.DETAIL.ordinal()) {
            NoteActivity.show(context, note.get_ID());
        } else if (item.getItemId() == NoteMenu.EDIT.ordinal()) {
            EditFullActivity.show(context, note);
        } else {
            showDialog(DIALOG_DELETE);
        }
        return false;
    }

    private void showToast(String str) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        mToast.show();
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
                                new deleteNote().execute(getNoteFromCursor(getListView(), selectedPosition).get_ID());
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            case DIALOG_LOADING:
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage(getString(R.string.str_wait));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            default:
                return null;
        }
    }


    private class deleteNote extends UserTask<Integer, Void, Boolean> {
        @Override
        public Boolean doInBackground(Integer... params) {
            int _id = Integer.valueOf(params[0]);
            Note note = Inote.instance.getNoteBy_id(_id);
            Inote.setNeedReloadCategory(true);
            String noteID = note.getNoteID();
            try {
                Inote.instance.deleteNoteBy_id(_id);
                List<AttachFile> noteAttachFileList = Inote.instance.getNoteAttachFileList(_id);
                for (AttachFile attachFile : noteAttachFileList) {
                    File file = attachFile.getFile();
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                }
                Inote.instance.deleteAttachFileByNote_Id(_id);
                if (StringUtil.hasText(noteID)) {
                    Inote.instance.addSyncChangeLocal(noteID, 1, 3);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Inote.instance.updateCategoryCount(category);
            }

            return false;
        }

        public void onPostExecute(Boolean n) {
            Inote.instance.startSync();
            removeDialog(DIALOG_LOADING);
            showToast(getString(R.string.str_delete_sucess));
            refresh();
        }
    }


    private class loadLocalNoteListCursor extends UserTask<Integer, Void, Cursor> {
        @Override
        public Cursor doInBackground(Integer... params) {
            return Inote.instance.getNoteListCursorByCateId(category.get_ID());
        }

        public void onPostExecute(Cursor cursor) {
            startManagingCursor(cursor);
            showData(cursor);
        }
    }
}

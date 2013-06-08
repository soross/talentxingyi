package com.snda.inote.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.snda.inote.Inote;
import com.snda.inote.R;
import com.snda.inote.model.Note;
import com.snda.inote.util.StringUtil;
import com.snda.inote.util.UserTask;

/*
 * @Author KevinComo@gmail.com
 * 2010-6-29
 */

public class SearchListActivity extends ListActivity {
    private View viewLoading;
    private View viewEmpty;
    
    private String searchKey;
    private final Activity context = SearchListActivity.this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notelist);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchKey = intent.getStringExtra(SearchManager.QUERY);
        }

        TextView titleView = (TextView) findViewById(R.id.header_title);
        titleView.setText(getString(R.string.search_label_text) + StringUtil.left(searchKey, 5, "..."));

        viewLoading = findViewById(R.id.load);
        viewEmpty = findViewById(R.id.empty);
        viewEmpty.setVisibility(View.GONE);
        getListView().setEmptyView(viewLoading);
        getListView().setOnItemClickListener(onCursorItemClickListener);
        new getData().execute(searchKey);

        findViewById(R.id.header_left_btn).setOnClickListener(handlerClickLisenter);
        findViewById(R.id.header_right_btn).setOnClickListener(handlerClickLisenter);
     //   SDOAnalyzeAgentInterface.onCreate(context);
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        SDOAnalyzeAgentInterface.onResume(context);
//    }

    Button.OnClickListener handlerClickLisenter = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_right_btn:
                    AddFullActivity.show(context, 0);
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
                NoteActivity.show(context, note.get_ID());
            }
        }
    };

    private class getData extends UserTask<String, Void, Cursor> {

        @Override
        public Cursor doInBackground(String... params) {
            try {
                return Inote.instance.getNoteListCursorByKey(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(Cursor cursor) {
            showData(cursor);
        }
    }


    private void showData(Cursor cursor) {
        if(cursor == null || cursor.getCount() == 0){
            viewLoading.setVisibility(View.GONE);
            getListView().setEmptyView(viewEmpty);
            return;
        }
        ListAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.noteitem, // Use a template
                cursor, // Give the cursor to the list adapter
                new String[]{"title", "desc"}, // Map the NAME column in the
                new int[]{R.id.note_title, R.id.note_abstract}); // The "text1" view defined in
        setListAdapter(adapter);
    }
}

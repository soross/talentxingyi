package com.snda.inote.activity;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import com.snda.inote.R;
import com.snda.inote.adapter.CategoryAdapter;
import com.snda.inote.io.Setting;
import com.snda.inote.model.Category;
import com.snda.inote.model.User;
import com.snda.inote.service.MKSyncService;
import com.snda.inote.util.UserTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {
    private final Activity context = MainActivity.this;

    private SyncServiceReceiver syncReceiver;

    private ImageButton btnRefresh;
    private RotateAnimation animRefresh;
    private Toast mToast;
    private List<Category> categorys;
    private Category selectedCategory;

    private static final int DIALOG_LOADING = 0;
    private static final int DIALOG_ABOUT = 1;
    private static final int DIALOG_EDIT = 2;
    private static final int DIALOG_DELETE = 3;
    private static final int DIALOG_ADD_CATEGORY = 4;
    private final static int UPDATE_DIALOG = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        getListView().setEmptyView(findViewById(R.id.load));
        getListView().setOnItemClickListener(onItemClickListener);
        getListView().setOnCreateContextMenuListener(this);
        findViewById(R.id.header_left_btn).setOnClickListener(handlerClickLisenter);
        btnRefresh = (ImageButton) findViewById(R.id.header_right_btn);
        btnRefresh.setOnClickListener(handlerClickLisenter);
        animRefresh = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.progress_image);

        syncReceiver = new SyncServiceReceiver();
        registerReceiver(syncReceiver, new IntentFilter(MKSyncService.SYNC_BROADCAST_KEY));
        refresh();
        sync();
        //SDOAnalyzeAgentInterface.onCreate(context);
        if (!Inote.needUpdateApp) {
            Inote.instance.startVersionCheck();
        }
    }

    @Override
    protected void onResume() {
//        SDOAnalyzeAgentInterface.onResume(context);
        if (Inote.needReloadCategory) {
            refresh();
            Inote.setNeedReloadCategory(false);
        }

        if (Inote.needreshowUpdateAppAlert) {
            if (Inote.needUpdateApp) {
                showDialog(UPDATE_DIALOG);
                Inote.needreshowUpdateAppAlert = false;
            }
        }
        super.onResume();

        User user = Inote.getUser();
        if (user == null || user.isOffLineUser()) {
            WelcomeActivity.show(context);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(syncReceiver);
        super.onDestroy();
        Inote.needreshowUpdateAppAlert = true;
        //SDOAnalyzeAgentInterface.onDestroy(context);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //SDOAnalyzeAgentInterface.onPause(context);
    }

    private void showData(List<Category> items) {
        if (items.size() != 0) {
            categorys = new ArrayList<Category>();
            List<Category> privateList = new ArrayList<Category>();
            List<Category> publicList = new ArrayList<Category>();
            for (Category category : items) {
                if (category.getAccessLevel() == 0) {
                    privateList.add(category);
                } else {
                    publicList.add(category);
                }
            }

            Category item = new Category();
            item.setName(getString(R.string.private_category_section_title));
            item.setIsGroupName(true);
            categorys.add(item);
            categorys.addAll(privateList);

            Category publicItem = new Category();
            publicItem.setName(getString(R.string.public_category_section_title));
            publicItem.setIsGroupName(true);
            categorys.add(publicItem);
            categorys.addAll(publicList);

            if (getListAdapter() == null) {
                CategoryAdapter adapter = new CategoryAdapter(context, categorys);
                setListAdapter(adapter);
            } else {
                CategoryAdapter listAdapter = (CategoryAdapter) getListAdapter();
                listAdapter.updateCategoryList(categorys);
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public class SyncServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bunde = intent.getExtras();
            String key = bunde.getString("key");
            if (key.equalsIgnoreCase(Consts.SERVICE_MSG_CATEGORY)) {
                refresh();
                showToast(getString(R.string.loading_note_to_local));
            } else if (key.equalsIgnoreCase(Consts.SERVICE_MSG_ALL)) {
                stopSyncAnimation();
                showToast(getString(R.string.sync_sucess));
                refresh();
            } else if (key.equalsIgnoreCase(Consts.SERVICE_MSG_CHANGE)) {
                refresh();
            } else if (key.equals(Consts.SERVICE_NOT_CONNECTION_ERROR)) {
                stopSyncAnimation();
                showToast(getString(R.string.sync_not_connection_error));
            } else if (key.equals(Consts.SERVICE_MSG_REQUEST_ERROR)) {
                stopSyncAnimation();
                showToast(getString(R.string.api_error));
            } else if (key.equals(Consts.SERVICE_MSG_IOERROR)) {
                stopSyncAnimation();
                showToast(getString(R.string.sync_socket_connection_error));
            } else if (key.equalsIgnoreCase(Consts.SERVICE_MSG_ERROR)) {
                stopSyncAnimation();
                String value = bunde.getString("value");
                if (value != null) {
                    showToast(value); //FIX check and add the full error info
                }
            } else {
                stopSyncAnimation();
            }
        }


    }

    public static void show(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

    Button.OnClickListener handlerClickLisenter = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.header_right_btn:
                    sync();
                    break;
                case R.id.header_left_btn:
                    AddFullActivity.show(context, 0);
                    break;
            }
        }
    };


    private void refresh() {
        showData(Inote.instance.getCategoryList());
    }

    private void sync() {
        startSyncAnimation();
        Inote.instance.startSync();
    }

    private void startSyncAnimation() {
        animRefresh.setRepeatCount(Animation.INFINITE);
        btnRefresh.startAnimation(animRefresh);
    }

    private void stopSyncAnimation() {
        animRefresh.setRepeatCount(0);
    }

    /*
      * set item click
      */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long arg3) {
            Category category = categorys.get(position);
            NoteListActivity.show(context, category.get_ID());
        }
    };

    /*
      * Create Menu
      * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
      */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_note: {
                AddFullActivity.show(context, 0);
                break;
            }
            case R.id.menu_add_category: {
                //sync();
                showDialog(DIALOG_ADD_CATEGORY);
                break;
            }
            case R.id.menu_refresh: {
                sync();
                break;
            }
            case R.id.menu_search: {
                startSearch("", false, null, false);
                break;
            }
            case R.id.menu_about: {
                showDialog(DIALOG_ABOUT);
                break;
            }
            case R.id.menu_logout: {
                Setting.delUser(context);
                Inote.setUser(null);
                Inote.instance.stopSync();
                sendBroadcast(new Intent("com.sdo.note.loginout"));
                WelcomeActivity.show(context);
                finish();
                break;
            }
            case R.id.menu_quit: {
                Inote.instance.stopSync();
                System.exit(0);
                break;
            }
        }
        return true;
    }


    /*
    * Create Context Menu
    */
    public enum CategoryMenu {
        ADD_CATEGORY, EDIT_CATEGORY, DELETE_CATEGORY
    }

    @Override
    public void onCreateContextMenu(ContextMenu conMenu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Integer selectedPosition = info.position;
        selectedCategory = categorys.get(selectedPosition);
//        conMenu.setHeaderTitle(selectedCategory.getName());
//        conMenu.add(Menu.FIRST, CategoryMenu.ADD_CATEGORY.ordinal(), CategoryMenu.ADD_CATEGORY.ordinal(), getString(R.string.title_note_category_add));
        if (!selectedCategory.getIsDefault()) {
            conMenu.add(Menu.FIRST, CategoryMenu.EDIT_CATEGORY.ordinal(), CategoryMenu.EDIT_CATEGORY.ordinal(), getString(R.string.title_category_edit));
            //conMenu.add(Menu.FIRST, CategoryMenu.DELETE_CATEGORY.ordinal(), CategoryMenu.DELETE_CATEGORY.ordinal(), getString(R.string.title_category_delete));
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CategoryMenu.ADD_CATEGORY.ordinal()) {
            //AddSimpleActivity.show(context, selectedCategory.getNoteCategoryID());
//            AddFullActivity.show(context,0);
        } else if (item.getItemId() == CategoryMenu.EDIT_CATEGORY.ordinal()) {
//            if (categoryNameEditText != null) {
//                categoryNameEditText.setText(selectedCategory.getName());
//            }
            showDialog(DIALOG_EDIT);
        } else if (item.getItemId() == CategoryMenu.DELETE_CATEGORY.ordinal()) {
            showDialog(DIALOG_DELETE);
        }
        return false;
    }


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ABOUT:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(getString(R.string.app_name) + " v" + Inote.clientVesion)
                        .setMessage(R.string.about_us)
                        .setPositiveButton(R.string.ok, null)
                        .create();
            case DIALOG_EDIT:
                return getCategoryEditDialog(DIALOG_EDIT);
            case DIALOG_ADD_CATEGORY:
                return getCategoryEditDialog(DIALOG_ADD_CATEGORY);
            case DIALOG_DELETE:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.title_category_delete)
                        .setMessage(R.string.title_category_deleteTip)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showDialog(DIALOG_LOADING);
                                new deleteCategory().execute(selectedCategory.get_ID().toString());
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();
            case DIALOG_LOADING:
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage(getString(R.string.please_wait_for_load_dialog));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            case UPDATE_DIALOG:
                return new AlertDialog.Builder(context)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.not_save_note_alert_title)
                        .setMessage(getString(R.string.update_app_tip))
                        .setPositiveButton(R.string.not_save_note_btn_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Inote.instance.openAttachFile(new File(Inote.appPath));
                            }
                        })
                        .setNegativeButton(R.string.not_save_note_btn_no, null).create();
            default:
                return null;
        }
    }

    private Dialog getCategoryEditDialog(final int type) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textView = factory.inflate(R.layout.dialogcategoryedit, null);
        final EditText categoryNameEditText = (EditText) textView.findViewById(R.id.iptCategoryName);
        final Spinner categoryTypeSpinner = (Spinner) textView.findViewById(R.id.categoryType);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, new String[]{"私有", "公开"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryTypeSpinner.setAdapter(adapter);
        if (type == DIALOG_EDIT) {
            categoryNameEditText.setText(selectedCategory.getName());
            categoryTypeSpinner.setSelection(selectedCategory.getAccessLevel());//simple but not safe
        } else {
            categoryNameEditText.setText("");
            categoryTypeSpinner.setSelection(0);//simple but not safe
        }
        return new AlertDialog.Builder(context)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.title_category_edit)
                .setView(textView)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name = categoryNameEditText.getText().toString();
                        String selectText = categoryTypeSpinner.getSelectedItem().toString();
                        String categotyType = selectText.equals(getString(R.string.public_category)) ? "1" : "0";
                        showDialog(DIALOG_LOADING);
                        if (type == DIALOG_EDIT) {
                            new updateCategoryTask().execute(name, categotyType);
                        } else {
                            new addCategory().execute(name, categotyType);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    private class updateCategoryTask extends UserTask<String, Void, Boolean> {
        @Override
        public Boolean doInBackground(String... params) {
            String name = params[0];
            String type = params[1];

            try {
                selectedCategory.setName(name);
                selectedCategory.setAccessLevel(Integer.valueOf(type));
                Inote.instance.updateCategory(selectedCategory);
                Inote.instance.addSyncChangeLocal(selectedCategory.get_ID().toString(), Consts.CHANGE_CATEGORY_CATEGORY, Consts.CHANGE_UPDATE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public void onPostExecute(Boolean result) {
            refresh();
            sync();
            removeDialog(DIALOG_LOADING);
            ((CategoryAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }

    private class addCategory extends UserTask<String, Void, Boolean> {
        @Override
        public Boolean doInBackground(String... params) {
            String name = params[0];
            String type = params[1];
            try {
                Category category = new Category();
                category.setName(name);
                category.setAccessLevel(Integer.valueOf(type));
                Inote.instance.addCategory(category);
                Inote.instance.addSyncChangeLocal(category.get_ID().toString(), Consts.CHANGE_CATEGORY_CATEGORY, Consts.CHANGE_CREATE);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public void onPostExecute(Boolean result) {
            refresh();
            sync();
            removeDialog(DIALOG_LOADING);
            ((CategoryAdapter) getListAdapter()).notifyDataSetChanged();
        }
    }


    private class deleteCategory extends UserTask<String, Void, Boolean> {
        @Override
        public Boolean doInBackground(String... params) {
            String noteCategoryID = selectedCategory.getNoteCategoryID();
            try {
                Inote.instance.deleteCategory(selectedCategory.get_ID());
                if (noteCategoryID != null && !"".equals(noteCategoryID)) {
                    Inote.instance.addSyncChangeLocal(noteCategoryID, Consts.CHANGE_CATEGORY_CATEGORY, Consts.CHANGE_DELETE);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public void onPostExecute(Boolean result) {
            sync();
            refresh();
            removeDialog(DIALOG_LOADING);
            ((CategoryAdapter) getListAdapter()).notifyDataSetChanged();
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

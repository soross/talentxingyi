package com.snda.inote.fileselector;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.snda.inote.Consts;
import com.snda.inote.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * @Author KevinComo@gmail.com
 * 2010-6-30
 */

public class FileSelector extends ListActivity {

    public static final int SUCCESS = 1;

    private String[] fileTypes = {};

    private FileAdapter fAdapter;
    private FileSelector context = FileSelector.this;
    //用静态变量保存上次选择的目录
    private static File curPath = new File("/sdcard/");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileTypes = Consts.FILE_UPLOAD_ALLOW;

        fAdapter = new FileAdapter(this);
        setListAdapter(fAdapter);
        if (!curPath.exists()) curPath = new File("/");
        ListFile(curPath);

        setTitle(curPath.getPath());
        getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                FileData fd = fAdapter.getItem(position);
                if (fd.getType() == FileType.UP) {
                    curPath = new File(curPath.getParent());
                    setTitle(curPath.getPath());
                    ListFile(curPath);
                } else if (fd.getType() == FileType.FOLDER) {
                    curPath = new File(curPath.getPath() + "/" + fd.getName() + "/");
                    setTitle(curPath.getPath());
                    ListFile(curPath);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("FileName", curPath.getPath() + "/" + fd.getName());
                    setResult(FileSelector.SUCCESS, intent);
                    finish();
                }
            }
        });
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                return false;
            }

        });
    }

    public static void show(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FileSelector.class);
        ((Activity) context).startActivityForResult(intent, FileSelector.SUCCESS);
    }

    public static String getResult(Intent intent) {
        return intent.getStringExtra("FileName");
    }

    @Override
    public void onBackPressed() {
        String path = curPath.getParent();
        if (path != null) {
            curPath = new File(curPath.getParent());
            setTitle(curPath.getPath());
            ListFile(curPath);
        }else{
            super.onBackPressed();
        }


    }


    private void ListFile(File directory) {
        fAdapter.clearItems();
        if (!directory.getPath().equals("/")) {
            FileData fd = new FileData();
            fd.setName("..");
            fd.setType(FileType.UP);
            fAdapter.addItem(fd);
        }
        FileData fd;
        File[] fs = directory.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if (f.isDirectory()) {
                    fd = new FileData();
                    fd.setName(f.getName());
                    fd.setType(FileType.FOLDER);
                    fAdapter.addItem(fd);
                } else {
                    String type = checkType(f.getName().toLowerCase());
                    if (type != null) {
                        fd = new FileData();
                        fd.setName(f.getName());
                        fd.setType(type);
                        fAdapter.addItem(fd);
                    }
                }
            }
        }
        fAdapter.notifyDataSetChanged();
        getListView().postInvalidate();
    }

    private String checkType(String name) {
        for (String a : fileTypes) {
            if (name.endsWith(a)) return a;
        }
        return null;
    }

    private static class FileAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<FileData> mItems = new ArrayList<FileData>();

        public FileAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
        }

        public void addItem(FileData item) {
            mItems.add(item);
        }

        public FileType getItemType(int i) {
            return getItem(i).getType();
        }

        public void clearItems() {
            mItems.clear();
        }

        public int getCount() {
            return mItems.size();
        }

        public FileData getItem(int i) {
            return mItems.get(i);
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.fileselectoritem, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            FileData fd = getItem(position);
            holder.text.setText(fd.getName());
            holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), fd.getIcon()));
            return convertView;
        }

        static class ViewHolder {
            TextView text;
            ImageView icon;
        }
    }

}

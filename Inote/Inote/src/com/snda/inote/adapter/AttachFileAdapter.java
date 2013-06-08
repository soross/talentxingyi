package com.snda.inote.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import com.snda.inote.R;
import com.snda.inote.model.AttachFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-19
 * Time: 下午2:22
 */
public class AttachFileAdapter extends BaseAdapter {

    //    int mGalleryItemBackground;
    private Activity mContext;
    private List<AttachFile> attachFileList = new ArrayList<AttachFile>();

    public AttachFileAdapter(Context c) {
        mContext = (Activity) c;
        // See res/values/attrs.xml for the <declare-styleable> that defines
        // Gallery1.
        //TypedArray a = mContext.obtainStyledAttributes(R.styleable.Gallery1);
//        mGalleryItemBackground = a.getResourceId(
//                R.styleable.Gallery1_android_galleryItemBackground, 0);
//        a.recycle();
    }

    public int getCount() {
        return attachFileList.size();
    }

    public Object getItem(int position) {
        return attachFileList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AttachFile file = attachFileList.get(position);
        ImageView i = convertView != null ? (ImageView) convertView : new ImageView(mContext);

        i.setImageResource(getIcon(checkType(file.getFileName().toLowerCase())));
        i.setScaleType(ImageView.ScaleType.CENTER);
        i.setLayoutParams(new Gallery.LayoutParams(50, 50));

        // The preferred Gallery item background
//        i.setBackgroundResource(mGalleryItemBackground);

        return i;
    }


    public void setAttachFileList(List<AttachFile> attachFileList) {
        this.attachFileList = attachFileList;
    }



    private String checkType(String name) {
        for (String a : iconKey) {
            if (name.endsWith(a)) return a;
        }
        return "generic";
    }

    public int getIcon(String name) {
        int indexInKey = getIndexInKey(name);
        if(indexInKey==-1){
            return R.drawable.generic;
        }
        return iconValue[indexInKey];
    }

    private static int getIndexInKey(String key) {
        for (int i = 0; i < iconKey.length; i++) {
            if (key.equalsIgnoreCase(iconKey[i])) return i;
        }
        return -1;
    }


    private static final int[] iconValue = {
            R.drawable.ai,
            R.drawable.asp,
            R.drawable.avi,
            R.drawable.bmp,
            R.drawable.css,
            R.drawable.doc,
            R.drawable.doc,
            R.drawable.exe,
            R.drawable.fla,
            R.drawable.folder,
            R.drawable.gif,
            R.drawable.html,
            R.drawable.jpg,
            R.drawable.js,
            R.drawable.jsp,
            R.drawable.mid,
            R.drawable.mov,
            R.drawable.mp3,
            R.drawable.mp4,
            R.drawable.mpeg,
            R.drawable.mpg,
            R.drawable.pdf,
            R.drawable.php,
            R.drawable.png,
            R.drawable.ppt,
            R.drawable.psd,
            R.drawable.ram,
            R.drawable.rar,
            R.drawable.swf,
            R.drawable.tiff,
            R.drawable.txt,
            R.drawable.up,
            R.drawable.vsd,
            R.drawable.wav,
            R.drawable.wma,
            R.drawable.xls,
            R.drawable.zip,
            R.drawable.generic
    };
    private static final String[] iconKey = {
            "ai",
            "asp",
            "avi",
            "bmp",
            "css",
            "doc",
            "docx",
            "exe",
            "fla",
            "folder",
            "gif",
            "html",
            "jpg",
            "js",
            "jsp",
            "mid",
            "mov",
            "mp3",
            "mp4",
            "mpeg",
            "mpg",
            "pdf",
            "php",
            "png",
            "ppt",
            "psd",
            "ram",
            "rar",
            "swf",
            "tiff",
            "txt",
            "up",
            "vsd",
            "wav",
            "wma",
            "xls",
            "zip",
            "generic",
    };


}

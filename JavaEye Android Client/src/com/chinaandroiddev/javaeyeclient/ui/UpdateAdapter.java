package com.chinaandroiddev.javaeyeclient.ui;

import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.model.Update;
import com.chinaandroiddev.javaeyeclient.util.Formatter;
import com.chinaandroiddev.javaeyeclient.util.IOUtils;


public class UpdateAdapter extends ArrayAdapter {

    private static final String LOG_TAG = "UpdateAdapter";
    Activity context;
    LayoutInflater inflater;
    WebView userIcon;
    TextView username, replyTo, updateBody, via, timestamp;
    ArrayList<Update> updates;
    
    public UpdateAdapter(Activity context, ArrayList<Update> Updates) {  
        super(context, R.layout.update_row, Updates);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.updates = Updates;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {  
        View row = convertView;
        UpdateWrapper wrapper;  
        
        row = inflater.inflate(R.layout.update_row, null);  
        wrapper = new UpdateWrapper(row);  
        row.setTag(wrapper);
        row.setLongClickable(true);

        Update u = updates.get(position);
        
        username = wrapper.getUsername();
        username.setText(u.user.name);
        replyTo = wrapper.getReplyTo();
        replyTo.setText(u.receiver.name);
//        Log.e(LOG_TAG, "reply to: " + u.receiver.name);
        updateBody = wrapper.getUpdateBody();
        updateBody.setText(u.body);
        via = wrapper.getVia();
        via.setText("通过: " + u.via);
        timestamp = wrapper.getTimestamp();
        timestamp.setText(Formatter.sdf.format(u.createdTime));
        
        if (u.user.logo.length() > 5) {
            userIcon = wrapper.getUserIcon();
            userIcon.loadUrl(IOUtils.constructThumbnail(u.user.logo));
        }
        return row;  
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    
}

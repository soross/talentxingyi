package com.chinaandroiddev.javaeyeclient.ui;

import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.chinaandroiddev.javaeyeclient.R;

public class UpdateWrapper {
    View base;
    WebView userIcon;
    TextView username, replyTo, updateBody, via, timestamp;

    UpdateWrapper(View base) {  
        this.base=base;  
    }
    WebView getUserIcon() {
        if (userIcon == null) {
            userIcon = (WebView)base.findViewById(R.id.user_icon);
        }
        return userIcon;
    }
    TextView getUsername() {  
        if (username == null) {  
            username = (TextView)base.findViewById(R.id.username);  
        }  
        return username;  
    }
    TextView getReplyTo() {  
        if (replyTo == null) {  
            replyTo = (TextView)base.findViewById(R.id.reply_to);  
        }  
        return replyTo;  
    }
    TextView getUpdateBody() {  
        if (updateBody == null) {  
            updateBody = (TextView)base.findViewById(R.id.update_body);  
        }  
        return updateBody;  
    }
    TextView getVia() {  
        if (via == null) {  
            via = (TextView)base.findViewById(R.id.via);  
        }  
        return via;  
    }
    TextView getTimestamp() {  
        if (timestamp == null) {  
            timestamp = (TextView)base.findViewById(R.id.timestamp);  
        }  
        return timestamp;  
    }
}

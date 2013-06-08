package com.snda.inote.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.snda.inote.Consts;
import com.snda.inote.R;

/**
 * Created by IntelliJ IDEA.
 * User: tom
 * Date: 11-2-17
 * Time: 下午2:04
 */


public class AddViewListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private int type;

    public AddViewListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public AddViewListAdapter(Context context, int type) {
        mInflater = LayoutInflater.from(context);
        this.type = type;
    }

    public int getCount() {
        if (type == 0) {
            return Consts.ADD_TEXTS.length;
        } else {
            return Consts.APPEND_TEXTS.length;
        }
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.additem, null);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.add_text);
            holder.icon = (ImageView) convertView.findViewById(R.id.add_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (type == 0) {
            holder.text.setText(Consts.ADD_TEXTS[position]);
            holder.icon.setImageResource(Consts.ADD_ICONS[position]);
        }else {
            holder.text.setText(Consts.APPEND_TEXTS[position]);
            holder.icon.setImageResource(Consts.APPEND_ICONS[position]);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView text;
        ImageView icon;
    }

}

package com.chinaandroiddev.javaeyeclient.ui;

import java.util.ArrayList;

import com.chinaandroiddev.javaeyeclient.R;
import com.chinaandroiddev.javaeyeclient.model.FavoriteItem;
import com.chinaandroiddev.javaeyeclient.model.Update;
import com.chinaandroiddev.javaeyeclient.util.Formatter;
import com.chinaandroiddev.javaeyeclient.util.IOUtils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FavoriteAdapter extends ArrayAdapter<FavoriteItem> {

	private static final String LOG_TAG = "FavoriteAdapter";
    Activity context;
    LayoutInflater inflater;
    WebView userIcon;
    TextView username, replyTo, updateBody, via, timestamp;
    ArrayList<FavoriteItem> items;
	
	public FavoriteAdapter(Activity context, ArrayList<FavoriteItem> items) {  
	    super(context, R.layout.favorite_row, items);
	    inflater = LayoutInflater.from(context);
	    this.context = context;
	    this.items = items;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {        
		View row = inflater.inflate(R.layout.favorite_row, null);
		row.setLongClickable(true);
        TextView body = (TextView)row.findViewById(R.id.favorite_body);        
        body.setText(items.get(position).title);
        if (items.get(position).categoryNames!=null && items.get(position).categoryNames.size()>0) {
            StringBuilder sb = new StringBuilder();
            sb.append("分类: ");
            for (String s : items.get(position).categoryNames) {
                sb.append(s).append(", ");
            }
            String category = sb.toString();
            category = category.substring(0, category.length()-2).trim();
            TextView favoriteCategory = (TextView)row.findViewById(R.id.favorite_category); 
            favoriteCategory.setText(category);
            favoriteCategory.setVisibility(View.VISIBLE);
        }
        String description = items.get(position).description.trim();
//        if(description != null && description.equals("null")){//for bug of javaeye
//        	description = null;
//        }
        if (description!=null && description.length()>0) {
        	description.replaceAll("/n", "");
            TextView favoriteDescription = (TextView)row.findViewById(R.id.favorite_desc); 
            favoriteDescription.setText("描述: " + description);
            favoriteDescription.setVisibility(View.VISIBLE);
        }
        TextView favoriteUrl = (TextView)row.findViewById(R.id.favorite_url);
        favoriteUrl.setText(items.get(position).url);
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

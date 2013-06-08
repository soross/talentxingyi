package com.snda.inote.adapter;

import java.util.List;

import com.snda.inote.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/*
 * @Author KevinComo@gmail.com
 * 2010-7-5
 */

public class FileAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public List<String> mItems;
	
	public FileAdapter(Activity context, List<String> items) {
		this.mInflater = LayoutInflater.from(context);
		this.mItems = items;
	}
	
	public int getCount() {
		return mItems == null ? 0 : mItems.size();
	}

	public String getItem(int position) {
		if (mItems == null || mItems.size() <= position)
			return null;
		return mItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.fileitem, null);
			holder = new ViewHolder();
			holder.item_name = (TextView)convertView.findViewById(R.id.file_name);
			holder.item_option = (TextView)convertView.findViewById(R.id.file_option);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		convertView.setTag(holder);
		holder.item_name.setText(mItems.get(position));
		return convertView;
	}
	
	static class ViewHolder{
		TextView item_name;
		TextView item_option;
	}
	

}

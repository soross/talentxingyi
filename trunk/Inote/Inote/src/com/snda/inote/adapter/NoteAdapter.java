package com.snda.inote.adapter;

import java.util.List;

import android.util.Log;
import com.snda.inote.R;
import com.snda.inote.model.Note;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	public List<Note> mItems;
	
	public NoteAdapter(Activity context, List<Note> items){
		this.mInflater = LayoutInflater.from(context);
		this.mItems = items;
	}


	public int getCount() {
		return mItems == null ? 0 : mItems.size();
	}

	public Object getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.noteitem, null);
			holder = new ViewHolder();
			holder.item_title = (TextView)convertView.findViewById(R.id.note_title);
			holder.item_abstract = (TextView)convertView.findViewById(R.id.note_abstract);
			holder.item_time = (TextView)convertView.findViewById(R.id.note_time);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		convertView.setTag(holder);
		Note note = mItems.get(position);
		holder.item_title.setText(note.getTitle());
		holder.item_abstract.setText(note.getAbstract());
		holder.item_time.setText(note.getUpdateDateTime().getMonth() + "月" + note.getUpdateDateTime().getDay());  //FIX remove the Deprecated method
		return convertView;
	}

	static class ViewHolder{
		TextView item_title;
		TextView item_abstract;
		TextView item_time;
	}

}

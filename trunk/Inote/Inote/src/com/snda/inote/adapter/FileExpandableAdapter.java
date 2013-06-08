package com.snda.inote.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.snda.inote.R;
import com.snda.inote.model.AttachFile;

import java.util.List;


public class FileExpandableAdapter extends BaseExpandableListAdapter {
	private LayoutInflater mInflater;
	public List<AttachFile> mItems;
    private Context mContext = null;

    public void setmItems(List<AttachFile> mItems) {
        this.mItems = mItems;
    }

    public FileExpandableAdapter(Activity context, List<AttachFile> items) {
		this.mInflater = LayoutInflater.from(context);
		this.mItems = items;
        mContext = context;
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
		holder.item_name.setText(mItems.get(position).getFileName());
		return convertView;
	}

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int i) {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getGroup(int i) {
        return "附件列表";
    }

    @Override
    public Object getChild(int i, int i1) {
        if (mItems == null || mItems.size() <= i1)
			return null;
		return mItems.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
       TextView text = new TextView(mContext);
		String name = "附件列表";
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 70);
		text.setLayoutParams(lp);
//		text.setTextSize(18);
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		text.setPadding(50, 0, 0, 0);
		text.setText(name);
		return text;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        TextView text = new TextView(mContext);
		String name = mItems.get(i1).getFileName();
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 70);
		text.setLayoutParams(lp);
//		text.setTextSize(18);
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		text.setPadding(50, 0, 0, 0);
		text.setText(name);
		return text;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    static class ViewHolder{
		TextView item_name;
		TextView item_option;
	}
	

}

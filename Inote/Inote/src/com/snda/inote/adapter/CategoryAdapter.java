package com.snda.inote.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.snda.inote.R;
import com.snda.inote.model.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public List<Category> mItems;

    public CategoryAdapter(Activity context, List<Category> items) {
        this.mItems = items;
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateCategoryList(List<Category> items){
        mItems = items;
    }

    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public Category getItem(int position) {
        if (mItems == null || mItems.size() <= position)
            return null;
        return mItems.get(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
//        return  true;
        return !mItems.get(position).getIsGroupName();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {  //FIX need add  view holder for better performance
        Category category = mItems.get(position);
        Object holder;
        if (convertView == null) {
            if (category.getIsGroupName()) {
                convertView = mInflater.inflate(R.layout.categorygroup, null);
                ViewHolderForGroup groupHolder = new ViewHolderForGroup();
                groupHolder.group_title = (TextView) convertView.findViewById(R.id.group_title);
                convertView.setTag(groupHolder);
                holder = groupHolder;
                //group_title.setText(category.getName());
            } else {
                convertView = mInflater.inflate(R.layout.categoryitem, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.category_title = (TextView) convertView.findViewById(R.id.category_title);
                viewHolder.category_num = (TextView) convertView.findViewById(R.id.category_num);
                convertView.setTag(viewHolder);
                holder = viewHolder;
            }
        } else {
            holder = convertView.getTag();
        }

        if (category.getIsGroupName()) {
            if (holder.toString().equals("view")) {
                convertView = mInflater.inflate(R.layout.categorygroup, null);
                ViewHolderForGroup groupHolder = new ViewHolderForGroup();
                groupHolder.group_title = (TextView) convertView.findViewById(R.id.group_title);
                convertView.setTag(groupHolder);
                holder = groupHolder;
            }
        } else {
            if (holder.toString().equals("group")) {
                convertView = mInflater.inflate(R.layout.categoryitem, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.category_title = (TextView) convertView.findViewById(R.id.category_title);
                viewHolder.category_num = (TextView) convertView.findViewById(R.id.category_num);
                convertView.setTag(viewHolder);
                holder = viewHolder;
            }
        }


        if (holder.toString().equals("view")) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.category_title.setText(category.getName());
            viewHolder.category_num.setText(String.valueOf(category.getNoteCount()));
        } else {
            ViewHolderForGroup viewHolder = (ViewHolderForGroup) holder;
            viewHolder.group_title.setText(category.getName());
        }


        return convertView;
    }

    static class ViewHolder {
        TextView category_title;
        TextView category_num;

        @Override
        public String toString() {
            return "view";
        }
    }

    static class ViewHolderForGroup {
        TextView group_title;

        @Override
        public String toString() {
            return "group";
        }
    }
}

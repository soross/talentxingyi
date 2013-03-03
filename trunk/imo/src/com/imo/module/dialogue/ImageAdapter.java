package com.imo.module.dialogue;

import com.imo.R;
import com.imo.global.IMOApp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private int[] mThumbIds;

	public ImageAdapter(Context c, int[] mThumbIds) {
		mContext = c;
		this.mThumbIds = mThumbIds;
	}

	@Override
	public int getCount() {
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageview;
		if (convertView == null) {
			imageview = new ImageView(mContext);
			imageview.setLayoutParams(new GridView.LayoutParams(
					(int) (64 * IMOApp.getApp().mScale), (int) (64 * IMOApp
							.getApp().mScale)));
			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageview.setPadding((int) (15 * IMOApp.getApp().mScale),
					(int) (15 * IMOApp.getApp().mScale),
					(int) (15 * IMOApp.getApp().mScale),
					(int) (15 * IMOApp.getApp().mScale));
		} else {
			imageview = (ImageView) convertView;
		}
		imageview.setImageResource(mThumbIds[position]);
		imageview.setBackgroundResource(R.color.chat_self_item_bg_solid);
		return imageview;
	}

}

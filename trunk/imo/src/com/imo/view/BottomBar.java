package com.imo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.module.dialogue.recent.RecentContactActivity;
import com.imo.util.LogFactory;

/**
 * 底部工具栏
 */
public class BottomBar extends LinearLayout {

	private int[] imgResId2NewPressed = {
			R.drawable.menu_dialogue_selected,// R.drawable.menu_dialogue_selected_new_msg,
			R.drawable.menu_organize_selected, R.drawable.menu_contact_selected, R.drawable.menu_group_selected
	};

	private int[] imgResId2New = {
			R.drawable.menu_dialogue_new_msg, R.drawable.menu_organize, R.drawable.menu_contact, R.drawable.menu_group
	};

	private GridView bottombar;

	private BottombarAdapter adapter;

	private int curPos = 0;

	public BottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initBottomBar();
	}

	public BottomBar(Context context) {
		super(context);
		initBottomBar();
	}

	public BottombarAdapter getAdapter() {
		return adapter;
	}

	/**
	 * 更新当前的选中项
	 * 
	 * @param curPos
	 */
	public void setCurPos(int curPos) {

		this.curPos = curPos;

		if (RecentContactActivity.getActivity() != null)
			hasNewMSG = (RecentContactActivity.getActivity().mHasReceivedNew && hasNewMSG); // ///复合条件的整合

		adapter.notifyDataSetInvalidated();
	}

	// //当前是否存在新的消息
	private boolean hasNewMSG;

	/**
	 * 根据是否存在NewMSG 更新BottomBar
	 * 
	 * @param hasNewMSG
	 */
	public void setHasNewMSG(boolean hasNewMSG) {
		this.hasNewMSG = hasNewMSG;
		hasNewMSG = (RecentContactActivity.getActivity().mHasReceivedNew && hasNewMSG); // ///符合条件的整合

		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 初始化底部菜单
	 */
	private void initBottomBar() {

		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.bottombar, this);

		bottombar = (GridView) view.findViewById(R.id.gridView);
		bottombar.setPadding(0, 0, 0, 0);
		bottombar.setDrawSelectorOnTop(false);
		bottombar.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		bottombar.setFocusable(true);
	}

	public void setBottomBar(String[] bottomBarName, int[] imgResIds, int[] imgResIdPress, final int[] clickable) {
		bottombar.setNumColumns(bottomBarName.length);
		adapter = getMenuAdapter(bottomBarName, imgResIds, imgResIdPress, clickable);
		bottombar.setAdapter(adapter);
	}

	public void setBottomBar(int[] imgResIds, int[] imgResIdPress, final int[] clickable) {
		bottombar.setNumColumns(imgResIds.length);
		adapter = getMenuAdapter(new String[imgResIds.length], imgResIds, imgResIdPress, clickable);
		bottombar.setAdapter(adapter);
	}

	public void setBottomBarListener(final OnItemClickListener listener) {

		bottombar.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (adapter.clickable[position] == 0) {
					listener.onItemClick(parent, view, position, id);
				}
			}
		});
	}

	private BottombarAdapter getMenuAdapter(String[] menuNameArray, int[] imgResIds, int[] imgResIdPress, int[] clickable) {
		return new BottombarAdapter(menuNameArray, imgResIds, imgResIdPress, clickable);
	}

	private class BottombarAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;

		public final String[] buttomBarNameArray;

		public final int[] imgResIdArray;
		public final int[] imgResIdPressArray;

		// 0为可点击，-1为不可点击
		public final int[] clickable;

		public BottombarAdapter(String[] menuNameArray, int[] imgResIds, int[] imgResIdsPress, int[] clickable) {
			mInflater = LayoutInflater.from(BottomBar.this.getContext());
			this.buttomBarNameArray = menuNameArray;
			this.imgResIdArray = imgResIds;
			this.clickable = clickable;
			this.imgResIdPressArray = imgResIdsPress;
		}

		@Override
		public int getCount() {
			return buttomBarNameArray.length;
		}

		@Override
		public Object getItem(int position) {
			return buttomBarNameArray[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.bottombar_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			int resId = 0;

			if (curPos != position) {
				if (hasNewMSG) {
					resId = imgResId2New[position];
				} else {
					resId = imgResIdArray[position];
				}

			} else {// / pressed
				if (hasNewMSG) {
					resId = imgResId2NewPressed[position];
				} else {
					resId = imgResIdPressArray[position];
				}
			}

			viewHolder.imageView.setImageResource(resId);

			if (buttomBarNameArray == null || buttomBarNameArray[position] == null) {
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				// params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				// params.addRule(RelativeLayout.CENTER_IN_PARENT);
				viewHolder.imageView.setLayoutParams(params);

			} else {}

			// int height = (int) (76 * IMOApp.getApp().mScale);
			// ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)
			// convertView.getLayoutParams();
			// params.height = height;
			// convertView.setLayoutParams(params);

			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageView;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (IMOApp.getApp().mScale != 1) {
			LogFactory.view("Bottombar", "Bottombar ------->onDraw" + " mScale = " + IMOApp.getApp().mScale);

			int height = (int) (76 * IMOApp.getApp().mScale);
			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();

			params.height = height;
			setLayoutParams(params);
		}
	}

}

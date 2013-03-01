package com.imo.module.dialogue;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.imo.R;
import com.imo.util.Functions;
import com.imo.util.ImageUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DialogueListAdapter extends SimpleAdapter {
	private List<Map<String, Object>> mData;

	private LayoutInflater mInflater;

	private Context context;

	private static final long space_time = 60 * 1000;

	public DialogueListAdapter(Context context, List<Map<String, Object>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	private static final int FROM_ITEM_VIEW_TYPE = 0;
	private static final int TO_ITEM_VIEW_TYPE = 1;

	private static final int COUNT_ITEM_VIEW_TYPE = FROM_ITEM_VIEW_TYPE
			+ TO_ITEM_VIEW_TYPE + 1;

	@Override
	public int getItemViewType(int position) {
		Map<String, Object> map = (Map<String, Object>) getItem(position);
		if (map == null) {
			return IGNORE_ITEM_VIEW_TYPE;
		}

		if ("from".equals(map.get("who"))) {
			return FROM_ITEM_VIEW_TYPE;
		} else {
			return TO_ITEM_VIEW_TYPE;
		}
	}

	@Override
	public int getViewTypeCount() {
		return COUNT_ITEM_VIEW_TYPE;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String, Object> map = (Map<String, Object>) getItem(position);
		ViewHolder holder = null;
		int viewType = getItemViewType(position);

		if (convertView == null) {
			if (viewType == FROM_ITEM_VIEW_TYPE) {
				convertView = mInflater.inflate(R.layout.dialogue_list_iteml,
						null);
			} else {
				convertView = mInflater.inflate(R.layout.dialogue_list_itemr,
						null);
			}

			holder = new ViewHolder();
			holder.time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.head = (ImageView) convertView.findViewById(R.id.iv_head);
			holder.msg = (TextView) convertView.findViewById(R.id.tv_msg);
			holder.fail = (TextView) convertView.findViewById(R.id.tv_failure);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String time = (String) map.get("time");

		// 控制是否显示时间
		boolean showTime = false;
		if (position % 500 == 0) {
			showTime = true;
		} else {
			Map<String, Object> mapTmp = (Map<String, Object>) getItem(position - 1);// 当前消息的前一条消息
			try {
				long currentTime = Functions.strToTime((String) map.get

				("date") + " " + (String) map.get("time"));
				long backTime = Functions.strToTime((String) mapTmp.get

				("date") + " " + (String) mapTmp.get("time"));
				if (currentTime - backTime > space_time)
					showTime = true;
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		if (showTime) {
			holder.time.setText(time);
		} else {
			holder.time.setVisibility(View.GONE);
		}
		if (viewType == FROM_ITEM_VIEW_TYPE) {
			// holder.head.setBackgroundDrawable((Drawable) map.get("head"));
			// holder.head.setImageBitmap((Bitmap) map.get("head"));
			if (context instanceof DialogueActivity) {
				if (DialogueActivity.instance.isOnline) {
					holder.head.setImageBitmap((Bitmap) map.get

					("head"));
				} else {
					ImageUtil.setFaceImg(holder.head, (Bitmap)

					map.get("head"), 0, 0xffCACACA);
				}
			} else {
				holder.head.setImageBitmap((Bitmap) map.get("head"));
			}

		} else {
			holder.head.setVisibility(View.GONE);
		}

		holder.msg.setText((CharSequence) map.get("msg"));

		if (((CharSequence) map.get("msg")).length() < 5)
			holder.msg.setGravity(Gravity.CENTER_HORIZONTAL);

		if (map.get("fail") == null)
			holder.fail.setVisibility(View.GONE);
		return convertView;
	}

	class ViewHolder {
		public TextView time;
		public ImageView head;
		public TextView msg;
		public TextView fail;
	}

}

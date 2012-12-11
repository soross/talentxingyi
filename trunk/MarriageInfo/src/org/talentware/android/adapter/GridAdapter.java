package org.talentware.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.talentware.android.R;

/**
 * Created with IntelliJ IDEA. User: Arron Date: 12-12-7 Time: 下午2:15 To change
 * this template use File | Settings | File Templates.
 */
public class GridAdapter extends BaseAdapter {

	/**
	 * 9宫格文字
	 */
	private static final String[] mGridTitles = new String[] { "婚前心里", "婚前准备",
			"婚礼用品", "婚纱礼服", "婚庆公司", "婚宴场地", "婚礼费用", "婚礼策划", "蜜月旅行" };

	/**
	 * 9宫格图片
	 */
	private static final int[] mGridResources = new int[] { R.drawable.ic_1,
			R.drawable.ic_2, R.drawable.ic_3, R.drawable.ic_4, R.drawable.ic_5,
			R.drawable.ic_6, R.drawable.ic_7, R.drawable.ic_8, R.drawable.ic_9 };

	private Context mContext;

	public GridAdapter(Context iContext) {
		this.mContext = iContext;
	}

	@Override
	public int getCount() {
		return mGridTitles == null ? 0 : mGridTitles.length;
	}

	@Override
	public Object getItem(int position) {
		return mGridTitles == null ? null : mGridTitles[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = (LinearLayout) LayoutInflater.from(mContext).inflate(
					R.layout.item_home_grid, null);
		}
		ImageView ivGrid = (ImageView) convertView
				.findViewById(R.id.imageViewGrid);
		TextView tvGrid = (TextView) convertView
				.findViewById(R.id.textViewGrid);
		ivGrid.setImageResource(mGridResources[position]);
		tvGrid.setText(mGridTitles[position]);
		return convertView;
	}

}

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
    private static final String[] mGridTitles = new String[]{"婚前心里", "婚前准备",
            "婚礼用品", "婚纱礼服", "婚庆公司", "婚宴场地", "婚礼费用", "婚礼策划", "蜜月旅行", "婚戒挑选", "婚车选择", "更多"};

    /**
     * 9宫格图片
     */
    private static final int[] mGridResources = new int[]{R.drawable.icon1,
            R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5,
            R.drawable.icon6, R.drawable.icon7, R.drawable.icon8, R.drawable.icon9, R.drawable.icon10, R.drawable.icon11, R.drawable.icon12};

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

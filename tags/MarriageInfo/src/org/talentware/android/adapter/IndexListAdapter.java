package org.talentware.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.talentware.android.R;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-10
 * Time: 下午3:25
 * To change this template use File | Settings | File Templates.
 */
public class IndexListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private String[] mContents;

    public IndexListAdapter(Context iContext, final String[] iContents) {
        mInflater = LayoutInflater.from(iContext);
        mContents = iContents;
    }

    @Override
    public int getCount() {
        return mContents == null ? 0 : mContents.length;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getItem(int i) {
        return mContents == null ? null : mContents[i];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getItemId(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getView(int iPosition, View iContertView, ViewGroup viewGroup) {
        if (iContertView == null) {
            iContertView = mInflater.inflate(R.layout.item_index_list, viewGroup, false);
        }

        TextView textView = (TextView) iContertView.findViewById(R.id.tv_indexlist_hint);
        textView.setText((String) this.getItem(iPosition));

        return iContertView;
    }
}

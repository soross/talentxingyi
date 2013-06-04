package org.talentware.android.view.searchtitle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import org.talentware.android.lbs.R;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-6-3
 * Time: 下午8:02
 * To change this template use File | Settings | File Templates.
 */
public class SearchTitle extends LinearLayout {
    public SearchTitle(Context context, AttributeSet attrs) {
        super(context, attrs);

        initViews(context);
    }

    private void initViews(Context aContext) {
        LayoutInflater aInflater = (LayoutInflater) aContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        aInflater.inflate(R.layout.view_searchtitle, this);

        EditText search = (EditText) findViewById(R.id.et_search);
        search.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {

                } else {

                }
            }
        });
    }
}

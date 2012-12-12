package org.talentware.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.talentware.android.R;


public class TitleBar extends LinearLayout {
    private TextView mRightButton;

    private TextView mTitleName;

//    private TextView mBackButton;

//    protected ImageView logo;

    private TextView mSecondToRightButton;

    public TitleBar(Context context) {
        super(context);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Resources resource = getResources();
        this.setMeasuredDimension(RelativeLayout.LayoutParams.FILL_PARENT,
                (int) resource.getDimension(R.dimen.titlebar_height));
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setBackgroundResource(R.drawable.titlebg);
        this.setWeightSum(1);

        // Inflate the view from the layout resource.
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) context
                .getSystemService(infService);
        li.inflate(R.layout.view_titlebar, this);

        InitViews();
    }

    private void InitViews() {
        // Get references to the child controls.
        mTitleName = (TextView) findViewById(R.id.tv_titlename);
//        mBackButton = (TextView) findViewById(R.id.tv_back);

//        mBackButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //To change body of implemented methods use File | Settings | File Templates.
//                mActivity.finish();
//            }
//        });
    }

    private Activity mActivity;

    public void setActivity(Activity iActivity) {
        mActivity = iActivity;
    }

//    public void hideBackButton() {
//        mBackButton.setVisibility(View.INVISIBLE);
//    }

    public void setTitleName(final String iTitleName) {
        mTitleName.setText(iTitleName);
    }

}


package org.talentware.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import org.talentware.android.DetailTextConst;
import org.talentware.android.R;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-9
 * Time: 下午8:20
 * To change this template use File | Settings | File Templates.
 */
public class DetailActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initialView() {
        setContentView(R.layout.activity_detail);

        mResources = getResources();

        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        mTitleBar.setActivity(this);
        mTitleBar.setTitleName(mTitleName);

        TextView mTV_Detail = (TextView) findViewById(R.id.tv_detail);

        String Content = "";
        switch (mDetailType) {
            case 0:

                Content = DetailTextConst.Content1;
                break;
            case 100:
                Content = DetailTextConst.Content100;
                break;
            case 101:
                Content = DetailTextConst.Content101;
                break;
            case 102:
                Content = DetailTextConst.Content102;
                break;
            case 103:
                Content = DetailTextConst.Content103;
                break;
            case 104:
                Content = DetailTextConst.Content104;
                break;
            case 105:
                Content = DetailTextConst.Content105;
                break;
            case 106:
                Content = DetailTextConst.Content106;
                break;
            case 107:
                Content = DetailTextConst.Content107;
                break;
        }

        mTV_Detail.setTextSize(mResources.getDimension(R.dimen.content_text_size));
        mTV_Detail.setTextColor(0xffffffff);
        mTV_Detail.setText(Content);
    }

    private int mDetailType;

    private String mTitleName;

    @Override
    protected void getIntentData() {
        //To change body of implemented methods use File | Settings | File Templates.
        mDetailType = getIntent().getExtras().getInt("DetailType", 0);
        mTitleName = getIntent().getExtras().getString("TitleName");
    }
}
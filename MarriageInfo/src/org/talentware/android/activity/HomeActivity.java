package org.talentware.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import org.talentware.android.MarriageApp;
import org.talentware.android.R;
import org.talentware.android.adapter.GridAdapter;

public class HomeActivity extends BaseActivity {

    private static final int COLUMN_NUM = 3;

    private GridView mGridView;

    //**********************************类型*************************************//
    // *****************ActivityType列表界面的************************************//
    // *****************DetailType正文详情的**************************************//
    //**********************************类型*************************************//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MobclickAgent.onError(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        MarriageApp.mScreenHeight = dm.heightPixels;
        MarriageApp.mScreenWidth = dm.widthPixels;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void initialView() {
        setContentView(R.layout.activity_home);

        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        mTitleBar.setTitleName("我们结婚啦");

        mGridView = (GridView) findViewById(R.id.gv_home);
        mGridView.setNumColumns(COLUMN_NUM);
//        setPaddingAndSpacing();
//        this.initMyAPPScreenSize();
        GridAdapter homeAdapter = new GridAdapter(this);

        mGridView.setAdapter(homeAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent mIntent = new Intent();
                Bundle bundle = new Bundle();



                switch (position) {
                    case 0:
                        bundle.putString("TitleName", "婚前心里");
                        bundle.putInt("DetailType", position);
                        mIntent.setClass(HomeActivity.this, DetailActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);

                        break;
                    case 1:
                        bundle.putString("TitleName", "婚前准备");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 2:
                        bundle.putString("TitleName", "婚礼用品");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 3:
                        bundle.putString("TitleName", "婚纱礼服");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 4:
                        bundle.putString("TitleName", "婚庆公司");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 5:
                        bundle.putString("TitleName", "婚宴场地");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 6:
                        bundle.putString("TitleName", "婚礼费用");
                        bundle.putInt("DetailType", position);
                        mIntent.setClass(HomeActivity.this, DetailActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 7:
                        bundle.putString("TitleName", "婚礼策划");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 8:
                        bundle.putString("TitleName", "蜜月旅行");
                        bundle.putInt("DetailType", position);
                        mIntent.setClass(HomeActivity.this, DetailActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 9:
                        bundle.putString("TitleName", "婚戒挑选");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 10:
                        bundle.putString("TitleName", "婚车选择");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        break;
                    case 11:
                        bundle.putString("TitleName", "注意事项");
                        bundle.putInt("ActivityType", position);
                        mIntent.setClass(HomeActivity.this, IndexListActivity.class);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);

                        break;
                }
            }
        });
    }

    @Override
    protected void getIntentData() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

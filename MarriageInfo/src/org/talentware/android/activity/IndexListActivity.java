package org.talentware.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.talentware.android.R;
import org.talentware.android.adapter.IndexListAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-10
 * Time: 下午3:04
 * To change this template use File | Settings | File Templates.
 */
public class IndexListActivity extends BaseActivity {
    private ListView mList;

    private IndexListAdapter mIndexLsitAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initialView() {
        //To change body of implemented methods use File | Settings | File Templates.
        setContentView(R.layout.activity_indexlist);

        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        mTitleBar.setActivity(this);
        mTitleBar.setTitleName(mTitleName);

        mIndexLsitAdapter = new IndexListAdapter(this, mListContents);
        mList = (ListView) findViewById(R.id.lv_index);
        mList.setAdapter(mIndexLsitAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //To change body of implemented methods use File | Settings | File Templates.
                JumpToActivity(mActivityType, i);
            }
        });
    }

    private int mActivityType;

    private String mTitleName;

    private String[] mListContents;

    @Override
    protected void getIntentData() {
        //To change body of implemented methods use File | Settings | File Templates.
        mActivityType = getIntent().getExtras().getInt("ActivityType", (byte) 0);
        Log.e("mActivityType", "" + mActivityType);
        mTitleName = getIntent().getExtras().getString("TitleName");
        switch (mActivityType) {
            case 1:
                mListContents = new String[]{"六个月", "三个月", "两个月", "一个月", "两个星期", "一个星期", "一天", "当天"};
                break;
            case 2:
                mListContents = new String[]{"婚礼筹备期间", "婚礼当天"};
                break;
            case 20:
                mListContents = new String[]{"新房布置", "服侍部分", "首饰部分", "小物及现场装饰部分", "食品部分", "其他"};
                break;
            case 21:
                mListContents = new String[]{"新娘和伴娘必备物品", "新狼和伴郎必备物品", "宴会厅", "酒店新房", "蜜月必备物品"};
                break;
        }
    }

    private void JumpToActivity(int iActivityType, int iPos) {
        String NextTitleName = "";
        int DetailType = 0;
        Class<?> JumpClass = null;

        switch (iActivityType) {
            case 1: {// 婚前准备
                JumpClass = DetailActivity.class;
                DetailType = 100 + iPos;
                switch (iPos) {
                    case 0:
                        NextTitleName = mTitleName + "-六个月";
                        break;
                    case 1:
                        NextTitleName = mTitleName + "-三个月";
                        break;
                    case 2:
                        NextTitleName = mTitleName + "-两个月";
                        break;
                    case 3:
                        NextTitleName = mTitleName + "-一个月";
                        break;
                    case 4:
                        NextTitleName = mTitleName + "-两个星期";
                        break;
                    case 5:
                        NextTitleName = mTitleName + "-一个星期";
                        break;
                    case 6:
                        NextTitleName = mTitleName + "-一天";
                        break;
                    case 7:
                        NextTitleName = mTitleName + "-当天";
                        break;
                }
                break;
            }
            case 2: {// 婚礼用品
                DetailType = 20 + iPos;
                JumpClass = IndexListActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "婚礼筹备期间";
                        break;
                    case 1:
                        NextTitleName = "婚礼当天";
                        break;
                }
                break;
            }
            case 20:
                break;
            case 21:
                break;
        }

        Intent mIntent = new Intent();
        Bundle mBundle = new Bundle();
        mIntent.setClass(IndexListActivity.this, JumpClass);
        mBundle.putString("TitleName", NextTitleName);
        mBundle.putInt("ActivityType", DetailType);
        mBundle.putInt("DetailType", DetailType);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }
}

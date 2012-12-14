package org.talentware.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.talentware.android.DetailTextConst;
import org.talentware.android.MarriageApp;
import org.talentware.android.R;
import org.talentware.android.adapter.IndexListAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-9
 * Time: 下午8:20
 * To change this template use File | Settings | File Templates.
 */
public class DetailActivity extends BaseActivity {

    private DragableSpace mDragableSpace;

    private View mMenuView;

    private View mDetailView;

    private View mTestView;

    private ListView mListView;

    private IndexListAdapter mIndexLsitAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_detail);

        mResources = getResources();

        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        mTitleBar.setActivity(this);
        mTitleBar.setTitleName(mTitleName);

        mDragableSpace = (DragableSpace) findViewById(R.id.view_dragspace);
        mMenuView = LayoutInflater.from(this).inflate(R.layout.view_menu, null);
        mDetailView = LayoutInflater.from(this).inflate(R.layout.view_detail, null);
        mTestView = LayoutInflater.from(this).inflate(R.layout.view_test, null);
        mDragableSpace.addView(mMenuView);
        mDragableSpace.addView(mDetailView);
//        mDragableSpace.addView(mTestView);

//        TextView mTV_Detail = (TextView) mDetailView.findViewById(R.id.tv_content);
//        mListView = (ListView) mMenuView.findViewById(R.id.lv_detail_indexlist);
//       String[] mListContents = new String[]{"六个月", "三个月", "两个月", "一个月", "两个星期", "一个星期", "一天", "当天"};
//        mIndexLsitAdapter = new IndexListAdapter(this, mListContents);
//        mListView = (ListView) findViewById(R.id.lv_index);
//        mListView.setAdapter(mIndexLsitAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //To change body of implemented methods use File | Settings | File Templates.
//                JumpToActivity(mActivityType, i);
//            }
//        });

//        String Content = "";
//        switch (mDetailType) {
//            case 0:
//                Content = DetailTextConst.Content1;
//                break;
//            case 100:
//                Content = DetailTextConst.Content100;
//                break;
//            case 101:
//                Content = DetailTextConst.Content101;
//                break;
//            case 102:
//                Content = DetailTextConst.Content102;
//                break;
//            case 103:
//                Content = DetailTextConst.Content103;
//                break;
//            case 104:
//                Content = DetailTextConst.Content104;
//                break;
//            case 105:
//                Content = DetailTextConst.Content105;
//                break;
//            case 106:
//                Content = DetailTextConst.Content106;
//                break;
//            case 107:
//                Content = DetailTextConst.Content107;
//                break;
//            case 200:
//                Content = DetailTextConst.Content200;
//                break;
//            case 201:
//                Content = DetailTextConst.Content201;
//                break;
//            case 202:
//                Content = DetailTextConst.Content202;
//                break;
//            case 203:
//                Content = DetailTextConst.Content203;
//                break;
//            case 204:
//                Content = DetailTextConst.Content204;
//                break;
//            case 205:
//                Content = DetailTextConst.Content205;
//                break;
//            case 210:
//                Content = DetailTextConst.Content210;
//                break;
//            case 211:
//                Content = DetailTextConst.Content211;
//                break;
//            case 212:
//                Content = DetailTextConst.Content212;
//                break;
//            case 213:
//                Content = DetailTextConst.Content213;
//                break;
//            case 214:
//                Content = DetailTextConst.Content214;
//                break;
//            case 300:
//                Content = DetailTextConst.Content300;
//                break;
//            case 301:
//                Content = DetailTextConst.Content301;
//                break;
//            case 302:
//                Content = DetailTextConst.Content302;
//                break;
//            case 303:
//                Content = DetailTextConst.Content303;
//                break;
//            case 304:
//                Content = DetailTextConst.Content304;
//                break;
//            case 4:
//                Content = DetailTextConst.Content4;
//                break;
//            case 500:
//                Content = DetailTextConst.Content500;
//                break;
//            case 501:
//                Content = DetailTextConst.Content501;
//                break;
//            case 502:
//                Content = DetailTextConst.Content502;
//                break;
//            case 503:
//                Content = DetailTextConst.Content503;
//                break;
//            case 504:
//                Content = DetailTextConst.Content504;
//                break;
//            case 505:
//                Content = DetailTextConst.Content505;
//                break;
//            case 506:
//                Content = DetailTextConst.Content506;
//                break;
//            case 6:
//                Content = DetailTextConst.Content6;
//                break;
//            case 700:
//                Content = DetailTextConst.Content700;
//                break;
//            case 701:
//                Content = DetailTextConst.Content701;
//                break;
//            case 702:
//                Content = DetailTextConst.Content702;
//                break;
//            case 703:
//                Content = DetailTextConst.Content703;
//                break;
//            case 704:
//                Content = DetailTextConst.Content704;
//                break;
//            case 8:
//                Content = DetailTextConst.Content8;
//                break;
//        }
//
//        mTV_Detail.setTextSize(mResources.getDimension(R.dimen.content_text_size));
//        mTV_Detail.setTextColor(0xff000000);
//        mTV_Detail.setText(Content);
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
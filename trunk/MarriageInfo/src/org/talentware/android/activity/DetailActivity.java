package org.talentware.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
public class DetailActivity extends BaseActivity{

//    private DragableSpace mDragableSpace;
//
//    private View mMenuView;
//
//    private View mDetailView;
//
//    private View mTestView;
//
//    private ListView mListView;
//
//    private IndexListAdapter mIndexLsitAdapter;

    TextView mTV_Detail;

    String[] mListContents;

    LinearLayout mLinearLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Animation anim = AnimationUtils.loadAnimation(DetailActivity.this, R.anim.push_up_out);
        findViewById(R.id.ll_base).startAnimation(anim);
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

//        mLinearLayout = (LinearLayout) findViewById(R.id.interGELinearLayout);
//        GEInstance geInstance = new GEInstance();
//        geInstance.loadInterAd(5, GEInstance.INTERUP, mLinearLayout);
//        geInstance.setInterAdVisible(View.VISIBLE);//显示

        mTV_Detail = (TextView) findViewById(R.id.tv_content);
//        mListView = (ListView) mMenuView.findViewById(R.id.lv_detail_indexlist);
//        mListContents = getIndexListData();
//        mIndexLsitAdapter = new IndexListAdapter(this, mListContents);
//        mListView.setAdapter(mIndexLsitAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //To change body of implemented methods use File | Settings | File Templates.
//                switch (mDetailType) {
//                    case 0:
//                        mDetailType = 0;
//                        break;
//                    case 100:
//                    case 101:
//                    case 102:
//                    case 103:
//                    case 104:
//                    case 105:
//                    case 106:
//                    case 107:
//                        mDetailType = 100 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 200:
//                    case 201:
//                    case 202:
//                    case 203:
//                    case 204:
//                    case 205:
//                        mDetailType = 200 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 210:
//                    case 211:
//                    case 212:
//                    case 213:
//                    case 214:
//                        mDetailType = 210 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 300:
//                    case 301:
//                    case 302:
//                    case 303:
//                    case 304:
//                        mDetailType = 300 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 400:
//                    case 401:
//                    case 402:
//                    case 403:
//                    case 404:
//                    case 405:
//                    case 406:
//                        mDetailType = 400 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 500:
//                    case 501:
//                    case 502:
//                    case 503:
//                    case 504:
//                    case 505:
//                    case 506:
//                        mDetailType = 500 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 700:
//                    case 701:
//                    case 702:
//                    case 703:
//                    case 704:
//                        mDetailType = 700 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 900:
//                    case 901:
//                    case 902:
//                        mDetailType = 900 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                    case 1000:
//                    case 1001:
//                    case 1002:
//                    case 1003:
//                    case 1004:
//                    case 1005:
//                        mDetailType = 1000 + i;
//                        mTitleBar.setTitleName(mListContents[i]);
//                        break;
//                }
//
//                setContent();
//                mDragableSpace.resetToContent();
//            }
//        });

        setContent();
    }

    private String[] getIndexListData() {
        String[] Data = null;
        switch (mDetailType) {
            case 0:
                break;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
                Data = new String[]{"六个月", "三个月", "两个月", "一个月", "两个星期", "一个星期", "一天", "当天"};
                break;
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
                Data = new String[]{"新房布置", "服侍部分", "首饰部分", "小物及现场装饰部分", "食品部分", "其他"};
                break;
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
                Data = new String[]{"新娘和伴娘必备物品", "新狼和伴郎必备物品", "宴会厅", "酒店新房", "蜜月必备物品"};
                break;
            case 300:
            case 301:
            case 302:
            case 303:
            case 304:
                Data = new String[]{"根据婚纱的面料", "根据婚纱的颜色", "根据婚纱配件——头纱", "根据婚纱的款式", "根据婚庆的氛围"};
                break;
            case 400:
            case 401:
            case 402:
            case 403:
            case 404:
            case 405:
            case 406:
                Data = new String[]{"公司成立时间", "公司有无自己的工程部", "公司的专业人员", "公司婚礼策划师的资质", "公司的婚礼设备", "公司的部门设置", "公司的资质"};
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 505:
            case 506:
                Data = new String[]{"从预算入手", "从日期筛选", "从风格寻找", "从宾客计算", "从流程规划", "从菜肴判断", "从设施比较"};
                break;
            case 700:
            case 701:
            case 702:
            case 703:
            case 704:
                Data = new String[]{"五星级婚宴", "舞会式晚宴", "下午茶式午宴", "一般餐厅喜宴", "自办式喜酒"};
                break;
            case 900:
            case 901:
            case 902:
                Data = new String[]{"如何挑选钻石戒指方法", "钻石等级证书", "钻石专家Q&A"};
                break;
            case 1000:
            case 1001:
            case 1002:
            case 1003:
            case 1004:
            case 1005:
                Data = new String[]{"需要事先预订", "首选豪华品牌", "主婚车要气派、亮眼", "婚车也可以DIY", "首选豪华品牌", "要做两手准备"};
                break;
            case 1100:
            case 1101:
            case 1102:
            case 1103:
            case 1104:
            case 1105:
            case 1106:
            case 1107:
            case 1108:
            case 1109:
                Data = new String[]{"结婚请柬的措辞", "新娘美容记要", "“吉日”的确定", "选择伴娘八项注意", "新郎注意事项", "关于宾客名单", "关于交通工具", "关于伴郎的礼物", "关于蜜月", "关于协助新娘"};
                break;
        }

        return Data;
    }

    private void setContent() {
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
            case 200:
                Content = DetailTextConst.Content200;
                break;
            case 201:
                Content = DetailTextConst.Content201;
                break;
            case 202:
                Content = DetailTextConst.Content202;
                break;
            case 203:
                Content = DetailTextConst.Content203;
                break;
            case 204:
                Content = DetailTextConst.Content204;
                break;
            case 205:
                Content = DetailTextConst.Content205;
                break;
            case 210:
                Content = DetailTextConst.Content210;
                break;
            case 211:
                Content = DetailTextConst.Content211;
                break;
            case 212:
                Content = DetailTextConst.Content212;
                break;
            case 213:
                Content = DetailTextConst.Content213;
                break;
            case 214:
                Content = DetailTextConst.Content214;
                break;
            case 300:
                Content = DetailTextConst.Content300;
                break;
            case 301:
                Content = DetailTextConst.Content301;
                break;
            case 302:
                Content = DetailTextConst.Content302;
                break;
            case 303:
                Content = DetailTextConst.Content303;
                break;
            case 304:
                Content = DetailTextConst.Content304;
                break;
//            case 4:
//                Content = DetailTextConst.Content4;
//                break;
            case 400:
                Content = DetailTextConst.Content400;
                break;
            case 401:
                Content = DetailTextConst.Content401;
                break;
            case 402:
                Content = DetailTextConst.Content402;
                break;
            case 403:
                Content = DetailTextConst.Content403;
                break;
            case 404:
                Content = DetailTextConst.Content404;
                break;
            case 405:
                Content = DetailTextConst.Content405;
                break;
            case 406:
                Content = DetailTextConst.Content406;
                break;
            case 500:
                Content = DetailTextConst.Content500;
                break;
            case 501:
                Content = DetailTextConst.Content501;
                break;
            case 502:
                Content = DetailTextConst.Content502;
                break;
            case 503:
                Content = DetailTextConst.Content503;
                break;
            case 504:
                Content = DetailTextConst.Content504;
                break;
            case 505:
                Content = DetailTextConst.Content505;
                break;
            case 506:
                Content = DetailTextConst.Content506;
                break;
            case 6:
                Content = DetailTextConst.Content6;
                break;
            case 700:
                Content = DetailTextConst.Content700;
                break;
            case 701:
                Content = DetailTextConst.Content701;
                break;
            case 702:
                Content = DetailTextConst.Content702;
                break;
            case 703:
                Content = DetailTextConst.Content703;
                break;
            case 704:
                Content = DetailTextConst.Content704;
                break;
            case 8:
                Content = DetailTextConst.Content8;
                break;
            case 900:
                Content = DetailTextConst.Content900;
                break;
            case 901:
                Content = DetailTextConst.Content901;
                break;
            case 902:
                Content = DetailTextConst.Content902;
                break;
            case 1000:
                Content = DetailTextConst.Content1000;
                break;
            case 1001:
                Content = DetailTextConst.Content1001;
                break;
            case 1002:
                Content = DetailTextConst.Content1002;
                break;
            case 1003:
                Content = DetailTextConst.Content1003;
                break;
            case 1004:
                Content = DetailTextConst.Content1004;
                break;
            case 1005:
                Content = DetailTextConst.Content1005;
                break;

            case 1100:
                Content = DetailTextConst.Content1100;
                break;
            case 1101:
                Content = DetailTextConst.Content1101;
                break;
            case 1102:
                Content = DetailTextConst.Content1102;
                break;
            case 1103:
                Content = DetailTextConst.Content1103;
                break;
            case 1104:
                Content = DetailTextConst.Content1104;
                break;
            case 1105:
                Content = DetailTextConst.Content1105;
                break;
            case 1106:
                Content = DetailTextConst.Content1106;
                break;
            case 1107:
                Content = DetailTextConst.Content1107;
                break;
            case 1108:
                Content = DetailTextConst.Content1108;
                break;
            case 1109:
                Content = DetailTextConst.Content1109;
                break;
        }

        mTV_Detail.setTextSize(mResources.getDimension(R.dimen.content_text_size));
        mTV_Detail.setTextColor(0xff000000);
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
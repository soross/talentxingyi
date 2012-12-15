package org.talentware.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
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

    private ScrollView mSV_PreHint;

    private TextView mTV_PreRead;

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
        //To change body of implemented methods use File | Settings | File Templates.
        setContentView(R.layout.activity_indexlist);

        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        mTitleBar.setActivity(this);
        mTitleBar.setTitleName(mTitleName);

        String PreRead = "";
        mTV_PreRead = (TextView) findViewById(R.id.tv_prehint);
        mSV_PreHint = (ScrollView) findViewById(R.id.sv_prehint);
        switch (mActivityType) {
            case 3:
                mSV_PreHint.setVisibility(View.VISIBLE);
                PreRead = "    穿婚纱有别于时装及晚礼服，是新娘子一生中，最重要的礼服，当准新娘面对林林总总的婚礼，不但会眼花缭乱，更加会心猿意马，难\n" +
                        "作理智决定，往往更会有“千挑万挑，挑个烂灯盏”的情况，又或者挑选了一套很漂亮，但不适合自己的婚纱。\n" +
                        "    为避免出现选择错误的情况，准新娘宜事先学习如何选婚纱，即从婚纱的颜色、款式及其他的一些细节方面考虑。从而在成千上万的婚\n" +
                        "纱中，缩小选择范围，直到最终试穿数套便可以从中作出最后决定。\n";
                break;
            case 4:
                mSV_PreHint.setVisibility(View.VISIBLE);
                PreRead = "    选择理想的婚庆公司的七大标准选择理想的婚庆公司的七大标准选择理想的婚庆公司的七大标准选择理想的婚庆公司的七大标准";
                break;
            case 7:
                mSV_PreHint.setVisibility(View.VISIBLE);
                PreRead = "    现代人社会生活比较繁忙，很多新人都选择在酒店举行婚宴，既减轻了负担，又给人以好印象。选择携手步上红地毯与相爱的人共度一生，是人间何等的美事，而一场精致浪漫的婚宴更是多数新人心中的美梦。不过婚宴是两家人共同参与的大事，于是事前的计划和协商是必要的。";
                break;
            case 9:
                mSV_PreHint.setVisibility(View.VISIBLE);
                PreRead = "    一句广告语说的好：“钻石恒久远,一颗永流传”，新人们必备的钻石婚戒是少不了的。钻石的特性很符合婚姻的特性，我们都期待婚姻\n" +
                        "能长久、感情能纯粹、关系最牢固，同时婚姻又很容易受外来伤害，珍惜婚姻，佩戴钻石，恒久远，永流传。一起来看那新人如何挑选钻石\n" +
                        "戒指全攻略，教你速成砖石达人。\n" +
                        "    钻石婚戒，应选择配合手形的钻石形状。一颗钻石的款式取决于切割后所呈的形状，传统上有五种切割形状。圆形、椭圆形、梨形、方\n" +
                        "形、心形，而在切割的同时，也决定了它的镶嵌方式。";
                break;
            case 10:
                mSV_PreHint.setVisibility(View.VISIBLE);
                PreRead = "    结婚，作为人生之中的大事，自然力求每个细节都能做到完美。虽然现在不主张铺张奢侈的婚礼仪式，但婚车队这个环节依然不可忽视。但是，如何选择婚车呢？";
                break;
        }
        mTV_PreRead.setTextColor(0xff000000);
        mTV_PreRead.setText(PreRead);

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
            case 3:
                mListContents = new String[]{"根据婚纱的面料", "根据婚纱的颜色", "根据婚纱配件——头纱", "根据婚纱的款式", "根据婚庆的氛围"};
                break;
            case 4:
                mListContents = new String[]{"公司成立时间", "公司有无自己的工程部", "公司的专业人员", "公司婚礼策划师的资质", "公司的婚礼设备", "公司的部门设置", "公司的资质"};
                break;
            case 5:
                mListContents = new String[]{"从预算入手", "从日期筛选", "从风格寻找", "从宾客计算", "从流程规划", "从菜肴判断", "从设施比较"};
                break;
            case 7:
                mListContents = new String[]{"五星级婚宴", "舞会式晚宴", "下午茶式午宴", "一般餐厅喜宴", "自办式喜酒"};
                break;
            case 9:
                mListContents = new String[]{"如何挑选钻石戒指方法", "钻石等级证书", "钻石专家Q&A"};
                break;
            case 10:
                mListContents = new String[]{"需要事先预订", "首选豪华品牌", "主婚车要气派、亮眼", "婚车也可以DIY", "首选豪华品牌", "要做两手准备"};
                break;
            case 11:
                mListContents = new String[]{"结婚请柬的措辞", "新娘美容记要", "“吉日”的确定", "选择伴娘八项注意", "新郎注意事项", "关于宾客名单", "关于交通工具", "关于伴郎的礼物", "关于蜜月", "关于协助新娘"};
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
            case 20:// 婚礼用品-婚礼筹备期间
                DetailType = 200 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "新房布置";
                        break;
                    case 1:
                        NextTitleName = "服侍部分";
                        break;
                    case 2:
                        NextTitleName = "首饰部分";
                        break;
                    case 3:
                        NextTitleName = "小物及现场装饰部分";
                        break;
                    case 4:
                        NextTitleName = "食品部分";
                        break;
                    case 5:
                        NextTitleName = "其他";
                        break;
                }
                break;
            case 21:
                DetailType = 210 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "新娘和伴娘必备物品";
                        break;
                    case 1:
                        NextTitleName = "新郎和伴郎必备物品";
                        break;
                    case 2:
                        NextTitleName = "宴会厅";
                        break;
                    case 3:
                        NextTitleName = "酒店新房";
                        break;
                    case 4:
                        NextTitleName = "蜜月必备物品";
                        break;
                }
                break;
            case 3:
                //"根据婚纱的面料", "根据婚纱的颜色", "根据婚纱配件——头纱", "根据婚纱的款式", "根据婚庆的氛围"
                DetailType = 300 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "根据婚纱的面料";
                        break;
                    case 1:
                        NextTitleName = "根据婚纱的颜色";
                        break;
                    case 2:
                        NextTitleName = "根据婚纱配件——头纱";
                        break;
                    case 3:
                        NextTitleName = "根据婚纱的款式";
                        break;
                    case 4:
                        NextTitleName = "根据婚庆的氛围";
                        break;
                }
                break;
            case 4:
                DetailType = 400 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "公司成立时间";
                        break;
                    case 1:
                        NextTitleName = "公司有无自己的工程部";
                        break;
                    case 2:
                        NextTitleName = "公司的专业人员";
                        break;
                    case 3:
                        NextTitleName = "公司婚礼策划师的资质";
                        break;
                    case 4:
                        NextTitleName = "公司的婚礼设备";
                        break;
                    case 5:
                        NextTitleName = "公司的部门设置";
                        break;
                    case 6:
                        NextTitleName = "公司的资质";
                        break;
                }
                break;
            case 5:
                //"从预算入手", "从日期筛选", "从风格寻找", "从宾客计算", "从流程规划", "从菜肴判断", "从设施比较"
                DetailType = 500 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "从预算入手";
                        break;
                    case 1:
                        NextTitleName = "从日期筛选";
                        break;
                    case 2:
                        NextTitleName = "从风格寻找";
                        break;
                    case 3:
                        NextTitleName = "从宾客计算";
                        break;
                    case 4:
                        NextTitleName = "从流程规划";
                        break;
                    case 5:
                        NextTitleName = "从菜肴判断";
                        break;
                    case 6:
                        NextTitleName = "从设施比较";
                        break;
                }
                break;
            case 7:
                //"五星级婚宴", "舞会式晚宴", "下午茶式午宴", "一般餐厅喜宴", "自办式喜酒"
                DetailType = 700 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "五星级婚宴";
                        break;
                    case 1:
                        NextTitleName = "舞会式晚宴";
                        break;
                    case 2:
                        NextTitleName = "下午茶式午宴";
                        break;
                    case 3:
                        NextTitleName = "一般餐厅喜宴";
                        break;
                    case 4:
                        NextTitleName = "自办式喜酒";
                        break;
                }
                break;
            case 9:
                //"如何挑选钻石戒指方法", "钻石等级证书", "钻石专家Q&A"
                DetailType = 900 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "如何挑选钻石戒指方法";
                        break;
                    case 1:
                        NextTitleName = "钻石等级证书";
                        break;
                    case 2:
                        NextTitleName = "钻石专家Q&A";
                        break;
                }
                break;
            case 10:
                //"需要事先预订", "首选豪华品牌", "主婚车要气派、亮眼", "婚车也可以DIY", "首选豪华品牌", "要做两手准备"
                DetailType = 1000 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "需要事先预订";
                        break;
                    case 1:
                        NextTitleName = "首选豪华品牌";
                        break;
                    case 2:
                        NextTitleName = "主婚车要气派、亮眼";
                        break;
                    case 3:
                        NextTitleName = "婚车也可以DIY";
                        break;
                    case 4:
                        NextTitleName = "首选豪华品牌";
                        break;
                    case 5:
                        NextTitleName = "要做两手准备";
                        break;
                }
                break;
            case 11:
                //"结婚请柬的措辞", "新娘美容记要", "“吉日”的确定", "选择伴娘八项注意", "新郎注意事项", "关于宾客名单", "关于交通工具", "关于伴郎的礼物", "关于蜜月", "关于协助新娘"
                DetailType = 1100 + iPos;
                JumpClass = DetailActivity.class;
                switch (iPos) {
                    case 0:
                        NextTitleName = "结婚请柬的措辞";
                        break;
                    case 1:
                        NextTitleName = "新娘美容记要";
                        break;
                    case 2:
                        NextTitleName = "“吉日”的确定";
                        break;
                    case 3:
                        NextTitleName = "选择伴娘八项注意";
                        break;
                    case 4:
                        NextTitleName = "新郎注意事项";
                        break;
                    case 5:
                        NextTitleName = "关于宾客名单";
                        break;
                    case 6:
                        NextTitleName = "关于交通工具";
                        break;
                    case 7:
                        NextTitleName = "关于伴郎的礼物";
                        break;
                    case 8:
                        NextTitleName = "关于蜜月";
                        break;
                    case 9:
                        NextTitleName = "关于协助新娘";
                        break;
                }
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

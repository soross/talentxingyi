package org.talentware.android.module;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import com.umeng.analytics.MobclickAgent;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-5-20
 * Time: 下午9:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseActivity extends Activity {

    protected Resources aResources;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    protected void init() {
        aResources = getResources();

        initViews();

        registerEvents();
    }

    /**
     * 初始化控件
     */
    protected abstract void initViews();

    /**
     * 为控件注册事件
     */
    protected void registerEvents() {

    }
}
package org.talentware.android.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import com.umeng.analytics.MobclickAgent;
import org.talentware.android.R;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-10
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseActivity extends Activity {

    protected TitleBar mTitleBar;

    protected Resources mResources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntentData();
        initialView();
    }

    @Override
    public void onResume(){
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected abstract void initialView();

    protected abstract void getIntentData();

}

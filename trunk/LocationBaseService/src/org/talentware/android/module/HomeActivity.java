package org.talentware.android.module;

import android.os.Bundle;
import org.talentware.android.lbs.R;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-6-3
 * Time: 下午7:59
 * To change this template use File | Settings | File Templates.
 */
public class HomeActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_home);
    }
}
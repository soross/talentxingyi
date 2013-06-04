package org.talentware.android.storage;

import android.content.Context;
import android.content.SharedPreferences;
import org.talentware.android.global.LBSApp;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-6-4
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
public class SharedPrefsManager {

    private static SharedPrefsManager mInstance;

    private Context mContext;

    public synchronized static SharedPrefsManager getInstance() {
        if (mInstance == null) {
            mInstance = new SharedPrefsManager(LBSApp.getAppContext());
        }

        return mInstance;
    }

    private SharedPrefsManager(Context aContext) {
        mContext = aContext;
    }

    public String getStringValue(String aFileName, String aKey) {
        SharedPreferences settings = mContext.getSharedPreferences(aFileName, 0);
        return settings.getString(aKey, "");
    }
}

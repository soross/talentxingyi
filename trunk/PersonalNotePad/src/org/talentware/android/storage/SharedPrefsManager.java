package org.talentware.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: Arron
 * Date: 12-12-7
 * Time: 上午11:24
 * To change this template use File | Settings | File Templates.
 */
public class SharedPrefsManager {

    private Context mContext = null;

    private static SharedPrefsManager mInstance = null;

    private static Object mSharedPrefsObj = new Object();

    public static SharedPrefsManager getInstance(Context iContext) {
        synchronized (mSharedPrefsObj) {
            if (mInstance == null) {
                mInstance = new SharedPrefsManager(iContext);
            }
        }

        return mInstance;
    }

    private SharedPrefsManager(Context iContext) {
        this.mContext = iContext;
    }

    public void save(Object key, Object value) {
        if (key instanceof Integer) {
            SharedPreferences settings = mContext.getSharedPreferences("eastmoney", 0);
            settings.edit()
                    .putString(
                            "PassUsrName",
                            new BlowfishUtil(
                                    GameConst.SYNC_LOGIN_KEY)
                                    .encryptString(accountInput
                                            .getText().toString()
                                            .trim().toLowerCase()))
                    .putString(
                            "PassUsrPswd",
                            new BlowfishUtil(
                                    GameConst.SYNC_LOGIN_KEY)
                                    .encryptString(passwordInput
                                            .getText().toString()
                                            .trim()))
                    .putBoolean("PassAuLoginFlag", true)
                    .putBoolean("PassFlag", true)
                    .putBoolean("SyncFlagDismiss0", true)
                    .putBoolean("SyncFlagDismiss1", true)
                    .putBoolean("SyncFlagDismiss2", true).commit();

        }
    }

}

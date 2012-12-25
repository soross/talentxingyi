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

    private PrefsMode mPrefsMode = PrefsMode.PRIVATE;

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

    public synchronized SharedPrefsManager save(final String mPrefsName, final Object key, final Object value) {
        SharedPreferences settings = mContext.getSharedPreferences(mPrefsName, mPrefsMode.mValue);
        if (key instanceof Integer) {
            settings.edit().putInt(key.toString(), Integer.parseInt(value.toString())).commit();
        } else if (key instanceof Long) {
            settings.edit().putLong(key.toString(), Long.parseLong(value.toString())).commit();
        } else if (key instanceof Boolean) {
            settings.edit().putBoolean(key.toString(), Boolean.parseBoolean(value.toString())).commit();
        } else if (key instanceof String) {
            settings.edit().putString(key.toString(), value.toString()).commit();
        }

        return mInstance;
    }

    public synchronized SharedPrefsManager save(final String mPrefsName, final Object key, final Object value, PrefsMode iPrefsMode) {
        this.mPrefsMode = iPrefsMode;
        return save(mPrefsName, key, value);
    }

    public synchronized Object get(final String mPrefsName, final Object key, final Object defaultvalue) {
        Object RetValue = null;
        SharedPreferences settings = mContext.getSharedPreferences(mPrefsName, mPrefsMode.mValue);
        if (key instanceof Integer) {
            settings.getInt(key.toString(), Integer.parseInt(defaultvalue.toString()));
        } else if (key instanceof Long) {
            settings.getLong(key.toString(), Long.parseLong(defaultvalue.toString()));
        } else if (key instanceof Boolean) {
            settings.getBoolean(key.toString(), Boolean.parseBoolean(defaultvalue.toString()));
        } else if (key instanceof String) {
            settings.getString(key.toString(), defaultvalue.toString());
        }

        return RetValue;
    }

    enum PrefsMode {
        PRIVATE(Context.MODE_PRIVATE), READABLE(Context.MODE_WORLD_READABLE), WRITTABLE(Context.MODE_WORLD_WRITEABLE), APPEND(Context.MODE_APPEND);

        int mValue;

        PrefsMode(int iValue) {
            this.mValue = iValue;
        }
    }
}

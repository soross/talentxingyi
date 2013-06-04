package org.talentware.android.global;

import android.app.Application;
import org.talentware.android.bean.User;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-5-20
 * Time: 下午9:43
 * To change this template use File | Settings | File Templates.
 */
public class LBSApp extends Application {

    public static User mUser = new User();

    private static LBSApp aInstance = new LBSApp();

    public void onCreate() {

    }

    public void onTerminate() {

    }

    public static Application getAppContext() {
        return aInstance;
    }

}

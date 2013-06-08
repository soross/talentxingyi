package com.snda.inote.io;

import android.content.Context;
import android.content.SharedPreferences;
import com.snda.inote.Consts;
import com.snda.inote.Inote;
import com.snda.inote.model.User;

public class Setting {
    public static void delUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Consts.FILE_SETTING, 0);
        sp.edit().remove(Consts.USER_TOKEN).remove(Consts.USER_SNDAID).commit();
    }

    public static void setUser(Context context, User user) {
        SharedPreferences sp = context.getSharedPreferences(Consts.FILE_SETTING, 0);
        sp.edit().putString(Consts.USER_TOKEN, user.getToken()).putString(Consts.USER_SNDAID, user.getSndaID()).commit();
    }

    public static User getUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Consts.FILE_SETTING, 0);
        String token = sp.getString(Consts.USER_TOKEN, null);
        String sndaid = sp.getString(Consts.USER_SNDAID, null);
        User user = new User();
        if (token != null && sndaid != null) {
            user.setSndaID(sndaid);
            user.setToken(token);
        } else {
            user.setSndaID("-1");  //offline mode  if user not login
            user.setToken("-1");
        }
        return user;
    }

    public static boolean getFullScreenTip(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Consts.FILE_SETTING, 0);
        return sp.getBoolean("hasFullScreenTip", false);
    }

    public static void setFullScreenTip(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Consts.FILE_SETTING, 0);
        sp.edit().putBoolean("hasFullScreenTip", true).commit();
    }

    public static String getSyncVersion() {
        SharedPreferences sp = Inote.instance.getSharedPreferences(Consts.FILE_SETTING, 0);
        //        long vs = sp.getLong("SVS" + Inote.getUser().getSndaID(), 0);
        return sp.getString("SV" + Inote.getUser().getSndaID(), null);

//		Log.v("Kevin", String.valueOf(System.currentTimeMillis()));
//		Log.v("Kevin", String.valueOf(vs));
//		if((System.currentTimeMillis() - vs) > (29 * 24 * 60 * 60 * 1000)){
//			return null;
//		} else{
//			return v;
//		}
    }

    public static void setSyncVersion() {
        SharedPreferences sp = Inote.instance.getSharedPreferences(Consts.FILE_SETTING, 0);
        sp.edit().putString("SV" + Inote.getUser().getSndaID(), Inote.SyncVersion).putLong("SVS" + Inote.getUser().getSndaID(), System.currentTimeMillis()).commit();
    }
}

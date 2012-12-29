package util;

import android.util.Log;

public class Logger {

	private static final String TAG = "TalentWare";

	private static final boolean DEBUG = true;

	public static void v(final String TAG, final String Content) {
		if (DEBUG) {
			Log.v(TAG, Content);
		}
	}

	public static void i(final String TAG, final String Content) {
		if (DEBUG) {
			Log.i(TAG, Content);
		}
	}

	public static void d(final String TAG, final String Content) {
		if (DEBUG) {
			Log.d(TAG, Content);
		}
	}

	public static void e(final String TAG, final String Content) {
		if (DEBUG) {
			Log.e(TAG, Content);
		}
	}

}

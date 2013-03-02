package com.imo.activity;

import java.util.Iterator;
import java.util.Set;

import android.os.Bundle;

/**
 * ActivityIdUtil÷˙ ÷¿‡
 * 
 * @author CaixiaoLong
 * 
 */
public class ActivityIdUtil {

	public static String computeId(final Class<?> activityCls, final Bundle bundle) {

		return activityCls.getName() + bundleToString(bundle);

	}

	private static String bundleToString(final Bundle bundle) {

		final StringBuilder builder = new StringBuilder();

		if (bundle != null) {
			final Set<String> keys = bundle.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				builder.append(key);
				builder.append(bundle.get(key));
			}
		}

		return builder.toString();
	}
}

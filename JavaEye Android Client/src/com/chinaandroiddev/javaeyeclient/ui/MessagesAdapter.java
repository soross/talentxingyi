package com.chinaandroiddev.javaeyeclient.ui;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleExpandableListAdapter;

public class MessagesAdapter extends SimpleExpandableListAdapter {

	public MessagesAdapter(Context context,
			List<? extends Map<String, ?>> groupData, int groupLayout,
			String[] groupFrom, int[] groupTo,
			List<? extends List<? extends Map<String, ?>>> childData,
			int childLayout, String[] childFrom, int[] childTo) {
		super(context, groupData, groupLayout, groupFrom, groupTo, childData,
				childLayout, childFrom, childTo);
	}

}

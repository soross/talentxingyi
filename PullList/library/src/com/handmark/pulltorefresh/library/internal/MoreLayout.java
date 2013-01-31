package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.R;

public class MoreLayout extends FrameLayout {

	private static final String TAG = "MoreLayout";

	private final ProgressBar mProgressBar;

	private final TextView mLoadMoreText;

	private final String LOADING_MORE = "加载更多";

	private final String IS_LOADING = "正在加载. . .";

	public MoreLayout(Context context, final Mode mode, TypedArray attrs) {
		super(context);
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header, this);
		mLoadMoreText = (TextView) header.findViewById(R.id.load_more_text);
		mProgressBar = (ProgressBar) header
				.findViewById(R.id.pull_to_refresh_progress);

		reset();
	}

	public void reset() {
		mProgressBar.setVisibility(View.GONE);
	}

	public void refreshing() {
		mProgressBar.setVisibility(View.VISIBLE);
	}
}

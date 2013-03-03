package com.imo.module.organize.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imo.R;

/**
 * StructNavItemView
 */
public class StructNavItemView extends LinearLayout {
	private String nodeName = null;

	private TextView nextImg = null;
	private TextView parentNode = null;

	public StructNavItemView(Context context) {
		super(context);
		init(context);
	}

	public StructNavItemView(Context context, String nodeName) {
		super(context);
		this.nodeName = nodeName;
		init(context);
	}

	public TextView getParentNode() {
		return parentNode;
	}

	/**
	 * 初始化方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public StructNavItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setOnClickListener(final OnClickListener listener) {
		parentNode.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

		parentNode.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {

					listener.onClick(v);
				}

				return false;
			}
		});
	}

	/**
	 * 更新显示状态：粗体
	 * 
	 * @param is
	 */
	public void updateShow(boolean isBlod) {
		parentNode.getPaint().setFakeBoldText(isBlod);
		parentNode.setText(parentNode.getText());
	}

	private void init(Context context) {
		getViewByInflater(context);
	}

	private void getViewByInflater(Context mContext) {
		parentNode = (TextView) findViewById(R.id.tv_parent);
		if (null != nodeName) {
			parentNode.setText(nodeName);
		}

		nextImg = (TextView) findViewById(R.id.tv_next_img);
	}

	public void hideNextImg() {
		nextImg.setVisibility(View.GONE);
	}

	public interface OnClickListener {
		public void onClick(View view);
	}

}

package com.imo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imo.R;

/**
 * StateItemView
 */
public class StateItemView extends LinearLayout {

	private boolean isCheckedState = false;

	private ImageView iv_state;

	private TextView tv_state_name;

	private ImageView iv_ischecked;

	private View mView;

	public StateItemView(Context context) {
		this(context, null);
	}

	public StateItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public void setData(int leftImgResId, String centerName, int rightImgResId) {
		iv_state.setImageResource(leftImgResId);
		tv_state_name.setText(centerName);
		iv_ischecked.setImageResource(rightImgResId);
	}

	public void setData(int leftImgResId, String centerName) {
		iv_state.setImageResource(leftImgResId);
		tv_state_name.setText(centerName);
	}

	/**
	 * 不显示左边的图片
	 * 
	 * @param centerName
	 * @param rightImgResId
	 */
	public void setData(String centerName, int rightImgResId) {
		iv_state.setVisibility(View.GONE);
		tv_state_name.setText(centerName);
		iv_ischecked.setImageResource(rightImgResId);
	}

	/**
	 * 绑定状态显示 和 逻辑的分离
	 * 
	 * @param listener
	 */
	public void setOnClickListener(final OnStateItemClickListener listener) {

		iv_ischecked.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					listener.onStateItemClick(StateItemView.this);
				}
				return true;
			}
		});
	}

	private void init(Context context, AttributeSet attrs) {

		getViewByInflater(context);

		if (attrs != null) {

			TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.StateItemView);

			Drawable drawableState = typeArray.getDrawable(R.styleable.StateItemView_statesrc);

			CharSequence stateName = typeArray.getText(R.styleable.StateItemView_statename);

			Drawable drawableIsChecked = typeArray.getDrawable(R.styleable.StateItemView_ischeckedsrc);

			typeArray.recycle();

			if (drawableState != null) {
				iv_state.setImageDrawable(drawableState);
			} else {
				iv_state.setVisibility(View.GONE);
			}

			if (stateName != null) {
				tv_state_name.setText(stateName);
			} else {
				tv_state_name.setText("");
			}

			if (drawableIsChecked != null) {
				iv_ischecked.setImageDrawable(drawableIsChecked);
			} else {
				iv_ischecked.setVisibility(View.INVISIBLE);
			}
		}

		this.setFocusable(false);
		this.setFocusableInTouchMode(false);
	}

	/**
	 * 修改ItemView的背景
	 * 
	 * @param resid
	 */
	public void setBackground(int resid) {
		mView.setBackgroundResource(resid);
	}

	private void getViewByInflater(Context mContext) {

		mView = LayoutInflater.from(mContext).inflate(R.layout.state_item_view, this, true);

		iv_state = (ImageView) findViewById(R.id.iv_state);

		tv_state_name = (TextView) findViewById(R.id.tv_state_name);

		iv_ischecked = (ImageView) findViewById(R.id.iv_ischecked);

		iv_ischecked.requestFocus();
	}

	public interface OnStateItemClickListener {

		void onStateItemClick(View v);
	}

}

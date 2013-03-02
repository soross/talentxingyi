package com.imo.view;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.imo.R;
import com.imo.util.LogFactory;

/**
 * 用户修改状态对话框 <br>
 * 在线，注销
 */
public class ChangeStatePop extends PopupWindow implements OnClickListener {

	private ChangeStatePop mChangeStatePop = null;

	private String TAG = "ChangeStatePop";

	private View mView;

	private View outside;

	// 联机状态
	private StateItemView item_onLineState;

	// 注销账号
	private StateItemView item_logout;

	private OnStateClickListener logoutListener = null;

	private OnStateClickListener reLoginListener = null;

	public ChangeStatePop(View contentView, int width, int height, boolean focusable) {
		super(contentView, width, height, focusable);

	}

	/**
	 * 弹窗显示的view
	 * 
	 * @param contentView
	 */
	public ChangeStatePop(View contentView) {

		super(contentView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		// super(contentView, LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT,true);
		mChangeStatePop = this;
		installViews(contentView);
	}

	private void installViews(View contentView) {
		mView = contentView;

		outside = mView.findViewById(R.id.outside);

		item_onLineState = (StateItemView) mView.findViewById(R.id.item_onLineState);
		item_logout = (StateItemView) mView.findViewById(R.id.item_logout);

		outside.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mChangeStatePop.isShowing()) {
						mChangeStatePop.dismiss();
					}
				}
				return true;
			}
		});

		item_onLineState.setOnClickListener(this);
		item_logout.setOnClickListener(this);

	}

	public void setListener(OnStateClickListener listener) {

		this.logoutListener = listener;
	}

	public void setReLoginListener(OnStateClickListener listener) {

		this.reLoginListener = listener;
	}

	public void popUp(View parent) {
		mChangeStatePop.update();
		mChangeStatePop.setBackgroundDrawable(new BitmapDrawable());
		mChangeStatePop.setOutsideTouchable(false);
		super.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	public void remove() {
		if (mChangeStatePop != null) {
			mChangeStatePop.dismiss();
		}
	}

	public interface OnStateClickListener {
		void onClickAction();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.item_onLineState:
				LogFactory.d(TAG, "--------------> click item_onLineState");
				remove();
				if (reLoginListener != null) {
					reLoginListener.onClickAction();
				}
				break;
			case R.id.item_logout:
				remove();
				LogFactory.d(TAG, "--------------> click item_logout");
				if (logoutListener != null) {
					logoutListener.onClickAction();
				}
				break;
		}
	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	//
	// if (event.getAction() == MotionEvent.ACTION_UP) {
	//
	// LogFactory.d("StatePop", "onTouch----------->");
	//
	// switch (v.getId()) {
	// case R.id.item_onLineState:
	// // remove();
	// break;
	// case R.id.item_logout:
	// // remove();
	// if (listener != null) {
	// listener.onClickAction();
	// }
	// break;
	//
	// default:
	// break;
	// }
	// }
	// return true;
	// }

}

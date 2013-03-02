package com.imo.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.imo.R;
import com.imo.module.contact.ContactActivity;
import com.imo.module.organize.OrganizeActivity;

/**
 * SearchBar
 */
public class SearchBar extends RelativeLayout implements OnClickListener {

	private Context mContext;

	private LayoutInflater inflater;

	private View searchView;

	private LinearLayout search_part;

	private ImageView iv_search;

	private boolean searchBarState = false;

	private EditText et_key;

	private Button btn_del;

	private ImageView iv_fixed;

	private int[] stateResIds = {
			R.drawable.icon_un_fixed, R.drawable.icon_fixed
	};

	/**
	 * Dynamic Search Listener
	 * 
	 * @param listener
	 */
	public void setOnSearchListener(final OnSearchListener listener) {

		et_key.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {

				if (et_key.getText().toString().trim().length() >= 1) {
					btn_del.setVisibility(View.VISIBLE);

					if (mContext instanceof OrganizeActivity) {
						OrganizeActivity.getActivity().pop_view.setVisibility(View.GONE);
					} else if (mContext instanceof ContactActivity) {
						ContactActivity.getActivity().pop_view.setVisibility(View.GONE);
					}
					listener.onSearch(et_key);
				} else {
					// et_key.setCursorVisible(false);
					btn_del.setVisibility(View.GONE);
					listener.initState();

					if (mContext instanceof OrganizeActivity) {
						OrganizeActivity.getActivity().pop_view.setVisibility(View.VISIBLE);
					} else if (mContext instanceof ContactActivity) {
						ContactActivity.getActivity().pop_view.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		// et_key.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// et_key.requestFocus();
		// InputMethodManager m =
		// (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		// m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		//
		//
		// }
		// });

	}

	// ======================更新点击状态===============================
	/**
	 * 实现更新是否固定：搜索工具条
	 * 
	 * 在不同的布局中显示
	 */
	private void updateFixedState(View one, View two) {

		searchBarState = !searchBarState;

		if (searchBarState) {
			iv_fixed.setImageResource(stateResIds[1]);
		} else {
			iv_fixed.setImageResource(stateResIds[0]);
		}

		if (searchBarState) {
			showView(one);
			hideView(two);
		} else {
			hideView(one);
			showView(two);
		}

	}

	public void updateFixedState() {

		searchBarState = !searchBarState;

		if (searchBarState) {
			iv_fixed.setImageResource(stateResIds[1]);
		} else {
			iv_fixed.setImageResource(stateResIds[0]);
		}

	}

	/**
	 * 绑定固定的事件
	 * 
	 * @param fixListener
	 */
	public void setFixListener(final SearchBarFixListener fixListener) {

		// iv_fixed.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// fixListener.onFix(v);//control
		// updateFixedState(fixListener.one, fixListener.two); //最终的实现:显示逻辑调用
		// // updateFixedState();
		//
		// }
		// });

		iv_fixed.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					fixListener.onFix(v);// control
					updateFixedState(fixListener.one, fixListener.two); // 最终的实现:显示逻辑调用
				}
				return true;
			}
		});
	}

	/**
	 * 界面的控制,监听对象使用该类
	 * 
	 * 需要实现固定操作。 实现逻辑分析： 使用的方案--替换法。
	 * 
	 * @author Scofield
	 * 
	 */
	public abstract class SearchBarFixListener implements OnSearchBarFixListener {

		public View one;

		public View two;

		public SearchBarFixListener(View one, View two) {
			this.one = one;
			this.two = two;
		}

	}

	public interface OnSearchListener {

		void onSearch(View v);

		void initState();
	}

	public interface OnSearchBarFixListener {

		void onFix(View v);
	}

	public boolean getState() {
		return searchBarState;
	}

	// =====================================================

	public SearchBar(Context context) {
		super(context);
		init(context);
	}

	public SearchBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {

		this.mContext = context;

		getViewByInflater(context);

		initSearchBar();

		// et_key.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// @Override
		// public void onFocusChange(View v, boolean hasFocus) {
		// et_key.setCursorVisible(hasFocus);
		// }
		// });
		et_key.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_key.setCursorVisible(true);
			}
		});

		// initBg();
		// searchView.setBackgroundDrawable(searchBarBg);
	}

	public void updateCursorState(boolean isShow) {
		et_key.setCursorVisible(isShow);
	}

	public void setOnClickEvent() {
		et_key.setOnClickListener(this);
	}

	/**
	 * 设置搜索监听
	 * 
	 * @param watcher
	 */
	public void setSearchListener(TextWatcher watcher) {
		et_key.addTextChangedListener(watcher);
	}

	private void initSearchBar() {

		iv_search = (ImageView) findViewById(R.id.iv_search);

		et_key = (EditText) findViewById(R.id.et_key);

		btn_del = (Button) findViewById(R.id.btn_delete);

		btn_del.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					et_key.setText("");
				}
				return true;
			}

		});
		// btn_del.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// et_key.setText("");
		// }
		// });

		// if (IMOApp.getApp().mScale != 1) {
		// et_key.setTextSize((int)(getResources().getDimension(R.dimen.searchbar_key_font_size
		// )* IMOApp.getApp().mScale));
		// }
		// iv_fixed = (ImageView) findViewById(R.id.iv_fixed);
	}

	public void setSearchListener(OnClickListener listener) {
		iv_search.setOnClickListener(listener);
	}

	public String getKeys() {
		return et_key.getText().toString().trim();
	}

	private void getViewByInflater(Context mContext) {
		inflater = LayoutInflater.from(mContext);
		searchView = inflater.inflate(R.layout.searchbar, this);
		// search_part = (LinearLayout) findViewById(R.id.search_part);
	}

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	//
	// // this.detector.onTouchEvent(event);
	// if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() ==
	// MotionEvent.ACTION_UP ) {
	// // System.out.println("px---cha---->" + (mScreenHeight - event.getY()));
	// }
	// // if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// // Log.v("Touch", "ACTION_DOWN");
	// // } else if (event.getAction() == MotionEvent.ACTION_UP) {
	// // Log.v("Touch", "ACTION_UP");
	// // } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	// // Log.v("Touch", "ACTION_MOVE");
	// // }
	// // return (super.onTouchEvent(event));
	// return true;
	// }

	private OnClickListener listener;

	// @Override
	// public boolean onDown(MotionEvent e) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void onShowPress(MotionEvent e) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean onSingleTapUp(MotionEvent e) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
	// float distanceY) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void onLongPress(MotionEvent e) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	// float velocityY) {
	// // TODO Auto-generated method stub
	// return false;
	// }

	private void hideView(View view) {
		view.setVisibility(View.GONE);
	}

	private void showView(View view) {
		view.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		updateCursorState(true);
		if (mContext instanceof OrganizeActivity) {
			if (et_key.getText().length() == 0) {
				OrganizeActivity.getActivity().pop_view.setVisibility(View.VISIBLE);
			}
		} else if (mContext instanceof ContactActivity) {
			if (et_key.getText().length() == 0) {
				ContactActivity.getActivity().pop_view.setVisibility(View.VISIBLE);
			}
		}
	}

	// @Override
	// protected void onDraw(Canvas canvas) {
	//
	// if (IMOApp.getApp().mScale!=1) {
	//
	// LogFactory.view("SearchBar", "SearchBar ------->onDraw");
	//
	// int height = (int)
	// (getResources().getDimension(R.dimen.searchbar_height_out) *
	// IMOApp.getApp().mScale);
	// ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)
	// getLayoutParams();
	// params.height = height;
	// setLayoutParams(params);
	//
	// RelativeLayout.LayoutParams et_key_params =
	// (RelativeLayout.LayoutParams)et_key.getLayoutParams();
	// et_key_params.addRule(RelativeLayout.CENTER_IN_PARENT);
	// int left
	// =((int)(getResources().getDimension(R.dimen.searchbar_content_padding_left)
	// * IMOApp.getApp().mScale));
	// et_key.setPadding(left, 0, 0, 0);
	// et_key.setLayoutParams(et_key_params);
	// }
	//
	// super.onDraw(canvas);

	// else {
	// ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)
	// getLayoutParams();
	// params.height = (int) (mContext.getResources().getDimension(
	// R.dimen.searchbar_height_in) );
	// setLayoutParams(params);
	// }
	// }

	// private LayerDrawable searchBarBg = null;
	//
	// private void initBg(){
	// int top = (int)
	// (mContext.getResources().getDimension(R.dimen.searchbar_height_out) *
	// IMOApp.getApp().mScale);
	// Drawable[] searchbarBg = new Drawable[2];
	// searchbarBg[0] =
	// mContext.getResources().getDrawable(R.drawable.imo_searchbar_gradual_bg);
	// searchbarBg[1] =
	// mContext.getResources().getDrawable(R.drawable.searchbar_frame);
	// searchBarBg = new LayerDrawable(searchbarBg);
	// searchBarBg.setLayerInset(0, 0, 0, 0, 0);
	// int padding =
	// (int)(mContext.getResources().getDimension(R.dimen.searchbar_padding) *
	// IMOApp.getApp().mScale);
	// searchBarBg.setLayerInset(1, padding,padding,padding, padding);
	// }

}

package com.imo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imo.R;

/**
 * SettingItemView
 * 
 * @author CaixiaoLong
 *
 */
public class SettingItemView extends LinearLayout {

	private ImageView iv_left; 

	private TextView tv_name , tv_center_name;

	private ImageView iv_right;

	private CheckBox checkBox_right;

	private View mView;

	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}



	public void setData(int leftImgResId,String centerName, int rightImgResId){
		iv_left.setImageResource(leftImgResId);
		tv_name.setText(centerName);
		iv_right.setImageResource(rightImgResId);
	}

	public void setData(int leftImgResId,String centerName){
		iv_left.setImageResource(leftImgResId);
		tv_name.setText(centerName);
	}

	/**
	 * 获得当前是否选中状态
	 * 
	 * @return
	 */
	public boolean  isChecked(){
		return checkBox_right.isChecked();
	}

	/**
	 * 进入界面的时候初始化是否处于选中状态
	 * 
	 * @param isChecked
	 */
	public void initCheckedState(boolean isChecked){
		checkBox_right.setChecked(isChecked);
	}

	/**
	 * 不显示左边的图片
	 * @param centerName
	 * @param rightImgResId
	 */
	public void setData(String centerName, int rightImgResId){
		iv_left.setVisibility(View.GONE);
		tv_name.setText(centerName);
		iv_right.setImageResource(rightImgResId);
	}

	/**
	 * 控制设置日期
	 * @param txt
	 * @param position
	 */
	public void updateText(String txt,int position){

		String[] content = null;
		if(tv_name.getText().toString().indexOf("-")!=-1){
			content = tv_name.getText().toString().split("-");
			if (position == 0) {
				content[0] = txt;
			}else if (position ==1) {
				content[1] = txt;
			}
			tv_name.setText(content[0]+"-"+content[1]);
		}

	}


	//	public void setOnClickListener(final OnSettingItemClickListener listener){
	////		iv_right
	//		this.setOnTouchListener(new View.OnTouchListener() {
	//			
	//			@Override
	//			public boolean onTouch(View v, MotionEvent event) {
	//				if (event.getAction() == MotionEvent.ACTION_UP) {
	//					listener.onSettingItemClick(SettingItemView.this);
	//				}
	//				return true;
	//			}
	//		});
	//	}

	/**
	 * 添加控件的点击事件
	 * @param listener
	 */
	public void setOnClickListener(final OnSettingItemClickListener listener){
		this.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				checkBox_right.toggle();
			}
		});

		checkBox_right.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				listener.onSettingItemClick(SettingItemView.this);
			}
		});
	}




	private void init(Context context, AttributeSet attrs) {

		getViewByInflater(context);

		if (attrs != null) {

			TypedArray typeArray = context.obtainStyledAttributes(attrs,R.styleable.SettingItemView);

			Drawable drawableLeft = typeArray.getDrawable(R.styleable.SettingItemView_leftsrc);

			CharSequence leftName = typeArray.getText(R.styleable.SettingItemView_itemText);
			CharSequence centerName = typeArray.getText(R.styleable.SettingItemView_itemCenterText);

			Drawable drawableRight = typeArray.getDrawable(R.styleable.SettingItemView_rightsrc);

			Boolean hasRightCheckBox = typeArray.getBoolean(R.styleable.SettingItemView_hasRightCheckbox, false);

			typeArray.recycle();

			if (drawableLeft != null) {
				iv_left.setImageDrawable(drawableLeft);
			} else {
				iv_left.setVisibility(View.GONE);
			}

			if (leftName != null) {
				tv_name.setText(leftName);
			} else {
				tv_name.setText("");
			}

			if (centerName != null) {
				tv_center_name.setVisibility(View.VISIBLE);
				tv_center_name.setText(centerName);
			} else {
				tv_center_name.setText("");
			}

			if (drawableRight != null) {
				iv_right.setImageDrawable(drawableRight);
			} else {
				iv_right.setVisibility(View.INVISIBLE);
			}

			if (hasRightCheckBox) {
				checkBox_right.setVisibility(View.VISIBLE);
			} else {
				checkBox_right.setVisibility(View.GONE);
			}
		}

		this.setFocusable(false);
		this.setFocusableInTouchMode(false);
	}



	/**
	 * 修改ItemView的背景
	 * @param resid
	 */
	public void setBackground(int resid) {
		mView.setBackgroundResource(resid);
	}


	private void getViewByInflater(Context mContext) {

		mView = LayoutInflater.from(mContext).inflate(R.layout.setting_item_view, this, true);
		// itemView.setBackgroundResource(R.drawable.selector);

		iv_left = (ImageView) findViewById(R.id.iv_left);

		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_center_name = (TextView) findViewById(R.id.tv_center_name);

		iv_right = (ImageView) findViewById(R.id.iv_right);

		checkBox_right = (CheckBox) findViewById(R.id.checkbox_right);
	}



	public interface OnSettingItemClickListener{

		void onSettingItemClick(View v);
	}

}

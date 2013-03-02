package com.imo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.util.LogFactory;

/**
 * TitleBar
 */
public class TitleBar extends LinearLayout {

	/**
	 * TitleBar类型
	 */
	public static final int TYPE_DIALOGURE = 1;

	public static final int TYPE_ORGANIZE = 2;

	public static final int TYPE_CONTACT = 3;

	public static final int TYPE_GROUP = 4;

	private Context mContext;

	private LayoutInflater mInflater;

	private LinearLayout mTitleView;

	private View mView_DefaultType;

	private View mView_SpecialType;

	private String mCenterTitle = "";

	private Resources mRes;

	public TitleBar(Context context) {
		super(context);
		init(context);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {

		this.mContext = context;

		mRes = getResources();

		getViewByInflater(context);

		// if (IMOApp.getApp().mScale != 1) {
		// tv_name.setTextSize((float)
		// (getResources().getDimension(R.dimen.titlebar_name_fontsize) *
		// IMOApp.getApp().mScale)) ;
		// tv_worksign.setTextSize((float)
		// (getResources().getDimension(R.dimen.titlebar_worksign_fontsize) *
		// IMOApp.getApp().mScale)) ;
		// }

	}

	public void initGroupTitlebar() {
		// TODO Auto-generated method stub

	}

	public void initContactTitlebar() {
		initOrganizeTitlebar();
	}

	public void initDialogureTitlebar(String leftBtnString, String centerString) {
		initDefaultTitleBar(leftBtnString, centerString);
	}

	public void initDialogureTitlebar(String leftBtnString, String centerString, String rightBtnString) {
		initDefaultTitleBar(leftBtnString, centerString, rightBtnString);
	}

	public void initDefaultTitleBar(String centerString) {
		this.setVisibility(View.VISIBLE);

		hideView(mView_SpecialType);
		showView(mView_DefaultType);

		tv_center.setText(centerString);

	}

	public void initDefaultTitleBar(String leftBtnString, String centerString) {
		this.setVisibility(View.VISIBLE);

		hideView(mView_SpecialType);
		showView(mView_DefaultType);
		hideView(btn_right);

		if (leftBtnString != null && leftBtnString.equals(getResources().getString(R.string.back))) {
			btn_left.setPadding(6, 0, 0, 0);
			btn_left.setBackgroundResource(R.drawable.titlebar_btn_back_bg);
		} else {
			btn_left.setPadding(0, 0, 0, 0);
			btn_left.setBackgroundResource(R.drawable.titlebar_btn_bg);
		}

		btn_left.setText(leftBtnString);
		tv_center.setText(centerString);

	}

	public void initDefaultTitleBar(String centerString, String rightBtnString, boolean isRight) {
		this.setVisibility(View.VISIBLE);

		hideView(mView_SpecialType);
		showView(mView_DefaultType);
		hideView(btn_left);
		showView(btn_right);
		tv_center.setText(centerString);
		btn_right.setText(rightBtnString);
	}

	// /**
	// * 显示中间标题 和 右边按钮
	// *
	// * @param centerString
	// * @param rightBtnResId
	// */
	// public void initDefaultTitleBar(String centerString,int rightBtnResId) {
	// this.setVisibility(View.VISIBLE);
	//
	// hideView(mView_SpecialType);
	// showView(mView_DefaultType);
	// hideView(btn_left);
	// showView(btn_right);
	// tv_center.setText(centerString);
	// btn_right.setBackgroundResource(rightBtnResId);
	// }

	/**
	 * 初始化默认的
	 * 
	 * @param leftBtnString
	 * @param centerString
	 * @param rightBtnString
	 */
	public void initDefaultTitleBar(String leftBtnString, String centerString, String rightBtnString) {

		this.setVisibility(View.VISIBLE);

		showView(mView_DefaultType);
		hideView(mView_SpecialType);

		showView(btn_left);
		showView(btn_right);

		if (leftBtnString != null) {
			// if (
			// leftBtnString.equals(getResources().getString(R.string.back))) {
			// btn_left.setBackgroundResource(R.drawable.titlebar_btn_back_bg);
			// }else{
			// btn_left.setBackgroundResource(R.drawable.titlebar_btn_bg);
			// }
			if (leftBtnString != null && leftBtnString.equals(getResources().getString(R.string.back))) {
				btn_left.setPadding(6, 0, 0, 0);
				btn_left.setBackgroundResource(R.drawable.titlebar_btn_back_bg);
			} else {
				btn_left.setPadding(0, 0, 0, 0);
				btn_left.setBackgroundResource(R.drawable.titlebar_btn_bg);
			}

		} else {
			hideView(btn_left);
		}

		btn_left.setText(leftBtnString);
		tv_center.setText(centerString);
		if (rightBtnString != null) {
			showView(btn_right);
			btn_right.setText(rightBtnString);
		} else {
			hideView(btn_right);
		}
	}

	/**
	 * 初始化默认的
	 * 
	 * @param leftBtnString
	 * @param centerString
	 * @param rightBtnString
	 */
	public void initDefaultTitleBarForNameCard(String leftBtnString, String centerString, String rightBtnString, int backgroundId) {

		this.setVisibility(View.VISIBLE);

		showView(mView_DefaultType);
		hideView(mView_SpecialType);

		showView(btn_left);
		showView(btn_right);

		if (leftBtnString != null) {
			// if (
			// leftBtnString.equals(getResources().getString(R.string.back))) {
			// btn_left.setBackgroundResource(R.drawable.titlebar_btn_back_bg);
			// }else{
			// btn_left.setBackgroundResource(R.drawable.titlebar_btn_bg);
			// }
			if (leftBtnString != null && leftBtnString.equals(getResources().getString(R.string.back))) {
				btn_left.setPadding(6, 0, 0, 0);
				btn_left.setBackgroundResource(R.drawable.titlebar_btn_back_bg);
			} else {
				btn_left.setPadding(0, 0, 0, 0);
				btn_left.setBackgroundResource(R.drawable.titlebar_btn_bg);
			}

		} else {
			hideView(btn_left);
		}

		btn_left.setText(leftBtnString);
		tv_center.setText(centerString);
		if (rightBtnString != null) {
			showView(btn_right);
			btn_right.setText(rightBtnString);
		} else {
			hideView(btn_right);
		}

		// System.out.println("设置了RightButtonLength");
		// btn_right.setBackgroundResource(backgroundId);
	}

	// /**
	// * 初始化默认的: 使用图片
	// *
	// * @param leftBtnResId
	// * @param centerString
	// * @param rightBtnResId -1标示不需要
	// */
	// public void initDefaultTitleBar(int leftBtnResId,String centerString,int
	// rightBtnResId) {
	//
	// this.setVisibility(View.VISIBLE);
	//
	// showView(mView_DefaultType);
	// hideView(mView_SpecialType);
	// showView(btn_left);
	// showView(btn_right);
	// btn_left.setBackgroundResource(leftBtnResId);
	// tv_center.setText(centerString);
	// if (rightBtnResId != -1) {
	// showView(btn_right);
	// btn_right.setBackgroundResource(rightBtnResId);
	// }else{
	// hideView(btn_right);
	// }
	// }

	/**
	 * 更新 中间的显示信息
	 * 
	 * @param txt
	 */
	public void updateCenterInfo(String txt) {
		tv_center.setText(txt);
	}

	public void initOrganizeTitlebar() {

		this.setVisibility(View.VISIBLE);

		hideView(mView_DefaultType);
		showView(mView_SpecialType);

	}

	public void setLeftBtnListene(View.OnClickListener l) {
		btn_left.setOnClickListener(l);
	}

	public void setRightBtnListener(View.OnClickListener l) {
		btn_right.setOnClickListener(l);
	}

	// ===========================================

	private FrameLayout face_frame;

	private ImageView iv_face;

	private TextView tv_name;

	private ImageView iv_state;

	private TextView tv_worksign;

	private View stateLayoutView;

	// ============================
	private Button btn_left;

	private TextView tv_center;

	private Button btn_right;

	private View btn_system_set;

	/**
	 * 更新图片bitmap
	 * 
	 * @param bitmap
	 */
	public void setFaceBitmap(Bitmap bitmap) {
		iv_face.setImageBitmap(bitmap);
	}

	/**
	 * 获得头像控件
	 * 
	 * @return
	 */
	public ImageView getFaceView() {
		return iv_face;
	}

	/**
	 * 更新图片ResID
	 * 
	 * @param resId
	 */
	public void setFaceDefault(boolean isBoy) {
		if (isBoy) {
			iv_face.setImageResource(R.drawable.imo_default_face_boy);
		} else {
			iv_face.setImageResource(R.drawable.imo_default_face_girl);
		}
	}

	/**
	 * 设置默认不在线头像
	 */
	public void setOfflineFaceDefault() {
		iv_face.setImageResource(R.drawable.imo_default_face);
	}

	/**
	 * 添加头像的点击事件
	 * 
	 * @param listener
	 */
	public void setFaceOnClickListener(View.OnClickListener listener) {
		iv_face.setOnClickListener(listener);
	}

	/**
	 * 设置状态的点击事件
	 * 
	 * @param listener
	 */
	public void setStateOnClickListener(View.OnClickListener listener) {
		stateLayoutView.setOnClickListener(listener);
	}

	private void getViewByInflater(Context mContext) {

		mInflater = LayoutInflater.from(mContext);
		mTitleView = (LinearLayout) mInflater.inflate(R.layout.titlebar, this);
		face_frame = (FrameLayout) findViewById(R.id.face_frame);

		mView_SpecialType = findViewById(R.id.special_layout);
		mView_DefaultType = findViewById(R.id.default_layout);

		// ================================

		iv_face = (ImageView) findViewById(R.id.iv_face);
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_state = (ImageView) findViewById(R.id.iv_state);
		tv_worksign = (TextView) findViewById(R.id.tv_worksign);

		stateLayoutView = findViewById(R.id.state_layout);

		// ================================

		btn_left = (Button) findViewById(R.id.btn_left);
		tv_center = (TextView) findViewById(R.id.tv_center);
		btn_right = (Button) findViewById(R.id.btn_right);

		btn_system_set = (View) findViewById(R.id.btn_system_set);

	}

	public void setSystemSetListener(View.OnTouchListener listener) {
		btn_system_set.setOnTouchListener(listener);
	}

	/**
	 * 更新状态的Icon
	 * 
	 * @param isOnline
	 */
	public void updateStateIcon(boolean isOnline) {
		if (isOnline) {
			iv_state.setImageResource(R.drawable.state_online);
		} else {
			iv_state.setImageResource(R.drawable.state_offline);
		}
	}

	/**
	 * 更新组织架构界面的TitleBar头部信息
	 * 
	 * @param name
	 *        登陆用户名称
	 * @param sign
	 *        工作签名
	 */
	public void setOrganizeTitleData(String name, String sign) {

		if (name != null) {
			tv_name.setText(name);
		}

		if (sign != null) {
			tv_worksign.setText(sign);
		}

	}

	/**
	 * 更新工作签名
	 * 
	 * @param sign
	 */
	public void updateSign(String sign) {
		if (sign != null) {
			tv_worksign.setText(sign);
		}
	}

	private void hideView(View view) {
		view.setVisibility(View.GONE);
	}

	private void showView(View view) {
		view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (IMOApp.getApp().mScale != 1) {
			LogFactory.view("TitleBar", "TitleBar ------->onDraw");

			// int height1 = (int) (mContext.getResources().getDimension(
			// R.dimen.titlebar_face_height) * IMOApp.getApp().mScale);
			// ViewGroup.LayoutParams params1 = (ViewGroup.LayoutParams)
			// face_frame.getLayoutParams();
			// params1.width = height1;
			// params1.height = height1;
			// face_frame.setLayoutParams(params1);

			// ======================
			// int height = (int) (mContext.getResources().getDimension(
			// R.dimen.titlebar_height) * IMOApp.getApp().mScale);
			// ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)
			// getLayoutParams();
			// params.height = height;
			// setLayoutParams(params);
			//
			// RelativeLayout.LayoutParams params2 =
			// (RelativeLayout.LayoutParams)mView_SpecialType.getLayoutParams();
			// params2.addRule(RelativeLayout.CENTER_VERTICAL);
			// mView_SpecialType.setLayoutParams(params);
		}
		super.onDraw(canvas);
	}

}
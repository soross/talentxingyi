package com.imo.view;


import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.util.ImageUtil;
import com.imo.util.LogFactory;

/**
 * 最近联系人控件
 * 
 * @author  CaixiaoLong
 *
 */
public class RecentContactView  extends RelativeLayout{
	
	
	private String TAG = "RecentContactView";
	
	private View mView;
	
	private ImageView iv_left ;
	
	private TextView tv_name;
	
	private TextView tv_info;
	
	private View right_part;
	private TextView tv_time;
	private TextView tv_count;
	
	private Button right_btn;

	public RecentContactView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public RecentContactView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecentContactView(Context context) {
		super(context);
		init(context);
	}
	
	
	
	private void init(Context context) {
		
		getViewByInflater(context);
		update2State(true);
	}
	
	private void getViewByInflater(Context context) {
		
		mView = LayoutInflater.from(context).inflate(R.layout.recent_contact_view, this, true);
 
		iv_left = (ImageView) findViewById(R.id.face_frame);
//		iv_left = (ImageView) findViewById(R.id.iv_left);
		
		tv_name = (TextView) findViewById(R.id.tv_name);
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		
		right_part = findViewById(R.id.right_part);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_count = (TextView) findViewById(R.id.tv_count);
		
		right_btn = (Button) findViewById(R.id.btn_right);		
	}
	
	
	/**
	 * 更新显示的状态
	 * 
	 * @param isDefault
	 */
	public void update2State(boolean isDefault){
		
		if (isDefault) {
			right_part.setVisibility(View.VISIBLE);
			right_btn.setVisibility(View.GONE);
		}else {
			right_part.setVisibility(View.GONE);
			right_btn.setVisibility(View.VISIBLE);
		}
	}
	
	
	/**
	 * 使用实际的头像
	 * 
	 * @param bitmap
	 */
	public void setFaceImg(Bitmap bitmap,int state){
		int borderColor = 0xffCACACA;
		ImageUtil.setFaceImg(iv_left, bitmap, state,borderColor);
	}
	
	
	private int[] face_resId = {
			R.drawable.imo_default_face,
			R.drawable.imo_default_face_boy,
			R.drawable.imo_default_face_girl,
	};

	
	/**
	 * 使用默认头像
	 * 
	 * @param state
	 * @param isboy
	 */
	public void setFaceImg(int state ,boolean isboy ){
		Bitmap bitmap = null;
		int borderColor = Color.BLACK;
//		int borderColor = 0xffCACACA;
		float width = IMOApp.getApp().getResources().getDimension(R.dimen.titlebar_face_height);
		
		if (state == 0) {///不在线
			bitmap = ImageUtil.generalCornerBitmapByResId(face_resId[0], IMOApp.getApp().radius, borderColor,width,width);
//			iv_left.setImageResource(face_resId[0]);
		}else{
			if (isboy) {
//				iv_left.setImageResource(face_resId[1]);
				bitmap = ImageUtil.generalCornerBitmapByResId(face_resId[1], IMOApp.getApp().radius, borderColor,width,width);
			}else {
//				iv_left.setImageResource(face_resId[2]);
				bitmap = ImageUtil.generalCornerBitmapByResId(face_resId[2], IMOApp.getApp().radius, borderColor,width,width);
			}
		}
		iv_left.setImageBitmap(bitmap);
	}
	
	
	public void setData(String name,CharSequence info, String time){
		tv_name.setText(name);
		tv_info.setText(info);
		tv_time.setText(time);
	}
	
	/**
	 * 更新未读信息的数量显示
	 * 
	 * @param count
	 */
	public void updateCount(int count){
		LogFactory.d(TAG, "MSG count = " + count);
//		this.setTag(count);
		if (count <1) {
			tv_count.setVisibility(View.GONE);
			return ;
		}
		
		String showCountInfo = "";
		
		if (count >= 100 ) {
			showCountInfo = "99+";
		}else {
		   showCountInfo = count+"";
		}
		
		tv_count.setVisibility(View.VISIBLE);
		tv_count.setText(showCountInfo);
	}
	
	
	
	/**
	 * 绑定关闭 联系人的点击事件
	 * 
	 * @param listener
	 */
	public void setCloseListene(View.OnClickListener listener){
		
		right_btn.setOnClickListener(listener);
	}

}

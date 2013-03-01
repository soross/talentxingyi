package com.imo.module.welcome;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.imo.R;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.module.login.LoginActivity;
import com.imo.util.DialogFactory;
import com.imo.util.LogFactory;
import com.imo.view.CustomFlipper;

/**
 * 新功能介绍界面
 * 
 * @author CaixiaoLong
 *
 */
public class NewFeaturesActivity extends AbsBaseActivityNetListener {
	
	private CustomFlipper flipper;
	
	private GestureDetector detector;
	
	private int mResIdArray[]= {
			R.drawable.guide_1,
			R.drawable.guide_2,
			R.drawable.guide_3
			};
	
	private boolean isFromSetting;

	public static void launch(Context c) {
		Intent intent = new Intent(c, NewFeaturesActivity.class);
		c.startActivity(intent);
	}
	
	public static void launch(Context c,Bundle bundle) {
		Intent intent = new Intent(c, NewFeaturesActivity.class);
		intent.putExtras(bundle);
		c.startActivity(intent);
	}
	
	

	@Override
	protected void installViews() {
		setContentView(R.layout.newfeatures_activity);
		
		initIntentData();
		
		mTitleBar.setVisibility(View.GONE);
	    detector = new GestureDetector(new GuideGuesterListener());
		
		flipper = (CustomFlipper) findViewById(R.id.viewFlipper);
		
		for (int i = 0; i < mResIdArray.length; i++) {
			flipper.addView(addImageView(mResIdArray[i]));
		}
	}

	private void initIntentData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			isFromSetting = bundle.getBoolean("isFromSetting",false);
		}
	}
	
	@Override
	protected boolean needSendRecoverNotice() {
			return isFromSetting;
	}
	
	@Override
	protected void registerEvents() {

	}

	@Override
	public void refresh(Object param) {
	}
	
	private ImageView addImageView(int resId){
		ImageView img = new ImageView(mContext);
		img.setImageResource(resId);
		img.setScaleType(ScaleType.FIT_XY);
		img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
		if (resId == mResIdArray[mResIdArray.length-1] ) {
			img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (isFromSetting) {
						finish();
					}else {
						LoginActivity.launch(mContext);
						finish();
					}
				}
			});
		}
		return img;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		detector.onTouchEvent(event);
//		LogFactory.d(TAG ,"px------->" + event.getY());
//		LogFactory.d(TAG ,"px---cha---->" + (mScreenHeight - event.getY()));
		
//		if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP ) {   
////			LogFactory.d(TAG ,"px---cha---->" + (mScreenHeight - event.getY()));
//			if (flipper.getDisplayedChild() == 3 ) {
//				if (isNormalScreen) {
//					if ((mScreenHeight - event.getY()) < 180 && (mScreenHeight- event.getY())>90 ){
//						GuideActivity.this.finish();
//						return true;
//					} 
//				}else {
//					if ((mScreenHeight - event.getY()) < 85 && (mScreenHeight- event.getY())>40 ){
////						LogFactory.d(TAG ,"UserGuideActivity finish.................Small Screen");
//						GuideActivity.this.finish();
//						return true;
//					} 
//				}
//				
//			}
//		}
//		 if (event.getAction() == MotionEvent.ACTION_DOWN) {   
//	            Log.v("Touch", "ACTION_DOWN");   
//	        } else if (event.getAction() == MotionEvent.ACTION_UP) {   
//	            Log.v("Touch", "ACTION_UP");   
//	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {   
//	            Log.v("Touch", "ACTION_MOVE");   
//	        }   

//		return (super.onTouchEvent(event));
		return true;
	}
	
	
	private class GuideGuesterListener implements OnGestureListener{

		private static final String TAG = "GuideGuesterListener";

		@Override
		public boolean onDown(MotionEvent arg0) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (e1.getX() - e2.getX() > 70) {
				if (flipper.getDisplayedChild() == mResIdArray.length-1) {
					if (isFromSetting) {
						finish();
					}else {
						LoginActivity.launch(mContext);
						finish();
					}
					return false;
				}
				flipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_in));
				flipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out));
				flipper.showNext();
				
				LogFactory.d(TAG ,"position=" + (flipper.getDisplayedChild()));
				
				return true;
			} else if (e1.getX() - e2.getX() < -70) {
				if (flipper.getDisplayedChild() == 0) {
					return false;
				}
				flipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_in));
				flipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_out));
				flipper.showPrevious();
				LogFactory.d(TAG ,"current position = " + flipper.getDisplayedChild());
				return true;
			}
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isFinishing()) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (isFromSetting == true) {
				finish();
			}else {
				DialogFactory.promptExit(mContext).show();
			}
			
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		
	}

}

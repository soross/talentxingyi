package com.imo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * »¬ÆÁ
 * 
 * @author CaixiaoLong
 *
 */
public class CustomFlipper extends ViewFlipper {

		public CustomFlipper(Context context) {
			super(context);
		}

		public CustomFlipper(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void onDetachedFromWindow() {
			try {
				super.onDetachedFromWindow();
			} catch (IllegalArgumentException e) {
				stopFlipping();
			}
		}
		
	}
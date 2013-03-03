package com.imo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * <pre>
 * 工作签名专用
 * </pre>
 * @author CaixiaoLong
 *
 */
public class LinedEditText extends EditText {

	private Rect mRect;
	
	private Paint mPaint;

	public LinedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mRect = new Rect();
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.STROKE); 
		mPaint.setColor(0xffCDCECE);
//		mPaint.setColor(0xffD8E5E8);
//		mPaint.setColor(0x800000FF);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		int count = this.getLineCount();
		
		for (int i = 0; i < count; i++) {
			
			int baselineY = this.getLineBounds(i, mRect);
			canvas.drawLine(mRect.left, baselineY + 1 +15, mRect.right, baselineY + 1+15, mPaint);
		}
		super.onDraw(canvas);
	}
}

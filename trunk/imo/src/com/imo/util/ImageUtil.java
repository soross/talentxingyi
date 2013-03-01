package com.imo.util;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;

import com.imo.R;
import com.imo.global.Globe;
import com.imo.global.IMOApp;
import com.imo.module.organize.struct.Node;
import com.imo.network.net.EngineConst;

/**
 * 图片工具类
 * 
 * @author CaixiaoLong
 * 
 */
public class ImageUtil {
	

	/**
	 * 获得头像的名称
	 * @return
	 */
	private static String getImgPath(int uid) {
		return "HeadPic" + uid;
	}
	
	
	/**
	 * 根据UId更新用户头像,需要感知状态
	 * 
	 * @param imageView
	 * @param uid
	 */
	public static void changeFaceByUid(ImageView imageView,int uid,float face_width,boolean isNeedState){
		
		assert(uid != 0);
		
		Node userNode = null;
		boolean isBoy = true;
		
		boolean isOnLine = true;
		
		///是否需要状态的变更
		if (isNeedState) {
			isOnLine = IMOApp.getApp().isOnlineFindByUid(uid);
		}
		
		if (IMOApp.getApp()!=null) {
			userNode = IMOApp.getApp().mNodeMap.get(uid);
			
			 if (userNode!=null) {
				   isBoy = userNode.getNodeData().isBoy;
			   }else{
				   LogFactory.e("sex","sex error, user default boy");
			   }
			   
			////使用默认的头像
			if (isBoy) {
					imageView.setImageResource(R.drawable.imo_default_face_boy);
			} else {
					imageView.setImageResource(R.drawable.imo_default_face_girl);
			}
			
			if (uid == EngineConst.uId) {
				Bitmap bitmap =Globe.bm_head;
				int borderColor = 0xffCACACA;
				if (bitmap!=null) {
					ImageUtil.setFaceImg(imageView, bitmap, isOnLine,borderColor,face_width);
				}
			}else{
				
				/// 使用自己的头像
				try {
					byte[] b_about_head = IOUtil.readFile(getImgPath(uid), IMOApp.getApp());
					if (b_about_head != null && b_about_head.length > 0) {
						Bitmap bitmap = BitmapFactory.decodeByteArray(b_about_head, 0,
								b_about_head.length);
						int borderColor = 0xffCACACA;
						ImageUtil.setFaceImg(imageView, bitmap, isOnLine,borderColor,face_width);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
			
	}
	
	public static void changeFaceByUidForNetState(ImageView imageView,int uid,float face_width,boolean netState){
		
		assert(uid != 0);
		
		Node userNode = null;
		boolean isBoy = true;
		
		boolean isOnLine = netState;
		
		if (IMOApp.getApp()!=null) {
			userNode = IMOApp.getApp().mNodeMap.get(uid);
			
			if (userNode!=null) {
				isBoy = userNode.getNodeData().isBoy;
			}else{
				LogFactory.e("sex","sex error, user default boy");
			}
			
			////使用默认的头像
			if (isBoy) {
				imageView.setImageResource(R.drawable.imo_default_face_boy);
			} else {
				imageView.setImageResource(R.drawable.imo_default_face_girl);
			}
			
			if (uid == EngineConst.uId) {
				Bitmap bitmap =Globe.bm_head;
				int borderColor = 0xffCACACA;
				if (bitmap!=null) {
					ImageUtil.setFaceImg(imageView, bitmap, isOnLine,borderColor,face_width);
				}
			}else{
				
				/// 使用自己的头像
				try {
					byte[] b_about_head = IOUtil.readFile(getImgPath(uid), IMOApp.getApp());
					if (b_about_head != null && b_about_head.length > 0) {
						Bitmap bitmap = BitmapFactory.decodeByteArray(b_about_head, 0,
								b_about_head.length);
						int borderColor = 0xffCACACA;
						ImageUtil.setFaceImg(imageView, bitmap, isOnLine,borderColor,face_width);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * @param view
	 * @param bitmap
	 * @param isOnLine 是否在线
	 * @param borderColor
	 */
	public static void setFaceImg(ImageView view, Bitmap bitmap, boolean isOnLine,int borderColor,float face_width) {
		
		bitmap = generateRoundCornerBitmap(bitmap, IMOApp.getApp().radius, face_width, face_width, borderColor);
		
		if (!isOnLine) {
			ImageUtil.turn2Grey(view, bitmap,borderColor);
		} else {
			view.setImageBitmap(bitmap);
		}
	}

	/**
	 * 图片的缩放
	 * 
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap scaleImg(Bitmap bm, float newWidth, float newHeight) {

		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = newWidth/ width;
		float scaleHeight = newHeight / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	}

	/**
	 * @param view
	 * @param bitmap
	 * @param state
	 *            0 不在线
	 */
	public static void setFaceImg(ImageView view, Bitmap bitmap, int state,int borderColor) {
		float width = IMOApp.getApp().getResources().getDimension(R.dimen.titlebar_face_height);
		bitmap = generateRoundCornerBitmap(bitmap, IMOApp.getApp().radius, width, width, borderColor);
		
		if (state == 0) {
			ImageUtil.turn2Grey(view, bitmap,borderColor);
		} else {
			view.setImageBitmap(bitmap);
		}
	}


	/**
	 * turn 2 Grey
	 * 
	 * @param view
	 * @param bitmap
	 */
	public static void turn2Grey(ImageView view, Bitmap bitmap,int borderColor) {

		bitmap = generateRoundCornerBitmap(bitmap, IMOApp.getApp().radius,borderColor);
		Drawable drawable = new BitmapDrawable(bitmap);
		drawable.mutate();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
		drawable.setColorFilter(cf);
		view.setImageDrawable(drawable);
	}
	
	
	public static Bitmap generalCornerBitmapByResId(int resid, float roundPx,int boardColor,float newWidth,float newHeight){
		BitmapDrawable bitmapDrawable = (BitmapDrawable) IMOApp.getApp().getResources().getDrawable(resid);
		Bitmap bitmap = scaleImg(bitmapDrawable.getBitmap(), newWidth, newHeight);
		return generateRoundCornerBitmap(bitmap, roundPx,boardColor);
	}
	

	/*** 获得圆角图片 */
	public static Bitmap generateRoundCornerBitmap(Bitmap bitmap, float roundPx,int boardColor) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect_border = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		final Rect rect = new Rect(2, 2, bitmap.getWidth()-2, bitmap.getHeight()-2);
		final RectF rectF_border = new RectF(rect_border);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		if (boardColor!=-1) {
			paint.setColor(boardColor);
			paint.setStyle(Paint.Style.STROKE); 
			paint.setStrokeWidth(2);
			canvas.drawRoundRect(rectF_border, roundPx, roundPx, paint);
		}
//			paint.setColor(Color.WHITE);
		return output;
	}
	
	public static Bitmap generateRoundCornerBitmap(Bitmap bitmap, float roundPx,float width ,float height,int borderColor) {
	     bitmap = scaleImg(bitmap, width, height);
		
		return generateRoundCornerBitmap(bitmap, roundPx,borderColor);
	}

	/** * 将Drawable2Bitmap */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	private LayerDrawable getLayerDrawable(Context mContext) {

		// Bitmap bm =
		// BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.imo_default_face));
		// int height = (int)
		// (getResources().getDimension(R.dimen.titlebar_face_height) *
		// IMOApp.getApp().mScale);
		// iv_face.setImageBitmap(scaleImg(bm , height, height));

		LayerDrawable faceFrameBg;
		int top = (int) (mContext.getResources().getDimension(
				R.dimen.titlebar_face_height) * IMOApp.getApp().mScale);
		Drawable[] faceFrameArray = new Drawable[2];
		faceFrameArray[0] = mContext.getResources().getDrawable(
				R.drawable.imo_titlebar_gradual_bg);
		// faceFrameArray[1] =
		// mContext.getResources().getDrawable(R.drawable.imo_titlebar_bg);

		faceFrameBg = new LayerDrawable(faceFrameArray);
		faceFrameBg.setLayerInset(0, 0, 0, 0, 2);
		faceFrameBg.setLayerInset(1, 0, (top - 2), 0, 1);
		faceFrameBg.setLayerInset(2, 0, (top - 1), 0, 0);

		return faceFrameBg;
	}

}

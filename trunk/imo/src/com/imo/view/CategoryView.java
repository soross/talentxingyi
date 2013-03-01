package com.imo.view;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imo.R;
import com.imo.global.IMOApp;


/**
 * 类别显示View
 * 
 * @author CaixiaoLong
 *
 */
public class CategoryView extends LinearLayout {
	
	private TextView tv_category;
	private TextView tv_category_time;

	public CategoryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CategoryView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		
		getViewByInflater(context);
	}
	
	private void getViewByInflater(Context context) {
		
	    LayoutInflater.from(context).inflate(R.layout.category_view, this, true);

	    tv_category = (TextView) findViewById(R.id.tv_category);
	    tv_category_time = (TextView) findViewById(R.id.tv_category_time);
		
	}
	
	
	public String  getCategory(){
		return tv_category.getText().toString();
	}
	
	
	public void setCategoryText(String category){
		
		tv_category.setText(formatShowDate(category));
	}
	
	
	public void setCategoryTime(String time){
		if (time!=null) {
			tv_category_time.setVisibility(View.VISIBLE);
			tv_category_time.setText(time);
		}else{
			tv_category_time.setVisibility(View.GONE);
		}
	}

	
    /**
	 * 格式化最后需要显示的时间格式
	 * 
	 * @param date
	 * 
	 * @return
	 */
	private String formatShowDate(String date) {

		String showDate = "";

		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date(System.currentTimeMillis()));
		String yesterday = format.format(new Date(System.currentTimeMillis()
				- 24 * 60 * 60 * 1000L));

		if (date.equals(today)) {
			showDate = IMOApp.getApp().getResources().getString(R.string.today);
			setCategoryTime(date);
		} else if (date.equals(yesterday)) {
			showDate = IMOApp.getApp().getResources().getString(R.string.yesterday);
			setCategoryTime(date);
		} else {
			showDate = date;
			setCategoryTime(null);
		}

		return showDate;
	}
	
}

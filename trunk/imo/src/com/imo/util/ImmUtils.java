package com.imo.util;

import com.imo.global.IMOApp;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ImmUtils {
	// ���ؼ���
	public static void hideKeyboard(Context context,View view) {
		InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
		imm.hideSoftInputFromWindow(view.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// ��ʾ����
	public static void showKeyboard(Context context,View view) {
		InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
		imm.showSoftInput(view, 0);
	}
	
	//�жϼ����Ƿ���ʾ
	public static boolean isShowing(Context context,View view){
		InputMethodManager imm = ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE));
		return imm.isActive(view);
	}

}

package com.imo.common;

import android.R.integer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.imo.util.DialogFactory;
import com.imo.util.Functions;

public class DoCallAndShortMessageHelper {
	private String mobile;
	private String tel;
	private Context context;

	public DoCallAndShortMessageHelper(String mobile,
			String tel, Context context) {
		this.mobile = mobile;
		this.tel = tel;
		this.context = context;
	}

	public boolean canDoCall() {
		if (!Functions.isEmpty(mobile) || !Functions.isEmpty(tel))
			return true;
		return false;
	}

	public boolean canSendShortMessage() {
		if (!Functions.isEmpty(mobile))
			return true;
		if (!Functions.isEmpty(tel))
			return true;
		return false;
	}

	public void call() {
		boolean hasMobile = !Functions.isEmpty(mobile);
		boolean hasTel = !Functions.isEmpty(tel);
		if (hasMobile && hasTel) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						doCall(mobile);
					} else if (which == 1) {
						doCall(tel);
					}
				}
			};
			DialogFactory.selectMobile(context, mobile, tel, listener);
			return;
		}
		if (hasMobile) {
			doCall(mobile);
			return;
		}
		if (hasTel) {
			doCall(tel);
			return;
		}
	}

	private void doCall(String tel) {
		Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ tel));
		context.startActivity(myIntentDial);
	}

	public void sendShortMessage() {
		boolean hasMobile = !Functions.isEmpty(mobile);
		boolean hasTel = !Functions.isEmpty(tel);
		if (hasMobile && hasTel) {
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						sendShortMessage(mobile);
					} else if (which == 1) {
						sendShortMessage(tel);
					}
				}
			};
			DialogFactory.selectMobile(context, mobile, tel, listener);
			return;
		}
		if (hasMobile) {
			sendShortMessage(mobile);
			return;
		}
		if (hasTel) {
			sendShortMessage(tel);
			return;
		}
	}

	private void sendShortMessage(String mobile) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
				+ mobile));
		context.startActivity(intent);
	}
}

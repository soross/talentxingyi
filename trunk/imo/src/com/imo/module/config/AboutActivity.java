package com.imo.module.config;

import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.imo.R;
import com.imo.activity.AbsBaseActivity;
import com.imo.activity.AbsBaseActivityNetListener;

/**
 * 关于
 * 
 * @author CaixiaoLong
 * 
 */
public class AboutActivity extends AbsBaseActivityNetListener  implements OnClickListener{
	
	private TextView tv_mobile_version_v ;
	private TextView tv_version_info ;
	
	
	public static void launch(Context c) {
		Intent intent = new Intent(c, AboutActivity.class);
		c.startActivity(intent);
	}

	@Override
	protected void installViews() {
		setContentView(R.layout.about_activity);
		
		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back),
				resources.getString(R.string.about_app));
		
		mTitleBar.setLeftBtnListene(this);
		
		tv_mobile_version_v = (TextView) findViewById(R.id.tv_mobile_version_v);
		tv_version_info = (TextView) findViewById(R.id.tv_version_info);
	}

	@Override
	protected void registerEvents() {
		tv_mobile_version_v.setText("(v" +getVersionName()+" Beta)");
		String versionInfo = getResources().getString(R.string.version_info);
		
		versionInfo +=(" v"+getVersionName()) + "(Build " +(getVersionCode())+")";
		tv_version_info.setText(versionInfo);
	}

	@Override
	public void refresh(Object param) {

	}
	
	
	/**
	 * 获得手机版本信息
	 * 
	 * @return
	 */
	private String getVersionName(){
		
		String version = "1.0";
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
			version =  packInfo.versionName+".5";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}
	
	private String getVersionCode(){
		
		String code = "01";
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
			code =  "" +packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (code.length()<2) {
			code ="0"+code;
		}
		return code;
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_left:
			finish();
			break;

		default:
			break;
		}
		
	}

	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub
		
	}

}

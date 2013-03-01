package com.imo.module.config;

import java.nio.ByteBuffer;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TimePicker;

import com.imo.R;
import com.imo.activity.AbsBaseActivity;
import com.imo.activity.AbsBaseActivityNetListener;
import com.imo.view.SettingItemView;
import com.imo.view.SettingItemView.OnSettingItemClickListener;

/**
 * ‘⁄œﬂ◊¥Ã¨…Ë÷√
 * 
 * @author CaixiaoLong
 *
 */
public class StateSetActivity extends AbsBaseActivityNetListener  implements OnSettingItemClickListener{
	
	
	private SettingItemView background_keep_online;
	
	private SettingItemView background_keep_online_scope;

	public static void launch(Context c) {
		Intent intent = new Intent(c, StateSetActivity.class);
		c.startActivity(intent);
	}
	
	
	@Override
	protected void installViews() {
		setContentView(R.layout.stateset_activity);
		
		background_keep_online =(SettingItemView) findViewById(R.id.background_keep_online);
		
		background_keep_online_scope =(SettingItemView) findViewById(R.id.background_keep_online_scope);
		

		mTitleBar.initDefaultTitleBar(resources.getString(R.string.back), 
				resources.getString(R.string.state_setting));
		
		mTitleBar.setLeftBtnListene(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	
	}

	@Override
	protected void registerEvents() {
		// TODO Auto-generated method stub
		background_keep_online.setOnClickListener(this);
		
		background_keep_online_scope.setOnClickListener(this);
	}

	@Override
	public void refresh(Object param) {
		// TODO Auto-generated method stub

	}
	
	
	@Override
	public void onSettingItemClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.background_keep_online:
			
			break;
		case R.id.background_keep_online_scope:
			new TimePickerDialog(mContext, mTimeSetListener, 00, 00, true)
			.show();
			break;

		default:
			break;
		}
	}
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =   
            new TimePickerDialog.OnTimeSetListener()    
       {   
  
                @Override  
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {   
                	
                    background_keep_online_scope.updateText(formatTime(hourOfDay, minute),1);
                }   
            };   
            
            
            
      private String formatTime(int hourOfDay, int minute){
    	  
    	  new TimePicker(mContext);
    	  
    	  
    	  if( (minute+"").length() == 1) {
    		  return hourOfDay+":0"+ minute;
		}else {
			return hourOfDay+":"+ minute;
		}
    	  
      }


	@Override
	public void NotifyHttpPacketArrived(String aConnectionId, ByteBuffer aBuffer) {
		// TODO Auto-generated method stub
		
	}
  


}

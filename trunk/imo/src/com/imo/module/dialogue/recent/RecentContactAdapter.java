package com.imo.module.dialogue.recent;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.module.MainActivityGroup;
import com.imo.module.organize.struct.Node;
import com.imo.util.IOUtil;
import com.imo.util.LogFactory;
import com.imo.util.MessageDataFilter;
import com.imo.view.CategoryView;
import com.imo.view.RecentContactView;


/**
 * 最近联系人适配器
 * 
* @author CaixiaoLong
*
*/
public class RecentContactAdapter extends BaseAdapter {
	
	private String TAG = "RecentContactAdapter";
	
	private String TAG1 = "RecentContactItemCount";
	
	private int MAXSIZE = 100;
	
	private RecentContactAdapter adapter;
    
    private List<String> categoryList = null;
    
    private List<RecentContactInfo> dataList = null;
    
    private Context mContext = null ;
    
	private boolean isEditState = false;

    public void setEditState(boolean isEditState){
    	this.isEditState = isEditState;
    	
    	this.notifyDataSetChanged();
    }
    
    public RecentContactAdapter(Context mContext, List<String> categoryList, List<RecentContactInfo> dataList) {
    	this.mContext = mContext;
    	this.categoryList = categoryList;
        this.dataList = dataList;
        
        adapter = this;
    }
    
    @Override
    public boolean isEnabled(int position) {
    	
    	if (isEditState) {
    		 return false;
		}
    	
        if(categoryList.contains(getItem(position).toString())){  
            return false;
        }
        return super.isEnabled(position);
    }
    
    
	@Override
	public int getCount(){
		
		LogFactory.d(TAG,"show item count = " + dataList.size() );
		return dataList.size() > MAXSIZE ? MAXSIZE : dataList.size();
	}

	@Override
	public RecentContactInfo getItem(int position) {
		
		return dataList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		
		return position;
	}
    
	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	///1- reset count
    	if (position == 0 ) {
    		RecentContactActivity.getActivity().mTotalCount = 0 ; 
		}
    	
    	CategoryView categoryView = null;
    	
    	RecentContactView recentContactView = null;
    	
    	if (dataList.get(position)!=null) {
			
    		LogFactory.d(TAG,"RecentContactInfo = " + dataList.get(position).toString() );
		}else {
			LogFactory.d(TAG,"RecentContactInfo = null   postion = " + position );
		}
    	
    	boolean isCategory = false;
    	 
    	if (getItem(position)!=null) {
    		 isCategory = categoryList.contains(getItem(position).toString());
    	} 
        
    	if (isCategory) {
			if (convertView != null && convertView instanceof CategoryView ) {
				categoryView = (CategoryView)convertView.getTag();
			}else{
				categoryView = new CategoryView(mContext);
			    categoryView.setTag(categoryView);
			}
		}else{
			if (convertView != null && convertView instanceof RecentContactView ) {
				recentContactView = (RecentContactView)convertView.getTag();
			}else{
				recentContactView = new RecentContactView(mContext);
				recentContactView.setTag(recentContactView);
			}
		}
		
    	// 得到 Item的数据源 对象
		RecentContactInfo info =  getItem(position);
		
		//LogFactory.d(TAG1, info.getCount()+"");
		
		if(info != null )
		{
	        if (isCategory) {
	        	categoryView.setCategoryText(info.getCategory());
	        	convertView = categoryView;
			} else {
				
				/////update total count
				RecentContactActivity.getActivity().mTotalCount += info.getCount();
				
				/////// 更新头像
				setFace(recentContactView, info);
				
				if (isEditState) {
					recentContactView.update2State(false);
					
					// bind close event
					recentContactView.setCloseListene(
							new CloseListener(position,info.getCid(),info.getType(),
												info.getUid(),info.getTime().split(" ")[0]));
				} else {
					recentContactView.update2State(true);
				}
				
				CharSequence showLastMessage;
				
				try {
					if (info.getInfo()!=null && !info.getInfo().equals("")) {
						showLastMessage = MessageDataFilter.jsonToText(new JSONObject(
								info.getInfo()));
					}else {
						showLastMessage="";
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					showLastMessage = "Message Error";
				}
				
				recentContactView.setData(getNameByUid(info.getUid()), showLastMessage,formatTime(info.getTime()));
				
				/// 更新显示的数量
				recentContactView.updateCount(info.getCount());
				
				// Log.d("RecentContactInfo", info.toString());
				convertView = recentContactView;
				
				convertView.setBackgroundResource(R.drawable.recent_contact_item_bg);
			}	
		}
        
        ////// update 
        if (position == getCount() -1) {
        	 if (RecentContactActivity.getActivity().mTotalCount > 0) {
             	((MainActivityGroup)RecentContactActivity.getActivity().getParent()).mHandler.sendEmptyMessage(1);
     		}else{
     			((MainActivityGroup)RecentContactActivity.getActivity().getParent()).mHandler.sendEmptyMessage(0);
     		}
		}
       
        convertView.setTag(R.layout.recent_contact_view, getItem(position));
        return convertView;
    }
    
    private String getNameByUid(int uid){
    	String name = "";
    	Node tempNode = IMOApp.getApp().mNodeMap.get(uid);
    	if (tempNode!=null) {
			name = tempNode.getNodeData().nodeName;
		}
    	return name;
    }
    
	/**
	 * 格式化显示的时间
	 * 
	 * @param time
	 * @return
	 */
	private String formatTime(String time){
		
		String showTime =  time;
		try {
			String[] tempArray = time.split(" ");
			if (tempArray!=null && tempArray.length == 3) {
				showTime = tempArray[2].substring(0,5);
			}else{
				showTime = time.split(" ")[1].substring(0,5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return showTime;
	}
    
    class CloseListener implements View.OnClickListener{
    	
    	private int cid;
    	private int type;
    	private int uid;
    	private String date;
    	
    	private int position;
    	

		public CloseListener(int position,int cid,int type, int uid,String date) {
			this.position = position;
			this.cid = cid;
			this.type = type;
			this.uid = uid;
			this.date = date;
			LogFactory.d(TAG, "delete user in category :" + date);
		}

		@Override
		public void onClick(View v) {
			doDelUserContactRecord(position,cid,type,uid);
		}

		/**
		 * 根据员工的type , Uid 删除员工的信息
		 * 
		 * @param type
		 * @param uid
		 */
		private void doDelUserContactRecord(final int position,final int cid, final int type, final int uid) {
			
//			DialogFactory.alertDialog(
//					mContext,
//					mContext.getResources().getString(R.string.warn), 
//					mContext.getResources().getString(R.string.confirm_del_recent_contect), 
//					new String[] {
//								mContext.getResources().getString(R.string.ok),
//								mContext.getResources().getString(R.string.cancel) },
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
							
							boolean result = true;
							try {
								///删除数据库中最近联系人的记录
								result = IMOApp.getApp().imoStorage.deleteRecentContact(type, uid);
							} catch (Exception e) {
								
								result = false;
								
								LogFactory.d(TAG, "db delete failed...");
								e.printStackTrace();
							}
							
							/// 数据库中删除成功后，删除map数据源
							if (result) {
								///// delete build data source
								((RecentContactActivity)mContext).deleteRecentUser(uid, uid,date);
								
								//删除成功后，需要更新界面。在这个时候无需重新排序。
								dataList.remove(position);
								if (dataList.size()==categoryList.size() || dataList.size()<=1) {
									dataList.clear();
									categoryList.clear();
									((RecentContactActivity)mContext).show2State(true);
								}else {
									if (((RecentContactActivity)mContext).recentContactMap.get(date).keySet().size()==0) {
										/// delete the empty category
										dataList.remove(position-1);
										categoryList.remove(date);
									}
									
									((RecentContactActivity)mContext).show2State(false);
									adapter.notifyDataSetChanged();
								}
							}
//						}
//					},null).show();

		}
    	
    }
    
    /**
     * 更新最近联系人的头像
     * 
     * @param recentContactView
     * @param info
     */
	private void setFace(RecentContactView recentContactView,RecentContactInfo info) {
		
		Integer state = IMOApp.getApp().userStateMap.get(info.getUid());
		if (state == null) {
			state = IMOApp.getApp().outerUserStateMap.get(info.getUid());
			if (state == null) {
				state =0;
			}
		}
		
		state =  state & 0x000000FF;
		
		boolean isBoy =	((RecentContactActivity)mContext).getNodeByUid(info.getUid())!=null
						?((RecentContactActivity)mContext).getNodeByUid(info.getUid()).getNodeData().isBoy
						:false;
		
		/// update user state
		recentContactView.setFaceImg(state, isBoy);
		
		try {
			byte[] b_about_head = IOUtil.readFile(info.getImgPath(), mContext);
			if (b_about_head != null && b_about_head.length > 0) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(b_about_head, 0,b_about_head.length);
				recentContactView.setFaceImg(bitmap,state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param gender
	 * @return
	 */
	private boolean isBoy(int gender){
		
		return gender==0 ? false:true;	
	}
   
}

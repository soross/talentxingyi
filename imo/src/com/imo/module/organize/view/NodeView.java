package com.imo.module.organize.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.module.organize.EmployeeDetailActivity;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;


/**
 * 
 * �ؼ� NodeView
 * 
 * @author CaixiaoLong
 * 
 */
public class NodeView extends RelativeLayout {

	public static int TYPE_PARENT_NODE = 1;

	public static int TYPE_LEAF_NODE = 2;

	private Context mContext;

	private LayoutInflater inflater;

	private ViewGroup viewGroup;

	private ImageView iv_treestate;
	private TextView tv_nodename;
	private TextView tv_statistics;

	private ImageView iv_sex_icon;
	private TextView tv_leafname;
	private View iv_triangle;
	
	private int[] sexIcon ={
			R.drawable.icon_person,
			R.drawable.icon_boy ,
			R.drawable.icon_girl
	} ;
	
	private int[] pc_boy_Icon ={
			R.drawable.icon_person,
			R.drawable.state_pc_boy_online ,
			R.drawable.state_pc_boy_out,
			R.drawable.state_pc_boy_busy,
			R.drawable.state_pc_boy_back_atonce
//			0x00	����  
//			0x01	����    state_pc_online
//			0x02	�뿪    state_pc_out
//			0x03	æµ     state_pc_busy
//			0x04	���ϻ��� state_pc_back_atonce
	} ;
	
	private int[] pc_girl_Icon ={
			R.drawable.icon_person,
			R.drawable.state_pc_girl_online ,
			R.drawable.state_pc_girl_out,
			R.drawable.state_pc_girl_busy,
			R.drawable.state_pc_girl_back_atonce
	} ;

	private boolean initTag = true;
	
	public NodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, TYPE_PARENT_NODE);
	}

	/**
	 * ����View��Type���ʹ���View
	 * 
	 * @param context
	 * @param type
	 */
	public NodeView(Context context, int type) {
		super(context);
		init(context, type);
	}
	
	/**
	 * 
	 * @param context
	 * @param initTag false������
	 */
	public NodeView(Context context ,boolean initTag) {
		super(context);
		this.initTag = initTag;
		init(context, 1);
	}
	
	public void updateNodeState(boolean nodeState){
		if (nodeState==true) {
			iv_treestate.setImageResource(R.drawable.tree_expansion);
		}else {
			iv_treestate.setImageResource(R.drawable.tree_collapsing);
		}
	}
	
	

	public void updatePNState(Node node){
//		����״̬
	}
	
	public void setPNData(String nodeName,String statistics){
		tv_nodename.setText(nodeName);
		tv_statistics.setText(statistics);
	}
	
	/**
	 * ���¸��ڵ��������ʾ
	 * @param data 
	 */
	public void setPNData(NodeData data){
		tv_nodename.setText(data.nodeName);
		tv_statistics.setText(data.statistics);
	}
	
	/**
	 * ֻ��Ҫ����ͳ������
	 * @param statistics
	 */
	public void setPNStatistics(String statistics){
		tv_statistics.setText(statistics);
	}
	
	public void setLNData(Boolean isBoy ,String leafNodeName){
		
		if (isBoy) {
			iv_sex_icon.setImageResource(sexIcon[0]);
		}else {
			iv_sex_icon.setImageResource(sexIcon[1]);
		}
		
		if (isMySelfNode) {
			tv_leafname.getPaint().setFakeBoldText(true);
		}else{
			tv_leafname.getPaint().setFakeBoldText(false);
		}
		tv_leafname.setText(leafNodeName);
	}
	
	/////��ʾ��ǰ�ڵ��Ƿ�Ϊ��½�û�
	public boolean isMySelfNode = false;
	
	/**
	 * ����Ҷ�ӵĽڵ�
	 * 
	 * @param data
	 */
	public void setLNData(NodeData data, int onLineState){
	    onLineState =  onLineState & 0x0000FFFF;
		int fromEquipment = onLineState >> 8;
		int state = onLineState & 0x000000FF;
//		0x00	PC��
//		0x01	Android��
//		0x02	iPhone/iTouch��
//		0x03	iPad��
//		0x04	Phone7��

		////��ǰ�豸ΪPC
		if (fromEquipment == 0) {
			switch (state) {
			case 0:
				iv_sex_icon.setImageResource(pc_boy_Icon[0]);
				break;
			case 1:
				if (data.isBoy) {
					iv_sex_icon.setImageResource(pc_boy_Icon[1]);
				} else {
					iv_sex_icon.setImageResource(pc_girl_Icon[1]);
				}
				break;
			case 2:
				if (data.isBoy) {
					iv_sex_icon.setImageResource(pc_boy_Icon[2]);
				} else {
					iv_sex_icon.setImageResource(pc_girl_Icon[2]);
				}
				break;
			case 3:
				if (data.isBoy) {
					iv_sex_icon.setImageResource(pc_boy_Icon[3]);
				} else {
					iv_sex_icon.setImageResource(pc_girl_Icon[3]);
				}
				break;
			case 4:
				if (data.isBoy) {
					iv_sex_icon.setImageResource(pc_boy_Icon[4]);
				} else {
					iv_sex_icon.setImageResource(pc_girl_Icon[4]);
				}
				break;
			default:
				break;
			}
			
		}else{ /////�������豸��½����ʾ�ֻ�ͼ��
			switch (state) {
			case 0:
				iv_sex_icon.setImageResource(sexIcon[0]);
				break;
			case 1:
				if (data.isBoy) {
					iv_sex_icon.setImageResource(sexIcon[1]);
				} else {
					iv_sex_icon.setImageResource(sexIcon[2]);
				}
				break;
			default:
				break;
			}
		}
		
//		status˵������8λ�� 0-7λ
//		0x00	����  
//		0x01	����    state_pc_offline
//		0x02	�뿪    state_pc_out
//		0x03	æµ     state_pc_busy
//		0x04	���ϻ��� state_pc_back_atonce

//		��8λ��8-15λ
//		0x00	PC��
//		0x01	Android��
//		0x02	iPhone/iTouch��
//		0x03	iPad��
//		0x04	Phone7��

//		if (onLineState == 0) {
//			
//			iv_sex_icon.setImageResource(sexIcon[2]);
//		} else {
//
//			if (data.isBoy) {
//				iv_sex_icon.setImageResource(sexIcon[0]);
//			} else {
//				iv_sex_icon.setImageResource(sexIcon[1]);
//			}
//		}
		if (isMySelfNode) {
			tv_leafname.getPaint().setFakeBoldText(true);
		}else{
			tv_leafname.getPaint().setFakeBoldText(false);
		}
//		tv_leafname.setText(Html.fromHtml("<font blod="">"))
		tv_leafname.setText(data.nodeName);
	}
	
	
	private void init(Context context, int type) {

		this.mContext = context;
		getViewByInflater(context);

		iv_treestate = (ImageView) findViewById(R.id.iv_treestate);
		tv_nodename = (TextView) findViewById(R.id.tv_nodename);
		tv_statistics = (TextView) findViewById(R.id.tv_statistics);

		iv_sex_icon = (ImageView) findViewById(R.id.iv_sex_icon);
		tv_leafname = (TextView) findViewById(R.id.tv_leafname);
		iv_triangle =  findViewById(R.id.iv_triangle);
		
//		if (IMOApp.getApp().mScale!=1) {
//			int size = (int) (mContext.getResources().getDimension(R.dimen.dept_node_fontsize) * IMOApp.getApp().mScale);
//			tv_nodename.setTextSize(size);
//			tv_statistics.setTextSize(size);
//			tv_leafname.setTextSize(size);
//			tv_nodename.setTextColor(0xff666666);
//			tv_statistics.setTextColor(0xff666666);
//			tv_leafname.setTextColor(0xff666666);
//		}
		
		if (initTag) {
			builderViewByType(type);
		}
//		int height = (int) (mContext.getResources().getDimension(R.dimen.dept_node_height) * IMOApp.getApp().mScale);
//		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));
		
/*	    int height = getMeasuredHeight();  
        int width = getMeasuredWidth();  
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)getLayoutParams();  
//        params.width = (int) (width * IMOApp.getApp().mScale);  
        params.height = (int) (height * IMOApp.getApp().mScale); //��ԭ�еĸ߶� ��ȷŴ���  
        this.setLayoutParams(params);  */

	}

	public void builderViewByType(int type) {
		switch (type) {
		case 1:
			buildParentView();
			break;
		case 2:
			buildLeafView();
			break;

		default:
			break;
		}
	}

	private void buildLeafView() {
		 showView(iv_sex_icon);
		 showView(tv_leafname);
		 showView(iv_triangle);
		 
//		 hideView(iv_treestate);
		 iv_treestate.setVisibility(View.INVISIBLE);
		 hideView(tv_nodename);
		 hideView(tv_statistics);
		 
	}

	private void buildParentView() {
		
		hideView(iv_sex_icon);
		hideView(tv_leafname);
		hideView(iv_triangle);
		 
		showView(iv_treestate);
		showView(tv_nodename);
		showView(tv_statistics);
	}

	private void getViewByInflater(Context mContext) {
		inflater = LayoutInflater.from(mContext);
		viewGroup = (ViewGroup) inflater.inflate(R.layout.nodeview, this);
	}

	private void hideView(View view) {
		view.setVisibility(View.GONE);
	}

	private void showView(View view) {
		view.setVisibility(View.VISIBLE);
	}
	
	
	/**
	 * Ա���ڵ�� ��ϸ��Ϣ����¼�
	 * 
	 * @param bundle
	 */
	public void setOnTriangleClickListener(final Bundle bundle){
		
		iv_triangle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, EmployeeDetailActivity.class);
				if (bundle != null) {
					intent.putExtras(bundle);
				}
				mContext.startActivity(intent);
				
			}
		});
	}
	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		super.onDraw(canvas);
//		
//		if (IMOApp.getApp().mScale!=1) {
//			LogFactory.view("NodeView", "NodeView ------->onDraw");
//			
//			int height = (int) (mContext.getResources().getDimension(
//					R.dimen.dept_node_height) * IMOApp.getApp().mScale);
//			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
//			
//			params.height = height;
//			setLayoutParams(params);
//		}
//	}

}

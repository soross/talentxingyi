package com.imo.module.organize.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.imo.R;
import com.imo.global.IMOApp;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeManager;
import com.imo.util.LogFactory;


/**
 * 组织结构：顶部导航的控件，实现动态的更新。
 * 
 * @author CaixiaoLong
 *
 */
public class StructNavView extends HorizontalScrollView {
 
	
	private Context mContext;
	
	private LayoutInflater inflater;
	
	private HorizontalScrollView scrollView;
	
	private LinearLayout viewGroup;
	
	
	private OnClickListener listener;
	
	public boolean isNeedShow = true; 
	
	public void setOnItemClickListener(OrganizeAdapter adapter){
		StructNavItemView view = null;
		
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			
			view = (StructNavItemView) viewGroup.getChildAt(i);
			view.setOnClickListener(new StructNavItemListener(this, i, adapter));
				
		}
	}

	public StructNavView(Context context) {
		super(context);
		init(context);
	}
	
	public void scrollToRight(){
//		this.scrollView.smoothScrollTo(, y)
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//    	int childCount = getChildCount() ;
//    	//获取该ViewGroup的实际长和宽  涉及到MeasureSpec类的使用
//    	int specSize_Widht = MeasureSpec.getSize(widthMeasureSpec) ;
//    	int specSize_Heigth = MeasureSpec.getSize(heightMeasureSpec) ;
//    	
//    	Log.i(TAG, "**** specSize_Widht " + specSize_Widht+ " * specSize_Heigth   *****" + specSize_Heigth) ;
//    	
//    	//设置本ViewGroup的宽高
//    	setMeasuredDimension(specSize_Widht , specSize_Heigth) ;
//    	
//    	
//    	
//    	
//    	for(int i=0 ;i<childCount ; i++){
//    		View child = getChildAt(i) ;   //获得每个对象的引用
//    		child.measure(50, 50) ;   //简单的设置每个子View对象的宽高为 50px , 50px  
//    		//或者可以调用ViewGroup父类方法measureChild()或者measureChildWithMargins()方法
//    	    this.measureChild(child, widthMeasureSpec, heightMeasureSpec) ;
//    	}
//    	
//    
//	}
	/**
	 * 初始化方法
	 * @param context
	 * @param attrs
	 */
	public StructNavView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public int getViewGroupChildCount(){
		return viewGroup.getChildCount();
	}
	
	
	public void boldEndDeptName() {
		
		int count = viewGroup.getChildCount();
		
		StructNavItemView view = null; 

		for (int i = 0; i < count; i++) {
			view = (StructNavItemView) viewGroup.getChildAt(i);
			if (i<count-1) {
				view.updateShow(false);
			}else if( i == count-1){
				view.updateShow(true);
			}
		}
//		postInvalidate();
	}
	
	public void addItem(String nodeName){
		StructNavItemView view = new StructNavItemView(mContext, nodeName);
//		if (IMOApp.getApp().mScale!=1) {
//			int size = (int) (mContext.getResources().getDimension(R.dimen.navbar_item_fontsize) * IMOApp.getApp().mScale);
//			view.getParentNode().setTextSize(size);
//		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT
		);
		viewGroup.addView(view, params);
		scrollToEnd();
		
		boldEndDeptName();
	}
	
	/**
	 * @param nodeName
	 * @param node
	 */
	public void addItem(String nodeName,Node node){
		
		StructNavItemView view = new StructNavItemView(mContext, nodeName);
		view.getParentNode().setTag(node);////////
		
//		if (IMOApp.getApp().mScale!=1) {
//			int size = (int) (mContext.getResources().getDimension(R.dimen.navbar_item_fontsize) * IMOApp.getApp().mScale);
//			view.getParentNode().setTextSize(size);
//		}
		
		if (NodeManager.isRoot(node)) {
			view.hideNextImg();
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT
		);
		viewGroup.addView(view, params);
//		viewGroup.addView(view);
		mHandler.post(mScrollToEnd);
//		scrollToEnd();
		
		boldEndDeptName();
	}
	
//	public int getChildCount(){
//		if (viewGroup == null) {
//			return 0;
//		}
//		return viewGroup.getChildCount();
//	}
//	
	/**
	 * 删除所有的child 控件
	 */
	public void removeAllItemView(){
		viewGroup.removeAllViews();
		StructNavView.this.setVisibility(View.GONE);
	}
	
	/**
	 * 获得最后Item前对应的Node
	 * 
	 * @return
	 */
	public Node getLastChildNode(){
		
		if (viewGroup.getChildCount() < 2) {
			return null;
		}
		
		viewGroup.removeViewAt(viewGroup.getChildCount()-1);
		
		int position = viewGroup.getChildCount()-1;
		
		if (NodeManager.isRoot((Node)((StructNavItemView)viewGroup.getChildAt(position)).getParentNode().getTag())) {
			StructNavView.this.setVisibility(View.GONE);
		}else{
			StructNavView.this.setVisibility(View.VISIBLE);
		}
		
		boldEndDeptName();
		
		return (Node)((StructNavItemView)viewGroup.getChildAt(position)).getParentNode().getTag();
	}
	
	/**
	 * 获得当前的Item对应的Node
	 * 
	 * @return
	 */
	public Node getCurChildNode(){
		
		if (viewGroup.getChildCount() < 1) {
			return null;
		}
		
		int position = viewGroup.getChildCount()-1;
		
		if (NodeManager.isRoot((Node)((StructNavItemView)viewGroup.getChildAt(position)).getParentNode().getTag())) {
			StructNavView.this.setVisibility(View.GONE);
		}else{
			StructNavView.this.setVisibility(View.VISIBLE);
		}
		
		return (Node)((StructNavItemView)viewGroup.getChildAt(position)).getParentNode().getTag();
	}
	
	
	public void addItem(String nodeName,OnClickListener listener){
		StructNavItemView view = new StructNavItemView(mContext, nodeName);
		
//		if (IMOApp.getApp().mScale!=1) {
//			int size = (int) (mContext.getResources().getDimension(R.dimen.navbar_item_fontsize) * IMOApp.getApp().mScale);
//			view.getParentNode().setTextSize(size);
//		}
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.FILL_PARENT
		);
		viewGroup.addView(view, params);
		scrollToEnd();
	}
	
	private Handler mHandler ;
	
	
	/**
	 * 从from开始删除
	 * 
	 * @param from
	 */
	public void removeItemfrom(int from){
		
		int count = viewGroup.getChildCount()-1 - from;
		
	    for (int i = 0; i < count; i++) {
	    	viewGroup.removeViewAt(viewGroup.getChildCount()-1);
	    }
	}
	
	public void removeAll(){
		viewGroup.removeAllViews();
	}
	
	private void init(Context context) {
		this.mContext = context;
		mHandler = new Handler();
		getViewByInflater(context);
		
		if (IMOApp.getApp().mScale!=1) {
			////
		}
	}

	private void getViewByInflater(Context mContext) {
		inflater = LayoutInflater.from(mContext);
		scrollView = (HorizontalScrollView)inflater.inflate(R.layout.struct_nav_view, this);
		viewGroup =(LinearLayout)findViewById(R.id.struct_nav_view);
		scrollView.setScrollbarFadingEnabled(true);
	}
	
	
	private Runnable mScrollToEnd = new Runnable() {

		@Override
		public void run() {
			
			int offset = viewGroup.getMeasuredWidth() - scrollView.getWidth();
			
			if (offset > 0) {
				scrollView.scrollTo(offset, 0);
			}
		}

	};
//	========================================
	public void scrollToEnd(){
		
//		scrollView.scrollTo(viewGroup.getMeasuredWidth(), scrollView.getTop());
//		scrollView.requestLayout();
		
//		int curPos = viewGroup.getChildAt(viewGroup.getChildCount()-1).getRight();
//		for (int i = 0; i < viewGroup.getChildCount(); i++) {
//			curWidth += viewGroup.getChildAt(i).getWidth();
//		}
//		Log.d("112", "curPos = "+curPos);
//		
//		scrollView.smoothScrollTo(curPos, 0);
	}
	
	/**
	 * 导航条的 item的点击事件。
	 *  需要一个记录的导航节点。
	 */
    public interface OnItemClickListener {
        void onItemClick(ViewGroup parent, View view, int position);
    }
    
//    public void  setOnItemClickListener(){
//    	for (int i = 0; i < viewGroup.getChildCount(); i++) {
//			viewGroup.getChildAt(i).setOnClickListener(new StructNavListener(this,i,));
//		}
//    	
//    }
//    
    

    
    
	public class StructNavItemListener implements StructNavItemView.OnClickListener{
		
		private int position;
		private StructNavView structNavView;
		private OrganizeAdapter adapter ;

		public StructNavItemListener(StructNavView structNavView,int position,OrganizeAdapter adapter) {
			this.position = position;
			this.structNavView = structNavView;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
//			实现的逻辑:删除
			structNavView.removeItemfrom(position);
			
			boldEndDeptName();
			
			if (NodeManager.isRoot((Node)v.getTag())) {
//				isNeedShow = false;
				StructNavView.this.setVisibility(View.GONE);
			}else
				StructNavView.this.setVisibility(View.VISIBLE);
			
//			更新ListView显示数据.
			adapter.showChildNodes((Node)v.getTag());
		}
		
	}
	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		
//		if (IMOApp.getApp().mScale!=1) {
//			LogFactory.view("NavBar", "NavBar ------->onDraw");
//			
//			int height = (int) (mContext.getResources().getDimension(
//					R.dimen.navbar_height) * IMOApp.getApp().mScale);
//			ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)getLayoutParams();
//			params.height = height;
//			setLayoutParams(params);
//		}
//		super.onDraw(canvas);
//	}
	
	
}

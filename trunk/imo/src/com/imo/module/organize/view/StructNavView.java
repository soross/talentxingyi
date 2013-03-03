package com.imo.module.organize.view;

import android.content.Context;
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

/**
 * 组织结构：顶部导航的控件，实现动态的更新。
 */
public class StructNavView extends HorizontalScrollView {

	private Context mContext;

	private LayoutInflater inflater;

	private HorizontalScrollView scrollView;

	private LinearLayout viewGroup;

	public boolean isNeedShow = true;

	public void setOnItemClickListener(OrganizeAdapter adapter) {
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

	public void scrollToRight() {
	}

	/**
	 * 初始化方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public StructNavView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public int getViewGroupChildCount() {
		return viewGroup.getChildCount();
	}

	public void boldEndDeptName() {

		int count = viewGroup.getChildCount();

		StructNavItemView view = null;

		for (int i = 0; i < count; i++) {
			view = (StructNavItemView) viewGroup.getChildAt(i);
			if (i < count - 1) {
				view.updateShow(false);
			} else if (i == count - 1) {
				view.updateShow(true);
			}
		}
	}

	public void addItem(String nodeName) {
		StructNavItemView view = new StructNavItemView(mContext, nodeName);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
		viewGroup.addView(view, params);
		scrollToEnd();
		boldEndDeptName();
	}

	/**
	 * @param nodeName
	 * @param node
	 */
	public void addItem(String nodeName, Node node) {
		StructNavItemView view = new StructNavItemView(mContext, nodeName);
		view.getParentNode().setTag(node);// //////

		if (NodeManager.isRoot(node)) {
			view.hideNextImg();
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
		viewGroup.addView(view, params);
		mHandler.post(mScrollToEnd);

		boldEndDeptName();
	}
	
	/**
	 * 删除所有的child 控件
	 */
	public void removeAllItemView() {
		viewGroup.removeAllViews();
		StructNavView.this.setVisibility(View.GONE);
	}

	/**
	 * 获得最后Item前对应的Node
	 * 
	 * @return
	 */
	public Node getLastChildNode() {

		if (viewGroup.getChildCount() < 2) {
			return null;
		}

		viewGroup.removeViewAt(viewGroup.getChildCount() - 1);

		int position = viewGroup.getChildCount() - 1;

		if (NodeManager.isRoot((Node) ((StructNavItemView) viewGroup.getChildAt(position)).getParentNode().getTag())) {
			StructNavView.this.setVisibility(View.GONE);
		} else {
			StructNavView.this.setVisibility(View.VISIBLE);
		}

		boldEndDeptName();

		return (Node) ((StructNavItemView) viewGroup.getChildAt(position)).getParentNode().getTag();
	}

	/**
	 * 获得当前的Item对应的Node
	 * 
	 * @return
	 */
	public Node getCurChildNode() {

		if (viewGroup.getChildCount() < 1) {
			return null;
		}

		int position = viewGroup.getChildCount() - 1;

		if (NodeManager.isRoot((Node) ((StructNavItemView) viewGroup.getChildAt(position)).getParentNode().getTag())) {
			StructNavView.this.setVisibility(View.GONE);
		} else {
			StructNavView.this.setVisibility(View.VISIBLE);
		}

		return (Node) ((StructNavItemView) viewGroup.getChildAt(position)).getParentNode().getTag();
	}

	public void addItem(String nodeName, OnClickListener listener) {
		StructNavItemView view = new StructNavItemView(mContext, nodeName);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
		viewGroup.addView(view, params);
		scrollToEnd();
	}

	private Handler mHandler;

	/**
	 * 从from开始删除
	 * 
	 * @param from
	 */
	public void removeItemfrom(int from) {

		int count = viewGroup.getChildCount() - 1 - from;

		for (int i = 0; i < count; i++) {
			viewGroup.removeViewAt(viewGroup.getChildCount() - 1);
		}
	}

	public void removeAll() {
		viewGroup.removeAllViews();
	}

	private void init(Context context) {
		this.mContext = context;
		mHandler = new Handler();
		getViewByInflater(context);

		if (IMOApp.getApp().mScale != 1) {
		}
	}

	private void getViewByInflater(Context mContext) {
		inflater = LayoutInflater.from(mContext);
		scrollView = (HorizontalScrollView) inflater.inflate(R.layout.struct_nav_view, this);
		viewGroup = (LinearLayout) findViewById(R.id.struct_nav_view);
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
	
	public void scrollToEnd() {
	}

	/**
	 * 导航条的 item的点击事件。 需要一个记录的导航节点。
	 */
	public interface OnItemClickListener {
		void onItemClick(ViewGroup parent, View view, int position);
	}

	public class StructNavItemListener implements StructNavItemView.OnClickListener {

		private int position;
		private StructNavView structNavView;
		private OrganizeAdapter adapter;

		public StructNavItemListener(StructNavView structNavView, int position, OrganizeAdapter adapter) {
			this.position = position;
			this.structNavView = structNavView;
			this.adapter = adapter;
		}

		@Override
		public void onClick(View v) {
			// 实现的逻辑:删除
			structNavView.removeItemfrom(position);

			boldEndDeptName();

			if (NodeManager.isRoot((Node) v.getTag())) {
				// isNeedShow = false;
				StructNavView.this.setVisibility(View.GONE);
			} else
				StructNavView.this.setVisibility(View.VISIBLE);

			// 更新ListView显示数据.
			adapter.showChildNodes((Node) v.getTag());
		}

	}
}

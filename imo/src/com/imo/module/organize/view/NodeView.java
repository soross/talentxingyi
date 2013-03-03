package com.imo.module.organize.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.imo.R;
import com.imo.module.organize.EmployeeDetailActivity;
import com.imo.module.organize.struct.Node;
import com.imo.module.organize.struct.NodeData;

/**
 * 控件 NodeView
 */
public class NodeView extends RelativeLayout {

	public static int TYPE_PARENT_NODE = 1;

	public static int TYPE_LEAF_NODE = 2;

	private Context mContext;

	private ImageView iv_treestate;
	private TextView tv_nodename;
	private TextView tv_statistics;

	private ImageView iv_sex_icon;
	private TextView tv_leafname;
	private View iv_triangle;

	private int[] sexIcon = {
			R.drawable.icon_person, R.drawable.icon_boy, R.drawable.icon_girl
	};

	private int[] pc_boy_Icon = {
			R.drawable.icon_person, R.drawable.state_pc_boy_online, R.drawable.state_pc_boy_out, R.drawable.state_pc_boy_busy, R.drawable.state_pc_boy_back_atonce
	// 0x00 离线
	// 0x01 上线 state_pc_online
	// 0x02 离开 state_pc_out
	// 0x03 忙碌 state_pc_busy
	// 0x04 马上回来 state_pc_back_atonce
	};

	private int[] pc_girl_Icon = {
			R.drawable.icon_person, R.drawable.state_pc_girl_online, R.drawable.state_pc_girl_out, R.drawable.state_pc_girl_busy, R.drawable.state_pc_girl_back_atonce
	};

	private boolean initTag = true;

	public NodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, TYPE_PARENT_NODE);
	}

	/**
	 * 根据View的Type类型创建View
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
	 * @param initTag
	 *        false不构建
	 */
	public NodeView(Context context, boolean initTag) {
		super(context);
		this.initTag = initTag;
		init(context, 1);
	}

	public void updateNodeState(boolean nodeState) {
		if (nodeState == true) {
			iv_treestate.setImageResource(R.drawable.tree_expansion);
		} else {
			iv_treestate.setImageResource(R.drawable.tree_collapsing);
		}
	}

	public void updatePNState(Node node) {
		// 更新状态
	}

	public void setPNData(String nodeName, String statistics) {
		tv_nodename.setText(nodeName);
		tv_statistics.setText(statistics);
	}

	/**
	 * 更新父节点的数据显示
	 * 
	 * @param data
	 */
	public void setPNData(NodeData data) {
		tv_nodename.setText(data.nodeName);
		tv_statistics.setText(data.statistics);
	}

	/**
	 * 只需要更新统计数据
	 * 
	 * @param statistics
	 */
	public void setPNStatistics(String statistics) {
		tv_statistics.setText(statistics);
	}

	public void setLNData(Boolean isBoy, String leafNodeName) {

		if (isBoy) {
			iv_sex_icon.setImageResource(sexIcon[0]);
		} else {
			iv_sex_icon.setImageResource(sexIcon[1]);
		}

		if (isMySelfNode) {
			tv_leafname.getPaint().setFakeBoldText(true);
		} else {
			tv_leafname.getPaint().setFakeBoldText(false);
		}
		tv_leafname.setText(leafNodeName);
	}

	// ///表示当前节点是否为登陆用户
	public boolean isMySelfNode = false;

	/**
	 * 设置叶子的节点
	 * 
	 * @param data
	 */
	public void setLNData(NodeData data, int onLineState) {
		onLineState = onLineState & 0x0000FFFF;
		int fromEquipment = onLineState >> 8;
		int state = onLineState & 0x000000FF;
		// 0x00 PC版
		// 0x01 Android版
		// 0x02 iPhone/iTouch版
		// 0x03 iPad版
		// 0x04 Phone7版

		// //当前设备为PC
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

		} else { // ///其他的设备登陆，显示手机图标
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
		if (isMySelfNode) {
			tv_leafname.getPaint().setFakeBoldText(true);
		} else {
			tv_leafname.getPaint().setFakeBoldText(false);
		}
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
		iv_triangle = findViewById(R.id.iv_triangle);

		if (initTag) {
			builderViewByType(type);
		}
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

		// hideView(iv_treestate);
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
	}

	private void hideView(View view) {
		view.setVisibility(View.GONE);
	}

	private void showView(View view) {
		view.setVisibility(View.VISIBLE);
	}

	/**
	 * 员工节点的 详细信息点击事件
	 * 
	 * @param bundle
	 */
	public void setOnTriangleClickListener(final Bundle bundle) {

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
}

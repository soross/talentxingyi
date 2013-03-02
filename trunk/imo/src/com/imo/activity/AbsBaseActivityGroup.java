package com.imo.activity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.view.View;
import android.view.Window;

/**
 * 抽象基类ActivityGroup
 */
public abstract class AbsBaseActivityGroup extends ActivityGroup {

	private String TAG = "AbsBaseActivityGroup";

	protected Stack<View> mChildActivityStack = new Stack<View>();

	public AbsBaseActivityGroup() {
		super(true);
	}

	/**
	 * 获得当前ActivityId
	 * 
	 * @return
	 */
	protected String getCurrentId() {
		return mChildActivityStack.isEmpty() ? null : mChildActivityStack.lastElement().getTag().toString();
	}

	@Override
	public Activity getCurrentActivity() {
		if (mChildActivityStack == null || mChildActivityStack.isEmpty()) {
			return null;
		}
		return (Activity) mChildActivityStack.lastElement().getContext();
	}

	public Activity getActivityAt(final int index) {

		final int count = mChildActivityStack == null ? 0 : mChildActivityStack.size();

		if (index < 0 || index >= count) {
			return null;
		}

		return (Activity) mChildActivityStack.elementAt(index).getContext();
	}

	public int getChildCount() {

		return mChildActivityStack == null ? 0 : mChildActivityStack.size();
	}

	/**
	 * 获得顶层的View
	 * 
	 * @param window
	 * @return
	 */
	protected View getTopLevelView(Window window) {

		return window.getDecorView();
	}

	/**
	 * 根据Id在回退栈中，查找位置
	 * 
	 * @param id
	 * @return -1表示不存在
	 */
	protected int locateActivity(final String id) {

		final Stack<View> stack = mChildActivityStack;

		final int size = stack == null ? 0 : stack.size();

		int location = -1;

		for (int i = size - 1; i >= 0; i--) {
			if (stack.elementAt(i).getTag().toString().equals(id)) {
				location = i;
				break;
			}
		}

		return location;
	}

	/**
	 * 销毁Child Activity
	 * 
	 * @param id
	 * @param finish
	 */
	protected void destroyActivity(String id, boolean finish) {

		final LocalActivityManager localActivityManager = getLocalActivityManager();

		localActivityManager.destroyActivity(id, finish);

		if (finish) {
			try {

				// Map<String, LocalActivityRecord> mActivities
				final Field field = LocalActivityManager.class.getDeclaredField("mActivities");
				field.setAccessible(true);
				final Map<String, Object> map = (Map<String, Object>) field.get(localActivityManager);
				map.remove(id);

			} catch (Exception e) {

			}
		}
	}

	/**
	 * ChildActivity重新获得焦点<br>
	 * 
	 */
	protected void resumeChildActivity() {
		if (mChildActivityStack == null || mChildActivityStack.isEmpty()) {
			return;
		}

		final View view = mChildActivityStack.lastElement();

		final ActivityTag tag = (ActivityTag) view.getTag();

		getLocalActivityManager().startActivity(tag.getId(), new Intent(this, tag.getActivityClass()).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		getCurrentActivity().onWindowFocusChanged(hasFocus);
	}

}

package org.talentware.android.activity;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.Scroller;
import org.talentware.android.MarriageApp;

public class DragableSpace extends ViewGroup {
    private Scroller mScroller;

    private VelocityTracker mVelocityTracker;

    private int mScrollX = 0;

    private int mCurrentScreen = 0;

    private float mLastMotionX;

    private float mLastMotionY;

    private static final String TAG = "DragableSpace";

    private static final int SNAP_VELOCITY = 1000;

    private final static int TOUCH_STATE_REST = 0;

    private final static int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;

    private final static int TOUCH_STATE_VERTICAL_SCROLLING = 2;

    private int mTouchState = TOUCH_STATE_REST;

    /**
     * 最小可以滚动的距离
     */
    private int mTouchSlop = 0;

    private Handler screenChangeHandler;

    private boolean hasInited = false;

    private boolean dragable = true;

    private boolean layoutChanged = true;


    private int orientation = -1;

    public final static int PORTRAIT = 1;

    public static final int LANDSCAPE = 2;

    /**
     * 设定该ViewGroup是横屏滑，还是 竖屏
     */
    public void setOrienation(int i) {
        orientation = i;
    }

    public DragableSpace(Context context) {
        super(context);
        init(context);
    }

    public DragableSpace(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();

        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.FILL_PARENT));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!dragable)
            return false;

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING)) {
            return true;
        }

        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                Log.v(TAG, "Intercept MotionEvent.ACTION_MOVE");
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);

                boolean xMoved = xDiff > mTouchSlop && xDiff > yDiff;

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
                } else {
                    mTouchState = TOUCH_STATE_VERTICAL_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                Log.v(TAG, "Intercept MotionEvent.ACTION_DOWN");
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                }
                mVelocityTracker.addMovement(ev);

                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;

                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't. mScroller.isFinished should be false when being
                 * flinged.
                 */
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_HORIZONTAL_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "Intercept MotionEvent.ACTION_UP & MotionEvent.ACTION_CANCEL");
                // Release the drag
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        /*
           * The only time we want to intercept motion events is if we are in the
           * drag mode.
           */
        return mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        Log.v(TAG, "this.getScrollX():" + this.getScrollX());
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        if (action != MotionEvent.ACTION_DOWN)
            mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // Scroll to follow the motion event
                final int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;
                mLastMotionY = y;

                if (deltaX < 0) {//从左往右手势
                    if (mScrollX > 0) {
                        scrollBy(Math.max(-mScrollX, deltaX), 0);
                    }
                } else if (deltaX > 0) {//从右往左手势

                    final int availableToScroll = (getVisibleChildCount() - 1) * getWidth() - mScrollX;
                    Log.v(TAG, "availableToScroll:" + availableToScroll);
                    if (availableToScroll > 0 && this.getScrollX() < 4 * getWidth() / 5) {
                        Log.v(TAG, "bbbbbbbbbbbb");
                        scrollBy(Math.min(availableToScroll, deltaX), 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();

                if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
                    // Fling hard enough to move left
                    snapLeftOrRight(true);
                } else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - 1) {
                    // Fling hard enough to move right
                    snapLeftOrRight(false);
                } else {
                    snapToDestination();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        mScrollX = this.getScrollX();
//        Log.v(TAG, "OnTouchEvent mScrollX:" + mScrollX);
        return true;
    }

    public void setDragable(boolean dragable) {
        this.dragable = dragable;
    }

    private void snapToDestination() {
        final int screenWidth = getWidth();
        final int whichScreen = (mScrollX + (screenWidth / 2)) / screenWidth + 1;
        setToVisibleScreen(computeScreen(whichScreen));
    }

    /**
     * 计算第几个可见界面实际是哪个界面
     *
     * @param whichScreen 第几个可见界面
     * @return 实际界面位置
     */
    private int computeScreen(final int whichScreen) {
        final int childCount = getChildCount();
        int visibleCount = 0;
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i).getVisibility() != View.GONE)
                visibleCount++;

            if (visibleCount >= whichScreen)
                return i;
        }

        return -1;
    }

    /**
     * 左右切换界面
     */
    public void snapLeftOrRight(final boolean left) {
        final int childCount = getChildCount();
        if (left && mCurrentScreen > 0) {// 查找离当前界面左边最近的可见界面
            for (int i = mCurrentScreen - 1; i > -1; i--) {
                if (getChildAt(i).getVisibility() == View.GONE)
                    continue;
                else {
                    mCurrentScreen = i;
                    break;
                }
            }
        } else if (!left && mCurrentScreen < childCount - 1) {// 查找离当前界面右边最近的可见界面
            for (int i = mCurrentScreen + 1; i < childCount; i++) {
                if (getChildAt(i).getVisibility() == View.GONE)
                    continue;
                else {
                    mCurrentScreen = i;
                    break;
                }
            }
        }

        final int newX = computeWidth(mCurrentScreen);
        Log.v(TAG, "mCurrentScreen:" + mCurrentScreen + ",newX:" + newX + ",mScrollX:" + mScrollX);
        final int delta = newX - mScrollX - (newX == getWidth() ? getWidth() / 5 : 0);
        mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) * 2);
        invalidate();

        refresh(mCurrentScreen);
    }

    /**
     * 计算界面的左边界
     *
     * @param whichScreen
     * @return
     */
    private int computeWidth(final int whichScreen) {
        int width = 0;

        for (int i = 0; i < whichScreen; i++) {
            if (getChildAt(i).getVisibility() != View.GONE) {

                if (orientation == PORTRAIT) {
                    width += MarriageApp.mScreenWidth;
                } else if (orientation == LANDSCAPE) {
                    width += MarriageApp.mScreenHeight;
                } else {
                    width += getWidth();
                }
            }
        }


        return width;
    }

    public void setToVisibleScreen(int whichScreen) {
        mCurrentScreen = whichScreen;
        final int newX = computeWidth(whichScreen);
        if (!hasInited) {
            mScroller.startScroll(newX, 0, 4 * getWidth() / 5, 0, 10);
        } else {
            mScroller.startScroll(newX, 0, 0, 0, 10);
        }
        invalidate();
        refresh(whichScreen);
    }

    public void resetToContent(){
        mScroller.startScroll(0, 0, 4 * getWidth() / 5, 0, 10);
    }

    public void setScreenChangeHandler(Handler handler) {
        screenChangeHandler = handler;
    }

    public void setCurrentScreen(final int currentScreen) {
        mCurrentScreen = currentScreen;
    }

    public void refresh(int whichScreen) {
        if (whichScreen > -1 && screenChangeHandler != null)
            screenChangeHandler.sendEmptyMessage(whichScreen);
    }

    public void setLayoutChanged(final boolean changed) {
        layoutChanged = changed;
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
//        Log.v(TAG, "OnLayout Method");
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                // 改造的方法：getMeasuredWidth()
                final int childWidth = child.getMeasuredWidth();
                Log.v(TAG, "ChildWidth:" + childWidth);
//                if (layoutChanged || (!layoutChanged && i == mCurrentScreen))// 总体布局未变化时只更新当前界面
                child.layout(childLeft, 0, childLeft + childWidth, getHeight());
                childLeft += childWidth;
            }
        }

        if (layoutChanged) {
            post(new Runnable() {
                public void run() {
                    layoutChanged = false;
                }
            });
        }

        if (!hasInited)// 初始定位屏幕
        {
            post(new Runnable() {
                public void run() {
                    setToVisibleScreen(mCurrentScreen);
                    hasInited = true;
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Log.v(TAG, "OnMeasure Method");
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("error mode.");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("error mode.");
        }


//        if (layoutChanged) {
        // The children are given the same width and height as the workspace
        final int count = getChildCount();
        Log.v(TAG, "In onMeasure , getWidth:" + getWidth());
        final int ViewWidth = getWidth();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec((i == 0 ? 4 * ViewWidth / 5 : ViewWidth), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
        }
//        } else {// 总体布局未变化时只更新当前界面
//            // Log.v(TAG, "LayOut Not Changed");
//            Log.v(TAG, "In onMeasure , getWidth:" + getWidth());
//            getChildAt(mCurrentScreen).measure(MeasureSpec.makeMeasureSpec((mCurrentScreen == 0 ? 4 * getWidth() / 5 : getWidth()), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
//        }
    }

    public int getVisibleChildCount() {
        final int count = super.getChildCount();
        int visibleCount = count;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE)
                --visibleCount;
        }
        return visibleCount;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mScrollX = mScroller.getCurrX() >= 4 * getWidth() / 5 ? 4 * getWidth() / 5 : mScroller.getCurrX();
//            Log.v(TAG, "ComputeScroll mScrollX:" + mScrollX);
            scrollTo(mScrollX, 0);
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        mScroller = null;
        mVelocityTracker = null;
        screenChangeHandler = null;
        super.onDetachedFromWindow();
    }

}

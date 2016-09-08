package com.example.android.recyclerplayground.layout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 9/8/2016.
 */
public class MyListManager extends RecyclerView.LayoutManager{

    public static final String TAG = "MyListManager";

    /* Fill Direction Constants */
    private static final int DIRECTION_NONE = -1;
    private static final int DIRECTION_START = 0;
    private static final int DIRECTION_END = 1;
    private static final int DIRECTION_UP = 2;
    private static final int DIRECTION_DOWN = 3;

    /* Consistent size applied to all child views */
    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;

    private int mVisibleRowCount;
    /* First (top-left) position visible at any point */
    private int mFirstVisiblePosition;

    public MyListManager(){
    }

    /*
     * Even without extending LayoutParams, we must override this method
     * to provide the default layout parameters that each child view
     * will receive when added.
    /**
     * {@inheritDoc}
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d(TAG, "child count: " + getChildCount() + " getPaddingLeft " + getPaddingLeft() + " paddingTop " + getPaddingTop() + "getItemCount " + getItemCount());
        if (getItemCount() == 0){
            detachAndScrapAttachedViews(recycler);
            return;
        }

        if (getChildCount() == 0){//  empty view child
            View firstScrapChild = recycler.getViewForPosition(0);
            addView(firstScrapChild);
            measureChildWithMargins(firstScrapChild, 0, 0);

              /*
             * We make some assumptions in this code based on every child
             * view being the same size (i.e. a uniform grid). This allows
             * us to compute the following values up front because they
             * won't change.
             */
            mDecoratedChildWidth = getDecoratedMeasuredWidth(firstScrapChild);
            mDecoratedChildHeight = getDecoratedMeasuredHeight(firstScrapChild);

            detachAndScrapView(firstScrapChild, recycler);
        }

        windowResize();

        int startLeftPadding = getPaddingLeft();
        int startTopPadding = getPaddingTop();

        if (getChildCount() == 0) { //First or empty layout
            //Reset the visible and scroll positions
            mFirstVisiblePosition = 0;
            startLeftPadding = getPaddingLeft();
            startTopPadding = getPaddingTop();
        }

        //Clear all views
        detachAndScrapAttachedViews(recycler);

        //Fill the grid for the initial layout of views
        fillGrid(DIRECTION_NONE, startLeftPadding, startTopPadding, recycler, state, null);

    }

    private void fillGrid(int direction, int recyclerTopLeftPadding, int recyclerTopPadding, RecyclerView.Recycler recycler, RecyclerView.State state, Object o) {

        Log.d(TAG, "only child count " + getChildCount() + " mVisibleRowCount " + mVisibleRowCount);

        if (mFirstVisiblePosition < 0) mFirstVisiblePosition = 0;
        if (mFirstVisiblePosition >= getItemCount()) mFirstVisiblePosition = (getItemCount() - 1);


        int startLeftOffset = recyclerTopLeftPadding;
        int startTopOffset = recyclerTopPadding;

        for (int i = 0; i<mVisibleRowCount; i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);

                  /*
                 * It is prudent to measure/layout each new view we
                 * receive from the Recycler. We don't have to do
                 * this for views we are just re-arranging.
                 */
            measureChildWithMargins(view, 0, 0);

            layoutDecorated(view,startLeftOffset, startTopOffset,startLeftOffset + mDecoratedChildWidth,startTopOffset + mDecoratedChildHeight);

            startTopOffset += mDecoratedChildHeight;
        }

    }


    private void windowResize() {

       mVisibleRowCount = getVerticalSpace()/mDecoratedChildHeight + 1;

        if (getVerticalSpace() % mDecoratedChildHeight > 0) {
            mVisibleRowCount++;
        }

        if (mVisibleRowCount > getItemCount()) {
            mVisibleRowCount = getItemCount();
        }

    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getChildCount() == 0){
            return 0;
        }

        //Take top measurements from the top child
        final View topView = getChildAt(0);
        //Take bottom measurements from the bottom child.
        final View bottomView = getChildAt(getChildCount()-1);


        //Optimize the case where the entire data set is too small to scroll
        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
        if (viewSpan < getVerticalSpace()) {
            //We cannot scroll in either direction
            return 0;
        }


        int delta = 0;
        int maxRowCount = mVisibleRowCount;
        boolean topBoundReached = mFirstVisiblePosition == 0;
        boolean bottomBoundReached = getLastVisibleRow() >= maxRowCount;
Log.d(TAG, "bottom reached: " + bottomBoundReached);
        //Check for up scroll
        if (dy > 0){
            int bottomOffset;
            if (bottomBoundReached){

                    //We are truly at the bottom, determine how far
                    bottomOffset = getVerticalSpace() - getDecoratedBottom(bottomView)
                            + getPaddingBottom();
                delta = Math.max(-dy, bottomOffset);
            } else {
                //No limits while the last row isn't visible
                delta = -dy;
            }

        }

        offsetChildrenVertical(delta);


        if (dy > 0) {
            Log.d(TAG, "dy "+ dy);
            if (getDecoratedBottom(topView) < 0 && !bottomBoundReached) {
                Log.d(TAG, "if dy "+ dy);
                fillGrid(DIRECTION_DOWN, 0, 0, recycler, state, null);
            } else if (!bottomBoundReached) {
                Log.d(TAG, "else dy "+ dy);
                fillGrid(DIRECTION_NONE, 0, 0, recycler, state, null);
            }
        }

        return -delta;
    }

    private int getLastVisibleRow() {
        return mFirstVisiblePosition + mVisibleRowCount;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }


    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }
}

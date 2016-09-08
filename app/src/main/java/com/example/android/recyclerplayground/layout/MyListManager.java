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

        int childLeft = 0;
        int childTop = 0;

        if (getChildCount() == 0) { //First or empty layout
            //Reset the visible and scroll positions
            mFirstVisiblePosition = 0;
            childLeft = getPaddingLeft();
            childTop = getPaddingTop();
        }

        //Clear all views
        detachAndScrapAttachedViews(recycler);

        //Fill the grid for the initial layout of views
        fillGrid(DIRECTION_NONE, childLeft, childTop, recycler, state, null);

    }

    private void fillGrid(int direction, int childLeft, int childTop, RecyclerView.Recycler recycler, RecyclerView.State state, Object o) {

        Log.d(TAG, "only child count " + getChildCount() + " mVisibleRowCount " + mVisibleRowCount);

        int startLeftOffset = 0;
        int startTopOffset = 0;

        for (int i = 0; i<mVisibleRowCount; i++){
            View view = recycler.getViewForPosition(i);
            addView(view);

           // layoutDecorated(view,0,0,500,200);
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
    public boolean canScrollHorizontally() {
        return false;
    }


    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }
}

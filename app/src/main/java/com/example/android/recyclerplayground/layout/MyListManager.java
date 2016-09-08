package com.example.android.recyclerplayground.layout;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by user on 9/8/2016.
 */
public class MyListManager extends RecyclerView.LayoutManager{

    public static final String TAG = "MyListManager";

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

        Log.d(TAG, "child count: " + getChildCount() + " getPaddingLeft " + getPaddingLeft() + " paddingTop " + getPaddingTop());
        //  super.onLayoutChildren(recycler, state);
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }
}

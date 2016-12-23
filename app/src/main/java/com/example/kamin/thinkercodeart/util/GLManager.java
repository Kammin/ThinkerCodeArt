package com.example.kamin.thinkercodeart.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Kamin on 23.12.2016.
 */

public class GLManager extends StaggeredGridLayoutManager {
    public GLManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
        super.setMeasuredDimension(childrenBounds, wSpec, hSpec);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, 500, 500);
    }
}

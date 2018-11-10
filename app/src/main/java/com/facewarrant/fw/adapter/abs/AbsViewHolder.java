package com.facewarrant.fw.adapter.abs;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by licrynoob on 2016/12/19 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * AbsHolder
 */

public class AbsViewHolder {

    private View mItemView;
    private SparseArray<View> mItemViewArray;

    public AbsViewHolder(View itemView) {
        mItemView = itemView;
        mItemViewArray = new SparseArray<>();
        mItemView.setTag(this);
    }

    /**
     * 通过childViewId获取控件
     *
     * @param childViewId 子控件id
     * @return childView
     */
    public <V extends View> V getChildView(int childViewId) {
        View childView = mItemViewArray.get(childViewId);
        if (childView == null) {
            childView = mItemView.findViewById(childViewId);
            mItemViewArray.put(childViewId, childView);
        }
        return (V) childView;
    }

    /**
     * 获取itemView
     *
     * @return itemView
     */
    public View getItemView() {
        return mItemView;
    }

    // 辅助方法
    /**
     * 设置文本
     *
     * @param childViewId 子控件id
     * @param text        文本
     * @return this
     */
    public AbsViewHolder setText(int childViewId, String text) {
        TextView childView = getChildView(childViewId);
        childView.setText(text);
        return this;
    }

}

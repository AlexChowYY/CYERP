package com.facewarrant.fw.adapter.abs;

import android.util.SparseArray;

/**
 * Created by licrynoob on 2016/12/19 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 子视图代表管理类
 */

public class AbsItemViewDelegateManager<T> {

    private SparseArray<AbsItemViewDelegate<T>> mDelegateArray;

    public AbsItemViewDelegateManager() {
        mDelegateArray = new SparseArray<>();
    }

    /**
     * 获取代理数量
     *
     * @return delegateSize
     */
    public int getDelegateSize() {
        return mDelegateArray.size();
    }

    /**
     * 增加 或者 修改 delegate is registered ?
     *
     * @param viewType 子视图类型
     * @param delegate 子视图代理
     * @return this
     */
    public AbsItemViewDelegateManager<T> putDelegate(int viewType, AbsItemViewDelegate<T> delegate) {
        if (delegate != null) {
            mDelegateArray.put(viewType, delegate);
        }
        return this;
    }

    /**
     * 在末尾添加子视图代理
     *
     * @param delegate 子视图代理
     * @return this
     */
    public AbsItemViewDelegateManager<T> putDelegateWithEnd(AbsItemViewDelegate<T> delegate) {
        int delegateArraySize = mDelegateArray.size();
        if (delegate != null) {
            mDelegateArray.put(delegateArraySize, delegate);
        }
        return this;
    }

    /**
     * 删
     *
     * @param viewType 子视图类型
     * @return this
     */
    public AbsItemViewDelegateManager<T> deleteDelegate(int viewType) {
        mDelegateArray.delete(viewType);
        return this;
    }

    /**
     * 子视图实现
     *
     * @param holder   AbsViewHolder
     * @param position 当前数据源选项
     * @param item     当前数据源
     */
    public void convert(AbsViewHolder holder, int position, T item) {
        int delegateArraySize = mDelegateArray.size();
        for (int i = 0; i < delegateArraySize; i++) {
            AbsItemViewDelegate<T> delegate = mDelegateArray.valueAt(i);
            if (delegate.isMatched(position)) {
                delegate.convert(holder, position, item);
                return;
            }
        }
        throw new IllegalArgumentException(
                "No AbsItemViewDelegate added that matches position = " + position);
    }

    /**
     * 获取子视图代理
     *
     * @param position 当前数据源位置
     * @return 子视图代理
     */
    public AbsItemViewDelegate<T> getDelegateByPosition(int position) {
        int delegateArraySize = mDelegateArray.size();
        for (int i = delegateArraySize - 1; i >= 0; i--) {
            AbsItemViewDelegate<T> delegate = mDelegateArray.valueAt(i);
            if (delegate.isMatched(position)) {
                return delegate;
            }
        }
        throw new IllegalArgumentException(
                "No AbsItemViewDelegate added that matches position = " + position);
    }

    /**
     * 获取子视图类型
     *
     * @param position 当前数据源位置
     * @return 子视图类型
     */
    public int getViewTypeByPosition(int position) {
        int delegateArraySize = mDelegateArray.size();
        for (int i = delegateArraySize - 1; i >= 0; i--) {
            AbsItemViewDelegate<T> delegate = mDelegateArray.valueAt(i);
            if (delegate.isMatched(position)) {
                return mDelegateArray.keyAt(i);
            }
        }
        throw new IllegalArgumentException(
                "No AbsItemViewDelegate added that matches position = " + position);
    }

    /**
     * 获取子视图布局id
     *
     * @param position 当前数据源位置
     * @return 子视图布局id
     */
    public int getItemViewLayoutIdByPosition(int position) {
        return getDelegateByPosition(position).getItemViewLayoutId();
    }

}

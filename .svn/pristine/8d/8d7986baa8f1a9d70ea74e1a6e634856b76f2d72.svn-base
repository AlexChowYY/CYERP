package com.facewarrant.fw.adapter.abs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by licrynoob on 2016/12/19 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * AbsAdapter
 */

public class AbsAdapter<T> extends BaseAdapter {

    protected List<T> mTList;
    private AbsItemViewDelegateManager<T> mDelegateManager;

    public AbsAdapter(List<T> TList) {
        mTList = TList;
        mDelegateManager = new AbsItemViewDelegateManager<>();
    }

    @Override
    public int getCount() {
        return mTList.size();
    }

    @Override
    public T getItem(int position) {
        return mTList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewLayoutId = mDelegateManager.getItemViewLayoutIdByPosition(position);
        AbsViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(itemViewLayoutId, parent, false);
            holder = new AbsViewHolder(convertView);
        } else {
            holder = (AbsViewHolder) convertView.getTag();
        }
        convert(holder, position, mTList.get(position));
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        if (isUsedDelegateManager()) {
            return mDelegateManager.getDelegateSize();
        }
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (isUsedDelegateManager()) {
            return mDelegateManager.getViewTypeByPosition(position);
        }
        return super.getItemViewType(position);
    }

    /**
     * 是否使用delegateManager
     *
     * @return if true
     */
    private boolean isUsedDelegateManager() {
        return mDelegateManager.getDelegateSize() > 0;
    }

    /**
     * 增加或者修改 delegate is registered ?
     *
     * @param viewType 子视图类型
     * @param delegate 子视图代理
     * @return this
     */
    public AbsAdapter<T> putDelegate(int viewType, AbsItemViewDelegate<T> delegate) {
        mDelegateManager.putDelegate(viewType, delegate);
        return this;
    }

    /**
     * 在末尾添加子视图代理
     *
     * @param delegate 子视图代理
     * @return this
     */
    public AbsAdapter<T> putDelegateWithEnd(AbsItemViewDelegate<T> delegate) {
        mDelegateManager.putDelegateWithEnd(delegate);
        return this;
    }

    /**
     * 子视图需实现的方法
     *
     * @param holder   AbsViewHolder
     * @param position 当前数据源选项
     * @param item     当前数据源
     */
    protected void convert(AbsViewHolder holder, int position, T item) {
        mDelegateManager.convert(holder, position, item);
    }

}

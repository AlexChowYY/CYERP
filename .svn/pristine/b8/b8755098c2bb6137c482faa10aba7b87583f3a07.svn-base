package com.facewarrant.fw.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by licrynoob on 2016/guide_2/25 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 通用的Adapter
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    private Context mContext;
    private List<T> mTList;
    private int mLayoutId;

    public CommonAdapter(Context context, List<T> TList, int layoutId) {
        mContext = context;
        mTList = TList;
        mLayoutId = layoutId;
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
        BaseHolder holder = BaseHolder.getInstance(mContext, convertView, parent, mLayoutId);
        convert(position, holder, getItem(position));
        return holder.getConvertView();
    }

    /**
     * 刷新Data
     *
     * @param newList 新的数据源
     */
    public void notifyData(List<T> newList) {
        mTList = newList;
        notifyDataSetChanged();
    }

    public abstract void convert(int position, BaseHolder holder, T item);

}

package com.facewarrant.fw.adapter.abs;

import java.util.List;

/**
 * Created by licrynoob on 2016/12/19 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 常用适配器
 */

public abstract class CommonAbsAdapter<T> extends AbsAdapter<T> {

    public CommonAbsAdapter(List<T> TList, final int itemViewLayoutId) {
        super(TList);
        putDelegateWithEnd(new AbsItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return itemViewLayoutId;
            }

            @Override
            public boolean isMatched(int position) {
                return true;
            }

            @Override
            public void convert(AbsViewHolder holder, int position, T item) {
                CommonAbsAdapter.this.convert(holder, position, item);
            }
        });
    }

    public abstract void convert(AbsViewHolder holder, int position, T item);

}

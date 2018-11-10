package com.facewarrant.fw.adapter.abs;

/**
 * Created by licrynoob on 2016/12/19 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 子视图代表
 */

public interface AbsItemViewDelegate<T> {

    /**
     * 获取子视图Id
     *
     * @return 子视图Id
     */
    int getItemViewLayoutId();

    /**
     * 当前数据源和子视图是否匹配
     *
     * @param position 当前数据源
     * @return if true
     */
    boolean isMatched(int position);

    /**
     * 子视图需实现的方法
     *
     * @param holder   AbsViewHolder
     * @param position 当前数据源选项
     * @param item     当前数据源
     */
    void convert(AbsViewHolder holder, int position, T item);

}

package com.facewarrant.fw.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by licrynoob on 2016/guide_2/13 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 通用的ViewHolder
 */
public class BaseHolder {

    private SparseArray<View> mViewArray;
    private View mConvertView;

    private BaseHolder(Context context, ViewGroup parent, int layoutId) {
        mViewArray = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    /**
     * 获取当前ViewHolder
     *
     * @param context     context
     * @param convertView convertView
     * @param parent      parent
     * @param layoutId    layoutId
     * @return BaseHolder
     */
    public static BaseHolder getInstance(Context context, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new BaseHolder(context, parent, layoutId);
        } else {
            return (BaseHolder) convertView.getTag();
        }
    }

    /**
     * 获取ConvertView
     *
     * @return ConvertView
     */
    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对应的控件，如果没有则加入ViewList
     *
     * @param childViewId 子控件Id
     * @param <T>         子控件类型
     * @return 子控件
     */
    public <T extends View> T getChildView(int childViewId) {
        View view = mViewArray.get(childViewId);
        if (view == null) {
            view = mConvertView.findViewById(childViewId);
            mViewArray.put(childViewId, view);
        }
        return (T) view;
    }

    /**
     * TextView相关设置 文字
     *
     * @param childViewId 子控件Id
     * @param text        文字类容
     * @return BaseHolder
     */
    public BaseHolder setText(int childViewId, String text) {
        TextView view = getChildView(childViewId);
        view.setText(text);
        return this;
    }

    public BaseHolder setText(int childViewId, SpannableString text) {
        TextView view = getChildView(childViewId);
        view.setText(text);
        return this;
    }

    /**
     * 通过资源为ImageView设置图片
     *
     * @param viewId   子控件Id
     * @param drawable drawable
     * @return BaseHolder
     */
    public BaseHolder setImageByDrawable(int viewId, int drawable) {
        ImageView view = getChildView(viewId);
        view.setImageResource(drawable);
        return this;
    }

    /**
     * 通过Bitmap为ImageView设置图片
     *
     * @param viewId 子控件Id
     * @param bitmap bitmap
     * @return BaseHolder
     */
    public BaseHolder setImageByBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getChildView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 通过Res为ImageView设置图片
     *
     * @param viewId   子控件Id
     * @param resource Resource
     * @return BaseHolder
     */
    public BaseHolder setImageByResource(int viewId, int resource) {
        ImageView view = getChildView(viewId);
        view.setImageResource(resource);
        return this;
    }

}

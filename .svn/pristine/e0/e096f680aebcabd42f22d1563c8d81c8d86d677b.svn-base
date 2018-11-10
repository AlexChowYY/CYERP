package com.facewarrant.fw.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facewarrant.fw.global.LocalApplication;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by licrynoob on 2016/guide_2/13 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * BaseFragment
 */
public abstract class BaseFragment extends Fragment implements BaseView {

    protected Fragment mFragment;
    protected LocalApplication mApp;
    protected Activity mActivity;
    protected LayoutInflater mInflater;
    protected ViewGroup mContainer;
    protected View mRootView;

    private Unbinder mUnbinder;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    /**
     * 获取布局Id
     *
     * @return InflateViewId
     */
    protected abstract int getInflateViewId();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = this;
        mApp = LocalApplication.getInstance();
        mInflater = inflater;
        mContainer = container;
        initInstanceState(savedInstanceState);
        beforeInflateView();
        if (mRootView == null) {
            mRootView = inflater.inflate(getInflateViewId(), container, false);
            mUnbinder = ButterKnife.bind(this, mRootView);
            initData();
            initEvent();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    /**
     * 初始化保存的数据
     */
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    /**
     * inflateView之前调用
     */
    protected void beforeInflateView() {

    }


    /**
     * 通过id初始化View
     *
     * @param viewId viewId
     * @param <T>    T
     * @return T
     */
    protected <T extends View> T byId(int viewId) {
        return (T) mRootView.findViewById(viewId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();

    }
}

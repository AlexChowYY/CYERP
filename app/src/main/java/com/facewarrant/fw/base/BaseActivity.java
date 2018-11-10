package com.facewarrant.fw.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facewarrant.fw.global.LocalApplication;
import com.facewarrant.fw.util.ActivityUtil;

import butterknife.ButterKnife;


/**
 * Created by licrynoob on 2016/guide_2/12 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    protected Context mContext;
    protected Activity mActivity;
    protected LocalApplication mApp;
    protected ActivityUtil mActivityUtil;
    protected LayoutInflater mInflater;
    private Handler mHandler;

    /**
     * 获取布局Id
     *
     * @return
     */
    protected abstract int getContentViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mActivity = this;
        mApp = LocalApplication.getInstance();
        mInflater = LayoutInflater.from(this);
        mActivityUtil = ActivityUtil.getInstance();
        mActivityUtil.addActivity(this);

        setSystemUi();
        initInstanceState(savedInstanceState);
        beforeSetContentView();
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        // EventBusUtils.register(this);
        initData();
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mActivityUtil.removeActivity(this);
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(mActivity);
        }
        //EventBusUtils.unregister(this);
    }

    /**
     * 设置系统界面
     */
    protected void setSystemUi() {

//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorTopBar));
//        }

        getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            StatusBarUtil.setStatusBarColor(this, R.color.colorWhite);
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.colorTopBar));
//        }

//        if (OSUtil.getRomType() == OSUtil.ROM_TYPE.MIUI) {
//            StatusBarUtil.MIUISetStatusBarLightMode(this.getWindow(), true);
//        } else if (OSUtil.getRomType() == OSUtil.ROM_TYPE.FLYME) {
        //    StatusBarUtil.FlymeSetStatusBarLightMode(this.getWindow(), true);
//        }
    }

    public final Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(getMainLooper());
        }
        return mHandler;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 初始化保存的数据
     */
    protected void initInstanceState(Bundle savedInstanceState) {

    }

    /**
     * setContentView之前调用
     */
    protected void beforeSetContentView() {

    }

    /**
     * 通过id初始化View
     *
     * @param viewId
     * @param <T>
     * @return
     */
    protected <T extends View> T byId(int viewId) {
        return (T) findViewById(viewId);
    }


}

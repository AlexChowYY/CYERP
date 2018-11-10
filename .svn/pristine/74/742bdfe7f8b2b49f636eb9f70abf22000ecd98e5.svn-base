package com.facewarrant.fw.global;

import android.app.Application;

import com.alibaba.sdk.android.oss.OSSClient;

import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.baidu.mapapi.SDKInitializer;
import com.facewarrant.fw.bean.UserBean;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitManager;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.jpush.android.api.JPushInterface;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class LocalApplication extends Application {
    private static final String TAG = "LocalApplication";
    private static LocalApplication sApp;
    private OSSClient mOSSClient;

    public static IWXAPI api;


    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        initRetrofit();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        String id = JPushInterface.getRegistrationID(getApplicationContext());
        LogUtil.e(TAG, id);
        SPUtil.put(Constant.REGISTER_ID, id);
        initLoginStatus();
        initUuId();
        System.loadLibrary("QuCore-ThirdParty");
        System.loadLibrary("QuCore");
        QupaiHttpFinal.getInstance().initOkHttpFinal();
        regToWx();
        SDKInitializer.initialize(this);
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, Constant.APP_ID, true);
        api.registerApp(Constant.APP_ID);
    }

    /**
     * 获取MyApplication实例
     */
    public static LocalApplication getInstance() {
        return sApp;
    }

    /**
     * 初始Retrofit
     */
    private void initRetrofit() {
        RequestManager.mRetrofitManager = new RetrofitManager.Builder()
                .baseUrl(RequestManager.mBaseUrl)
                .connectTimeout(20 * 1000)
                .readTimeout(20 * 1000)
                .writeTimeout(20 * 1000)
                .build();
    }

    private void initLoginStatus() { //保存登录状态
        String userBase64 = (String) SPUtil.get(Constant.USER, "");
        if (!"".equals(userBase64)) {
            AccountManager.sUserBean = (UserBean) CommonUtil.base64ToObject(userBase64);
        }
    }

    private void initUuId() {
        SPUtil.put(Constant.UUID, CommonUtil.android_id(this));

    }


}

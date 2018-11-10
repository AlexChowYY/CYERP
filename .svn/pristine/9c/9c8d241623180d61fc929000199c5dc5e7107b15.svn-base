package com.facewarrant.fw.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;


import com.aliyun.common.utils.ToastUtil;
import com.facewarrant.fw.bean.UserBean;
import com.facewarrant.fw.bean.WechatPayEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.global.LocalApplication;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.account.BindPhoneActivity;
import com.facewarrant.fw.ui.activity.MainActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.google.gson.JsonObject;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：maqing on 2016/12/2 0002 11:22
 * 邮箱：2856992713@qq.com
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    private static final int RETURN_MSG_TYPE_LOGIN = 1; //登录
    private static final int RETURN_MSG_TYPE_SHARE = 2; //分享

    private JSONObject mWXLoginSuccessData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalApplication.api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtil.e(TAG, baseResp.errCode + "");
        LogUtil.e(TAG, "onResp:------>");
        LogUtil.e(TAG, "error_code:---->" + baseResp.errCode);
        int type = baseResp.getType(); //类型：分享还是登录
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝授权
                ToastUtil.showToast(this, "拒绝授权微信登录");
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                String message = "";
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    message = "取消了微信登录";

                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    message = "取消了微信分享";
                }
                ToastUtil.showToast(this, message);
                finish();
                break;
            case BaseResp.ErrCode.ERR_OK:
                //用户同意
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code，仅在ErrCode为0时有效
                    String code = ((SendAuth.Resp) baseResp).code;
                    LogUtil.e(TAG, "code:------>" + code);
                    getWXUserInfo(code);


                    //这里拿到了这个code，去做2次网络请求获取access_token和用户个人信息

                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    ToastUtil.showToast(this, "微信分享成功");
                    finish();
                }
                break;
        }


    }

    private void getWXUserInfo(String code) {
        final Map<String, String> map = new HashMap<>();
        map.put("authCode", code);
        map.put("sourceType", 2 + "");

        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getWXUserInfo(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                mWXLoginSuccessData = result;
                                login("", "86", "", 4, result.getString("openId"), CommonUtil.getAndroidID(WXEntryActivity.this));


                            } else {
                                com.facewarrant.fw.util.ToastUtil.showShort(WXEntryActivity.this, data.getString("resultDesc"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });

    }

    private void login(final String phone, final String code, String pwd, int type, String openId, String uuid) {
        final Map<String, String> map = new HashMap<>();
        map.put("countryCode", code);
        map.put("password", pwd);
        map.put("loginType", type + "");
        map.put("openId", openId);
        map.put("uuid", uuid);
        if (!TextUtils.isEmpty((String) SPUtil.get(Constant.REGISTER_ID, ""))) {
            map.put("registrationId", (String) SPUtil.get(Constant.REGISTER_ID, ""));
        } else {
            map.put("registrationId", "1234");
        }
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class).login(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        AccountManager.sUserBean = new UserBean();
                        AccountManager.sUserBean.setId(result.getString("userId"));
                        AccountManager.sUserBean.setPhone(result.getString("phoneNo"));
                        AccountManager.sUserBean.setProvince(result.getString("province"));
                        AccountManager.sUserBean.setProvinceId(result.getString("provinceId"));
                        AccountManager.sUserBean.setCity(result.getString("city"));
                        AccountManager.sUserBean.setCityId(result.getString("cityId"));
                        AccountManager.sUserBean.setHeadUrl(result.getString("headImageUrl"));
                        AccountManager.sUserBean.setCountryCode(result.getString("countryCode"));
                        AccountManager.sUserBean.setLoginType(4);
                        String base64 = CommonUtil.objectToBase64(AccountManager.sUserBean);
                        LogUtil.e(TAG, "" + (base64 == null));
                        SPUtil.put(Constant.USER, base64);
                        startActivity(new Intent(WXEntryActivity.this, MainActivity.class));
                        finish();
                    } else if (data.getInt("resultCode") == 4002) {
                        BindPhoneActivity.open(WXEntryActivity.this, mWXLoginSuccessData.toString(), BindPhoneActivity.TYPE_WX);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable t) {
            }
        });
    }
}

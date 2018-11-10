package com.facewarrant.fw.ui.account;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.UserBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.global.LocalApplication;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.MainActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class BindPhoneActivity extends BaseActivity {
    private static final String TAG = "BindPhoneActivity";
    @BindView(R.id.ll_activity_bind_phone_choose_country)
    LinearLayout mChooseLL;
    @BindView(R.id.et_activity_bind_phone_phone)
    EditText mPhoneET;
    @BindView(R.id.et_activity_bind_phone_code)
    EditText mCodeET;
    @BindView(R.id.tv_activity_bind_phone_next)
    TextView mLoginTV;
    @BindView(R.id.tv_activity_bind_phone_country)
    TextView mCountryTV;
    @BindView(R.id.tv_activity_bind_phone_send_code)
    TextView mSendCodeTV;

    @BindView(R.id.tv_activity_bind_phone_country_code)
    TextView mCountryCodeTV;

    public static final int REQUEST_COUNTRY_CODE = 100;

    /**
     * 第三方登录返回的数据
     */
    private JSONObject mOtherLoginData;

    private int mLoginType;
    public static final int TYPE_WX = 1;
    public static final int TYPE_ALI = 2;
    public static final int TYPE_WEIBO = 3;

    private String mCountryCode = "86";
    private String mCountryID = "94001";
    private String mCountry = "中国大陆";

    private CountDownTimer mTimer;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_bind_phone;
    }

    @Override
    public void initData() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                try {
                    mOtherLoginData = new JSONObject(getIntent().getExtras().getString(Constant.KEYWORD));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mLoginType = getIntent().getExtras().getInt(Constant.TYPE);

            }
        }

        mTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mSendCodeTV.setClickable(false);
                mSendCodeTV.setText(millisUntilFinished / 1000 + " S");
                mSendCodeTV.setBackgroundResource(R.drawable.shape_red_corner_22);
            }

            @Override
            public void onFinish() {
                mSendCodeTV.setClickable(true);
                mSendCodeTV.setText(getString(R.string.resend));
                mSendCodeTV.setBackgroundResource(R.drawable.shape_red_corner_22);
            }
        };

    }

    @Override
    public void initEvent() {
        mLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                String code = mCodeET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请填写手机号");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(mActivity, "请填写验证码");
                } else {
                    checkCode(phone, mCountryCode, 4, code);
                }
            }
        });

        mSendCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请输入手机号码");
                } else {
                    getCode(phone, 4, mCountryCode);
                }
            }
        });

    }

    public static void open(Activity activity, String data, int type) {
        Intent intent = new Intent(activity, BindPhoneActivity.class);
        intent.putExtra(Constant.KEYWORD, data);
        intent.putExtra(Constant.TYPE, type);
        activity.startActivity(intent);
    }

    private void getCode(String phone, int type, String code) {
        final Map<String, String> map = new HashMap<>();
        map.put("phoneNo", phone);
        map.put("smsType", type + "");
        map.put("countryCode", code);
        RequestManager
                .mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getCode(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        mTimer.start();
                    } else {
                        ToastUtil.showShort(mActivity, data.getString("resultDesc"));
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


    private void register(final String url, final String name, final String phoneNo,
                          final String countryCode, final String countryId, final String country,
                          final String provinceId, final String province,
                          final String cityId, final String city, final String password,
                          final String referee, String registerType, final int type, final String openId) {


        final Map<String, String> map = new HashMap<>();
        map.put("headImageUrl", url);
        map.put("name", name);
        map.put("phoneNo", phoneNo);
        map.put("countryCode", countryCode);
        map.put("countryId", countryId);
        map.put("country", country);
        map.put("provinceId", provinceId);
        map.put("province", province);
        map.put("cityId", cityId);
        map.put("city", city);
        map.put("password", password);
        map.put("inviteCode", referee);
        map.put("registerType", registerType);
        map.put("type", type + "");
        map.put("openId", openId);

        if (!TextUtils.isEmpty((String) SPUtil.get(Constant.REGISTER_ID, ""))) {
            map.put("registrationId", (String) SPUtil.get(Constant.REGISTER_ID, ""));
        } else {
            map.put("registrationId", "1234");
        }
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .register(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        AccountManager.sUserBean = new UserBean();
                        AccountManager.sUserBean.setId(result.getString("id"));
                        AccountManager.sUserBean.setCountryCode(result.getString("countryCode"));
                        AccountManager.sUserBean.setPhone(phoneNo);
                        AccountManager.sUserBean.setName(result.getString("name"));
                        AccountManager.sUserBean.setHeadUrl(result.getString("headUrl"));
                        switch (mLoginType) {
                            case TYPE_WEIBO:
                                AccountManager.sUserBean.setLoginType(6);
                                break;
                            case TYPE_ALI:
                                AccountManager.sUserBean.setLoginType(5);
                                break;
                        }
                        ToastUtil.showShort(mActivity, "绑定成功！");
                        String base64 = CommonUtil.objectToBase64(AccountManager.sUserBean);
                        LogUtil.e(TAG, "" + (base64 == null));
                        SPUtil.put(Constant.USER, base64);
                        startActivity(new Intent(mActivity, MainActivity.class));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COUNTRY_CODE && data != null) {
            mCountryCode = data.getStringExtra(Constant.CODE);
            mCountryID = data.getStringExtra(Constant.ID);
            mCountry = data.getStringExtra(Constant.COUNTRY);
            mCountryTV.setText(mCountry);
            mCountryCodeTV.setText("+" + mCountryCode);
        }
    }

    private void checkCode(final String phone, final String countryCode, int type, String code) {
        final Map<String, String> map = new HashMap<>();
        map.put("phoneNo", phone);
        map.put("countryCode", countryCode);
        map.put("smsType", type + "");
        map.put("smsCode", code);

        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .checkCode(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        switch (mLoginType) {
                            case TYPE_WX://微信登录
                                try {
                                    register(mOtherLoginData.getString("headImgUrl")
                                            , mOtherLoginData.getString("nickName")
                                            , phone, mCountryCode, mCountryID, mCountry, "",
                                            mOtherLoginData.getString("province"), "",
                                            mOtherLoginData.getString("city"), "",
                                            "", "3", 1,
                                            mOtherLoginData.getString("openId"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case TYPE_ALI://支付宝登录
                                register(mOtherLoginData.getString("avatar")
                                        , mOtherLoginData.getString("nickName")
                                        , phone, mCountryCode, mCountryID, mCountry, "",
                                        mOtherLoginData.getString("province"), "",
                                        mOtherLoginData.getString("city"), "",
                                        "", "3", 3,
                                        mOtherLoginData.getString("userId"));

                                break;

                            case TYPE_WEIBO:
                                register(mOtherLoginData.getString("profile_image_url")
                                        , mOtherLoginData.getString("screen_name")
                                        , phone, mCountryCode, mCountryID, mCountry, "",
                                        "", "",
                                        "", "",
                                        "", "3", 2,
                                        mOtherLoginData.getString("id"));

                                break;
                        }
                        finish();
                    } else {
                        ToastUtil.showShort(mActivity, data.getString("resultDesc"));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}

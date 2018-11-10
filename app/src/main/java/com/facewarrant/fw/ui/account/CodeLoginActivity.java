package com.facewarrant.fw.ui.account;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.UserBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.MainActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class CodeLoginActivity extends BaseActivity {
    private static final String TAG = "CodeLoginActivity";
    @BindView(R.id.tv_activity_login_country_code)
    TextView mCountryCodeTV;
    @BindView(R.id.tv_activity_login_country)
    TextView mCountryTV;
    @BindView(R.id.ll_activity_login_choose_country)
    LinearLayout mChooseLL;
    @BindView(R.id.tv_activity_login)
    TextView mLoginTV;
    @BindView(R.id.et_activity_login_phone)
    EditText mPhoneET;
    @BindView(R.id.et_activity_login_pwd)
    EditText mCodeET;
    @BindView(R.id.tv_activity_code_login_code)
    TextView mSendCodeTV;
    @BindView(R.id.tv_activity_code_login_login)
    TextView mAccountLoginTV;


    private CountDownTimer mTimer;
    private String mCountryCode = "86";
    private String mCountry = "中国大陆";
    public static final int REQUEST_COUNTRY_CODE = 100;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_code_login;
    }

    @Override
    public void initData() {
        mTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mSendCodeTV.setClickable(false);
                mSendCodeTV.setText(millisUntilFinished / 1000 + " S");
                mSendCodeTV.setBackgroundResource(R.drawable.shape_gray_corner_22);
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
        mChooseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mActivity, ChooseCountryActivity.class), REQUEST_COUNTRY_CODE);
            }
        });

        mSendCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = mPhoneET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请输入手机号码");
                } else {
                    getCode(phone, 1, mCountryCode);
                }
            }
        });
        mLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                String code = mCodeET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请填写手机号码");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(mActivity, "请填写验证码");
                } else {
                    mLoginTV.setClickable(false);
                    checkCode(phone, mCountryCode, 1, code);
                }
            }
        });
        mAccountLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPhoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = mCodeET.getText().toString().trim();
                if (!TextUtils.isEmpty(pwd)) {
                    if (s.length() > 0) {
                        mLoginTV.setClickable(true);
                        mLoginTV.setBackgroundResource(R.drawable.selector_account_btn_bg);
                    } else if (s.length() == 0) {
                        mLoginTV.setClickable(false);
                        mLoginTV.setBackgroundResource(R.drawable.shape_little_red_corner);
                    }
                } else {
                    mLoginTV.setClickable(false);
                    mLoginTV.setBackgroundResource(R.drawable.shape_little_red_corner);
                }

            }
        });
        mCodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = mPhoneET.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    if (s.length() > 0) {
                        mLoginTV.setClickable(true);
                        mLoginTV.setBackgroundResource(R.drawable.selector_account_btn_bg);
                    } else if (s.length() == 0) {
                        mLoginTV.setClickable(false);
                        mLoginTV.setBackgroundResource(R.drawable.shape_little_red_corner);
                    }
                } else {
                    mLoginTV.setClickable(false);
                    mLoginTV.setBackgroundResource(R.drawable.shape_little_red_corner);
                }
            }
        });

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
                        login(phone, 1, "", CommonUtil.getAndroidID(mActivity));
                    } else if (data.getInt("resultCode") == 4003) {
                        ToastUtil.showShort(mActivity, "账号在别处登录，请重新登录！");
                        AccountManager.loginOut(mActivity);
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

    private void login(final String phone, int type, String openId, String uuid) {
        final Map<String, String> map = new HashMap<>();
        map.put("phoneNo", phone);
        map.put("countryCode", mCountryCode);
        map.put("loginType", type + "");
        map.put("openId", openId);
        map.put("uuid", uuid);
        if (!TextUtils.isEmpty((String) SPUtil.get(Constant.REGISTER_ID, ""))) {
            map.put("registrationId", (String) SPUtil.get(Constant.REGISTER_ID, ""));
        } else {
            map.put("registrationId", "1234");
        }
        LogUtil.e(TAG, map.toString());

        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class).login(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                mLoginTV.setClickable(true);
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
                        AccountManager.sUserBean.setLoginType(1);
                        String base64 = CommonUtil.objectToBase64(AccountManager.sUserBean);
                        LogUtil.e(TAG, "" + (base64 == null));
                        SPUtil.put(Constant.USER, base64);
                        startActivity(new Intent(mActivity, MainActivity.class));
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
                mLoginTV.setClickable(true);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_COUNTRY_CODE && data != null) {
            mCountryCode = data.getStringExtra(Constant.CODE);
            mCountry = data.getStringExtra(Constant.COUNTRY);
            mCountryTV.setText(mCountry);
            mCountryCodeTV.setText("+" + mCountryCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
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
}

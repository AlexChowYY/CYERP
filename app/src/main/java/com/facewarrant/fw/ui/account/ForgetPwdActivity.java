package com.facewarrant.fw.ui.account;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
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
public class ForgetPwdActivity extends BaseActivity {
    private static final String TAG = "ForgetPwdActivity";
    @BindView(R.id.et_activity_forget_pwd_phone)
    EditText mPhoneET;
    @BindView(R.id.et_activity_forget_pwd_code)
    EditText mCodeET;
    @BindView(R.id.et_activity_forget_pwd_pwd)
    EditText mPwdET;
    @BindView(R.id.et_activity_forget_pwd_confirm)
    EditText mConfirmET;
    @BindView(R.id.tv_activity_foget_pwd_achieve)
    TextView mAchieveTV;
    @BindView(R.id.tv_activity_forget_pwd_send_code)
    TextView mSendCodeTV;
    @BindView(R.id.ll_activity_login_choose_country)
    LinearLayout mChooseLL;
    @BindView(R.id.tv_activity_login_country_code)
    TextView mCountryCodeTV;
    @BindView(R.id.tv_activity_login_country)
    TextView mCountryTV;
    @BindView(R.id.iv_activity_forget_pwd_confirm_eye)
    ImageView mConfirmIV;
    @BindView(R.id.iv_activity_forget_pwd_eye)
    ImageView mEyeIV;
    private boolean mIsVisible;

    private boolean mConfirmIsVisible;


    private CountDownTimer mTimer;

    private String mCountryCode = "86";
    private String mCountry = "中国大陆";

    public static final int REQUEST_COUNTRY_CODE = 100;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_forget_pwd;
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
        mEyeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsVisible = !mIsVisible;
                if (mIsVisible) {
                    mPwdET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mEyeIV.setImageResource(R.drawable.eye);
                } else {
                    mPwdET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mEyeIV.setImageResource(R.drawable.eye_close);
                }
                mPwdET.setSelection(mPwdET.getText().toString().trim().length());
            }
        });
        mConfirmIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmIsVisible = !mConfirmIsVisible;
                if (mConfirmIsVisible) {
                    mConfirmET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mConfirmIV.setImageResource(R.drawable.eye);
                } else {
                    mConfirmET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mConfirmIV.setImageResource(R.drawable.eye_close);
                }
                mConfirmET.setSelection(mConfirmET.getText().toString().trim().length());
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
    public void initEvent() {
        mSendCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = mPhoneET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请输入手机号码");
                } else {
                    getCode(phone, 2, mCountryCode);
                }
            }
        });

        mAchieveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                String code = mCodeET.getText().toString().trim();
                String pwd = mPwdET.getText().toString().trim();
                String confirm = mConfirmET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请先填写手机号码！");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(mActivity, "请填写验证码！");
                } else if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showShort(mActivity, "请填写密码！");
                } else if (TextUtils.isEmpty(confirm)) {
                    ToastUtil.showShort(mActivity, "请确认密码！");
                } else if (!confirm.equals(pwd)) {
                    ToastUtil.showShort(mActivity, "确认密码错误，请重新填写！");
                } else {
                    checkCode(phone, mCountryCode, 2, code, pwd);
                }

            }
        });
        mChooseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mActivity, ChooseCountryActivity.class), REQUEST_COUNTRY_CODE);
            }
        });


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

    private void checkCode(final String phone, final String countryCode, int type, String code, final String pwd) {
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
                        forgetPwd(phone, countryCode, pwd);
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

    private void forgetPwd(String phone, String countryCode, String pwd) {
        final Map<String, String> map = new HashMap<>();
        map.put("phoneNo", phone);
        map.put("countryCode", countryCode);
        map.put("password", pwd);
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .forgetPwd(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        finish();
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }


}

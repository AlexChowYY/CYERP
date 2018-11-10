package com.facewarrant.fw.ui.account;

import android.content.Intent;
import android.nfc.Tag;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.CommonWebViewActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import retrofit2.http.Query;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    @BindView(R.id.ll_activity_register_choose_country)
    LinearLayout mChooseLL;
    @BindView(R.id.et_activity_register_phone)
    EditText mPhoneET;
    @BindView(R.id.et_activity_register_code)
    EditText mCodeET;
    @BindView(R.id.et_activity_register_pwd)
    EditText mPwdET;
    @BindView(R.id.tv_activity_register_send_code)
    TextView mSendCodeTV;
    @BindView(R.id.iv_activity_register_eye)
    ImageView mEyeIV;
    @BindView(R.id.tv_activity_register_next)
    TextView mNextTV;
    @BindView(R.id.tv_activity_register_country)
    TextView mCountryTV;
    @BindView(R.id.tv_activity_register_country_code)
    TextView mCountryCodeTV;
    @BindView(R.id.iv_activity_register_agree)
    ImageView mAgreeIV;
    @BindView(R.id.tv_activity_protocal)
    TextView mProtocalTV;

    private boolean mIsRead = true;

    private String mCountryCode = "86";
    private String mCountryID = "94001";
    private String mCountry = "中国大陆";


    private boolean mIsVisible;


    private CountDownTimer mTimer;

    public static final int REQUEST_COUNTRY_CODE = 100;

    @Override
    protected int getContentViewId() {

        return R.layout.activity_register;
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
        mProtocalTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonWebViewActivity.open(mActivity,CommonWebViewActivity.TYPE_FW);
            }
        });


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
                    getCode(phone, 0, mCountryCode);
                }
            }
        });
        mAgreeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsRead = !mIsRead;
                if (mIsRead) {
                    mAgreeIV.setImageResource(R.drawable.gou_red);
                    mNextTV.setBackgroundResource(R.drawable.selector_account_btn_bg);
                    mNextTV.setClickable(true);
                } else {
                    mAgreeIV.setImageResource(R.drawable.gou_gray);
                    mNextTV.setBackgroundResource(R.drawable.shape_gray_corner22);
                    mNextTV.setClickable(false);
                }
            }
        });
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
        mNextTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                String smsCode = mCodeET.getText().toString().trim();
                String pwd = mPwdET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请填写手机号码");
                } else if (TextUtils.isEmpty(smsCode)) {
                    ToastUtil.showShort(mActivity, "请填写验证码");
                } else if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showShort(mActivity, "请填写密码");
                } else {
                    if (mIsRead) {
                        checkCode(phone, mCountryCode, 0, smsCode, pwd);
                    } else {
                        ToastUtil.showShort(mActivity, "请同意注册协议");
                    }
                }
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
                        LogUtil.e("mCountryID==" + mCountryID);
                        ConsummateDataActivity.open(mActivity, mCountryCode, mCountryID, mCountry, phone, pwd);
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

    private void getCode(String phone, int type, String code) {
        final Map<String, String> map = new HashMap<>();
        map.put("phoneNo", phone);
        map.put("smsType", type + "");
        map.put("countryCode", code);
        LogUtil.e(TAG, map.toString());
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


}

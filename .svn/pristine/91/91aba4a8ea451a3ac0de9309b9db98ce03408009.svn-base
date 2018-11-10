package com.facewarrant.fw.ui.activity.mine;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.account.ChooseCountryActivity;
import com.facewarrant.fw.ui.account.ConsummateDataActivity;
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
public class ChangePhoneActivity extends BaseActivity {
    private static final String TAG = "ChangePhoneActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.tv_activity_change_phone_achieve)
    TextView mAchieveTV;
    @BindView(R.id.et_activity_change_phone_code)
    EditText mCodeET;
    @BindView(R.id.et_activity_change_phone_phone)
    EditText mPhoneET;
    @BindView(R.id.tv_activity_change_phone_country_code)
    TextView mCountryCodeTV;
    @BindView(R.id.tv_activity_change_phone_send_code)
    TextView mSendCodeTV;
    @BindView(R.id.tv_change_phone_country)
    TextView mCountryTV;


    private CountDownTimer mTimer;

    private String mCountryCode = "86";
    private String mCountryID = "94001";
    private String mCountry = "中国大陆";
    public static final int REQUEST_CODE = 100;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_change_phone;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.change_phone);
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
                mSendCodeTV.setBackgroundResource(R.drawable.shape_strke_coner_13);
            }
        };

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAchieveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                String code = mCodeET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请输入新的电话号码");
                } else if (TextUtils.isEmpty(code)) {
                    ToastUtil.showShort(mActivity, "请输入验证码");
                } else {

                    checkCode(phone, mCountryCode, 3, code);

                }
            }
        });
        mCountryCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mActivity, ChooseCountryActivity.class), REQUEST_CODE);
            }
        });
        mSendCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    getCode(phone, 3, mCountryCode);
                } else {
                    ToastUtil.showShort(mActivity, "请填写手机号！");
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
                        changePhone(phone);
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

    private void changePhone(String phone) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("phoneNo", phone);
        map.put("countryCode", mCountryCode);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .changePhone(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        AccountManager.loginOut(mActivity);
                        ToastUtil.showShort(mActivity, "修改成功！");
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
        if (requestCode == REQUEST_CODE && data != null) {
            mCountryCode = data.getStringExtra(Constant.CODE);
            mCountryID = data.getStringExtra(Constant.ID);
            mCountry = data.getStringExtra(Constant.COUNTRY);
            mCountryTV.setText(mCountry);
            mCountryCodeTV.setText("+" + mCountryCode);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}

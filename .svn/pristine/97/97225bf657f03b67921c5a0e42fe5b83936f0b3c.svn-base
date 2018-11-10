package com.facewarrant.fw.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.common.LogThreadPoolManager;
import com.alipay.sdk.app.AuthTask;
import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.AuthResult;
import com.facewarrant.fw.bean.UserBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.global.LocalApplication;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitManager;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.MainActivity;
import com.facewarrant.fw.ui.activity.warrantIt.TypeCommonActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.OrderInfoUtil2_0;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.FieldMap;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.tv_activity_login)
    TextView mLoginTV;
    @BindView(R.id.tv_activity_login_forget_pwd)
    TextView mForgetTV;
    @BindView(R.id.tv_activity_login_register)
    TextView mRegisterTV;
    @BindView(R.id.ll_activity_login_choose_country)
    LinearLayout mChooseLL;
    @BindView(R.id.et_activity_login_phone)
    EditText mPhoneET;
    @BindView(R.id.et_activity_login_pwd)
    EditText mPwdET;
    @BindView(R.id.iv_activity_login_eye)
    ImageView mEyeIV;
    @BindView(R.id.tv_activity_login_country_code)
    TextView mCountryCodeTV;
    @BindView(R.id.tv_activity_login_country)
    TextView mCountryTV;
    @BindView(R.id.ll_activity_login_wechart)
    LinearLayout mWeChartLL;
    @BindView(R.id.ll_activity_login_weibo)
    LinearLayout mWeiBoLL;
    @BindView(R.id.ll_activity_login_alipay)
    LinearLayout mAliPayLL;
    @BindView(R.id.tv_activity_login_code)
    TextView mCodeLoginTV;

    private String mCountryCode = "86";
    private String mCountry = "中国大陆";

    public static final int REQUEST_COUNTRY_CODE = 100;
    private SsoHandler mSsoHandler;

    private String mWeBoOpenID;
    private String mWeBoUserInfo;

    private int mLoginType;
    public static final int TYPE_PHONE = 0;
    public static final int TYPE_WEI_BO = 1;
    public static final int TYPE_ALI_PAY = 2;
    public static final int TYPE_WEICHART = 3;


    private static final int SDK_AUTH_FLAG = 2;

    /**
     * 支付宝授权信息
     */
    private String mAuthInfo = "";
    private String mAliuserInfo;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(mActivity,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                        getAlipayUserInfo(authResult.getAuthCode());
                        LogUtil.e(TAG, authResult.getAuthCode());
                        mLoginType = TYPE_ALI_PAY;
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(mActivity,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    private boolean mIsVisible;

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        if (AccountManager.sUserBean != null) {
            startActivity(new Intent(mActivity, MainActivity.class));
            finish();
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {

        if (TextUtils.isEmpty((String) SPUtil.get(Constant.KEY_ID, ""))) {
            getOSS();
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        getSimRegion(telephonyManager.getSimCountryIso().toUpperCase());

    }

    @Override
    public void initEvent() {
        mLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneET.getText().toString().trim();
                String pwd = mPwdET.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShort(mActivity, "请填写手机号码");
                } else if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showShort(mActivity, "请填写密码");
                } else {
                    mLoginTV.setClickable(false);
                    mLoginType = TYPE_PHONE;
                    login(phone, mCountryCode, pwd, 2, "", CommonUtil.getAndroidID(mActivity));
                }
            }
        });
        mChooseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mActivity, ChooseCountryActivity.class), REQUEST_COUNTRY_CODE);
            }
        });
        mForgetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, ForgetPwdActivity.class));
            }
        });
        mRegisterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, RegisterActivity.class));
            }
        });
        //微信登录
        mWeChartLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalApplication.api.isWXAppInstalled()) {
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "diandi_wx_login";
                    LocalApplication.api.sendReq(req);
                } else {
                    ToastUtil.showShort(mActivity, "请先安装微信！");
                }
            }
        });
        //微博登陆
        mWeiBoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WbSdk.install(mActivity, new AuthInfo(mActivity, Constant.WEIBO_APP_ID, Constant.REDIRECT_URL,
                        Constant.SCOPE));
                if (mSsoHandler == null) {
                    mSsoHandler = new SsoHandler(mActivity);
                }
                mSsoHandler.authorize(new SelfWbAuthListener());
            }
        });
        mAliPayLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlipayAuthInfo();


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
        mCodeLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, CodeLoginActivity.class));
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
                String pwd = mPwdET.getText().toString().trim();
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
        mPwdET.addTextChangedListener(new TextWatcher() {
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

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (token.isSessionValid()) {
                        LogUtil.e(TAG, token.getToken());
                        mWeBoOpenID = token.getUid();
                        final HashMap<String, String> map = new HashMap<>();
                        map.put("access_token", token.getToken());
                        map.put("uid", token.getUid());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                requestGet(map);
                            }
                        }).start();

                    }
                }
            });
        }

        @Override
        public void cancel() {
            ToastUtil.showShort(mActivity, "取消了微博登陆！");
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            ToastUtil.showShort(mActivity, errorMessage.getErrorMessage());
        }
    }


    private void getOSS() {
        final Map<String, String> map = new HashMap<>();
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getOSSData(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject respone1 = new JSONObject(response);
                    if (respone1.getInt("resultCode") == 200) {
                        JSONObject data = respone1.getJSONObject("result");
//                        SPUtil.put(Constant.KEY_ID, data.getString("OSS_accessKeyId"));
//                        SPUtil.put(Constant.KEY_SECRET, data.getString("OSS_accessKeySecret"));
                        SPUtil.put(Constant.BUCKET_NAME, data.getString("OSS_BUCKET_NAME"));
                        SPUtil.put(Constant.END_POINT, data.getString("OSS_endpoint"));
                        SPUtil.put(Constant.IMAGE_DOMAIN, data.getString("IMAGE_SERVER_DOMAIN"));
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
        map.put("phoneNo", phone);
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
                        switch (mLoginType) {
                            case TYPE_ALI_PAY:
                                AccountManager.sUserBean.setLoginType(5);
                                break;
                            case TYPE_WEI_BO:
                                AccountManager.sUserBean.setLoginType(6);
                                break;
                            case TYPE_PHONE:
                                AccountManager.sUserBean.setLoginType(2);
                                break;
                        }

                        String base64 = CommonUtil.objectToBase64(AccountManager.sUserBean);
                        LogUtil.e(TAG, "" + (base64 == null));
                        SPUtil.put(Constant.USER, base64);
                        startActivity(new Intent(mActivity, MainActivity.class));
                        finish();
                    } else if (data.getInt("resultCode") == 4002) {
                        switch (mLoginType) {
                            case TYPE_WEI_BO:
                                BindPhoneActivity.open(mActivity, mWeBoUserInfo, BindPhoneActivity.TYPE_WEIBO);
                                break;
                            case TYPE_ALI_PAY:
                                BindPhoneActivity.open(mActivity, mAliuserInfo, BindPhoneActivity.TYPE_ALI);
                                break;

                        }

                        ToastUtil.showShort(mActivity, data.getString("resultDesc"));
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
        } else {
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }


    private void getSimRegion(String code) {
        final Map<String, String> map = new HashMap<>();
        if (TextUtils.isEmpty(code)) {
            map.put("region", "CN");
        } else {
            map.put("region", code);
        }
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getCountryByRegion(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }


    private void requestGet(HashMap<String, String> paramsMap) {
        try {
            String baseUrl = "https://api.weibo.com/2/users/show.json?";
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String requestUrl = baseUrl + tempParams.toString();
            // 新建一个URL对象
            URL url = new URL(requestUrl);
            // 打开一个HttpURLConnection连接
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            // 设置连接主机超时时间
            urlConn.setConnectTimeout(5 * 1000);
            //设置从主机读取数据超时
            urlConn.setReadTimeout(5 * 1000);
            // 设置是否使用缓存  默认是true
            urlConn.setUseCaches(true);
            // 设置为Post请求
            urlConn.setRequestMethod("GET");
            //urlConn设置请求头信息
            //设置请求中的媒体类型信息。
            urlConn.setRequestProperty("Content-Type", "application/json");
            //设置客户端与服务连接类型
            urlConn.addRequestProperty("Connection", "Keep-Alive");
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                String result = convertStreamToString(urlConn.getInputStream());
                JSONObject data = new JSONObject(result);
                mWeBoUserInfo = data.toString();
                LogUtil.e(TAG, data.getString("screen_name"));
                mLoginType = TYPE_WEI_BO;
                login("", mCountryCode, "", 6, mWeBoOpenID, CommonUtil.getAndroidID(mActivity));
                LogUtil.e(TAG, "Get方式请求成功，result--->" + result);
            } else {
                LogUtil.e(TAG, "Get方式请求失败");
            }
            // 关闭连接
            urlConn.disconnect();
        } catch (Exception e) {
            LogUtil.e(TAG, e.toString());
        }
    }

    public String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 获取支付宝用户信息
     */
    private void getAlipayUserInfo(String code) {
        final Map<String, String> map = new HashMap<>();
        map.put("authCode", code);
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getAlipayUserInfo(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                mAliuserInfo = result.toString();
                                login("", mCountryCode, "", 5, result.getString("userId"), CommonUtil.android_id(mActivity));
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

    /**
     * 获取支付宝authInfo
     */
    private void getAlipayAuthInfo() {
        final Map<String, String> map = new HashMap<>();

        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getAlipayAuthInfo(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());

                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mAuthInfo = data.getString("result");

                                Runnable authRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        // 构造AuthTask 对象
                                        AuthTask authTask = new AuthTask(mActivity);
                                        // 调用授权接口，获取授权结果
                                        Map<String, String> result = authTask.authV2(mAuthInfo, true);
                                        Message msg = new Message();
                                        msg.what = SDK_AUTH_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };

                                // 必须异步调用
                                Thread authThread = new Thread(authRunnable);
                                authThread.start();


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

package com.facewarrant.fw.ui.activity.warrantIt;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
import com.facewarrant.fw.event.UpdateWarrantListEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.TimeUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FillInformationActivity extends BaseActivity {
    private static final String TAG = "FillInformationActivity";
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.ll_activity_fill_in_brand)
    LinearLayout mBrandLL;
    @BindView(R.id.ll_activity_fill_in_big_type)
    LinearLayout mBigTypeLL;
    @BindView(R.id.ll_activity_fill_in_small_type)
    LinearLayout mSmallTypeLL;
    @BindView(R.id.ll_activity_fill_in_name)
    LinearLayout mNameLL;
    @BindView(R.id.iv_activity_fill_in_delete)
    ImageView mDeleteIV;
    @BindView(R.id.rl_activity_fill_in_pic)
    RelativeLayout mPicRL;
    @BindView(R.id.iv_activity_fill_in_top)
    ImageView mTopIV;
    @BindView(R.id.tv_activity_fill_in_release)
    TextView mReleaseTV;
    @BindView(R.id.et_activity_fill_in_experience)
    EditText mExperienceET;
    @BindView(R.id.et_activity_fill_in_standing)
    EditText mStandingET;
    @BindView(R.id.tv_activity_fill_in_brand)
    TextView mBrandTV;
    @BindView(R.id.tv_activity_fill_in_big_type)
    TextView mBigTypeTV;
    @BindView(R.id.tv_activity_fill_in_small_type)
    TextView mSmallTypeTV;
    @BindView(R.id.tv_activity_fill_in_goods_name)
    TextView mGoodsNameTV;
    @BindView(R.id.tv_activity_fill_in_number)
    TextView mNumberCountTV;

    private String mImagePath;
    private String mVideoPath;
    private String mVideoId;

    private String mBrandId = "";
    private String mBigTypeId = "";
    private String mSmallTypeId = "";
    private String mGoodsId = "";
    private String mGoodName = "";


    public static final int REQUEST_BRAND_CODE = 100;
    public static final int REQUEST_BIG_TYPE_CODE = 200;
    public static final int REQUEST_SMALL_TYPE_CODE = 300;
    public static final int REQUEST_GOOD_CODE = 400;

    private String mKeyId;
    private String mSecretId;
    private String mSecurityToken;
    private String mExpiration;
    private VODSVideoUploadClient vodsVideoUploadClient;

    private OSS oss = null;



    @Override
    protected int getContentViewId() {
        return R.layout.activity_fill_in;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.warrant_it);
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mImagePath = getIntent().getExtras().getString(Constant.IMAGEURL);

                mVideoPath = getIntent().getExtras().getString(Constant.VIDEO_URI);
                LogUtil.e(TAG, "mImagePath==" + mImagePath);
                Glide.with(mActivity).load(mImagePath).into(mTopIV);
            }
        }
        getInfoBeforeRelease();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mTopIV.getLayoutParams();
        layoutParams.width = DisplayUtil.getScreenWidth(mActivity);
        layoutParams.height = DisplayUtil.getScreenWidth(mActivity) * 4 / 3;
        vodsVideoUploadClient = new VODSVideoUploadClientImpl(this.getApplicationContext());
        vodsVideoUploadClient.init();

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBrandLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeCommonActivity.open(mActivity, TypeCommonActivity.TYPE_BRAND, "", "", "", REQUEST_BRAND_CODE);
            }
        });
        mBigTypeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeCommonActivity.open(mActivity, TypeCommonActivity.TYPE_BIG_TYPE, mBrandId, "", "", REQUEST_BIG_TYPE_CODE);
            }
        });
        mSmallTypeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeCommonActivity.open(mActivity, TypeCommonActivity.TYPE_SMALL_TYPE, mBrandId, mBigTypeId, "", REQUEST_SMALL_TYPE_CODE);
            }
        });
        mNameLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeCommonActivity.open(mActivity, TypeCommonActivity.TYPE_NAME, mBrandId, mBigTypeId, mSmallTypeId, REQUEST_GOOD_CODE);
            }
        });
        mDeleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicRL.setVisibility(View.GONE);
            }
        });
        mReleaseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String experience = mExperienceET.getText().toString().trim();
                String standing = mStandingET.getText().toString().trim();

                if (!TextUtils.isEmpty(experience)) {
                    if (!TextUtils.isEmpty(mVideoPath)) {
                        //是拍视频的碑它
                        mReleaseTV.setClickable(false);
                        getVideoSTSToken();
                    } else {
                        getPicStsToken();
                    }
                } else {
                    ToastUtil.showShort(mActivity, "请填写使用体会！");
                }
            }
        });
        if (TextUtils.isEmpty(mExperienceET.getText().toString())) {
            mNumberCountTV.setText("0" + "/200");
        } else {
            mNumberCountTV.setText(mExperienceET.getText().length() + "/20");
        }
        showCharNumber(200);

    }

    private void showCharNumber(final int maxNumber) {
        mExperienceET.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = s.length();
                mNumberCountTV.setText(number + "/" + maxNumber);
                selectionStart = mExperienceET.getSelectionStart();
                selectionEnd = mExperienceET.getSelectionEnd();
                //System.out.println("start="+selectionStart+",end="+selectionEnd);
                if (temp.length() > maxNumber) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    mExperienceET.setText(s);
                    mExperienceET.setSelection(tempSelection);
                }
            }
        });
    }


    public static void open(Activity activity, String imagePath, String videoPath) {
        Intent intent = new Intent(activity, FillInformationActivity.class);
        intent.putExtra(Constant.IMAGEURL, imagePath);
        intent.putExtra(Constant.VIDEO_URI, videoPath);
        activity.startActivity(intent);
    }

    private void getInfoBeforeRelease() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("brandId", mBrandId);
        map.put("btypeId", mBigTypeId);
        map.put("stypeId", mSmallTypeId);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getInfoBeforeRelease(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                if (TextUtils.isEmpty(mBrandId)) {
                                    mBrandId = result.getString("brandId");
                                    mBrandTV.setText(result.getString("brandName"));
                                }
                                if (TextUtils.isEmpty(mBigTypeId)) {
                                    mBigTypeId = result.getString("btypeId");
                                    mBigTypeTV.setText(result.getString("btypeName"));
                                }
                                if (TextUtils.isEmpty(mSmallTypeId)) {
                                    mSmallTypeId = result.getString("stypesId");
                                    mSmallTypeTV.setText(result.getString("stypesName"));
                                }
                                mGoodsNameTV.setText(result.getString("goodName"));
                                mGoodsId = result.getString("goodsId");
                                mStandingET.setText(result.getString("standing"));


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
     *
     */
    private void release(String experience, String standing, String videoID, String imageUrl) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("btypeId", mBigTypeId);
        map.put("stypeId", mSmallTypeId);
        map.put("goodsId", mGoodsId);
        map.put("goodsName", mGoodsNameTV.getText().toString().trim());
        map.put("useDetail", experience);
        if (TextUtils.isEmpty(mVideoPath)) {
            map.put("fwType", 0 + "");
            map.put("fwUrl", SPUtil.get(Constant.IMAGE_DOMAIN, "") + imageUrl);
        } else {
            map.put("fwType", 1 + "");
            map.put("fwUrl", imageUrl);
        }
        map.put("videoUrl", videoID);
        map.put("standing", standing);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .release(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        mReleaseTV.setClickable(true);
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                EventBus.getDefault().post(new UpdateWarrantListEvent());
                                EventBus.getDefault().post(new UpdateUserInfoEvent());
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
                        mReleaseTV.setClickable(true);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == REQUEST_BRAND_CODE) {
                mBrandId = data.getExtras().getString(Constant.ID);
                mBrandTV.setText(data.getExtras().getString(Constant.NAME));

                mBigTypeId = "";
                mSmallTypeId = "";
                mGoodsId = "";
                getInfoBeforeRelease();

            }
            if (requestCode == REQUEST_BIG_TYPE_CODE) {
                mBigTypeId = data.getExtras().getString(Constant.ID);
                mBigTypeTV.setText(data.getExtras().getString(Constant.NAME));
                mSmallTypeId = "";
                mGoodsId = "";
                getInfoBeforeRelease();

            }
            if (requestCode == REQUEST_SMALL_TYPE_CODE) {
                mSmallTypeId = data.getExtras().getString(Constant.ID);
                mSmallTypeTV.setText(data.getExtras().getString(Constant.NAME));
                mGoodsId = "";
                getInfoBeforeRelease();
            }
            if (requestCode == REQUEST_GOOD_CODE) {
                mGoodsId = data.getExtras().getString(Constant.ID);
                mGoodsNameTV.setText(data.getExtras().getString(Constant.NAME));
            }
        }


    }

    private void getVideoSTSToken() {
        final Map<String, String> map = new HashMap<>();
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getSTSToken(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    mKeyId = data.optString("AccessKeyId");
                    mSecretId = data.optString("AccessKeySecret");
                    mExpiration = data.optString("Expiration");
                    mSecurityToken = data.optString("SecurityToken");
                    uploadVideo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable t) {
                mReleaseTV.setClickable(true);

            }
        });
    }

    private void uploadVideo() {
        VodHttpClientConfig vodHttpClientConfig = new VodHttpClientConfig.Builder()
                .setMaxRetryCount(2)//重试次数
                .setConnectionTimeout(15 * 1000)//连接超时
                .setSocketTimeout(15 * 1000)//socket超时
                .build();
        //构建短视频VideoInfo,常见的描述，标题，详情都可以设置
        SvideoInfo svideoInfo = new SvideoInfo();
        svideoInfo.setTitle(new File(mVideoPath).getName());
        svideoInfo.setDesc("");
        svideoInfo.setCateId(1);
        VodSessionCreateInfo vodSessionCreateInfo = new VodSessionCreateInfo.Builder()
                .setImagePath(mImagePath)//图片地址
                .setVideoPath(mVideoPath)//视频地址
                .setAccessKeyId(mKeyId)//临时accessKeyId
                .setAccessKeySecret(mSecretId)//临时accessKeySecret
                .setSecurityToken(mSecurityToken)
                .setExpriedTime(mExpiration)//STStoken过期时间
                //requestID，开发者可以传将获取STS返回的requestID设置也可以不设.
                //是否转码.如开启转码请AppSever务必监听服务端转码成功的通知
                .setSvideoInfo(svideoInfo)//短视频视频信息
                .setVodHttpClientConfig(vodHttpClientConfig)//网络参数
                .build();
        vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
            @Override
            public void onUploadSucceed(String videoId, String imageUrl) {
//上传成功返回视频ID和图片URL.
                LogUtil.d(TAG, "onUploadSucceed" + "videoId:" + videoId + "imageUrl" + imageUrl);
                String experience = mExperienceET.getText().toString().trim();
                String standing = mStandingET.getText().toString().trim();
                if (TextUtils.isEmpty(standing)) {
                    ToastUtil.showShort(mActivity, "请填写身份信息！");
                } else if (TextUtils.isEmpty(experience)) {
                    ToastUtil.showShort(mActivity, "请填写使用体会！");
                } else {
                    release(experience, standing, videoId, imageUrl);

                }
            }

            @Override
            public void onUploadFailed(String code, String message) {
                //上传失败返回错误码和message.错误码有详细的错误信息请开发者仔细阅读
                LogUtil.d(TAG, "onUploadFailed" + "code" + code + "message" + message);
                mReleaseTV.setClickable(true);
            }

            @Override
            public void onUploadProgress(long uploadedSize, long totalSize) {
                //上传的进度回调,非UI线程
                LogUtil.d(TAG, "onUploadProgress" + uploadedSize * 100 / totalSize);

            }

            @Override
            public void onSTSTokenExpried() {
                Log.d(TAG, "onSTSTokenExpried");
                //STS token过期之后刷新STStoken，如正在上传将会断点续传
                vodsVideoUploadClient.refreshSTSToken(mKeyId, mSecretId, mSecurityToken, mExpiration);
            }

            @Override
            public void onUploadRetry(String code, String message) {
                //上传重试的提醒
                Log.d(TAG, "onUploadRetry" + "code" + code + "message" + message);
            }

            @Override
            public void onUploadRetryResume() {
                //上传重试成功的回调.告知用户重试成功
                Log.d(TAG, "onUploadRetryResume");
            }
        });


    }


    /**
     * 上传OSS
     *
     * @param keyId
     * @param secretId
     * @param accessToken
     */
    private void initOSS(String keyId, String secretId, String accessToken) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(keyId, secretId, accessToken);
        oss = new OSSClient(mContext, (String) SPUtil.get(Constant.END_POINT, ""), credentialProvider);
    }

    /**
     * 获取上传图片的token
     */
    private void getPicStsToken() {
        final Map<String, String> map = new HashMap<>();
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getPicStsToken(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("StatusCode") == 200) {
                                initOSS(data.getString("AccessKeyId"),
                                        data.getString("AccessKeySecret"),
                                        data.getString("SecurityToken"));
                                uploadIMG();
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
     * 上传图片
     */
    private void uploadIMG() {
        PutObjectRequest put = new PutObjectRequest((String) SPUtil.get(Constant.BUCKET_NAME, ""),
                "1" + AccountManager.sUserBean.getPhone() + TimeUtil.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".jpg", mImagePath);
        // 文件元信息的设置是可选的
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream"); // 设置content-type
        try {
            metadata.setContentMD5(BinaryUtil.calculateBase64Md5(mImagePath)); // 校验MD5
        } catch (IOException e) {
            e.printStackTrace();
        }
        put.setMetadata(metadata);
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long currentSize, long totalSize) {
                Log.e("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
            }
        });
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                LogUtil.e("PutObject=========", "UploadSuccess");
                LogUtil.d("ETag==========", putObjectResult.getETag());
                LogUtil.d("RequestId==========", putObjectResult.getRequestId());
                String imageUrl = putObjectRequest.getObjectKey();
                String experience = mExperienceET.getText().toString().trim();
                String standing = mStandingET.getText().toString().trim();
                if (!TextUtils.isEmpty(experience) && !TextUtils.isEmpty(standing)) {
                    release(experience, standing, "", imageUrl);
                } else {
                    ToastUtil.showShort(mActivity, "请完善身份信息和使用体会！");
                }


            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                if (e != null) {
                    // 本地异常如网络异常等
                    e.printStackTrace();
                    Toast.makeText(mContext, "oss网络异常", Toast.LENGTH_LONG).show();
                }
                if (e1 != null) {
                    // 服务异常
                    LogUtil.e("ErrorCode", e1.getErrorCode());
                    LogUtil.e("RequestId", e1.getRequestId());
                    LogUtil.e("HostId", e1.getHostId());
                    LogUtil.e("RawMessage", e1.getRawMessage());
                }

            }
        });
    }


}

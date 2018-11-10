package com.facewarrant.fw.ui.activity.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.CleanMessageUtil;
import com.facewarrant.fw.util.FileUtilcll;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.TimeUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.makeramen.roundedimageview.RoundedImageView;
//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.config.PictureConfig;
//import com.luck.picture.lib.config.PictureMimeType;
//import com.luck.picture.lib.entity.LocalMedia;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class SettingActivity extends BaseActivity {
    private static final String TAG = "SettingActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.rl_activity_setting_account)
    RelativeLayout mAccountRL;
    @BindView(R.id.rl_activity_setting_change_head)
    RelativeLayout mHeadRL;
    @BindView(R.id.rl_activity_setting_feedback)
    RelativeLayout mFeedbackRL;
    @BindView(R.id.riv_activity_setting_head)
    RoundedImageView mHeadRIV;
    @BindView(R.id.tv_activity_setting_login_out)
    TextView mLoginOutTV;
    @BindView(R.id.rl_fragment_clear_cache)
    RelativeLayout mCacheRL;

    @BindView(R.id.rl_activity_setting_version)
    RelativeLayout mVersionRL;

    private PopupWindow mLoginOutPopupWindow;
    private PopupWindow mVersionPopupWindow;

    private static final int REQUEST_ALBUM = 100;
    public static final int PERMISSION_STORAGE = 1000;
    private static final int REQUEST_PICKER_AND_CROP = 200;


    //private List<LocalMedia> selectList = new ArrayList<>();
    private String imageUrl;
    private String mCutPath;
    private OSS oss = null;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.setting);
        getUserInfo();
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAccountRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mActivity, AccountAndSafeActivity.class));

            }
        });
        mHeadRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                    } else {
                        openPicture();
                    }
                } else {
                    openPicture();
                }
            }
        });
        mFeedbackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, FeedbackActivity.class));
            }
        });
        mLoginOutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        mCacheRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CleanMessageUtil.clearAllCache(getApplicationContext());
                ToastUtil.showShort(mActivity, "清除缓存成功！");
            }
        });
        mVersionRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVersionRL.setClickable(false);
                getVersionUpdate();
            }
        });
    }

    private void openPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_ALBUM);

    }


    private void changeHeadUrl() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("headImageUrl", SPUtil.get(Constant.IMAGE_DOMAIN, "") + imageUrl);
        map.put("loginType", AccountManager.sUserBean.getLoginType() + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .changeHeadUrl(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        ToastUtil.showShort(mActivity, data.getString("resultDesc"));
                        getUserInfo();
                        EventBus.getDefault().post(new UpdateUserInfoEvent());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPicture();
                } else {
                    ToastUtil.showShort(mActivity, "未开启手机读写权限，请去设置打开！");
                }
                break;


        }
    }

    private void getVersionUpdate() {
        final Map<String, String> map = new HashMap<>();
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .versionUpdate(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        mVersionRL.setClickable(true);
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                showVersionPopupWindow();
                            } else {
                                ToastUtil.showShort(mActivity, data.getString("resultDesc"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        mVersionRL.setClickable(true);

                    }
                });
    }

    private void getUserInfo() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        switch (AccountManager.sUserBean.getLoginType()) {
            case 1:
            case 2:
                map.put("loginType", 1 + "");
                break;
            default:
                map.put("loginType", AccountManager.sUserBean.getLoginType() + "");
                break;

        }

        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyUserInfo(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        Glide.with(mActivity)
                                .load(result.getString("headUrl"))
                                .into(mHeadRIV);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALBUM && data != null) {//调用系统相册
            Uri selectedImage = data.getData();
            startPhotoZoom(selectedImage);
        }
        if (requestCode == REQUEST_PICKER_AND_CROP && data != null) {
            mCutPath = setPicToView(data);
            getPicStsToken();
        }


    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_PICKER_AND_CROP);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private String setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        String path = "";
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //图片路径
            path = FileUtilcll.saveFile(mActivity, "temphead.jpg", photo);

        }
        return path;
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
     * 上传图片
     */
    private void uploadIMG() {
        PutObjectRequest put = new PutObjectRequest((String) SPUtil.get(Constant.BUCKET_NAME, ""),
                "1" + AccountManager.sUserBean.getPhone() + TimeUtil.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".jpg", mCutPath);
        // 文件元信息的设置是可选的
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/octet-stream"); // 设置content-type
        try {
            metadata.setContentMD5(BinaryUtil.calculateBase64Md5(mCutPath)); // 校验MD5
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
                imageUrl = putObjectRequest.getObjectKey();
                changeHeadUrl();


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

    private void showPopupWindow() {
        if (mLoginOutPopupWindow != null) {
            if (mLoginOutPopupWindow.isShowing()) {
                mLoginOutPopupWindow.dismiss();
            }
        } else {
            View view = mInflater.inflate(R.layout.pop_login_out_tips, null);
            TextView cancelTV = view.findViewById(R.id.tv_pop_login_out_cancel);
            TextView sureTV = view.findViewById(R.id.tv_pop_login_out_sure);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLoginOutPopupWindow.isShowing()) {
                        mLoginOutPopupWindow.dismiss();
                    }
                }
            });
            sureTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginOut();

                }
            });
            mLoginOutPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLoginOutPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    private void showVersionPopupWindow() {
        if (mVersionPopupWindow != null) {
            if (mVersionPopupWindow.isShowing()) {
                mVersionPopupWindow.dismiss();
            }
        } else {
            View view = mInflater.inflate(R.layout.pop_version_tips, null);
            TextView cancelTV = view.findViewById(R.id.cancel);
            TextView sureTV = view.findViewById(R.id.tv_sure);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVersionPopupWindow.isShowing()) {
                        mVersionPopupWindow.dismiss();
                    }
                }
            });
            sureTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showShort(mActivity, "");

                }
            });
            mVersionPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mVersionPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    private void loginOut() {

        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .loginOut(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        ToastUtil.showShort(mActivity, "退出成功！");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable t) {

            }
        });
        AccountManager.loginOut(mActivity);
    }


}

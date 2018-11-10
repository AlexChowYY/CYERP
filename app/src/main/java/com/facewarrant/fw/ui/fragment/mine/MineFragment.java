package com.facewarrant.fw.ui.fragment.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.event.UpdateMesageEvent;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.CommonWebViewActivity;
import com.facewarrant.fw.ui.activity.MainActivity;
import com.facewarrant.fw.ui.activity.information.InformationActivity;
import com.facewarrant.fw.ui.activity.mine.MyAnswerActivity;
import com.facewarrant.fw.ui.activity.mine.MyFollowActivity;
import com.facewarrant.fw.ui.activity.mine.MyQuestionActivity;
import com.facewarrant.fw.ui.activity.mine.MyWarrantActivity;
import com.facewarrant.fw.ui.activity.mine.MyWishActivity;
import com.facewarrant.fw.ui.activity.mine.SettingActivity;
import com.facewarrant.fw.ui.activity.mine.faceGroup.ManagerActivity;
import com.facewarrant.fw.ui.activity.mine.faceValue.FaceValueActivity;
import com.facewarrant.fw.ui.activity.mine.integral.MyIntegralActivity;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.FileStorage;
import com.facewarrant.fw.util.FileUtilcll;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.TimeUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
public class MineFragment extends BaseFragment {
    private static final String TAG = "MineFragment";
    @BindView(R.id.ll_fragment_mine_message)
    LinearLayout mMessageLL;
    @BindView(R.id.ll_fragment_mine_question_me)
    LinearLayout mMyQuestionLL;
    @BindView(R.id.ll_fragment_mine_setting)
    LinearLayout mSettingLL;
    @BindView(R.id.ll_fragment_mine_follow)
    LinearLayout mFollowLL;
    @BindView(R.id.ll_fragment_mine_warrant)
    LinearLayout mWarrantLL;
    @BindView(R.id.ll_fragment_mine_wish)
    LinearLayout mWishLL;
    @BindView(R.id.ll_fragment_mine_face_group)
    LinearLayout mGroupLL;
    @BindView(R.id.tv_fragment_mine_name)
    TextView mNameTV;
    @BindView(R.id.tv_fragment_mine_fans)
    TextView mFansTV;
    @BindView(R.id.tv_fragment_mine_faces)
    TextView mFaceTV;
    @BindView(R.id.tv_fragment_mine_follows)
    TextView mFollowsTV;
    @BindView(R.id.tv_fragment_mine_warrant_it)
    TextView mWarrantItTV;
    @BindView(R.id.tv_fragment_mine_wish)
    TextView mWishTV;
    @BindView(R.id.riv_fragment_mine_head)
    RoundedImageView mHeadRIV;
    @BindView(R.id.ll_fragment_mine_my_question)
    LinearLayout mQuestionLL;
    @BindView(R.id.ll_mine_fragment_face_value)
    LinearLayout mFaceValueLL;
    @BindView(R.id.ll_fragment_mine_integral)
    LinearLayout mIntegralLL;
    @BindView(R.id.iv_fragment_mine_new)
    ImageView mNewIV;
    @BindView(R.id.tv_fragment_mine_sign)
    TextView mSignTV;
    @BindView(R.id.ll_fragment_mine_sign)
    LinearLayout mSignLL;
    @BindView(R.id.ll_fragment_mine_invite)
    LinearLayout mInviteLL;
    @BindView(R.id.ll_fragment_mine_about_us)
    LinearLayout mAboutUsLL;

    private Uri mTakePhotoUri;
    private String mCutPath;
    private static final int REQUEST_ALBUM = 100;
    private static final int REQUEST_PICKER_AND_CROP = 200;
    private static final int REQUEST_TAKE_PHOTO = 300;
    private static final int REQUEST_CROP = 400;


    private int mSignStatus;
    public static final int NONE_SIGN = 0;
    public static final int SIGN = 1;

    private PopupWindow mPopupWindow;

    private PopupWindow mPictrueWindow;


    public static final int PERMISSION_STORAGE = 1000;
    public static final int PERMISSION_CAMRA = 2000;

    private OSS oss = null;
    private String imageUrl;


    private String mFaceValue;


    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);

        getUserInfo();


    }

    @Override
    public void initEvent() {
        mMessageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, InformationActivity.class));
            }
        });
        mMyQuestionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mSettingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, SettingActivity.class));
            }
        });
        mFollowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MyFollowActivity.class));
            }
        });
        mWarrantLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MyWarrantActivity.class));
            }
        });
        mWishLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MyWishActivity.class));
            }
        });
        mGroupLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, ManagerActivity.class));
            }
        });
        mQuestionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MyAnswerActivity.class));
            }
        });
        mMyQuestionLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MyQuestionActivity.class));
            }
        });
        mFaceValueLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceValueActivity.open(mActivity, mFaceValue);
            }
        });
        mIntegralLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, MyIntegralActivity.class));
            }
        });
        mSignLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mSignStatus) {
                    case NONE_SIGN:
                        mSignLL.setClickable(false);
                        sign();
                        break;
                }

            }
        });
        mInviteLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonWebViewActivity.open(mActivity, CommonWebViewActivity.TYPE_INVITE_FRIEND);
            }
        });
        mAboutUsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonWebViewActivity.open(mActivity, CommonWebViewActivity.TYPE_ABOUT_US);
            }
        });

        mHeadRIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictrueWindow();


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
                        mNameTV.setText(result.getString("name"));
                        mFansTV.setText(result.getString("fansCount"));
                        mFaceTV.setText(result.getString("favouriteCount"));
                        mFollowsTV.setText(result.getString("attentionCount"));
                        mWarrantItTV.setText(result.getString("releaseGoodsCount"));
                        mWishTV.setText(result.getString("collectionCount"));
                        AccountManager.sUserBean.setName(result.getString("name"));
                        AccountManager.sUserBean.setInteragel(result.getString("pointsRegister"));
                        mFaceValue = result.getString("balance");
                        mSignStatus = result.getInt("isSignOn");
                        switch (mSignStatus) {
                            case NONE_SIGN:
                                mSignTV.setText(R.string.sign);
                                mSignLL.setBackgroundResource(R.drawable.shape_sign);
                                break;
                            case SIGN:
                                mSignTV.setText(R.string.signed);
                                mSignLL.setBackgroundResource(R.drawable.shape_sign_gray);
                                break;
                        }
                        Glide.with(mActivity)
                                .load(result.getString("headUrl"))
                                .into(mHeadRIV);
                        if (result.getInt("messageCount") > 0) {
                            mNewIV.setVisibility(View.VISIBLE);
                        } else {
                            mNewIV.setVisibility(View.GONE);
                        }
                    } else if (data.getInt("resultCode") == 4003) {
                        ToastUtil.showShort(mActivity, "账号在别处登录，请重新登录！");
                        AccountManager.loginOut(mActivity);
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


    private void sign() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .sign(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                ToastUtil.showShort(mActivity, "签到成功！");
                                mSignLL.setBackgroundResource(R.drawable.shape_sign_gray);
                                mSignTV.setText(R.string.signed);
                                mSignStatus = SIGN;
                                JSONObject result = data.getJSONObject("result");
                                showPopupWindow(result.getString("pointsBase"));
                            } else {
                                ToastUtil.showShort(mActivity, data.getString("resultDesc"));
                            }
                            mSignLL.setClickable(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateUserInfoEvent updateUserInfoEvent) {

        getUserInfo();
    }

    private void showPopupWindow(String point) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            View view = mInflater.inflate(R.layout.pop_sign_success, null);
            TextView pointTV = view.findViewById(R.id.tv_pop_sign_success_point);
            pointTV.setText("+" + point);

            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
            ((MainActivity) mActivity).getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPopupWindow.dismiss();
                }
            }, 1000);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uodateInfoRead(UpdateMesageEvent updateMesageEvent) {
        getUserInfo();
    }


    private void showPictrueWindow() {
        if (mPictrueWindow != null && mPictrueWindow.isShowing()) {
            mPictrueWindow.dismiss();
        } else {
            View view = mInflater.inflate(R.layout.pop_pictrue_select, null);
            TextView takePhotoTV = view.findViewById(R.id.tv_pop_pictrue_select_take);
            TextView libraryTV = view.findViewById(R.id.tv_pop_pictrue_select_library);
            TextView cancelTV = view.findViewById(R.id.tv_pop_pictrue_select_cancel);
            takePhotoTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CAMRA);
                        } else {
                            openCamera();
                        }
                    } else {

                        openCamera();


                    }

                }
            });
            libraryTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                        } else {
                            openPictrueLibrary();
                        }
                    } else {
                        openPictrueLibrary();
                    }
                }
            });
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPictrueWindow.dismiss();
                }
            });
            mPictrueWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPictrueWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);

        }
    }


    /**
     * 打开系统相机
     */
    private void openCamera() {
        if (mPictrueWindow != null && mPictrueWindow.isShowing()) {
            mPictrueWindow.dismiss();
        }
        File file = new FileStorage().createIconFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTakePhotoUri = FileProvider.getUriForFile(mActivity, "com.facewarrant.fw.fileprovider", file);//通过FileProvider创建一个content类型的Uri
        } else {
            mTakePhotoUri = Uri.fromFile(file);
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }

        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }


    private void openPictrueLibrary() {
        
        if (mPictrueWindow != null && mPictrueWindow.isShowing()) {
            mPictrueWindow.dismiss();
        }
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_ALBUM);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPictrueLibrary();
                } else {
                    ToastUtil.showShort(mActivity, "未开启手机读写权限，请去设置打开！");
                }
                break;

            case PERMISSION_CAMRA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ToastUtil.showShort(mActivity, "未开启手机读写权限，请去设置打开！");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALBUM && data != null) {
            Uri selectedImage = data.getData();
            startPhotoZoom(selectedImage);//选择照片回调成功后进行裁剪
        }
        if (requestCode == REQUEST_PICKER_AND_CROP && data != null) {
            mCutPath = setPicToView(data);
            getPicStsToken();

        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            cropPhoto();//裁剪拍照后的图片
        }
        if (requestCode == REQUEST_CROP && data != null) {
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
     * 裁剪 拍照后图片
     */
    private void cropPhoto() {
        File file = new FileStorage().createCropFile();
        Uri outputUri = Uri.fromFile(file);//缩略图保存地址
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(mTakePhotoUri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CROP);
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
        oss = new OSSClient(mActivity, (String) SPUtil.get(Constant.END_POINT, ""), credentialProvider);
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
                    Toast.makeText(mActivity, "oss网络异常", Toast.LENGTH_LONG).show();
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
}

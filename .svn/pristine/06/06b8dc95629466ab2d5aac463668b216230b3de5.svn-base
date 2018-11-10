package com.facewarrant.fw.ui.account;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.BinaryUtil;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.bean.UserBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.MainActivity;
import com.facewarrant.fw.ui.activity.warrantIt.FillInformationActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.FileStorage;
import com.facewarrant.fw.util.FileUtilcll;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.TimeUtil;
import com.facewarrant.fw.util.ToastUtil;
//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.config.PictureConfig;
//import com.luck.picture.lib.config.PictureMimeType;
//import com.luck.picture.lib.entity.LocalMedia;
//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.config.PictureConfig;
//import com.luck.picture.lib.config.PictureMimeType;
//import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class ConsummateDataActivity extends BaseActivity {
    private static final String TAG = "ConsummateDataActivity";
    private static final int REQUEST_ALBUM = 100;
    private static final int REQUEST_PICKER_AND_CROP = 200;
    private static final int REQUEST_TAKE_PHOTO = 300;
    private static final int REQUEST_CROP = 400;

    @BindView(R.id.ll_activity_consummate_data_province)
    LinearLayout mProvinceLL;
    @BindView(R.id.ll_activity_consummate_data_city)
    LinearLayout mCityLL;
    @BindView(R.id.tv_activity_consummate_data_province)
    TextView mProvinceTV;
    @BindView(R.id.tv_activity_consummate_data_city)
    TextView mCityTV;
    @BindView(R.id.riv_activity_consummate_date_head)
    RoundedImageView mHeadRIV;
    @BindView(R.id.tv_activity_consummate_data_register)
    TextView mRegisterTV;
    @BindView(R.id.et_consummate_date_name)
    EditText mNameET;
    @BindView(R.id.et_activity_consummate_data_invite)
    EditText mInviteET;

    private String mCode;
    private String mId;
    private String mCountry;
    private String mPhone;
    private String mPwd;

    private ArrayList<ClassifyBean> mProvinceList = new ArrayList<>();
    private ArrayList<String> mProvinceStringList = new ArrayList<>();

    private List<ClassifyBean> mCityList = new ArrayList<>();
    private ArrayList<String> mCityStringList = new ArrayList<>();


    private ClassifyBean mSelectProvince;
    private ClassifyBean mSelectCity;

    private boolean mCityFinish = false;

    private String mCutPath;

    private OSS oss = null;


    //     private List<LocalMedia> selectList = new ArrayList<>();
    private String imageUrl;
    private OptionsPickerView mProvinceOPV;

    private OptionsPickerView mCityOPV;

    private Uri mTakePhotoUri;


    private PopupWindow mPopupWindow;

    public static final int PERMISSION_STORAGE = 1000;
    public static final int PERMISSION_CAMRA = 2000;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_consummate_data;
    }

    @Override
    public void initData() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mCode = getIntent().getExtras().getString(Constant.CODE);
                mId = getIntent().getExtras().getString(Constant.ID);
                mCountry = getIntent().getExtras().getString(Constant.COUNTRY);
                mPhone = getIntent().getExtras().getString(Constant.PHONE);
                mPwd = getIntent().getExtras().getString(Constant.PWD);
            }
        }
        if (mCode.equals("86")) {

            getProvince();
        } else {
            mProvinceLL.setVisibility(View.GONE);
            getforeignCities();
        }
        //getCities();

    }

    @Override
    public void initEvent() {
        mProvinceLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProvinceOPV();
            }
        });
        mCityLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCityFinish) {
                    showCityOPV();
                } else {
                    ToastUtil.showLong(mActivity, "请先选择城市");
                }
            }
        });
        mHeadRIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();
            }
        });
        mRegisterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameET.getText().toString().trim();
                String province = mProvinceTV.getText().toString().trim();
                String city = mCityTV.getText().toString().trim();
                String invite = mInviteET.getText().toString().trim();
                if (TextUtils.isEmpty(mCutPath)) {
                    ToastUtil.showShort(mActivity, "请选择照片作为头像");
                } else if (TextUtils.isEmpty(name)) {
                    ToastUtil.showShort(mActivity, "请填写姓名");
                } else if (TextUtils.isEmpty(province)) {
                    ToastUtil.showShort(mActivity, "请选择省份");
                } else if (mCode.equals("86") && TextUtils.isEmpty(city)) {
                    ToastUtil.showShort(mActivity, "请选择城市");
                } else {
                    getPicStsToken(name, invite);
                }

            }
        });
    }


    private void getforeignCities() {
        final Map<String, String> map = new HashMap<>();
        map.put("countryId", mId);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getCountriesCities(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mCityFinish = true;
                                JSONArray result = data.getJSONArray("result");
                                mCityList.clear();
                                mCityStringList.clear();
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject resultItem = result.getJSONObject(i);
                                    ClassifyBean classifyBean = new ClassifyBean();
                                    classifyBean.setId(resultItem.getString("id"));
                                    classifyBean.setName(resultItem.getString("name"));
                                    mCityList.add(classifyBean);
                                    mCityStringList.add(resultItem.getString("name"));
                                }
                                mCityTV.setText(mCityList.get(0).getName());
                                mSelectCity = mCityList.get(0);
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

    public static void open(Activity activity, String code, String id, String Country, String phone, String pwd) {
        Intent intent = new Intent(activity, ConsummateDataActivity.class);
        intent.putExtra(Constant.CODE, code);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.COUNTRY, Country);
        intent.putExtra(Constant.PHONE, phone);
        intent.putExtra(Constant.PWD, pwd);
        activity.startActivity(intent);
    }

    private void getProvince() {
        final Map<String, String> map = new HashMap<>();
        RequestManager
                .mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getProvince(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONArray result = data.getJSONArray("result");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject resultItem = result.getJSONObject(i);
                            ClassifyBean classifyBean = new ClassifyBean();
                            classifyBean.setId(resultItem.getString("id"));
                            classifyBean.setName(resultItem.getString("name"));
                            mProvinceList.add(classifyBean);
                            mProvinceStringList.add(resultItem.getString("name"));
                        }
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

    private void getCities(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("provinceId", id);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getCities(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {

                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mCityFinish = true;
                                JSONArray result = data.getJSONArray("result");
                                mCityList.clear();
                                mCityStringList.clear();
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject resultItem = result.getJSONObject(i);
                                    ClassifyBean classifyBean = new ClassifyBean();
                                    classifyBean.setId(resultItem.getString("id"));
                                    classifyBean.setName(resultItem.getString("name"));
                                    mCityList.add(classifyBean);
                                    mCityStringList.add(resultItem.getString("name"));
                                }
                                mCityTV.setText(mCityList.get(0).getName());
                                mSelectCity = mCityList.get(0);
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

    private void showProvinceOPV() {
        if (mProvinceOPV == null) {
            mProvinceOPV = new OptionsPickerView(this);
            mProvinceOPV.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    mSelectProvince = mProvinceList.get(options1);
                    mProvinceTV.setText(mSelectProvince.getName());
                    getCities(mProvinceList.get(options1).getId());
                }
            });

        }
        mProvinceOPV.setPicker(mProvinceStringList);
        mProvinceOPV.show();
        mProvinceOPV.setCyclic(false, false, false);
    }

    private void showCityOPV() {
        if (mCityOPV == null) {
            mCityOPV = new OptionsPickerView(this);
            mCityOPV.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    mSelectCity = mCityList.get(options1);
                    mCityTV.setText(mSelectCity.getName());
                }
            });
        }
        mCityOPV.setPicker(mCityStringList);
        mCityOPV.show();
        mCityOPV.setCyclic(false, false, false);
    }

    private void openPicture() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALBUM && data != null) {
            Uri selectedImage = data.getData();
            startPhotoZoom(selectedImage);//选择照片回调成功后进行裁剪
        }
        if (requestCode == REQUEST_PICKER_AND_CROP && data != null) {
            mCutPath = setPicToView(data);
            Glide.with(mActivity)
                    .load(mCutPath)
                    .into(mHeadRIV);
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            cropPhoto();//裁剪拍照后的图片
        }
        if (requestCode == REQUEST_CROP && data != null) {
            mCutPath = setPicToView(data);
            Glide.with(mActivity).load(mCutPath).into(mHeadRIV);
        }

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


    private void register(final String url, final String name, final String phoneNo,
                          final String countryCode, final String countryId, final String country,
                          final String provinceId, final String province,
                          final String cityId, final String city, final String password,
                          final String referee, String registerType, final int type, final String openId) {


        final Map<String, String> map = new HashMap<>();
        map.put("headImageUrl", SPUtil.get(Constant.IMAGE_DOMAIN, "") + imageUrl);
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
        map.put("registerType", "0");
        map.put("type", type + "");
        map.put("openId", openId);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .register(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
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

    /**
     * 上传图片
     *
     * @param name
     * @param invite
     */
    private void uploadIMG(final String name, final String invite) {
        PutObjectRequest put = new PutObjectRequest((String) SPUtil.get(Constant.BUCKET_NAME, ""),
                "1" + mPhone + TimeUtil.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss") + ".jpg", mCutPath);
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
                register(imageUrl, name, mPhone, mCode, mId, mCountry,
                        mSelectProvince.getId(), mSelectProvince.getName(),
                        mSelectCity.getId(), mSelectCity.getName(),
                        mPwd, invite, "3", 0, "");
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
     *
     * @param name
     * @param invite
     */
    private void getPicStsToken(final String name, final String invite) {
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
                                uploadIMG(name, invite);
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

    private void showPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
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
                            openPicture();
                        }
                    } else {
                        openPicture();
                    }
                }
            });
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }
    }


    /**
     * 打开系统相机
     */
    private void openCamera() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
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

            case PERMISSION_CAMRA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ToastUtil.showShort(mActivity, "未开启手机读写权限，请去设置打开！");
                }
                break;
        }
    }


}

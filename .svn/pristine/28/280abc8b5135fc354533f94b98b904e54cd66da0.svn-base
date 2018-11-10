package com.facewarrant.fw.ui.personal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.bean.FaceGroupBean;
import com.facewarrant.fw.bean.GoodsBean;
import com.facewarrant.fw.event.UpdateAiTeList;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.event.UpdateWarrantGoodsEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.mine.WarrantDetailActivity;
import com.facewarrant.fw.ui.activity.warrantIt.FillInformationActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.FileStorage;
import com.facewarrant.fw.util.FileUtilcll;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.util.TimeUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.view.SpaceItemDecoration;
//import com.luck.picture.lib.PictureSelector;
//import com.luck.picture.lib.config.PictureConfig;
//import com.luck.picture.lib.config.PictureMimeType;
//import com.luck.picture.lib.entity.LocalMedia;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
import butterknife.ButterKnife;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class PersonalActivity extends AppCompatActivity {
    private static final String TAG = "PersonalActivity";
    @BindView(R.id.rv_activity_personal)
    RecyclerView mRV;
    @BindView(R.id.tv_activity_personal_latest)
    TextView mLatestTV;
    @BindView(R.id.tv_activity_personal_hot)
    TextView mHotTV;
    @BindView(R.id.iv_activity_personal_back)
    ImageView mBackIV;
    @BindView(R.id.tv_activity_personal_all_brand)
    TextView mAllBrandTV;
    @BindView(R.id.iv_activity_personal_bg)
    ImageView mBgIV;
    @BindView(R.id.tv_activity_personal_name)
    TextView mNameTV;
    @BindView(R.id.tv_activity_personal_favouriteCount)
    TextView mFavouriteCountTV;
    @BindView(R.id.tv_activity_personal_attention)
    TextView mAttentionTV;
    @BindView(R.id.tv_activity_personal_fans)
    TextView mFansTV;
    @BindView(R.id.tv_activity_personal_warrant)
    TextView mWarrantTV;
    @BindView(R.id.tv_activity_personal_introduce)
    TextView mIntroduceTV;
    @BindView(R.id.riv_activity_personal)
    RoundedImageView mHeadRIV;
    @BindView(R.id.tv_activity_personal_follow)
    TextView mFollowTV;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;

    private String mKeywords = "";


    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    private List<FaceGroupBean> mPopList = new ArrayList<>();
    private List<FaceGroupBean> mSelectList = new ArrayList<>();

    private int mAttention;

    private Drawable mAdd;

    private int mType;
    public static final int TYPE_NEWEST = 0;
    public static final int TYPE_HOT = 1;


    //private List<LocalMedia> selectList = new ArrayList<>();
    private OSS oss = null;


    private List<GoodsBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<GoodsBean> mAdapter;
    private String mCutPath;

    private String mImageUrl;

    private String mId;
    private PopupWindow mPromptPopupWindow;

    private static final int REQUEST_CROP = 200;
    private static final int REQUEST_ALBUM = 100;

    public static final int PERMISSION_STORAGE = 1000;
    public static final int PERMISSION_CAMRA = 2000;
    private PopupWindow mPopupWindow;

    private Uri mTakePhotoUri;
    private static final int REQUEST_TAKE_PHOTO = 300;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        mRV.setNestedScrollingEnabled(false);
        initData();
        initEvent();


    }

    private void initData() {
        mRV.setNestedScrollingEnabled(false);
        mAdd = ContextCompat.getDrawable(this, R.drawable.follow_add);
        mAdd.setBounds(0, 0, mAdd.getMinimumWidth(), mAdd.getMinimumHeight());
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mKeywords = getIntent().getExtras().getString(Constant.KEYWORD);
            getFaceInfo();
            getGoods();
            getMyGroups();
        }
        CommonUtil.setRefreshStyle(mRefreshLayout, PersonalActivity.this);
        EventBus.getDefault().register(PersonalActivity.this);
    }

    private void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAllBrandTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllBrandActivity.open(PersonalActivity.this, mId);
            }
        });
        mBgIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startPictrueLibrary();

            }
        });
        mLatestTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLatestTV.setTextColor(ContextCompat.getColor(PersonalActivity.this, R.color.font_personal_red));
                mHotTV.setTextColor(ContextCompat.getColor(PersonalActivity.this, R.color.color_gray_personal));
                mType = TYPE_NEWEST;
                mRefreshLayout.startRefresh();

            }
        });
        mHotTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHotTV.setTextColor(ContextCompat.getColor(PersonalActivity.this, R.color.font_personal_red));
                mLatestTV.setTextColor(ContextCompat.getColor(PersonalActivity.this, R.color.color_gray_personal));
                mType = TYPE_HOT;
                mRefreshLayout.startRefresh();
            }
        });
        mFollowTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAttention(mId);
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mPage = 1;
                mDataStatus = STATUS_REFRESH;
                getGoods();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getGoods();
            }
        });
        mHeadRIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

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
            mTakePhotoUri = FileProvider.getUriForFile(PersonalActivity.this, "com.facewarrant.fw.fileprovider", file);//通过FileProvider创建一个content类型的Uri
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

    private void  showPupWindow(){


    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<GoodsBean>(this, R.layout.item_personal_gooods, mList) {
                @Override
                protected void convert(ViewHolder holder, final GoodsBean goodsBean, int position) {
                    RoundedImageView topIV = holder.getView(R.id.riv_item_personal_goods_top);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topIV.getLayoutParams();
                    params.height = (DisplayUtil.getScreenWidth(mContext) -
                            (int) DisplayUtil.dpToPx(PersonalActivity.this, 40)) / 2 * goodsBean.getHeight() / goodsBean.getWidth();
                    Glide.with(PersonalActivity.this).load(goodsBean.getTopUrl()).into(topIV);
                    holder.setText(R.id.tv_item_personal_goods_name, goodsBean.getName());
                    holder.setText(R.id.tv_item_personal_goods_face_number, goodsBean.getFaceNumber());
                    holder.setText(R.id.tv_item_personal_goods_time, goodsBean.getTime());
                    holder.setText(R.id.tv_item_personal_goods_buy_no, goodsBean.getCarNumber());
                    ImageView newIV = holder.getView(R.id.iv_item_personal_goods_new);
                    switch (goodsBean.getNew()) {
                        case 0:
                            newIV.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            newIV.setVisibility(View.GONE);
                            break;
                    }
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (goodsBean.getType()) {
                                case GoodsBean.TYPE_PIC:
                                    WarrantDetailActivity.open(PersonalActivity.this, goodsBean.getId(),
                                            AccountManager.sUserBean.getId(), "");
                                    break;
                                case GoodsBean.TYPE_VIDEO:
                                    WarrantDetailActivity.open(PersonalActivity.this, goodsBean.getId(),
                                            AccountManager.sUserBean.getId(), goodsBean.getVId());
                                    break;
                            }
                        }
                    });

                }
            };
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRV.setLayoutManager(staggeredGridLayoutManager);
            mRV.addItemDecoration(new SpaceItemDecoration(25, 2));
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void openPicture() {
//
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
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
                    openPicture();
                } else {
                    ToastUtil.showShort(PersonalActivity.this, "未开启手机读写权限，请去设置打开！");
                }
                break;

            case PERMISSION_CAMRA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ToastUtil.showShort(PersonalActivity.this, "未开启手机读写权限，请去设置打开！");
                }

                break;


        }
    }

    /**
     * 打开相册
     */
    private void startPictrueLibrary() {
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
        if (requestCode ==
                REQUEST_CROP && data != null) {
            mCutPath = setPicToView(data);
            Glide.with(PersonalActivity.this)
                    .load(mCutPath)
                    .into(mHeadRIV);
            getPicStsToken();
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            cropPhoto();//裁剪拍照后的图片
            getPicStsToken();
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
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0.1);
        intent.putExtra("aspectY", 0.1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        startActivityForResult(intent, REQUEST_CROP);

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
            path = FileUtilcll.saveFile(PersonalActivity.this, "temphead.jpg", photo);

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
                                ToastUtil.showShort(PersonalActivity.this, data.getString("resultDesc"));

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

    private void uploadIMG() {
        PutObjectRequest put = new PutObjectRequest((String) SPUtil.get(Constant.BUCKET_NAME, ""),
                "1" + "15962165392" + TimeUtil.formatTime(System.currentTimeMillis(), "yyyyMMddHHmmss")
                        + ".jpg", mCutPath);
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
                LogUtil.e("ETag==========", putObjectResult.getETag());
                LogUtil.e("RequestId==========", putObjectResult.getRequestId());

                mImageUrl = SPUtil.get(Constant.IMAGE_DOMAIN, "") + putObjectRequest.getObjectKey();
                LogUtil.e(TAG, mImageUrl);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(PersonalActivity.this).load(mImageUrl).into(mBgIV);
                    }
                });

                changeBgImage();

            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                if (e != null) {
                    // 本地异常如网络异常等
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "oss网络异常", Toast.LENGTH_LONG).show();
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
        oss = new OSSClient(PersonalActivity.this, (String) SPUtil.get(Constant.END_POINT, ""), credentialProvider);
    }


    private void changeBgImage() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("backgroundPicture", SPUtil.get(Constant.IMAGE_DOMAIN, "") + mImageUrl);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .changePersonalBG(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    private void getFaceInfo() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", mId);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getFaceInfo(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        mNameTV.setText(result.getString("faceName"));
                        mFavouriteCountTV.setText(result.getString("favouriteCount"));
                        mAttentionTV.setText(result.getString("attentionCount"));
                        mFansTV.setText(result.getString("fansCount"));
                        mWarrantTV.setText(result.getString("releaseGoodsCount"));
                        mIntroduceTV.setText(result.getString("standing"));
                        mAttention = result.getInt("isAttention");
                        switch (mAttention) {
                            case 0:
                                mFollowTV.setCompoundDrawables(mAdd, null, null, null);
                                mFollowTV.setText(R.string.follow);
                                mFollowTV.setBackgroundResource(R.drawable.shape_personal_deep_red);
                                break;
                            case 1:
                                mFollowTV.setCompoundDrawables(null, null, null, null);
                                mFollowTV.setText(R.string.followed);
                                mFollowTV.setBackgroundResource(R.drawable.shape_follow_gray);
                                break;

                        }
                        Glide.with(PersonalActivity.this)
                                .load(result.getString("headUrl"))
                                .into(mHeadRIV);

                    } else {
                        ToastUtil.showShort(PersonalActivity.this, data.getString("resultDesc"));
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

    private void getGoods() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", mId);
        map.put("sortType", mType + "");
        if (!TextUtils.isEmpty(mKeywords)) {
            map.put("searchCondition", mKeywords);
        }
        map.put("page", mPage + "");
        map.put("rows", 25 + "");
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getFaceGoods(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                switch (mDataStatus) {
                    case STATUS_REFRESH:
                        mRefreshLayout.finishRefreshing();
                        mList.clear();
                        break;
                    case STATUS_LOAD:
                        mRefreshLayout.finishLoadmore();
                        break;
                }
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONArray result = data.getJSONArray("result");
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject resultItem = result.getJSONObject(i);
                            GoodsBean goodsBean = new GoodsBean();
                            goodsBean.setWidth(resultItem.getInt("width"));
                            goodsBean.setHeight(resultItem.getInt("Height"));
                            goodsBean.setTopUrl(resultItem.getString("modelUrl"));
                            goodsBean.setTime(resultItem.getString("createTime"));
                            goodsBean.setName(resultItem.getString("goodsName"));
                            goodsBean.setCarNumber(resultItem.getString("buyNo"));
                            goodsBean.setFaceNumber(resultItem.getString("favoriteCount"));
                            goodsBean.setId(resultItem.getString("releaseGoodsId"));
                            goodsBean.setType(resultItem.getInt("modelType"));
                            goodsBean.setVId(resultItem.getString("videoUrl"));
                            goodsBean.setNew(resultItem.getInt("isNew"));


                            mList.add(goodsBean);
                        }
                        showRecyclerView();

                    } else if (data.getInt("resultCode") == 4003) {
                        ToastUtil.showShort(PersonalActivity.this, "账号在别处登录，请重新登录！");
                        AccountManager.loginOut(PersonalActivity.this);
                    } else {
                        ToastUtil.showShort(PersonalActivity.this, data.getString("resultDesc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable t) {
                switch (mDataStatus) {
                    case STATUS_REFRESH:
                        mRefreshLayout.finishRefreshing();
                        break;
                    case STATUS_LOAD:
                        mRefreshLayout.finishLoadmore();
                        break;
                }
            }
        });
    }

    public static void open(Activity activity, String id) {
        Intent intent = new Intent(activity, PersonalActivity.class);
        intent.putExtra(Constant.ID, id);
        activity.startActivity(intent);
    }


    public static void searchOpen(Activity activity, String id, String keywords) {
        Intent intent = new Intent(activity, PersonalActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.KEYWORD, keywords);
        activity.startActivity(intent);
    }

    private void divideGroup() {
        final Map<String, String> map = new HashMap<>();
        map.put("faceId", mId);
        String mGIds = "";
        for (int i = 0; i < mSelectList.size(); i++) {
            mGIds = mGIds + mSelectList.get(i).getId() + ",";
        }
        map.put("groupsId", mGIds.substring(0, mGIds.length() - 1));
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .followJoinGroup(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                EventBus.getDefault().post(new UpdateFaceListEvent());
                            } else {
                                ToastUtil.showShort(PersonalActivity.this, data.getString("resultDesc"));
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

    private void createGroup(String name) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("groupsName", name);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .createGroup(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                getMyGroups();

                            } else {
                                ToastUtil.showShort(PersonalActivity.this, data.getString("resultDesc"));
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

    private void getAttention(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", id);
        map.put("isAttention", mAttention + "");
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .attention(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {

                                switch (mAttention) {
                                    case 0: //关注成功
                                        mFollowTV.setCompoundDrawables(null, null, null, null);
                                        mFollowTV.setText(R.string.followed);
                                        mFollowTV.setBackgroundResource(R.drawable.shape_follow_gray);
                                        mAttention = 1;
                                        showPromptPopupwindow();

                                        break;
                                    case 1://取消关注成功
                                        mFollowTV.setCompoundDrawables(mAdd, null, null, null);
                                        mFollowTV.setText(R.string.follow);
                                        mFollowTV.setBackgroundResource(R.drawable.shape_personal_deep_red);
                                        mAttention = 0;
                                        break;

                                }
                                EventBus.getDefault().post(new UpdateFaceListEvent());
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

    private void showPromptPopupwindow() {
        if (mPromptPopupWindow != null && mPromptPopupWindow.isShowing()) {
            mPromptPopupWindow.dismiss();
        } else {
            View view = LayoutInflater.from(this).inflate(R.layout.pop_divide_group, null);
            RecyclerView recyclerView = view.findViewById(R.id.rv_pop_divider_group);
            TextView sureTV = view.findViewById(R.id.tv_pop_add_face_group_sure);
            TextView cancelTV = view.findViewById(R.id.tv_pop_add_face_group_cancel);
            TextView add = view.findViewById(R.id.tv_add_group);
            final LinearLayout groupListLL = view.findViewById(R.id.ll_group_list);
            final LinearLayout addLL = view.findViewById(R.id.tv_item_pop_divider_group_add);
            final EditText nameET = view.findViewById(R.id.et_pop_add_face_group_name);
            TextView addSureTV = view.findViewById(R.id.tv_pop_add_face_add_sure);
            if (mSelectList.size() > 0) {
                for (int i = 0; i < mSelectList.size(); i++) {
                    FaceGroupBean faceGroupBean = mSelectList.get(i);
                    for (int j = 0; j < mPopList.size(); j++) {
                        FaceGroupBean faceGroupBean1 = mPopList.get(j);
                        if (faceGroupBean.getId().equals(faceGroupBean1.getId())) {
                            faceGroupBean1.setSelect(true);
                        }
                    }
                }
            }
            RecyclerCommonAdapter<FaceGroupBean> adapter = new RecyclerCommonAdapter<FaceGroupBean>
                    (PersonalActivity.this, R.layout.item_pop_divider_grop, mPopList) {
                @Override
                protected void convert(ViewHolder holder, final FaceGroupBean faceGroupBean, int position) {
                    holder.setText(R.id.tv_item_pop_divider_group_name, faceGroupBean.getName());
                    final ImageView selectIV = holder.getView(R.id.iv_item_pop_divider_group);
                    if (faceGroupBean.isSelect()) {
                        selectIV.setImageResource(R.drawable.item_choose_red);
                    } else {
                        selectIV.setImageResource(R.drawable.item_choose_gray);
                    }
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!faceGroupBean.isSelect()) {
                                faceGroupBean.setSelect(true);
                                selectIV.setImageResource(R.drawable.item_choose_red);
                                mSelectList.add(faceGroupBean);
                            } else {
                                faceGroupBean.setSelect(false);
                                selectIV.setImageResource(R.drawable.item_choose_gray);
                                mSelectList.remove(faceGroupBean);
                            }
                        }
                    });


                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonalActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            sureTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPromptPopupWindow.dismiss();
                    if (mSelectList.size() > 0) {
                        divideGroup();
                    }
                }
            });
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPromptPopupWindow.dismiss();
                    mSelectList.clear();
                }
            });
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupListLL.setVisibility(View.GONE);
                    addLL.setVisibility(View.VISIBLE);
                }
            });
            addSureTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameET.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        ToastUtil.showShort(PersonalActivity.this, "请填写群组名称");
                    } else {
                        groupListLL.setVisibility(View.VISIBLE);
                        addLL.setVisibility(View.GONE);
                        createGroup(name);
                    }
                }
            });


            mPromptPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPromptPopupWindow.setFocusable(true);
            mPromptPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);


        }

    }

    private void getMyGroups() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyGroups(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        mPopList.clear();
                        JSONObject result = data.getJSONObject("result");
                        JSONArray groupsList = result.getJSONArray("groupsList");
                        for (int i = 0; i < groupsList.length(); i++) {
                            JSONObject group = groupsList.getJSONObject(i);
                            if (group.getInt("groupsType") != 1) {
                                FaceGroupBean faceGroupBean = new FaceGroupBean();
                                faceGroupBean.setId(group.getString("groupsId"));
                                faceGroupBean.setName(group.getString("groupsName"));
                                faceGroupBean.setNumber(group.getString("faceNum"));
                                mPopList.add(faceGroupBean);
                            }
                        }

                    } else {
                        ToastUtil.showShort(PersonalActivity.this, data.getString("resultDesc"));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fresh(UpdateWarrantGoodsEvent updateWarrantGoodsEvent) {
        mRefreshLayout.startRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(PersonalActivity.this);
    }
}

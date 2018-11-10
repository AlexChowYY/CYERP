package com.facewarrant.fw.ui.fragment.WarrantIt;

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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.snap.AliyunSnapVideoParam;
import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.bean.GoodsBean;
import com.facewarrant.fw.event.UpdateWarrantListEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.mine.WarrantDetailActivity;
import com.facewarrant.fw.ui.activity.warrantIt.FillInformationActivity;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.FileStorage;
import com.facewarrant.fw.util.FileUtilcll;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.RealPathFromUriUtils;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.view.SpaceItemDecoration;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class WarrantItFragment extends BaseFragment {
    private static final String TAG = "WarrantItFragment";
    private static final int REQUEST_CROP = 200;
    private static final int REQUEST_ALBUM = 100;
    private static final int REQUEST_TAKE_PHOTO = 300;


    public static final int PERMISSION_STORAGE = 1000;
    public static final int PERMISSION_CAMRA = 2000;


    @BindView(R.id.rv_fragment_warrant_it)
    RecyclerView mRV;
    @BindView(R.id.iv_fragment_warrant_it_release)
    ImageView mReleaseIV;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private RecyclerCommonAdapter<GoodsBean> mAdapter;


    private List<GoodsBean> mList = new ArrayList<>();

    public static final int REQUEST_CODE = 2001;


    private PopupWindow mPopupWindow;

    private Uri mTakePhotoUri;

    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_warrant_it;
    }

    @Override
    public void initData() {

        getWarrantList();
        EventBus.getDefault().register(this);
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);
    }

    @Override
    public void initEvent() {
        mReleaseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPromptPopupwindow();
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mPage = 1;
                mDataStatus = STATUS_REFRESH;
                getWarrantList();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getWarrantList();
            }
        });
    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<GoodsBean>(mActivity, R.layout.item_warrant_it, mList) {
                @Override
                protected void convert(ViewHolder holder, final GoodsBean goodsBean, int position) {
                    RoundedImageView topIV = holder.getView(R.id.iv_item_warrant_it);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topIV.getLayoutParams();
                    if (goodsBean.getWidth() != 0) {
                        params.height = (DisplayUtil.getScreenWidth(mContext) -
                                (int) DisplayUtil.dpToPx(mActivity, 40)) / 3 * goodsBean.getHeight() / goodsBean.getWidth();
                    }

                    Glide.with(mActivity).load(goodsBean.getTopUrl()).into(topIV);
                    ImageView playIV = holder.getView(R.id.iv_warrant_it_detail_play);
                    switch (goodsBean.getType()) {
                        case GoodsBean.TYPE_PIC:
                            playIV.setVisibility(View.GONE);
                            break;
                        case GoodsBean.TYPE_VIDEO:
                            playIV.setVisibility(View.VISIBLE);
                            break;
                    }
                    topIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (goodsBean.getType()) {
                                case GoodsBean.TYPE_PIC:
                                    WarrantDetailActivity.open(mActivity, goodsBean.getId(),
                                            AccountManager.sUserBean.getId(), "");
                                    break;
                                case GoodsBean.TYPE_VIDEO:
                                    WarrantDetailActivity.open(mActivity, goodsBean.getId(),
                                            AccountManager.sUserBean.getId(), goodsBean.getVId());
                                    break;
                            }
                        }
                    });
                }
            };
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mRV.addItemDecoration(new SpaceItemDecoration((int) DisplayUtil.dpToPx(mActivity, 10), 3));
            mRV.setLayoutManager(staggeredGridLayoutManager);
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }


    private void getWarrantList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", mPage + "");
        map.put("rows", 15 + "");
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyWarrantItList(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
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
                            goodsBean.setTopUrl(resultItem.getString("modelUrl"));
                            goodsBean.setWidth(resultItem.getInt("width"));
                            goodsBean.setHeight(resultItem.getInt("Height"));
                            goodsBean.setId(resultItem.getString("releaseGoodsId"));
                            goodsBean.setType(resultItem.getInt("modelType"));
                            goodsBean.setVId(resultItem.getString("videoUrl"));
                            mList.add(goodsBean);
                        }
                        showRecyclerView();
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


    private void showPromptPopupwindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_show_bottom_warant, null);
            LinearLayout topLL = view.findViewById(R.id.ll_pop_show_bottom_warrant_top);
            ImageView closeIV = view.findViewById(R.id.iv_pop_show_bottom_warrant_close);
            LinearLayout libraryLL = view.findViewById(R.id.ll_pop_show_bottom_warrant_library);
            LinearLayout takePhotoLL = view.findViewById(R.id.ll_pop_show_bottom_warrant_take);
            LinearLayout videoLL = view.findViewById(R.id.ll_pop_show_bottom_warrant);
            takePhotoLL.setOnClickListener(new View.OnClickListener() {//拍照
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
            libraryLL.setOnClickListener(new View.OnClickListener() {//选取相册
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE);
                        } else {
                            startPictrueLibrary();
                        }
                    } else {
                        startPictrueLibrary();
                    }
                }
            });
            videoLL.setOnClickListener(new View.OnClickListener() {//拍摄视频
                @Override
                public void onClick(View v) {
                    AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
                            .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_3_4)
                            .build();
                    AliyunVideoRecorder.startRecordForResult(mActivity, REQUEST_CODE, recordParam);
                }
            });
            closeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                }
            });
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateWarrantListEvent event) {
        //处理逻辑
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }

        mTakePhotoUri = null;
        mRefreshLayout.startRefresh();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALBUM && data != null) {//调用系统相册
            String path = RealPathFromUriUtils.getRealPathFromUri(mActivity, data.getData());
            FillInformationActivity.open(mActivity, path, "");
        }
        if (requestCode == REQUEST_CROP && data != null) {
            setPicToView(data);
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            cropPhoto();//裁剪拍照后的图片
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
        intent.putExtra("outputX", 161);
        intent.putExtra("outputY", 161);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", false);
        startActivityForResult(intent, REQUEST_CROP);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //图片路径
            String path = FileUtilcll.saveFile(mActivity, "temphead.jpg", photo);
            FillInformationActivity.open(mActivity, path, "");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPictrueLibrary();

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


    /**
     * 打开系统相机
     */
    private void openCamera() {
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
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0.1);
        intent.putExtra("aspectY", 0.1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        startActivityForResult(intent, REQUEST_CROP);
    }
}

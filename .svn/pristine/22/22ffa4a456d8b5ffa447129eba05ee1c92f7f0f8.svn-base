package com.facewarrant.fw.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
import com.aliyun.demo.crop.AliyunVideoCrop;
import com.aliyun.demo.crop.MediaActivity;
import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.CropKey;
import com.aliyun.struct.common.VideoQuality;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.snap.AliyunSnapVideoParam;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.vp.VpFragmentAdapter;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.event.BackToHomePagerEvent;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.global.LocalApplication;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.mine.WarrantDetailActivity;
import com.facewarrant.fw.ui.activity.warrantIt.FillInformationActivity;
import com.facewarrant.fw.ui.fragment.faceLibrary.FaceLibraryFragment;
import com.facewarrant.fw.ui.fragment.find.FindFragment;
import com.facewarrant.fw.ui.fragment.mine.MineFragment;
import com.facewarrant.fw.ui.fragment.WarrantIt.WarrantItFragment;
import com.facewarrant.fw.ui.fragment.home.HomePagerFragment;
import com.facewarrant.fw.util.ActivityUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.SPUtil;
import com.facewarrant.fw.view.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.vp_activity_main)
    NoScrollViewPager mMainVP;
    @BindView(R.id.rd_activity_main_home_pager)
    RadioButton mHomePagerRB;
    @BindView(R.id.rd_activity_main_library)
    RadioButton mLibraryRB;
    @BindView(R.id.iv_activity_main_warrant_it)
    ImageView mWarrantItIV;
    @BindView(R.id.rd_activity_main_find)
    RadioButton mFindRB;
    @BindView(R.id.rd_activity_main_mine)
    RadioButton mMineRB;
    @BindView(R.id.rd_activity_main_warrant_it)
    RadioButton mWarrantRB;
    @BindView(R.id.rg_activity_main)
    RadioGroup mMainRG;
    private static final String SD_PATH = "/sdcard/fw/pic/";
    private static final String IN_PATH = "/fw/pic/";


    private static final int REQUEST_CROP = 2002;
    public static final int REQUEST_RECORD = 2001;

    private List<Fragment> mFragList = new ArrayList<>();
    private VpFragmentAdapter mAdapter;
    private TelephonyManager mTelephonyManager;

    private String mKeyId;
    private String mSecretId;
    private String mSecurityToken;
    private String mExpiration;
    private VODSVideoUploadClient vodsVideoUploadClient;
    private String mImagePath;
    private String mVideoPath;


    @Override

    protected int getContentViewId() {

        return R.layout.activity_main;
    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();


    }


    @Override
    public void initData() {
        EventBus.getDefault().register(mActivity);
        mMainVP.setNoScroll(true);
        mFragList.add(new HomePagerFragment());
        mFragList.add(new FaceLibraryFragment());
        mFragList.add(new WarrantItFragment());
        mFragList.add(new FindFragment());
        mFragList.add(new MineFragment());
        mAdapter = new VpFragmentAdapter(getSupportFragmentManager(), mFragList);
        mMainVP.setAdapter(mAdapter);
        //获取OSS参数
        getOSS();
        //getUserStanding();

        vodsVideoUploadClient = new VODSVideoUploadClientImpl(this.getApplicationContext());
        vodsVideoUploadClient.init();
        if (AccountManager.sUserBean != null) {
            if (getIntent() != null) {
                Intent data = getIntent();
                String action = data.getAction();
                if (Intent.ACTION_VIEW.equals(action)) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        String id = uri.getQueryParameter("id");
                        WarrantDetailActivity.open(mActivity, id);
                    }
                }
            }
        } else {
            AccountManager.loginOut(mActivity);
        }


    }


    @Override
    public void initEvent() {
        mMainRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rd_activity_main_home_pager:
                        mMainVP.setCurrentItem(0, false);
                        break;
                    case R.id.rd_activity_main_library:
                        mMainVP.setCurrentItem(1, false);
                        break;
                    case R.id.rd_activity_main_find:
                        mMainVP.setCurrentItem(3, false);
                        break;
                    case R.id.rd_activity_main_mine:
                        mMainVP.setCurrentItem(4, false);
                        break;

                }

            }
        });
        mWarrantItIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainVP.setCurrentItem(2, false);
            }
        });
        mWarrantRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainVP.setCurrentItem(2, false);
            }
        });
        mMineRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainVP.setCurrentItem(4, false);
                EventBus.getDefault().post(new UpdateUserInfoEvent());
            }
        });
        mMainVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mHomePagerRB.setChecked(true);
                        break;
                    case 1:
                        mLibraryRB.setChecked(true);
                        break;
                    case 2:
                        mWarrantRB.setChecked(true);
                        break;
                    case 3:
                        mFindRB.setChecked(true);
                        break;
                    case 4:
                        mMineRB.setChecked(true);
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        vodsVideoUploadClient.resume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        vodsVideoUploadClient.pause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setToHomePager(BackToHomePagerEvent backToHomePagerEvent) {
        mMainVP.setCurrentItem(0, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vodsVideoUploadClient.release();
        EventBus.getDefault().unregister(mActivity);
    }

    public static Bitmap getVideoPhoto(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }


    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + "yangynag" + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }


    private void getOSS() {
        final Map<String, String> map = new HashMap<>();
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class).getOSSData(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
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

    private void upload() {
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
        LogUtil.e(TAG, "vodsVideoUploadClient" + vodsVideoUploadClient);
        vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
            @Override
            public void onUploadSucceed(String videoId, String imageUrl) {
//上传成功返回视频ID和图片URL.
                LogUtil.d(TAG, "onUploadSucceed" + "videoId:" + videoId + "imageUrl" + imageUrl);
            }

            @Override
            public void onUploadFailed(String code, String message) {
                //上传失败返回错误码和message.错误码有详细的错误信息请开发者仔细阅读
                LogUtil.d(TAG, "onUploadFailed" + "code" + code + "message" + message);
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

    private void getSTSToken() {
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
                    upload();

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
        if (requestCode == 2001) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE, 0);
                if (type == AliyunVideoRecorder.RESULT_TYPE_CROP) {
                    String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);

                    Toast.makeText(mActivity, "文件路径为 " + path + " 时长为 " + data.getLongExtra(CropKey.RESULT_KEY_DURATION, 0), Toast.LENGTH_SHORT).show();
                } else if (type == AliyunVideoRecorder.RESULT_TYPE_RECORD) {

                    Toast.makeText(mActivity, "文件路径为 " + data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH),
                            Toast.LENGTH_SHORT).show();
                    LogUtil.e(TAG, data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH));
                    mVideoPath = data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH);
                    mImagePath = saveBitmap(getVideoPic(mVideoPath));
                    //startCrop();

                    FillInformationActivity.open(mActivity, mImagePath, mVideoPath);

                    LogUtil.e(TAG, "mVideoPath=" + mVideoPath + " mImagePath ==" + mImagePath);
                }
                //getSTSToken();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mActivity, "用户取消录制", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                int type = data.getIntExtra(MediaActivity.RESULT_TYPE, 0);
                if (type == MediaActivity.RESULT_TYPE_CROP) {
                    String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
                    mImagePath = path;
                    Toast.makeText(this, "文件路径为 " + path + " 时长为 " + data.getLongExtra(CropKey.RESULT_KEY_DURATION, 0), Toast.LENGTH_SHORT).show();

                } else if (type == MediaActivity.RESULT_TYPE_RECORD) {
                    Toast.makeText(this, "文件路径为 " + data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH), Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "用户取消裁剪", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private Bitmap getVideoPic(String path) {
        MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();
        //mPath本地视频地址
        mMetadataRetriever.setDataSource(path);
        //这个时候就可以通过mMetadataRetriever来获取这个视频的一些视频信息了
        String duration = mMetadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
        String width = mMetadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
        String height = mMetadataRetriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        //上面三行代码可以获取这个视频的宽高和播放总时长
        //下面这行代码才是关键，用来获取当前视频某一时刻(毫秒*1000)的一帧
        Bitmap bitmap = mMetadataRetriever.getFrameAtTime(1 * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
        //这时就可以获取这个视频的某一帧的bitmap了
        return bitmap;

    }

    public String saveBitmap(Bitmap bitmap) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "video_pic");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "video_pic" + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "/sdcard/namecard/")));
        return file.getPath();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityUtil.getInstance().finishAllActivity();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 获取到Activity下的Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null)
        {
            return;
        }
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (Fragment fragment : fragments)
        {
            if (fragment != null)
            {
                // 这里就会调用我们Fragment中的onRequestPermissionsResult方法
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }



}

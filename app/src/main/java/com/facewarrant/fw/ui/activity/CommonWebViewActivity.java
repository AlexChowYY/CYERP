package com.facewarrant.fw.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.global.LocalApplication;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.util.Util;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class CommonWebViewActivity extends BaseActivity {
    private static final String TAG = "CommonWebViewActivity";

    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.wv_activity_common_webView)
    WebView mWebView;
    private int mType;
    public static final int TYPE_ABOUT_US = 1;
    public static final int TYPE_INVITE_FRIEND = 2;
    public static final int TYPE_FW = 3;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_common_webview;
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mType = getIntent().getExtras().getInt(Constant.TYPE);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);//允许使用js
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
            //支持屏幕缩放
            webSettings.setSupportZoom(false);
            webSettings.setBuiltInZoomControls(true);

            switch (mType) {
                case TYPE_ABOUT_US:
                    mTitleTV.setText(R.string.about_us);

                    mWebView.loadUrl(RequestManager.mBaseUrl + RequestManager.mInterfacePrefix +
                            "v1/base/aboutUs");
                    break;
                case TYPE_INVITE_FRIEND:
                    mTitleTV.setText(R.string.my_invite);
                    mWebView.loadUrl(RequestManager.mBaseUrl + RequestManager.mInterfacePrefix +
                            "v1/base/inviteInfo?userId=" + AccountManager.sUserBean.getId());
                    mWebView.addJavascriptInterface(new js(), "webViewInterface");
                    break;

                case TYPE_FW:
                    mWebView.loadUrl("file:///android_asset/agreement.html");
                    mTitleTV.setText("软件许可和服务协议");

                    break;

            }
        }

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static void open(Activity activity, int type) {
        Intent intent = new Intent(activity, CommonWebViewActivity.class);
        intent.putExtra(Constant.TYPE, type);
        activity.startActivity(intent);
    }

    public class js {
        @JavascriptInterface
        public void JS_OCClick(int flag) {
            switch (flag) {
                case 0://微信朋友
                    shareToWechart();


                    break;
                case 1://微信朋友圈
                    shareToFriends();


                    break;

            }

        }

    }

    private void shareToWechart() {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = RequestManager.mBaseUrl + RequestManager.mInterfacePrefix +
                "v1/base/invite?userId=" + AccountManager.sUserBean.getId();
        LogUtil.e(TAG, wxWebpageObject.webpageUrl);
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = "我是" + AccountManager.sUserBean.getName() + "，快来下载【脸碑】";
        message.description = "注册立即奖励" + AccountManager.sUserBean.getInteragel() + "个积分，可以兑换脸值（钱呀钱），签到还有更多惊喜噢~";
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.qr);

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        bmp.recycle();
        message.thumbData = Util.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        LocalApplication.api.sendReq(req);

    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void shareToFriends() {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = RequestManager.mBaseUrl + RequestManager.mInterfacePrefix +
                "v1/base/invite?userId=" + AccountManager.sUserBean.getId();
        LogUtil.e(TAG, wxWebpageObject.webpageUrl);
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = "我是" + AccountManager.sUserBean.getName() + "，快来下载【脸碑】";
        message.description = "注册立即奖励" + AccountManager.sUserBean.getInteragel() + "个积分，可以兑换脸值（钱呀钱），签到还有更多惊喜噢~";
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.qr);

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        bmp.recycle();
        message.thumbData = Util.bmpToByteArray1(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        LocalApplication.api.sendReq(req);

    }

}

package com.facewarrant.fw.ui.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.EvaluateBean;
import com.facewarrant.fw.event.UpdateEvaluateList;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
import com.facewarrant.fw.event.UpdateWarrantGoodsEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.global.LocalApplication;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.ImageUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.util.Util;
import com.facewarrant.fw.util.ZXingUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class WarrantDetailActivity extends BaseActivity {
    private static final String TAG = "WarrantDetailActivity";
    @BindView(R.id.iv_layout_top_back_share)
    ImageView mShareIV;
    @BindView(R.id.iv_layout_top_back_location)
    ImageView mLocationIV;
    @BindView(R.id.iv_activity_warrant_detail_top)
    RoundedImageView mTopIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.riv_activity_warrant_detail_top)
    RoundedImageView mHeadRIV;
    @BindView(R.id.tv_activity_warrant_it_name)
    TextView mNameTV;
    @BindView(R.id.tv_activity_warrant_detail_brand_name)
    TextView mBrandNameTV;
    @BindView(R.id.tv_activity_warrant_detail_brand_type)
    TextView mBrandTypeTV;
    @BindView(R.id.tv_activity_warrant_detail_brand_use)
    TextView mUseTV;
    @BindView(R.id.tv_activity_warrant_detail_introduce)
    TextView mContentTV;
    @BindView(R.id.tv_activity_warrant_detail_use_detail)
    TextView mDetailTV;
    @BindView(R.id.ll_activity_warrant_detail_container)
    LinearLayout mContainerLL;
    @BindView(R.id.tv_activity_warrant_detail_evaluate_num)
    TextView mNumTV;
    @BindView(R.id.view_bottom_go)
    View mGoView;
    @BindView(R.id.view_bottom)
    View mNoneGoView;
    @BindView(R.id.iv_warrant_it_detail_face)
    ImageView mFaceIV;
    @BindView(R.id.iv_item_warrant_detail_collect)
    ImageView mCollectIV;
    @BindView(R.id.tv_warrant_it_detail_face)
    TextView mFaceTV;
    @BindView(R.id.tv_warrant_it_detail_evaluate)
    TextView mEvaluateTV;
    @BindView(R.id.tv_item_warrant_detail_collect)
    TextView mCollectTV;
    @BindView(R.id.ll_item_warrant_detail_collect)
    LinearLayout mCollectLL;
    @BindView(R.id.ll_warrant_it_detail_face)
    LinearLayout mFaceLL;
    @BindView(R.id.tv_warrant_detail_buy)
    TextView mBuyTV;
    @BindView(R.id.tv_activity_warrant_detail_attention)
    TextView mAttentionTV;
    @BindView(R.id.iv_warrant_detail_more)
    ImageView mMoreIV;
    @BindView(R.id.rv_activity_warrant_detail)
    RecyclerView mRV;
    @BindView(R.id.tv_activity_warrant_detail_more)
    TextView mMoreTV;
    @BindView(R.id.ll_evaluate)
    LinearLayout mEvaluateLL;
    @BindView(R.id.ll_activity_warrant_detail_attention)
    LinearLayout mAttentionLL;


    private String mGoodTitle;
    private String mGoodContent;
    private Bitmap mGoodBitmap;


    private RecyclerCommonAdapter<EvaluateBean> mAdapter;
    private List<EvaluateBean> mList = new ArrayList<>();


    private boolean isExpand = false;

    private int mIsNew;


    private String mId;
    private String mFaceId;


    private int mCollectStatus = -1;
    private int mCollectCount = 0;
    private int mFavoriteStatus = -1;
    private int mFavoriteCount = 0;

    private int mAttentionStatus = -1;
    private PopupWindow mPromptPopupWindow;

    private Drawable mAdd;
    private Drawable mZanRed;
    private Drawable mZanGray;


    private PopupWindow mPopupWindow;

    private static final int THUMB_SIZE = 150;
    /**
     * 视频播放的VId
     */
    private String mVId;
    private String mShareImageUrl;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_warrant_detail;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(mActivity);
        mRV.setNestedScrollingEnabled(false);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mFaceId = getIntent().getExtras().getString(Constant.FACE_ID);
            getWarrantItDetail();
        }
        mTitleTV.setText(R.string.warrant_detail);
        mAdd = ContextCompat.getDrawable(mActivity, R.drawable.add_red);
        mAdd.setBounds(0, 0, mAdd.getMinimumWidth(), mAdd.getMinimumHeight());
        mZanRed = ContextCompat.getDrawable(mActivity, R.drawable.zan_red);
        mZanRed.setBounds(0, 0, mZanRed.getMinimumWidth(), mZanRed.getMinimumHeight());
        mZanGray = ContextCompat.getDrawable(mActivity, R.drawable.zan_gray);
        mZanGray.setBounds(0, 0, mZanGray.getMinimumWidth(), mZanGray.getMinimumHeight());


    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        mCollectLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collection();
            }
        });
        mFaceLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorite();
            }
        });
        mBuyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crateOrder();
            }
        });
        mAttentionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAttention(mFaceId, mAttentionStatus + "");
            }
        });
        mMoreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpand = !isExpand;
                if (isExpand) {
                    mContentTV.setEllipsize(null);
                    mContentTV.setSingleLine(false);
                    mMoreIV.setImageResource(R.drawable.up);

                } else {
                    mContentTV.setEllipsize(TextUtils.TruncateAt.END);
                    mContentTV.setLines(2);
                    mMoreIV.setImageResource(R.drawable.down);

                }
            }
        });
        mMoreTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllEvaluateActivity.open(mActivity, mId, mFaceId);
            }
        });
        mShareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow();

            }
        });
        mEvaluateLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllEvaluateActivity.open(mActivity, mId, mFaceId);
            }
        });

    }

    private void getWarrantItDetail() {
        final Map<String, String> map = new HashMap<>();
//        map.put("userId", "2165");
//        map.put("releaseGoodsId", "5289");
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("releaseGoodsId", mId);
        map.put("isNew", 0 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getWarrantDetail(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        setData(result);
                        EventBus.getDefault().post(new UpdateWarrantGoodsEvent());
                        EventBus.getDefault().post(new UpdateFaceListEvent());
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

    public static void open(Activity activity, String id, String mFid, String vId) {
        Intent intent = new Intent(activity, WarrantDetailActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.FACE_ID, mFid);
        intent.putExtra(Constant.V_ID, vId);
        activity.startActivity(intent);
    }

    public static void open(Activity activity, String id) {
        Intent intent = new Intent(activity, WarrantDetailActivity.class);
        intent.putExtra(Constant.ID, id);
        activity.startActivity(intent);

    }


    private void setData(final JSONObject data) {
        try {

            Glide.with(mActivity).load(data.getString("headUrl")).into(mHeadRIV);

            mNameTV.setText(data.getString("faceName"));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTopIV.getLayoutParams();
            params.width = DisplayUtil.getScreenWidth(mActivity);
            params.height = DisplayUtil.getScreenWidth(mActivity)
                    * data.getInt("height") / data.getInt("width");

            Glide.with(mActivity).load(data.getString("modelUrl")).into(mTopIV);
            mShareImageUrl = data.getString("modelUrl");
            mFaceId = data.getString("faceId");
            mBrandNameTV.setText(data.getString("brandName"));
            mBrandTypeTV.setText(data.getString("goodsBtype"));
            mUseTV.setText(data.getString("goodsName"));
            mGoodTitle = data.getString("goodsName");
            mContentTV.setText(data.getString("brandSynopsis"));
            mGoodContent = data.getString("brandSynopsis");
            mDetailTV.setText(data.getString("useDetail"));
            mNumTV.setText("评论（" + data.getString("commentCount") + "）");
            if (data.getString("faceId").equals(AccountManager.sUserBean.getId())) {
                mAttentionLL.setVisibility(View.GONE);
            } else {
                mAttentionLL.setVisibility(View.VISIBLE);
            }

            switch (data.getInt("hasBuy")) {
                case 0:
                    mGoView.setVisibility(View.GONE);
                    mNoneGoView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mGoView.setVisibility(View.VISIBLE);
                    mNoneGoView.setVisibility(View.GONE);
                    break;
            }
            //是否赏脸
            mFavoriteStatus = data.getInt("isFavorite");
            switch (mFavoriteStatus) {
                case 0:
                    mFaceIV.setImageResource(R.drawable.smile_black);
                    break;
                case 1:
                    mFaceIV.setImageResource(R.drawable.smile_red);
                    break;

            }
            //是否收藏
            mCollectStatus = data.getInt("isCollect");
            switch (mCollectStatus) {
                case 0:
                    mCollectIV.setImageResource(R.drawable.star);
                    break;
                case 1:
                    mCollectIV.setImageResource(R.drawable.star_red);
                    break;
            }
            //是否关注
            mAttentionStatus = data.getInt("isAttention");
            switch (mAttentionStatus) {
                case 0:
                    mAttentionTV.setCompoundDrawables(mAdd, null, null, null);
                    mAttentionTV.setText(R.string.follow);
                    mAttentionTV.setTextColor(ContextCompat.getColor(mActivity, R.color.common_red));
                    mAttentionLL.setBackgroundResource(R.drawable.shape_common_detail_bg);
                    break;
                case 1:
                    mAttentionTV.setCompoundDrawables(null, null, null, null);
                    mAttentionTV.setText(R.string.followed);
                    mAttentionTV.setTextColor(ContextCompat.getColor(mActivity, R.color.colorFontHint));
                    mAttentionLL.setBackgroundResource(R.drawable.shape_common_detail_bg_1);

                    break;

            }
            mFaceId = data.getString("faceId");
            mHeadRIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalActivity.open(mActivity, mFaceId);
                }
            });
            mFavoriteCount = data.getInt("favoriteCount");
            mCollectCount = data.getInt("collectCount");
            mFaceTV.setText("赏脸·" + data.getString("favoriteCount"));
            mEvaluateTV.setText("评论·" + data.getString("commentCount"));
            mCollectTV.setText("心愿·" + data.getString("collectCount"));
            mList.clear();
            if (!data.getString("commendReplyResponseDtoList").equals("null")) {
                //评论列表
                JSONArray commentList = data.getJSONArray("commendReplyResponseDtoList");
                for (int i = 0; i < commentList.length(); i++) {
                    final JSONObject commentItem = commentList.getJSONObject(i);
                    EvaluateBean evaluateBean = new EvaluateBean();
                    evaluateBean.setId(commentItem.getString("commentId"));
                    evaluateBean.setName(commentItem.getString("commentFromUser"));
                    evaluateBean.setToUrl(commentItem.getString("commentFromUserHeadUrl"));
                    evaluateBean.setContent(commentItem.getString("commentContent"));
                    evaluateBean.setTime(commentItem.getString("commentTime"));
                    evaluateBean.setEvaluateNumber(commentItem.getString("replyCount"));
                    evaluateBean.setZanNumber(commentItem.getString("commentLikeCount"));
                    evaluateBean.setIsLike(commentItem.getInt("isLike"));
                    evaluateBean.setUserId(commentItem.getString("commentFromUserId"));
                    if (!commentItem.getString("replyResponseDtoList").equals("null")) {
                        JSONArray replyArray = commentItem.getJSONArray("replyResponseDtoList");
                        List<EvaluateBean.EvaluateEvaluate> replyList = new ArrayList<>();
                        for (int j = 0; j < replyArray.length(); j++) {
                            JSONObject replyItem = replyArray.getJSONObject(j);
                            EvaluateBean.EvaluateEvaluate evaluateEvaluate = new EvaluateBean.EvaluateEvaluate();
                            evaluateEvaluate.setId(replyItem.getString("commentId"));
                            evaluateEvaluate.setFromNAME(replyItem.getString("replyFromUser"));
                            evaluateEvaluate.setToName(replyItem.getString("replyToUser"));
                            evaluateEvaluate.setFromId(replyItem.getString("replyFromUserId"));
                            evaluateEvaluate.setToId(replyItem.getString("replyToUserId"));
                            evaluateEvaluate.setTime(replyItem.getString("replyTime"));
                            evaluateEvaluate.setContent(replyItem.getString("replyContent"));
                            evaluateEvaluate.setZanNumber(replyItem.getString("replyLikeCount"));
                            replyList.add(evaluateEvaluate);
                        }
                        evaluateBean.setList(replyList);
                    }
                    mList.add(evaluateBean);

                }
                showRecyclerView();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void collection() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("releaseGoodsId", mId);
        map.put("isCollect", mCollectStatus + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .collection(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        switch (mCollectStatus) {
                            case 0:
                                mCollectCount = mCollectCount + 1;
                                mCollectIV.setImageResource(R.drawable.star_red);
                                mCollectTV.setText("心愿·" + mCollectCount);
                                mCollectStatus = 1;
                                break;
                            case 1:
                                mCollectCount = mCollectCount - 1;
                                mCollectIV.setImageResource(R.drawable.star);
                                mCollectTV.setText("心愿·" + mCollectCount);
                                mCollectStatus = 0;
                                break;

                        }
                        EventBus.getDefault().post(new UpdateUserInfoEvent());

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

    private void favorite() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("releaseGoodsId", mId);
        map.put("isFavorite", mFavoriteStatus + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .favorite(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        switch (mFavoriteStatus) {
                            case 0:
                                mFavoriteCount = mFavoriteCount + 1;
                                mFaceIV.setImageResource(R.drawable.smile_red);
                                mFaceTV.setText("赏脸·" + mFavoriteCount);
                                mFavoriteStatus = 1;
                                break;
                            case 1:
                                mFavoriteCount = mFavoriteCount - 1;
                                mFaceIV.setImageResource(R.drawable.smile_black);
                                mFaceTV.setText("赏脸·" + mFavoriteCount);
                                mFavoriteStatus = 0;
                                break;

                        }
                        EventBus.getDefault().post(new UpdateUserInfoEvent());

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


    private void crateOrder() {
        final Map<String, String> map = new HashMap<>();
        map.put("orderSource", 2 + "");
        map.put("phoneNo", AccountManager.sUserBean.getPhone());
        map.put("releaseGoodsId", mId);
        map.put("faceId", mFaceId);
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("countryCode", AccountManager.sUserBean.getCountryCode());
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .goBuy(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("code") == 200) {
                                mAttentionStatus = 1;
                                mAttentionTV.setCompoundDrawables(null, null, null, null);
                                mAttentionTV.setText(R.string.followed);
                                if (!TextUtils.isEmpty(data.getString("buyUrl")) ||
                                        !TextUtils.isEmpty(data.getString("microMallCode"))) {
                                    showPromptPopupwindow(data.getString("buyUrl"), data.getString("microMallCode"));
                                } else {


                                }

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

    private void showPromptPopupwindow(final String buyUrl, String mallUrl) {
        if (mPromptPopupWindow != null && mPromptPopupWindow.isShowing()) {
            mPromptPopupWindow.dismiss();
        } else {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_go_buy, null);
            LinearLayout typeOne = view.findViewById(R.id.ll_pop_buy_type_one);
            LinearLayout typeTwo = view.findViewById(R.id.ll_pop_buy_type_two);
            final ImageView typeOneNetIV = view.findViewById(R.id.iv_pop_buy_type_one_net);
            final ImageView typeOneMallIV = view.findViewById(R.id.iv_pop_buy_type_one_mall);
            final ImageView typeTwoIV = view.findViewById(R.id.iv_pop_buy_type_two);
            TextView typeTwoTV = view.findViewById(R.id.tv_pop_buy_type_two);
            ImageView closeIV = view.findViewById(R.id.iv_pop_go_buy_dismiss);

            if (!TextUtils.isEmpty(buyUrl) && !TextUtils.isEmpty(mallUrl)) {
                typeOne.setVisibility(View.VISIBLE);
                typeTwo.setVisibility(View.GONE);
                Bitmap bitmap = null;
                bitmap = ZXingUtils.createQRCode(buyUrl, (int) DisplayUtil.dpToPx(mActivity, 108), (int) DisplayUtil.dpToPx(mActivity, 108), null);
                typeOneNetIV.setImageBitmap(bitmap);
                Bitmap bitmap1 = null;
                bitmap1 = ZXingUtils.createQRCode(mallUrl, (int) DisplayUtil.dpToPx(mActivity, 108), (int) DisplayUtil.dpToPx(mActivity, 108), null);
                typeOneNetIV.setImageBitmap(bitmap1);

            } else if (!TextUtils.isEmpty(buyUrl)) {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;
                bitmap = ZXingUtils.createQRCode(buyUrl, (int) DisplayUtil.dpToPx(mActivity, 108), (int) DisplayUtil.dpToPx(mActivity, 108), null);
                typeTwoIV.setImageBitmap(bitmap);
                typeTwoTV.setText(R.string.net_mall);
            } else if (!TextUtils.isEmpty(mallUrl)) {
                typeOne.setVisibility(View.GONE);
                typeTwo.setVisibility(View.VISIBLE);
                Bitmap bitmap = null;
                bitmap = ZXingUtils.createQRCode(mallUrl, (int) DisplayUtil.dpToPx(mActivity, 108), (int) DisplayUtil.dpToPx(mActivity, 108), null);
                typeTwoIV.setImageBitmap(bitmap);
                typeTwoTV.setText(R.string.net_mall);
                typeTwoTV.setText(R.string.micro_mall);
            }
            closeIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPromptPopupWindow.dismiss();
                }
            });
            typeOneMallIV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    typeOneMallIV.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(typeOneMallIV.getDrawingCache());
                    typeOneMallIV.setDrawingCacheEnabled(false);
                    decodeQRCode(bitmap);
                    return true;
                }
            });

            typeTwoIV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    typeTwoIV.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(typeTwoIV.getDrawingCache());
                    typeTwoIV.setDrawingCacheEnabled(false);
                    decodeQRCode(bitmap);
                    return true;
                }
            });
            typeOneNetIV.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    typeOneNetIV.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(typeOneNetIV.getDrawingCache());
                    typeOneNetIV.setDrawingCacheEnabled(false);
                    decodeQRCode(bitmap);
                    return true;
                }
            });
            mPromptPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPromptPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }

    }

    /**
     * 解析二维码图片
     *
     * @param bitmap   要解析的二维码图片
     */
    public final Map<DecodeHintType, Object> HINTS = new EnumMap<>(DecodeHintType.class);

    public void decodeQRCode(final Bitmap bitmap) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    com.google.zxing.RGBLuminanceSource source = new com.google.zxing.RGBLuminanceSource(width, height, pixels);
                    Result result = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(source)), HINTS);
                    return result.getText();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result.toString())) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(result.toString());
                    intent.setData(content_url);
                    startActivity(intent);
                }
            }
        }.execute();

    }

    private void getAttention(String id, final String attention) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", id);
        map.put("isAttention", attention);
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
                                switch (attention) {
                                    case "0":
                                        mAttentionStatus = 1;
                                        mAttentionTV.setCompoundDrawables(null, null, null, null);
                                        mAttentionTV.setText(R.string.followed);
                                        mAttentionTV.setTextColor(ContextCompat.getColor(mActivity,R.color.colorFontHint));
                                        mAttentionLL.setBackgroundResource(R.drawable.shape_common_detail_bg_1);

                                        break;
                                    case "1":
                                        mAttentionStatus = 0;
                                        mAttentionTV.setCompoundDrawables(mAdd, null, null, null);
                                        mAttentionTV.setText(R.string.follow);
                                        mAttentionTV.setTextColor(ContextCompat.getColor(mActivity,R.color.common_red));
                                        mAttentionLL.setBackgroundResource(R.drawable.shape_common_detail_bg);
                                        break;
                                }

                                EventBus.getDefault().post(new UpdateUserInfoEvent());


                            } else {

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


    private void zan(final EvaluateBean evaluateBean, final int position, final TextView zan) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("isLike", evaluateBean.getIsLike() + "");
        map.put("type", 2 + "");
        map.put("relatedId", evaluateBean.getId());
        map.put("toUserId", evaluateBean.getUserId());
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .zanComment(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                switch (evaluateBean.getIsLike()) {
                                    case 0:
                                        mList.get(position).setZanNumber(Integer.parseInt(zan.getText().toString().trim()) + 1 + "");
                                        mList.get(position).setIsLike(1);
                                        break;
                                    case 1:
                                        mList.get(position).setZanNumber(Integer.parseInt(zan.getText().toString().trim()) - 1 + "");
                                        mList.get(position).setIsLike(0);
                                        break;
                                }
                                mAdapter.notifyItemChanged(position);
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

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<EvaluateBean>(mActivity, R.layout.item_warrant_detail_evaluate, mList) {
                @Override
                protected void convert(ViewHolder holder, final EvaluateBean evaluateBean, final int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_warrant_it_detail_evaluate_top);
                    Glide.with(mActivity)
                            .load(evaluateBean.getToUrl())
                            .into(topRIV);
                    TextView nameTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_name);
                    nameTV.setText(evaluateBean.getName());
                    TextView contentTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_content);
                    contentTV.setText(evaluateBean.getContent());
                    TextView timeTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_time);
                    timeTV.setText(evaluateBean.getTime());
                    TextView evaluateCountTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_count);
                    evaluateCountTV.setText(evaluateBean.getEvaluateNumber());
                    final TextView zanTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_zan_count);
                    zanTV.setText(evaluateBean.getZanNumber());
                    TextView sumTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_sum);
                    sumTV.setText("共" + evaluateBean.getEvaluateNumber() + "条评论");
                    ImageView trigIV = holder.getView(R.id.iv_item_warrant_detail_evaluate_trig);
                    switch (evaluateBean.getIsLike()) {
                        case 0:
                            zanTV.setCompoundDrawables(mZanGray, null, null, null);
                            break;
                        case 1:
                            zanTV.setCompoundDrawables(mZanRed, null, null, null);
                            break;

                    }
                    LinearLayout containerLL = holder.getView(R.id.ll_item_warrant_detail_evaluate_container);
                    int number = Integer.parseInt(evaluateBean.getEvaluateNumber());
                    if (number == 0) {
                        containerLL.setVisibility(View.GONE);
                        trigIV.setVisibility(View.GONE);
                    }
                    containerLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllReplyActivity.open(mActivity, evaluateBean.getId(), evaluateBean);
                        }
                    });
                    zanTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            zan(evaluateBean, position, zanTV);
                        }
                    });
                    evaluateCountTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllReplyActivity.open(mActivity, evaluateBean.getId(), evaluateBean);
                        }
                    });
                    TextView topTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_top);
                    TextView bottomTV = holder.getView(R.id.tv_item_warrant_detail_evaluate_bottom);
                    if (number == 1) {
                        topTV.setVisibility(View.VISIBLE);
                        bottomTV.setVisibility(View.GONE);
                        topTV.setText(evaluateBean.getList().get(0).getFromNAME() + " 回复 "
                                + evaluateBean.getList().get(0).getToName() + " " + evaluateBean.getList().get(0).getContent());
                    } else if (number >= 2) {
                        topTV.setVisibility(View.VISIBLE);
                        bottomTV.setVisibility(View.VISIBLE);
                        topTV.setText(evaluateBean.getList().get(0).getFromNAME() + " 回复 "
                                + evaluateBean.getList().get(0).getToName() + " " + evaluateBean.getList().get(0).getContent());
                        bottomTV.setText(evaluateBean.getList().get(1).getFromNAME() + " 回复 "
                                + evaluateBean.getList().get(1).getToName() + " " + evaluateBean.getList().get(1).getContent());
                    }

                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setAdapter(mAdapter);
            mRV.setLayoutManager(linearLayoutManager);

        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    private void showPopupWindow() {
        if (mPromptPopupWindow != null && mPromptPopupWindow.isShowing()) {
            mPromptPopupWindow.dismiss();
        } else {
            View view = mInflater.inflate(R.layout.pop_share_wechart, null);
            LinearLayout wechartLL = view.findViewById(R.id.ll_pop_share_wechart_share);
            LinearLayout circleLL = view.findViewById(R.id.ll_pop_share_circle);
            TextView cancelTV = view.findViewById(R.id.tv_pop_share_wechart_cancel);
            wechartLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            shareToWechart(mId);
                        }
                    }).start();
                }
            });
            circleLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            shareToFriends(mId);
                        }
                    }).start();

                }
            });
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPromptPopupWindow != null && mPromptPopupWindow.isShowing()) {
                        mPromptPopupWindow.dismiss();
                    }
                }
            });
            mPromptPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPromptPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }
    }


    private void shareToWechart(String releaseGoodsId) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = RequestManager.mBaseUrl + RequestManager.mInterfacePrefix + "v1/base/share?" + "userId="
                + AccountManager.sUserBean.getId() + "&" + "releaseGoodsId=" + releaseGoodsId + "&" + "isNew=" + "0";
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = mGoodTitle;
        message.description = mGoodContent;
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(new URL(mShareImageUrl).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 150, true);
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

    private void shareToFriends(String releaseGoodsId) {
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = RequestManager.mBaseUrl + RequestManager.mInterfacePrefix + "v1/base/share" + "?" + "userId="
                + AccountManager.sUserBean.getId() + "&" + "releaseGoodsId=" + releaseGoodsId + "&" + "isNew=" + "0";
        LogUtil.e(TAG, wxWebpageObject.webpageUrl);
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = mGoodTitle;
        message.description = mGoodContent;
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(new URL(mShareImageUrl).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 150, true);
        bmp.recycle();
        message.thumbData = Util.bmpToByteArray1(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        LocalApplication.api.sendReq(req);

    }

    private void getVideoPlayAuth(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("videoId", id);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getVideoPlayAuth(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {


                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateEvaluateList updateEvaluateList) {
        getWarrantItDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mActivity);
    }
}

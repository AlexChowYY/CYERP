package com.facewarrant.fw.ui.fragment.find;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.bean.FaceBean;
import com.facewarrant.fw.event.BackToHomePagerEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.view.GridSpacingItemDecoration;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class NewGoodsFragment extends BaseFragment {
    private static final String TAG = "NewGoodsFragment";
    @BindView(R.id.rv_fragment_new_goods)
    RecyclerView mRV;
    @BindView(R.id.ll_fragment_new_goods_none)
    LinearLayout mNoneLL;
    @BindView(R.id.tv_fragment_new_goods_more)
    TextView mMoreTV;
    private List<FaceBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<FaceBean> mAdapter;
    private Drawable isAttention;
    private Drawable noAttention;

    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_new_goods;

    }

    @Override
    public void initData() {
        mRV.setNestedScrollingEnabled(false);
        getFindList();
        isAttention = ContextCompat.getDrawable(mActivity, R.drawable.love_red);
        noAttention = ContextCompat.getDrawable(mActivity, R.drawable.love);
        isAttention.setBounds(0, 0, isAttention.getMinimumWidth(), isAttention.getMinimumHeight());
        noAttention.setBounds(0, 0, noAttention.getMinimumWidth(), noAttention.getMinimumHeight());
    }

    @Override
    public void initEvent() {
        mMoreTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new BackToHomePagerEvent());

            }
        });


    }

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_new_goods, mList) {
                @Override
                protected void convert(ViewHolder holder, FaceBean faceBean, int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_new_goods_top);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topRIV.getLayoutParams();
                    params.width = (DisplayUtil.getScreenWidth(mActivity) - (int) DisplayUtil.dpToPx(mActivity, 10) * 3) / 2;
                    params.height = (DisplayUtil.getScreenWidth(mActivity) - (int) DisplayUtil.dpToPx(mActivity, 10) * 3) / 2;
                    holder.setText(R.id.tv_item_new_goods_name, faceBean.getName());
                    holder.setText(R.id.tv_item_new_goods_favor, faceBean.getFavour());
                    ImageView playIV = holder.getView(R.id.iv_item_new_goods_play);
                    switch (faceBean.getGoodType()) {
                        case 0:
                            playIV.setVisibility(View.GONE);
                            break;
                        case 1:
                            playIV.setVisibility(View.VISIBLE);
                            break;
                    }
                    Glide.with(mActivity)
                            .load(faceBean.getGoodUrl())
                            .into(topRIV);


                }
            };
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
            mRV.setLayoutManager(gridLayoutManager);
            mRV.addItemDecoration(new GridSpacingItemDecoration(2, (int) DisplayUtil.dpToPx(mActivity, 10), true));
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void getFindList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getFindList(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                if (result.getInt("type") == 0 || result.getInt("type") == -1) {
                                    mNoneLL.setVisibility(View.VISIBLE);
                                    JSONArray faceList = result.getJSONArray("recommendFaceList");
                                    for (int i = 0; i < faceList.length(); i++) {
                                        JSONObject faceItem = faceList.getJSONObject(i);
                                        FaceBean faceBean = new FaceBean();
                                        faceBean.setId(faceItem.getString("userId"));
                                        faceBean.setName(faceItem.getString("trueName"));
                                        faceBean.setContent(faceItem.getString("standing"));
                                        faceBean.setIsAttention(faceItem.getString("isAttentioned"));
                                        faceBean.setLove(faceItem.getString("cnt"));
                                        faceBean.setTopUrl(faceItem.getString("portraitUrl"));
                                        mList.add(faceBean);

                                    }
                                    showRecommendRecyclerView();
                                } else if (result.getInt("type") == 1) {
                                    mNoneLL.setVisibility(View.GONE);
                                    JSONArray faceList = result.getJSONArray("faceGoodsList");
                                    for (int i = 0; i < faceList.length(); i++) {
                                        JSONObject faceItem = faceList.getJSONObject(i);
                                        JSONArray goodArray = faceItem.getJSONArray("releaseGoodsList");
                                        for (int j = 0; j < goodArray.length(); j++) {
                                            FaceBean faceBean = new FaceBean();
                                            JSONObject goodItem = goodArray.getJSONObject(j);
                                            faceBean.setId(faceItem.getString("faceId"));
                                            faceBean.setName(faceItem.getString("faceName"));
                                            faceBean.setGoodUrl(goodItem.getString("modelUrl"));
                                            faceBean.setGoodType(goodItem.getInt("modelType"));
                                            faceBean.setGoodId(goodItem.getString("releaseGoodsId"));
                                            faceBean.setFavour(faceItem.getString("favoriteCount"));
                                            mList.add(faceBean);
                                        }

                                    }
                                    showRecyclerView();
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


    private void showRecommendRecyclerView() {
        mAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_face, mList) {
            @Override
            protected void convert(ViewHolder holder, final FaceBean faceBean, final int position) {
                holder.setText(R.id.tv_item_face_name, faceBean.getName());
                holder.setText(R.id.tv_item_face_love, faceBean.getLove());
                holder.setText(R.id.tv_item_face_content, faceBean.getContent());
                final TextView attentionTV = holder.getView(R.id.tv_item_face_love);
                ImageView topIV = holder.getView(R.id.iv_item_face_top);
                TextView newNum = holder.getView(R.id.tv_item_face_new_num);
                if (faceBean.isHasNew()) {
                    newNum.setVisibility(View.VISIBLE);
                    newNum.setText(faceBean.getNewCount());
                } else {
                    newNum.setVisibility(View.GONE);
                }
                switch (faceBean.getIsAttention()) {
                    case "0":
                        attentionTV.setCompoundDrawables(noAttention, null, null, null);
                        break;
                    case "1":
                        attentionTV.setCompoundDrawables(isAttention, null, null, null);
                        break;

                }
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topIV.getLayoutParams();
                params.width = (DisplayUtil.getScreenWidth(mActivity) - 75) / 2;
                params.height = (DisplayUtil.getScreenWidth(mActivity) - 75) / 2;
                Glide.with(mActivity)
                        .load(faceBean.getTopUrl())
                        .into(topIV);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PersonalActivity.open(mActivity, faceBean.getId());
                    }
                });
                holder.setOnClickListener(R.id.tv_item_face_love, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAttention(faceBean.getId(), faceBean.getIsAttention(), attentionTV, position);
                    }
                });
            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        mRV.setLayoutManager(gridLayoutManager);
        mRV.addItemDecoration(new GridSpacingItemDecoration(2, 20, true));
        mRV.setAdapter(mAdapter);

    }

    private void getAttention(String id, final String attention, final TextView love, final int position) {
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
                                        love.setCompoundDrawables(isAttention, null, null, null);
                                        mList.get(position).setIsAttention("1");
                                        break;
                                    case "1":
                                        love.setCompoundDrawables(noAttention, null, null, null);
                                        mList.get(position).setIsAttention("0");
                                        break;

                                }
                                mAdapter.notifyDataSetChanged();
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

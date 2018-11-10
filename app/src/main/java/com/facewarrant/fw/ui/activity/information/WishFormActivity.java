package com.facewarrant.fw.ui.activity.information;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.LLSInterface;
import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.FaceMeBean;
import com.facewarrant.fw.bean.WishBean;
import com.facewarrant.fw.event.UpdateMesageEvent;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.mine.WarrantDetailActivity;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
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
public class WishFormActivity extends BaseActivity {
    private static final String TAG = "WishFormActivity";
    @BindView(R.id.rv_activity_wish_form)
    RecyclerView mRV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    private RecyclerCommonAdapter<WishBean> mAdapter;
    private List<WishBean> mList = new ArrayList<>();
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;
    private Drawable mAdd;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_wish_form;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.wish_data);
        mSettingTV.setText(R.string.all_read);
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);
        mAdd = ContextCompat.getDrawable(mActivity, R.drawable.add_follow);
        mAdd.setBounds(0, 0, mAdd.getMinimumWidth(), mAdd.getMinimumHeight());
        getInfoList();
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mPage = 1;
                mDataStatus = STATUS_REFRESH;
                getInfoList();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getInfoList();
            }
        });
        mSettingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readAllMessage();
            }
        });

    }

    private void readAllMessage() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("messageType", "CO");
        map.put("type", 1 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .deleteAllMessage(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mRefreshLayout.startRefresh();
                                ToastUtil.showShort(mActivity, "全部已读成功！");
                                EventBus.getDefault().post(new UpdateMesageEvent());
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
            mAdapter = new RecyclerCommonAdapter<WishBean>(mActivity, R.layout.item_wish_form, mList) {
                @Override
                protected void convert(ViewHolder holder, final WishBean wishBean, final int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_wish_form);
                    Glide.with(mActivity).load(wishBean.getTopUrl()).into(topRIV);
                    holder.setText(R.id.tv_item_wish_form_name, wishBean.getName());
                    holder.setText(R.id.tv_item_wish_form_time, wishBean.getTime());
                    holder.setText(R.id.tv_item_wish_form_content, "把我碑它的" + wishBean.getBrandName() + wishBean.getGoodsName() +
                            wishBean.getContent());
                    RoundedImageView picRIV = holder.getView(R.id.ri_item_wish_form_pic);
                    final TextView followTV = holder.getView(R.id.tv_item_wish_form_follow);
                    LinearLayout attentionLL = holder.getView(R.id.ll_item_wish_form_follow);
                    ImageView readIV = holder.getView(R.id.iv_item_wish_form_read);
                    switch (wishBean.getStatus()) {
                        case 0:
                            readIV.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            readIV.setVisibility(View.GONE);
                            break;
                    }
                    switch (wishBean.getAttention()) {
                        case 0:
                            followTV.setText(R.string.follow);
                            followTV.setCompoundDrawables(mAdd, null, null, null);
                            break;
                        case 1:
                            followTV.setText(R.string.followed);
                            followTV.setCompoundDrawables(null, null, null, null);
                            break;
                    }
                    switch (wishBean.getAttentionVisible()) {
                        case 0:
                            followTV.setVisibility(View.GONE);
                            attentionLL.setVisibility(View.GONE);
                            break;
                        case 1:
                            followTV.setVisibility(View.VISIBLE);
                            attentionLL.setVisibility(View.VISIBLE);
                            break;
                    }

                    followTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getAttention(wishBean.getFromUserId(), wishBean.getAttention() + "", position);
                        }
                    });


                    Glide.with(mActivity)
                            .load(wishBean.getPic())
                            .into(picRIV);
                    picRIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            readOneMessage(wishBean.getId(), wishBean.getGoodId(), wishBean.getFromUserId(), wishBean.getVId());
                        }
                    });

                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getAttention(String id, final String attention, final int position) {
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
                                        mList.get(position).setAttention(1);
                                        break;
                                    case "1":
                                        mList.get(position).setAttention(0);
                                        break;

                                }

                                mAdapter.notifyDataSetChanged();
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

    private void getInfoList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("messageType", "CO");
        map.put("page", mPage + "");
        map.put("rows", 15 + "");
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class).getInfoList(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
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
                        JSONObject result = data.getJSONObject("result");
                        int noReadCount = result.getInt("messagesCount");
                        if (noReadCount == 0) {
                            mSettingTV.setTextColor(ContextCompat.getColor(mActivity, R.color.colorFontHint));
                            mSettingTV.setClickable(false);
                        } else if (noReadCount > 0) {
                            mSettingTV.setTextColor(ContextCompat.getColor(mActivity, R.color.common_red));
                            mSettingTV.setClickable(true);
                        }
                        JSONArray List = result.getJSONArray("messagesResponseDtoList");
                        for (int i = 0; i < List.length(); i++) {
                            JSONObject resultItem = List.getJSONObject(i);
                            WishBean wishBean = new WishBean();
                            wishBean.setId(resultItem.getString("messageId"));
                            wishBean.setTopUrl(resultItem.getString("headUrl"));
                            wishBean.setTime(resultItem.getString("createTime"));
                            wishBean.setName(resultItem.getString("fromUser"));
                            wishBean.setGoodsName(resultItem.getString("releaseGoodsName"));
                            wishBean.setBrandName(resultItem.getString("brandName"));
                            wishBean.setPic(resultItem.getString("modelUrl"));
                            wishBean.setStatus(resultItem.getInt("status"));
                            wishBean.setAttention(resultItem.getInt("isAttention"));
                            wishBean.setFromUserId(resultItem.getString("fromUserId"));
                            wishBean.setGoodId(resultItem.getString("releaseGoodsId"));
                            wishBean.setVId(resultItem.getString("videoUrl"));
                            wishBean.setAttentionVisible(resultItem.getInt("hasAttention"));
                            mList.add(wishBean);
                        }
                        showRecyclerView();
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

    private void readOneMessage(String id, final String goodId, final String userId, final String vid) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("messageId", id);
        map.put("type", 1 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .readOneMessage(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mList.clear();
                                mPage = 1;
                                getInfoList();
                                WarrantDetailActivity.open(mActivity, goodId, userId, vid);
                                EventBus.getDefault().post(new UpdateMesageEvent());
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
}

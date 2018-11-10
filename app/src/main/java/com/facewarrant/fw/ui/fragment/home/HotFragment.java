package com.facewarrant.fw.ui.fragment.home;

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
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
public class HotFragment extends BaseFragment {
    private static final String TAG = "HotFragment";
    @BindView(R.id.rv_fragment_hot)
    RecyclerView mRV;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private RecyclerCommonAdapter<FaceBean> mAdapter;
    private List<FaceBean> mFaceList = new ArrayList<>();

    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;

    private Drawable isAttention;
    private Drawable noAttention;

    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_hot;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);
        getHomePagerData();
        isAttention = ContextCompat.getDrawable(mActivity, R.drawable.love_red);
        noAttention = ContextCompat.getDrawable(mActivity, R.drawable.love);
        isAttention.setBounds(0, 0, isAttention.getMinimumWidth(), isAttention.getMinimumHeight());
        noAttention.setBounds(0, 0, noAttention.getMinimumWidth(), noAttention.getMinimumHeight());

    }

    @Override
    public void initEvent() {

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mPage = 1;
                mDataStatus = STATUS_REFRESH;
                getHomePagerData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getHomePagerData();
            }
        });


    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_face, mFaceList) {
                @Override
                protected void convert(ViewHolder holder, final FaceBean faceBean, final int position) {
                    holder.setText(R.id.tv_item_face_name, faceBean.getName());
                    holder.setText(R.id.tv_item_face_love, faceBean.getLove());
                    holder.setText(R.id.tv_item_face_content, faceBean.getContent());
                    ImageView topIV = holder.getView(R.id.iv_item_face_top);
                    TextView newNum = holder.getView(R.id.tv_item_face_new_num);
                    final TextView attentionTV = holder.getView(R.id.tv_item_face_love);
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
                    params.width = (DisplayUtil.getScreenWidth(mActivity) - 60) / 2;
                    params.height = (DisplayUtil.getScreenWidth(mActivity) - 60) / 2;

                    Glide.with(mActivity)
                            .load(faceBean.getTopUrl())
                            .into(topIV);
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PersonalActivity.open(mActivity, faceBean.getId());
                        }
                    });
                    attentionTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getAttention(faceBean.getId(),
                                    faceBean.getIsAttention(), attentionTV, position,
                                    Integer.parseInt(faceBean.getLove()));
                        }
                    });
                }
            };
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
            mRV.setLayoutManager(gridLayoutManager);
            mRV.addItemDecoration(new GridSpacingItemDecoration(2, 20, true));
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getHomePagerData() {
        final Map<String, String> map = new HashMap<>();
        map.put("searchType", "H");
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", mPage + "");
        map.put("rows", 20 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getHomePagerData(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                switch (mDataStatus) {
                    case STATUS_REFRESH:
                        mRefreshLayout.finishRefreshing();
                        mFaceList.clear();
                        break;
                    case STATUS_LOAD:
                        mRefreshLayout.finishLoadmore();
                        break;
                }
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        JSONArray list = result.getJSONArray("faceLibraryInfoList");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject item = list.getJSONObject(i);
                            FaceBean faceBean = new FaceBean();
                            faceBean.setTopUrl(item.getString("portraitUrl"));
                            faceBean.setName(item.getString("trueName"));
                            faceBean.setContent(item.getString("standing"));
                            faceBean.setLove(item.getString("cnt"));
                            faceBean.setId(item.getString("userId"));
                            faceBean.setHasNew(item.getBoolean("hasNew"));
                            faceBean.setNewCount(item.getString("hasNewReleaseGoodsCount"));
                            faceBean.setIsAttention(item.getString("isAttentioned"));
                            mFaceList.add(faceBean);
                        }
                        showRecyclerView();

                    }  else if (data.getInt("resultCode") == 4003) {
                        ToastUtil.showShort(mActivity, "账号在别处登录，请重新登录！");
                        AccountManager.loginOut(mActivity);
                    }else {
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
                        mFaceList.clear();
                        break;
                    case STATUS_LOAD:
                        mRefreshLayout.finishLoadmore();
                        break;
                }

            }
        });
    }

    private void getAttention(String id, final String attention, final TextView love, final int position, final int number) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", id);
        map.put("isAttention", attention);
        LogUtil.e(TAG, map.toString());
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
                                        love.setText(number + 1 + "");
                                        mFaceList.get(position).setLove((number + 1) + "");
                                        mFaceList.get(position).setIsAttention("1");

                                        break;
                                    case "1":
                                        love.setCompoundDrawables(noAttention, null, null, null);
                                        mFaceList.get(position).setIsAttention("0");
                                        mFaceList.get(position).setLove((number - 1) + "");
                                        love.setText(number - 1 + "");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateFaceListEvent updateFaceListEvent) {
        mFaceList.clear();
        getHomePagerData();
    }


}

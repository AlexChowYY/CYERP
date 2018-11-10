package com.facewarrant.fw.ui.activity.mine;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.GoodsBean;
import com.facewarrant.fw.event.UpdateUserInfoEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.view.SpaceItemDecoration;
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
public class MyWarrantActivity extends BaseActivity {
    private static final String TAG = "MyWarrantActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;

    @BindView(R.id.rv_activity_my_warrant)
    RecyclerView mRV;

    private RecyclerCommonAdapter<GoodsBean> mAdapter;
    private List<GoodsBean> mList = new ArrayList<>();

    private boolean mEditable = false;
    private int mDeletePosition = -1;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;

    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_warrant;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.my_warrant);
        mSettingTV.setText(R.string.edit);
        mSettingTV.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlue));
        mSettingTV.setTextSize(14);
        getWarrantList();
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSettingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditable = !mEditable;
                if (mEditable) {
                    mSettingTV.setText(R.string.delete);
                } else {
                    mSettingTV.setText(R.string.edit);
                    if (mDeletePosition >= 0) {
                        cancelWarrant(mList.get(mDeletePosition).getId());
                    }
                }
                mAdapter.notifyDataSetChanged();
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
            mAdapter = new RecyclerCommonAdapter<GoodsBean>(mActivity, R.layout.item_my_warrant, mList) {
                @Override
                protected void convert(ViewHolder holder, final GoodsBean goodsBean, final int position) {
                    holder.setText(R.id.tv_item_my_warrant_name, goodsBean.getName());
                    holder.setText(R.id.tv_item_my_warrant_follow, goodsBean.getFaceNumber());
                    holder.setText(R.id.tv_item_my_warrant_time, goodsBean.getTime());
                    holder.setText(R.id.tv_item_my_warrant_car_number, goodsBean.getCarNumber());
                    RoundedImageView topIV = holder.getView(R.id.riv_item_my_warrant_top);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topIV.getLayoutParams();
                    params.height = (DisplayUtil.getScreenWidth(mContext) -
                            (int) DisplayUtil.dpToPx(mActivity, 30)) / 2 * goodsBean.getHeight() / goodsBean.getWidth();
                    Glide.with(mActivity).load(goodsBean.getTopUrl()).into(topIV);
                    final ImageView selectIV = holder.getView(R.id.iv_item_my_warrant_select);
                    if (mEditable) {
                        selectIV.setVisibility(View.VISIBLE);
                        if (goodsBean.isSelect()) {
                            selectIV.setImageResource(R.drawable.item_choose_red);
                        } else {
                            selectIV.setImageResource(R.drawable.item_choose_gray);
                        }

                    } else {
                        selectIV.setVisibility(View.GONE);
                    }
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mEditable) {
                                if (mDeletePosition != position) {
                                    ViewHolder holder1 = (ViewHolder) mRV.findViewHolderForLayoutPosition(mDeletePosition);
                                    if (holder1 != null) {
                                        ImageView selectIV = holder1.getView(R.id.iv_item_my_warrant_select);
                                        selectIV.setImageResource(R.drawable.item_choose_gray);
                                    } else {
                                        if (mDeletePosition != -1) {
                                            notifyItemChanged(mDeletePosition);
                                        }
                                    }
                                    if (mDeletePosition != -1) {
                                        mList.get(mDeletePosition).setSelect(false);
                                    }
                                    mDeletePosition = position;
                                    mList.get(position).setSelect(true);
                                    selectIV.setImageResource(R.drawable.item_choose_red);
                                } else {
                                    if (goodsBean.isSelect()) {
                                        selectIV.setImageResource(R.drawable.item_choose_gray);
                                        goodsBean.setSelect(false);
                                    } else {
                                        selectIV.setImageResource(R.drawable.item_choose_red);
                                        goodsBean.setSelect(true);
                                    }

                                }

                            } else {
                                WarrantDetailActivity.open(mActivity,goodsBean.getId());
                            }

                        }
                    });
                }
            };
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRV.addItemDecoration(new SpaceItemDecoration((int) DisplayUtil.dpToPx(mActivity, 10), 2));
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
                            goodsBean.setId(resultItem.getString("releaseGoodsId"));
                            goodsBean.setTopUrl(resultItem.getString("modelUrl"));
                            goodsBean.setWidth(resultItem.getInt("width"));
                            goodsBean.setHeight(resultItem.getInt("Height"));
                            goodsBean.setFaceNumber(resultItem.getString("favoriteCount"));
                            goodsBean.setCarNumber(resultItem.getString("buyNo"));
                            goodsBean.setTime(resultItem.getString("createTime"));
                            goodsBean.setName(resultItem.getString("goodsName"));
                            mList.add(goodsBean);
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
                        mList.clear();
                        break;
                    case STATUS_LOAD:
                        mRefreshLayout.finishLoadmore();
                        break;
                }

            }
        });
    }

    private void cancelWarrant(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("releaseGoodIds", id);
        map.put("userId", AccountManager.sUserBean.getId());

        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .cancelRelease(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mList.remove(mDeletePosition);
                                mAdapter.notifyDataSetChanged();
                                EventBus.getDefault().post(new UpdateUserInfoEvent());
                                ToastUtil.showShort(mActivity, "删除成功！");
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

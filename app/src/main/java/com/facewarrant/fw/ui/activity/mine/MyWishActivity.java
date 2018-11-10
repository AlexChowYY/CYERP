package com.facewarrant.fw.ui.activity.mine;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.facewarrant.fw.view.GridSpacingItemDecoration;
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
public class MyWishActivity extends BaseActivity {
    private static final String TAG = "MyWishActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.rv_activity_my_wish)
    RecyclerView mRV;
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;

    private PopupWindow mPopupWindow;
    private RecyclerCommonAdapter<GoodsBean> mAdapter;
    private List<GoodsBean> mList = new ArrayList<>();
    private boolean mEditable = false;
    private int mDeletePostion = -1;

    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;

    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_wish;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.my_wish);
        mSettingTV.setText(R.string.edit);
        mSettingTV.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlue));
        mSettingTV.setTextSize(14);
        //createData();
        //showRecyclerView();
        getCollectionList();
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
                    mAdapter.notifyDataSetChanged();
                } else {
                    if (mDeletePostion >= 0) {
                        showPopupWindow();
                    } else {
                        ToastUtil.showShort(mActivity, "未选择要删除的心愿单！");
                        mSettingTV.setText(R.string.edit);
                        mAdapter.notifyDataSetChanged();
                    }
                }

            }
        });

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mPage = 1;
                mDataStatus = STATUS_REFRESH;
                getCollectionList();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getCollectionList();
            }
        });


    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<GoodsBean>(mActivity, R.layout.item_my_wish, mList) {
                @Override
                protected void convert(final ViewHolder holder, final GoodsBean goodsBean, final int position) {
                    holder.setText(R.id.tv_item_my_wish_name, goodsBean.getName());
                    holder.setText(R.id.tv_item_my_wish_car_number, goodsBean.getCarNumber());
                    holder.setText(R.id.tv_item_my_wish_time, goodsBean.getTime());
                    holder.setText(R.id.tv_item_my_wish_smile, goodsBean.getFaceNumber());
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_my_wish_top);
                    Glide.with(mActivity)
                            .load(goodsBean.getTopUrl())
                            .into(topRIV);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topRIV.getLayoutParams();
                    params.height = (DisplayUtil.getScreenWidth(mActivity) - 60) / 2;
                    final ImageView selectIV = holder.getView(R.id.iv_item_my_wish_select);
                    if (mEditable) {
                        selectIV.setVisibility(View.VISIBLE);
                        if (goodsBean.isSelect()&&mDeletePostion>=0) {
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
                                if (mDeletePostion != position) {
                                    ViewHolder holder1 = (ViewHolder) mRV.findViewHolderForLayoutPosition(mDeletePostion);
                                    if (holder1 != null) {
                                        ImageView selectIV = holder1.getView(R.id.iv_item_my_wish_select);
                                        selectIV.setImageResource(R.drawable.item_choose_gray);
                                    } else {
                                        if (mDeletePostion >= 0) {
                                            notifyItemChanged(mDeletePostion);
                                        }
                                    }
                                    if (mDeletePostion >= 0) {
                                        mList.get(mDeletePostion).setSelect(false);
                                    }
                                    mDeletePostion = position;
                                    mList.get(position).setSelect(true);
                                    selectIV.setImageResource(R.drawable.item_choose_red);
                                } else {
                                    if (goodsBean.isSelect()) {
                                        selectIV.setImageResource(R.drawable.item_choose_gray);
                                        goodsBean.setSelect(false);
                                        mDeletePostion = -1;
                                    } else {
                                        selectIV.setImageResource(R.drawable.item_choose_red);
                                        goodsBean.setSelect(true);
                                        mDeletePostion = position;
                                    }

                                }

                            } else {
                                WarrantDetailActivity.open(mActivity, goodsBean.getId());
                            }

                        }
                    });
                }
            };
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
            mRV.setLayoutManager(gridLayoutManager);
            mRV.addItemDecoration(new GridSpacingItemDecoration(2, 20, true));
            mRV.setAdapter(mAdapter);
//            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//            mRV.addItemDecoration(new SpaceItemDecoration((int) DisplayUtil.dpToPx(mActivity, 10), 2));
//            mRV.setLayoutManager(staggeredGridLayoutManager);
//            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getCollectionList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", mPage + "");
        map.put("rows", 15 + "");
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getCollectionList(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
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
                        break;
                    case STATUS_LOAD:
                        mRefreshLayout.finishLoadmore();
                        break;
                }

            }
        });
    }

    private void collection(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("releaseGoodsId", id);
        map.put("isCollect", 1 + "");
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
                        mList.remove(mDeletePostion);
                        mAdapter.notifyDataSetChanged();
                        mDeletePostion = -1;
                        mSettingTV.setText(R.string.edit);
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

    private void showPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            View view = mInflater.inflate(R.layout.pop_delete_face_froup, null);
            TextView contentTV = view.findViewById(R.id.tv_content);
            TextView sureTV = view.findViewById(R.id.tv_pop_add_face_group_sure);
            TextView cancelTV = view.findViewById(R.id.tv_pop_add_face_group_cancel);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    mEditable = false;
                    mSettingTV.setText(R.string.edit);
                    mAdapter.notifyDataSetChanged();
                    mDeletePostion = -1;
                }
            });
            contentTV.setText(R.string.wish_data_tips);
            sureTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    collection(mList.get(mDeletePostion).getId());
                }
            });
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }

    }
}

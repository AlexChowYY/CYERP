package com.facewarrant.fw.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.bean.FaceBean;
import com.facewarrant.fw.event.UpdateSearchLibraryEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
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
public class SearchLibraryFragment extends BaseFragment {
    private static final String TAG = "SearchLibraryFragment";
    @BindView(R.id.rv_fragment_search_library)
    RecyclerView mRV;

    private String mKeyword;
    private String mId;
    private String mBId;


    private RecyclerCommonAdapter<FaceBean> mAdapter;
    private List<FaceBean> mFaceList = new ArrayList<>();

    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_search_library;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            mId = getArguments().getString(Constant.ID);
            mKeyword = getArguments().getString(Constant.KEYWORD);
            mBId = getArguments().getString(Constant.CODE);

            getData(mKeyword);
        }
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);

    }

    @Override
    public void initEvent() {
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mPage = 1;
                mDataStatus = STATUS_REFRESH;
                getData(mKeyword);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getData(mKeyword);
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
                    final TextView attentionTV = holder.getView(R.id.tv_item_face_love);
                    ImageView topIV = holder.getView(R.id.iv_item_face_top);
                    TextView newNum = holder.getView(R.id.tv_item_face_new_num);
                    if (faceBean.isHasNew()) {
                        newNum.setVisibility(View.VISIBLE);
                        newNum.setText(faceBean.getNewCount());
                    } else {
                        newNum.setVisibility(View.GONE);
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

    private void getData(String keyword) {
        final Map<String, String> map = new HashMap<>();
        map.put("groupsId", mId);
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("searchCondition", keyword);
        map.put("brandId", mBId);
        map.put("page", mPage + "");
        map.put("rows", 25 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getSearchMyFaceLibraryList(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
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
                        JSONArray list = data.getJSONArray("result");
                        mFaceList.clear();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject item = list.getJSONObject(i);
                            FaceBean faceBean = new FaceBean();
                            faceBean.setTopUrl(item.getString("headUrl"));
                            faceBean.setName(item.getString("faceName"));
                            faceBean.setContent(item.getString("standing"));
                            faceBean.setLove(item.getString("fansCount"));
                            faceBean.setId(item.getString("faceId"));
                            mFaceList.add(faceBean);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateSearchLibraryEvent updateSearchLibraryEvent) {
        mBId = updateSearchLibraryEvent.getBId();
        if (!TextUtils.isEmpty(updateSearchLibraryEvent.getKeywords())) {
            mKeyword = updateSearchLibraryEvent.getKeywords();
        }
        mRefreshLayout.startRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

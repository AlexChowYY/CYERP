package com.facewarrant.fw.ui.fragment.faceLibrary;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.MultiItemTypeAdapter;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.bean.FaceLibraryBean;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.event.UpdateGroupMemberEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.view.SDAvatarListLayout;
import com.facewarrant.fw.view.SDCircleImageView;
import com.facewarrant.fw.view.SpacesItemDecoration;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FaceClassifyFragment extends BaseFragment {
    private static final String TAG = "FaceClassifyFragment";
    @BindView(R.id.rv_fragment_face_classify)
    RecyclerView mRV;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private List<FaceLibraryBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<FaceLibraryBean> mAdapter;
    private String mId;


    private Drawable isAttention;
    private Drawable noAttention;

    private int fromPosition;
    private int toPosition;

    private ItemTouchHelper mItemTouchHelper;
    private boolean mIS;

    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_face_classify;

    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            mId = getArguments().getString(Constant.ID);
            getMyLibrary();
        }
        isAttention = ContextCompat.getDrawable(mActivity, R.drawable.love_red);
        noAttention = ContextCompat.getDrawable(mActivity, R.drawable.love);
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
                getMyLibrary();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getMyLibrary();
            }
        });

    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<FaceLibraryBean>(mActivity, R.layout.item_face_library, mList) {
                @Override
                protected void convert(ViewHolder holder, final FaceLibraryBean faceLibraryBean, int position) {
                    holder.setText(R.id.tv_item_face_library_name, faceLibraryBean.getName());
                    holder.setText(R.id.tv_item_face_library_warrant_it, faceLibraryBean.getWarrantIt());
                    holder.setText(R.id.tv_item_face_library_fans, faceLibraryBean.getFans());
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_face_library_top);
                    Glide.with(mActivity).load(faceLibraryBean.getTopUrl()).into(topRIV);
                    SDAvatarListLayout sdAvatarListLayout = holder.getView(R.id.sd_item_face_library);
                    TextView newCount = holder.getView(R.id.tv_item_face_new_num);
                    if (faceLibraryBean.getCount() > 0) {
                        newCount.setVisibility(View.VISIBLE);
                        if (faceLibraryBean.getCount() < 10) {
                            newCount.setBackgroundResource(R.drawable.shape_circle_red_15);
                            newCount.setText(faceLibraryBean.getCount() + "");
                        } else {
                            newCount.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                            if (faceLibraryBean.getCount() > 99) {
                                newCount.setText("99+");
                            } else {
                                newCount.setText(faceLibraryBean.getCount() + "");
                            }
                        }
                    }else {
                        newCount.setVisibility(View.GONE);
                    }
                    final List<String> imageData = new ArrayList<>();
                    for (int j = 0; j < faceLibraryBean.getGoodList().size(); j++) {
                        imageData.add(faceLibraryBean.getGoodList().get(j).getTopUrl());
                    }
                    sdAvatarListLayout.setAvatarListListener(new SDAvatarListLayout.ShowAvatarListener() {
                        @Override
                        public void showImageView(List<SDCircleImageView> imageViewList) {
                            int imageSize = imageViewList.size();
                            //实际需要显示的图片的数量
                            int realDataSize = imageData.size();
                            int mul = imageSize - realDataSize;
                            for (int i = 0; i < imageSize; i++) {
                                if (i >= mul) {//6
                                    //可以替换为网络图片处理
                                    Glide.with(mActivity).load(imageData.get(realDataSize - (i - mul) - 1)).into(imageViewList.get(i));
                                    imageViewList.get(i).setVisibility(View.VISIBLE);
                                } else {
                                    imageViewList.get(i).setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PersonalActivity.open(mActivity, faceLibraryBean.getId());
                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mItemTouchHelper = new ItemTouchHelper(mOnPositionChangeListener);
            mItemTouchHelper.attachToRecyclerView(mRV);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(new SpacesItemDecoration(15));
            mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    fromPosition = position;
                    return true;
                }
            });
        } else {
            mAdapter.notifyDataSetChanged();

        }
    }

    private void getMyLibrary() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("groupsId", mId);
        map.put("page", mPage + "");
        map.put("rows", 15 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyFaceLibrary(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
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
                                    FaceLibraryBean faceLibraryBean = new FaceLibraryBean();
                                    faceLibraryBean.setFans(resultItem.getString("fansCount"));
                                    faceLibraryBean.setTopUrl(resultItem.getString("headUrl"));
                                    faceLibraryBean.setName(resultItem.getString("faceName"));
                                    faceLibraryBean.setId(resultItem.getString("faceId"));
                                    faceLibraryBean.setWarrantIt(resultItem.getString("releaseGoodsCount"));
                                    faceLibraryBean.setIndex(resultItem.getInt("userIndex"));
                                    faceLibraryBean.setCount(resultItem.getInt("newReleaseGoodsCount"));
                                    JSONArray goodList = resultItem.getJSONArray("releaseGoodsList");
                                    List<FaceLibraryBean.Good> list = new ArrayList<>();
                                    for (int i1 = 0; i1 < goodList.length(); i1++) {
                                        FaceLibraryBean.Good good = new FaceLibraryBean.Good();
                                        JSONObject goodItem = goodList.getJSONObject(i1);
                                        good.setId(goodItem.getString("releaseGoodsId"));
                                        good.setTopUrl(goodItem.getString("modelUrl"));
                                        list.add(good);
                                    }
                                    faceLibraryBean.setGoodList(list);
                                    mList.add(faceLibraryBean);
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

    ItemTouchHelper.Callback mOnPositionChangeListener = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            toPosition = target.getAdapterPosition();
            mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            //注意这里有个坑的，itemView 都移动了，对应的数据也要移动


            return false;
        }


        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


        }

        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            //return true后，可以实现长按拖动排序和拖动动画了
            return true;
        }


        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            if (toPosition >= 0 && fromPosition >= 0 && (toPosition != fromPosition)) {
                changPosition(mList.get(fromPosition).getIndex(), mList.get(toPosition).getIndex());
            }
            toPosition = -1;
            fromPosition = -1;
        }
    };

    private void changPosition(final int fromPosition, final int endPosition) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("groupsId", mId);
        map.put("faceId", mList.get(this.fromPosition).getId());
        map.put("beforeIndex", fromPosition + "");
        map.put("afterIndex", endPosition + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .changePosition(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());

                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                //注意这里有个坑的，itemView 都移动了，对应的数据也要移动
                                mRefreshLayout.startRefresh();

                            } else {
                                mAdapter.notifyDataSetChanged();
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
    public void onEvent(UpdateFaceListEvent event) {
        //处理逻辑
        mRefreshLayout.startRefresh();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateGroupMemberEvent event) {
        //处理逻辑
        mRefreshLayout.startRefresh();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}



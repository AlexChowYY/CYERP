package com.facewarrant.fw.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.FaceBean;
import com.facewarrant.fw.event.UpdateSearchHistoryEvent;
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
public class SearchResultActivity extends BaseActivity {
    private static final String TAG = "SearchResultActivity";
    @BindView(R.id.rv_activity_search_result)
    RecyclerView mRV;
    @BindView(R.id.et_activity_search_result)
    EditText mSearchET;
    @BindView(R.id.tv_activity_search_cancel)
    TextView mCancelTV;
    @BindView(R.id.iv_activity_search_result_clear)
    ImageView mClearIV;
    @BindView(R.id.iv_activity_search_result)
    ImageView mSearchIV;

    private List<FaceBean> mFaceList = new ArrayList<>();
    private RecyclerCommonAdapter<FaceBean> mAdapter;
    private String mKeyword;

    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_search_result;
    }

    @Override
    public void initData() {
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mKeyword = getIntent().getExtras().getString(Constant.SEARCH_KEYWORD);
                mSearchET.setText(mKeyword);
                mSearchET.setSelection(mKeyword.length());
                getData();
            }
        }
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);
    }

    @Override
    public void initEvent() {
        mCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mSearchET.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    ToastUtil.showShort(mActivity, "请听写关键字");
                } else {
                    mKeyword = keyword;
                    mRefreshLayout.startRefresh();
                }
            }
        });
        mSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = mSearchET.getText().toString().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        ToastUtil.showShort(mActivity, "请填写关键字！");
                    } else {
                        mKeyword = keyword;
                        mRefreshLayout.startRefresh();
                    }
                    return true;
                }
                return false;
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                mPage = 1;
                mDataStatus = STATUS_REFRESH;
                getData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getData();
            }
        });

    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_face, mFaceList) {
                @Override
                protected void convert(ViewHolder holder, final FaceBean faceBean, int position) {
                    holder.setText(R.id.tv_item_face_name, faceBean.getName());
                    holder.setText(R.id.tv_item_face_love, faceBean.getLove());
                    holder.setText(R.id.tv_item_face_content, faceBean.getContent());
                    ImageView topIV = holder.getView(R.id.iv_item_face_top);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topIV.getLayoutParams();
                    params.width = (DisplayUtil.getScreenWidth(mActivity) - 60) / 2;
                    params.height = (DisplayUtil.getScreenWidth(mActivity) - 60) / 2;
                    Glide.with(mActivity)
                            .load(faceBean.getTopUrl())
                            .into(topIV);
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PersonalActivity.searchOpen(mActivity, faceBean.getId(), mKeyword);
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

    public static void open(Activity activity, String keyWord) {
        Intent intent = new Intent(activity, SearchResultActivity.class);
        intent.putExtra(Constant.SEARCH_KEYWORD, keyWord);
        activity.startActivity(intent);
    }

    private void getData() {
        final Map<String, String> map = new HashMap<>();
        map.put("searchType", "H");
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("memberClass", "");
        map.put("condition", mKeyword);
        map.put("page", mPage + "");
        map.put("rows", 25 + "");
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
                        EventBus.getDefault().post(new UpdateSearchHistoryEvent());
                        JSONObject result = data.getJSONObject("result");
                        if (!result.getString("faceLibraryInfoList").equals("null")) {
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
}

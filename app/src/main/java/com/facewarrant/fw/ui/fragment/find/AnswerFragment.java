package com.facewarrant.fw.ui.fragment.find;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.bean.QuestionBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.mine.AnswerDetailActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.makeramen.roundedimageview.RoundedImageView;

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
public class AnswerFragment extends BaseFragment {
    private static final String TAG = "AnswerFragment";
    @BindView(R.id.rv_fragment_answer)
    RecyclerView mRV;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private List<QuestionBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<QuestionBean> mAdapter;

    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_answer;
    }

    @Override
    public void initData() {
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);
        getInfoList();

    }

    @Override
    public void initEvent() {
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
    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<QuestionBean>(mActivity, R.layout.item_answer, mList) {
                @Override
                protected void convert(ViewHolder holder, final QuestionBean questionBean, int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_answer);
                    Glide.with(mActivity)
                            .load(questionBean.getTopUrl())
                            .into(topRIV);
                    holder.setText(R.id.tv_item_answer_name, questionBean.getName());
                    holder.setText(R.id.tv_item_answer_time, questionBean.getTime());
                    holder.setText(R.id.tv_item_answer_content, questionBean.getContent());
                    TextView answerTV = holder.getView(R.id.tv_item_answer_answer);
                    answerTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AnswerDetailActivity.open(mActivity, questionBean.getId());
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

    private void getInfoList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("messageType", "Q");
        map.put("page", mPage + "");
        map.put("rows", 15 + "");
        LogUtil.e(TAG, map.toString());
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
                        if (!data.getString("result").equals("null")) {
                            JSONObject result = data.getJSONObject("result");
                            int noReadCount = result.getInt("messagesCount");
                            JSONArray messageResponseDtoList = result.getJSONArray("messagesResponseDtoList");
                            for (int i = 0; i < messageResponseDtoList.length(); i++) {
                                JSONObject questionItem = messageResponseDtoList.getJSONObject(i);
                                QuestionBean questionBean = new QuestionBean();
                                questionBean.setId(questionItem.getString("questionId"));
                                questionBean.setTime(questionItem.getString("createTime"));
                                questionBean.setTopUrl(questionItem.getString("headUrl"));
                                questionBean.setContent(questionItem.getString("questionContent"));
                                questionBean.setQid(questionItem.getString("fromUserId"));
                                mList.add(questionBean);
                            }
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


}

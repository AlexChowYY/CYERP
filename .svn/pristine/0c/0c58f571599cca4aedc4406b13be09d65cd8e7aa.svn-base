package com.facewarrant.fw.ui.activity.information;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.AnswerMeBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
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
public class AnswerMeActivity extends BaseActivity {
    private static final String TAG = "AnswerMeActivity";

    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;

    @BindView(R.id.rv_activity_answer_me)
    RecyclerView mRV;
    private RecyclerCommonAdapter<AnswerMeBean> mAdapter;

    private List<AnswerMeBean> mList = new ArrayList<>();

    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_answer_me;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.answer_me);
        getInfoList();
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
            mAdapter = new RecyclerCommonAdapter<AnswerMeBean>(mActivity, R.layout.item_answer_me, mList) {
                @Override
                protected void convert(ViewHolder holder, AnswerMeBean answerMeBean, int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_answer_me);
                    Glide.with(mActivity).load(answerMeBean.getTopUrl()).into(topRIV);
                    holder.setText(R.id.tv_item_answer_me_name, answerMeBean.getName());
                    holder.setText(R.id.tv_item_answer_me_time, answerMeBean.getTime());
                    holder.setText(R.id.tv_item_answer_me_content, answerMeBean.getContent());
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
        map.put("messageType", "A");
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
                        JSONArray messageResponseDtoList = result.getJSONArray("messagesResponseDtoList");
                        for (int i = 0; i < messageResponseDtoList.length(); i++) {
                            JSONObject resultItem = messageResponseDtoList.getJSONObject(i);
                            AnswerMeBean answerMeBean = new AnswerMeBean();
                            answerMeBean.setId(resultItem.getString("messageId"));
                            answerMeBean.setTopUrl(resultItem.getString("headUrl"));
                            answerMeBean.setTime(resultItem.getString("createTime"));
                            answerMeBean.setName(resultItem.getString("fromUser"));
                            answerMeBean.setContent(resultItem.getString("answerContent"));
                            mList.add(answerMeBean);
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

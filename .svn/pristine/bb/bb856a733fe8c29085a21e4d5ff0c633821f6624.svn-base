package com.facewarrant.fw.ui.activity.mine;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.MyAnswerBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

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
public class MyAnswerActivity extends BaseActivity {
    private static final String TAG = "MyAnswerActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.rv_activity_my_answer)
    RecyclerView mRV;
    private RecyclerCommonAdapter<MyAnswerBean> mAdapter;
    private List<MyAnswerBean> mList = new ArrayList<>();


    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_answer;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.my_answer);
        getAnswerList();

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<MyAnswerBean>(mActivity, R.layout.item_my_answer, mList) {
                @Override
                protected void convert(ViewHolder holder, final MyAnswerBean myAnswerBean, int position) {
                    holder.setText(R.id.tv_item_my_answer_content, myAnswerBean.getAnswerContent());
                    holder.setText(R.id.tv_item_my_answer_time, myAnswerBean.getTime());
                    holder.setText(R.id.tv_item_my_answer_question_content, myAnswerBean.getQuestionContent());
                    ImageView deleteIV = holder.getView(R.id.iv_item_my_answer);
                    deleteIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteAnswer(myAnswerBean.getId());

                        }
                    });
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AnswerDetailActivity.open(mActivity, myAnswerBean.getQId());
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

    private void getAnswerList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", 1 + "");
        map.put("rows", 15 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyAnswerList(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mList.clear();
                                JSONArray questionArray = data.getJSONArray("result");
                                for (int i = 0; i < questionArray.length(); i++) {
                                    MyAnswerBean myAnswerBean = new MyAnswerBean();
                                    JSONObject myAnswerItem = questionArray.getJSONObject(i);
                                    myAnswerBean.setId(myAnswerItem.getString("answerId"));
                                    myAnswerBean.setAnswerContent(myAnswerItem.getString("questionContent"));
                                    myAnswerBean.setQuestionContent(myAnswerItem.getString("questionContent"));
                                    myAnswerBean.setAnswerContent(myAnswerItem.getString("answerContent"));
                                    myAnswerBean.setAnswerType(myAnswerItem.getInt("answerType"));
                                    myAnswerBean.setTime(myAnswerItem.getString("answerTime"));
                                    myAnswerBean.setQId(myAnswerItem.getString("questionId"));
                                    mList.add(myAnswerBean);
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

                    }
                });
    }

    private void deleteAnswer(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("answerId", id);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .deleteMyAnswer(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                ToastUtil.showShort(mActivity, "删除成功！");
                                getAnswerList();


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

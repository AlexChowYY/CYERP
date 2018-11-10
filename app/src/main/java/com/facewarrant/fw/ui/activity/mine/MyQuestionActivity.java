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
import com.facewarrant.fw.bean.MyQuestionBean;
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
public class MyQuestionActivity extends BaseActivity {
    private static final String TAG = "MyQuestionActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.rv_activity_my_question)
    RecyclerView mRV;
    private RecyclerCommonAdapter<MyQuestionBean> mAdapter;
    private List<MyQuestionBean> mList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_question;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.my_question);
        getQuestionList();


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

    private void getQuestionList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", 1 + "");
        map.put("rows", 15 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyQuestionList(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONArray questionArray = data.getJSONArray("result");
                                for (int i = 0; i < questionArray.length(); i++) {
                                    JSONObject questionItem = questionArray.getJSONObject(i);
                                    MyQuestionBean myQuestionBean = new MyQuestionBean();
                                    myQuestionBean.setId(questionItem.getString("questionId"));
                                    myQuestionBean.setContent(questionItem.getString("questionContent"));
                                    myQuestionBean.setCount(questionItem.getString("answerCount"));
                                    myQuestionBean.setTime(questionItem.getString("createTime"));
                                    mList.add(myQuestionBean);
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

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<MyQuestionBean>(mActivity, R.layout.item_my_question, mList) {
                @Override
                protected void convert(ViewHolder holder, final MyQuestionBean myQuestionBean, int position) {
//                    holder.setText(R.id.tv_item_my_question_content, myQuestionBean.getContent());
//                    holder.setText(R.id.tv_item_my_question_answer_count, myQuestionBean.getCount() + "回答");
//                    holder.setText(R.id.tv_item_mt_question_time, myQuestionBean.getTime());
//                    //final SwipeLayout rootSL = holder.getView(R.id.sl_item_my_question);
//
//                    ImageView deleteIV = holder.getView(R.id.iv_item_my_question_delete);
//                    deleteIV.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                           // rootSL.close();
//                            deleteQuestion(myQuestionBean.getId());
//
//
//                        }
//                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteQuestion(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("questionId", id);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .deleteMyQuestion(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                ToastUtil.showShort(mActivity, "删除成功！");
                                getQuestionList();


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

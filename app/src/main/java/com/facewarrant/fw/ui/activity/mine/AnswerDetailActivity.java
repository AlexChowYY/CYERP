package com.facewarrant.fw.ui.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.AnswerDetailBean;
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.bean.GoodsBean;
import com.facewarrant.fw.bean.QuestionBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
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
public class AnswerDetailActivity extends BaseActivity {
    private static final String TAG = "AnswerDetailActivity";
    @BindView(R.id.rv_activity_answer_detail)
    RecyclerView mRV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.riv_activity_answer_detail_top)
    RoundedImageView mTopRIV;
    @BindView(R.id.tv_activity_answer_detail_question_name)
    TextView mNameTV;
    @BindView(R.id.tv_activity_answer_detail_question_time)
    TextView mTimeTV;
    @BindView(R.id.tv_activity_answer_detail_question_content)
    TextView mContentTV;
    @BindView(R.id.tv_activity_answer_detail_count)
    TextView mCountTV;
    @BindView(R.id.tv_activity_answer_detail_send)
    TextView mSendTV;
    @BindView(R.id.et_activity_answer_detail_answer)
    EditText mAnswerET;


    private String mId;
    private List<AnswerDetailBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<AnswerDetailBean> mAdapter;

    private String mQuestionUserId;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_answer_detail;
    }

    @Override
    public void initData() {
        mRV.setNestedScrollingEnabled(false);
        mTitleTV.setText(R.string.answer_detail);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            getDetail();
        }

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = mAnswerET.getText().toString().trim();
                if (TextUtils.isEmpty(answer)) {
                    ToastUtil.showShort(mActivity, "回答不能为空");
                } else {
                    answer(answer);
                }

            }
        });

    }

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<AnswerDetailBean>(mActivity, R.layout.item_answer_detail, mList) {
                @Override
                protected void convert(ViewHolder holder, AnswerDetailBean answerDetailBean, int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_activity_answer_detail_top);
                    Glide.with(mActivity).load(answerDetailBean.getTopUrl()).into(topRIV);
                    holder.setText(R.id.tv_item_answer_detail_question_name, answerDetailBean.getName());
                    holder.setText(R.id.tv_item_answer_detail_question_time, answerDetailBean.getTime());
                    holder.setText(R.id.tv_item_answer_detail_question_content, answerDetailBean.getContent());
                    LinearLayout containerLL = holder.getView(R.id.ll_item_answer_detail_container);
                    if (answerDetailBean.getList() != null) {
                        for (int i = 0; i < answerDetailBean.getList().size(); i++) {
                            ImageView imageView = new ImageView(mActivity);
                            LinearLayout.LayoutParams params = new
                                    LinearLayout.LayoutParams((int) DisplayUtil.dpToPx(mActivity, 95),
                                    (int) DisplayUtil.dpToPx(mActivity, 95));
                            imageView.setLayoutParams(params);
                            Glide.with(mActivity).load(answerDetailBean.getList().get(i).getTopUrl()).into(imageView);
                            containerLL.addView(imageView);
                        }

                    }


                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getDetail() {
        final Map<String, String> map = new HashMap<>();
        map.put("questionId", mId);
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", 1 + "");
        map.put("rows", 15 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getAnswerDeatil(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                Glide.with(mActivity).load(result.getString("headUrl")).into(mTopRIV);
                                mNameTV.setText(result.getString("questionUser"));
                                mTimeTV.setText(result.getString("createTime"));
                                mContentTV.setText(result.getString("questionContent"));
                                mQuestionUserId = result.getString("questionUserId");
                                mCountTV.setText("全部回答（" + result.getString("answerCount") + "）");
                                if (result.getInt("answerCount") > 0) {
                                    mList.clear();
                                    JSONArray answerArray = result.getJSONArray("answerInfoList");
                                    for (int i = 0; i < answerArray.length(); i++) {
                                        JSONObject answerItem = answerArray.getJSONObject(i);
                                        AnswerDetailBean answerDetailBean = new AnswerDetailBean();
                                        answerDetailBean.setName(answerItem.getString("answerUser"));
                                        answerDetailBean.setContent(answerItem.getString("answerContent"));
                                        answerDetailBean.setTime(answerItem.getString("answerTime"));
                                        answerDetailBean.setTopUrl(answerItem.getString("headUrl"));

                                        if (!answerItem.getString("releaseGoodsDtoList").equals("null")) {
                                            JSONArray goodsArray = answerItem.getJSONArray("releaseGoodsDtoList");
                                            List<GoodsBean> list = new ArrayList<>();
                                            for (int j = 0; j < goodsArray.length(); j++) {
                                                GoodsBean goodsBean = new GoodsBean();
                                                JSONObject goodItem = goodsArray.getJSONObject(j);
                                                goodsBean.setId(goodItem.getString("releaseGoodsId"));
                                                goodsBean.setTopUrl(goodItem.getString("modelUrl"));
                                                goodsBean.setType(goodItem.getInt("modelType"));
                                                list.add(goodsBean);
                                            }
                                            answerDetailBean.setList(list);

                                        }
                                        mList.add(answerDetailBean);
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

                    }
                });
    }

    public static void open(Activity activity, String id) {
        Intent intent = new Intent(activity, AnswerDetailActivity.class);
        intent.putExtra(Constant.ID, id);
        activity.startActivity(intent);
    }

    private void answer(String content) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("questionId", mId);
        map.put("toUserId", mQuestionUserId);
        map.put("answerType", 0 + "");
        map.put("answerContent", content);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .answer(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        getDetail();
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

}

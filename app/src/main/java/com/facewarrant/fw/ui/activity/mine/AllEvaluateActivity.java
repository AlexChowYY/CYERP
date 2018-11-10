package com.facewarrant.fw.ui.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.EvaluateBean;
import com.facewarrant.fw.event.UpdateEvaluateList;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class AllEvaluateActivity extends BaseActivity {
    private static final String TAG = "AllEvaluateActivity";
    @BindView(R.id.rv_activity_all_evaluate)
    RecyclerView mRV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.riv_activity_all_evaluate)
    RoundedImageView mHeadRIV;
    @BindView(R.id.et_activity_all_evaluate)
    EditText mEvaluateET;

    private List<EvaluateBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<EvaluateBean> mAdapter;
    private String mId;

    private Drawable mZanRed;
    private Drawable mZanGray;

    private String mFId;

    private EvaluateBean.EvaluateEvaluate mEvaluateEvaluate;
    private EvaluateBean mEvaluateBean;
    private int mReplyType;
    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;
    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_all_evaluate;

    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.all_evaluate);
        mRV.setNestedScrollingEnabled(false);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mFId = getIntent().getExtras().getString(Constant.FACE_ID);
            getAllComments();
        }
        mZanRed = ContextCompat.getDrawable(mActivity, R.drawable.zan_red);
        mZanRed.setBounds(0, 0, mZanRed.getMinimumWidth(), mZanRed.getMinimumHeight());
        mZanGray = ContextCompat.getDrawable(mActivity, R.drawable.zan_gray);
        mZanGray.setBounds(0, 0, mZanGray.getMinimumWidth(), mZanGray.getMinimumHeight());
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);
        EventBus.getDefault().register(mActivity);
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //监听软键盘回车键
        mEvaluateET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = mEvaluateET.getText().toString().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtil.showShort(mActivity, "请填写评论！");
                    } else {
                        comment(content);
//                        switch (mReplyType) {
//                            case 2:
//                       case 1:
////                                reply(mReplyType, mEvaluateBean.getId(), mEvaluateBean.getUserId(), mEvaluateBean.getId(), content);
////                                        reply(mReplyType, mEvaluateEvaluate.getId(), mEvaluateEvaluate.getFromId(), mEvaluateBean.getId(), content);
//                                break;
//                             break;
//                            default:
//                                comment(content);
//                                break;
//                        }


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
                getAllComments();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getAllComments();
            }
        });

    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<EvaluateBean>(mActivity, R.layout.item_all_evaluate, mList) {
                @Override
                protected void convert(ViewHolder holder, final EvaluateBean evaluateBean, final int position) {
                    holder.setText(R.id.tv_item_all_evaluate_name, evaluateBean.getName());
                    holder.setText(R.id.tv_item_all_evaluate_content, evaluateBean.getContent());
                    holder.setText(R.id.tv_item_all_evaluate_time, evaluateBean.getTime());
                    TextView evaluateTV = holder.getView(R.id.tv_item_all_evaluate_evaluate);
                    holder.setText(R.id.tv_item_all_evaluate_evaluate, evaluateBean.getEvaluateNumber());
                    holder.setText(R.id.tv_item_all_evaluate_zan, evaluateBean.getZanNumber());
                    ImageView trigIV = holder.getView(R.id.iv_trig);
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_all_evaluate);
                    Glide.with(mActivity).load(evaluateBean.getToUrl()).into(topRIV);
                    final TextView expandTV = holder.getView(R.id.tv_item_all_evaluate_expand);
                    final TextView zanTopTV = holder.getView(R.id.tv_item_all_evaluate_zan);
                    zanTopTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            zanTopTV.setClickable(false);
                            zan(evaluateBean, position, 0, zanTopTV, "2");
                        }
                    });
                    switch (evaluateBean.getIsLike()) {
                        case 0:
                            zanTopTV.setCompoundDrawables(mZanGray, null, null, null);
                            break;
                        case 1:
                            zanTopTV.setCompoundDrawables(mZanRed, null, null, null);
                            break;
                    }
                    evaluateTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllReplyActivity.open(mActivity, evaluateBean.getId(), evaluateBean);
                        }
                    });

                    LinearLayout cantainerLL = holder.getView(R.id.ll_item_all_evaluate_container);
                    cantainerLL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllReplyActivity.open(mActivity, evaluateBean.getId(), evaluateBean);
                        }
                    });
                    int number = evaluateBean.getReplyCount();
                    if (number == 0) {
                        cantainerLL.setVisibility(View.GONE);
                        trigIV.setVisibility(View.GONE);
                    }
                    if (number > 2) {
                        cantainerLL.setVisibility(View.VISIBLE);
                        trigIV.setVisibility(View.VISIBLE);
                        expandTV.setVisibility(View.VISIBLE);
                        expandTV.setText("展开" + evaluateBean.getEvaluateNumber() + "条回复");
                    } else if (number <= 2 && number > 0) {
                        expandTV.setVisibility(View.GONE);
                        cantainerLL.setVisibility(View.VISIBLE);
                        trigIV.setVisibility(View.VISIBLE);
                    }
                    TextView topTV = holder.getView(R.id.tv_item_all_evaluate_top);
                    TextView bottomTV = holder.getView(R.id.tv_item_all_evaluate_bottom);

                    if (number == 1) {
                        topTV.setVisibility(View.VISIBLE);
                        bottomTV.setVisibility(View.GONE);
                        topTV.setText(evaluateBean.getList().get(0).getFromNAME() + " 回复 "
                                + evaluateBean.getList().get(0).getToName() + " " + evaluateBean.getList().get(0).getContent());
                    } else if (number >= 2) {
                        topTV.setVisibility(View.VISIBLE);
                        bottomTV.setVisibility(View.VISIBLE);
                        topTV.setText(evaluateBean.getList().get(0).getFromNAME() + " 回复 "
                                + evaluateBean.getList().get(0).getToName() + " " + evaluateBean.getList().get(0).getContent());
                        bottomTV.setText(evaluateBean.getList().get(1).getFromNAME() + " 回复 "
                                + evaluateBean.getList().get(1).getToName() + " " + evaluateBean.getList().get(1).getContent());
                    }


                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setAdapter(mAdapter);
            mRV.scrollToPosition(0);
            mRV.setLayoutManager(linearLayoutManager);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void zan(final EvaluateBean evaluateBean, final int position, final int secondPosition, final TextView zan, final String type) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("type", type);
        switch (type) {
            case "2":
                map.put("isLike", evaluateBean.getIsLike() + "");
                map.put("relatedId", evaluateBean.getId());
                map.put("toUserId", evaluateBean.getUserId());
                break;
            case "3":
                map.put("isLike", evaluateBean.getList().get(secondPosition).getIsLike() + "");
                map.put("relatedId", evaluateBean.getList().get(secondPosition).getId());
                map.put("toUserId", evaluateBean.getList().get(secondPosition).getFromId());
                break;
        }
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .zanComment(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                switch (evaluateBean.getIsLike()) {
                                    case 0:
                                        if (type.equals("2")) {
                                            mList.get(position).setZanNumber(Integer.parseInt(mList.get(position).getZanNumber()) + 1 + "");
                                            mList.get(position).setIsLike(1);
                                        }
                                        break;
                                    case 1:
                                        if (type.equals("2")) {
                                            mList.get(position).setZanNumber(Integer.parseInt(mList.get(position).getZanNumber()) - 1 + "");
                                            mList.get(position).setIsLike(0);
                                        }
                                        break;
                                }
                                mAdapter.notifyItemChanged(position);
                            } else {
                                ToastUtil.showShort(mActivity, data.getString("resultDesc"));
                            }
                            zan.setClickable(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }


    private void getAllComments() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", mPage + "");
        map.put("rows", 25 + "");
        map.put("releaseGoodsId", mId);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getComments(RequestManager.encryptParams(map))
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
                            JSONObject res = new JSONObject(response);
                            if (res.getInt("resultCode") == 200) {
                                if (!res.getString("result").equals("null")) {
                                    JSONObject data = res.getJSONObject("result");
                                    Glide.with(mActivity)
                                            .load(data.getString("headUrl"))
                                            .into(mHeadRIV);
                                    JSONArray commentArray = data.getJSONArray("commendReplyResponseDtoList");
                                    for (int i = 0; i < commentArray.length(); i++) {
                                        JSONObject commentItem = commentArray.getJSONObject(i);
                                        EvaluateBean evaluateBean = new EvaluateBean();
                                        evaluateBean.setId(commentItem.getString("commentId"));
                                        evaluateBean.setUserId(commentItem.getString("commentFromUserId"));
                                        evaluateBean.setToUrl(commentItem.getString("commentFromUserHeadUrl"));
                                        evaluateBean.setContent(commentItem.getString("commentContent"));
                                        evaluateBean.setZanNumber(commentItem.getString("commentLikeCount"));
                                        evaluateBean.setEvaluateNumber(commentItem.getString("replyCount"));
                                        evaluateBean.setName(commentItem.getString("commentFromUser"));
                                        evaluateBean.setTime(commentItem.getString("commentTime"));
                                        evaluateBean.setIsLike(commentItem.getInt("isLike"));
                                        evaluateBean.setReplyCount(commentItem.getInt("replyCount"));
                                        JSONArray replyArray = commentItem.getJSONArray("replyResponseDtoList");
                                        List<EvaluateBean.EvaluateEvaluate> list = new ArrayList<>();
                                        for (int j = 0; j < replyArray.length(); j++) {
                                            JSONObject replyItem = replyArray.getJSONObject(j);
                                            EvaluateBean.EvaluateEvaluate evaluateEvaluate = new EvaluateBean.EvaluateEvaluate();
                                            evaluateEvaluate.setFromNAME(replyItem.getString("replyFromUser"));
                                            evaluateEvaluate.setToName(replyItem.getString("replyToUser"));
                                            evaluateEvaluate.setTime(replyItem.getString("replyTime"));
                                            evaluateEvaluate.setFromId(replyItem.getString("replyFromUserId"));
                                            evaluateEvaluate.setToId(replyItem.getString("replyToUserId"));
                                            evaluateEvaluate.setContent(replyItem.getString("replyContent"));
                                            evaluateEvaluate.setZanNumber(replyItem.getString("replyLikeCount"));
                                            evaluateEvaluate.setEvaluateNumber(replyItem.getString("replyCount"));
                                            evaluateEvaluate.setIsLike(replyItem.getInt("isLike"));
                                            evaluateEvaluate.setId(replyItem.getString("replyId"));
                                            list.add(evaluateEvaluate);
                                        }
                                        evaluateBean.setList(list);
                                        mList.add(evaluateBean);
                                    }
                                    showRecyclerView();
                                } else {
                                    Glide.with(mActivity).load(AccountManager.sUserBean.getHeadUrl()).into(mHeadRIV);
                                }
                            } else {
                                ToastUtil.showShort(mActivity, res.getString("resultDesc"));
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

    public static void open(Activity activity, String id, String fId) {
        Intent intent = new Intent(activity, AllEvaluateActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.FACE_ID, fId);
        activity.startActivity(intent);
    }


    /**
     * 设置评论内容点击事件
     *
     * @param item
     * @return
     */
    public SpannableString setClickableSpanContent(final String item) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // TODO: 2017/9/3 评论内容点击事件

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的内容文本颜色
                ds.setColor(ContextCompat.getColor(mActivity, R.color.font_gray));
                ds.setUnderlineText(false);
            }
        };
        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * 设置评论用户名字点击事件
     *
     * @param item
     * @return
     */
    public SpannableString setClickableSpan(final String item, final String id) {
        final SpannableString string = new SpannableString(item);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // TODO: 2017/9/3 评论用户名字点击事件
                PersonalActivity.open(mActivity, id);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                // 设置显示的用户名文本颜色
                ds.setColor(ContextCompat.getColor(mActivity, R.color.colorFontHint));
                ds.setUnderlineText(false);
            }
        };

        string.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    private void comment(String content) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", mFId);
        map.put("commentContent", content);
        map.put("releaseGoodsId", mId);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .comment(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                ToastUtil.showShort(mActivity, "评价成功");
                                CommonUtil.hideKeyBoard(mActivity);
                                mEvaluateBean = null;
                                mEvaluateET.setText("");
                                mEvaluateET.setFocusable(false);
                                mEvaluateET.setFocusableInTouchMode(false);
                                mPage = 1;
                                mDataStatus = STATUS_REFRESH;
                                getAllComments();
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

//    private void getReplyList(String id, final LinearLayout containerLL, final EvaluateBean evaluateBean, final int position) {
//        final Map<String, String> map = new HashMap<>();
//        map.put("userId", AccountManager.sUserBean.getId());
//        map.put("page", 1 + "");
//        map.put("row", 15 + "");
//        map.put("commentId", id);
//        LogUtil.e(TAG, map.toString());
//        RequestManager.mRetrofitManager
//                .createRequest(RetrofitRequestInterface.class)
//                .getReplyList(RequestManager.encryptParams(map))
//                .enqueue(new RetrofitCallBack() {
//                    @Override
//                    public void onSuccess(String response) {
//                        try {
//                            JSONObject data = new JSONObject(response);
//                            if (data.getInt("resultCode") == 200) {
//                                JSONObject result = data.getJSONObject("result");
//                                JSONArray replyArray = result.getJSONArray("replyResponseDtoList");
//                                List<EvaluateBean.EvaluateEvaluate> list = new ArrayList<>();
//                                for (int i = 0; i < replyArray.length(); i++) {
//                                    JSONObject replyItem = replyArray.getJSONObject(i);
//                                    EvaluateBean.EvaluateEvaluate evaluateEvaluate = new EvaluateBean.EvaluateEvaluate();
//                                    evaluateEvaluate.setFromNAME(replyItem.getString("replyFromUser"));
//                                    evaluateEvaluate.setToName(replyItem.getString("replyToUser"));
//                                    evaluateEvaluate.setTime(replyItem.getString("replyTime"));
//                                    evaluateEvaluate.setFromId(replyItem.getString("replyFromUserId"));
//                                    evaluateEvaluate.setToId(replyItem.getString("replyToUserId"));
//                                    evaluateEvaluate.setContent(replyItem.getString("replyContent"));
//                                    evaluateEvaluate.setZanNumber(replyItem.getString("replyLikeCount"));
//                                    evaluateEvaluate.setEvaluateNumber(replyItem.getString("replyCount"));
//                                    evaluateEvaluate.setIsLike(replyItem.getInt("isLike"));
//                                    evaluateEvaluate.setId(replyItem.getString("replyId"));
//                                    list.add(evaluateEvaluate);
//                                }
//                                evaluateBean.setList(list);
//                                for (int i = 0; i < list.size(); i++) {
//                                    final EvaluateBean.EvaluateEvaluate evaluateEvaluate = list.get(i);
//                                    View itemView = mInflater.inflate(R.layout.item_all_evaluate_evaluate, null);
//                                    TextView contentTV = itemView.findViewById(R.id.tv_item_all_evaluate_evaluate_content);
//                                    TextView timeTV = itemView.findViewById(R.id.tv_item_all_evaluate_evaluate_time);
//                                    TextView evaluateNumberTV = itemView.findViewById(R.id.tv_item_all_evaluate_evaluate_number);
//                                    final TextView zanTV = itemView.findViewById(R.id.tv_item_all_evaluate_evaluate_zan);
//                                    timeTV.setText(evaluateEvaluate.getTime());
//                                    evaluateNumberTV.setText(evaluateEvaluate.getEvaluateNumber());
//                                    switch (evaluateEvaluate.getIsLike()) {
//                                        case 0:
//                                            zanTV.setCompoundDrawables(mZanGray, null, null, null);
//                                            break;
//                                        case 1:
//                                            zanTV.setCompoundDrawables(mZanRed, null, null, null);
//                                            break;
//                                    }
//                                    SpannableStringBuilder builder = new SpannableStringBuilder();
//                                    builder.append(setClickableSpan(evaluateEvaluate.getFromNAME(), evaluateEvaluate.getFromId()));
//                                    builder.append(" 回复 ");
//                                    builder.append(setClickableSpan(evaluateEvaluate.getToName(), evaluateEvaluate.getToId()));
//                                    builder.append(" : ");
//                                    builder.append(setClickableSpanContent(evaluateEvaluate.getContent()));
//                                    contentTV.setText(builder);
//                                    contentTV.setMovementMethod(LinkMovementMethod.getInstance());
//                                    contentTV.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            mEvaluateET.setFocusable(true);
//                                            mEvaluateET.setFocusableInTouchMode(true);
//                                            mEvaluateET.requestFocus();
//                                            mReplyType = 2;
//                                            mEvaluateBean = evaluateBean;
//                                            mEvaluateEvaluate = evaluateEvaluate;
//                                            CommonUtil.showKeyBorad(mActivity);
//                                        }
//                                    });
//                                    zanTV.setText(evaluateEvaluate.getZanNumber());
//                                    final int secondPosition = i;
//                                    zanTV.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            zan(evaluateBean, position, secondPosition, zanTV, "3");
//                                        }
//                                    });
//                                    containerLL.addView(itemView);
//
//                                }
//
//                            } else {
//                                ToastUtil.showShort(mActivity, data.getString("resultDesc"));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//
//                    }
//                });
//    }


    private void reply(int type, String id, String toId, final String cId, String content) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("replyType", type + "");
        map.put("replyCommentId", id);
        map.put("toUserId", toId);
        map.put("replyContent", content);
        map.put("commentId", cId);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .reply(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {


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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateEvaluateList updateEvaluateList) {
        mRefreshLayout.startRefresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mActivity);
    }
}

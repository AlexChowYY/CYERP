package com.facewarrant.fw.ui.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.ConditionVariable;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.EvaluateBean;
import com.facewarrant.fw.bean.ReplyBean;
import com.facewarrant.fw.event.UpdateEvaluateList;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.CommonUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
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
public class AllReplyActivity extends BaseActivity {
    private static final String TAG = "AllReplyActivity";
    @BindView(R.id.rv_activity_all_reply)
    RecyclerView mRV;
    @BindView(R.id.riv_activity_all_reply)
    RoundedImageView mHeadRIV;
    @BindView(R.id.et_activity_all_evaluate)
    EditText mEvaluateET;
    @BindView(R.id.riv_activity_all_reply_head)
    RoundedImageView mTopRIV;
    @BindView(R.id.tv_activity_all_reply_name)
    TextView mNameTV;
    @BindView(R.id.tv_activity_all_reply_azn_number)
    TextView mZanTV;
    @BindView(R.id.tv_activity_all_reply_content)
    TextView mContentTV;
    @BindView(R.id.tv_activity_all_reply_time)
    TextView mTimeTV;

    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    private List<ReplyBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<ReplyBean> mAdapter;

    @BindView(R.id.trk)
    TwinklingRefreshLayout mRefreshLayout;

    private String mId;
    private int mReplyType;

    private ReplyBean mSelectBean;
    private EvaluateBean mEvaluateBean;

    private int mDataStatus = STATUS_REFRESH;
    private int mPage = 1;
    private static final int STATUS_REFRESH = 1;
    private static final int STATUS_LOAD = 2;

    private Drawable mZanRed;
    private Drawable mZanGray;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_all_reply;
    }

    @Override
    public void initData() {
        getUserInfo();
        mTitleTV.setText(R.string.all_reply);
        mRV.setNestedScrollingEnabled(false);
        mZanRed = ContextCompat.getDrawable(mActivity, R.drawable.zan_red);
        mZanRed.setBounds(0, 0, mZanRed.getMinimumWidth(), mZanRed.getMinimumHeight());
        mZanGray = ContextCompat.getDrawable(mActivity, R.drawable.zan_gray);
        mZanGray.setBounds(0, 0, mZanGray.getMinimumWidth(), mZanGray.getMinimumHeight());
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mEvaluateBean = (EvaluateBean) getIntent().getExtras().get(Constant.CODE);
            Glide.with(mActivity).load(mEvaluateBean.getToUrl()).into(mTopRIV);
            mNameTV.setText(mEvaluateBean.getName());
            mZanTV.setText(mEvaluateBean.getZanNumber());
            switch (mEvaluateBean.getIsLike()) {
                case 0:
                    mZanTV.setCompoundDrawables(mZanGray, null, null, null);
                    break;
                case 1:
                    mZanTV.setCompoundDrawables(mZanRed, null, null, null);
                    break;
            }
            mContentTV.setText(mEvaluateBean.getContent());
            mTimeTV.setText(mEvaluateBean.getTime());
            getReplyList();
        }
        CommonUtil.setRefreshStyle(mRefreshLayout, mActivity);


    }

    @Override
    public void initEvent() {
        //监听软键盘回车键
        mEvaluateET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = mEvaluateET.getText().toString().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtil.showShort(mActivity, "请填写评论！");
                    } else {
                        if (mSelectBean != null) {
                            reply(2, mSelectBean.getId(), mSelectBean.getReplyFromId(), content);
                        } else {
                            reply(1, mEvaluateBean.getId(), mEvaluateBean.getUserId(), content);
                        }
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
                getReplyList();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                mPage++;
                mDataStatus = STATUS_LOAD;
                getReplyList();
            }
        });
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mZanTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mZanTV.setClickable(false);
                zanComment();


            }
        });
    }


    private void zanComment() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("isLike", mEvaluateBean.getIsLike() + "");
        map.put("type", 2 + "");
        map.put("relatedId", mEvaluateBean.getId());
        map.put("toUserId", mEvaluateBean.getUserId());
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .zanComment(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        mZanTV.setClickable(true);
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                switch (mEvaluateBean.getIsLike()) {
                                    case 0:
                                        mEvaluateBean.setIsLike(1);
                                        mZanTV.setCompoundDrawables(mZanRed, null, null, null);
                                        int number = Integer.parseInt(mEvaluateBean.getZanNumber()) + 1;
                                        mEvaluateBean.setZanNumber(number + "");
                                        mZanTV.setText(mEvaluateBean.getZanNumber());
                                        break;
                                    case 1:
                                        mEvaluateBean.setIsLike(0);
                                        mZanTV.setCompoundDrawables(mZanGray, null, null, null);
                                        int number1 = Integer.parseInt(mEvaluateBean.getZanNumber()) - 1;
                                        mEvaluateBean.setZanNumber(number1 + "");
                                        mZanTV.setText(mEvaluateBean.getZanNumber());
                                        break;
                                }
                                EventBus.getDefault().post(new UpdateEvaluateList());
                            } else {
                                ToastUtil.showShort(mActivity, data.getString("resultDesc"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        mZanTV.setClickable(true);

                    }
                });
    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<ReplyBean>(mActivity, R.layout.item_all_reply, mList) {
                @Override
                protected void convert(ViewHolder holder, final ReplyBean replyBean, final int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_all_reply);
                    Glide.with(mActivity).load(replyBean.getHeadUrl()).into(topRIV);
                    holder.setText(R.id.tv_item_all_reply_name, replyBean.getReplyFromName());
                    final TextView zanTV = holder.getView(R.id.tv_item_all_reply_azn_number);
                    holder.setText(R.id.tv_item_all_reply_azn_number, replyBean.getZanNumber());
                    holder.setText(R.id.tv_activity_all_reply_content, replyBean.getReplyFromName() + " 回复 "
                            + replyBean.getReplyToName() + " " + replyBean.getContent());
                    holder.setText(R.id.tv_activity_all_reply_time, replyBean.getTime());
                    TextView evaluateTV = holder.getView(R.id.tv_item_all_reply_number);
                    switch (replyBean.getIsLike()) {
                        case 0:
                            zanTV.setCompoundDrawables(mZanGray, null, null, null);
                            break;
                        case 1:
                            zanTV.setCompoundDrawables(mZanRed, null, null, null);
                            break;

                    }
                    evaluateTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEvaluateET.setFocusable(true);
                            mEvaluateET.setFocusableInTouchMode(true);
                            mSelectBean = replyBean;
                            CommonUtil.showKeyBorad(mActivity);
                            mEvaluateET.requestFocus();


                        }
                    });
                    zanTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            zan(replyBean, position, zanTV, "3");

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

    public static void open(Activity activity, String id, EvaluateBean evaluateBean) {
        Intent intent = new Intent(activity, AllReplyActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.CODE, evaluateBean);
        activity.startActivity(intent);

    }


    private void getReplyList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("page", mPage + "");
        map.put("row", 15 + "");
        map.put("commentId", mId);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getReplyList(RequestManager.encryptParams(map))
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
                                JSONObject result = data.getJSONObject("result");
                                JSONArray replyArray = result.getJSONArray("replyResponseDtoList");
                                for (int i = 0; i < replyArray.length(); i++) {
                                    JSONObject replyItem = replyArray.getJSONObject(i);
                                    ReplyBean replyBean = new ReplyBean();
                                    replyBean.setId(replyItem.getString("replyId"));
                                    replyBean.setReplyFromId(replyItem.getString("replyFromUserId"));
                                    replyBean.setReplyToId(replyItem.getString("replyToUserId"));
                                    replyBean.setReplyFromName(replyItem.getString("replyFromUser"));
                                    replyBean.setReplyToName(replyItem.getString("replyToUser"));
                                    replyBean.setTime(replyItem.getString("replyTime"));
                                    replyBean.setZanNumber(replyItem.getString("replyLikeCount"));
                                    replyBean.setHeadUrl(replyItem.getString("replyFromUserHeadUrl"));
                                    replyBean.setContent(replyItem.getString("replyContent"));
                                    replyBean.setIsLike(replyItem.getInt("isLike"));

                                    mList.add(replyBean);
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

    private void reply(int type, String id, String toId, String content) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("replyType", type + "");
        map.put("replyCommentId", id);
        map.put("toUserId", toId);
        map.put("replyContent", content);
        map.put("commentId", mId);
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
                                ToastUtil.showShort(mActivity, "回复成功！");
                                CommonUtil.hideKeyBoard(mActivity);
                                mSelectBean = null;
                                mEvaluateET.setText("");
                                mPage = 1;
                                mDataStatus = STATUS_REFRESH;
                                getReplyList();
                                EventBus.getDefault().post(new UpdateEvaluateList());

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

    private void getUserInfo() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        switch (AccountManager.sUserBean.getLoginType()) {
            case 1:
            case 2:
                map.put("loginType", 1 + "");
                break;
            default:
                map.put("loginType", AccountManager.sUserBean.getLoginType() + "");
                break;
        }
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyUserInfo(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        Glide.with(mActivity)
                                .load(result.getString("headUrl"))
                                .into(mHeadRIV);

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

    private void zan(final ReplyBean replyBean, final int position, final TextView zan, final String type) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("type", type);
        switch (type) {
//            case "2":
//                map.put("isLike", replyBean.getIsLike() + "");
//                map.put("relatedId", evaluateBean.getId());
//                map.put("toUserId", evaluateBean.getUserId());
//                break;
            case "3":
                map.put("isLike", replyBean.getIsLike() + "");
                map.put("relatedId", replyBean.getId());
                map.put("toUserId", replyBean.getReplyFromId());
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
                                switch (replyBean.getIsLike()) {
                                    case 0:
                                        if (type.equals("3")) {
                                            mList.get(position).setZanNumber(Integer.parseInt(mList.get(position).getZanNumber()) + 1 + "");
                                            mList.get(position).setIsLike(1);

                                        }
                                        break;
                                    case 1:
                                        if (type.equals("3")) {
                                            mList.get(position).setZanNumber(Integer.parseInt(mList.get(position).getZanNumber()) - 1 + "");
                                            mList.get(position).setIsLike(0);

                                        }
                                        break;
                                }
                                mAdapter.notifyItemChanged(position);
                                EventBus.getDefault().post(new UpdateEvaluateList());
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

}

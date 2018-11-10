package com.facewarrant.fw.ui.activity.information;

import android.content.Intent;
import android.util.JsonReader;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.event.UpdateMesageEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class InformationActivity extends BaseActivity {
    private static final String TAG = "InformationActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.ll_activity_information_answer_me)
    LinearLayout mAnswerMeLL;
    @BindView(R.id.ll_activity_information_question_me)
    LinearLayout mQuestionMeLL;
    @BindView(R.id.ll_activity_information_face_me)
    LinearLayout mFaceMeLL;
    @BindView(R.id.ll_activity_information_zan)
    LinearLayout mZanLL;
    @BindView(R.id.ll_activity_information_wish_form)
    LinearLayout mWishFormLL;
    @BindView(R.id.ll_activity_information_follow)
    LinearLayout mFollowLL;
    @BindView(R.id.ll_activity_information_reply)
    LinearLayout mReplyLL;
    @BindView(R.id.tv_activity_information_question_me)
    TextView mQuestionTipsTV;
    private int mQuestionCount;
    @BindView(R.id.tv_activity_information_answer_me)
    TextView mAnswerTipsTV;
    private int mAnswerCount;
    @BindView(R.id.tv_activity_information_face_me)
    TextView mFaceTipsTV;
    private int mFavoriteCount;
    @BindView(R.id.tv_activity_information_zan)
    TextView mLikeTipsTV;
    private int mLikeCount;
    @BindView(R.id.tv_activity_information_wish_form)
    TextView mCollectionTipsTV;
    private int mCollectionCount;
    @BindView(R.id.tv_activity_information_follow)
    TextView mAttentionTipsTV;
    private int mAttentionCount;
    @BindView(R.id.tv_activity_information_reply)
    TextView mCommendTipsTV;
    private int mCommendCount;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_information;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.my_message);
        getTips();
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
        mAnswerMeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, AnswerMeActivity.class));
            }
        });
        mQuestionMeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, QuestionMeActivity.class));
            }
        });
        mFaceMeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, FaceMeActivity.class));
            }
        });
        mZanLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, PraiseCommentActivity.class));
            }
        });
        mWishFormLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, WishFormActivity.class));
            }
        });
        mFollowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, FollowActivity.class));
            }
        });
        mReplyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, EvaluateAndReplyActivity.class));
            }
        });

    }

    private void getTips() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMessageTips(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                mQuestionCount = result.getInt("questionToMeCount");
                                mAnswerCount = result.getInt("answerToMeCount");
                                mFavoriteCount = result.getInt("favoriteNewCount");
                                mLikeCount = result.getInt("likeNewCount");
                                mCollectionCount = result.getInt("collectionCount");
                                mAttentionCount = result.getInt("attentionNewCount");
                                mCommendCount = result.getInt("commendNewCount");
                                seTips();

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

    private void seTips() {
        if (mQuestionCount > 0) {
            mQuestionTipsTV.setVisibility(View.VISIBLE);
            if (mQuestionCount >= 10) {
                mQuestionTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mQuestionCount > 99) {
                    mQuestionTipsTV.setText("99+");
                } else {
                    mQuestionTipsTV.setText(mQuestionCount + "");
                }
            } else {
                mQuestionTipsTV.setText(mQuestionCount + "");
                mQuestionTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }


        } else {
            mQuestionTipsTV.setVisibility(View.GONE);
        }
        if (mAnswerCount > 0) {
            mAnswerTipsTV.setVisibility(View.VISIBLE);
            if (mQuestionCount >= 10) {
                mAnswerTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mAnswerCount > 99) {
                    mAnswerTipsTV.setText("99+");
                } else {
                    mAnswerTipsTV.setText(mAnswerCount + "");
                }
            } else {
                mAnswerTipsTV.setText(mAnswerCount + "");
                mAnswerTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }
        } else {
            mAnswerTipsTV.setVisibility(View.GONE);
        }
        if (mAttentionCount > 0) {
            mAttentionTipsTV.setVisibility(View.VISIBLE);
            if (mAttentionCount >= 10) {
                mAttentionTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mAttentionCount > 99) {
                    mAttentionTipsTV.setText("99+");
                } else {
                    mAttentionTipsTV.setText(mAttentionCount + "");
                }
            } else {
                mAttentionTipsTV.setText(mAttentionCount + "");
                mAttentionTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }
        } else {
            mAttentionTipsTV.setVisibility(View.GONE);
        }
        if (mFavoriteCount > 0) {
            mFaceTipsTV.setVisibility(View.VISIBLE);
            if (mFavoriteCount >= 10) {
                mFaceTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mFavoriteCount > 99) {
                    mFaceTipsTV.setText("99+");
                } else {
                    mFaceTipsTV.setText(mFavoriteCount + "");
                }
            } else {
                mFaceTipsTV.setText(mFavoriteCount + "");
                mFaceTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }
        } else {
            mFaceTipsTV.setVisibility(View.GONE);
        }
        if (mLikeCount > 0) {
            mLikeTipsTV.setVisibility(View.VISIBLE);
            if (mLikeCount >= 10) {
                mLikeTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mLikeCount > 99) {
                    mLikeTipsTV.setText("99+");
                } else {
                    mLikeTipsTV.setText(mLikeCount + "");
                }
            } else {
                mLikeTipsTV.setText(mLikeCount + "");
                mLikeTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }
        } else {
            mLikeTipsTV.setVisibility(View.GONE);
        }
        if (mCollectionCount > 0) {
            mCollectionTipsTV.setVisibility(View.VISIBLE);
            if (mCollectionCount >= 10) {
                mCollectionTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mCollectionCount > 99) {
                    mCollectionTipsTV.setText("99+");
                } else {
                    mCollectionTipsTV.setText(mCollectionCount + "");
                }
            } else {
                mCollectionTipsTV.setText(mCollectionCount + "");
                mCollectionTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }
        } else {
            mCollectionTipsTV.setVisibility(View.GONE);
        }
        if (mAttentionCount > 0) {

            mAttentionTipsTV.setVisibility(View.VISIBLE);
            if (mAttentionCount >= 10) {
                mAttentionTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mAttentionCount > 99) {
                    mAttentionTipsTV.setText("99+");
                } else {
                    mAttentionTipsTV.setText(mAttentionCount + "");
                }
            } else {
                mAttentionTipsTV.setText(mAttentionCount + "");
                mAttentionTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }
        } else {
            mAttentionTipsTV.setVisibility(View.GONE);
        }
        if (mCommendCount > 0) {
            mCommendTipsTV.setVisibility(View.VISIBLE);
            if (mCommendCount >= 10) {
                mCommendTipsTV.setBackgroundResource(R.drawable.shape_ellipse_red_15);
                if (mCommendCount > 99) {
                    mCommendTipsTV.setText("99+");
                } else {
                    mCommendTipsTV.setText(mCommendCount + "");
                }
            } else {
                mCommendTipsTV.setText(mCommendCount + "");
                mCommendTipsTV.setBackgroundResource(R.drawable.shape_circle_red_15);
            }
        } else {
            mCommendTipsTV.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uodateInfoRead(UpdateMesageEvent updateMesageEvent) {
        getTips();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mActivity);
    }
}

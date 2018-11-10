package com.facewarrant.fw.ui.activity.withdraw;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class WithdrawDetailActivity extends BaseActivity {
    private static final String TAG = "WithdrawDetailActivity";
    private String mId;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.riv_activity_withdraw_detail_icon)
    RoundedImageView mTopRIV;
    @BindView(R.id.tv_activity_withdraw_detail_name)
    TextView mNameTV;
    @BindView(R.id.tv_activity_withdraw_detail_money)
    TextView mMoneyTV;
    @BindView(R.id.iv_activity_withdraw_detail_ing)
    ImageView mWithdrawingIV;
    @BindView(R.id.iv_activity_withdraw_detail_ed)
    ImageView mWithdrawedIV;
    @BindView(R.id.tv_activity_withdraw_detail_ed)
    TextView mWithdrawedTV;
    @BindView(R.id.tv_activity_withdraw_detail_account)
    TextView mAccountTV;
    @BindView(R.id.tv_activity_withdraw_detail_time)
    TextView mTimeTV;
    @BindView(R.id.tv_activity_withdraw_detail_sum)
    TextView mSumTV;
    @BindView(R.id.tv_activity_withdraw_detail_withdraw_again)
    TextView mAgainTV;
    private int mType;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_withdraw_detail;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.withdraw_detail);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            getWithdrawDetail();
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
        mAgainTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WithdrawActivity.open(mActivity, mType);
            }
        });

    }

    private void getWithdrawDetail() {
        final Map<String, String> map = new HashMap<>();
        map.put("accountExpend", mId);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getWithdrawDetail(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                mType = result.getInt("expendType");
                                switch (result.getInt("expendType")) {
                                    case 0://银行
                                        Glide.with(mActivity)
                                                .load(result.getString("bankUrl"))
                                                .into(mTopRIV);
                                        mNameTV.setText(result.getString("bankName"));
                                        mAccountTV.setText(result.getString("bankName") + "（尾号" + result.getString("bankAccountNumber")
                                                .substring(result.getString("bankAccountNumber").length() - 4)
                                                + "）");


                                        break;
                                    case 1://支付宝
                                        mTopRIV.setImageResource(R.drawable.withdraw_ali);
                                        mNameTV.setText(result.getString("realName"));
                                        mAccountTV.setText(result.getString("bankAccountNumber").substring(0, 3) + "****" +
                                                result.getString("bankAccountNumber").substring(7, result.getString("bankAccountNumber").length()));
                                        break;
                                }
                                mMoneyTV.setText("+" + result.getString("withdrawFee"));
                                switch (result.getInt("status")) {
                                    case 0://失败
                                        mWithdrawedIV.setImageResource(R.drawable.witdraw_fail);
                                        mWithdrawedTV.setText(R.string.withdraw_fail);
                                        mWithdrawedTV.setTextColor(ContextCompat.getColor(mActivity, R.color.color_withdraw_red));
                                        break;
                                    case 1://成功
                                        mWithdrawedIV.setImageResource(R.drawable.withdraw_success);
                                        mWithdrawedTV.setTextColor(ContextCompat.getColor(mActivity, R.color.font_blue_big1));

                                        break;
                                    case 2://提现中
                                        mWithdrawingIV.setImageResource(R.drawable.withdraw_ing_blue);
                                        mWithdrawedTV.setTextColor(ContextCompat.getColor(mActivity, R.color.font_blue_big1));
                                        mWithdrawedTV.setTextColor(ContextCompat.getColor(mActivity, R.color.colorFontHint));
                                        break;

                                }

                                mTimeTV.setText(result.getString("withdrawalsTime"));
                                mSumTV.setText(result.getString("withdrawFee"));

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
        Intent intent = new Intent(activity, WithdrawDetailActivity.class);
        intent.putExtra(Constant.ID, id);
        activity.startActivity(intent);
    }


}

package com.facewarrant.fw.ui.activity.withdraw;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class WithdrawActivity extends BaseActivity {
    private static final String TAG = "WithdrawActivity";
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.et_activity_withdraw_name)
    EditText mBankNameET;
    @BindView(R.id.et_activity_withdraw_ali_name)
    EditText mAliNameET;
    @BindView(R.id.ll_activity_withdraw_type_bank)
    LinearLayout mTypeBankLL;
    @BindView(R.id.ll_activity_withdraw_type_ali)
    LinearLayout mTypeAliLL;
    @BindView(R.id.et_activity_withdraw_account)
    EditText mBankAccountET;
    @BindView(R.id.et_activity_withdraw_ali_account)
    EditText mAliAccoutET;
    @BindView(R.id.tv_activity_withdraw_leave_money)
    TextView mLeaveMoneyTV;
    @BindView(R.id.tv_activity_withdraw_tips)
    TextView mTipsTV;
    @BindView(R.id.tv_activity_withdraw_rule_one)
    TextView mRuleOneTV;
    @BindView(R.id.tv_activity_withdraw_rule_two)
    TextView mRuleTwoTV;
    @BindView(R.id.tv_activity_withdraw_rule_three)
    TextView mRuleThreeTV;
    @BindView(R.id.tv_activity_withdraw_withdraw)
    TextView mWithdrawTV;
    @BindView(R.id.et_activity_withdraw_money)
    EditText mMoneyET;

    private String mAccount;
    private String mBId;
    private String mBankName;
    private String mName;

    private int mType;
    public static final int TYPE_WITHDRAW_BANK = 0;
    public static final int TYPE_WITHDRAW_ALI = 1;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_withdraw;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.withdraw);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mType = getIntent().getExtras().getInt(Constant.TYPE);
            switch (mType) {
                case TYPE_WITHDRAW_ALI:
                    mTypeAliLL.setVisibility(View.VISIBLE);
                    mTypeBankLL.setVisibility(View.GONE);
                    break;
                case TYPE_WITHDRAW_BANK:
                    mTypeBankLL.setVisibility(View.VISIBLE);
                    mTypeAliLL.setVisibility(View.GONE);
                    break;
            }

            getAccount();
            getLeaveMoney();
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
        mWithdrawTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = mMoneyET.getText().toString().trim();
                if (TextUtils.isEmpty(money)) {
                    ToastUtil.showShort(mActivity, "请填写提现金额");
                } else {
                    withdraw(money);
                }

            }
        });

    }

    private void getAccount() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        // map.put("userId", "1263");
        map.put("type", mType + "");
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getCashAccount(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                if (!data.getString("result").equals("null")) {
                                    JSONObject result = data.getJSONObject("result");
                                    mBankNameET.setText(result.getString("realName"));
                                    mAliNameET.setText(result.getString("realName"));
                                    mAccount = result.getString("cashAccount");
                                    mBankAccountET.setText(result.getString("cashAccount").substring(0, 3) + "****" +
                                            result.getString("cashAccount").substring(7, result.getString("cashAccount").length()));
                                    mAliAccoutET.setText(result.getString("cashAccount").substring(0, 3) + "****" +
                                            result.getString("cashAccount").substring(7, result.getString("cashAccount").length()));
                                    mBankNameET.setSelection(result.getString("realName").length());
                                    mAliAccoutET.setSelection(result.getString("cashAccount").length());
                                    mBankAccountET.setSelection(result.getString("cashAccount").length());
                                    mBId = result.getString("bankId");
                                    mBankName = result.getString("cashBankType");
                                    mName = result.getString("realName");

                                    switch (mType) {
                                        case TYPE_WITHDRAW_ALI:
                                            mTipsTV.setText("提现到支付宝（" + result.getString("cashAccount").substring(0, 3) + "****" +
                                                    result.getString("cashAccount").substring(7, result.getString("cashAccount").length())
                                                    + "），到账时间以支付宝实际到账时间为准");
                                            break;
                                        case TYPE_WITHDRAW_BANK:
                                            mTipsTV.setText("提现到" + result.getString("cashBankType") + "（尾号" + result.getString("cashAccount")
                                                    .substring(result.getString("cashAccount").length() - 4)
                                                    + "），到账时间以银行实际到账时间为准");
                                            break;

                                    }
                                }


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

    private void getLeaveMoney() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        // map.put("userId", "1263");
        // map.put("type", mType + "");
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getLeaveMoney(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                if (!data.getString("result").equals("null")) {
                                    JSONObject result = data.getJSONObject("result");
                                    mLeaveMoneyTV.setText("可提现余额" + result.getString("balance") + "元");
                                    mRuleOneTV.setText("1、提现时间：每" + result.getString("withdrawTime"));
                                    mRuleTwoTV.setText("2、提现条件：提上周及以前未提现的现金；");
                                    mRuleThreeTV.setText("3、单日提现次数：" + result.getString("cashOnHand") + "次，"
                                            + "单日限制" + result.getString("withdrawLimit") + "元");
                                }


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

    private void withdraw(String money) {
        final Map<String, String> map = new HashMap<>();
        // map.put("userId", "1263");
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("type", mType + "");
        map.put("cashAccount", mAccount);
        switch (mType) {
            case TYPE_WITHDRAW_BANK:
                map.put("bankId", mBId);
                map.put("bankName", mBankName);
                break;
        }
        map.put("realName", mName);
        map.put("withdrawCash", money);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .withdraw(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                ToastUtil.showShort(mActivity, "提现成功");
                                finish();

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


    public static void open(Activity activity, int type) {
        Intent intent = new Intent(activity, WithdrawActivity.class);
        intent.putExtra(Constant.TYPE, type);
        activity.startActivity(intent);
    }
}

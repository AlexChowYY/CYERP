package com.facewarrant.fw.ui.activity.mine.faceValue;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.ui.activity.withdraw.WithdrawChooseActivity;
import com.facewarrant.fw.ui.activity.withdraw.WithdrawRecordActivity;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FaceValueActivity extends BaseActivity {
    private static final String TAG = "FaceValueActivity";
    @BindView(R.id.iv_activity_face_value_back)
    ImageView mBackIV;
    @BindView(R.id.ll_activity_face_value_withdraw)
    LinearLayout mWithdrawLL;
    @BindView(R.id.ll_activity_face_value_record)
    LinearLayout mRecordLL;
    @BindView(R.id.rl_activity_face_value_order)
    RelativeLayout mOrderRL;
    @BindView(R.id.rl_activity_face_value_invite)
    RelativeLayout mInviteRl;
    @BindView(R.id.rl_activity_face_value_exchange)
    RelativeLayout mExchangeRL;
    @BindView(R.id.tv_activity_face_value_value)
    TextView mValueTV;

    private String mFaceValue;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_face_value;
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mFaceValue = getIntent().getExtras().getString(Constant.LABEL);
            mValueTV.setText(mFaceValue);
        }


    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWithdrawLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, WithdrawChooseActivity.class));
            }
        });
        mRecordLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, WithdrawRecordActivity.class));
            }
        });
        mOrderRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, OrderIncomeActivity.class));
            }
        });
        mInviteRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, InviteRewardActivity.class));
            }
        });
        mExchangeRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, IntegralExchangeActivity.class));
            }
        });

    }

    public static void open(Activity activity, String faceValue) {
        Intent intent = new Intent(activity, FaceValueActivity.class);
        intent.putExtra(Constant.LABEL, faceValue);
        activity.startActivity(intent);
    }


}

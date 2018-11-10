package com.facewarrant.fw.ui.activity.withdraw;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class WithdrawChooseActivity extends BaseActivity {
    private static final String TAG = "WithdrawChooseActivity";
    @BindView(R.id.ll_activity_withdraw_zhifubao)
    LinearLayout mZhifubaoLL;
    @BindView(R.id.ll_activity_withdraw_bank)
    LinearLayout mBankLL;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_withdrow;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.withdraw);

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mZhifubaoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               WithdrawActivity.open(mActivity,WithdrawActivity.TYPE_WITHDRAW_ALI);
            }
        });
        mBankLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WithdrawActivity.open(mActivity,WithdrawActivity.TYPE_WITHDRAW_BANK);
            }
        });


    }


}

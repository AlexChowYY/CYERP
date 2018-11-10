package com.facewarrant.fw.ui.activity.mine.integral;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.Constant;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class IntegralRuleActivity extends BaseActivity {
    private static final String TAG = "IntegralRuleActivity";
    private String mPoint;
    private String mSignPoint;
    private String mRegisterPoint;
    private String mExchangePoint;

    @BindView(R.id.tv_activity_integral_rule_present)
    TextView mPointTV;
    @BindView(R.id.iv_activity_face_value_back)
    ImageView mBackIV;
    @BindView(R.id.tv_sign)
    TextView mSignTV;
    @BindView(R.id.tv_register)
    TextView mRegisterTV;
    @BindView(R.id.tv_exchange)
    TextView mExchangeTV;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_integral_rule;
    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mPoint = getIntent().getExtras().getString(Constant.CODE);
            mPointTV.setText(mPoint);
            mSignPoint = getIntent().getExtras().getString(Constant.TYPE);
            mExchangePoint = getIntent().getExtras().getString(Constant.ID);
            mRegisterPoint= getIntent().getExtras().getString(Constant.NAME);
            mSignTV.setText(String.format(getString(R.string.say_1),mSignPoint));
            mRegisterTV.setText(String.format(getString(R.string.say_2),mRegisterPoint));
            mExchangeTV.setText(String.format(getString(R.string.use_2),mExchangePoint));

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

    }

    public static void open(Activity activity, String number, String sign, String exchange,String register) {
        Intent intent = new Intent(activity, IntegralRuleActivity.class);
        intent.putExtra(Constant.CODE, number);
        intent.putExtra(Constant.TYPE, sign);
        intent.putExtra(Constant.ID, exchange);
        intent.putExtra(Constant.NAME,register);
        activity.startActivity(intent);
    }
}

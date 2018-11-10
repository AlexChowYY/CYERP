package com.facewarrant.fw.ui.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class RevisePhoneActivity extends BaseActivity {
    private static final String TAG = "RevisePhoneActivity";
    @BindView(R.id.tv_activity_revise_phone_change)
    TextView mChangeTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_revise_phone;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.bind_phone);

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, ChangePhoneActivity.class));

            }
        });

    }
}

package com.facewarrant.fw.ui.activity.mine;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.AccountManager;
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
public class RevisePwdActivity extends BaseActivity {
    private static final String TAG = "RevisePwdActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.et_activity_change_pwd_confirm)
    EditText mConfirmET;
    @BindView(R.id.et_activity_change_phone_old)
    EditText mOldET;
    @BindView(R.id.et_activity_change_pwd_new)
    EditText mNewET;
    @BindView(R.id.tv_activity_revise_pwd_achieve)
    TextView mAchieveTV;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_revise_pwd;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.change_pwd);
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAchieveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old = mOldET.getText().toString().trim();
                String newPwd = mNewET.getText().toString().trim();
                String confirm = mConfirmET.getText().toString().trim();
                if (TextUtils.isEmpty(old)) {
                    ToastUtil.showShort(mActivity, "请输入旧密码");
                } else if (TextUtils.isEmpty(newPwd)) {
                    ToastUtil.showShort(mActivity, "请输入新密码");
                } else if (TextUtils.isEmpty(confirm)) {
                    ToastUtil.showShort(mActivity, "请确认新密码");
                } else if (!newPwd.equals(confirm)) {
                    ToastUtil.showShort(mActivity, "两次输入不一致");
                } else {
                    changePwd(old, newPwd);
                }
            }
        });


    }

    private void changePwd(String old, String newPwd) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("oldPwd", old);
        map.put("newPwd", newPwd);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .changePwd(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        ToastUtil.showShort(mActivity,"修改成功！");
                        AccountManager.loginOut(mActivity);
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
}

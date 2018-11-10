package com.facewarrant.fw.ui.activity.mine.faceGroup;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.FaceBean;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class ReviseGroupNameActivity extends BaseActivity {
    private static final String TAG = "ReviseGroupNameActivity";
    @BindView(R.id.et_activity_revise_group_name)
    EditText mNameET;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;
    private String mName;

    private String mId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_revise_group_name;
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mName = getIntent().getExtras().getString(Constant.NAME);
            mNameET.setText(mName);
            mNameET.setSelection(mName.length());
        }
        mTitleTV.setText("Face名称");
        mSettingTV.setText(R.string.save);
        mSettingTV.setTextColor(ContextCompat.getColor(mActivity, R.color.font_blue_big));
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSettingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameET.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtil.showShort(mActivity, "请填写群名称");
                } else {
                    reviseGroupName(name);
                }
            }
        });

    }

    public static void open(Activity activity, String name, String id) {
        Intent intent = new Intent(activity, ReviseGroupNameActivity.class);
        intent.putExtra(Constant.NAME, name);
        intent.putExtra(Constant.ID, id);
        activity.startActivity(intent);
    }

    private void reviseGroupName(String name) {
        final Map<String, String> map = new HashMap<>();
        map.put("groupsId", mId);
        map.put("groupsName", name);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .reviseGroupName(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
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

}

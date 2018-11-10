package com.facewarrant.fw.ui.activity.mine.integral;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class ExchangeIntegralActivity extends BaseActivity {
    private static final String TAG = "ExchangeIntegralActivit";
    @BindView(R.id.tv_activity_exchange_integral_number)
    TextView mNumberTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.tv_activity_exchange_integral_exchange)
    TextView mExchangeTV;
    @BindView(R.id.et_activity_exchange_integral_number)
    EditText mNumberET;

    @BindView(R.id.tv_activity_exchange_integral_tips)
    TextView mTipsTV;
    private String mPoint;
    private String mPoint1;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_exchange_integral;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.change_face_value);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mPoint = getIntent().getExtras().getString(Constant.CODE);
            mPoint1 = getIntent().getExtras().getString(Constant.TYPE);
            mNumberTV.setText(mPoint);
            StringBuffer s1 = new StringBuffer(getString(R.string.exchange));  //原字符串
                                       //要插入的字符串

            Pattern p = Pattern.compile("为");             //插入位置
            Matcher m = p.matcher(s1.toString());
            if(m.find()){
                s1.insert((m.start()+1), mPoint1);        //插入字符串
            }
            Pattern p1 = Pattern.compile("是");             //插入位置
            Matcher m1 = p1.matcher(s1.toString());
            if(m1.find()){
                s1.insert((m1.start()+1), mPoint1);        //插入字符串
            }
            mTipsTV.setText(s1.toString());


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
        mExchangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mNumberET.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    ToastUtil.showShort(mActivity, "请输入积分数量");
                } else if (Integer.parseInt(number) <= 0 || Integer.parseInt(number) % 10 != 0) {
                    ToastUtil.showShort(mActivity, "积分数量输入不正确");
                } else {
                    exchangeFaceValue(number);
                }
            }
        });

    }

    private void exchangeFaceValue(String points) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("points", points);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .exchangeFaceValue(RequestManager.encryptParams(map))
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

    public static void open(Activity activity, String number, String number1) {
        Intent intent = new Intent(activity, ExchangeIntegralActivity.class);
        intent.putExtra(Constant.CODE, number);
        intent.putExtra(Constant.TYPE, number1);
        activity.startActivity(intent);
    }
}

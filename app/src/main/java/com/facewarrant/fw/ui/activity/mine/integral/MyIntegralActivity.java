package com.facewarrant.fw.ui.activity.mine.integral;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.IntegralBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class MyIntegralActivity extends BaseActivity {
    private static final String TAG = "MyIntegralActivity";
    @BindView(R.id.rv_my_integral)
    RecyclerView mRV;
    @BindView(R.id.tv_activity_my_integral_present)
    TextView mPresentTV;
    @BindView(R.id.tv_activity_my_integral_rule)
    TextView mRuleTV;
    @BindView(R.id.tv_activity_my_integral_exchange)
    TextView mExchangeTV;
    @BindView(R.id.iv_activity_face_value_back)
    ImageView mBackIV;
    private List<IntegralBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<IntegralBean> mAdapter;
    private String mPoint;
    private String mExchangePoint;
    private String mSignPoint;
    private String mRegisterPoint;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_integral;
    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void initData() {
        getPointsDetail();
        mRV.setNestedScrollingEnabled(false);

    }

    @Override
    public void initEvent() {
        mRuleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntegralRuleActivity.open(mActivity, mPoint, mSignPoint, mExchangePoint, mRegisterPoint);
            }
        });
        mExchangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExchangeIntegralActivity.open(mActivity, mPoint, mExchangePoint);
            }
        });
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getPointsDetail() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
       // map.put("userId", "1263");
        map.put("page", 1 + "");
        map.put("rows", 15 + "");
        LogUtil.e(TAG, map.toString());

        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getPointDetail(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                mPresentTV.setText(result.getString("remainderPoints"));
                                mPoint = result.getString("remainderPoints");
                                mExchangePoint = result.getString("pointsPercent");
                                mSignPoint = result.getString("pointsBase");
                                mRegisterPoint = result.getString("pointsRegister");
                                JSONArray detailArray = result.getJSONArray("pointsDetailList");
                                for (int i = 0; i < detailArray.length(); i++) {
                                    JSONObject detail = detailArray.getJSONObject(i);
                                    IntegralBean integralBean = new IntegralBean();
                                    switch (detail.getInt("operateType")) {
                                        case 0:
                                            integralBean.setName("提取到脸值");
                                            break;
                                        case 1:
                                            integralBean.setName("签到");
                                            break;
                                        case 2:
                                            integralBean.setName("注册得积分");
                                            break;

                                    }
                                    integralBean.setNumber(detail.getInt("afterPoints")
                                            - detail.getInt("beforePoints") + "");
                                    integralBean.setTime(detail.getString("createTime"));
                                    mList.add(integralBean);
                                }
                                showRecyclerView();

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

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<IntegralBean>(mActivity, R.layout.item_my_integral, mList) {
                @Override
                protected void convert(ViewHolder holder, IntegralBean integralBean, int position) {
                    holder.setText(R.id.tv_item_my_integral_name, integralBean.getName());
                    holder.setText(R.id.tv_item_my_integral_time, integralBean.getTime());

                    if (Integer.parseInt(integralBean.getNumber()) > 0) {
                        holder.setText(R.id.tv_item_my_integral_number, "+" + integralBean.getNumber());
                        holder.setTextColor(R.id.tv_item_my_integral_number, ContextCompat.getColor(mActivity, R.color.color_withdraw_blue));
                    } else {
                        holder.setText(R.id.tv_item_my_integral_number, "-" + integralBean.getNumber());
                        holder.setTextColor(R.id.tv_item_my_integral_number, ContextCompat.getColor(mActivity, R.color.color_withdraw_red));
                    }


                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }
}

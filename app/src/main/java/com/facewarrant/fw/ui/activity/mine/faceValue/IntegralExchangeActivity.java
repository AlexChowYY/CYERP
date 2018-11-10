package com.facewarrant.fw.ui.activity.mine.faceValue;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.IncomeBean;
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
public class IntegralExchangeActivity extends BaseActivity {
    private static final String TAG = "IntegralExchangeActivit";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;

    @BindView(R.id.rv_activity_integral_exchange)
    RecyclerView mRV;

    private List<IncomeBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<IncomeBean> mAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_integral_exchange;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.point_change);
        getIncome();

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

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<IncomeBean>(mActivity, R.layout.item_order_income, mList) {
                @Override
                protected void convert(ViewHolder holder, IncomeBean incomeBean, int position) {
                    holder.setText(R.id.tv_item_order_income_month, incomeBean.getMonth());
                    LinearLayout containerLL = holder.getView(R.id.ll_item_order_income_container);
                    containerLL.removeAllViews();
                    for (int i = 0; i < incomeBean.getList().size(); i++) {
                        IncomeBean.Income income = incomeBean.getList().get(i);
                        View view = mInflater.inflate(R.layout.item_item_order_income, null);
                        ImageView topIV = view.findViewById(R.id.iv_top);
                        TextView nameTV = view.findViewById(R.id.tv_brand_name);
                        TextView timeTV = view.findViewById(R.id.tv_time);
                        topIV.setVisibility(View.GONE);
                        TextView numberTV = view.findViewById(R.id.tv_number);
                        Glide.with(mActivity).load(income.getIcon()).into(topIV);
                        nameTV.setText(income.getName());
                        timeTV.setText(income.getTime());
                        numberTV.setText(income.getNumber());
                        containerLL.addView(view);
                    }
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getIncome() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
       // map.put("userId", "1263");
        map.put("incomeType", 3 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getIncome(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONArray result = data.getJSONArray("result");
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject resultItem = result.getJSONObject(i);
                                    IncomeBean incomeBean = new IncomeBean();
                                    incomeBean.setMonth(resultItem.getString("dateYearMonth"));
                                    JSONArray incomeList = resultItem.getJSONArray("accountIncomeInfoList");
                                    List<IncomeBean.Income> list = new ArrayList<>();
                                    for (int i1 = 0; i1 < incomeList.length(); i1++) {
                                        JSONObject incomeItem = incomeList.getJSONObject(i1);
                                        IncomeBean.Income income = new IncomeBean.Income();
                                        income.setIcon(incomeItem.getString("brandUrl"));
                                        income.setId("orderId");
                                        income.setName(incomeItem.getString("brandName"));
                                        income.setTime(incomeItem.getString("createTime"));
                                        income.setNumber("+" + incomeItem.getString("incomeFee"));
                                        list.add(income);
                                    }
                                    incomeBean.setList(list);
                                    mList.add(incomeBean);
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
}

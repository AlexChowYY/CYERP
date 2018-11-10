package com.facewarrant.fw.ui.activity.withdraw;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.WithdrawRecordBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;

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
public class WithdrawRecordActivity extends BaseActivity {
    private static final String TAG = "WithdrawRecordActivity";
    @BindView(R.id.rv_activity_withdraw_record)
    RecyclerView mRV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    private List<WithdrawRecordBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<WithdrawRecordBean> mAdapter;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_withdraw_record;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.withdraw_record);
        getWithdrawRecord();
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

    private void getWithdrawRecord() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());

        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getWithdrawRecord(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray resultArray = data.getJSONArray("result");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject resultItem = resultArray.getJSONObject(i);
                                WithdrawRecordBean withdrawRecordBean = new WithdrawRecordBean();
                                withdrawRecordBean.setMonth(resultItem.getString("dateYearMonth"));
                                JSONArray recordArray = resultItem.getJSONArray("accountExpendList");
                                List<WithdrawRecordBean.Record> list = new ArrayList<>();
                                for (int i1 = 0; i1 < recordArray.length(); i1++) {
                                    JSONObject recordItem = recordArray.getJSONObject(i1);
                                    WithdrawRecordBean.Record record = new WithdrawRecordBean.Record();

                                    record.setMoney(recordItem.getString("withdrawFee"));
                                    record.setTime(recordItem.getString("withdrawalsTime"));
                                    record.setStatus(recordItem.getInt("status"));
                                    record.setId(recordItem.getString("accountExpend"));
                                    switch (recordItem.getInt("expendType")) {
                                        case 0://银行
                                            record.setName(recordItem.getString("bankName") + "（尾号"
                                                    + recordItem.getString("bankAccountNumber")
                                                    .substring(recordItem.getString("bankAccountNumber").length() - 4) + "）");
                                            break;
                                        case 1://支付宝
                                            record.setName("支付宝" + "（"
                                                    + recordItem.getString("bankAccountNumber").substring(0,3)+"****"+
                                                    recordItem.getString("bankAccountNumber").substring(7,recordItem.getString("bankAccountNumber").length() ) + "）");
                                            break;


                                    }
                                    list.add(record);
                                }
                                withdrawRecordBean.setList(list);
                                mList.add(withdrawRecordBean);
                            }
                            showRecyclerView();

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
            mAdapter = new RecyclerCommonAdapter<WithdrawRecordBean>(mActivity, R.layout.item_withdraw_record, mList) {

                @Override
                protected void convert(ViewHolder holder, final WithdrawRecordBean withdrawRecordBean, int position) {
                    holder.setText(R.id.tv_item_withdraw_record_month, withdrawRecordBean.getMonth());
                    LinearLayout containerLL = holder.getView(R.id.ll_item_withdraw_record_container);
                    containerLL.removeAllViews();
                    for (int i = 0; i < withdrawRecordBean.getList().size(); i++) {
                        final WithdrawRecordBean.Record record = withdrawRecordBean.getList().get(i);
                        View view = mInflater.inflate(R.layout.item_withdraw_record_recrd, null);
                        TextView nameTV = view.findViewById(R.id.tv_item_withdraw_record_bank_name);
                        TextView moneyTV = view.findViewById(R.id.tv_item_withdraw_record_money);
                        TextView timeTV = view.findViewById(R.id.tv_item_withdraw_record_time);
                        TextView statusTV = view.findViewById(R.id.tv_item_withdraw_record_status);
                        nameTV.setText(record.getName());
                        moneyTV.setText(record.getMoney());
                        timeTV.setText(record.getTime());
                        switch (record.getStatus()) {
                            case 0:
                                statusTV.setText("提现失败");
                                statusTV.setTextColor(ContextCompat.getColor(mActivity, R.color.color_withdraw_red));
                                break;
                            case 1:
                                statusTV.setText("提现成功");
                                statusTV.setTextColor(ContextCompat.getColor(mActivity, R.color.color_withdraw_blue));
                                break;
                            case 2:
                                statusTV.setText("提现中");
                                statusTV.setTextColor(ContextCompat.getColor(mActivity, R.color.color_withdraw_yellow));
                                break;

                        }
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WithdrawDetailActivity.open(mActivity, record.getId());

                            }
                        });
                        containerLL.addView(view);
                    }


                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
        } else {
            mRV.setAdapter(mAdapter);
        }

    }
}

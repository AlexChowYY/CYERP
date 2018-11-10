package com.facewarrant.fw.ui.account;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

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
public class ChooseCountryActivity extends BaseActivity {
    private static final String TAG = "ChooseCountryActivity";
    private static final String INDEX_STRING_TOP = "↑";
    @BindView(R.id.rv_activity_choose_country)
    RecyclerView mRV;
    @BindView(R.id.ib_activity_choose_country)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;
    @BindView(R.id.iv_activity_choose_country_back)
    ImageView mBackIV;
    private RecyclerCommonAdapter<PinyinBean> mAdapter;
    private List<PinyinBean> mDatas = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_country;
    }

    @Override
    public void initData() {
        getCountries();

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
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_choose_face, mDatas) {
                @Override
                protected void convert(ViewHolder holder, final PinyinBean pinyinBean, final int position) {
                    holder.setText(R.id.tv_item_choose_face_name, pinyinBean.getName());
                    final ImageView selectIV = holder.getView(R.id.iv_item_choose_face);
                    selectIV.setVisibility(View.GONE);
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(Constant.ID, pinyinBean.getId());
                            intent.putExtra(Constant.COUNTRY, pinyinBean.getName());
                            intent.putExtra(Constant.CODE, pinyinBean.getContent());
                            setResult(100, intent);
                            finish();
                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(new SuspensionDecoration(mActivity, mDatas).setColorTitleBg(ContextCompat.getColor(mActivity, R.color.colorBg)));
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
            mIB.setmPressedShowTextView(mHintTV)//设置HintTextView
                    .setNeedRealIndex(true)//设置需要真实的索引
                    .setmLayoutManager(linearLayoutManager);
            mIB.setmSourceDatas(mDatas)//设置数据
                    .invalidate();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void getCountries() {
        final Map<String, String> map = new HashMap<>();
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getCountries(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONArray result = data.getJSONArray("result");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject resultItem = result.getJSONObject(i);
                                JSONArray countryArray = resultItem.getJSONArray("countryList");
                                for (int i1 = 0; i1 < countryArray.length(); i1++) {
                                    PinyinBean pinyinBean = new PinyinBean();
                                    JSONObject countryItem = countryArray.getJSONObject(i1);
                                    pinyinBean.setName(countryItem.getString("name"));
                                    if (Character.isDigit(resultItem.getString("first").charAt(0))) {
                                        pinyinBean.setTop(true).setBaseIndexTag(INDEX_STRING_TOP);
                                    }
                                    pinyinBean.setContent(countryItem.getString("value"));
                                    pinyinBean.setId(countryItem.getString("id"));
                                    mDatas.add(pinyinBean);
                                }
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


}




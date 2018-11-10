package com.facewarrant.fw.ui.personal;

import android.app.Activity;
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
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class AllBrandActivity extends BaseActivity {
    private static final String TAG = "AllBrandActivity";
    @BindView(R.id.rv_activity_all_brand)
    RecyclerView mRV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.ib_activity_choose_country)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;


    private RecyclerCommonAdapter<PinyinBean> mAdapter;
    private List<PinyinBean> mDatas = new ArrayList<>();

    private String mID;
    private static final String INDEX_STRING_TOP = "↑";


    @Override
    protected int getContentViewId() {
        return R.layout.activity_all_brand;

    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.all_brand);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mID = getIntent().getExtras().getString(Constant.ID);
            getAllBrand();
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

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_choose_face, mDatas) {
                @Override
                protected void convert(ViewHolder holder, PinyinBean pinyinBean, int position) {
                    holder.setText(R.id.tv_item_choose_face_name, pinyinBean.getName());
                    ImageView selectIV = holder.getView(R.id.iv_item_choose_face);
                    selectIV.setVisibility(View.GONE);
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(mActivity, BrandDetailActivity.class));

                        }
                    });


                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.addItemDecoration(new SuspensionDecoration(mActivity, mDatas).setColorTitleBg(ContextCompat.getColor(mActivity, R.color.colorBg)));
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
            mRV.setAdapter(mAdapter);
            mIB.setmPressedShowTextView(mHintTV)//设置HintTextView
                    .setNeedRealIndex(true)//设置需要真实的索引
                    .setmLayoutManager(linearLayoutManager);
            mIB.setmSourceDatas(mDatas)//设置数据
                    .invalidate();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void getAllBrand() {
        final Map<String, String> map = new HashMap<>();
        map.put("faceId", mID);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getFaceBrand(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        List<String> keyList = new ArrayList<>();
                        Iterator keys = result.keys();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            keyList.add(key);
                        }
                        for (int i = 0; i < keyList.size(); i++) {
                            JSONArray itemList = result.getJSONArray(keyList.get(i));
                            for (int j = 0; j < itemList.length(); j++) {
                                JSONObject item = itemList.getJSONObject(j);
                                PinyinBean pinyinBean = new PinyinBean();
                                pinyinBean.setId(item.getString("brandId"));
                                pinyinBean.setName(item.getString("brandName"));
                                if (Character.isDigit(keyList.get(i).charAt(0))) {
                                    pinyinBean.setTop(true).setBaseIndexTag(INDEX_STRING_TOP);
                                }
                                mDatas.add(pinyinBean);
                            }
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

    public static void open(Activity activity, String id) {
        Intent intent = new Intent(activity, AllBrandActivity.class);
        intent.putExtra(Constant.ID, id);
        activity.startActivity(intent);
    }


}

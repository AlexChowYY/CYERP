package com.facewarrant.fw.ui.activity.warrantIt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
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
public class TypeCommonActivity extends BaseActivity {
    private static final String TAG = "TypeCommonActivity";
    @BindView(R.id.rv_activity_type_common)
    RecyclerView mRV;
    @BindView(R.id.ib_activity_type_common)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;
    @BindView(R.id.ll_activity_type_common_search)
    LinearLayout mSearchLL;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    private static final String INDEX_STRING_TOP = "↑";
    private RecyclerCommonAdapter<PinyinBean> mAdapter;
    private List<PinyinBean> mDatas = new ArrayList<>();
    /**
     * 品牌id
     */
    private String mBrandId;

    /**
     * 大类id
     */
    private String mBigTypeId;
    /**
     * 小类id
     */
    private String mSmallId;


    private int mType;
    /**
     *
     */
    public static final int TYPE_BRAND = 3;
    public static final int TYPE_BIG_TYPE = 0;
    public static final int TYPE_SMALL_TYPE = 1;
    public static final int TYPE_NAME = 2;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_type_common;
    }

    @Override
    public void initData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            mType = getIntent().getExtras().getInt(Constant.TYPE);
            mBrandId = getIntent().getExtras().getString(Constant.BRAND_ID);
            mBigTypeId = getIntent().getExtras().getString(Constant.BIG_TYPE_ID);
            mSmallId = getIntent().getExtras().getString(Constant.SMALL_TYPE_ID);
            switch (mType) {
                case TYPE_BRAND:
                    mSearchLL.setVisibility(View.VISIBLE);
                    mTitleTV.setText(R.string.brand);
                    getAllBrands("");

                    break;
                case TYPE_BIG_TYPE:
                    mTitleTV.setText(R.string.big_type);
                    getAllType(0);

                    break;
                case TYPE_SMALL_TYPE:
                    mTitleTV.setText(R.string.small_type);
                    getAllType(1);
                    break;
                case TYPE_NAME:
                    mTitleTV.setText(R.string.name_definition);
                    getAllType(2);
                    break;
            }

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

    public static void open(Activity activity, int type, String brandId, String bId, String sId, int RequestCode) {
        Intent intent = new Intent(activity, TypeCommonActivity.class);
        intent.putExtra(Constant.TYPE, type);
        intent.putExtra(Constant.BRAND_ID, brandId);
        intent.putExtra(Constant.BIG_TYPE_ID, bId);
        intent.putExtra(Constant.SMALL_TYPE_ID, sId);
        intent.putExtra(Constant.CODE, RequestCode);
        activity.startActivityForResult(intent, RequestCode);

    }

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_choose_face, mDatas) {
                @Override
                protected void convert(ViewHolder holder, final PinyinBean pinyinBean, int position) {
                    holder.setText(R.id.tv_item_choose_face_name, pinyinBean.getName());
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(Constant.ID, pinyinBean.getId());
                            intent.putExtra(Constant.NAME, pinyinBean.getName());
                            setResult(RESULT_OK, intent);
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
                    .setmLayoutManager(linearLayoutManager).setTop(0);
            mIB.setmSourceDatas(mDatas)//设置数据
                    .invalidate();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 获取所有品牌
     */
    private void getAllBrands(String keywords) {
        final Map<String, String> map = new HashMap<>();
        map.put("searchCondition", keywords);
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getAllBrands(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                Iterator<String> it = result.keys();
                                List<String> keyList = new ArrayList<>();
                                while (it.hasNext()) {
                                    keyList.add(it.next());
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

    /**
     * 获取所有类型
     */
    private void getAllType(int flag) {
        final Map<String, String> map = new HashMap<>();
        map.put("flag", flag + "");
        map.put("brandId", mBrandId);
        map.put("btypeId", mBigTypeId);
        map.put("stypeId", mSmallId);
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getTypeAndGoodName(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                Iterator<String> it = result.keys();
                                List<String> keyList = new ArrayList<>();
                                while (it.hasNext()) {
                                    keyList.add(it.next());
                                }
                                for (int i = 0; i < keyList.size(); i++) {
                                    JSONArray itemList = result.getJSONArray(keyList.get(i));
                                    for (int j = 0; j < itemList.length(); j++) {
                                        JSONObject item = itemList.getJSONObject(j);
                                        PinyinBean pinyinBean = new PinyinBean();
                                        switch (mType) {
                                            case TYPE_BIG_TYPE:
                                                pinyinBean.setId(item.getString("btypeId"));
                                                pinyinBean.setName(item.getString("btypeName"));
                                                break;
                                            case TYPE_SMALL_TYPE:
                                                pinyinBean.setId(item.getString("stypeId"));
                                                pinyinBean.setName(item.getString("stypeName"));
                                                break;
                                            case TYPE_NAME:
                                                pinyinBean.setId(item.getString("goodsId"));
                                                pinyinBean.setName(item.getString("goodName"));
                                                break;

                                        }

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
}

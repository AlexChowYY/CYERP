package com.facewarrant.fw.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.adapter.vp.VpFragmentAdapter;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.event.UpdateGroupMemberEvent;
import com.facewarrant.fw.event.UpdateSearchHistoryEvent;
import com.facewarrant.fw.event.UpdateSearchLibraryEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
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
public class SearchLibraryActivity extends BaseActivity {
    private static final String TAG = "SearchLibraryActivity";
    @BindView(R.id.iv_activity_search_library_back)
    ImageView mBackIV;
    @BindView(R.id.et_activity_search_library_search)
    EditText mSearchET;
    @BindView(R.id.tv_activity_search_library_all)
    TextView mAllTV;
    @BindView(R.id.tfl_activity_search_library)
    TagFlowLayout mLibraryTFL;
    @BindView(R.id.mi_activity_search_library)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.vp_activity_search_library)
    ViewPager mViewPager;
    @BindView(R.id.rv_activity_search_library)
    RecyclerView mRV;
    @BindView(R.id.ib_activity_search_library)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;
    @BindView(R.id.dl_activity_search_library)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.iv_activity_search_library_close)
    ImageView mCloseIV;
    @BindView(R.id.iv_activity_search_library_search)
    ImageView mSearchIV;
    private List<PinyinBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<PinyinBean> mAdapter;
    private SuspensionDecoration mDecoration;


    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<ClassifyBean> mBrandList = new ArrayList<>();
    private List<ClassifyBean> mTabList = new ArrayList<>();
    private VpFragmentAdapter mVpAdapter;

    private String mKeyword;
    private String mBId;

    private int mSelectPosition = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_search_library;
    }

    @Override
    public void initData() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mKeyword = getIntent().getExtras().getString(Constant.KEYWORD);
            mSearchET.setText(mKeyword);
            mSearchET.setSelection(mKeyword.length());
            getInfo(mKeyword);
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
        mAllTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMoreInfo(mKeyword);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        mCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });
        mSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = mSearchET.getText().toString().toString().trim();

                    if (TextUtils.isEmpty(keyword)) {
                        ToastUtil.showShort(mActivity, "请填写关键字！");
                    } else {
                        SearchResultActivity.open(mActivity, keyword);
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
        mSearchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keywords = mSearchET.getText().toString().trim();
                if (TextUtils.isEmpty(keywords)) {
                    ToastUtil.showShort(mActivity, "请输入搜索关键字！");
                } else {
                    mKeyword = keywords;
                    getInfo(keywords);
                }

            }
        });
    }

    private void initVp() {
        mFragmentList.clear();
        for (int i = 0; i < mTabList.size(); i++) {
            SearchLibraryFragment faceClassifyFragment = new SearchLibraryFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ID, mTabList.get(i).getId());
            bundle.putString(Constant.KEYWORD, mKeyword);
            bundle.putString(Constant.CODE, mBId);
            faceClassifyFragment.setArguments(bundle);
            mFragmentList.add(faceClassifyFragment);
        }
        mVpAdapter = new VpFragmentAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mVpAdapter);
        initMagicIndicator();
    }

    private void initMagicIndicator() {
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTabList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mActivity, R.color.colorFontHint));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
                simplePagerTitleView.setText(mTabList.get(index).getName());
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(ContextCompat.getColor(mActivity, R.color.font_red));
                return linePagerIndicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }


    private void getInfo(String keyWord) {
        EventBus.getDefault().post(new UpdateSearchHistoryEvent());
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("searchCondition", keyWord);
        map.put("requestType", 0 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getSearchInfo(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                JSONArray brandArray = result.getJSONArray("brandList");
                                mBrandList.clear();
                                for (int i = 0; i < brandArray.length(); i++) {
                                    JSONObject brandItem = brandArray.getJSONObject(i);
                                    ClassifyBean classifyBean = new ClassifyBean();
                                    classifyBean.setId(brandItem.getString("brandId"));
                                    classifyBean.setName(brandItem.getString("brandName"));
                                    classifyBean.setCount(brandItem.getString("releaseCount"));
                                    if (i == 0) {
                                        classifyBean.setSelect(true);
                                    }
                                    mBrandList.add(classifyBean);
                                }
                                mBId = mBrandList.get(0).getId();
                                showBrandList();
                                JSONArray groupArray = result.getJSONArray("groupsList");
                                mTabList.clear();
                                for (int i = 0; i < groupArray.length(); i++) {
                                    JSONObject group = groupArray.getJSONObject(i);
                                    ClassifyBean classifyBean = new ClassifyBean();
                                    classifyBean.setId(group.getString("groupsId"));
                                    classifyBean.setName(group.getString("groupsName"));
                                    mTabList.add(classifyBean);
                                }
                                initVp();
                            } else {


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

    private void getMoreInfo(String keyWord) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("searchCondition", keyWord);
        map.put("requestType", 1 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getSearchInfo(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mList.clear();
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
                                        pinyinBean.setContent(item.getString("releaseCount"));
                                        mList.add(pinyinBean);
                                    }
                                }
                                showRecyclerView();


                            } else {

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
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_choose_face, mList) {
                @Override
                protected void convert(ViewHolder holder, final PinyinBean pinyinBean, int position) {
                    holder.setText(R.id.tv_item_choose_face_name, pinyinBean.getName());
                    ImageView selectIV = holder.getView(R.id.iv_item_choose_face);
                    selectIV.setVisibility(View.GONE);
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UpdateSearchLibraryEvent updateSearchLibraryEvent = new UpdateSearchLibraryEvent();
                            updateSearchLibraryEvent.setBId(pinyinBean.getId());
                            EventBus.getDefault().post(updateSearchLibraryEvent);
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(mDecoration = new SuspensionDecoration(this, mList).setColorTitleBg(ContextCompat.getColor(mActivity, R.color.colorBg)));
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
            mIB.setmPressedShowTextView(mHintTV)//设置HintTextView
                    .setNeedRealIndex(true)//设置需要真实的索引
                    .setmLayoutManager(linearLayoutManager);
            mIB.setmSourceDatas(mList)//设置数据
                    .invalidate();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    public static void open(Activity activity, String keyword) {
        Intent intent = new Intent(activity, SearchLibraryActivity.class);
        intent.putExtra(Constant.KEYWORD, keyword);
        activity.startActivity(intent);
    }

    private void showBrandList() {
        mLibraryTFL.setAdapter(new TagAdapter<ClassifyBean>(mBrandList) {
            @Override
            public View getView(FlowLayout parent, int position, ClassifyBean classifyBean) {
                TextView textView = (TextView) mInflater.inflate(R.layout.item_search, null);
                textView.setText(classifyBean.getName() + "（" + classifyBean.getCount() + "）");
                textView.setBackgroundResource(R.drawable.item_brand_bg);
                if (classifyBean.isSelect()) {
                    textView.setTextColor(ContextCompat.getColor(mActivity, R.color.common_red));
                }
                textView.setTextSize(13);
                return textView;
            }
        });
        mLibraryTFL.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                mBId = mBrandList.get(position).getId();
                LogUtil.e(TAG, "bid==" + mBId);
                mBrandList.get(mSelectPosition).setSelect(false);
                mSelectPosition = position;
                mBrandList.get(position).setSelect(true);
                mLibraryTFL.onChanged();
                UpdateSearchLibraryEvent updateSearchLibraryEvent = new UpdateSearchLibraryEvent();
                updateSearchLibraryEvent.setBId(mBId);
                String keywords = mSearchET.getText().toString().trim();
                updateSearchLibraryEvent.setKeywords(keywords);
                EventBus.getDefault().post(updateSearchLibraryEvent);
                return true;
            }
        });
    }


}

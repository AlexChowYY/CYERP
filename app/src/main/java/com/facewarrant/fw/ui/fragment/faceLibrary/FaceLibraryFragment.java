package com.facewarrant.fw.ui.fragment.faceLibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.vp.VpFragmentAdapter;
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.addFace.AddFaceActivity;
import com.facewarrant.fw.ui.activity.question.QuestionActivity;
import com.facewarrant.fw.ui.activity.SearchActivity;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

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
public class FaceLibraryFragment extends BaseFragment {
    @BindView(R.id.mi_fragment_face_library)
    MagicIndicator mMagicIndicator;
    @BindView(R.id.vp_fragment_face_library)
    ViewPager mViewPager;
    @BindView(R.id.ll_fragment_face_library_search)
    LinearLayout mSearchLL;
    @BindView(R.id.iv_fragment_face_library_voice)
    ImageView mVoiceIV;
    @BindView(R.id.iv_fragment_face_library_add)
    ImageView mAddIV;
    @BindView(R.id.iv_fragment_face_library_question)
    ImageView mQuestionIV;

    private static final String TAG = "FaceLibraryFragment";
    private List<ClassifyBean> mTabList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private VpFragmentAdapter mAdapter;


    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_face_library;
    }

    @Override
    public void initData() {


        getMyGroups();


    }

    private void initVp() {
        for (int i = 0; i < mTabList.size(); i++) {
            FaceClassifyFragment faceClassifyFragment = new FaceClassifyFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ID, mTabList.get(i).getId());
            faceClassifyFragment.setArguments(bundle);
            mFragmentList.add(faceClassifyFragment);
        }
        mAdapter = new VpFragmentAdapter(getChildFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mAdapter);
        initMagicIndicator();
    }

    @Override
    public void initEvent() {
        mSearchLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.open(mActivity, SearchActivity.TYPE_TEXT, true);
            }
        });
        mVoiceIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.open(mActivity, SearchActivity.TYPE_VOICE, true);
            }
        });
        mAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mViewPager.getCurrentItem();
                if (position == 0) {
                    AddFaceActivity.open(mActivity, mTabList.get(position).getId(), mViewPager.getCurrentItem(), "1");
                } else if (position == 1) {
                    AddFaceActivity.open(mActivity, mTabList.get(position).getId(), mViewPager.getCurrentItem(), "2");
                } else {
                    AddFaceActivity.open(mActivity, mTabList.get(position).getId(), mViewPager.getCurrentItem(), "3");
                }

            }
        });
        mQuestionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, QuestionActivity.class));
            }
        });
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

    private void getMyGroups() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyGroups(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        JSONArray groupsList = result.getJSONArray("groupsList");
                        for (int i = 0; i < groupsList.length(); i++) {
                            JSONObject group = groupsList.getJSONObject(i);
                            ClassifyBean classifyBean = new ClassifyBean();
                            classifyBean.setId(group.getString("groupsId"));
                            classifyBean.setName(group.getString("groupsName"));

                            mTabList.add(classifyBean);
                        }
                        initVp();

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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

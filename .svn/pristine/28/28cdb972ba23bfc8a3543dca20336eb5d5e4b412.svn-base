package com.facewarrant.fw.ui.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.vp.VpFragmentAdapter;
import com.facewarrant.fw.base.BaseFragment;
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.NearbyStoreActivity;
import com.facewarrant.fw.ui.activity.SearchActivity;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
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
public class HomePagerFragment extends BaseFragment {
    private static final String TAG = "HomePagerFragment";
    @BindView(R.id.vp_activity_main)
    ViewPager mHomeVP;
    @BindView(R.id.ll_fragment_home_pager_search)
    LinearLayout mSearchLL;
    @BindView(R.id.mi_fragment_home_pager)
    MagicIndicator mIndicatorMI;
    @BindView(R.id.iv_fragment_home_pager_location)
    ImageView mLocationIV;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private VpFragmentAdapter mAdapter;

    private String[] mTitleArray;


    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_home_pager;
    }

    @Override
    public void initData() {
        intTabVP();
        getLimitTime();
    }

    @Override
    public void initEvent() {
        mSearchLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, SearchActivity.class));
            }
        });
        mLocationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbyStoreActivity.open(mActivity, NearbyStoreActivity.TYPE_STORE);

            }
        });


    }

    private void intTabVP() {
        mTitleArray = getResources().getStringArray(R.array.home_top_tab);
        mFragmentList.add(new LatestFragment());
        mFragmentList.add(new HotFragment());
        mFragmentList.add(new ClassifyFragment());
        mAdapter = new VpFragmentAdapter(getChildFragmentManager(), mFragmentList);
        mHomeVP.setAdapter(mAdapter);
        initMagicIndicator();

    }


    private void initMagicIndicator() {
        CommonNavigator commonNavigator = new CommonNavigator(mActivity);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleArray.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setNormalColor(ContextCompat.getColor(mActivity, R.color.colorFontHint));
                simplePagerTitleView.setSelectedColor(ContextCompat.getColor(mActivity, R.color.colorBlack));
                simplePagerTitleView.setText(mTitleArray[index]);
                simplePagerTitleView.setTextSize(17);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHomeVP.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(UIUtil.dip2px(context, 50));
                linePagerIndicator.setColors(ContextCompat.getColor(mActivity, R.color.font_red));
                return linePagerIndicator;
            }
        });
        mIndicatorMI.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(mActivity, 45);
            }
        });

        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(mIndicatorMI);
        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
        fragmentContainerHelper.setDuration(300);
        mHomeVP.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                fragmentContainerHelper.handlePageSelected(position);
            }
        });
    }


    private void getLimitTime() {
        final Map<String, String> map = new HashMap<>();
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getLimitTime(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");


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

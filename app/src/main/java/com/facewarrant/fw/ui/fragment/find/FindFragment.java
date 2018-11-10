package com.facewarrant.fw.ui.fragment.find;

import android.content.Context;
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
import com.facewarrant.fw.ui.activity.SearchActivity;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FindFragment extends BaseFragment {
    private static final String TAG = "WarrantItFragment";
    @BindView(R.id.mi_fragment_warrant_it)
    MagicIndicator mIndicatorMI;
    @BindView(R.id.vp_fragment_warrant_it)
    ViewPager mVP;
    @BindView(R.id.ll_fragment_find_search)
    LinearLayout mSearchLL;
    @BindView(R.id.iv_fragment_find_voice)
    ImageView mVoiceIV;
    @BindView(R.id.iv_fragment_find_search)
    ImageView mSearchIV;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private VpFragmentAdapter mAdapter;

    private String[] mTitleArray;

    @Override
    protected int getInflateViewId() {
        return R.layout.fragment_find;
    }

    @Override
    public void initData() {
        intTabVP();


    }

    @Override
    public void initEvent() {
        mSearchLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.open(mActivity, SearchActivity.TYPE_TEXT,false);
            }
        });
        mVoiceIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.open(mActivity, SearchActivity.TYPE_VOICE,false);
            }
        });


    }

    private void intTabVP() {
        mTitleArray = getResources().getStringArray(R.array.warrant_it_tab);
        mFragmentList.add(new NewGoodsFragment());
     //   mFragmentList.add(new FaceTicketFragment());
        mFragmentList.add(new AnswerFragment());
        mAdapter = new VpFragmentAdapter(getChildFragmentManager(), mFragmentList);
        mVP.setAdapter(mAdapter);
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
                        mVP.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                linePagerIndicator.setLineWidth(UIUtil.dip2px(context, 55));
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
                return UIUtil.dip2px(mActivity, 65);
            }
        });

        final FragmentContainerHelper fragmentContainerHelper = new FragmentContainerHelper(mIndicatorMI);
        fragmentContainerHelper.setInterpolator(new OvershootInterpolator(2.0f));
        fragmentContainerHelper.setDuration(300);
        mVP.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                fragmentContainerHelper.handlePageSelected(position);
            }
        });
    }
}

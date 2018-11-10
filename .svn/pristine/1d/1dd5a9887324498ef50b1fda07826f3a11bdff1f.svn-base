package com.facewarrant.fw.adapter.vp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by licrynoob on 2016/guide_0/28 <br>
 * Copyright (C) 2016 <br>
 * Email:licrynoob@gmail.com <p>
 * 多页面的FragmentAdapter
 */

public class VpFragmentStateAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList;

    public VpFragmentStateAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}

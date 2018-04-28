package com.gin.xjh.shin_music.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Gin on 2018/4/23.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private FragmentManager fm;

    public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragments = fragmentList;
        this.fm = fm;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

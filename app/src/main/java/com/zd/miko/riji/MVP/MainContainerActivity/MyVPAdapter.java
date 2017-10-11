package com.zd.miko.riji.MVP.MainContainerActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Miko on 2017/1/23.
 *
 */

public class MyVPAdapter extends FragmentPagerAdapter {

    private List<Fragment> viewList;

    public MyVPAdapter(FragmentManager fm, List<Fragment> viewList) {
        super(fm);
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }


    @Override
    public Fragment getItem(int position) {
        return viewList.get(position);
    }
}

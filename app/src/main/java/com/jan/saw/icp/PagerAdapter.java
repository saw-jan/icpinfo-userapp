package com.jan.saw.icp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int tabNums;
    public PagerAdapter(FragmentManager FM, int TabNums){
        super(FM);
        this.tabNums = TabNums;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Home home = new Home();
                return home;
            case 1:
                Notice notice = new Notice();
                return  notice;
            case 2:
                Post post = new Post();
                return post;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNums;
    }
}

package com.example.adapter;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.fragment.CategoryFragment;
import com.example.fragment.CityFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.ProductCategoryFragment;
import com.example.fragment.ProductCityFragment;

public class MyAdapter extends FragmentStatePagerAdapter {

    private Context myContext;
    int totalTabs;

    public MyAdapter( FragmentManager fm) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                CategoryFragment categoryFragment = new CategoryFragment();
                return categoryFragment;
            case 2:
                CityFragment cityFragment = new CityFragment();
                return cityFragment;
            case 3:
                ProductCategoryFragment productCategoryFragment = new ProductCategoryFragment();
                return productCategoryFragment;
            case 4:
                ProductCityFragment productCityFragment = new ProductCityFragment();
                return productCityFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return 5;
    }


}
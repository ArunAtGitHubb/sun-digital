package com.example.adapter;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.fragment.CategoryFragment;
import com.example.fragment.CityFragment;
import com.example.fragment.FemaleMatrimonyCategoryFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.MaleMatrimonyCategoryFragment;
import com.example.fragment.MatrimonyCategoryFragment;
import com.example.fragment.MatrimonyCityFragment;
import com.example.fragment.ProductCategoryFragment;
import com.example.fragment.ProductCityFragment;
import com.example.fragment.ServiceCategoryFragment;
import com.example.fragment.ServiceCityFragment;

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
                return new HomeFragment();
            case 1:
                return new CategoryFragment();
            case 2:
                return new CityFragment();
            case 3:
                return new ProductCategoryFragment();
            case 4:
                ProductCityFragment productCityFragment = new ProductCityFragment();
                return productCityFragment;
            case 5:
                return new ServiceCategoryFragment();
            case 6:
                return new ServiceCityFragment();
            case 7:
                return new MaleMatrimonyCategoryFragment();
            case 8:
                return new FemaleMatrimonyCategoryFragment();
            case 9:
                return new MatrimonyCityFragment();
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return 10;
    }

}
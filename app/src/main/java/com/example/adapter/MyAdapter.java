package com.example.adapter;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.fragment.CategoryFragment;
import com.example.fragment.CityFragment;
import com.example.fragment.FemaleMatrimonyCategoryFragment;
import com.example.fragment.FemaleMatrimonyReligionFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.JobOpportunitiesFragment;
import com.example.fragment.MaleMatrimonyCategoryFragment;
import com.example.fragment.MaleMatrimonyReligionFragment;
import com.example.fragment.MatrimonyCityFragment;
import com.example.fragment.MatrimonyFragment;
import com.example.fragment.ProductCategoryFragment;
import com.example.fragment.ProductCityFragment;
import com.example.fragment.ProductFragment;
import com.example.fragment.RstrntCityFragment;
import com.example.fragment.ServiceCategoryFragment;
import com.example.fragment.ServiceCityFragment;
import com.example.fragment.ServiceFragment;

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
                return new JobOpportunitiesFragment();
            case 2:
                return new ProductFragment();
            case 3:
                return new ServiceFragment();
            case 4:
                return new MatrimonyFragment();
            case 5:
                return new RstrntCityFragment();
            case 6:
                return new CategoryFragment();
            case 7:
                return new CityFragment();
            case 8:
                return new ProductCategoryFragment();
            case 9:
                return new ProductCityFragment();
            case 10:
                return new ServiceCategoryFragment();
            case 11:
                return new ServiceCityFragment();
            case 12:
                return new FemaleMatrimonyCategoryFragment();
            case 13:
                return new MaleMatrimonyCategoryFragment();
            case 14:
                return new MatrimonyCityFragment();
            case 15:
                return new FemaleMatrimonyReligionFragment();
            case 16:
                return new MaleMatrimonyReligionFragment();
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return 17;
    }

}
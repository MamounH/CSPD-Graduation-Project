package com.example.civilaffairs.ui.DepartmentServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.civilaffairs.ui.DepartmentServices.Provider.ProviderFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private String services , provided ;
    private String[] tabArray ;

     TabPagerAdapter(@NonNull FragmentManager fm, String services ,String provided) {
        super(fm);
        this.services = services;
        this.provided= provided;


        tabArray = new String[]{services, provided};

    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabArray[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return new ServicesFragment();

            case 1:
                return new ProviderFragment();


        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

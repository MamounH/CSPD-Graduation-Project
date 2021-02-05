package com.example.civilaffairs.ui.DepartmentServices.PassportServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.civilaffairs.ui.DepartmentServices.PassportServices.AddEnum.AddEnumFragment;
import com.example.civilaffairs.ui.DepartmentServices.PassportServices.Renewal.PassportRenewalFragment;

public class TapPassportPagerAdapter extends FragmentStatePagerAdapter {

     String services , provided ;
     String[] tabArray ;

    TapPassportPagerAdapter(@NonNull FragmentManager fm, String services , String provided) {
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
                return new PassportRenewalFragment();
            case 1:
                return new AddEnumFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

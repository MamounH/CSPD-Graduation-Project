package com.example.admincivil.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private String add , delete ;
    private String[] tabArray ;

     TabPagerAdapter(@NonNull FragmentManager fm, String add , String delete) {
        super(fm);
        this.add = add;
        this.delete= delete;

        tabArray = new String[]{add, delete};

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
                return new AddEmployeeFragment();

            case 1:
                return new DeleteEmployeeFragment();


        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

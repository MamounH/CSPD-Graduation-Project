package com.example.civilaffairs.ui.DepartmentServices.BirthServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.civilaffairs.ui.DepartmentServices.BirthServices.DeathCertificate.IssueDeathCertificateFragment;
import com.example.civilaffairs.ui.DepartmentServices.BirthServices.IssueBirth.IssueBirthFragment;
import com.example.civilaffairs.ui.DepartmentServices.BirthServices.OutsideBirth.IssuanceOfBirthOutsideFragment;

public class TapBirthPagerAdapter extends FragmentStatePagerAdapter {

    private String services , provided, followup ;
    private String[] tabArray ;

    TapBirthPagerAdapter(@NonNull FragmentManager fm, String services , String provided, String followup) {
        super(fm);
        this.services = services;
        this.provided= provided;
        this.followup= followup;

        tabArray = new String[]{services, provided, followup};

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
                return new IssueBirthFragment();

            case 1:
                return new IssueDeathCertificateFragment();

            case 2:
                return new IssuanceOfBirthOutsideFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
package com.example.civilaffairs.ui.DepartmentServices.Provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.civilaffairs.ui.DepartmentServices.Provider.CertificateProvider.CertificateProviderFragment;
import com.example.civilaffairs.ui.DepartmentServices.Provider.FamilyProvider.FamilyProvidedFragment;
import com.example.civilaffairs.ui.DepartmentServices.Provider.PassportProvider.PassporrtProvidreFragment;

public class TabProviderPagerAdapter extends FragmentStatePagerAdapter {

    private String services , provided , certificate ;
    private String[] tabArray ;

     TabProviderPagerAdapter(@NonNull FragmentManager fm, String services, String provided, String certificate) {
        super(fm);
        this.services = services;
        this.provided= provided;
         this.certificate = certificate ;

        tabArray = new String[]{services, provided, certificate};

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
                return new FamilyProvidedFragment();

            case 1:
                return new PassporrtProvidreFragment();

            case 2:
                return new CertificateProviderFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

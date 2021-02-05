package com.example.admincivil.Employee.CertificateServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.admincivil.Employee.CertificateServices.IssueBirth.IssueBirthFragment;
import com.example.admincivil.Employee.CertificateServices.IssueDeath.IssueDeathFragment;
import com.example.admincivil.Employee.CertificateServices.IssueOutside.IssueBirthOutsideFragment;


public class TabPagerAdpterCertificate extends FragmentStatePagerAdapter {


    private String birth , Death, outside ;
    private String[] tabArray ;

    TabPagerAdpterCertificate(@NonNull FragmentManager fm, String birth , String Death, String outside) {
        super(fm);
        this.birth = birth;
        this.Death= Death;
        this.outside = outside;

        tabArray = new String[]{birth, Death,outside};

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
                return new IssueDeathFragment();

            case 2:
                return new IssueBirthOutsideFragment();


        }

      return new IssueBirthFragment();

    }

    @Override
    public int getCount() {
        return 3;
    }
}

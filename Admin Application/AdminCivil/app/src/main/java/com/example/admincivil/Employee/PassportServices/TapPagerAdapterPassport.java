package com.example.admincivil.Employee.PassportServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.admincivil.Employee.CertificateServices.IssueBirth.IssueBirthFragment;
import com.example.admincivil.Employee.CertificateServices.IssueDeath.IssueDeathFragment;
import com.example.admincivil.Employee.CertificateServices.IssueOutside.IssueBirthOutsideFragment;
import com.example.admincivil.Employee.PassportServices.AddEnum.AddEnumFragment;
import com.example.admincivil.Employee.PassportServices.Renewal.RenewalFragment;

public class TapPagerAdapterPassport extends FragmentStatePagerAdapter {

    private String birth , Death ;
    private String[] tabArray ;

    TapPagerAdapterPassport(@NonNull FragmentManager fm, String birth , String Death) {
        super(fm);
        this.birth = birth;
        this.Death= Death;


        tabArray = new String[]{birth, Death};

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
                return new AddEnumFragment();

            case 1:
                return new RenewalFragment();


        }

        return new IssueBirthFragment();

    }

    @Override
    public int getCount() {
        return 2;
    }
}

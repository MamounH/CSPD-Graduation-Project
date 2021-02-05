package com.example.civilaffairs.ui.DepartmentServices.PassportServices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.civilaffairs.R;
import com.example.civilaffairs.ui.DepartmentServices.TabPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class PassportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);

        TapPassportPagerAdapter adapter = new TapPassportPagerAdapter(this.getSupportFragmentManager(),this.getString(R.string.passport_renewal),
                this.getString(R.string.add_enum_to_passport));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

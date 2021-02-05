package com.example.civilaffairs.ui.DepartmentServices.BirthServices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.civilaffairs.R;
import com.example.civilaffairs.ui.DepartmentServices.TabPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class BirthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth);

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);

        TapBirthPagerAdapter adapter = new TapBirthPagerAdapter(this.getSupportFragmentManager(), this.getString(R.string.issue_birth_certificate),
                this.getString(R.string.issue_death_certificate), this.getString(R.string.issuance_of_birth_certificate_occurred_ouside_jordan));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}

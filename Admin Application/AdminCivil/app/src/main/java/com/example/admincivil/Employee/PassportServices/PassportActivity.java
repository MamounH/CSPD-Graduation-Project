package com.example.admincivil.Employee.PassportServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.admincivil.Employee.CertificateServices.CertificateActivity;
import com.example.admincivil.Employee.CertificateServices.TabPagerAdpterCertificate;
import com.example.admincivil.Employee.FamilyServices.FamilyActivity;
import com.example.admincivil.LoginActivity;
import com.example.admincivil.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

public class PassportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.view_pager);

        TapPagerAdapterPassport adapter = new TapPagerAdapterPassport(this.getSupportFragmentManager(),
                "Add Enum","Renewal");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.status_login), MODE_PRIVATE).edit();
            editor.putString("key", "0");
            editor.apply();
            FirebaseMessaging.getInstance().unsubscribeFromTopic("Admin");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        else if (id == R.id.passport) {
            startActivity(new Intent(this, PassportActivity.class));
            finish();
        }else if (id == R.id.Family){
            startActivity(new Intent(this, FamilyActivity.class));
            finish();
        }else if (id == R.id.certificate){
            startActivity(new Intent(this, CertificateActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

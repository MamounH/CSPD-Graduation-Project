package com.example.civilaffairs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;
import java.util.Objects;


public class HomeActivity extends AppCompatActivity implements ReplaceFragment {

    private AppBarConfiguration mAppBarConfiguration;
    TextView tx_email, tx_national;
    ImageView img_profile;
    DatabaseReference refHomeActivity;
    String nationalID;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_department, R.id.nav_user_guide, R.id.nav_open_times, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View view = navigationView.getHeaderView(0);

        tx_email = view.findViewById(R.id.tx_email);
        tx_national = view.findViewById(R.id.tx_national);
        img_profile = view.findViewById(R.id.img_profile);
        refHomeActivity = FirebaseDatabase.getInstance().getReference("Users");

        SharedPreferences prefs = getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE);
        nationalID = prefs.getString("national_number", null);
        tx_national.setText(nationalID);

        receiveNotificationTopic(nationalID);

        refHomeActivity.child(nationalID).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tx_email.setText(Objects.requireNonNull(dataSnapshot.child("nameFirst").getValue()).toString() + " "
                        + Objects.requireNonNull(dataSnapshot.child("nameMiddle").getValue()).toString() + " "
                        + Objects.requireNonNull(dataSnapshot.child("nameLast").getValue()).toString());
                String imgUrl = (String) dataSnapshot.child("urlImage").getValue();
                Glide.with(getApplicationContext()).load(imgUrl).into(img_profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void receiveNotificationTopic(String nationalID) {
        FirebaseMessaging.getInstance().subscribeToTopic(nationalID).addOnSuccessListener(aVoid ->
                Log.d("Topic","Ready Topic"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.status_login), MODE_PRIVATE).edit();
            editor.putString("key", "0");
            editor.apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }else if (item.getItemId() == R.id.action_change){
                setLocale("ar");
        }else if (item.getItemId() == R.id.action_english){
            setLocale("en");
        }

        return super.onOptionsItemSelected(item);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, HomeActivity.class);
        finish();
        startActivity(refresh);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();

    }

    @Override
    public void showFragment(FragmentSelect fragmentSelect) {

        if (fragmentSelect == FragmentSelect.fragment_services) {
            navController.navigate(R.id.nav_department);
        } else if (fragmentSelect == FragmentSelect.fragment_guide) {
            navController.navigate(R.id.nav_user_guide);
        } else if (fragmentSelect == FragmentSelect.fragment_time) {
            navController.navigate(R.id.nav_open_times);
        } else if (fragmentSelect == FragmentSelect.fragment_setting) {
            navController.navigate(R.id.nav_setting);
        }

    }

}

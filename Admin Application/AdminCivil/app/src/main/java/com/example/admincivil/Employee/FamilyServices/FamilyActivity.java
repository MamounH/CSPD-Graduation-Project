package com.example.admincivil.Employee.FamilyServices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincivil.Employee.CertificateServices.CertificateActivity;
import com.example.admincivil.Employee.PassportServices.PassportActivity;
import com.example.admincivil.LoginActivity;
import com.example.admincivil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FamilyActivity extends AppCompatActivity {

    RecyclerView recyclerEmployee;
    AdapterFamily adapterFamily;
    ValueEventListener valueEventLis, v;
    DatabaseReference refEmployee, ref;
    ProgressDialog dialog;
    List<String> list ;
    RecyclerView.LayoutManager layoutManager;
    FamilyClass familyClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emplyee_home);

        FirebaseMessaging.getInstance().subscribeToTopic("Admin").addOnSuccessListener(aVoid ->
                Toast.makeText(FamilyActivity.this, "done", Toast.LENGTH_SHORT).show());

        dialog = new ProgressDialog(this);
        list = new ArrayList<>();
        familyClass = new FamilyClass();

        dialog.setTitle(getString(R.string.download_data));
        dialog.setMessage(getString(R.string.please_wait) + " ...");
        dialog.show();

        refEmployee = FirebaseDatabase.getInstance().getReference("Services").child("Family");
        ref = FirebaseDatabase.getInstance().getReference("StatusRequest");



        recyclerEmployee = findViewById(R.id.recycler_employee);
        layoutManager = new LinearLayoutManager(this);
        recyclerEmployee.setLayoutManager(layoutManager);
        adapterFamily = new AdapterFamily(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        v = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    valueEventLis = refEmployee.child(Objects.requireNonNull(snapshot.getKey())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<FamilyClass> employeeList = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                employeeList.add(snapshot.getValue(FamilyClass.class));
                            }
                            adapterFamily.setFamilyList(employeeList);
                            recyclerEmployee.setAdapter(adapterFamily);

                            dialog.dismiss();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(FamilyActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (valueEventLis != null)
            refEmployee.removeEventListener(valueEventLis);
        dialog.dismiss();

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
            startActivity(new Intent(FamilyActivity.this, LoginActivity.class));
            finish();
        }
        else if (id == R.id.passport) {
            startActivity(new Intent(getApplicationContext(), PassportActivity.class));
            finish();
        }else if (id == R.id.Family){
            startActivity(new Intent(getApplicationContext(), FamilyActivity.class));
            finish();
        }else if (id == R.id.certificate){
            startActivity(new Intent(getApplicationContext(), CertificateActivity.class));
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}

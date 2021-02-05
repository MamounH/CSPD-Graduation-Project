package com.example.admincivil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.admincivil.Admin.AdminHomeActivity;
import com.example.admincivil.Employee.FamilyServices.FamilyActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText et_username, et_password;
    RadioButton rd_admin, rd_emp;
    DatabaseReference refLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refLogin = FirebaseDatabase.getInstance().getReference("Admin");

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        rd_admin = findViewById(R.id.rd_admin);
        rd_emp = findViewById(R.id.rd_emp);

        findViewById(R.id.bt_login).setOnClickListener(v -> {
                    if (checkValidation()) {

                if (rd_admin.isChecked()) {
                    refLogin = FirebaseDatabase.getInstance().getReference("Admin");
                } else if (rd_emp.isChecked()) {
                    refLogin = FirebaseDatabase.getInstance().getReference("Employee");
                }
                refLogin.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String user = et_username.getText().toString();
                        String pass = et_password.getText().toString();

                        if (dataSnapshot.hasChild(user)) {

                            String password = Objects.requireNonNull(dataSnapshot.child(user).child("password").getValue()).toString();

                            if (pass.equals(password)) {
                                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.status_login), MODE_PRIVATE).edit();

                                Toast.makeText(LoginActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                if (rd_admin.isChecked()) {
                                    editor.putString("key", "1");
                                    editor.apply();
                                    startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                                    finish();
                                } else if (rd_emp.isChecked()) {
                                    editor.putString("key", "2");
                                    editor.apply();
                                    startActivity(new Intent(LoginActivity.this, FamilyActivity.class));
                                    finish();
                                }


                            } else {
                                et_password.setError("Enter your password correctly!");
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.your_username_not_created), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });

            }


        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(getString(R.string.status_login), MODE_PRIVATE);
        String statusLogin = prefs.getString("key", "0");

        if (statusLogin.equals("1")) {
            startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
            finish();
        } else if (statusLogin.equals("2")) {
            startActivity(new Intent(LoginActivity.this, FamilyActivity.class));
            finish();
        }

    }

    boolean checkValidation() {

        if (et_username.getText().toString().isEmpty()) {
            et_username.setError(getString(R.string.enter_your_user_name));
        } else if (et_password.getText().toString().isEmpty()) {
            et_password.setError(getString(R.string.enter_your_password));
        } else {
            return true;
        }
        return false;
    }
}

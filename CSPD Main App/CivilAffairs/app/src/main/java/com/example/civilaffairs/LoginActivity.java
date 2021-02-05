package com.example.civilaffairs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    List<String> listSpinner;
    Spinner spinner;
    Button bt_signUp, bt_signIn;
    EditText et_ID, et_password;
    TextView tx_forgot, tx_guide;
    DatabaseReference refLogin;
    String id, password;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hidden status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Hidden Action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        refLogin = FirebaseDatabase.getInstance().getReference("Users");

        bt_signIn = findViewById(R.id.bt_sign_in);
        bt_signUp = findViewById(R.id.bt_sign_up);
        et_ID = findViewById(R.id.et_ID);
        et_password = findViewById(R.id.et_password);
        tx_forgot = findViewById(R.id.tx_forgot);
        tx_guide = findViewById(R.id.tx_guide);
        spinner = findViewById(R.id.spinner_user);

        listSpinner = new ArrayList<>();
        dialog = new ProgressDialog(LoginActivity.this);

        //Add item to arrayList
        listSpinner.add(getString(R.string.citizen));
        listSpinner.add(getString(R.string.employee));

        //Add array list to Spinner view
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Moving on from LoginActivity to SignUpActivity
        bt_signUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        bt_signIn.setOnClickListener(v -> {

            dialog.setTitle(getString(R.string.loading));
            dialog.setMessage(getString(R.string.sign_in) + "...");
            dialog.show();

            et_ID.setError(null);
            et_password.setError(null);

            if (et_ID.getText() == null) {
                et_ID.setError(getString(R.string.enter_your_nationality_number));
            } else if (et_password.getText() == null) {
                et_password.setError(getString(R.string.enter_your_password));
            } else {
                refLogin.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(et_ID.getText().toString())) {
                            id = et_ID.getText().toString();
                            password = Objects.requireNonNull(String.valueOf(dataSnapshot.child(id).child("password").getValue()));
                            if (password.equals(String.valueOf(et_password.getText()))) {
                                dialog.dismiss();
                                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.status_login), MODE_PRIVATE).edit();
                                editor.putString("key", "1");
                                editor.apply();

                                SharedPreferences.Editor editor1 = getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE).edit();
                                editor1.putString("national_number" , et_ID.getText().toString());
                                editor1.apply();

                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                et_ID.setError(getString(R.string.something_went_wrong));
                                et_password.setError(getString(R.string.something_went_wrong));
                                dialog.dismiss();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.create_an_account_first), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        tx_forgot.setOnClickListener(v->startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

    }


}

package com.example.civilaffairs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText et_national, et_phone, et_code, et_password, et_confirm_password;
    Button bt_verification, bt_check, btn_change_password;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String numberPhone, mVerificationId;
    ProgressDialog dialog;
    DatabaseReference refForgot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();
        refForgot = FirebaseDatabase.getInstance().getReference("Users");
        dialog = new ProgressDialog(ForgotPasswordActivity.this);

        et_national = findViewById(R.id.et_forgot_national);
        et_phone = findViewById(R.id.et_forgot_phone);
        et_code = findViewById(R.id.et_code);
        bt_check = findViewById(R.id.bt_check);


        bt_verification = findViewById(R.id.bt_verification);

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+9627")) {
                    et_phone.setText("+9627");
                    Selection.setSelection(et_phone.getText(), et_phone.getText().length());

                }

            }
        });

        bt_verification.setOnClickListener(v -> {

            if (checkValidation()) {
                dialog.setTitle(getString(R.string.check_validation));
                dialog.setMessage(getString(R.string.send_message_verification) + " ...");
                dialog.show();
                numberPhone = et_phone.getText().toString();
                et_phone.setEnabled(false);

                refForgot.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(et_national.getText().toString())) {
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    numberPhone,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    ForgotPasswordActivity.this, // Activity (for callback binding)
                                    mCallbacks);
                        } else {
                            et_national.setError(getString(R.string.there_is_no_account_for_this_national_number));
                            dialog.dismiss();
                            et_phone.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ForgotPasswordActivity.this, getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
                    }
                });


            }


        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                bt_verification.setVisibility(View.VISIBLE);

                et_phone.setEnabled(true);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = s;
                mResendToken = forceResendingToken;
                et_code.setVisibility(View.VISIBLE);

                dialog.dismiss();
                bt_check.setVisibility(View.VISIBLE);
            }
        };


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                bt_verification.setVisibility(View.VISIBLE);
                bt_check.setVisibility(View.GONE);
                ChangePassword();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.the_verification_code_entered_was_invalid), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ChangePassword() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, viewGroup, false);
        et_password = dialogView.findViewById(R.id.et_password);
        et_confirm_password = dialogView.findViewById(R.id.et_confirm_password);
        btn_change_password = dialogView.findViewById(R.id.bt_change_password);

        dialog.setView(dialogView);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        btn_change_password.setOnClickListener(v->{
            if (!et_password.getText().toString().isEmpty()&&!et_confirm_password.getText().toString().isEmpty()){

            if (et_password.getText().toString().equals(et_confirm_password.getText().toString()))
            refForgot.child(et_national.getText().toString()).child("password").setValue(et_password.getText().toString()).addOnSuccessListener(aVoid -> {
                startActivity(new Intent(ForgotPasswordActivity.this, HomeActivity.class));
                finish();
            });
            else
                et_password.setError(getString(R.string.password_not_match));
                Toast.makeText(this, getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean checkValidation() {
        if (et_national.getText().toString().isEmpty()) {
            et_national.setError(getString(R.string.enter_your_nationality_number));
        } else if (et_phone.getText().toString().isEmpty()) {
            et_phone.setError(getString(R.string.enter_your_phone_number));
        } else {
            et_phone.setError(null);
            et_national.setError(null);
            return true;
        }

        return false;
    }


}

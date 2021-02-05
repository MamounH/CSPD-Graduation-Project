package com.example.admincivil.Admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admincivil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddEmployeeFragment extends Fragment {

    private EditText et_new_username, et_new_password;
    private DatabaseReference ref_new_employee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_employee, container, false);

        et_new_username = view.findViewById(R.id.et_new_username);
        et_new_password = view.findViewById(R.id.et_new_password);

        ref_new_employee = FirebaseDatabase.getInstance().getReference("Employee");

        view.findViewById(R.id.bt_create_new).setOnClickListener(v -> {

            if (checkValidation()) {

                ref_new_employee.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(et_new_username.getText().toString())) {
                            Toast.makeText(getContext(), getString(R.string.the_user_already_exists), Toast.LENGTH_SHORT).show();
                        } else {
                            checkExists();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        et_new_username.setText("");
                        et_new_password.setText("");
                    }
                });
            }
        });


        return view;
    }


    private boolean checkValidation() {

        if (et_new_username.getText().toString().isEmpty()) {
            et_new_username.setError(getString(R.string.please_enter_username_new_employee));
        } else if (et_new_password.getText().toString().isEmpty()) {
            et_new_password.setError(getString(R.string.please_enter_password_for_employee));
        } else {
            et_new_username.setError(null);
            et_new_password.setError(null);
            return true;
        }

        return false;
    }

    private void checkExists() {

        ref_new_employee.child(et_new_username.getText().toString())
                .child("password").setValue(et_new_password.getText().toString())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), getString(R.string.successful_upload_data), Toast.LENGTH_SHORT).show());

    }
}

package com.example.admincivil.Admin;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.admincivil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteEmployeeFragment extends Fragment {

    private RecyclerView recyclerDelete;
    private AdapterDelete adapterDelete;
    private DatabaseReference refDelete;
    private ValueEventListener valueEventLis;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delete_employee, container, false);
        dialog = new ProgressDialog(getContext());
        dialog.setTitle(getString(R.string.download_data));
        dialog.setMessage(getString(R.string.please_wait) + " ...");
        dialog.show();
        refDelete = FirebaseDatabase.getInstance().getReference("Employee");
        recyclerDelete = view.findViewById(R.id.recycler_delete);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerDelete.setLayoutManager(layoutManager);
        adapterDelete = new AdapterDelete();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
         valueEventLis = refDelete.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> employeeList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    employeeList.add(snapshot.getKey());
                    adapterDelete.setEmployeeList(employeeList);
                    recyclerDelete.setAdapter(adapterDelete);
                }
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (valueEventLis != null)
            refDelete.removeEventListener(valueEventLis);
        dialog.dismiss();

    }
}

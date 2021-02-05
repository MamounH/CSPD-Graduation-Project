package com.example.admincivil.Employee.CertificateServices.IssueOutside;

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

import com.example.admincivil.Employee.CertificateServices.IssueDeath.IssueDeathAdapter;
import com.example.admincivil.Employee.CertificateServices.IssueDeath.IssueDeathClass;
import com.example.admincivil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class IssueBirthOutsideFragment extends Fragment {

    private RecyclerView recyclerOutside;
    private IssueOutsideAdapter outsideAdapter;
    private ValueEventListener valueEventLis, v;
    private DatabaseReference refOutside, ref;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_issue_birth_outside, container, false);

        recyclerOutside = view.findViewById(R.id.recycler_outside);
        outsideAdapter = new IssueOutsideAdapter(getContext());

        dialog = new ProgressDialog(getContext());
        dialog.setTitle(getString(R.string.download_data));
        dialog.setMessage(getString(R.string.please_wait) + " ...");
        dialog.show();

        refOutside = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("Outside");
        ref = FirebaseDatabase.getInstance().getReference("StatusRequest");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerOutside.setLayoutManager(layoutManager);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        v = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    valueEventLis = refOutside.child(Objects.requireNonNull(snapshot.getKey())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<IssueOutsideClass> outsideList = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                outsideList.add(snapshot.getValue(IssueOutsideClass.class));
                            }
                            outsideAdapter.setOutsideList(outsideList);
                            recyclerOutside.setAdapter(outsideAdapter);

                            dialog.dismiss();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
    public void onPause() {
        super.onPause();

        if (valueEventLis != null)
            refOutside.removeEventListener(valueEventLis);
        dialog.dismiss();

    }
}

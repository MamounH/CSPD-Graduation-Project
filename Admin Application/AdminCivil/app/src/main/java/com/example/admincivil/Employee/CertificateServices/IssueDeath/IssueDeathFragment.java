package com.example.admincivil.Employee.CertificateServices.IssueDeath;

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

import com.example.admincivil.Employee.CertificateServices.IssueBirth.IssueBirthAdapter;

import com.example.admincivil.Employee.CertificateServices.IssueBirth.IssueBirthClass;
import com.example.admincivil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class IssueDeathFragment extends Fragment {

    private RecyclerView recyclerDeath;
    private IssueDeathAdapter deathAdapter;
    private ValueEventListener valueEventLis, v;
    private DatabaseReference refDeath, ref;
    private ProgressDialog dialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_issue_death, container, false);


        recyclerDeath = view.findViewById(R.id.recycler_death);

        dialog = new ProgressDialog(getContext());

        deathAdapter = new IssueDeathAdapter(getContext());

        dialog.setTitle(getString(R.string.download_data));
        dialog.setMessage(getString(R.string.please_wait) + " ...");
        dialog.show();

        refDeath = FirebaseDatabase.getInstance().getReference("Services").child("Certificate").child("IssueDeath");
        ref = FirebaseDatabase.getInstance().getReference("StatusRequest");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerDeath.setLayoutManager(layoutManager);

        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();
        v = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    valueEventLis = refDeath.child(Objects.requireNonNull(snapshot.getKey())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<IssueDeathClass> deathList = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                deathList.add(snapshot.getValue(IssueDeathClass.class));
                            }
                            deathAdapter.setDeathList(deathList);
                            recyclerDeath.setAdapter(deathAdapter);

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
            refDeath.removeEventListener(valueEventLis);
        dialog.dismiss();

    }
}

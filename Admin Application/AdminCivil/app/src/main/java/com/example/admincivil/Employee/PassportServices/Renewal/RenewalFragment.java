package com.example.admincivil.Employee.PassportServices.Renewal;

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

import com.example.admincivil.Employee.PassportServices.AddEnum.AddEnumClass;
import com.example.admincivil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class RenewalFragment extends Fragment {

    private RecyclerView recyclerRenewal;
    private RenewalAdapter renewalAdapter;
    private ValueEventListener valueEventLis, v;
    private DatabaseReference refRenewal, ref;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_renewal, container, false);

        recyclerRenewal = view.findViewById(R.id.recycler_renewal);
        renewalAdapter = new RenewalAdapter(getContext());


        dialog = new ProgressDialog(getContext());
        dialog.setTitle(getString(R.string.download_data));
        dialog.setMessage(getString(R.string.please_wait) + " ...");
        dialog.show();

        refRenewal   = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("Renewal");
        ref = FirebaseDatabase.getInstance().getReference("StatusRequest");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerRenewal.setLayoutManager(layoutManager);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        v = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    valueEventLis = refRenewal.child(Objects.requireNonNull(snapshot.getKey())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<RenewalClass> renewalList = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                renewalList.add(snapshot.getValue(RenewalClass.class));
                            }
                            renewalAdapter.setRenewalList(renewalList);
                            recyclerRenewal.setAdapter(renewalAdapter);

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
            refRenewal.removeEventListener(valueEventLis);
        dialog.dismiss();

    }
}

package com.example.civilaffairs.ui.DepartmentServices.Provider.CertificateProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.civilaffairs.R;
import com.example.civilaffairs.ui.DepartmentServices.Provider.AdapterFamilyProvider;
import com.example.civilaffairs.ui.DepartmentServices.Provider.ProviderClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class CertificateProviderFragment extends Fragment {

    private RecyclerView recycler_certificate;
    private AdapterFamilyProvider certficateProvidreAdapter ;
    private DatabaseReference refCertificate;
    private ValueEventListener valueEventLis ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_certficate_providre, container, false);

        recycler_certificate = view.findViewById(R.id.recycler_certificate);
        refCertificate = FirebaseDatabase.getInstance().getReference("StatusRequest");


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_certificate.setLayoutManager(layoutManager);
        certficateProvidreAdapter = new AdapterFamilyProvider(getContext());


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE);
        String nationalID = prefs.getString("national_number", null);

        valueEventLis = refCertificate.child(nationalID).child("Certificate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<ProviderClass> providerList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    providerList.add(snapshot.getValue(ProviderClass.class));
                    certficateProvidreAdapter.setMissingList(providerList);
                    recycler_certificate.setAdapter(certficateProvidreAdapter);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), getString(R.string.check_your_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
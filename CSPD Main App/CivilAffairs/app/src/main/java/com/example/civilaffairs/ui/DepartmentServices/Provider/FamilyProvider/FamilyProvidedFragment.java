package com.example.civilaffairs.ui.DepartmentServices.Provider.FamilyProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class FamilyProvidedFragment extends Fragment {

    private RecyclerView recyclerProvider;
    private AdapterFamilyProvider adapterFamilyProvider;
    private DatabaseReference refProvider;
    private ValueEventListener valueEventLis ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_provided, container, false);

        refProvider = FirebaseDatabase.getInstance().getReference("StatusRequest");

        recyclerProvider = view.findViewById(R.id.recycler_provider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerProvider.setLayoutManager(layoutManager);
        adapterFamilyProvider = new AdapterFamilyProvider(getContext());



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.national_id), MODE_PRIVATE);
        String nationalID = prefs.getString("national_number", null);

         valueEventLis = refProvider.child(nationalID).child("Family").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<ProviderClass> providerList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    providerList.add(snapshot.getValue(ProviderClass.class));
                    adapterFamilyProvider.setMissingList(providerList);
                    recyclerProvider.setAdapter(adapterFamilyProvider);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

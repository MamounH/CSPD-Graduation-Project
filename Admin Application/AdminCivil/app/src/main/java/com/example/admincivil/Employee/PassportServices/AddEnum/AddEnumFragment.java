package com.example.admincivil.Employee.PassportServices.AddEnum;

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

import com.example.admincivil.Employee.CertificateServices.IssueOutside.IssueOutsideAdapter;
import com.example.admincivil.Employee.CertificateServices.IssueOutside.IssueOutsideClass;
import com.example.admincivil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEnumFragment extends Fragment {

    private RecyclerView recyclerAddEnum;
    private AddEnumAdapter addEnumAdapter;
    private ValueEventListener valueEventLis, v;
    private DatabaseReference refAddEnum, ref;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_enum, container, false);

        recyclerAddEnum = view.findViewById(R.id.recycler_addenum);
        addEnumAdapter = new AddEnumAdapter(getContext());

        dialog = new ProgressDialog(getContext());
        dialog.setTitle(getString(R.string.download_data));
        dialog.setMessage(getString(R.string.please_wait) + " ...");
        dialog.show();

        refAddEnum   = FirebaseDatabase.getInstance().getReference("Services").child("Passport").child("AddEnum");
        ref = FirebaseDatabase.getInstance().getReference("StatusRequest");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerAddEnum.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        v = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    valueEventLis = refAddEnum.child(Objects.requireNonNull(snapshot.getKey())).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            List<AddEnumClass> addEnumList = new ArrayList<>();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                addEnumList.add(snapshot.getValue(AddEnumClass.class));
                            }
                            addEnumAdapter.setAddEnumList(addEnumList);
                            recyclerAddEnum.setAdapter(addEnumAdapter);

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
            refAddEnum.removeEventListener(valueEventLis);
        dialog.dismiss();

    }
}

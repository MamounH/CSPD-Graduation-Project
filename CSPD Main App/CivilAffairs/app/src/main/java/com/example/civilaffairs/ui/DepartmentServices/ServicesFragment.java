package com.example.civilaffairs.ui.DepartmentServices;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.civilaffairs.R;
import com.example.civilaffairs.ui.DepartmentServices.BirthServices.BirthActivity;
import com.example.civilaffairs.ui.DepartmentServices.FamilyServices.FamilyActivity;
import com.example.civilaffairs.ui.DepartmentServices.PassportServices.PassportActivity;

public class ServicesFragment extends Fragment  {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_services, container, false);

        view.findViewById(R.id.card_family).setOnClickListener(v->
                getContext().startActivity(new Intent(getContext(), FamilyActivity.class)));

        view.findViewById(R.id.card_passport).setOnClickListener(v->
                getContext().startActivity(new Intent(getContext(), PassportActivity.class)));

        view.findViewById(R.id.cardBirthCertificate).setOnClickListener(v->
                getContext().startActivity(new Intent(new Intent(getContext(), BirthActivity.class))));

        return view;
    }
}

package com.example.civilaffairs.ui.DepartmentServices;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.civilaffairs.R;
import com.google.android.material.tabs.TabLayout;

public class DepartmentServicesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_department_services, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabs);
        ViewPager viewPager = view.findViewById(R.id.view_pager);

        TabPagerAdapter adapter = new TabPagerAdapter(getActivity().getSupportFragmentManager(), getContext().getString(R.string.services),
                getContext().getString(R.string.services_provided));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

package com.example.civilaffairs.ui.DepartmentServices.Provider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.civilaffairs.R;
import com.google.android.material.tabs.TabLayout;

public class ProviderFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_provider, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_provider);
        ViewPager viewPager = view.findViewById(R.id.pager_provider);

        TabProviderPagerAdapter adapter = new TabProviderPagerAdapter(getActivity().getSupportFragmentManager(), getContext().getString(R.string.family_book),
                getContext().getString(R.string.passport),getContext().getString(R.string.certificate_services));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;

    }
}

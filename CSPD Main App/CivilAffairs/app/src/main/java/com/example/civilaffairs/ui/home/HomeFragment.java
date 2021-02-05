package com.example.civilaffairs.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.TransitionInflater;

import com.example.civilaffairs.R;
import com.example.civilaffairs.ReplaceFragment;
import com.example.civilaffairs.ui.DepartmentServices.DepartmentServicesFragment;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private ReplaceFragment replaceFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = getActivity();
        if (activity instanceof ReplaceFragment)
            replaceFragment = (ReplaceFragment) activity;

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.lay_services).setOnClickListener(v -> {
            if (replaceFragment != null)
                replaceFragment.showFragment(ReplaceFragment.FragmentSelect.fragment_services);

        });
        view.findViewById(R.id.lay_guide).setOnClickListener(v -> {
            if (replaceFragment != null)
                replaceFragment.showFragment(ReplaceFragment.FragmentSelect.fragment_guide);
        });

        view.findViewById(R.id.lay_time).setOnClickListener(v -> {
            if (replaceFragment != null)
                replaceFragment.showFragment(ReplaceFragment.FragmentSelect.fragment_time);
        });

        view.findViewById(R.id.lay_setting).setOnClickListener(v -> {
            if (replaceFragment != null)
                replaceFragment.showFragment(ReplaceFragment.FragmentSelect.fragment_setting);
        });

        view.findViewById(R.id.bt_img_call).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "027273827"))));

        view.findViewById(R.id.bt_img_facebook).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://web.facebook.com/CSPD.JO/"))));

        view.findViewById(R.id.bt_img_twitter).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/cspdgov_jo?lang=ar"))));

        return view;
    }


}

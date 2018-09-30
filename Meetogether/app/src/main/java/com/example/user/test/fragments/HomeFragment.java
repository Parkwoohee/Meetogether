package com.example.user.test.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.test.AddTmapActivity;
import com.example.user.test.MainActivity;
import com.example.user.test.MenuFrag1;
import com.example.user.test.MenuFrag2;
import com.example.user.test.MenuFrag3;
import com.example.user.test.R;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private int[] tabIcons = {R.drawable.menu1,R.drawable.menu2,R.drawable.menu3};
    FloatingActionButton fab;
    TabLayout mTab;
    ViewPager vp;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fab = view.findViewById(R.id.fab);
        mTab = view.findViewById(R.id.tabs);
        vp = view.findViewById(R.id.viewpager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTmapActivity.class);
                startActivity(intent);
            }
        });

        vp.setAdapter(new pagerAdapter(getFragmentManager()));
        vp.setCurrentItem(0);
        mTab.setupWithViewPager(vp);
        setupTabIcons();

        return view;
    }

    private void setupTabIcons() {
        mTab.getTabAt(0).setIcon(tabIcons[0]);
        mTab.getTabAt(1).setIcon(tabIcons[1]);
        mTab.getTabAt(2).setIcon(tabIcons[2]);
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {
        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MenuFrag1();
                case 1:
                    return new MenuFrag2();
                case 2:
                    return new MenuFrag3();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ROOM";
                case 1:
                    return "BestRoot";
                case 2:
                    return "MyRoom";
                default:
                    return null;
            }
        }
    }
}

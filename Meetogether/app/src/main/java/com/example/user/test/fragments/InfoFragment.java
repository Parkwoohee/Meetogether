package com.example.user.test.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.user.test.MainActivity.userName;
import static com.example.user.test.MainActivity.member;

import com.example.user.test.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoFragment extends Fragment {

    FirebaseDatabase database;
    LinearLayout changepwfrag;
    LinearLayout delelt;

    TextView myId;
    TextView myEmail;
    TextView myName;
    TextView mainName;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        changepwfrag = view.findViewById(R.id.changepwfrag);

        myId = view.findViewById(R.id.myrfid);
        myEmail = view.findViewById(R.id.myemail);
        myName = view.findViewById(R.id.myname);
        mainName = view.findViewById(R.id.main_name);

        myId.setText(member);
        myEmail.setText(member);
        myName.setText(userName);
        mainName.setText(userName);


        changepwfrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeInfoFragment fragment = new ChangeInfoFragment();
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.NavFrag, fragment).commit();
            }
        });
        return view;
    }
}
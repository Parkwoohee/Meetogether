package com.example.user.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuFrag1 extends Fragment {
    String TAG = getClass().getSimpleName();
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;

    List<ListData> mRoomList;

    FirebaseDatabase database;
    ListViewAdapter mAdapter;

    public MenuFrag1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_frag1, container, false);

        mRecyclerView = v.findViewById(R.id.room_list_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRoomList = new ArrayList<>();
        // specify an adapter (see also next example)


            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mRecyclerView.removeAllViews();
                    mAdapter = new ListViewAdapter(mRoomList, getActivity());
                    mRecyclerView.setAdapter(mAdapter);

                    for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                        String value2 = dataSnapshot2.getValue().toString();
                        Log.d(TAG, "Value is: " + value2);
                        //ListData data = dataSnapshot2.getValue(ListData.class);
                        ListData data1 = dataSnapshot2.getValue(ListData.class);

                        mRoomList.add(data1);
                        mAdapter.notifyItemInserted(mRoomList.size() - 1);
                    }

                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        return v;
    }
}
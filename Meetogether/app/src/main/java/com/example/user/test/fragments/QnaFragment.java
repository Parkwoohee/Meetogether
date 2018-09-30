package com.example.user.test.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.user.test.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QnaFragment extends Fragment {

    ArrayList<String> mList;
    ListView mListView;
    ListAdapter mAdapter;

    public QnaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qna, container, false);

        mList = new ArrayList<>();
        mListView = view.findViewById(R.id.qna_list);
        mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(mAdapter);
        mList.add("경로 지정은 어떻게 하나요?");
        mList.add("방 생성이 하나밖에 안돼요.");
        mList.add("이 앱의 목적이 뭔가요?");
        mList.add("마음에 드는 경로와 가이드를 찾아서 채팅하고 싶어요.");

        return view;
    }
}
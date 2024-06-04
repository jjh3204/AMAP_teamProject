package com.example.amap_teamproject.SearchPage;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import java.util.List;

public class SR_ActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private List<Activity> activityList;

    public SR_ActivityFragment() {
        // Required empty public constructor
    }

    public SR_ActivityFragment(List<Activity> activityList, ActivityAdapter activityAdapter) {
        this.activityList = activityList;
        this.adapter = activityAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (adapter == null) {
            adapter = new ActivityAdapter(activityList);
        }
        recyclerView.setAdapter(adapter);

        return view;
    }
}
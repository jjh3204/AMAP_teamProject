package com.example.amap_teamproject.menu;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.amap_teamproject.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private List<Activity> activityList;
    private FirebaseFirestore db;

    public ActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_activity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        activityList = new ArrayList<>();
        adapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadActivities();

        return view;
    }

    private void loadActivities() {
        db.collection("activities")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    activityList.clear();
                    activityList.addAll(queryDocumentSnapshots.toObjects(Activity.class));
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }
}

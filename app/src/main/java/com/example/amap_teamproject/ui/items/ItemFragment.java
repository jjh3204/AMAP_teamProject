package com.example.amap_teamproject.ui.items;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment {

    private List<Event> eventList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private ActivityAdapter activityAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventAdapter = new EventAdapter(eventList);
        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(eventAdapter); // 기본적으로 이벤트 어댑터로 설정

        Button allButton = view.findViewById(R.id.allButton);
        Button contestButton = view.findViewById(R.id.contestButton);
        Button activityButton = view.findViewById(R.id.activityButton);

        allButton.setOnClickListener(v -> fetchAll());
        contestButton.setOnClickListener(v -> fetchEvents());
        activityButton.setOnClickListener(v -> fetchActivities());

        fetchAll();
        return view;
    }

    private void fetchAll() {
        fetchEvents();
        fetchActivities();
    }

    private void fetchEvents() {
        db.collection("contests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventList.add(event);
                        }
                        recyclerView.setAdapter(eventAdapter);
                        eventAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void fetchActivities() {
        db.collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Activity activity = document.toObject(Activity.class);
                            activityList.add(activity);
                        }
                        recyclerView.setAdapter(activityAdapter);
                        activityAdapter.notifyDataSetChanged();
                    }
                });
    }
}




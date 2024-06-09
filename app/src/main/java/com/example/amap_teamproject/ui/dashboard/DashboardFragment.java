package com.example.amap_teamproject.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private List<Event> eventList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private ActivityAdapter activityAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private Button contestButton, activityButton;

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_item_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position == state.getItemCount() - 1) {
                    outRect.bottom = getResources().getDimensionPixelSize(R.dimen.bottom_padding);
                }
            }
        });

        eventAdapter = new EventAdapter(eventList);
        activityAdapter = new ActivityAdapter(activityList);

        contestButton = view.findViewById(R.id.contestButton);
        activityButton = view.findViewById(R.id.activityButton);

        contestButton.setOnClickListener(v -> {
            contestButton.setSelected(true);
            activityButton.setSelected(false);
            recyclerView.setAdapter(eventAdapter);
            fetchEvents();
        });

        activityButton.setOnClickListener(v -> {
            contestButton.setSelected(false);
            activityButton.setSelected(true);
            recyclerView.setAdapter(activityAdapter);
            fetchActivities();
        });

        // 초기 화면을 공모전 리스트로 설정
        contestButton.setSelected(true);
        activityButton.setSelected(false);
        recyclerView.setAdapter(eventAdapter);
        fetchEvents(); // 초기 데이터를 가져오기 위해 호출

        return view;
    }

    private void fetchEvents() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        db.collection("users").document(userId).collection("favorites_event")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            if (title != null) {
                                fetchFavoriteEventByTitle(title);
                            }
                        }
                    } else {
                        // 오류 처리
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchFavoriteEventByTitle(String title) {
        db.collection("contests")
                .whereEqualTo("title", title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventList.add(event);
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchActivities() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        db.collection("users").document(userId).collection("favorites_activity")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            if (title != null) {
                                fetchActivityByTitle(title);
                            }
                        }
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchActivityByTitle(String title) {
        db.collection("activities")
                .whereEqualTo("title", title)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Activity activity = document.toObject(Activity.class);
                            activityList.add(activity);
                        }
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        // 오류 처리
                    }
                });
    }
}
package com.example.amap_teamproject.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardFragment extends Fragment {

    private List<Event> eventList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private ActivityAdapter activityAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private Button contestButton, activityButton;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        emptyView = view.findViewById(R.id.empty_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
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

        // SwipeRefreshLayout 설정
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (contestButton.isSelected()) {
                fetchEvents();
            } else {
                fetchActivities();
            }
        });

        return view;
    }

    private void fetchEvents() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return; // Exit if no user is logged in
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("favorites_event")
                .orderBy("time", Query.Direction.ASCENDING) // Order events by time in ascending order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> titles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            Timestamp timestamp = document.getTimestamp("time"); // Ensure the time field is a Firestore timestamp
                            if (title != null && timestamp != null) {
                                titles.add(title);
                            }
                        }
                        fetchFavoriteEventsByTitles(titles); // Fetch details for all events by titles
                    } else {
                        // Handle error
                    }
                    swipeRefreshLayout.setRefreshing(false); // 새로고침 중지
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchFavoriteEventsByTitles(List<String> titles) {
        if (titles.isEmpty()) {
            eventList.clear();
            eventAdapter.notifyDataSetChanged();
            toggleEmptyViewVisibility(eventList.isEmpty());
            swipeRefreshLayout.setRefreshing(false); // 새로고침 중지
            return;
        }

        db.collection("contests")
                .whereIn("title", titles)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear(); // Clear the existing list of events
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            eventList.add(event);
                        }
                        // Sort the eventList by title to maintain the order of titles
                        eventList.sort(Comparator.comparing(Event::getTitle, Comparator.comparingInt(titles::indexOf)));
                        eventAdapter.notifyDataSetChanged();
                        toggleEmptyViewVisibility(eventList.isEmpty());
                    } else {
                        // Handle error
                    }
                    swipeRefreshLayout.setRefreshing(false); // 새로고침 중지
                });
    }

    private void fetchActivities() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return; // Exit if no user is logged in
        }

        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).collection("favorites_activity")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> titles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            if (title != null) {
                                titles.add(title);
                            }
                        }
                        fetchActivitiesByTitles(titles); // Fetch details for all activities by titles
                    } else {
                        // Handle error
                    }
                    swipeRefreshLayout.setRefreshing(false); // 새로고침 중지
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchActivitiesByTitles(List<String> titles) {
        if (titles.isEmpty()) {
            activityList.clear();
            activityAdapter.notifyDataSetChanged();
            toggleEmptyViewVisibility(activityList.isEmpty());
            swipeRefreshLayout.setRefreshing(false); // 새로고침 중지
            return;
        }

        db.collection("activities")
                .whereIn("title", titles)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear(); // Clear the existing list of activities
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Activity activity = document.toObject(Activity.class);
                            activityList.add(activity);
                        }
                        // Sort the activityList by title to maintain the order of titles
                        activityList.sort(Comparator.comparing(Activity::getTitle, Comparator.comparingInt(titles::indexOf)));
                        activityAdapter.notifyDataSetChanged();
                        toggleEmptyViewVisibility(activityList.isEmpty());
                    } else {
                        // Handle error
                    }
                    swipeRefreshLayout.setRefreshing(false); // 새로고침 중지
                });
    }

    private void toggleEmptyViewVisibility(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
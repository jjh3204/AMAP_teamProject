package com.example.amap_teamproject.ui.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.FragmentDashboardBinding;
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
    private Button contestButton, activityButton, allButton, categoryButton1, categoryButton2, categoryButton3, categoryButton4;

    public DashboardFragment() {
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

        allButton = view.findViewById(R.id.allButton);
        categoryButton1 = view.findViewById(R.id.categoryButton1);
        categoryButton2 = view.findViewById(R.id.categoryButton2);
        categoryButton3 = view.findViewById(R.id.categoryButton3);
        categoryButton4 = view.findViewById(R.id.categoryButton4);

        View.OnClickListener filterClickListener = v -> {
            resetFilterButtonColors();
            v.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue_dark));
            if (v == allButton) {
                if (contestButton.isSelected()) {
                    fetchEvents();
                } else {
                    fetchActivities();
                }
            } else {
                String category = ((Button) v).getText().toString();
                if (contestButton.isSelected()) {
                    fetchEventsByCategory(category);
                } else {
                    fetchActivitiesByCategory(category);
                }
            }
        };

        contestButton.setOnClickListener(v -> {
            contestButton.setSelected(true);
            activityButton.setSelected(false);
            recyclerView.setAdapter(eventAdapter);
            allButton.performClick();
            updateFilterButtons("contest");
        });

        activityButton.setOnClickListener(v -> {
            contestButton.setSelected(false);
            activityButton.setSelected(true);
            recyclerView.setAdapter(activityAdapter);
            allButton.performClick();
            updateFilterButtons("activity");
        });

        allButton.setOnClickListener(filterClickListener);
        categoryButton1.setOnClickListener(filterClickListener);
        categoryButton2.setOnClickListener(filterClickListener);
        categoryButton3.setOnClickListener(filterClickListener);
        categoryButton4.setOnClickListener(filterClickListener);

        // 초기 화면을 공모전 리스트로 설정
        contestButton.setSelected(true);
        recyclerView.setAdapter(eventAdapter);
        updateFilterButtons("contest");

        allButton.performClick();

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

    private void fetchEventsByCategory(String category) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        db.collection("users").document(userId).collection("favorites_event")
                .whereArrayContains("contest_field", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        List<String> titles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            if (title != null) {
                                titles.add(title);
                            }
                        }
                        if (!titles.isEmpty()) {
                            fetchFavoriteEventsByTitlesAndCategory(titles, category);
                        } else {
                            eventAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchFavoriteEventsByTitlesAndCategory(List<String> titles, String category) {
        db.collection("contests")
                .whereIn("title", titles)
                .whereArrayContains("contest_field", category)
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

    private void fetchActivitiesByCategory(String category) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        db.collection("users").document(userId).collection("favorites_activity")
                .whereArrayContains("interest_field", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
                        List<String> titles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            if (title != null) {
                                titles.add(title);
                            }
                        }
                        if (!titles.isEmpty()) {
                            fetchActivitiesByTitlesAndCategory(titles, category);
                        } else {
                            activityAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchActivitiesByTitlesAndCategory(List<String> titles, String category) {
        db.collection("activities")
                .whereIn("title", titles)
                .whereArrayContains("interest_field", category)
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

    private void updateFilterButtons(String type) {
        if (type.equals("contest")) {
            allButton.setText("전체");
            categoryButton1.setText("사진/영상/UCC");
            categoryButton2.setText("디자인/순수미술/공예");
            categoryButton3.setText("분야 3");
            categoryButton4.setText("분야 4");
        } else {
            allButton.setText("전체");
            categoryButton1.setText("경영/경제");
            categoryButton2.setText("사회과학");
            categoryButton3.setText("자연과학");
            categoryButton4.setText("공학");
        }
    }

    private void resetFilterButtonColors() {
        allButton.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton1.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton2.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton3.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton4.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
    }
}
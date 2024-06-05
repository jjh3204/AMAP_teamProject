package com.example.amap_teamproject.SearchPage;

import android.graphics.Rect;
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
import android.widget.LinearLayout;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SR_Fragment extends Fragment {

    private List<Event> eventList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private ActivityAdapter activityAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private Button contestButton, activityButton, allButton, categoryButton1, categoryButton2, categoryButton3, categoryButton4;
    private LinearLayout filterButtonsContainer;
    private String searchText;

    public SR_Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        // 전달받은 검색 텍스트를 가져옵니다.
        if (getArguments() != null) {
            searchText = getArguments().getString("searchText");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add padding to the bottom of the RecyclerView
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
            String category = ((Button) v).getText().toString();
            if (v == allButton) {
                if (contestButton.isSelected()) {
                    fetchEvents(searchText);
                } else {
                    fetchActivities(searchText);
                }
            } else {
                if (contestButton.isSelected()) {
                    fetchEventsByCategory(category, searchText);
                } else {
                    fetchActivitiesByCategory(category, searchText);
                }
            }
        };

        contestButton.setOnClickListener(v -> {
            contestButton.setSelected(true);
            activityButton.setSelected(false);
            recyclerView.setAdapter(eventAdapter);
            fetchEvents(searchText);
            updateFilterButtons("contest");
            // allButton 클릭 상태로 설정
            allButton.performClick();
        });

        activityButton.setOnClickListener(v -> {
            contestButton.setSelected(false);
            activityButton.setSelected(true);
            recyclerView.setAdapter(activityAdapter);
            fetchActivities(searchText);
            updateFilterButtons("activity");
            // allButton 클릭 상태로 설정
            allButton.performClick();
        });

        allButton.setOnClickListener(filterClickListener);
        categoryButton1.setOnClickListener(filterClickListener);
        categoryButton2.setOnClickListener(filterClickListener);
        categoryButton3.setOnClickListener(filterClickListener);
        categoryButton4.setOnClickListener(filterClickListener);

        // 초기 화면을 공모전 리스트로 설정
        contestButton.setSelected(true);
        recyclerView.setAdapter(eventAdapter);
        fetchEvents(searchText);
        updateFilterButtons("contest");

        // 초기 필터링 버튼 상태 설정
        allButton.performClick();

        return view;
    }

    private void fetchEvents(String searchText) {
        if (searchText == null) {
            searchText = "";
        }
        final String finalSearchText = searchText;

        db.collection("contests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            if (event.getTitle() != null && event.getTitle().toLowerCase().contains(finalSearchText.toLowerCase())) {
                                eventList.add(event);
                            }
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchEventsByCategory(String category, String searchText) {
        if (searchText == null) {
            searchText = "";
        }
        final String finalSearchText = searchText.toLowerCase();

        db.collection("contests")
                .whereArrayContains("contest_field", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            String title = event.getTitle();
                            if (title != null && title.toLowerCase().contains(finalSearchText)) {
                                eventList.add(event);
                            }
                        }
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchActivities(String searchText) {
        if (searchText == null) {
            searchText = "";
        }
        final String finalSearchText = searchText.toLowerCase();

        db.collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Activity activity = document.toObject(Activity.class);
                            String title = activity.getTitle();
                            if (title != null && title.toLowerCase().contains(finalSearchText)) {
                                activityList.add(activity);
                            }
                        }
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchActivitiesByCategory(String category, String searchText) {
        if (searchText == null) {
            searchText = "";
        }
        final String finalSearchText = searchText.toLowerCase();

        db.collection("activities")
                .whereArrayContains("interest_field", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Activity activity = document.toObject(Activity.class);
                            String title = activity.getTitle();
                            if (title != null && title.toLowerCase().contains(finalSearchText)) {
                                activityList.add(activity);
                            }
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
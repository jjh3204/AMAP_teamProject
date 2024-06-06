package com.example.amap_teamproject.ui.items;

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

public class ItemFragment extends Fragment {

    private List<Event> eventList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private ActivityAdapter activityAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private Button contestButton, activityButton, allButton, categoryButton1, categoryButton2, categoryButton3, categoryButton4, categoryButton5, categoryButton6, categoryButton7, categoryButton8, categoryButton9, categoryButton10;
    private LinearLayout filterButtonsContainer;

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
        categoryButton5 = view.findViewById(R.id.categoryButton5);
        categoryButton6 = view.findViewById(R.id.categoryButton6);
        categoryButton7 = view.findViewById(R.id.categoryButton7);
        categoryButton8 = view.findViewById(R.id.categoryButton8);
        categoryButton9 = view.findViewById(R.id.categoryButton9);
        categoryButton10 = view.findViewById(R.id.categoryButton10);

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
            fetchEvents();
            updateFilterButtons("contest");
        });

        activityButton.setOnClickListener(v -> {
            contestButton.setSelected(false);
            activityButton.setSelected(true);
            recyclerView.setAdapter(activityAdapter);
            fetchActivities();
            updateFilterButtons("activity");
        });

        allButton.setOnClickListener(filterClickListener);
        categoryButton1.setOnClickListener(filterClickListener);
        categoryButton2.setOnClickListener(filterClickListener);
        categoryButton3.setOnClickListener(filterClickListener);
        categoryButton4.setOnClickListener(filterClickListener);
        categoryButton5.setOnClickListener(filterClickListener);
        categoryButton6.setOnClickListener(filterClickListener);
        categoryButton7.setOnClickListener(filterClickListener);
        categoryButton8.setOnClickListener(filterClickListener);
        categoryButton9.setOnClickListener(filterClickListener);
        categoryButton10.setOnClickListener(filterClickListener);

        // 초기 화면을 공모전 리스트로 설정
        contestButton.setSelected(true);
        recyclerView.setAdapter(eventAdapter);
        fetchEvents();
        updateFilterButtons("contest");

        // 초기 필터링 버튼 상태 설정
        allButton.performClick();

        return view;
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
                        eventAdapter.notifyDataSetChanged();
                    } else {
                        // 오류 처리
                    }
                });
    }

    private void fetchEventsByCategory(String category) {
        db.collection("contests")
                .whereArrayContains("contest_field", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
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
        db.collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
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
        db.collection("activities")
                .whereArrayContains("interest_field", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
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
            categoryButton3.setText("기획/아이디어");
            categoryButton4.setText("과학/공학");
            categoryButton5.setText("학술");
            categoryButton6.setText("문학/시나리오");
            categoryButton7.setText("건축/건설/인테리어");
            categoryButton8.setText("창업");
            categoryButton9.setText("캐릭터/만화/게임");
            categoryButton10.setText("기타");
        } else {
            allButton.setText("전체");
            categoryButton1.setText("체육/헬스");
            categoryButton2.setText("경영/컨설팅/마케팅");
            categoryButton3.setText("과학/공학/기술/IT");
            categoryButton4.setText("환경/에너지");
            categoryButton5.setText("여행/호텔/항공");
            categoryButton6.setText("콘텐츠");
            categoryButton7.setText("사회공헌/교류");
            categoryButton8.setText("교육");
            categoryButton9.setText("행사/페스티벌");
            categoryButton10.setText("기타");
        }
    }

    private void resetFilterButtonColors() {
        allButton.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton1.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton2.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton3.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton4.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton5.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton6.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton7.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton8.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton9.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
        categoryButton10.setBackgroundColor(getResources().getColor(R.color.transparent_sky_blue));
    }
}

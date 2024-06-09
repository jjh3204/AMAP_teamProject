package com.example.amap_teamproject.SearchPage;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SR_Fragment extends Fragment {

    private List<Event> eventList = new ArrayList<>();
    private List<Activity> activityList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private ActivityAdapter activityAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private Button contestButton, activityButton, allButton, categoryButton1, categoryButton2, categoryButton3, categoryButton4, categoryButton5, categoryButton6, categoryButton7, categoryButton8, categoryButton9, categoryButton10;
    private Spinner sortSpinner;
    private String currentSortOption = "등록순"; // 현재 정렬 옵션을 저장하는 변수

    public SR_Fragment() {
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
        sortSpinner = view.findViewById(R.id.sort_spinner);

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
            allButton.performClick(); // 공모전 버튼 클릭 시 allButton 클릭
        });

        activityButton.setOnClickListener(v -> {
            contestButton.setSelected(false);
            activityButton.setSelected(true);
            recyclerView.setAdapter(activityAdapter);
            fetchActivities();
            updateFilterButtons("activity");
            allButton.performClick(); // 대외활동 버튼 클릭 시 allButton 클릭
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

        setupSortSpinner();

        return view;
    }

    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, R.layout.spinner_item); // 커스텀 레이아웃 사용
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        // 초기 정렬 옵션을 등록순으로 설정
        sortSpinner.setSelection(adapter.getPosition("등록순"));

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortOption = parent.getItemAtPosition(position).toString();
                if (contestButton.isSelected()) {
                    sortEvents(currentSortOption);
                } else {
                    sortActivities(currentSortOption);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때의 동작을 정의하지 않음
            }
        });
    }

    private void sortEvents(String sortOption) {
        switch (sortOption) {
            case "조회수 높은 순":
                eventList.sort((e1, e2) -> Integer.compare(e2.getHits(), e1.getHits()));
                break;
            case "조회수 낮은 순":
                eventList.sort((e1, e2) -> Integer.compare(e1.getHits(), e2.getHits()));
                break;
            case "좋아요 많은 순":
                eventList.sort((e1, e2) -> Integer.compare(e2.getLikes(), e1.getLikes()));
                break;
            case "좋아요 적은 순":
                eventList.sort((e1, e2) -> Integer.compare(e1.getLikes(), e2.getLikes()));
                break;
            case "최신순":
                eventList.sort((e1, e2) -> Long.compare(e2.getTimestamp().toDate().getTime(), e1.getTimestamp().toDate().getTime()));
                break;
            case "등록순":
                eventList.sort((e1, e2) -> Long.compare(e1.getTimestamp().toDate().getTime(), e2.getTimestamp().toDate().getTime()));
                break;
            case "마감순":
                eventList.sort((e1, e2) -> {
                    String endDateStr1 = e1.getSubPeriod().split(" ~ ")[1];
                    String endDateStr2 = e2.getSubPeriod().split(" ~ ")[1];

                    boolean isDate1 = isDate(endDateStr1);
                    boolean isDate2 = isDate(endDateStr2);

                    if (isDate1 && isDate2) {
                        Date endDate1 = parseDate(endDateStr1);
                        Date endDate2 = parseDate(endDateStr2);
                        return endDate1.compareTo(endDate2);
                    } else if (isDate1) {
                        return -1; // endDateStr1이 날짜이고 endDateStr2가 특수한 경우
                    } else if (isDate2) {
                        return 1; // endDateStr2가 날짜이고 endDateStr1이 특수한 경우
                    } else {
                        return 0; // 둘 다 특수한 경우
                    }
                });
                break;
        }
        moveEndedEventsToBottom(eventList);
        eventAdapter.notifyDataSetChanged();
    }

    private void sortActivities(String sortOption) {
        switch (sortOption) {
            case "조회수 높은 순":
                activityList.sort((a1, a2) -> Integer.compare(a2.getHits(), a1.getHits()));
                break;
            case "조회수 낮은 순":
                activityList.sort((a1, a2) -> Integer.compare(a1.getHits(), a2.getHits()));
                break;
            case "좋아요 많은 순":
                activityList.sort((a1, a2) -> Integer.compare(a2.getLikes(), a1.getLikes()));
                break;
            case "좋아요 적은 순":
                activityList.sort((a1, a2) -> Integer.compare(a1.getLikes(), a2.getLikes()));
                break;
            case "최신순":
                activityList.sort((a1, a2) -> Long.compare(a2.getTimestamp().toDate().getTime(), a1.getTimestamp().toDate().getTime()));
                break;
            case "등록순":
                activityList.sort((a1, a2) -> Long.compare(a1.getTimestamp().toDate().getTime(), a2.getTimestamp().toDate().getTime()));
                break;
            case "마감순":
                activityList.sort((a1, a2) -> {
                    String endDateStr1 = a1.getSubPeriod().split(" ~ ")[1];
                    String endDateStr2 = a2.getSubPeriod().split(" ~ ")[1];

                    boolean isDate1 = isDate(endDateStr1);
                    boolean isDate2 = isDate(endDateStr2);

                    if (isDate1 && isDate2) {
                        Date endDate1 = parseDate(endDateStr1);
                        Date endDate2 = parseDate(endDateStr2);
                        return endDate1.compareTo(endDate2);
                    } else if (isDate1) {
                        return -1; // endDateStr1이 날짜이고 endDateStr2가 특수한 경우
                    } else if (isDate2) {
                        return 1; // endDateStr2가 날짜이고 endDateStr1이 특수한 경우
                    } else {
                        return 0; // 둘 다 특수한 경우
                    }
                });
                break;
        }
        moveEndedEventsToBottom(activityList);
        activityAdapter.notifyDataSetChanged();
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

    private boolean isDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd", Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd", Locale.getDefault());
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private void fetchEvents() {
        db.collection("contests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            if (document.contains("timestamp")) {
                                event.setTimestamp(document.getTimestamp("timestamp"));
                            }
                            eventList.add(event);
                        }
                        // 데이터 로드 후 현재 정렬 옵션으로 정렬
                        sortEvents(currentSortOption);
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
                            if (document.contains("timestamp")) {
                                event.setTimestamp(document.getTimestamp("timestamp"));
                            }
                            eventList.add(event);
                        }
                        // 데이터 로드 후 현재 정렬 옵션으로 정렬
                        sortEvents(currentSortOption);
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
                            if (document.contains("timestamp")) {
                                activity.setTimestamp(document.getTimestamp("timestamp"));
                            }
                            activityList.add(activity);
                        }
                        // 데이터 로드 후 현재 정렬 옵션으로 정렬
                        sortActivities(currentSortOption);
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
                            if (document.contains("timestamp")) {
                                activity.setTimestamp(document.getTimestamp("timestamp"));
                            }
                            activityList.add(activity);
                        }
                        // 데이터 로드 후 현재 정렬 옵션으로 정렬
                        sortActivities(currentSortOption);
                    } else {
                        // 오류 처리
                    }
                });
    }

    private <T> void moveEndedEventsToBottom(List<T> list) {
        List<T> endedEvents = new ArrayList<>();
        List<T> specialCases = new ArrayList<>();

        list.removeIf(item -> {
            boolean isEnded = false;
            boolean isSpecialCase = false;

            if (item instanceof Activity) {
                Activity activity = (Activity) item;
                String endDateStr = activity.getSubPeriod().split(" ~ ")[1];
                if (isDate(endDateStr)) {
                    Date endDate = parseDate(endDateStr);
                    if (endDate.before(new Date())) {
                        isEnded = true;
                    }
                } else {
                    isSpecialCase = true;
                }
            } else if (item instanceof Event) {
                Event event = (Event) item;
                String endDateStr = event.getSubPeriod().split(" ~ ")[1];
                if (isDate(endDateStr)) {
                    Date endDate = parseDate(endDateStr);
                    if (endDate.before(new Date())) {
                        isEnded = true;
                    }
                } else {
                    isSpecialCase = true;
                }
            }

            if (isEnded) {
                endedEvents.add(item);
                return true;
            } else if (isSpecialCase) {
                specialCases.add(item);
                return true;
            }
            return false;
        });

        list.addAll(specialCases);
        list.addAll(endedEvents);
    }
}
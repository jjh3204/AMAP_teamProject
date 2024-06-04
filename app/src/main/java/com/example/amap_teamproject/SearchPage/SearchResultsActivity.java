package com.example.amap_teamproject.SearchPage;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityAdapter;
import com.example.amap_teamproject.menu.Event;
import com.example.amap_teamproject.menu.EventAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Event> eventList;
    private List<Activity> activityList;
    private EventAdapter eventAdapter;
    private ActivityAdapter activityAdapter;
    private Map<Integer, Runnable> menuActions;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        EditText searchEditText = findViewById(R.id.search_edit_text_again);
        Button searchButton = findViewById(R.id.search_button_again);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuery = searchEditText.getText().toString();
                performSearch(currentQuery);
            }
        });

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();
        activityList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList);
        activityAdapter = new ActivityAdapter(activityList);

        menuActions = new HashMap<>();
        menuActions.put(R.id.navigation_activity, () -> {
            searchActivities(currentQuery);
            replaceFragment(new SR_ActivityFragment(activityList, activityAdapter));
        });
        menuActions.put(R.id.navigation_event, () -> {
            searchEvents(currentQuery);
            replaceFragment(new SR_EventFragment(eventList, eventAdapter));
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Runnable action = menuActions.get(item.getItemId());
                        if (action != null) {
                            action.run();
                            return true;
                        }
                        return false;
                    }
                });

        // 디폴트 프래그먼트를 설정합니다.
        if (savedInstanceState == null) {
            performSearch("");
        }
    }

    private void performSearch(String query) {
        // 현재 선택된 메뉴 아이템에 따라 검색을 수행합니다.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuItem selectedItem = bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId());
        Runnable action = menuActions.get(selectedItem.getItemId());
        if (action != null) {
            action.run();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private void searchEvents(String query) {
        db.collection("contests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            eventList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                if (event.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                    eventList.add(event);
                                }
                            }
                            eventAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SearchResultsActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void searchActivities(String query) {
        db.collection("activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            activityList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Activity activity = document.toObject(Activity.class);
                                if (activity.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                    activityList.add(activity);
                                }
                            }
                            activityAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SearchResultsActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
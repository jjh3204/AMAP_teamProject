package com.example.amap_teamproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContestAdapter contestAdapter;
    private List<Contest> contestList;
    private EditText searchEditText;
    private Button searchButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contestList = new ArrayList<>();
        contestAdapter = new ContestAdapter(contestList);
        recyclerView.setAdapter(contestAdapter);

        searchEditText = findViewById(R.id.search_edit_text_again);
        searchButton = findViewById(R.id.search_button_again);

        String query = getIntent().getStringExtra("query");
        if (query != null && !query.isEmpty()) {
            searchContests(query);
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (event == null || !event.isShiftPressed()) {
                        performSearch();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString();
        Intent intent = new Intent(SearchResultsActivity.this, SearchResultsActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    private void searchContests(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            contestList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Contest contest = document.toObject(Contest.class);
                                if (contest.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                    contestList.add(contest);
                                }
                            }
                            contestAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SearchResultsActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
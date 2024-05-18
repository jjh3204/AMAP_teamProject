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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        String query = getIntent().getStringExtra("query");
        if (query != null && !query.isEmpty()) {
            searchContests(query);
        }

        searchEditText = findViewById(R.id.search_edit_text_again);
        searchButton = findViewById(R.id.search_button_again);

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
                        // Enter is pressed and it is not a shift + enter combination.
                        performSearch();
                        return true; // Consume the action.
                    }
                }
                return false; // Let other actions proceed.
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("contests");
        databaseReference.orderByChild("title").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contestList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contest contest = snapshot.getValue(Contest.class);
                    contestList.add(contest);
                }
                contestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SearchResultsActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
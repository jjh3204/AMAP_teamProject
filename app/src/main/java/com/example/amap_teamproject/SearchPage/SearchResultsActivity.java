package com.example.amap_teamproject.SearchPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.amap_teamproject.R;

public class SearchResultsActivity extends AppCompatActivity {
    private EditText editTextSearch;
    private ImageButton buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("검색");
        }

        editTextSearch = findViewById(R.id.search_edit_text_again);
        buttonSearch = findViewById(R.id.search_button_again);

        // HomeFragment로부터 전달된 검색 텍스트를 가져옵니다.
        Intent intent = getIntent();
        String initialSearchText = intent.getStringExtra("searchText");
        if (initialSearchText != null) {
            editTextSearch.setText(initialSearchText);
        }

        // Fragment에 검색 텍스트를 전달합니다.
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("searchText", initialSearchText);
            Fragment fragment = new SR_Fragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextSearch.getText().toString();
                searchInFragment(searchText);
            }
        });

        // Enter 키를 눌렀을 때 검색을 실행합니다.
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        String searchText = editTextSearch.getText().toString();
                        searchInFragment(searchText);
                        return true; // 이벤트를 소비합니다.
                    }
                }
                return false; // 다른 리스너에게 이벤트를 전달합니다.
            }
        });
    }

    private void searchInFragment(String searchText) {
        // Fragment에 검색 텍스트를 전달합니다.
        Bundle bundle = new Bundle();
        bundle.putString("searchText", searchText);
        Fragment fragment = new SR_Fragment();
        fragment.setArguments(bundle);

        // Fragment를 추가합니다.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
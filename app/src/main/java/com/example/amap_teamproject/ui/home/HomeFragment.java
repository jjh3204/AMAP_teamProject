package com.example.amap_teamproject.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.SearchPage.SearchResultsActivity;
import com.example.amap_teamproject.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private Handler handler;
    private Runnable runnable;
    private ViewPager2 viewPager;
    private ImagePagerAdapter adapter;
    private List<ImageItem> imageItems = new ArrayList<>();
    private final int delay = 7000;
    private boolean isAutoSlideActive = true;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText searchEditText = binding.searchId;
        ImageButton searchButton = binding.searchButtonId;
        viewPager = binding.viewPager;

        db = FirebaseFirestore.getInstance();
        handler = new Handler();

        // Search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                startSearchActivity(query);
            }
        });

        // Enter key press listener using setOnEditorActionListener
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        String query = searchEditText.getText().toString();
                        startSearchActivity(query);
                        return true;
                    }
                }
                return false;
            }
        });

        fetchLatestImages();

        return root;
    }

    private void startSearchActivity(String query) {
        Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
        intent.putExtra("searchText", query);
        startActivity(intent);
    }

    private void fetchLatestImages() {
        db.collection("activities")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(9)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        imageItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imgSrc = document.getString("poster_url");
                            String title = document.getString("title");
                            if (imgSrc != null && title != null) {
                                imageItems.add(new ImageItem(imgSrc, title));
                            }
                        }
                        // Update the ViewPager adapter
                        if (!imageItems.isEmpty()) {
                            adapter = new ImagePagerAdapter(getActivity(), imageItems);
                            viewPager.setAdapter(adapter);
                            startAutoSlide();
                        }
                    } else {
                        Log.e("HomeFragment", "Error fetching images", task.getException());
                        Toast.makeText(getContext(), "Failed to load images", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (adapter != null && adapter.getItemCount() > 0 && isAutoSlideActive) {
                    int currentItem = viewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % adapter.getItemCount();
                    viewPager.setCurrentItem(nextItem, true);
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void onResume() {
        super.onResume();
        isAutoSlideActive = true;
        handler.postDelayed(runnable, delay); // Resume auto-slide when returning to the fragment
    }

    @Override
    public void onPause() {
        super.onPause();
        isAutoSlideActive = false;
        handler.removeCallbacks(runnable); // Stop auto-slide when leaving the fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        binding = null;
    }
}

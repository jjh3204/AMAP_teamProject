package com.example.amap_teamproject.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.SearchPage.SearchResultsActivity;
import com.example.amap_teamproject.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private ImageView imageView;
    private List<String> imageUrls = new ArrayList<>();
    private int currentImageIndex = 0;
    private final int delay = 5000; // 5초 간격

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //  final TextView textView = binding.textHome;
        //  homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final EditText searchEditText = binding.searchId;
        Button searchButton = binding.searchButtonId;
        imageView = binding.imageView;

        db = FirebaseFirestore.getInstance();
        handler = new Handler();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
            }
        });
        fetchLatestImages();

        return root;
    }

    private void fetchLatestImages() {
        db.collection("activities")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        imageUrls.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imgSrc = document.getString("poster_url");
                            if (imgSrc != null) {
                                imageUrls.add(imgSrc);
                            }
                        }
                        // Start the image slideshow
                        if (!imageUrls.isEmpty()) {
                            startImageSlideshow();
                        }
                    } else {
                        // 실패 처리
                    }
                });
    }

    private void startImageSlideshow() {
        // Runnable 정의
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!imageUrls.isEmpty()) {
                    Glide.with(HomeFragment.this)
                            .load(imageUrls.get(currentImageIndex))
                            .into(imageView);
                    currentImageIndex = (currentImageIndex + 1) % imageUrls.size();
                    handler.postDelayed(this, delay);
                }
            }
        };

        // 첫 실행
        handler.post(runnable);
    }

    public void onResume() {
        super.onResume();
        ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText("홈");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.amap_teamproject.Notice.Notice;
import com.example.amap_teamproject.Notice.NoticeActivity;
import com.example.amap_teamproject.Notice.NoticeDetailActivity;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.SearchPage.SearchResultsActivity;
import com.example.amap_teamproject.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView notice1;
    private TextView notice2;
    private TextView notice3;
    private TextView notice4;

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private Handler handler;
    private Runnable runnable;
    private ViewPager2 viewPager;
    private ImagePagerAdapter adapter;
    private List<ImageItem> imageItems = new ArrayList<>();
    private final int delay = 7000;
    private boolean isAutoSlideActive = true;
    private LinearLayout indicatorLayout;
    private ListenerRegistration noticeListener;

    private EditText searchEditText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchEditText = binding.searchId;
        ImageButton searchButton = binding.searchButtonId;
        viewPager = binding.viewPager;
        indicatorLayout = binding.indicatorLayout;

        notice1 = binding.notice1;
        notice2 = binding.notice2;
        notice3 = binding.notice3;
        notice4 = binding.notice4;

        db = FirebaseFirestore.getInstance();
        handler = new Handler();

        loadLatestNotices();

        LinearLayout noticeLayout = binding.noticeLayout;
        noticeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NoticeActivity.class);
            startActivity(intent);
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                startSearchActivity(query);
            }
        });

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

        // ViewPager 페이지 변경 리스너 추가
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    viewPager.postDelayed(() -> viewPager.setCurrentItem(adapter.getItemCount() - 2, false), 300);
                } else if (position == adapter.getItemCount() - 1) {
                    viewPager.postDelayed(() -> viewPager.setCurrentItem(1, false), 300);
                }
                updateIndicator(position - 1);
            }
        });

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

                        if (!imageItems.isEmpty()) {
                            adapter = new ImagePagerAdapter(getActivity(), imageItems);
                            viewPager.setAdapter(adapter);
                            viewPager.setCurrentItem(1, false); // 처음에 실제 첫 번째 아이템을 표시하도록 설정
                            setupIndicators(imageItems.size());
                            startAutoSlide();
                        }
                    } else {
                        Log.e("HomeFragment", "Error fetching images", task.getException());
                        Toast.makeText(getContext(), "Failed to load images", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupIndicators(int count) {
        indicatorLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            final int index = i;
            View indicator = new View(getContext());
            indicator.setBackgroundResource(R.drawable.indicator_inactive); // 비활성화된 점의 배경
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    16, // 너비
                    16  // 높이
            );
            params.setMargins(8, 0, 8, 0); // 점 사이의 간격 조정
            indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(index + 1); // 해당 페이지로 이동
                }
            });
            indicatorLayout.addView(indicator, params);
        }
        updateIndicator(0); // 첫 번째 아이템을 선택된 상태로 초기화
    }

    private void updateIndicator(int position) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View indicator = indicatorLayout.getChildAt(i);
            if (i == position) {
                indicator.setBackgroundResource(R.drawable.indicator_active); // 활성화된 점의 배경
            } else {
                indicator.setBackgroundResource(R.drawable.indicator_inactive); // 비활성화된 점의 배경
            }
        }
    }

    private void startAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (adapter != null && adapter.getItemCount() > 0 && isAutoSlideActive) {
                    int currentItem = viewPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % adapter.getItemCount();
                    if (nextItem == adapter.getItemCount() - 1) {
                        viewPager.setCurrentItem(nextItem, true);
                        handler.postDelayed(() -> viewPager.setCurrentItem(1, false), delay);
                    } else {
                        viewPager.setCurrentItem(nextItem, true);
                        handler.postDelayed(this, delay);
                    }
                }
            }
        };
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delay);
    }

    private void loadLatestNotices() {
        db.collection("notices")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(4) // 최신 공지 4개만 불러오기
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        int count = 0;
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Notice notice = doc.toObject(Notice.class);
                            if (notice != null) {
                                if (count == 0) {
                                    notice1.setText(notice.getTitle());
                                    setNoticeClickListener(notice1, notice);
                                } else if (count == 1) {
                                    notice2.setText(notice.getTitle());
                                    setNoticeClickListener(notice2, notice);
                                } else if (count == 2) {
                                    notice3.setText(notice.getTitle());
                                    setNoticeClickListener(notice3, notice);
                                } else if (count == 3) {
                                    notice4.setText(notice.getTitle());
                                    setNoticeClickListener(notice4, notice);
                                }
                                count++;
                            }
                        }
                    }
                });
    }

    private void attachNoticeListener() {
        noticeListener = db.collection("notices")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(4)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (snapshots != null) {
                            int count = 0;
                            for (DocumentSnapshot doc : snapshots.getDocuments()) {
                                Notice notice = doc.toObject(Notice.class);
                                if (notice != null) {
                                    if (count == 0) {
                                        notice1.setText(notice.getTitle());
                                        setNoticeClickListener(notice1, notice);
                                    } else if (count == 1) {
                                        notice2.setText(notice.getTitle());
                                        setNoticeClickListener(notice2, notice);
                                    } else if (count == 2) {
                                        notice3.setText(notice.getTitle());
                                        setNoticeClickListener(notice3, notice);
                                    } else if (count == 3) {
                                        notice4.setText(notice.getTitle());
                                        setNoticeClickListener(notice4, notice);
                                    }
                                    count++;
                                }
                            }
                            if (count < 4) {
                                switch (count) {
                                    case 0:
                                        notice1.setText("");
                                    case 1:
                                        notice2.setText("");
                                    case 2:
                                        notice3.setText("");
                                    case 3:
                                        notice4.setText("");
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    private void setNoticeClickListener(TextView textView, Notice notice) {
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NoticeDetailActivity.class);
            intent.putExtra("title", notice.getTitle());
            intent.putExtra("content", notice.getContent());
            intent.putExtra("timestamp", notice.getTimestamp());
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isAutoSlideActive = true;
        handler.postDelayed(runnable, delay);
        attachNoticeListener();
        searchEditText.setText("");  // 홈 화면으로 돌아올 때 검색창의 텍스트를 지우기
    }

    @Override
    public void onPause() {
        super.onPause();
        isAutoSlideActive = false;
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        binding = null;
    }

}
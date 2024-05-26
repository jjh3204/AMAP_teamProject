package com.example.amap_teamproject.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amap_teamproject.SearchPage.Contest;
import com.example.amap_teamproject.SearchPage.ContestAdapter;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private RecyclerView recyclerView;
    private ContestAdapter contestAdapter;
    private List<Contest> contestList;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //  final TextView textView = binding.textDashboard;
        //  dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contestList = new ArrayList<>();
        contestAdapter = new ContestAdapter(contestList, getContext());
        recyclerView.setAdapter(contestAdapter);

        db = FirebaseFirestore.getInstance();
        loadFavorites();

        return root;
    }

    private void loadFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            // 사용자 인증이 되어 있지 않으면 처리
            // 예: 로그인 화면으로 이동 또는 오류 메시지 표시
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        contestList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Contest contest = document.toObject(Contest.class);
                            contestList.add(contest);
                        }
                        contestAdapter.notifyDataSetChanged();
                    } else {
                        // 오류 처리
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText("스크랩");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
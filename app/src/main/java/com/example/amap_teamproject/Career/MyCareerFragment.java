package com.example.amap_teamproject.Career;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.amap_teamproject.databinding.FragmentMycareerBinding;
import com.example.amap_teamproject.R;

import java.util.List;
import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

public class MyCareerFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private CareerItemAdapter careerItemAdapter;
    private PageViewModel pageViewModel;
    private FragmentMycareerBinding binding;
    private List<CareerItem> careerList = new ArrayList<CareerItem>();
    private ListenerRegistration registration;
    private FirebaseFirestore db;

    public static MyCareerFragment newInstance(int index) {
        MyCareerFragment fragment = new MyCareerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        // pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMycareerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.titleTextView.setLayoutManager(new LinearLayoutManager(getContext())); // titleTextView는 RecyclerView

        careerItemAdapter = new CareerItemAdapter(careerList, careerItem -> {
            Intent intent = new Intent(getContext(), AddCareerActivity.class);
            intent.putExtra("DOCUMENT_ID", careerItem.getDocumentId());
            startActivity(intent);
        });
        binding.titleTextView.setAdapter(careerItemAdapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        pageViewModel.setUserId(userId);
        pageViewModel.loadCareerItems(getArguments().getInt(ARG_SECTION_NUMBER));

        pageViewModel.getCareerListLiveData().observe(getViewLifecycleOwner(), new Observer<List<CareerItem>>() {
            @Override
            public void onChanged(List<CareerItem> careerItems) {
                careerItemAdapter.updateData(careerItems);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        int section = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
        pageViewModel.loadCareerItems(section);

        pageViewModel.getCareerListLiveData().observe(getViewLifecycleOwner(), new Observer<List<CareerItem>>() {
            @Override
            public void onChanged(List<CareerItem> careerItems) {
                careerItemAdapter.updateData(careerItems);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
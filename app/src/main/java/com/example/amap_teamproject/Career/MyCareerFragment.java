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
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMycareerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.titleTextView.setLayoutManager(new LinearLayoutManager(getContext())); // titleTextView는 RecyclerView

        careerItemAdapter = new CareerItemAdapter(careerList, careerItem -> {
            Intent intent = new Intent(getContext(), AddCareerActivity.class);
            intent.putExtra("DOCUMENT_ID", careerItem.getDocumentId());
            startActivity(intent);
        });
        binding.titleTextView.setAdapter(careerItemAdapter);
        db = FirebaseFirestore.getInstance();


        assert getArguments() != null;
        loadCareerItems(getArguments().getInt(ARG_SECTION_NUMBER));
        return root;
    }

    private void loadCareerItems(int section) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        registration = db.collection("users").document(userId).collection("career")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Error while loading!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            careerList.clear();
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                CareerItem careerItem = dc.getDocument().toObject(CareerItem.class);
                                careerItem.setDocumentId(dc.getDocument().getId());
                                if (section == 1 ||
                                        (section == 2 && "대외활동".equals(careerItem.getCategory())) ||
                                        (section == 3 && "공모전".equals(careerItem.getCategory()))) {
                                    switch (dc.getType()) {
                                        case ADDED:
                                            if (dc.getNewIndex() <= careerList.size()) {
                                                careerList.add(dc.getNewIndex(), careerItem);
                                                careerItemAdapter.notifyItemInserted(dc.getNewIndex());
                                            } else {
                                                careerList.add(careerItem); // Fallback to add at the end
                                                careerItemAdapter.notifyItemInserted(careerList.size() - 1);
                                            }
                                            break;
                                        case MODIFIED:
                                            if (dc.getOldIndex() < careerList.size()) {
                                                careerList.set(dc.getOldIndex(), careerItem);
                                                careerItemAdapter.notifyItemChanged(dc.getOldIndex());
                                                if (dc.getOldIndex() != dc.getNewIndex()) {
                                                    if (dc.getNewIndex() < careerList.size()) {
                                                        careerList.add(dc.getNewIndex(), careerList.remove(dc.getOldIndex()));
                                                        careerItemAdapter.notifyItemMoved(dc.getOldIndex(), dc.getNewIndex());
                                                    } else {
                                                        careerList.remove(dc.getOldIndex());
                                                        careerList.add(careerItem);
                                                        careerItemAdapter.notifyItemRemoved(dc.getOldIndex());
                                                        careerItemAdapter.notifyItemInserted(careerList.size() - 1);
                                                    }
                                                }
                                            } else {
                                                careerList.add(dc.getNewIndex(), careerItem);
                                                careerItemAdapter.notifyItemInserted(dc.getNewIndex());
                                            }
                                            // updateCareerItem(dc.getDocument());
                                            break;
                                        case REMOVED:
                                            if (dc.getOldIndex() < careerList.size()) {
                                                careerList.remove(dc.getOldIndex());
                                                careerItemAdapter.notifyItemRemoved(dc.getOldIndex());
                                            }
                                            // removeCareerItem(dc.getDocument());
                                            break;
                                    }
                                }
                            }
                            careerItemAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void updateCareerItem(DocumentSnapshot document) {
        String docId = document.getId();
        for (int i = 0; i < careerList.size(); i++) {
            if (careerList.get(i).getDocumentId().equals(docId)) {
                careerList.set(i, document.toObject(CareerItem.class));
                careerItemAdapter.notifyItemChanged(i);
                break;
            }
        }
    }

    private void removeCareerItem(DocumentSnapshot document) {
        String docId = document.getId();
        for (int i = 0; i < careerList.size(); i++) {
            if (careerList.get(i).getDocumentId().equals(docId)) {
                careerList.remove(i);
                careerItemAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }
    /*
    // 파이어스토어에서 section 값에 따라 필터링된 데이터 가져오기
    private void fetchFirestoreData(int section) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).collection("career")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CareerItem careerItem = document.toObject(CareerItem.class);
                            careerItem.setDocumentId(document.getId());
                            if (section == 1 ||
                                    (section == 2 && "대외활동".equals(careerItem.getCategory())) ||
                                    (section == 3 && "공모전".equals(careerItem.getCategory()))) {
                                dataList.add(careerItem);
                            }
                        }
                        careerItemAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("MyCareerFragment", "Error getting documents.", task.getException());
                    }
                });
    }

     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if(registration != null)
            registration.remove();
    }
}
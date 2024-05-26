package com.example.amap_teamproject.Career;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AlertDialog;

import com.example.amap_teamproject.databinding.FragmentMycareerBinding;
import com.example.amap_teamproject.R;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyCareerFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private CareerItemAdapter careerItemAdapter;
    private PageViewModel pageViewModel;
    private FragmentMycareerBinding binding;
    private List<CareerItem> dataList = new ArrayList<CareerItem>();

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
        careerItemAdapter = new CareerItemAdapter(dataList, getContext(), this::showEditItemDialog);;
        binding.titleTextView.setAdapter(careerItemAdapter);

        fetchFirestoreData(getArguments().getInt(ARG_SECTION_NUMBER));
        return root;
    }
    // 파이어스토어에서 section 값에 따라 필터링된 데이터 가져오기
    private void fetchFirestoreData(int section) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String collectionPath = "career";

        db.collection("users").document(userId).collection(collectionPath)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CareerItem data = document.toObject(CareerItem.class);
                            data.setId(document.getId());
                            if (section == 1 ||
                                    (section == 2 && "대외활동".equals(data.getField())) ||
                                    (section == 3 && "공모전".equals(data.getField()))) {
                                dataList.add(data);
                            }
                        }
                        careerItemAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("MyCareerFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    private void showEditItemDialog(CareerItem data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_item, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.edit_title);
        EditText contentEditText = dialogView.findViewById(R.id.edit_content);
        Button saveButton = dialogView.findViewById(R.id.button_save);
        Button updateButton = dialogView.findViewById(R.id.button_update);

        titleEditText.setText(data.getTitle());
        contentEditText.setText(data.getContent());
        saveButton.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);

        AlertDialog dialog = builder.create();

        updateButton.setOnClickListener(v -> {
            String newTitle = titleEditText.getText().toString();
            String newContent = contentEditText.getText().toString();
            updateDataInFirestore(data.getId(), newTitle, newContent);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateDataInFirestore(String documentId, String title, String content) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("content", content);

        db.collection("users").document(userId).collection("career").document(documentId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    // Data updated successfully
                    Log.d("PlaceholderFragment", "DocumentSnapshot successfully updated!");
                    fetchFirestoreData(getArguments().getInt(ARG_SECTION_NUMBER));
                })
                .addOnFailureListener(e -> {
                    // Error updating document
                    Log.w("PlaceholderFragment", "Error updating document", e);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.amap_teamproject.ui.MyPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

public class MyPageViewModel extends ViewModel {
    private String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private final MutableLiveData<String> text;
    private final FirebaseFirestore db;

    public MyPageViewModel() {
        text = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        loadUserName();
    }

    public LiveData<String> getText() {
        return text;
    }

    private void loadUserName() {
        DocumentReference docRef = db.collection("users").document(user_id); // USER_ID를 실제 사용자 ID로 변경
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                text.setValue(name);
            } else {
                text.setValue("No name found");
            }
        }).addOnFailureListener(e -> {
            text.setValue("Error getting name");
        });
    }
}
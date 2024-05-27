package com.example.amap_teamproject.Career;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.amap_teamproject.databinding.ActivityAddCareerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCareerActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private RadioGroup radioGroup;
    private RadioButton radioCompetition;
    private RadioButton radioActivity;
    private Button saveButton;
    private Button editButton;

    private FirebaseFirestore db;
    private ActivityAddCareerBinding binding;
    private String userId;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityAddCareerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        titleEditText = binding.titleEditText;
        contentEditText = binding.contentEditText;
        radioGroup = binding.radioGroup;
        radioCompetition = binding.radioCompetition;
        radioActivity = binding.radioActivity;
        saveButton = binding.saveButton;
        editButton = binding.editButton;

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(this, "사용자 인식 오류", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 새로 작성되는 건지 아이템을 클릭한건지 확인
        documentId = getIntent().getStringExtra("DOCUMENT_ID");
        if (documentId != null) {
            loadCareerItem(documentId);
            setEditable(false);
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        }
        else {
            setEditable(true);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(v -> {
            if (documentId == null)
                saveCareerItem();
            else
                updateCareerItem();
        });
        editButton.setOnClickListener(v -> setEditable(true));
    }

    private void setEditable(boolean isEditable) {  // 수정가능 여부
        titleEditText.setEnabled(isEditable);
        contentEditText.setEnabled(isEditable);
        radioCompetition.setEnabled(isEditable);
        radioActivity.setEnabled(isEditable);
        saveButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        editButton.setVisibility(isEditable ? View.GONE : View.VISIBLE);
    }

    private void saveCareerItem() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String category = radioCompetition.isChecked() ? "공모전" : "대외활동";

        if (title.isEmpty() || content.isEmpty() || radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        CareerItem careerItem = new CareerItem(title, content, category);
        db.collection("users").document(userId).collection("career")
                .add(careerItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Career item added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding career item", Toast.LENGTH_SHORT).show());
    }

    private void loadCareerItem(String documentId) {
        db.collection("users").document(userId).collection("career").document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String content = documentSnapshot.getString("content");
                        String category = documentSnapshot.getString("category");

                        titleEditText.setText(title);
                        contentEditText.setText(content);
                        if ("공모전".equals(category)) {
                            radioCompetition.setChecked(true);
                        } else {
                            radioActivity.setChecked(true);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading career item", Toast.LENGTH_SHORT).show());
    }

    // 수정 예정
    private void updateCareerItem() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String category = radioCompetition.isChecked() ? "공모전" : "대외활동";

        if (title.isEmpty() || content.isEmpty() || radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        CareerItem careerItem = new CareerItem(title, content, category);
        db.collection("users").document(userId).collection("career").document(documentId)
                .set(careerItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Career item updated", Toast.LENGTH_SHORT).show();
                    setEditable(false);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating career item", Toast.LENGTH_SHORT).show());
    }
}

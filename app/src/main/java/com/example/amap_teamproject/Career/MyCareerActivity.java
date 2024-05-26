package com.example.amap_teamproject.Career;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.ActivityMyCareerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyCareerActivity extends AppCompatActivity {

    private ActivityMyCareerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMyCareerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewPager viewPager = binding.viewPager;
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCareerActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
                builder.setView(dialogView)
                        .setTitle("Add Item")
                        .setPositiveButton("Save", (dialog, id) -> {
                            EditText titleEditText = dialogView.findViewById(R.id.title);
                            EditText contentEditText = dialogView.findViewById(R.id.content);

                            String title = titleEditText.getText().toString();
                            String content = contentEditText.getText().toString();
                            saveDataToFirestore(title, content);
                        })
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
                builder.create().show();
            }

            private void saveDataToFirestore(String title, String content) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("content", content);

                db.collection("users").document(userId).collection("career")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            // Data added successfully
                            Log.d("CareerActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            // Error adding document
                            Log.w("CareerActivity", "Error adding document", e);
                        });
            }
        });
    }
}
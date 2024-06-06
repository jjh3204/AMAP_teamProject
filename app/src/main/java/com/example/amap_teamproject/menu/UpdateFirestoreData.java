package com.example.amap_teamproject.menu;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UpdateFirestoreData {
    private FirebaseFirestore db;

    public UpdateFirestoreData() {
        db = FirebaseFirestore.getInstance();
    }

    public void updateAllDocuments() {
        // Update 'contests' collection
        db.collection("contests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.contains("hits")) {
                                db.collection("contests").document(document.getId())
                                        .update("hits", 0)
                                        .addOnSuccessListener(aVoid -> {
                                            // Successfully updated document
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle the error
                                        });
                            }
                        }
                    } else {
                        // Handle the error
                    }
                });

        // Update 'activities' collection
        db.collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.contains("hits")) {
                                db.collection("activities").document(document.getId())
                                        .update("hits", 0)
                                        .addOnSuccessListener(aVoid -> {
                                            // Successfully updated document
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle the error
                                        });
                            }
                        }
                    } else {
                        // Handle the error
                    }
                });
    }
}

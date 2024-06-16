package com.example.amap_teamproject.menu;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UpdateFirestoreData {
    private FirebaseFirestore db;

    public UpdateFirestoreData() {
        db = FirebaseFirestore.getInstance();
    }

    public void updateAllDocuments() {
        db.collection("contests")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.contains("hits")) {
                                db.collection("contests").document(document.getId())
                                        .update("hits", 0)
                                        .addOnSuccessListener(aVoid -> {
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                            }
                            if (!document.contains("likes")) {
                                db.collection("contests").document(document.getId())
                                        .update("likes", 0)
                                        .addOnSuccessListener(aVoid -> {
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                            }
                        }
                    } else {
                    }
                });

        db.collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.contains("hits")) {
                                db.collection("activities").document(document.getId())
                                        .update("hits", 0)
                                        .addOnSuccessListener(aVoid -> {
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                            }
                            if (!document.contains("likes")) {
                                db.collection("activities").document(document.getId())
                                        .update("likes", 0)
                                        .addOnSuccessListener(aVoid -> {
                                        })
                                        .addOnFailureListener(e -> {
                                        });
                            }
                        }
                    } else {
                    }
                });
    }
}

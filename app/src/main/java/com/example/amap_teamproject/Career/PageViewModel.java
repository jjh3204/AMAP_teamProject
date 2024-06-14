package com.example.amap_teamproject.Career;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
public class PageViewModel extends ViewModel {
    private final MutableLiveData<List<CareerItem>> careerListLiveData = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration registration;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId;
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LiveData<List<CareerItem>> getCareerListLiveData() {
        return careerListLiveData;
    }

    public void loadCareerItems(int section) {
        if (registration != null) {
            registration.remove();
        }

        Query query = db.collection("users").document(userId).collection("career")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            List<CareerItem> careerList = new ArrayList<>();
                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                                CareerItem careerItem = dc.getDocument().toObject(CareerItem.class);
                                careerItem.setDocumentId(dc.getDocument().getId());

                                if (section == 1 ||
                                        (section == 2 && "대외활동".equals(careerItem.getCategory())) ||
                                        (section == 3 && "공모전".equals(careerItem.getCategory()))) {
                                    switch (dc.getType()) {
                                        case ADDED:
                                            careerList.add(careerItem);
                                            break;
                                        case MODIFIED:
                                            for (int i = 0; i < careerList.size(); i++) {
                                                if (careerList.get(i).getDocumentId().equals(careerItem.getDocumentId())) {
                                                    careerList.set(i, careerItem);
                                                    break;
                                                }
                                            }
                                            break;
                                        case REMOVED:
                                            careerList.removeIf(item -> item.getDocumentId().equals(careerItem.getDocumentId()));
                                            break;
                                    }
                                }
                            }
                            careerListLiveData.setValue(careerList);
                        }
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (registration != null) {
            registration.remove();
        }
    }
}
package com.example.amap_teamproject.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.menu.Activity;
import com.example.amap_teamproject.menu.ActivityDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ImageFragment extends Fragment {

    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_TITLE = "title";
    private String imageUrl;
    private String title;
    private static final String TAG = "ImageFragment";

    public static ImageFragment newInstance(String imageUrl, String title) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(ARG_IMAGE_URL);
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        Glide.with(this).load(imageUrl).into(imageView);
        titleTextView.setText(title);

        // 이미지 클릭 리스너 추가
        imageView.setOnClickListener(v -> {
            Log.d(TAG, "Image clicked: " + title);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("activities")
                    .whereEqualTo("title", title)
                    .whereEqualTo("poster_url", imageUrl)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result != null && !result.isEmpty()) {
                                DocumentSnapshot document = result.getDocuments().get(0);
                                Activity activity = document.toObject(Activity.class);
                                Intent intent = new Intent(getActivity(), ActivityDetailActivity.class);
                                intent.putExtra(ActivityDetailActivity.EXTRA_ACTIVITY, activity);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "Document not found.");
                                Toast.makeText(getContext(), "Activity not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Query result is empty or null.");
                            Toast.makeText(getContext(), "Activity not found", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Firestore query failed", e);
                        Toast.makeText(getContext(), "Failed to fetch activity", Toast.LENGTH_SHORT).show();
                    });
        });

        return view;
    }
}




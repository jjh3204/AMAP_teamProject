package com.example.amap_teamproject.ui.MyPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.amap_teamproject.Career.MyCareerActivity;
import com.example.amap_teamproject.Login.LoginActivity;
import com.example.amap_teamproject.MainActivity;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.FragmentMyPageBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MyPageFragment extends Fragment {

    private MyPageViewModel myPageViewModel;
    private FragmentMyPageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myPageViewModel = new ViewModelProvider(this).get(MyPageViewModel.class);

        binding = FragmentMyPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.nameTextView;
        Button logoutButton = binding.logoutButton;
        FrameLayout careerManagementFrame = binding.careerManagementFrame;
        myPageViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("알람 팝업")
                        .setMessage("로그 아웃하시겠습니까??")
                        .setPositiveButton("로그 아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        })
                        .show();

            }
        });

        careerManagementFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyCareerActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    public void onResume() {
        super.onResume();
        ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText("MY");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
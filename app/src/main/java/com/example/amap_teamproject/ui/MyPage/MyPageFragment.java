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
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.amap_teamproject.Career.MyCareerActivity;
import com.example.amap_teamproject.Login.LoginActivity;
import com.example.amap_teamproject.MainActivity;
import com.example.amap_teamproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;  // 추가된 부분
import com.google.firebase.firestore.FirebaseFirestore;

public class MyPageFragment extends Fragment {

    private static final int REQUEST_UPDATE_USER_INFO = 1;
    private Button deleteUserButton;
    private Button logoutButton;
    private Button updateUserInfoButton;
    private FrameLayout careerManagementFrame;
    private TextView nameTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_page, container, false);

        deleteUserButton = root.findViewById(R.id.deleteUserButton);
        logoutButton = root.findViewById(R.id.logoutButton);
        updateUserInfoButton = root.findViewById(R.id.updateUserInfoButton);
        careerManagementFrame = root.findViewById(R.id.careerManagementFrame);
        nameTextView = root.findViewById(R.id.nameTextView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            fetchUserName(); // 사용자 이름 가져오기
        }

        // 전달된 사용자 이름 받기
        if (getArguments() != null) {
            String userName = getArguments().getString("USER_NAME");
            nameTextView.setText(userName);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("로그 아웃하시겠습니까?")
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
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
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

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });

        updateUserInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateUserInfoActivity.class);
                startActivityForResult(intent, REQUEST_UPDATE_USER_INFO); // 바뀐 이름 바로 표시
            }
        });
        return root;
    }

    private void fetchUserName() {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String name = task.getResult().getString("name");
                    if (name != null) {
                        nameTextView.setText(name);
                    }
                } else {
                    Toast.makeText(getActivity(), "사용자 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override // 바뀐 이름 바로 표시
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 바뀐 이름 바로 표시
        super.onActivityResult(requestCode, resultCode, data); // 바뀐 이름 바로 표시
        if (requestCode == REQUEST_UPDATE_USER_INFO && resultCode == getActivity().RESULT_OK && data != null) { // 바뀐 이름 바로 표시
            String newName = data.getStringExtra("newName"); // 바뀐 이름 바로 표시
            if (newName != null) { // 바뀐 이름 바로 표시
                nameTextView.setText(newName); // 바뀐 이름 바로 표시
            } // 바뀐 이름 바로 표시
        } // 바뀐 이름 바로 표시
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("비밀번호 확인");

        View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_delete_user, null, false);
        final EditText input = viewInflated.findViewById(R.id.inputPassword);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String password = input.getText().toString();
                reauthenticateAndDeleteUser(password);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void reauthenticateAndDeleteUser(String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showDeleteConfirmationDialog();
                    } else {
                        Toast.makeText(getActivity(), "비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("회원탈퇴")
                .setMessage("정말로 회원탈퇴를 하시겠습니까?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(userId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "User account deleted.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Failed to delete user data.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
package com.example.amap_teamproject.ui.MyPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri; // 갤러리 접근
import android.os.Bundle;
import android.util.Log; // 로그 출력, 어떤 오류가 발생하는지 출력
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView; // 갤러리 접근
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide; // 프로필 사진 저장
import com.example.amap_teamproject.Career.MyCareerActivity;
import com.example.amap_teamproject.Login.LoginActivity;
import com.example.amap_teamproject.MainActivity;
import com.example.amap_teamproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener; // 프로필 사진 저장
import com.google.android.gms.tasks.OnSuccessListener; // 프로필 사진 저장
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;  // 추가된 부분
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage; // 프로필 사진 저장
import com.google.firebase.storage.StorageReference; // 프로필 사진 저장
import com.google.firebase.storage.UploadTask; // 프로필 사진 저장
public class MyPageFragment extends Fragment {

    private static final int REQUEST_UPDATE_USER_INFO = 1;
    private static final int REQUEST_IMAGE_PICK = 2; // 갤러리 접근

    private TextView deleteUserTextView;
    private TextView logoutTextView;
    private TextView updateUserInfoTextView;
    private FrameLayout careerManagementFrame;
    private TextView nameTextView;
    private ImageView profileImageView; // 갤러리 접근
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef; // 프로필 사진 저장
    private String userId;
    private Uri imageUri; // 필요없을 수도 있는 코드

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_page, container, false);

        deleteUserTextView = root.findViewById(R.id.deleteUserTextView); // 텍스트뷰로 변환
        logoutTextView = root.findViewById(R.id.logoutTextView); // 텍스트뷰로 변환
        updateUserInfoTextView = root.findViewById(R.id.updateUserInfoTextView); // 텍스트뷰로 변환
        careerManagementFrame = root.findViewById(R.id.careerManagementFrame);
        nameTextView = root.findViewById(R.id.nameTextView);
        profileImageView = root.findViewById(R.id.profileImageView); // 갤러리 접근

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference(); // 프로필 사진 저장

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            fetchUserName(); // 사용자 이름 가져오기
            fetchProfileImage(); // 프로필 초기화
        }

        // 전달된 사용자 이름 받기
        if (getArguments() != null) {
            String userName = getArguments().getString("USER_NAME");
            nameTextView.setText(userName);
        }

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("로그아웃하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
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

        deleteUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog();
            }
        });

        updateUserInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateUserInfoActivity.class);
                startActivityForResult(intent, REQUEST_UPDATE_USER_INFO); // 바뀐 이름 바로 표시
            }
        });


    // 프로필 이미지 클릭 리스너 추가 (갤러리 접근)
        profileImageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery();
        }
    });

        return root;
}

    // 갤러리 접근
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
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
                    Toast.makeText(getActivity(), "사용자 이름을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchProfileImage() {
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String imageUrl = task.getResult().getString("profileImageUrl");
                    if (imageUrl != null) {
                        Glide.with(MyPageFragment.this).load(imageUrl).into(profileImageView); // 프로필 초기화
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_profile_placeholder); // R.drawable.default_profile_image는 기본 프로필 이미지 리소스
                        //Toast.makeText(getActivity(), "프로필 이미지를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "프로필 이미지를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    } // 프로필 초기화

    @Override // 바뀐 이름 바로 표시 및 갤러리 접근
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 바뀐 이름 바로 표시
        super.onActivityResult(requestCode, resultCode, data); // 바뀐 이름 바로 표시
        if (requestCode == REQUEST_UPDATE_USER_INFO && resultCode == getActivity().RESULT_OK && data != null) { // 바뀐 이름 바로 표시
            String newName = data.getStringExtra("newName"); // 바뀐 이름 바로 표시
            if (newName != null) { // 바뀐 이름 바로 표시
                nameTextView.setText(newName); // 바뀐 이름 바로 표시
            } // 바뀐 이름 바로 표시
        } // 바뀐 이름 바로 표시
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == getActivity().RESULT_OK && data != null) { // 갤러리 접근
            Uri imageUri = data.getData(); // 갤러리 접근
           // profileImageView.setImageURI(imageUri); // 갤러리 접근: 선택한 이미지를 ImageView에 설정
            //uploadProfileImage(imageUri); // 프로필 사진 저장: 선택한 이미지를 Firebase Storage에 업로드
            confirmProfileImageChange(imageUri); // 프로필 변경 문구
        } // 갤러리 접근
    }

    // 프로필 변경 문구: 선택한 이미지를 확인하는 다이얼로그 표시
    private void confirmProfileImageChange(Uri uri) {
        new AlertDialog.Builder(getActivity())
                .setTitle("프로필 사진 변경")
                .setMessage("이 사진을 프로필로 선택하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        profileImageView.setImageURI(uri);
                        uploadProfileImage(uri);
                        Toast.makeText(getActivity(), "프로필 사진 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show(); // 프로필 변경 문구
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // 프로필 사진 저장: 선택한 이미지를 Firebase Storage에 업로드
    private void uploadProfileImage(Uri uri) {
        if (uri != null) {
            StorageReference profileImageRef = storageRef.child("profileImages/" + userId + ".jpg");
            profileImageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            saveProfileImageUrl(downloadUri.toString()); // 프로필 사진 저장: Firestore에 이미지 URL 저장
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "프로필 이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 프로필 사진 저장: Firestore에 이미지 URL 저장
    private void saveProfileImageUrl(String downloadUrl) {
        db.collection("users").document(userId).update("profileImageUrl", downloadUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "프로필 이미지 URL 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                                    Toast.makeText(getActivity(), "회원정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "회원정보를 삭제하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "회원정보를 삭제하는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
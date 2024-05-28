package com.example.amap_teamproject.ui.MyPage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull; // 회원탈퇴 코드
import androidx.appcompat.app.AlertDialog;  // 탈퇴 확인
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater; // 탈퇴 확인
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // 탈퇴 확인
import android.widget.FrameLayout; // FrameLayout을 사용하기 위한 import 추가
import android.widget.TextView; // 마이페이지에 사용자 이름 표시
import android.widget.Toast; // 회원탈퇴 코드

import com.example.amap_teamproject.Login.LoginActivity;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.FragmentMyCareerBinding;

import com.google.android.gms.tasks.OnCompleteListener; // 회원탈퇴 코드
import com.google.android.gms.tasks.Task; // 회원탈퇴 코드
import com.google.firebase.auth.AuthCredential; // 탈퇴 확인
import com.google.firebase.auth.EmailAuthProvider; // 탈퇴 확인
import com.google.firebase.auth.FirebaseAuth; // 회원탈퇴 코드
import com.google.firebase.auth.FirebaseUser; // 회원탈퇴 코드
import com.google.firebase.firestore.FirebaseFirestore; // 회원탈퇴 코드

public class MyPage extends AppCompatActivity {

    private Button deleteUserButton;
    private Button logoutButton;
    private FrameLayout careerManagementFrame;
    private TextView nameTextView; // 마이페이지에 사용자 이름 표시

    private FirebaseAuth mAuth; // 회원탈퇴 코드
    private FirebaseFirestore db; // 회원탈퇴 코드
    private String userId; // 회원탈퇴 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_page);

        deleteUserButton = findViewById(R.id.deleteUserButton);
        logoutButton = findViewById(R.id.logoutButton);
        careerManagementFrame = findViewById(R.id.careerManagementFrame);
        nameTextView = findViewById(R.id.nameTextView); // 마이페이지에 사용자 이름 표시

        mAuth = FirebaseAuth.getInstance(); // 회원탈퇴 코드
        db = FirebaseFirestore.getInstance(); // 회원탈퇴 코드

        // 추가된 부분: Intent로 전달된 name 데이터를 TextView에 설정
        Intent intent = getIntent();
        String name = intent.getStringExtra("USER_NAME");
        if (name != null) {
            nameTextView.setText(name);
        }

        FirebaseUser user = mAuth.getCurrentUser(); // 회원탈퇴 코드
        if (user != null) {
            userId = user.getUid(); // 회원탈퇴 코드
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPage.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        careerManagementFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 화면으로 이동하는 코드
                Intent intent = new Intent(MyPage.this, CareerManagementActivity.class);
                startActivity(intent);
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() { // 회원탈퇴 코드
            @Override
            public void onClick(View v) {
                showPasswordDialog(); // 탈퇴 확인
            }
        });


    }

    private void showPasswordDialog() { // 탈퇴 확인
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호 확인");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.comfirm_delete_user, null, false);
        final EditText input = viewInflated.findViewById(R.id.inputPassword);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String password = input.getText().toString();
                reauthenticateAndDeleteUser(password); // 탈퇴 확인
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

    private void reauthenticateAndDeleteUser(String password) { // 탈퇴 확인
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showDeleteConfirmationDialog(); // 탈퇴 확인
                    } else {
                        Toast.makeText(MyPage.this, "비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showDeleteConfirmationDialog() { // 탈퇴 확인
        new AlertDialog.Builder(this)
                .setTitle("회원탈퇴")
                .setMessage("정말로 회원탈퇴를 하시겠습니까?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(); // 탈퇴 확인
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUser() { // 회원탈퇴 코드
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
                                    Toast.makeText(MyPage.this, "User account deleted.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MyPage.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MyPage.this, "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(MyPage.this, "Failed to delete user data.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

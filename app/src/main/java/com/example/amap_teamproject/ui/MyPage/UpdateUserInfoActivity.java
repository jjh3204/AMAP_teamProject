package com.example.amap_teamproject.ui.MyPage;
import android.content.DialogInterface;
import android.content.Intent; // 수정된 이름 바로 표시
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView; // 텍스트뷰로 변환
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amap_teamproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserInfoActivity extends AppCompatActivity {

    private TextView textViewUpdateName, textViewUpdatePassword; // 텍스트뷰로 변환
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        textViewUpdateName = findViewById(R.id.textViewUpdateName); // 텍스트뷰로 변환
        textViewUpdatePassword = findViewById(R.id.textViewUpdatePassword); // 텍스트뷰로 변환

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        textViewUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateNameDialog();
            }
        });

        textViewUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdatePasswordDialog();
            }
        });
    }

    private void showUpdateNameDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_update_name, null);
        final EditText editTextNewName = dialogView.findViewById(R.id.editTextNewName);
        final EditText editTextCurrentPasswordForName = dialogView.findViewById(R.id.editTextCurrentPasswordForName);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("닉네임 변경");
        dialogBuilder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = editTextNewName.getText().toString().trim();
                String currentPassword = editTextCurrentPasswordForName.getText().toString().trim();
                if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(currentPassword)) {
                    updateName(newName, currentPassword);
                } else {
                    Toast.makeText(UpdateUserInfoActivity.this, "새 닉네임과 기존 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("취소", null);
        dialogBuilder.create().show();
    }

    private void updateName(final String newName, String currentPassword) {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        db.collection("users").document(userId).update("name", newName)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(UpdateUserInfoActivity.this, "닉네임이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                            Intent resultIntent = new Intent(); // 수정된 이름 바로 표시
                                            resultIntent.putExtra("newName", newName); // 수정된 이름 바로 표시
                                            setResult(RESULT_OK, resultIntent); // 수정된 이름 바로 표시
                                            finish(); // 수정된 이름 바로 표시
                                        } else {
                                            Toast.makeText(UpdateUserInfoActivity.this, "닉네임 변경 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(UpdateUserInfoActivity.this, "기존 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showUpdatePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_update_password, null);
        final EditText editTextCurrentPasswordForPassword = dialogView.findViewById(R.id.editTextCurrentPasswordForPassword);
        final EditText editTextNewPassword = dialogView.findViewById(R.id.editTextNewPassword);
        final EditText editTextNewPasswordConfirm = dialogView.findViewById(R.id.editTextNewPasswordConfirm);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("비밀번호 변경");
        dialogBuilder.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentPassword = editTextCurrentPasswordForPassword.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();
                String newPasswordConfirm = editTextNewPasswordConfirm.getText().toString().trim();
                if (!TextUtils.isEmpty(currentPassword) && !TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(newPasswordConfirm)) {
                    if (newPassword.equals(newPasswordConfirm)) {
                        updatePassword(currentPassword, newPassword);
                    } else {
                        Toast.makeText(UpdateUserInfoActivity.this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateUserInfoActivity.this, "기존 비밀번호, 새 비밀번호, 새 비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("취소", null);
        dialogBuilder.create().show();
    }

    private void updatePassword(final String currentPassword, final String newPassword) {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UpdateUserInfoActivity.this, "비밀번호가 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UpdateUserInfoActivity.this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(UpdateUserInfoActivity.this, "기존 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
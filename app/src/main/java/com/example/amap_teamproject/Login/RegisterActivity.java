package com.example.amap_teamproject.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns; // 추가된 부분: 이메일 형식 검사
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amap_teamproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest; // 회원가입창에 name을 추가하면서 추가한 코드
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.nameEditText); // 추가된 부분
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(
                        nameEditText.getText().toString(), // 수정된 부분: name 인자 추가
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        confirmPasswordEditText.getText().toString());
            }
        });
    }

    private void createAccount(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) { // 추가된 부분
            nameEditText.setError("이름을 입력해야 합니다."); // 추가된 부분
            return; // 추가된 부분
        } // 추가된 부분

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("이메일을 입력해야 합니다.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // 추가된 부분: 이메일 형식 검사
            emailEditText.setError("올바른 이메일 형식이 아닙니다.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("비밀번호를 입력해야 합니다.");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("비밀번호가 6글자 이상이어야 합니다.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("비밀번호가 서로 일치하지 않습니다.");
            return;
        }

        db.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // 같은 닉네임의 사용자가 있을 경우
                            Toast.makeText(this, "이미 같은 닉네임의 사용자가 있습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(name) // 추가된 부분: 이름 설정
                                                            .build();
                                                    user.updateProfile(profileUpdates)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // Firestore에 사용자 정보 저장
                                                                        DocumentReference docRef = db.collection("users").document(user.getUid());
                                                                        docRef.set(new User(name, email))
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                                                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                                            startActivity(intent);
                                                                                            finish();
                                                                                        } else {
                                                                                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "회원가입 실패";
                                                                                            Log.e("RegisterActivity", "회원가입 실패: " + errorMessage);
                                                                                            Toast.makeText(RegisterActivity.this, "회원가입 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    } else {
                                                                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "회원가입 실패";
                                                                        Log.e("RegisterActivity", "회원가입 실패: " + errorMessage); // 추가된 부분: 로그 출력
                                                                        Toast.makeText(RegisterActivity.this, "회원가입 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else {
                                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                    Log.e("RegisterActivity", "이미 등록된 이메일입니다."); // 등록된 이메일 문구 출력
                                                    Toast.makeText(RegisterActivity.this, "이미 등록된 이메일입니다.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "회원가입 실패";
                                                    Log.e("RegisterActivity", "회원가입 실패: " + errorMessage); // 로그 출력
                                                    Toast.makeText(RegisterActivity.this, "회원가입 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    class User {
        private String name;
        private String email;

        public User() {
        }

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        // Getter and Setter methods
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
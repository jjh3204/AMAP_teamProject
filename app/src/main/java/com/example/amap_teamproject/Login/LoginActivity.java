package com.example.amap_teamproject.Login;

import android.content.Intent; // 변경된 부분: RegisterActivity로 이동을 위해 추가된 import 문
import android.os.Bundle;
//gpt로 추가한 코드
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

//gpt로 추가한 코드
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.amap_teamproject.R;

//firebase 관련 gpt 추가 코드
import com.example.amap_teamproject.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot; // 사용자 이름 출력
import com.google.firebase.firestore.FirebaseFirestore; // 사용자 이름 출력

public class LoginActivity extends AppCompatActivity {

    //firebase 관련으로 gpt 추가 코드
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firestore 인스턴스 추가 , 사용자이름 출력
    private EditText emailEditText, passwordEditText;
    private Button registerButton, loginButton;
    private FirebaseUser currentUser;

    protected void onStart(){
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Firestore 인스턴스 초기화 , 사용자이름추가
        //로그인 회원가입
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RegisterActivity로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent); // 변경된 부분: RegisterActivity 시작
                //회원가입 창을 만들면서 삭제한 코드

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                db.collection("users").document(user.getUid()) // Firestore에서 사용자 정보 가져오기
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful() && task.getResult() != null) {
                                                    String name = task.getResult().getString("name"); // Firestore에서 이름 가져오기
                                                    Toast.makeText(LoginActivity.this, "Login Success.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.putExtra("USER_NAME", name); // 변경된 부분: 이름 전달
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
   /* private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Login Success.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close activity after successful registration
                            // Update UI with the signed-in user's information
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}*/
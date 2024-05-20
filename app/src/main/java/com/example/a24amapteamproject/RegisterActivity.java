package com.example.a24amapteamproject;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString());
            }
        });
    }

    private void createAccount(String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // 추가된 부분: 이메일 형식 검사
            emailEditText.setError("Please enter a valid email address.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Registration Success.", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity after successful registration
                        } else {
                            // If sign in fails, display a message to the user.
                            String errorMessage = task.getException().getMessage();
                            Log.e("RegisterActivity", "Registration Failed: " + errorMessage); // 추가된 부분: 로그 출력
                            //Toast.makeText(RegisterActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show(); // 추가된 부분: 오류 메시지 표시
                            Toast.makeText(RegisterActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show(); // 윗줄이랑 겹쳐서 해당 코드 삭제
                        }
                    }
                });
    }
}


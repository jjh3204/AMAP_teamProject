package com.example.amap_teamproject.ui.MyPage;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout; // FrameLayout을 사용하기 위한 import 추가
import android.widget.TextView; // 마이페이지에 사용자 이름 표시

import com.example.amap_teamproject.Login.LoginActivity;
import com.example.amap_teamproject.R;
import com.example.amap_teamproject.databinding.FragmentMyCareerBinding;

public class MyPage extends AppCompatActivity {

    private Button updateInfoButton;
    private Button logoutButton;
    private FrameLayout careerManagementFrame;
    private TextView nameTextView; // 마이페이지에 사용자 이름 표시

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_page);

        updateInfoButton = findViewById(R.id.updateInfoButton);
        logoutButton = findViewById(R.id.logoutButton);
        careerManagementFrame = findViewById(R.id.careerManagementFrame);
        nameTextView = findViewById(R.id.nameTextView); // 마이페이지에 사용자 이름 표시

        // 추가된 부분: Intent로 전달된 name 데이터를 TextView에 설정
        Intent intent = getIntent();
        String name = intent.getStringExtra("USER_NAME");
        if (name != null) {
            nameTextView.setText(name);
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
                Intent intent = new Intent(MyPage.this, FragmentMyCareerBinding.class);
                startActivity(intent);
            }
        });
    }
}

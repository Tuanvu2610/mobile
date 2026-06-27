package com.example.bansach.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.User;
import com.example.bansach.model.Account;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtFullName, edtRegEmail, edtRegPassword, edtConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvGoToLogin;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private DatabaseReference userDatabaseReference;

    private ImageView btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
        setContentView(R.layout.activity_register);

        databaseReference = FirebaseDatabase.getInstance().getReference("accounts").child("accounts");

        btnBack = findViewById(R.id.btnBack);
        edtFullName = findViewById(R.id.edtFullName);
        edtRegEmail = findViewById(R.id.edtRegEmail);
        edtRegPassword = findViewById(R.id.edtRegPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnRegister.setOnClickListener(v -> handleRegister());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String username = edtRegEmail.getText().toString().trim();
        String password = edtRegPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            edtFullName.setError("Vui lòng nhập họ tên");
            return;
        }
        if (username.isEmpty()) {
            edtRegEmail.setError("Vui lòng nhập username");
            return;
        }
        if (password.isEmpty()) {
            edtRegPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }
        if (password.length() < 6) {
            edtRegPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        showLoading(true);

        // Kiểm tra username đã tồn tại chưa
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Account account = child.getValue(Account.class);
                    if (account != null
                            && account.getUsername() != null
                            && account.getUsername().equals(username)) {
                        showLoading(false);
                        edtRegEmail.setError("Username đã tồn tại");
                        return;
                    }
                }

                // Tạo tài khoản mới
                long newId = snapshot.getChildrenCount() + 1;
                String newIdStr = String.valueOf(newId);
                Account newAccount = new Account(newIdStr, username, password, "user", "active");
                databaseReference.child(newIdStr).setValue(newAccount)
                        .addOnSuccessListener(aVoid -> {
                            User newUser = new User(newIdStr,"" , fullName, "", "", "");
                            userDatabaseReference.child(newIdStr).setValue(newUser);
                            showLoading(false);
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            showLoading(false);
                            Toast.makeText(RegisterActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
    }
}
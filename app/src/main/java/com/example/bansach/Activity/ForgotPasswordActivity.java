package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.Account;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText edtForgotEmail;
    private MaterialButton btnSendResetLink;
    private TextView tvBackToLogin;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        databaseReference = FirebaseDatabase.getInstance().getReference("accounts").child("accounts");

        edtForgotEmail = findViewById(R.id.edtForgotEmail);
        btnSendResetLink = findViewById(R.id.btnSendResetLink);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
        progressBar = findViewById(R.id.progressBar);

        btnSendResetLink.setOnClickListener(v -> handleForgotPassword());

        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleForgotPassword() {
        String username = edtForgotEmail.getText().toString().trim();

        if (username.isEmpty()) {
            edtForgotEmail.setError("Vui lòng nhập username");
            return;
        }

        showLoading(true);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Account account = child.getValue(Account.class);
                    if (account != null
                            && account.getUsername() != null
                            && account.getUsername().equals(username)) {
                        found = true;
                        showLoading(false);
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Mật khẩu của bạn là: " + account.getPassword(),
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                showLoading(false);
                if (!found) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Không tìm thấy tài khoản với username này!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(ForgotPasswordActivity.this,
                        "Lỗi kết nối: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSendResetLink.setEnabled(!isLoading);
    }
}
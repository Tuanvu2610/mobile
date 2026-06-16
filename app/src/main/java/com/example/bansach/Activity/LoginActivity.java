package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.bansach.R;
import com.example.bansach.model.Account;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private MaterialButton btnLogin, btnGoogleSignIn;
    private TextView tvForgotPassword, tvGoToRegister;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private CredentialManager credentialManager;

    // Thay YOUR_WEB_CLIENT_ID bằng Web Client ID trong Firebase Console
    // Vào Firebase Console → Authentication → Sign-in method → Google → Web SDK configuration
    private static final String WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("accounts").child("accounts");

        credentialManager = CredentialManager.create(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> handleLogin());

        btnGoogleSignIn.setOnClickListener(v -> handleGoogleSignIn());

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void handleLogin() {
        String username = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty()) {
            edtEmail.setError("Vui lòng nhập username");
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
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
                            && account.getUsername().equals(username)
                            && account.getPassword() != null
                            && account.getPassword().equals(password)) {

                        found = true;

                        if (!"active".equals(account.getStatus())) {
                            showLoading(false);
                            Toast.makeText(LoginActivity.this,
                                    "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        navigateByRole(account.getRole());
                        return;
                    }
                }
                showLoading(false);
                if (!found) {
                    Toast.makeText(LoginActivity.this,
                            "Username hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleGoogleSignIn() {
        showLoading(true);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        runOnUiThread(() -> {
                            try {
                                GoogleIdTokenCredential googleIdTokenCredential =
                                        GoogleIdTokenCredential.createFrom(result.getCredential().getData());
                                String email = googleIdTokenCredential.getId();
                                String displayName = googleIdTokenCredential.getDisplayName();
                                checkGoogleAccountInDatabase(email, displayName);
                            } catch (Exception e) {
                                showLoading(false);
                                Toast.makeText(LoginActivity.this,
                                        "Lỗi xử lý Google Sign-In: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(LoginActivity.this,
                                    "Đăng nhập Google thất bại: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );
    }

    private void checkGoogleAccountInDatabase(String email, String displayName) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Account account = child.getValue(Account.class);
                    if (account != null
                            && account.getUsername() != null
                            && account.getUsername().equals(email)) {
                        found = true;

                        if (!"active".equals(account.getStatus())) {
                            showLoading(false);
                            Toast.makeText(LoginActivity.this,
                                    "Tài khoản đã bị khóa!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        navigateByRole(account.getRole());
                        return;
                    }
                }

                if (!found) {
                    createGoogleAccount(email, displayName, snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createGoogleAccount(String email, String displayName, long currentCount) {
        String newId = String.valueOf(currentCount + 1);
        Account newAccount = new Account(newId, email, "", "user", "active");

        databaseReference.child(newId).setValue(newAccount)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this,
                            "Chào mừng " + displayName + "!", Toast.LENGTH_SHORT).show();
                    navigateByRole("user");
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this,
                            "Lỗi tạo tài khoản: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateByRole(String role) {
        showLoading(false);
        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        Intent intent;
        if ("admin".equals(role)) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, UserActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        runOnUiThread(() -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!isLoading);
            btnGoogleSignIn.setEnabled(!isLoading);
        });
    }
}

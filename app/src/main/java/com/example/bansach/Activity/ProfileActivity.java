package com.example.bansach.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;
import com.example.bansach.model.Account;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvPoints, tvAvatarInitials;
    private LinearLayout layoutAccountInfo, layoutSavedAddress, layoutLoyalCustomer;
    private LinearLayout layoutOrderPending, layoutOrderPickup, layoutOrderDelivering, layoutOrderReview;
    private TextView tvOrderHistory, tvLogout;
    private ImageView btnBack;
    private SessionManager sessionManager;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        initViews();
        loadUserData();
        setupClickListeners();
    }

    private void initViews() {

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPoints = findViewById(R.id.tvPoints);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        tvOrderHistory = findViewById(R.id.tvOrderHistory);
        tvLogout = findViewById(R.id.tvLogout);

        layoutAccountInfo = findViewById(R.id.layoutAccountInfo);
        layoutSavedAddress = findViewById(R.id.layoutSavedAddress);
        layoutLoyalCustomer = findViewById(R.id.layoutLoyalCustomer);

        layoutOrderPending = findViewById(R.id.layoutOrderPending);
        layoutOrderPickup = findViewById(R.id.layoutOrderPickup);
        layoutOrderDelivering = findViewById(R.id.layoutOrderDelivering);
        layoutOrderReview = findViewById(R.id.layoutOrderReview);
        btnBack = findViewById(R.id.btnBack);

    }

    private void loadUserData() {
        String username = sessionManager.getUsername();
        String userId = sessionManager.getUserId();

        if (username != null && username.length() >= 2) {
            tvAvatarInitials.setText(username.substring(0, 2).toUpperCase());
        } else if (username != null && !username.isEmpty()) {
            tvAvatarInitials.setText(username.substring(0, 1).toUpperCase());
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("accounts").child("accounts");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Account account = child.getValue(Account.class);
                    if (account != null && userId.equals(account.getUser_id())) {
                        tvUsername.setText(account.getUsername());
                        tvEmail.setText(account.getUsername());
                        tvPoints.setText("0 điểm");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this,
                        "Lỗi tải dữ liệu: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        if (tvOrderHistory != null) {
            tvOrderHistory.setOnClickListener(v ->
                    Toast.makeText(this, "Lịch sử đơn hàng", Toast.LENGTH_SHORT).show());
        }

        if (layoutOrderPending != null) {
            layoutOrderPending.setOnClickListener(v ->
                    Toast.makeText(this, "Chờ xác nhận", Toast.LENGTH_SHORT).show());
        }
        if (layoutOrderPickup != null) {
            layoutOrderPickup.setOnClickListener(v ->
                    Toast.makeText(this, "Chờ lấy hàng", Toast.LENGTH_SHORT).show());
        }
        if (layoutOrderDelivering != null) {
            layoutOrderDelivering.setOnClickListener(v ->
                    Toast.makeText(this, "Đang giao", Toast.LENGTH_SHORT).show());
        }
        if (layoutOrderReview != null) {
            layoutOrderReview.setOnClickListener(v ->
                    Toast.makeText(this, "Đánh giá", Toast.LENGTH_SHORT).show());
        }

        // Menu dưới
        if (layoutAccountInfo != null) {
            layoutAccountInfo.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, AccountInfoActivity.class)));
        }
        if (layoutSavedAddress != null) {
            layoutSavedAddress.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, SavedAddressActivity.class)));
        }
        if (layoutLoyalCustomer != null) {
            layoutLoyalCustomer.setOnClickListener(v ->
                    Toast.makeText(this, "Khách hàng thân thiết", Toast.LENGTH_SHORT).show());
        }

        // Đăng xuất
        if (tvLogout != null) {
            tvLogout.setOnClickListener(v -> handleLogout());
        }



        tvOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("TRANG_THAI", "Tất cả");
            startActivity(intent);
        });

        layoutOrderPending.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("TRANG_THAI", "Chờ xử lý");
            startActivity(intent);
        });

        layoutOrderPickup.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("TRANG_THAI", "Hoàn thành");
            startActivity(intent);
        });

        layoutOrderDelivering.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("TRANG_THAI", "Đang giao");
            startActivity(intent);
        });

        layoutOrderReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            intent.putExtra("TRANG_THAI", "Đã hủy");
            startActivity(intent);
        });
    }

    private void handleLogout() {
        // Xóa session
        sessionManager.logout();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
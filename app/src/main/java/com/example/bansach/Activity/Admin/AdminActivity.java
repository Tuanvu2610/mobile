package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.Activity.LoginActivity;
import com.example.bansach.Activity.ProfileActivity;
import com.example.bansach.Activity.SessionManager;
import com.example.bansach.R;

public class AdminActivity extends BaseActivityAdmin {

    private LinearLayout quanlysanpham;
    private LinearLayout quanlydanhgia;
    private LinearLayout quanlydanhmuc;
    private LinearLayout thongke;
    private LinearLayout quanlydonhang;
    private LinearLayout quanlyvoucher;
    private Button btnAdminLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
        sessionManager = new SessionManager(this);

        quanlysanpham = findViewById(R.id.quanlysanpham);
        quanlysanpham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageBookActivity.class);
                intent.putExtra("is_admin", true);
                startActivity(intent);

            }
        });
        quanlydonhang = findViewById(R.id.quanlydonhang);
        quanlydonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminOrderActivity.class);
                intent.putExtra("is_admin", true);
                startActivity(intent);

            }
        });
        findViewById(R.id.cardManageUsers).setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminUserManagementActivity.class);
            intent.putExtra("is_admin", true);
            startActivity(intent);
        });

        quanlydanhgia = findViewById(R.id.quanlydanhgia);
        quanlydanhgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminReviewActivity.class);
                intent.putExtra("is_admin", true);
                startActivity(intent);

            }
        });

        quanlydanhmuc = findViewById(R.id.quanlydanhmuc);
        quanlydanhmuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminCategoryActivity.class);
                intent.putExtra("is_admin", true);
                startActivity(intent);

            }
        });

        thongke = findViewById(R.id.thongke);
        thongke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminDashboardActivity.class);
                intent.putExtra("is_admin", true);
                startActivity(intent);
            }
        });
        quanlyvoucher = findViewById(R.id.quanlyvoucher);
        quanlyvoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminVoucherManagement.class);
                intent.putExtra("is_admin", true);
                startActivity(intent);
            }
        });
        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnAdminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Toast.makeText(AdminActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                goToLogin();
            }
        });
        setupHeader();
    }

    private void goToLogin() {
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

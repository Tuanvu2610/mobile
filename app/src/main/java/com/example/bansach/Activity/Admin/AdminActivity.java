package com.example.bansach.Activity.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bansach.R;

public class AdminActivity extends AppCompatActivity {

    private LinearLayout quanlysanpham;
    private LinearLayout quanlydanhgia;
    private LinearLayout quanlydanhmuc;
    private LinearLayout thongke;
    private LinearLayout quanlydonhang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        quanlysanpham = findViewById(R.id.quanlysanpham);
        quanlysanpham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageBookActivity.class);
                startActivity(intent);

            }
        });
        quanlydonhang = findViewById(R.id.quanlydonhang);
        quanlydonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminOrderActivity.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.cardManageUsers).setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminUserManagementActivity.class);
            startActivity(intent);
        });

        quanlydanhgia = findViewById(R.id.quanlydanhgia);
        quanlydanhgia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminReviewActivity.class);
                startActivity(intent);

            }
        });

        quanlydanhmuc = findViewById(R.id.quanlydanhmuc);
        quanlydanhmuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminCategoryActivity.class);
                startActivity(intent);

            }
        });

        thongke = findViewById(R.id.thongke);
        thongke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, AdminDashboardActivity.class);
                startActivity(intent);

            }
        });
    }
}
